package de.dfki.lt.hfc.proxy;

import java.util.*;

/**
 * the UriProxy class currently only works for triples as it assumes
 * that the URI (this = the subject) is connected via an arc (the property)
 * with other URIs (the objects);
 * even though an RDF graph can be seen as a_non_-deterministic finite
 * automaton, we implement the outgoing edges and their destination as
 * a map;
 * NOTE: we also use this class to represent blank nodes
 *
 * @see de.dfki.lt.hfc.proxy.Literal
 *
 * @author (C) Hans-Ulrich Krieger
 * @since JDK 1.5
 * @version Fri Mar 18 10:04:30 CET 2016

 */
public class UriProxy extends Literal {

  /**
   * a mapping from properties (strings) onto _sets_ of literals (either
   * URIs or XSD atoms)
   */
  private Map<String, Set<Literal>> predToObj;

  /**
   * sets the name of the URI
   */
  public UriProxy(String name) {
    this.name = name;
    this.predToObj = new HashMap<String, Set<Literal>>();
  }

  /**
   * sets the name of the URI, but also adds an edge-value collection
   * to the new node
   */
  public UriProxy(String name, Map<String, Set<Literal>> predToObj) {
    this(name);
    this.predToObj = predToObj;
  }

  /**
   * returns the value(s) for a given property as a set of literals;
   * if the property is not known, null is returned, thus this method
   * can be used to check whether the property exists (as null values
   * are _not_ allowed)
   * @see de.dfki.lt.hfc.proxy.Literal
   * @return the set of literals that are connected to this URI node
   */
  public Set<Literal> getValues(String property) {
    return this.predToObj.get(property);
  }

  /**
   * @return true iff this URI is connected with literal value via property
   * @return false otherwise
   */
  public boolean containsValue(String property, Literal value) {
    final Set<Literal> values = getValues(property);
    if (values == null)
      return false;
    return values.contains(value);
  }

  /**
   * adds a property (an edge with an _empty_ set) to this URI
   */
  public void addProperty(String property) {
    this.predToObj.put(property, new HashSet<Literal>());
  }

  /**
   * removes property from predToObj
   */
  public void removeProperty(String property) {
    this.predToObj.remove(property);
  }

  /**
   * adds for a given property and an at least empty set further elements
   * to this set
   */
  public boolean addValue(String property, Literal value) {
    return this.predToObj.get(property).add(value);
  }

}
