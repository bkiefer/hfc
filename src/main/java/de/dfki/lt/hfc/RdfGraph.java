package de.dfki.lt.hfc;

import java.util.*;

/**
 * at the moment, the class RdfGraph only works for triples (as RDF indicates);
 * it builds up an RDF graph for the triples contained in the tuple store;
 * technically, the graph might be devided into several independent subgraphs,
 * as they need not be connected
 *
 * @author (C) Hans-Ulrich Krieger
 * @since JDK 1.5
 * @version Fri Mar 18 11:05:43 CET 2016

 */
public class RdfGraph {

  /**
   * a handle to the tuple store to which the tuples belong
   */
  private TupleStore tupleStore;

  /**
   * a mapping from URI and XSD atom names onto instances of the corresponding classes
   * @see de.dfki.lt.hfc.Literal
   * @see de.dfki.lt.hfc.UriProxy
   * @see de.dfki.lt.hfc.XsdAtomProxy
   */
  private Map<String, Literal> nameToLiteral;

  /**
   * establishes an empty mapping from URI/XSD atom names to literals (URIs and XSD atoms)
   */
  public RdfGraph(TupleStore tupleStore) {
    this.tupleStore = tupleStore;
    this.nameToLiteral = new HashMap<String, Literal>();
  }

  /**
   * returns the literal for for a given URI or XSD atom name
   */
  public Literal getLiteral(String name) {
    return this.nameToLiteral.get(name);
  }

  /**
   * call this method when tuples are added to the tuple store and when you
   * wish to have them represented in the RDF graph;
   * this method does _not_ check whether individuals at certain positions
   * are of wrong type, e.g., it does not check for XSD atoms in subject
   * position; thus this assumes that the data is syntactically correct!
   *
   * @return true iff tuple is not already represented in this RDF graph
   */
  public boolean addToRdfGraph(List<String> tuple) {
    // final String polarity = tuple.get(?);  // ? = 0
    final String subj = tuple.get(0);  // change to 1 for 5-tuples
    final String pred = tuple.get(1);  // change to 2
    final String obj = tuple.get(2);   // change to 3
    //final String time = tuple.get(?);  // ? = 4
    // is subject brand-new ?
    final Literal lsubj = getLiteral(subj);
    final UriProxy usubj;
    if (lsubj == null) {
      usubj = new UriProxy(subj);
      this.nameToLiteral.put(subj, usubj);
    }
    else
      // subject should be an URI
      usubj = (UriProxy)(lsubj);
    // is predicate brand-new ?
    final Set<Literal> values = usubj.getValues(pred);
    if (values == null)
      usubj.addProperty(pred);
    // is object brand-new ?
    Literal lobj = getLiteral(obj);
    if (lobj == null) {
      if (TupleStore.isAtom(obj))
        lobj = new XsdAtomProxy(obj);
      else
        // note: blank nodes are also represented via UriProxy
        lobj = new UriProxy(obj);
      this.nameToLiteral.put(obj, lobj);
    }
    return usubj.addValue(pred, lobj);
  }

  /**
   * @see addToRdfGraph(List<String> tuple)
   */
  public boolean addToRdfGraph(String[] stuple) {
    ArrayList<String> tuple = new ArrayList<String>();
    for (String s : stuple)
      tuple.add(s);
    return addToRdfGraph(tuple);
  }

  /**
   * @see addToRdfGraph(List<String> tuple)
   */
  public boolean addToRdfGraph(int[] ituple) {
    ArrayList<String> tuple = new ArrayList<String>();
    for (int i : ituple)
      tuple.add(this.tupleStore.getObject(i));
    return addToRdfGraph(tuple);
  }

  /**
   * this method assumes that the property is a functional property;
   * if it is _not_, it overwrites all values (the set);
   * in case the subject or the predicate for the subject is new, this method
   * has the same effect as method addToRdfGraph() above and returns false
   */
  public boolean overwriteInRdfGraph(List<String> tuple) {
    boolean success = false;
    Set<Literal> value = getFromRdfGraph(tuple.get(0), tuple.get(1));
    if (value == null) {
      addToRdfGraph(tuple);
      return false;
    }
    value.clear();
    final String obj = tuple.get(2);
    Literal lobj = getLiteral(obj);
    if (lobj == null) {
      if (TupleStore.isAtom(obj))
        lobj = new XsdAtomProxy(obj);
      else
        // note: blank nodes are also represented via UriProxy
        lobj = new UriProxy(obj);
      this.nameToLiteral.put(obj, lobj);
    }
    value.add(lobj);
    return true;
  }

  /**
   * @return the set of literals which can be accessed from subject via predicate
   * @return null if there is no such predicate or even no such subject, or if the
   *         literal is an XSD atom
   */
  public Set<Literal> getFromRdfGraph(String subject, String predicate) {
    final Literal lit = this.nameToLiteral.get(subject);
    if (lit == null)
      return null;
    if (Literal.isXsdAtom(lit))
      return null;
    // contains() via getValues() suffices here as we do _not_ allow for null values
    return ((UriProxy)(lit)).getValues(predicate);
  }

  /**
   * removes a tuple from this RDF graph
   *
   * @return true if tuple is part of this RDF graph
   * @return false otherwise
   */
  public boolean removeFromRdfGraph(List<String> tuple) {
    // final String polarity = tuple.get(?);
    final String subj = tuple.get(0);  // change to 1 for 5-tuples
    final String pred = tuple.get(1);  // change to 2
    final String obj = tuple.get(2);   // change to 3
    //final String time = tuple.get(?);
    // does subj exist ?
    final Literal lsubj = getLiteral(subj);
    if (lsubj == null)
      return false;
    // subject has to be a URI
    final UriProxy usubj = (UriProxy)(lsubj);
    // does property exist ?
    final Set<Literal> values = usubj.getValues(pred);
    if (values == null)
      return false;
    // does object exist ?
    final Literal lobj = getLiteral(obj);
    if (lobj == null)
      return false;
    // is object contained in values attached to property ?
    if (! values.contains(lobj))
      return false;
    // tuple is _definitely_ represented by this RDF graph; so now remove it !
    values.remove(lobj);
    // was lobj the last value in set / the only edge labeled with pred ?
    if (values.isEmpty())
      usubj.removeProperty(pred);
    // was this the only property left / the only outgoing edge ?
    // if so, I'm _not_ allowed to clear the nameToLiteral mapping depending on
    // this local information as the literal might be accessed by a fifferent path
    return true;
  }

  /**
   * @see removeFromRdfGraph(List<String> tuple)
   */
  public boolean removeFromRdfGraph(String[] stuple) {
    ArrayList<String> tuple = new ArrayList<String>();
    for (String s : stuple)
      tuple.add(s);
    return removeFromRdfGraph(tuple);
  }

  /**
   * @see removeFromRdfGraph(List<String> tuple)
   */
  public boolean removeFromRdfGraph(int[] ituple) {
    ArrayList<String> stuple = new ArrayList<String>();
    for (int i : ituple)
      stuple.add(this.tupleStore.getObject(i));
    return removeFromRdfGraph(stuple);
  }

  /**
   * a garbage collector for RDF graphs;
   * walks over all literals recorded in nameToLiteral and removes all those
   * literals (URIs) whose predToObject mapping is empty _and_ which are not
   * accessed by other literals
   * @return true iff at least one literal has been removed
   * @return false otherwise
   *
   * NOTE: !!!!! NOT IMPLEMENTED YET !!!!!
   */
  public boolean gc() {
    return false;
  }

  /**
   * for test purposes only
   */
  public static void main(String[] args) throws Exception {
    Namespace ns = new Namespace("/Users/krieger/Desktop/Java/HFC/hfc/src/resources/default.ns");
    TupleStore ts = new TupleStore(ns, "/Users/krieger/Desktop/Java/HFC/hfc/src/resources/default.eqred.nt");
    RdfGraph rg = new RdfGraph(ts);
    // add info for child
    rg.addToRdfGraph(new String[] {"<test:child_0>", "<rdf:type>", "<test:Child>"});
    rg.addToRdfGraph(new String[] {"<test:child_0>", "<rdf:type>", "<test:Human>"});
    // the next two statements should not add further info
    rg.addToRdfGraph(new String[] {"<test:child_0>", "<rdf:type>", "<test:Child>"});
    rg.addToRdfGraph(new String[] {"<test:child_0>", "<rdf:type>", "<test:Human>"});
    //
    rg.addToRdfGraph(new String[] {"<test:child_0>", "<test:forename>", "\"Henk\"^^<xsd:string>"});
    rg.addToRdfGraph(new String[] {"<test:child_0>", "<test:lastname>", "\"Jansen\"^^<xsd:string>"});
    rg.addToRdfGraph(new String[] {"<test:child_0>", "<test:hasLabValue>", "<test:labval_1>"});
    rg.addToRdfGraph(new String[] {"<test:labval_1>", "<rdf:type>", "<test:LabValue>"});
    rg.addToRdfGraph(new String[] {"<test:labval_1>", "<test:hr>", "\"62\"^^<xsd:min-1>"});
    rg.addToRdfGraph(new String[] {"<test:labval_1>", "<test:height>", "\"178\"^^<xsd:cm>"});
    rg.addToRdfGraph(new String[] {"<test:labval_1>", "<test:weight>", "\"63\"^^<xsd:kg>"});
    rg.addToRdfGraph(new String[] {"<test:labval_1>", "<test:time>", "\"1234567890\"^^<xsd:long>"});
    // check RDF graph
    System.out.println(rg.nameToLiteral);
    // remove all information in a different order
    rg.removeFromRdfGraph(new String[] {"<test:child_0>", "<rdf:type>", "<test:Human>"});
    rg.removeFromRdfGraph(new String[] {"<test:child_0>", "<test:forename>", "\"Henk\"^^<xsd:string>"});
    rg.removeFromRdfGraph(new String[] {"<test:child_0>", "<test:lastname>", "\"Jansen\"^^<xsd:string>"});
    rg.removeFromRdfGraph(new String[] {"<test:labval_1>", "<test:hr>", "\"62\"^^<xsd:min-1>"});
    rg.removeFromRdfGraph(new String[] {"<test:labval_1>", "<test:height>", "\"178\"^^<xsd:cm>"});
    rg.removeFromRdfGraph(new String[] {"<test:child_0>", "<test:hasLabValue>", "<test:labval_1>"});
    rg.removeFromRdfGraph(new String[] {"<test:child_0>", "<rdf:type>", "<test:Child>"});
    rg.removeFromRdfGraph(new String[] {"<test:labval_1>", "<test:weight>", "\"63\"^^<xsd:kg>"});
    rg.removeFromRdfGraph(new String[] {"<test:labval_1>", "<test:time>", "\"1234567890\"^^<xsd:long>"});
    rg.removeFromRdfGraph(new String[] {"<test:labval_1>", "<rdf:type>", "<test:LabValue>"});
    // check RDF graph again
    System.out.println(rg.nameToLiteral);
    // nevertheless information remains in the RDF graph that is not accessable via links from URIs,
    // similar to chunks of memory that need to be reclaimed by a garbage collector
  }

}
