package de.dfki.lt.hfc;

import de.dfki.lt.hfc.types.AnyType;
import de.dfki.lt.hfc.types.Uri;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;
import java.util.StringTokenizer;

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
 *
 * It is important that no two Namespace objects will exist that are equals.
 * Even if a mapping is introduced after the long form was used in an URI, the
 * namespace object in the uri will remain valid, it will only reflect the new
 * mapping. That also allows to do the test on equal namespaces with == safely.
 */
public final class NamespaceManager {

  public static class Namespace {

    private String SHORT_NAMESPACE;
    private String LONG_NAMESPACE;

    private boolean isShort;

    @Override
    public boolean equals(Object o) {
      if (this == o)
        return true;
      if (o == null || getClass() != o.getClass())
        return false;
      Namespace that = (Namespace) o;
      return isShort == that.isShort
          && Objects.equals(getShort(), that.getShort())
          && Objects.equals(getLong(), that.getLong());
    }

    @Override
    public int hashCode() {
      return Objects.hash(getShort(), getLong(), isShort);
    }

    Namespace(String shortNamespace, String longNamespace, boolean isShort) {
      this.SHORT_NAMESPACE = shortNamespace;
      this.LONG_NAMESPACE = longNamespace;
      setIsShort(isShort);
    }

    /**
     * @return the sHORT_NAMESPACE
     */
    public String getShort() {
      return SHORT_NAMESPACE;
    }

    /**
     * @return the lONG_NAMESPACE
     */
    public String getLong() {
      return LONG_NAMESPACE;
    }

    public void setIsShort(boolean isShort) {
      // if this is not a full mapping, isShort plays no role
      if (this.SHORT_NAMESPACE == null) {
        this.isShort = false;
      } else if (this.LONG_NAMESPACE == null) {
        this.isShort = true;
      } else {
        this.isShort = isShort;
      }
    }

    public boolean isShort() {
      return isShort;
    }

    @Override
    public String toString() {
      return isShort ? getShort() + ":" : getLong();
    }
  }

  /**
   * what follows are useful constants that can be accessed within each class
   * implementations for the below XSD types can be found in HFC in package de.dfki.lt.hfc.types
   */

  // SHORT and LONG namespace for XSD, RDF, RDFS, OWL (1.0), OWL 1.1
  //public static NamespaceManager instance = null;

  public static final Namespace XSD = new Namespace("xsd", "http://www.w3.org/2001/XMLSchema#", true);
  public static final Namespace RDF = new Namespace("rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#", true);
  public static final Namespace RDFS = new Namespace("rdfs", "http://www.w3.org/2000/01/rdf-schema#", true);
  public static final Namespace OWL = new Namespace("owl", "http://www.w3.org/2002/07/owl#", true);
  public static final Namespace TEST = new Namespace("test", "http://www.dfki.de/lt/onto/test.owl#", true);
  public static final Namespace EMPTY = new Namespace("", "", true);
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
  private boolean shortIsDefault = true;
  private HashSet<Namespace> allNamespaces = new HashSet<>();

  NamespaceManager() {
    //if(instance != null)
    //  throw new IllegalStateException("Already instatiated");
    shortIsDefault = true;
    addNamespace(EMPTY);
    addNamespace(XSD);
    addNamespace(OWL);
    addNamespace(RDF);
    addNamespace(RDFS);
    addNamespace(TEST);
  }

  private NamespaceManager(HashSet<Namespace> allNamespaces, HashMap<String, Namespace> shortToNs, HashMap<String, Namespace> longToNs, boolean shortIsDefault) {
    this.allNamespaces = allNamespaces;
    this.shortToNs = shortToNs;
    this.longToNs = longToNs;
    this.shortIsDefault = shortIsDefault;
  }

  public static void clear() {
    NamespaceManager.EMPTY.setIsShort(true);
    NamespaceManager.XSD.setIsShort(true);
    NamespaceManager.OWL.setIsShort(true);
    NamespaceManager.RDF.setIsShort(true);
    NamespaceManager.RDFS.setIsShort(true);
    NamespaceManager.TEST.setIsShort(true);
    //instance = null;
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
    /*
    if(instance == null) {
      instance = new NamespaceManager();
    }
    return instance;
    */
    return new NamespaceManager();
  }

  /**
   * a mapping between XSD type specifiers and Java classes representing these types
   * in HFC; now in XsdAnySimpleType, where it belongs
   */
  private void addNamespace(Namespace ns) {
    this.allNamespaces.add(ns);
    this.shortToNs.put(ns.getShort(), ns);
    this.longToNs.put(ns.getLong(), ns);
  }

  /**
   * Adds a new mapping to the namespace.
   *
   * If the short form is already present, nothing will be entered and an error
   * is signalled. If the some form is already present, the entry is only
   * accepted if the other form of the namespace object is empty, which means a
   * tuple of that namespace string was seen before the mapping was entered.
   */
  public boolean putForm(String shortForm, String longForm, boolean shortIsDefault) {
    assert !shortForm.isEmpty();
    assert !longForm.isEmpty();
    if (shortToNs.containsKey(shortForm) || longToNs.containsKey(longForm)) {
      Namespace ns1 = shortToNs.get(shortForm);
      Namespace ns2 = longToNs.get(longForm);
      // make sure the rest of the definition is consistent with what's already
      // there
      if (ns1 != null || ns2 != null) {
        // simply ignore already existing mappings
        if (ns1 == ns2) return true;
        // if this is a "proper" mapping, e.g. ns1 != ns2, then this is an
        // attempt to overwrite an existing mapping
        if (ns1 != null && ns1.LONG_NAMESPACE != null
            || ns2 != null && ns2.SHORT_NAMESPACE != null) {
          logger.error("Ignoring diverging namespace mappings: {}/{} vs. {}/{}!",
              shortForm, longForm,
              ns2 == null ? shortForm : ns2.SHORT_NAMESPACE,
              ns1 == null ? longForm : ns1.LONG_NAMESPACE);
          return false;
        } else {
          if (ns1 == null) {
            // ns1 must be null here: no short form like this exists
            ns2.SHORT_NAMESPACE = shortForm;
            this.shortToNs.put(shortForm, ns2);
            ns2.isShort = shortIsDefault;
          } else {
            // ns2 must be null here: no long form like this exists
            ns1.LONG_NAMESPACE = longForm;
            this.longToNs.put(shortForm, ns1);
            ns1.isShort = shortIsDefault;
          }
        }
      }
    } else {
      addNamespace(new Namespace(shortForm, longForm, shortIsDefault));
    }
    return true;
  }

  /**
   * obtains the short form, if present; null otherwise
   */
  public String getShortForm(String longForm) {
    return this.longToNs.get(longForm).getShort();
  }

  /**
   * obtains the long form, if present; null otherwise
   */
  public String getLongForm(String shortForm) {
    return this.shortToNs.get(shortForm).getLong();
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
    String expansion = shortToNs.get(prefix).getLong();
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

  /** TODO could that be done more nicely? */
  public Pair<Namespace, String> separateNSfromURI(String literal) {
    String namespace;
    int pos = literal.lastIndexOf("#");
    if (pos != -1) {
      // uri must be in long form: get also rid of <>
      namespace = literal.substring(1, pos + 1);
      literal = literal.substring(pos + 1, literal.length() - 1);
    } else {
      //uri should be in short form or have no namespace at all.
      pos = literal.indexOf(":"); // this may also match the "method" http:
      // check for empty namespace
      if (pos < 0) {
        pos = 0;
        namespace = "";
      } else {
        namespace = literal.substring(1, pos);
        if (namespace.equals("http")) {
          // get rid of <>
          namespace = literal.substring(1, literal.length() -1 );
          literal = ""; // name is empty
        } else {
          literal = literal.substring(pos + 1, literal.length() - 1);
        }
      }
    }
    return new Pair<Namespace, String>(getNamespaceObject(namespace), literal);
  }

  public Namespace getNamespaceObject(String namespaceString) {
    if (namespaceString == null || namespaceString.isEmpty()) {
      throw new WrongFormatException("Empty namespace");
    }
    boolean longNs = namespaceString.charAt(namespaceString.length() - 1) == '#';
    if (! longNs && shortToNs.containsKey(namespaceString))
      return shortToNs.get(namespaceString);
    else if (longNs && longToNs.containsKey(namespaceString))
      return longToNs.get(namespaceString);
    else { // we encountered an unknown namespace
      //throw new UnknownNamespaceException("Unknown NamespaceManager " + namespaceString);
      // write a log message then create a new namespace
      logger.info("Unknown namespace {}, created new namespace mapping", namespaceString);
      Namespace namespace;
      if (longNs) {
        namespace = new Namespace(null, namespaceString, shortIsDefault);
        longToNs.put(namespaceString, namespace);
      } else {
        namespace = new Namespace(namespaceString, null, shortIsDefault);
        shortToNs.put(namespaceString, namespace);
      }
      return namespace;
    }
  }

  public String getXSDNamespace(StringTokenizer st) {
    StringBuilder stb = new StringBuilder();
    String token;
    while(st.hasMoreTokens()){
      token = st.nextToken();
      if (!token.equals(">")){
        stb.append(token);
      } else {
        stb.append(token);
        break;
      }
    }
    String namespace = stb.toString();
    if (namespace.endsWith(">")){
      return namespace;
    } else {
      throw new IllegalArgumentException("Illegal or unknown namespace: " + namespace);
    }
  }
}
