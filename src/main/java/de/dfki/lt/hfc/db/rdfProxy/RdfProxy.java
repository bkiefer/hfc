/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.dfki.lt.hfc.db.rdfProxy;

import static de.dfki.lt.hfc.NamespaceManager.getNamespace;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.helpers.FormattingTuple;
import org.slf4j.helpers.MessageFormatter;

import de.dfki.lt.hfc.WrongFormatException;
import de.dfki.lt.hfc.db.QueryException;
import de.dfki.lt.hfc.db.QueryResult;
import de.dfki.lt.hfc.db.StreamingClient;
import de.dfki.lt.hfc.db.TupleException;
import de.dfki.lt.hfc.types.XsdAnySimpleType;
import gnu.trove.map.hash.TIntObjectHashMap;

/** The object that ties the whole RDF functionality together.
 *
 * It contains the client connection to the database, the hierarchy and provides
 * all necessary factory methods
 *
 * @author kiefer
 */
public class RdfProxy implements StreamingClient {

  static Logger logger = LoggerFactory.getLogger(RdfProxy.class);

  static final String RDF_LIST = "<rdf:List>";
  static final String RDF_FIRST= "<rdf:first>";
  static final String RDF_REST = "<rdf:rest>";
  static final String RDF_NIL = "<rdf:nil>";

  static final String OWL_NAMED_INDIVIDUAL = "<owl:NamedIndividual>";

  /**
   * Client-field, taken from client/HfcDbClient.java, more information
   *  under server/HfcDbService.Client.java
   */
  protected DbClient _client;

  /** The class hierarchy in the database that is in the client database */
  private RdfHierarchy _hierarchy;

  /** Map uniquely from class base names to URIs */
  private Map<String, String> _resolvedClasses;

  /** Store all classes for which we have a RdfClass proxy object */
  private TIntObjectHashMap<RdfClass> _cachedClasses;

  /** Store all objects for which we have a Rdf proxy object */
  private Map<String, Rdf> _cachedObjects;

  /** Store all lists for which we have a RdfList proxy object */
  private Map<String, RdfList> _cachedLists;

  /** When was the last call to compute()? Currently not used, see below */
  private long _lastUpdate = 0;

  /** Create a new proxy to an RDF database backed by the given DbClient
   *  object.
   */
  public RdfProxy(DbClient client) {
    _client = client;
    _hierarchy = new RdfHierarchy(this);
    _hierarchy.initialize();
    _resolvedClasses = new HashMap<>();
    _cachedClasses = new TIntObjectHashMap<>();
    _cachedObjects = new HashMap<>();
    _cachedLists = new HashMap<>();
    // register as streaming client
    _lastUpdate = System.currentTimeMillis();
    client.registerStreamingClient(this);
  }


  // ######################################################################
  // methods for testing ONLY!
  // ######################################################################

  void clearCache() {
    _resolvedClasses.clear();
    _cachedClasses.clear();
    _cachedObjects.clear();
    _cachedLists.clear();
  }

  // ######################################################################
  // StreamingClient methods
  // ######################################################################

  /*
  @Override
  public void init(DbClient handler) {
    // either do nothing here, or move the initialization part of the
    // constructor to this point.
    _lastUpdate = System.currentTimeMillis();
  }
  */

  @Override
  public void compute(Set<String> affectedUsers) {
    // select all new triples and invalidate the ones in the cache
    // select ... filter is not an option because the filter is applied
    // after *all* triples have been selected.
    // TODO: add a function providing the latest changes to hfc-db

    // Invalidate the _fields cache of all recently changed objects. To do
    // this efficiently, it would be preferable to get a table of changed
    // objects as an argument.

    _lastUpdate = System.currentTimeMillis();
  }

  // ######################################################################

  /** Return the class hierarchy of the ontology */
  public RdfHierarchy getHierarchy() { return _hierarchy; }

  /** Execute a query on the underlying database */
  QueryResult selectQuery(String varQuery) {
    try {
      return _client.selectQuery(varQuery);
    } catch (Exception ex) {
      throw new RuntimeException(ex);
    }
  }

  /** Execute the given query on the underlying database, filling the
   *  variable with arg */
  public QueryResult selectQuery(String varQuery, String arg) {
    try {
      FormattingTuple ft = MessageFormatter.format(varQuery, arg);
      return _client.selectQuery(ft.getMessage());
    } catch (Exception ex) {
      throw new RuntimeException(ex);
    }
  }

  /** Execute the given query on the underlying database, filling the
   *  variables with arg1 and arg2
   */
  public QueryResult selectQuery(String varQuery, String arg1, String arg2) {
    try {
      FormattingTuple ft = MessageFormatter.format(varQuery, arg1, arg2);
      return _client.selectQuery(ft.getMessage());
    } catch (Exception ex) {
      throw new RuntimeException(ex);
    }
  }

  /** Execute the given query on the underlying database, filling the
   *  variables with args
   */
  public QueryResult selectQuery(String varQuery, String ... args) {
    try {
      FormattingTuple ft = MessageFormatter.arrayFormat(varQuery, args);
      return _client.selectQuery(ft.getMessage());
    } catch (Exception ex) {
      throw new RuntimeException(ex);
    }
  }

  /** Return the results of a query returning a single column of results */
  public static List<String> getValues(QueryResult qr) {
    return qr.getTable().projectColumn(0);
  }

  /** Return a set of resolved objects from a database query */
  public List<List<Object>> queryTable(String query, String ... args) {
    QueryResult qr = selectQuery(query, args);
    List<List<Object>> result = new ArrayList<>();
    for(List<String> row : qr.getTable().getRows()) {
      List<Object> newRow = new ArrayList<>();
      for (String elt : row) {
        newRow.add(uriToObject(elt));
      }
      result.add(newRow);
    }
    return result;
  }

  /** Return a set of resolved objects from a database query */
  public List<Object> query(String query, String ... args) {
    QueryResult qr = selectQuery(query, args);
    List<Object> result = new ArrayList<>(qr.getTable().getRowsSize());
    for(List<String> row : qr.getTable().getRows()) {
      result.add(uriToObject(row.get(0)));
    }
    return result;
  }

  /** Add name/URI pairs to the name resolving map */
  public void setBaseToUri(Map<String, String> resolved) {
    for (Map.Entry<String, String> pair: resolved.entrySet()) {
      if (isClass(pair.getValue())) {
        _resolvedClasses.put(pair.getKey(), pair.getValue());
      } else {
        logger.error("Invalid uri in mapping: {} (for {})",
            pair.getValue(), pair.getKey());
      }
    }
  }

  /** Check if uri points to a class */
  private boolean isClass(String uri) {
    return _hierarchy.getVertex(uri) >= 0;
  }

  private String baseToFull(String base, boolean strict) {
    String result = _resolvedClasses.get(base);
    if (result != null)
      return result;

    List<String> fullNames = _hierarchy.getFullNames(base);
    if (fullNames == null) {
      if (strict) logger.error("Not a class: {}", base);
      return null;
    }
    if (fullNames.size() > 1) {
      Set<String> uniq = new HashSet<>();
      for (String s : fullNames) {
        int v = _hierarchy.getVertex(s);
        String canonical = _hierarchy.getVertexName(v);
        uniq.add(canonical);
      }
      if (uniq.size() > 1) {
        StringBuilder sb = new StringBuilder();
        for (String c : uniq) {
          sb.append(c).append(", ");
        }
        logger.warn("base name {} can be one of {} please resolve manually.",
            base, sb.toString());
      }
    }
    result = fullNames.get(0);
    _resolvedClasses.put(base, result);
    return result;
  }

  /** Get RdfClass object for an URI which is definitively a class URI */
  private RdfClass createRdfClass(String uri) {
    int classId = _hierarchy.getVertex(uri);
    // test if already created
    RdfClass tempRdfClass = _cachedClasses.get(classId);
    // to get the canonical class in case of equivalent classes
    uri = _hierarchy.getVertexName(classId);

    // Rdf already created
    if (tempRdfClass != null) {
      return tempRdfClass;
    }
    // else: create by calling private constructor:
    RdfClass newRdfClass = new RdfClass(uri, this);
    _cachedClasses.put(classId, newRdfClass);
    return newRdfClass;
  }

  /**
   * Fetch the class object of an existing RDF class
   *
   * @param baseName the name of the class, not the uri
   * @return an RdfClass object, or null, if no class can be found
   */
  public RdfClass fetchClass(String baseName) {
    // if uri is not a full URI (not starting with '<'), find a matching
    // class name (going through a list of prefixes, or listing all classes)
    // should only be used with baseName, not complete URIs
    String uri = baseToFull(baseName, false);
    if (uri == null)
      return null;
    return createRdfClass(uri);
  }

  /** Return the class object for a given URI */
  public RdfClass getClass(String uri) {
    if (!isClass(uri))
      return null;
    return createRdfClass(uri);
  }

  /** Fetch the class object for the given string, no matter if it is a URI or
   *  just a name, if possible.
   */
  public RdfClass getRdfClass(String baseNameOrUri) {
    if(!baseNameOrUri.contains("<")){
      return fetchClass(baseNameOrUri);
    } else {
      return getClass(baseNameOrUri);
    }
  }

  /** Return true if subUri is a subclass of supUri */
  protected boolean isSubclassOf(String subUri, String supUri) {
    int subType = _hierarchy.getVertex(subUri);
    int supType = _hierarchy.getVertex(supUri);
    return _hierarchy.subsumes(supType, subType);
  }

  protected Rdf getRdf(String uri, RdfClass clazz) {
    Rdf newRdf = new Rdf(uri, clazz, this);
    _cachedObjects.put(uri, newRdf);
    return newRdf;
  }

  public RdfClass getMostSpecificClass(String uri)
      throws TupleException, QueryException {
    Set<String> clazzUris = _client.getMultiValue(uri, "<rdf:type>");
    if (clazzUris == null || clazzUris.isEmpty()) return null;
    RdfClass result = null;
    for(String clazzUri : clazzUris) {
      if (! OWL_NAMED_INDIVIDUAL.equals(clazzUri)) {
        RdfClass clazz = getClass(clazzUri);
        if (result == null || clazz.isSubclassOf(result)) {
          result = clazz;
        }
      }
    }
    return result;
  }

  public List<RdfClass> getAllSuperClasses(RdfClass clz) {
    Set<Integer> supers =
        _hierarchy.getSuperclasses(_hierarchy.getVertex(clz._uri));
    List<RdfClass> result = new ArrayList<>(supers.size());
    for (int s : supers) {
      RdfClass sup = getRdfClass(_hierarchy.getVertexName(s));
      // if there is no such class, it is an artificial top type.
      if (sup != null) result.add(sup);
    }
    return result;
  }

  /** Return a RDF proxy object for the given URI.
   *  Might already be cached.
   * @param uri the URI for the object
   * @return an Rdf object representing the same object as the URI
   */
  public Rdf getRdf(String uri) {
    try {
      Rdf tempRdf = _cachedObjects.get(uri);

      // Rdf already created ?
      if (tempRdf != null)
        return tempRdf;

      // verify if uri exists and has <rdf:type>
      RdfClass clazz = getMostSpecificClass(uri);
      if (clazz == null) {
        logger.error("{} is not present or has no known class.", uri);
        return null;
      }
      // then: create RDF by calling private constructor:
      return getRdf(uri, clazz);
    } catch (QueryException|TupleException ex) {
      throw new RuntimeException(ex);
    }
  }

  /** Return an empty RDF list */
  public RdfList newList(String namespace) {
    RdfClass listClass = createRdfClass(RDF_LIST);
    Rdf newListHead = listClass.getNewInstance(namespace);
    RdfList newList = new RdfList(newListHead, namespace);
    _cachedLists.put(newListHead.getURI(), newList);
    return newList;
  }

  /** If this Rdf object represents a RDF list, return the list proxy
   * representation, otherwise, return null.
   */
  public RdfList getList(Rdf listHead) {
    if (!listHead.getClazz().isList()) return null;
    RdfList list = _cachedLists.get(listHead.getURI());
    if (list == null) {
      list = new RdfList(listHead, getNamespace(listHead.getURI()));
      _cachedLists.put(listHead.getURI(), list);
      // now fetch list elements
      list.fetchElements();
    }
    return list;
  }

  /** If this Rdf object represents a RDF list, return the list proxy
   * representation, otherwise, return null.
   * TODO: DOES NOT WORK IN ALL CASES, E.G., IF THE OBJECT HAS NO CLASS
  public RdfList getCollection(String collHead) {
    //if (!listHead.getClazz().isList()) return null;
    RdfList list = _cachedLists.get(collHead);
    if (list == null) {
      list = new RdfList(getRdf(collHead), getNamespace(collHead));
      _cachedLists.put(collHead, list);
      // now fetch list elements
      list.fetchElements();
    }
    return list;
  }*/

  /**
   * get the most specific type (RdfClass) out of two types in subclass relation
   * @param type1 fullname, e.g. <dom:Child>
   * @param type2 fullname, e.g. <dom:Child>
   * @return the most specific type (RdfClass) as String or
   *         null if both types are not compatible (not in a sublcass relation)
   */
  public String fetchMostSpecific(String type1, String type2) {
    if (isSubclassOf(type1, type2))
      return type1;
    if (isSubclassOf(type2, type1))
      return type2;
    else return null;
  }

  /** Turn a URI string into an object.
   *  A "normal" URI or blank node is turned into an RDF object, an XSD is
   *  converted into a POD object.
   *
   * @param value
   * @return
   */
  Object uriToObject(String value) {
    Object o = null;
    switch (value.charAt(0)) {
      case '<': // URI
      case '_': // blank node
        o = getRdf(value);
        break;
      case '"': // simple type
        try {
          o = XsdAnySimpleType.getXsdObject(value).toJava();
        } catch (WrongFormatException e) {
          throw new RuntimeException(e);
        }
        break;
      default:
        // Error: don't know what this is.
        logger.warn("Can't covert this string representation: {}", value);
        break;
    }
    return o;
  }


  /** Turn an object into a RDF representation.
   *  For an Rdf object, return the URI, for a POD or an XSD object, an XSD
   *  string representation
   * @param obj
   * @return
   */
  static String objectToUri(Object obj)  {
    String result = null;
    if (obj instanceof Rdf) {
      result = ((Rdf)obj).getURI();
    } else if (obj instanceof XsdAnySimpleType) {
      result = ((XsdAnySimpleType)obj).toString();
    } else {
      try {
        result = XsdAnySimpleType.javaToXsd(obj).toString();
      } catch (WrongFormatException e) {
        throw new RuntimeException(e);
      }
    }
    return result;
  }
}