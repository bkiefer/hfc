package de.dfki.lt.hfc;

import de.dfki.lt.hfc.types.AnyType;
import de.dfki.lt.hfc.types.Uri;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.HashSet;

/**
 * NamespaceManager implements a bidirectional mapping between strings, given by
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
public final class NamespaceManager {

  /**
   * what follows are useful constants that can be accessed within each class
   * implementations for the below XSD types can be found in HFC in package de.dfki.lt.hfc.types
   */

  // SHORT and LONG namespace for XSD, RDF, RDFS, OWL (1.0), OWL 1.1
  public static NamespaceManager instance = null;

  public static final Namespace XSD = new Namespace("xsd", "http://www.w3.org/2001/XMLSchema#", false);
  public static final Namespace RDF = new Namespace("rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#", false);
  public static final Namespace RDFS = new Namespace("rdfs", "http://www.w3.org/2000/01/rdf-schema#", false);
  public static final Namespace OWL = new Namespace("owl", "http://www.w3.org/2002/07/owl#", false);
  public static final Namespace TEST = new Namespace("test", "http://www.dfki.de/lt/onto/test.owl#", false);
  public static final Namespace EMPTY = new Namespace("", "", false);
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
  private static final Logger logger = LoggerFactory.getLogger(NamespaceManager.class);
  /**
   * translation table for establishing mappings between short and long form
   * namespace strings
   */
  public HashMap<String, Namespace> shortToNs = new HashMap<String, Namespace>();
  /**
   * translation table for establishing mappings between long and short form
   * namespace strings
   */
  public HashMap<String, Namespace> longToNs = new HashMap<String, Namespace>();
  private boolean shortIsDefault = false;
  private HashSet<Namespace> allNamespaces = new HashSet<>();

  private NamespaceManager() {
    if(instance != null)
      throw new IllegalStateException("Already instatiated");
    shortIsDefault = false;
    addNamespace(EMPTY);
    addNamespace(XSD);
    addNamespace(OWL);
    addNamespace(RDF);
    addNamespace(RDFS);
    addNamespace(TEST);
  }

  public NamespaceManager(HashSet<Namespace> allNamespaces, HashMap<String, Namespace> shortToNs, HashMap<String, Namespace> longToNs, boolean shortIsDefault) {
    this.allNamespaces = allNamespaces;
    this.shortToNs = shortToNs;
    this.longToNs = longToNs;
    this.shortIsDefault = shortIsDefault;
  }

  public static void clear() {
    NamespaceManager.EMPTY.setIsShort(false);
    NamespaceManager.XSD.setIsShort(false);
    NamespaceManager.OWL.setIsShort(false);
    NamespaceManager.RDF.setIsShort(false);
    NamespaceManager.RDFS.setIsShort(false);
    NamespaceManager.TEST.setIsShort(false);
    instance = null;
  }

  public boolean isShortIsDefault() {
    return shortIsDefault;
  }

  public void setShortIsDefault(boolean shortIsDefault) {
    this.shortIsDefault = shortIsDefault;
    for (Namespace ns : shortToNs.values()) {
      ns.setIsShort(this.shortIsDefault);
    }
    if (shortToNs.size() != longToNs.size())
      for (Namespace ns : shortToNs.values()) {
        ns.setIsShort(this.shortIsDefault);
      }
  }

  public static NamespaceManager getInstance(){
    if(instance == null) {
      instance = new NamespaceManager();
    }
    return instance;
  }

  /**
   * a mapping between XSD type specifiers and Java classes representing these types
   * in HFC; now in XsdAnySimpleType, where it belongs
   */
  public void addNamespace(Namespace ns) {
    this.allNamespaces.add(ns);
    this.shortToNs.put(ns.SHORT_NAMESPACE, ns);
    this.longToNs.put(ns.LONG_NAMESPACE, ns);
  }

  /**
   * adds a new mapping to the namespace
   */
  public void putForm(String shortForm, String longForm, boolean shortIsDefault) {
    assert !shortForm.isEmpty();
    assert !longForm.isEmpty();
    if (shortToNs.containsKey(shortForm))
      shortToNs.get(shortForm).setIsShort(shortIsDefault);
    else if (longToNs.containsKey(longForm))
      longToNs.get(longForm).setIsShort(shortIsDefault);
    else {
      Namespace ns = new Namespace(shortForm, longForm, shortIsDefault);
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


  public NamespaceManager copy() {
    NamespaceManager copy = new NamespaceManager(allNamespaces, shortToNs,longToNs,shortIsDefault);

    return copy;
  }


  public String[] separateNSfromURI(String literal) { //TODO verschoenern
    String namespace;
    int pos = literal.lastIndexOf("#");
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

  public Namespace getNamespaceObject(String namespaceString) {
    if (shortToNs.containsKey(namespaceString))
      return shortToNs.get(namespaceString);
    else if (longToNs.containsKey(namespaceString))
      return longToNs.get(namespaceString);
    else // we encountered an unknown namespace
      throw new IllegalArgumentException("Unknown NamespaceManager " + namespaceString);
  }
}
