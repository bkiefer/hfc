/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.dfki.lt.hfc.db.rdfProxy;

import static de.dfki.lt.hfc.db.rdfProxy.RdfProxy.getValues;
import static de.dfki.lt.hfc.db.rdfProxy.RdfProxy.logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.dfki.lt.hfc.db.TupleException;


/** A proxy for a class in RDF, with functions to determine the
 *  defined PROPERTIES and their type
 *
 * @author Christophe Biwer, christophe.biwer@dfki.de
 * @author Bernd Kiefer, kiefer@dfki.de
 */
public class RdfClass {

  private static final Map<String, String> xsd2java = new HashMap<>();

  static {
    final String[][] xsd2javatypes = {
      {"<xsd:int>", "Integer"},
      {"<xsd:string>", "String"},
      {"<xsd:boolean>", "Boolean"},
      {"<xsd:double>", "Double"},
      {"<xsd:float>", "Float"},
      {"<xsd:long>", "Long"},
      {"<xsd:integer>", "Long"},
      {"<xsd:byte>", "Byte"},
      {"<xsd:short>", "Short"},
      {"<xsd:integer>", "Long"},
      {"<xsd:dateTime>", "XsdDateTime"},
      {"<xsd:date>", "XsdDate"},
    };
    for (String[] pair : xsd2javatypes) {
      xsd2java.put(pair[0], pair[1]);
    }
  }

  /* Version for triples
  private static final String GET_DEFINED_PROPERTIES =
      "select distinct ?pred where ?pred <rdfs:domain> ?class "
      + "& {} <rdfs:subClassOf> ?class";
  private static final String GET_PROPERTY_RANGE =
      "select distinct ?clzz where {} <rdfs:range> ?clzz";
  private static final String GET_PROPERTY_TYPE =
      "select distinct ?type where {} <rdf:type> ?type";
  */

  // Version for quadruples
  private static final String GET_DEFINED_PROPERTIES =
      "select distinct ?pred where ?pred <rdfs:domain> {} ?_";
  private static final String GET_PROPERTY_RANGE =
      "select distinct ?clzz where {} <rdfs:range> ?clzz ?_";
  private static final String GET_PROPERTY_TYPE =
      "select distinct ?type where {} <rdf:type> ?type ?_";

  /** getPropertyType returns an integer composed of bits from these
   *  constants
   */
  public static final int OBJECT_PROPERTY = 1;
  public static final int DATATYPE_PROPERTY = 2;
  public static final int FUNCTIONAL_PROPERTY = 4;


  /**
   * Client-field, taken from client/HfcDbClient.java, more information
   * under server/HfcDbService.Client.java
   */
  protected final RdfProxy _proxy;

  // URI of the created object
  protected final String _uri;

  /** Map from properties (URIs, defined on this class) to the range types */
  protected Map<String, Set<String>> _propertyRange;

  /** Map from property URIs to property type (see above) */
  protected Map<String, Integer> _propertyType;

  /** Map from property base name to full URI */
  protected Map<String, String> _propertyBaseToFull;

  /** Get a new class object for the given uri, using the access object */
  protected RdfClass(String uri, RdfProxy proxy) {
    _uri = uri;
    // TODO: check if uri really points to a RDF/OWL class object
    _proxy = proxy;
  }

  private DbClient getClient() {
    return _proxy._client;
  }

  public static String xsdToJavaPod(String something) {
    String ret = xsd2java.get(something);
    return (ret != null) ? ret : something;
  }

  /** Return a new instance of this class
   *
   * @param namespace the namespace the instance should be in
   * @return a new Rdf object of this class
   */
  public Rdf getNewInstance(String namespace) {
    try {
      String newUri = getClient().getNewId(namespace, _uri);
      // newUri is an instance of this Rdf class, getNewId does this already.
      return _proxy.getRdf(newUri, this);
    } catch (TupleException ex) {
      logger.error("Database error: {}", ex);
    }
    return null;
  }

  /** Return a new instance of this class, under the given URI
   *
   * @param uri the uri pointing to the new instance
   * @return a new Rdf object of this class with URI uri
   */
  public Rdf newRdf(String uri) {
    try {
      _proxy._client.setValue(uri, "<rdf:type>", _uri);
      return _proxy.getRdf(uri);
    } catch (TupleException ex) {
      logger.error("Database error: {}", ex);
    }
    return null;
  }

  /** Determine all the properties defined on this class. We use our own
   *  type hierarchy structure to determine this because constructs such as
   *  owl:unionOf forbid the direct use of a subClassOf query.
   *  TODO: implement as described
   */
  private void fetchProperties() {
    if (_propertyType != null) return;
    _propertyType = new HashMap<>();
    _propertyRange = new HashMap<>();
    // TODO: THIS IS DANGEROUS. IS IT POSSIBLE TO "OVERWRITE" A PROPERTY
    // DEFINITION IN A SUBCLASS? IF SO, THE RECURSIVE PROCEDURE SHOULD USE
    // A "LAYER BY LAYER" APPROACH STARTING AT THE TOP, and even that might
    // not give the right result
    List<RdfClass> supers = _proxy.getAllSuperClasses(this);
    for (RdfClass sup : supers) {
      sup.fetchProperties();
      _propertyType.putAll(sup._propertyType);
      _propertyRange.putAll(sup._propertyRange);
    }
    // Now add locally defined properties
    List<String> properties =
        getValues(_proxy.selectQuery(GET_DEFINED_PROPERTIES, _uri));
    for (String property : properties) {
      _propertyRange.put(property,
          new HashSet<>(getValues(_proxy.selectQuery(GET_PROPERTY_RANGE, property))));
      Set<String> predType =
          new HashSet<>(getValues(_proxy.selectQuery(GET_PROPERTY_TYPE, property)));
      int type = 0;
      if (predType.contains("<owl:DatatypeProperty>")) {
        type += DATATYPE_PROPERTY;
      }
      if (predType.contains("<owl:ObjectProperty>")) {
        type += OBJECT_PROPERTY;
      }
      if (predType.contains("<owl:FunctionalProperty>")) {
        type += FUNCTIONAL_PROPERTY;
      }
      if ((type & (DATATYPE_PROPERTY | OBJECT_PROPERTY)) ==
          (DATATYPE_PROPERTY | OBJECT_PROPERTY)) {
        logger.warn("property {} is both object and datatype property", property);
      }
      _propertyType.put(property, type);
    }
  }

  /** Compute the full property URI from a base name (without namespace) */
  public String fetchProperty(String baseName) {
    if (_propertyBaseToFull == null) {
      fetchProperties();
      _propertyBaseToFull = new HashMap<>();
      for (String uri : _propertyRange.keySet()) {
        String base = RdfHierarchy.uriBaseName(uri);
        _propertyBaseToFull.put(base, uri);
      }
    }
    return _propertyBaseToFull.get(baseName);
  }

  /** Is this property defined for the given class
   *
   * @param property the property in question
   * @return a bit mask that specifies the type of the property,
   *         if defined, zero otherwise
   */
  public int getPropertyType(String propertyUri)  {
    fetchProperties();
    Integer result = _propertyType.get(propertyUri);
    return (result == null) ? 0 : result;
  }

  /** Returns true if propertyUri, which must be defined on this RdfClass,
   *  is a functional property
   */
  public boolean isFunctionalProperty(String propertyUri) {
    return (getPropertyType(propertyUri) & FUNCTIONAL_PROPERTY) != 0;
  }

  /** Returns true if propertyUri, which must be defined on this RdfClass,
   *  is a datatype property, i.e., the values are of some XSD type
   */
  public boolean isDatatypeProperty(String propertyUri) {
    return (getPropertyType(propertyUri) & DATATYPE_PROPERTY) != 0;
  }

  /** Returns the properties (URIs) defined on this class */
  public Set<String> getProperties() {
    fetchProperties();
    return _propertyType.keySet();
  }

  /** Returns the property range of the given property */
  public Set<String> getPropertyRange(String propertyUri) {
    // get range type of predURI
    Set<String> ranges = _propertyRange.get(propertyUri);
    if (ranges == null || ranges.isEmpty()) return Collections.emptySet();
    return ranges;
  }

  /** Returns true if this class is a subclass of sup */
  public boolean isSubclassOf(RdfClass sup) {
    return _proxy.isSubclassOf(_uri, sup._uri);
  }

  /** Returns true if this class is a true subclass of sup (not the same class)
   */
  public boolean isTrueSubclassOf(RdfClass sup) {
    return isSubclassOf(sup) && ! equals(sup);
  }

  @Override
  public boolean equals(Object other){
    if (! (other instanceof RdfClass)) return false;
    RdfClass otherClass = (RdfClass)other;
    return _uri.equals(otherClass._uri);
  }

  /** Returns true if this class is a superclass of sub */
  public boolean isSuperclassOf(RdfClass sub) {
    return _proxy.isSubclassOf(sub._uri, _uri);
  }

  /** Returns true if this class is a true superclass of sub (not the same class)
   */
  public boolean isTrueSuperclassOf(RdfClass sub) {
    return isSuperclassOf(sub) && ! equals(sub);
  }

  /** String representation of class: URI */
  @Override
  public String toString() { return _uri; }


  /** Retrieves the range type for a chain of properties, i.e., the
   *  range type of the last property in the chain, if the chain is
   *  meaningful at all.
   *
   *  A chain is not meaningful if
   *  a) there is data type property in the middle of the chain
   *  b) in the middle of the chain is a non-functional property
   *
   * @param propertiesBaseName
   * @return a List of the given properties (URIfied)
   * + the final type (JAVAfied if possible)
   */
  public List<String> getPropertyType(List<String> propertiesBaseNames) {
    List<String> result = new ArrayList<>();
    int current = 0;
    String type = _uri;
    RdfClass currentClass = this;
    for(String propertyBaseName : propertiesBaseNames) {
      String predURI;
      // transform given first propertiesBaseName to predURI
      try {
        predURI = currentClass.fetchProperty(propertyBaseName);
      } catch (Exception e) {
        logger.error("property {} of class {} could not be URIfied "
            + "(may not exist)", propertyBaseName, this._uri );
        return null;
      }
      result.add(predURI);

      // get range type of predURI
      try {
        type = currentClass._propertyRange.get(predURI).iterator().next();
      } catch (java.lang.NullPointerException e) {
        logger.error("{} does not have property {} ({})",
            this._uri, propertyBaseName, predURI);
        return null;
      }

      // convert xsd-types to java primitive datatype, resp. check if last type.
      if (xsd2java.containsKey(type) || type.contains("<xsd:")) {
        if (current < propertiesBaseNames.size() - 1) {
          logger.error("xsd-datatype " + type + " does not have any properties.");
          return null;
        }
      }
      currentClass = _proxy.getClass(type);
      ++current;
    }
    type = xsdToJavaPod(type);
    result.add(type);
    return result;
  }

  /** Is this the RDF list class? */
  public boolean isList() {
    return _uri.equals(RdfProxy.RDF_LIST);
  }
}
