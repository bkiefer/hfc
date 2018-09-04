package de.dfki.lt.hfc;

import de.dfki.lt.hfc.types.AnyType;
import de.dfki.lt.hfc.types.Uri;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.HashSet;

/**
 * Namespace implements a bidirectional mapping between strings, given by
 * _short_ and _long_ form namespace names, as used in URIs and in type
 * specifiers of XSD atoms
 * <p>
 * a namespace file contains a sequence of lines, whereas each line _must_
 * consists of exactly two strings, the first being the short form and the
 * second representing the long form namespace name;
 * comments are allowed and MUST start with the '#' character (in separate
 * lines)
 * <p>
 * example
 * rdf  http://www.w3.org/1999/02/22-rdf-syntax-ns#
 * rdfs http://www.w3.org/2000/01/rdf-schema#
 * xsd  http://www.w3.org/2001/XMLSchema
 * owl  http://www.w3.org/2002/07/owl#
 * <p>
 * NEW
 * since HFC v6.0.11, namespace files also support dynamic (XSD) type to
 * internal HFC Java class mappings
 * <p>
 * for short-to-long mappings, use &short2long directive (new);
 * for type-to-class mappings, use &type2class directive (new);
 *
 * @author (C) Hans-Ulrich Krieger
 * @version Mon Oct  5 09:45:59 CEST 2015
 * @see default.ns (default namespace file for all rule sets)
 * <p>
 * example
 * &short2long
 * rdf  http://www.w3.org/1999/02/22-rdf-syntax-ns#
 * rdfs http://www.w3.org/2000/01/rdf-schema#
 * xsd  http://www.w3.org/2001/XMLSchema
 * owl  http://www.w3.org/2002/07/owl#
 * <p>
 * &type2class
 * xsd:int  XsdInt
 * xsd:long  XsdLong
 * xsd:float  XsdFloat
 * xsd:double  XsdDouble
 * xsd:string  XsdString
 * @since JDK 1.5
 */
public final class Namespace {

  /**
   * what follows are useful constants that can be accessed within each class
   * implementations for the below XSD types can be found in HFC in package de.dfki.lt.hfc.types
   */

  // SHORT and LONG namespace for XSD, RDF, RDFS, OWL (1.0), OWL 1.1

  public static final NamespaceObject XSD = new NamespaceObject("xsd", "http://www.w3.org/2001/XMLSchema#", false);
  public static final NamespaceObject RDF = new NamespaceObject("rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#", false);
  public static final NamespaceObject RDFS = new NamespaceObject("rdfs", "http://www.w3.org/2000/01/rdf-schema#", false);
  public static final NamespaceObject OWL = new NamespaceObject("owl", "http://www.w3.org/2002/07/owl#", false);
  public static final NamespaceObject TEST = new NamespaceObject("test", "http://www.dfki.de/lt/onto/test.owl", false);
  public static final NamespaceObject EMPTY = new NamespaceObject("", "", false);
  public static final String RDF_TYPE_SHORT = "<rdf:type>";
  public static final String RDF_TYPE_LONG = "<http://www.w3.org/1999/02/22-rdf-syntax-ns#type>";

  // special value UNBOUND/NULL, not used at the moment
  public static final AnyType UNBOUND = new Uri("NULL", EMPTY);
  public static final int UNBOUND_ID = 0;
  // RDFS: subClassOf
  public static final AnyType RDFS_SUBCLASSOF_SHORT = new Uri("subClassOf", RDFS);
  public static final int RDFS_SUBCLASSOF_ID = 1;
  // OWL: sameAs, equivalentClass, equivalentProperty, disjointWith
  public static final Uri OWL_SAMEAS_SHORT = new Uri("sameAs", OWL);
  public static final int OWL_SAMEAS_ID = 2;

  public static final Uri OWL_EQUIVALENTCLASS_SHORT = new Uri("equivalentClass", OWL);

  public static final int OWL_EQUIVALENTCLASS_ID = 3;
  public static final Uri OWL_EQUIVALENTPROPERTY_SHORT = new Uri("equivalentProperty", OWL);

  public static final int OWL_EQUIVALENTPROPERTY_ID = 4;
  public static final Uri OWL_DISJOINTWITH_SHORT = new Uri("disjointWith", OWL);
  public static final int OWL_DISJOINTWITH_ID = 5;
  private static final Logger logger = LoggerFactory.getLogger(Namespace.class);
  /**
   * translation table for establishing mappings between short and long form
   * namespace strings
   */
  public HashMap<String, NamespaceObject> shortToNs = new HashMap<String, NamespaceObject>();
  /**
   * translation table for establishing mappings between long and short form
   * namespace strings
   */
  public HashMap<String, NamespaceObject> longToNs = new HashMap<String, NamespaceObject>();
  private boolean shortIsDefault = false;
  private HashSet<NamespaceObject> allNamespaces = new HashSet<>();

  public Namespace() {
    shortIsDefault = true;
    addNamespace(EMPTY);
    addNamespace(XSD);
    addNamespace(OWL);
    addNamespace(RDF);
    addNamespace(RDFS);
    addNamespace(TEST);
  }

  public boolean isShortIsDefault() {
    return shortIsDefault;
  }

  public void setShortIsDefault(boolean shortIsDefault) {
//    System.out.println("Set short is default: " + shortIsDefault);
    this.shortIsDefault = shortIsDefault;
    for (NamespaceObject ns : shortToNs.values()) {
      ns.setIsShort(this.shortIsDefault);
    }
    if (shortToNs.size() != longToNs.size())
      for (NamespaceObject ns : shortToNs.values()) {
        ns.setIsShort(this.shortIsDefault);
      }
//    System.out.println(XSD.isShort());
  }

  /**
   * a mapping between XSD type specifiers and Java classes representing these types
   * in HFC; now in XsdAnySimpleType, where it belongs
   */
  //protected HashMap<String, Constructor<XsdAnySimpleType>> typeToConstructor = new HashMap<String, Constructor<XsdAnySimpleType>>();
  public void addNamespace(NamespaceObject ns) {
    this.allNamespaces.add(ns);
    this.shortToNs.put(ns.SHORT_NAMESPACE, ns);
    this.longToNs.put(ns.LONG_NAMESPACE, ns);
  }

  /**
   * adds a new mapping to the namespace
   */
  public void putForm(String shortForm, String longForm, boolean shortIsDefault) {
    if (shortToNs.containsKey(shortForm))
      shortToNs.get(shortForm).setIsShort(shortIsDefault);
    else if (longToNs.containsKey(longForm))
      longToNs.get(longForm).setIsShort(shortIsDefault);
    else {
      NamespaceObject ns = new NamespaceObject(shortForm, longForm, shortIsDefault);
      this.allNamespaces.add(ns);
      this.shortToNs.put(shortForm, ns);
      this.longToNs.put(longForm, ns);
    }
  }

  /**
   * obtains the short form, if present; null otherwise
   */
  public String getShortForm(String longForm) {
    return this.longToNs.get(longForm).SHORT_NAMESPACE;
  }

  /**
   * obtains the long form, if present; null otherwise
   */
  public String getLongForm(String shortForm) {
    return this.shortToNs.get(shortForm).LONG_NAMESPACE;
  }


  /**
   * this method borrows code from normalizeNamespace() above and always tries to fully
   * expand the namespace prefix of an URI, even if shortIsDefault == true
   */
  public String expandUri(String uri) {
    int pos = uri.indexOf("://");
    if (pos != -1)
      // a fully-expanded URI
      return uri;
    pos = uri.indexOf(":");
    if (pos == -1)
      // a URI with an _empty_ namespace
      return uri;
    // URI _not_ expanded, otherwise
    String prefix = uri.substring(1, pos);  // skip '<'
    String suffix = uri.substring(pos + 1);
    String expansion = shortToNs.get(prefix).LONG_NAMESPACE;
    // namespace maping specified?
    if (expansion == null)
      return uri;
    else
      return "<" + expansion + suffix;
  }

  public void updateNamespace(boolean shortIsDefault) {
    for (NamespaceObject ns : allNamespaces) {
      ns.setIsShort(shortIsDefault);
    }
  }

  public Namespace copy() {
    Namespace copy = new Namespace();
    copy.allNamespaces = (HashSet<NamespaceObject>) this.allNamespaces.clone();
    copy.shortToNs = (HashMap<String, NamespaceObject>) this.shortToNs.clone();
    copy.longToNs = (HashMap<String, NamespaceObject>) this.longToNs.clone();
    return copy;
  }


  public String[] seperateNSfromURI(String literal) { //TODO verschoenern
    String namespace;
    int pos = literal.indexOf("#");
    //uri must be in short form
    if (pos != -1) {
      // get rid of <>
      namespace = literal.substring(1, pos + 1);
      literal = literal.substring(pos + 1, literal.length() - 1);
    } else {
      //uri should be in short form or have no namespace at all.
      pos = literal.indexOf(":");
      // check for empty namespace
      if (pos < 0) {
        pos = 0;
        namespace = "";
      } else {
        namespace = literal.substring(1, pos);
        if (namespace.equals("http")) {
          pos = 0;
          namespace = "";
        }
      }
      literal = literal.substring(pos + 1, literal.length() - 1);
    }
    return new String[]{namespace, literal};
  }

  public NamespaceObject getNamespaceObject(String namespaceString) {
    if (shortToNs.containsKey(namespaceString))
      return shortToNs.get(namespaceString);
    else if (longToNs.containsKey(namespaceString))
      return longToNs.get(namespaceString);
    else // we encountered an unknown namespace
      throw new IllegalArgumentException("Unknown Namespace " + namespaceString);
  }
}
