package de.dfki.lt.hfc;

import java.io.*;
import java.util.*;

/**
 * Namespace implements a bidirectional mapping between strings, given by
 * _short_ and _long_ form namespaces, as used in URIs and in type specifiers
 * of XSD atoms
 *
 * a namespace file is a sequence of lines, whereas each line _must_ consists of
 * exactly two strings, the first being the short form and the second representing
 * the long form namespace;
 * comments are allowed and MUST start with the '#' character
 *
 * example
 *   rdf  http://www.w3.org/1999/02/22-rdf-syntax-ns#
 *   rdfs http://www.w3.org/2000/01/rdf-schema#
 *   owl  http://www.w3.org/2002/07/owl#
 *   xsd  http://www.w3.org/2001/XMLSchema
 *
 *
 * @author (C) Hans-Ulrich Krieger
 * @since JDK 1.5
 * @version Thu Jul 25 16:00:54 CEST 2013
 */

public final class Namespace {
	
	/**
	 * what follows are useful constants that can be accessed within each class
	 * implementations for the below XSD types can be found in HFC in package de.dfki.lt.hfc.types
	 */
	
	// SHORT and LONG namespace
	
	public static final String XSD_SHORT = "xsd";
	
	public static final String XSD_LONG = "http://www.w3.org/2001/XMLSchema#";

	public static final String RDF_SHORT = "rdf";
	
	public static final String RDF_LONG = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
	
	public static final String RDFS_SHORT = "rdfs";
	
	public static final String RDFS_LONG = "http://www.w3.org/2000/01/rdf-schema#";
	
	public static final String OWL_SHORT = "owl";
	
	public static final String OWL_LONG = "http://www.w3.org/2002/07/owl#";
	
	// XSD: string, int, long, float, double, dateTime, boolean
	
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
	
	// not a predefined XSD data type, but would like to use the xsd namespace, instead of, say, dfki
	public static final String XSD_UDATETIME_SHORT = "<xsd:uDateTime>";
	
	public static final String XSD_UDATETIME_LONG = "<http://www.w3.org/2001/XMLSchema#uDateTime>";
	
	// not a predefined XSD data type, but would like to use the xsd namespace, instead of, say, dfki
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
	 * default settings for RDF, RDFS, and OWL are entered, thus _must_ be
	 * specified in the file
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
	 * assume that a line in a namespace file consists of exactly two strings,
	 * separated by at least one whitespace character;
	 * the first element is the short form, the second the long form
	 */
	public void readNamespaces(String filename) {
		if (this.verbose)
			System.out.println("\n  reading namespaces from " + filename + " ...");
		String line, token;
    StringTokenizer st;
		int noOfMappings = 0;
		try {
      BufferedReader br = new BufferedReader(new FileReader(filename));
      while ((line = br.readLine()) != null) {
				line = line.trim();
				// allow empty lines and comments in lines
				if ((line.length() == 0) || (line.startsWith("#")))
					continue;
				st = new StringTokenizer(line);
				putForm(st.nextToken(), st.nextToken());
				++noOfMappings;
			}
		}
		catch (IOException e) {
			System.err.println("\nerror while reading namespaces from " + filename);
			System.exit(1);
		}
		if (this.verbose)
			System.out.println("\n  read " + noOfMappings + " namespace mappings");
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
