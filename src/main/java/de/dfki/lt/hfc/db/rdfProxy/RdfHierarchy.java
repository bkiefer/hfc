/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
  * and open the template in the editor.
  */
package de.dfki.lt.hfc.db.rdfProxy;

import static de.dfki.lt.hfc.db.rdfProxy.RdfProxy.getValues;
import static de.dfki.lt.hfc.db.rdfProxy.RdfProxy.logger;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import de.dfki.lt.hfc.NamespaceManager;
import de.dfki.lt.hfc.db.QueryException;
import de.dfki.lt.hfc.db.QueryResult;
import de.dfki.lt.hfc.db.Table;
import de.dfki.lt.loot.digraph.DirectedBiGraph;
import de.dfki.lt.loot.digraph.Edge;
import de.dfki.lt.loot.digraph.VertexBooleanPropertyMap;
import de.dfki.lt.loot.digraph.VertexIsomorphism;
import de.dfki.lt.loot.digraph.VertexListPropertyMap;
import de.dfki.lt.loot.digraph.VertexPropertyMap;
import de.dfki.lt.loot.digraph.algo.TransitiveReduction;
import de.dfki.lt.loot.jada.Partition;
import gnu.trove.map.TIntIntMap;
import gnu.trove.map.hash.TIntIntHashMap;
import gnu.trove.procedure.TIntProcedure;
import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;

/** An efficient data structure representing the subclass relations in an
 *  ontology.
 *
 *  Initially queries the ontology, builds a graph structure from the subclass
 *  relations and computes a bit vector encoding for every type to support
 *  efficient subsumption and unification operations.
 *
 * @author kiefer
 */
public class RdfHierarchy {

  private static final String ALL_CLASSES_QUERY =
      "selectall distinct ?cl where ?cl <rdf:type> <owl:Class> ?_";
  private static final String EQUIV_CLASS_QUERY =
      "SELECTALL distinct ?a ?b where ?a "
          + NamespaceManager.OWL_EQUIVALENTCLASS_SHORT + " ?b ?_";
  private static final String ALL_SUPERCLASSES_QUERY =
      "selectall distinct ?sup where {} <rdfs:subClassOf> ?sup ?_";

  /** The transitive reduction of the subclass relations of the hierarchy as
   *  a simple directed graph.
   *
   *  The <?> must be there because the iterator otherwise does not work.
   */
  private DirectedBiGraph<?> _classGraph;

  /** The mapping from class names to vertices and back. */
  private VertexIsomorphism<String> _name2Vertex;

  /** Map class names without namespace to full names. */
  private Map<String, List<String>> _name2fullName;

  /** Maps from a class to its top class */
  private TIntIntMap _representatives;

  /** This data structure maps equivalent classes to a representative */
  private Partition _equivalentClasses;

  /** Returns the BitSet for a vertex */
  private VertexPropertyMap<BitSet> _codes;

  /** Access to the RDF database */
  private final RdfProxy _proxy;

  public RdfHierarchy(RdfProxy proxy) {
    _proxy = proxy;
  }

  protected static String uriBaseName(String uri) {
    if (uri.isEmpty() || uri.charAt(0) != '<'
        || uri.charAt(uri.length() - 1) != '>')
      return uri;
    int i = uri.lastIndexOf('#');
    if (i < 0) i = uri.lastIndexOf(':');
    if (i < 0) return uri;
    return uri.substring(i + 1, uri.length() - 1);
  }

  private int newClazz(String name) {
    int clazzVertex = _classGraph.newVertex();
    _name2Vertex.put(clazzVertex, name);
    return clazzVertex;
  }

  private void newSuperEdge(int clazzVertex, int supVertex) {
    _classGraph.newEdge(null, clazzVertex, supVertex);
  }

  /** from and clazz may be different if they are equivalent classes. Then,
   *  clazz is the canonical class (representative) for all those classes.
   * @param from the URI to be reduced to base form
   * @param clazz the full URI to map to
   */
  private void addBaseToFull(String from, String clazz) {
    String base = uriBaseName(from);
    List<String> full = _name2fullName.get(base);
    if (full == null) {
      full = new ArrayList<>(2);
      _name2fullName.put(base, full);
    }
    if (! full.contains(clazz))
      full.add(clazz);
  }

  /** Iterate through the class objects, get the subclass statements from the
   * ontology and add edges to the graph for them, from the subclass to the
   * superclass
   */
  private void createGraph(Collection<String> classes) {
    // The set of all classes for which all edges have been added already
    VertexBooleanPropertyMap visited = new VertexBooleanPropertyMap(
        _classGraph);

    for (String clazz : classes) {
      int clazzVertex = _name2Vertex.getVertex(clazz);
      if (visited.get(clazzVertex))
        continue;
      // all equivalent classes which are not representatives will remain
      // singletons, and not be considered by any other operation in this class
      int rep = _equivalentClasses.findRepresentative(clazzVertex);
      if (rep != clazzVertex) {
        String canonical = _name2Vertex.get(rep);
        addBaseToFull(clazz, canonical);
        continue;
      }
      addBaseToFull(clazz, clazz);

      visited.set(clazzVertex);
      if (clazzVertex > 0) {
        List<String> supers =
            getValues(_proxy.selectQuery(ALL_SUPERCLASSES_QUERY, clazz));
        for (String sup : supers) {
          if (!sup.equals(clazz)) {
            int supVertex = _name2Vertex.getVertex(sup);
            if (supVertex < 0) {
              supVertex = newClazz(sup);
              newSuperEdge(clazzVertex, supVertex);
            } else {
              if (!_classGraph.hasEdge(clazzVertex, supVertex)) {
                newSuperEdge(clazzVertex, supVertex);
              }
            }
          }
        }
      }
    }
  }
  private List<String> fetchUnionElements(String headUri) {
    List<String> result = new ArrayList<>();
    // read the list into memory, only to be accessed by RdfProxy
    do {
      List<String> first = getValues(_proxy.selectQuery(
          "select ?e where {} {} ?e ?_", headUri, RdfProxy.RDF_FIRST));
      if (first != null && !first.isEmpty()) result.add(first.get(0));
      List<String> next = getValues(_proxy.selectQuery(
          "select ?e where {} {} ?e ?_", headUri, RdfProxy.RDF_REST));
      if (next == null || next.isEmpty() ||
          next.get(0).equals(RdfProxy.RDF_NIL)) {
        headUri = null;
      } else {
        headUri = next.get(0);
      }
    } while (headUri != null);
    return result;
  }

  private void treatAllUnions() {
    // first is class, second
    QueryResult unions = _proxy.selectQuery(
        "select ?c ?l where ?c <rdf:type> <owl:Class> ?_ & ?c <owl:unionOf> ?l ?_");
    for(List<String> unionAndList: unions.getTable().getRows()) {
      String union = unionAndList.get(0);
      int supVertex = _name2Vertex.getVertex(union);
      for (String sub : fetchUnionElements(unionAndList.get(1))) {
        int clazzVertex = _name2Vertex.getVertex(sub);
        // clazzVertex may not be known because sub is an owl:Restriction
        // TODO ARE RESTRICTIONS CLASSES OR NOT?
        if (clazzVertex >= 0) {
          if (!_classGraph.hasEdge(clazzVertex, supVertex)) {
            newSuperEdge(clazzVertex, supVertex);
          }
        } else {
          logger.info("{} is no class", sub);
        }
      }
    }
  }

  /** Build the graph from the subclass relations
   * @throws QueryException
   */
  private void buildClassGraph() throws QueryException {
    _classGraph = new DirectedBiGraph<>();
    _name2Vertex = new VertexIsomorphism<>(_classGraph);
    _classGraph.register("names", _name2Vertex);

    // fetch all class objects of the ontology
    List<String> classes = getValues(_proxy.selectQuery(ALL_CLASSES_QUERY));
    // Reserve the zero node for the "top" type
    classes.add(0, "top");

    // create a union-find datastructure to capture equivalentClass relations
    for (String clazz : classes) {
      int clazzVertex = _name2Vertex.getVertex(clazz);
      if (clazzVertex < 0) {
        clazzVertex = newClazz(clazz);
      }
    }

    Table t = _proxy.selectQuery(EQUIV_CLASS_QUERY).getTable();
    _equivalentClasses = new Partition(classes.size());
    for(List<String> row : t.rows) {
      int a = _name2Vertex.getVertex(row.get(0));
      int b = _name2Vertex.getVertex(row.get(1));
      // TODO: ARE RESTRICTIONS CLASSES OR NOT?
      if (a >= 0 && b >=0)
        _equivalentClasses.union(a, b);
    }

    createGraph(classes);

    treatAllUnions();

    TransitiveReduction.transitiveReduction(_classGraph);
  }

  private void compRepsRec(int v) {
    if (_representatives.containsKey(v))
      return;
    if (_classGraph.hasOutEdges(v)) {
      TIntSet reps = new TIntHashSet();
      for (Edge<?> e : _classGraph.getOutEdges(v)) {
        compRepsRec(e.getTarget());
        reps.add(_representatives.get(e.getTarget()));
      }
      if (reps.size() > 1) {
        final StringBuilder sb = new StringBuilder();
        sb.append(' ');
        reps.forEach(new TIntProcedure() {
          @Override
          public boolean execute(int value) {
            sb.append(_name2Vertex.get(value)).append(' ');
            return true;
          }
        });
        String crown = sb.toString();
        logger.error("Non-unique top types detected {}", crown);
        throw new UnsupportedOperationException(
            "Non-unique top class (crown): " + crown);
      }
      _representatives.put(v, reps.iterator().next());
    } else {
      _representatives.put(v, v);
    }
  }


  /** Compute the representative for every vertex in the graph */
  private void computeRepresentatives() {
    _representatives = new TIntIntHashMap();
    for (int v : _classGraph)
      compRepsRec(v);
  }


  /** Compute the connected components recursively by marking all edges that
   *  can be reached by some outgoing edge
   */
  private void findCompos(int v, BitSet visited, final Partition compos) {
    if (visited.get(v))
      return;
    visited.set(v);
    if (_classGraph.hasOutEdges(v)) {
      for (Edge<?> e : _classGraph.getOutEdges(v)) {
        compos.union(v, e.getTarget());
        findCompos(e.getTarget(), visited, compos);
      }
    }
  }

  /** Add a synthetic unique top type to all connected components that have more
   *  than one top type.
   */
  private void removeCrowns() {
    Partition compos = new Partition(_classGraph.getNumberOfVertices());
    BitSet visited = new BitSet();
    for (int v : _classGraph) {
      findCompos(v, visited, compos);
    }
    visited.clear();
    int lastVertex = _classGraph.getNumberOfVertices();
    for (int v : _classGraph) {
      if (v >= lastVertex) break;
      if (visited.get(v)) continue;
      // find all top types of this component
      List<Integer> tops = new ArrayList<>();
      for(int u : compos.getMembers(v)) {
        visited.set(u);
        if (! _classGraph.hasOutEdges(u)) {
          tops.add(u);
        }
      }
      // If it's a crown, add a synthetic top type and add edges from the tops
      // to this new type
      if (tops.size() > 1) {
        int representative = tops.get(0);
        String newTop = String.format("TOP_TYPE_%05d", representative);
        int newTopVertex = newClazz(newTop);
        for (int u : tops) {
          newSuperEdge(u, newTopVertex);
        }
      }
    }
  }


  /** Assign bit codes to all types in a connected component, starting from the
    *  representative.
    *
    *  Recursively descend until all subtypes of a currently treated type have
    *  a bit code, disjunctively combine the bit codes of all direct subtypes,
    *  and add a new bit for the current type.
    */
   private int computeBitCode(int current, int bitsUsed) {
     BitSet currentCode = _codes.get(current);
     if (currentCode != null) return bitsUsed;
     currentCode = new BitSet();
     for (Edge<?> e : _classGraph.getInEdges(current)) {
       int sub = e.getSource();
       bitsUsed = computeBitCode(sub, bitsUsed);
       currentCode.or(_codes.get(sub));
     }
     currentCode.set(bitsUsed);
     ++bitsUsed;
     _codes.put(current, currentCode);
     return bitsUsed;
   }


  /** Go through the class graph assign bit codes to all relevant types.
   *
   *  First, we have to find the connected components and see if we have
   *  crowns (multiple independent top types), where we maybe need to
   *  introduce an artificial single top type. The top type is the
   *  representative of the connected component and serves as it's ID.
   *
   *  Then, we assign bit codes, treating every component as a single hierarchy
   *
   *  During processing, the first step is to see if two classes are in the same
   *  component. If not, they are not comparable anyway.
   */
  private void assignBitCodes() {
    computeRepresentatives();
    _codes = new VertexListPropertyMap<>(_classGraph);

    TIntSet reps = new TIntHashSet();
    reps.addAll(_representatives.values());

    reps.forEach(new TIntProcedure(){
      @Override
      public boolean execute(int vertex) {
        // not for singletons
        if (_classGraph.hasInEdges(vertex)) {
          computeBitCode(vertex, 0);
        }
        return true;
      }
    });
  }


  /** Initialise the hierarchy proxy using the subClass relations in the
   *  ontology.
   */
  public void initialize() {
    _name2fullName = new HashMap<>();
    try {
      buildClassGraph();
    } catch (QueryException tex) {
      throw new RuntimeException(tex);
    }
    removeCrowns();
    assignBitCodes();
  }

  private int findRepresentative(int v) {
    return (v >= 0 && v < _equivalentClasses.size())
        ? _equivalentClasses.findRepresentative(v)
        : v;
  }

  /** get an integer id for the given class uri */
  public int getVertex(String clazzUri) {
    return findRepresentative(_name2Vertex.getVertex(clazzUri));
  }

  /** Get the class uri for a given id. Must be in the class hierarchy */
  public String getVertexName(int id) {
    return _name2Vertex.get(id);
  }

  Set<Integer> getSuperclasses(int id) {
    Set<Integer> result = new HashSet<>();
    Queue<Integer> active = new ArrayDeque<>();
    active.add(id);
    while (! active.isEmpty()) {
      int next = active.poll();
      if (! result.contains(next)) {
        result.add(next);
        for (Edge<?> e : _classGraph.getOutEdges(next)) {
          int sup = e.getTarget();
          if (sup == findRepresentative(sup)) {
            active.add(sup);
          }
        }
      }
    }
    return result;
  }

  /** Add new instances (singletons) to the hierarchy */
  public int addNewSingleton(String name) {
    int newId = newClazz(name);
    _representatives.put(newId, newId);
    return newId;
  }

  protected List<String> getFullNames(String base) {
    List<String> res = _name2fullName.get(base);
    // return (res == null) ? Collections.<String>emptyList() : res;
    return res;
  }

  /** return true if type2 is a subtype of type1 */
  public boolean subsumes(int type1, int type2) {
    if (type1 == type2) return true;
    type1 = findRepresentative(type1);
    type2 = findRepresentative(type2);
    int rep1 = _representatives.get(type1);
    int rep2 = _representatives.get(type2);
    if (rep1 != rep2) return false;
    BitSet res = (BitSet)_codes.get(type1).clone();
    BitSet code2 = _codes.get(type2);
    res.and(code2);
    return res.equals(code2);
  }
}
