package de.dfki.lt.hfc;

import java.io.*;
import java.util.*;
import java.lang.reflect.Constructor;
import de.dfki.lt.hfc.types.XsdAnySimpleType;

/**
 * Namespace implements a bidirectional mapping between strings, given by
 * _short_ and _long_ form namespace names, as used in URIs and in type
 * specifiers of XSD atoms
 *
 * a namespace file contains a sequence of lines, whereas each line _must_
 * consists of exactly two strings, the first being the short form and the
 * second representing the long form namespace name;
 * comments are allowed and MUST start with the '#' character (in separate
 * lines)
 *
 * example
 *   rdf  http://www.w3.org/1999/02/22-rdf-syntax-ns#
 *   rdfs http://www.w3.org/2000/01/rdf-schema#
 *   xsd  http://www.w3.org/2001/XMLSchema
 *   owl  http://www.w3.org/2002/07/owl#
 *
 * NEW
 * since HFC v6.0.11, namespace files also support dynamic (XSD) type to
 * internal HFC Java class mappings
 *
 * for short-to-long mappings, use &short2long directive (new);
 * for type-to-class mappings, use &type2class directive (new);
 * @see default.ns (default namespace file for all rule sets)
 *
 * example
 *   &short2long
 *   rdf  http://www.w3.org/1999/02/22-rdf-syntax-ns#
 *   rdfs http://www.w3.org/2000/01/rdf-schema#
 *   xsd  http://www.w3.org/2001/XMLSchema
 *   owl  http://www.w3.org/2002/07/owl#
 *
 *   &type2class
 *   xsd:int  XsdInt
 *   xsd:long  XsdLong
 *   xsd:float  XsdFloat
 *   xsd:double  XsdDouble
 *   xsd:string  XsdString
 *
 * @author (C) Hans-Ulrich Krieger
 * @since JDK 1.5
 * @version Mon Oct  5 09:45:59 CEST 2015
 */
public final class Namespace {
	
  /**
   * all (custom) types should be put in package de.dfki.lt.hfc.types
   */
  public static final String TYPE_PATH = "de.dfki.lt.hfc.types.";  // last '.' char is OK
  
  /**
   * the two directives that can be found in a namespace file
   */
  public static final String SHORT_TO_LONG = "&short2long";
  public static final String TYPE_TO_CLASS = "&type2class";
  
	/**
	 * what follows are useful constants that can be accessed within each class
	 * implementations for the below XSD types can be found in HFC in package de.dfki.lt.hfc.types
	 */
  
	// SHORT and LONG namespace for XSD, RDF, RDFS, OWL (1.0), OWL 1.1
	
	public static final String XSD_SHORT = "xsd";
	public static final String XSD_LONG = "http://www.w3.org/2001/XMLSchema#";

	public static final String RDF_SHORT = "rdf";
	public static final String RDF_LONG = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
	
	public static final String RDFS_SHORT = "rdfs";
	public static final String RDFS_LONG = "http://www.w3.org/2000/01/rdf-schema#";
	
	public static final String OWL_SHORT = "owl";
	public static final String OWL_LONG = "http://www.w3.org/2002/07/owl#";
  
	// XSD: string, int, long, float, double, gYear, gYearMonth, gMonth, gMonthDay, gDay, date, dateTime, duration, boolean, anyURI
	
	public static final String XSD_STRING_SHORT = "<xsd:string>";
	public static final String XSD_STRING_LONG = "<http://www.w3.org/2001/XMLSchema#string>";
	
	public static final String XSD_INT_SHORT = "<xsd:int>";
	public static final String XSD_INT_LONG = "<http://www.w3.org/2001/XMLSchema#int>";
	
	public static final String XSD_LONG_SHORT = "<xsd:long>";
	public static final String XSD_LONG_LONG = "<http://www.w3.org/2001/XMLSchema#long>";
	
	public static final String XSD_FLOAT_SHORT = "<xsd:float>";
	public static final String XSD_FLOAT_LONG = "<http://www.w3.org/2001/XMLSchema#float>";
	
	public static final String XSD_DOUBLE_SHORT = "<xsd:double>";
	public static final String XSD_DOUBLE_LONG = "<http://www.w3.org/2001/XMLSchema#double>";
	
	public static final String XSD_GYEAR_SHORT = "<xsd:gYear>";
	public static final String XSD_GYEAR_LONG = "<http://www.w3.org/2001/XMLSchema#gYear>";
	
	public static final String XSD_GYEARMONTH_SHORT = "<xsd:gYearMonth>";
	public static final String XSD_GYEARMONTH_LONG = "<http://www.w3.org/2001/XMLSchema#gYearMonth>";
	
	public static final String XSD_GMONTH_SHORT = "<xsd:gMonth>";
	public static final String XSD_GMONTH_LONG = "<http://www.w3.org/2001/XMLSchema#gMonth>";
	
	public static final String XSD_GMONTHDAY_SHORT = "<xsd:gMonthDay>";
	public static final String XSD_GMONTHDAY_LONG = "<http://www.w3.org/2001/XMLSchema#gMonthDay>";
	
	public static final String XSD_GDAY_SHORT = "<xsd:gDay>";
	public static final String XSD_GDAY_LONG = "<http://www.w3.org/2001/XMLSchema#gDay>";
	
	public static final String XSD_DATE_SHORT = "<xsd:date>";
	public static final String XSD_DATE_LONG = "<http://www.w3.org/2001/XMLSchema#date>";
	
	public static final String XSD_DATETIME_SHORT = "<xsd:dateTime>";
	public static final String XSD_DATETIME_LONG = "<http://www.w3.org/2001/XMLSchema#dateTime>";
	
	public static final String XSD_DURATION_SHORT = "<xsd:duration>";
	public static final String XSD_DURATION_LONG = "<http://www.w3.org/2001/XMLSchema#duration>";
	
	public static final String XSD_BOOLEAN_SHORT = "<xsd:boolean>";
	public static final String XSD_BOOLEAN_LONG = "<http://www.w3.org/2001/XMLSchema#boolean>";
	
	public static final String XSD_ANYURI_SHORT = "<xsd:anyURI>";
	public static final String XSD_ANYURI_LONG = "<http://www.w3.org/2001/XMLSchema#anyURI>";
  
	// NOT predefined XSD datatypes: uDateTime, monetary
	
  public static final String XSD_UDATETIME_SHORT = "<xsd:uDateTime>";
	public static final String XSD_UDATETIME_LONG = "<http://www.w3.org/2001/XMLSchema#uDateTime>";
	
  public static final String XSD_MONETARY_SHORT = "<xsd:monetary>";
	public static final String XSD_MONETARY_LONG = "<http://www.w3.org/2001/XMLSchema#monetary>";
	
	// RDF: type
	
	public static final String RDF_TYPE_SHORT = "<rdf:type>";
	public static final String RDF_TYPE_LONG = "<http://www.w3.org/1999/02/22-rdf-syntax-ns#type>";
	
	// special value UNBOUND/NULL, not used at the moment
	
	public static final String UNBOUND = "NULL";
	public static final int UNBOUND_ID = 0;
	
	// RDFS: subClassOf
	
  public static final String RDFS_SUBCLASSOF_SHORT = "<rdfs:subClassOf>";
	public static final String RDFS_SUBCLASSOF_LONG = "<http://www.w3.org/2000/01/rdf-schema#subClassOf>";
	public static final int RDFS_SUBCLASSOF_ID = 1;
  
	// OWL: sameAs, equivalentClass, equivalentProperty, disjointWith
	
	public static final String OWL_SAMEAS_SHORT = "<owl:sameAs>";
	public static final String OWL_SAMEAS_LONG = "<http://www.w3.org/2002/07/owl#sameAs>";
	public static final int OWL_SAMEAS_ID = 2;
	
	public static final String OWL_EQUIVALENTCLASS_SHORT = "<owl:equivalentClass>";
	public static final String OWL_EQUIVALENTCLASS_LONG = "<http://www.w3.org/2002/07/owl#equivalentClass>";
	public static final int OWL_EQUIVALENTCLASS_ID = 3;
	
	public static final String OWL_EQUIVALENTPROPERTY_SHORT = "<owl:equivalentProperty>";
	public static final String OWL_EQUIVALENTPROPERTY_LONG = "<http://www.w3.org/2002/07/owl#equivalentProperty";
	public static final int OWL_EQUIVALENTPROPERTY_ID = 4;
	
	public static final String OWL_DISJOINTWITH_SHORT = "<owl:disjointWith>";
	public static final String OWL_DISJOINTWITH_LONG = "<http://www.w3.org/2002/07/owl#disjointWith>";
	public static final int OWL_DISJOINTWITH_ID = 5;
	
	/**
	 * determines whether short (= true) or long (= false) namespaces are the
	 * cannonical form;
	 * perhaps make this field an instance field, if needed later (that's why
	 * we use camel case to name this field)
	 */
	public static boolean shortIsDefault = true;
	
	/**
	 * if true, some statistics are printed out
	 */
	public boolean verbose = true;
	
	/**
	 * translation table for establishing mappings between short and long form
	 * namespace strings
	 */
	protected HashMap<String, String> shortToLong = new HashMap<String, String>();
	
	/**
	 * translation table for establishing mappings between long and short form
	 * namespace strings
	 */
	protected HashMap<String, String> longToShort = new HashMap<String, String>();
  
  /**
   * a mapping between XSD type specifiers and Java classes representing these types in HFC
   */
  protected HashMap<String, Constructor<XsdAnySimpleType>> typeToConstructor = new HashMap<String, Constructor<XsdAnySimpleType>>();
  
	/**
	 * creates a default namespace, consisting only of mappings for RDF, RDFS, and OWL (1.0)
	 */
	public Namespace() {
		putForm(Namespace.XSD_SHORT, Namespace.XSD_LONG);
		putForm(Namespace.RDF_SHORT, Namespace.RDF_LONG);
		putForm(Namespace.RDFS_SHORT, Namespace.RDFS_LONG);
		putForm(Namespace.OWL_SHORT, Namespace.OWL_LONG);
		if (this.verbose)
			System.out.println("\n  defining default namespace for XSD, RDF, RDFS, and OWL ..."); 
	}
	
	/**
	 * creates a new namespace, whereas the mappings are specified in the
	 * namesapace file;
	 * default settings for RDF, RDFS, and OWL are _not_ entered, thus _must_ be
	 * specified explicitly in the file
	 */
	public Namespace(String namespaceFile) {
		readNamespaces(namespaceFile);
	}
	
	/**
	 *
	 */
	public Namespace(String namespaceFile, boolean verbose) {
		this.verbose = verbose;
		readNamespaces(namespaceFile);
	}
	
	/**
	 * adds a new mapping to the namespace
	 */
	public void putForm(String shortForm, String longForm) {
		this.shortToLong.put(shortForm, longForm);
		this.longToShort.put(longForm, shortForm);
	}

	/**
	 * obtains the short form, if present; null otherwise
	 */
	public String getShortForm(String longForm) {
		return this.longToShort.get(longForm);
	}
	
	/**
	 * obtains the long form, if present; null otherwise
	 */
	public String getLongForm(String shortForm) {
		return this.shortToLong.get(shortForm);
	}
	
	/**
	 * normalizeNamespace() takes a URI (without '<' and '>') and normalizes the
	 * namespace depending on global Namespace.shortIsDefault;
	 * if shortIsDefault == true, normalizeNamespace() tries to replace long forms
	 * by their short forms;
	 * if shortIsDefault == false, normalizeNamespace() tries to replace short forms
	 * by their long forms
	 */
	public String normalizeNamespace(String uri) {
		if (Namespace.shortIsDefault) {
			// read characters until we find a '#'
			int pos = uri.indexOf("#");
			if (pos == -1)
				// URI already in short form or no type info but instead a language tag
				return uri;
			String prefix = uri.substring(0, pos + 1);
			String suffix = uri.substring(pos + 1);
			String expansion = getShortForm(prefix);
			if (expansion == null)
				// there is no namespace maping
				return uri;
			else
				return expansion + ":" + suffix;
		}
		else {
			// read characters until we find a ':'
			int pos = uri.indexOf(":");
			if (pos == -1)
				// URI already in long form or no type info but instead a language tag
				return uri;
			String prefix = uri.substring(0, pos);
			String suffix = uri.substring(pos + 1);
			String expansion = getLongForm(prefix);
			if (expansion == null)
				// there is no namespace maping
				return uri;
			else
				return expansion + suffix;
		}
	}
	
	/**
	 * this method borrows code from normalizeNamespace() above and always tries to fully
	 * expand the namespace prefix of an URI, even if Namespace.shortIsDefault == true
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
		String expansion = getLongForm(prefix);
		// namespace maping specified?
		if (expansion == null)
			return uri;
		else
			return "<" + expansion + suffix;
	}

	/**
   * namespace files define two mappings:
   *   (1) short to long namespace name mappings, using directive &short2long
   *   (2) short namespace type URIs to HFC classes, using directive &type2class
   * usually, one starts
	 */
	public void readNamespaces(String filename) {
		if (this.verbose)
			System.out.println("\n  reading namespace & type mappings from " + filename + " ...");
    String line, first, second;
    StringTokenizer st;
		int noOfNamespaces = 0;
    int noOfTypes = 0;
    // section number:
    //   0 : no directive read
    //   1 : SHORT_TO_LONG
    //   2 : TYPE_TO_CLASS
    int sectionNo = 0;  // no directive so far
		try {
      // the class object for the class String which is used to specify the unary XSD type constructor (string arg)
      final Class stringClass = Class.forName("java.lang.String");   // need the _fully qualified_ name
      final BufferedReader br = new BufferedReader(new FileReader(filename));
      while ((line = br.readLine()) != null) {
				line = line.trim();
				// allow empty lines and comments in lines
        if ((line.length() == 0) || (line.startsWith("#"))) {
          continue;
        }
        if (line.startsWith(Namespace.SHORT_TO_LONG)) {
          sectionNo = 1;
          continue;
        }
        if (line.startsWith(Namespace.TYPE_TO_CLASS)) {
          sectionNo = 2;
          continue;
        }
        if (sectionNo == 1) {
          st = new StringTokenizer(line);
          putForm(st.nextToken(), st.nextToken());
          ++noOfNamespaces;
        }
        else if (sectionNo == 2) {
          st = new StringTokenizer(line);
          first = "<" + st.nextToken() + ">";  // add the angle brackets
          second = st.nextToken();
          Class clazz = Class.forName(Namespace.TYPE_PATH + second);
          // choose the unary constructor taking String arguments
          Constructor<XsdAnySimpleType> constructor = clazz.getConstructor(stringClass);
          this.typeToConstructor.put(first, constructor);
          ++noOfTypes;
        }
        else {
          System.err.println("\nerror while reading namespace & type mappings from " + filename + ": missing directive");
          System.exit(1);
        }
			}
		}
		catch (Exception e) {
			System.err.println("\nerror while reading namespace & type mappings from " + filename);
			System.exit(1);
		}
    // as TYPE_TO_CLASS might appear _before_ SHORT_TO_LONG, expand short name to long name and
    // record this mapping as well (it might also happen that we have several namespace files);
    // make sure to have a shallow copy of the keys (or key-value pairs) as iteration will modify
    // the backed-up set !!
    Set<String> keyset = this.typeToConstructor.keySet();
    for (String key : keyset.toArray(new String[keyset.size()])) {
      this.typeToConstructor.put(expandUri(key),
                                 this.typeToConstructor.get(key));
    }
    if (this.verbose) {
			System.out.println("\n  read " + noOfNamespaces + " namespace mappings");
      System.out.println("  read " + noOfTypes + " type mappings");
    }
	}
	
	/**
	 * FOR TEST PURPOSES ONLY
	 */
	/*
	public static void main(String[] args) {
		Namespace ns = new Namespace();
		Namespace.shortIsDefault = false;
		String s1 = "owl:Class";
		String l1 = "http://www.w3.org/2002/07/owl#Class";
		// unknown short form namespace
		String s2 = "foo:Bar";
		// unknown long form namespace
		String l2 = "http://www.w3.org/2002/07/baz#yummi";
		// neither short nor long form
		String nsnl = "sfhsdkjhfwufwiuehfhi";
		System.out.println(s1 + " --> " + ns.normalizeNamespace(s1));
		System.out.println(l1 + " --> " + ns.normalizeNamespace(l1));
		System.out.println(s2 + " --> " + ns.normalizeNamespace(s2));
		System.out.println(l2 + " --> " + ns.normalizeNamespace(l2));
		System.out.println(nsnl + " --> " + ns.normalizeNamespace(nsnl));
	}
	 */

}