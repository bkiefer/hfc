package de.dfki.lt.hfc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.HashSet;
import java.util.StringTokenizer;

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

	private static final Logger logger = LoggerFactory.getLogger(Namespace.class);

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

	public static final String TEST_SHORT = "test";
	public static final String TEST_LONG = "http://www.dfki.de/lt/onto/test.owl";

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
	public static final String OWL_EQUIVALENTPROPERTY_LONG = "<http://www.w3.org/2002/07/owl#equivalentProperty>";
	public static final int OWL_EQUIVALENTPROPERTY_ID = 4;

	public static final String OWL_DISJOINTWITH_SHORT = "<owl:disjointWith>";
	public static final String OWL_DISJOINTWITH_LONG = "<http://www.w3.org/2002/07/owl#disjointWith>";
	public static final int OWL_DISJOINTWITH_ID = 5;


	private HashSet<NamespaceObject> allNamespaces = new HashSet<>();

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


	public boolean shortIsDefault = false;
	/**
	 * @deprecated for test only
	 * @return
	 */
	public static Namespace defaultNamespace(){
		Namespace namespace = new Namespace();
		namespace.shortIsDefault = true;
		namespace.putForm(XSD_SHORT,XSD_LONG,namespace.shortIsDefault);
		namespace.putForm(RDF_SHORT, RDF_LONG, namespace.shortIsDefault);
		namespace.putForm(RDFS_SHORT, RDFS_LONG, namespace.shortIsDefault);
		namespace.putForm(OWL_SHORT, OWL_LONG, namespace.shortIsDefault);
		namespace.putForm(TEST_SHORT, TEST_LONG, namespace.shortIsDefault);
		return namespace;
	}

	/**
   * a mapping between XSD type specifiers and Java classes representing these types
	 * in HFC; now in XsdAnySimpleType, where it belongs
   */
  //protected HashMap<String, Constructor<XsdAnySimpleType>> typeToConstructor = new HashMap<String, Constructor<XsdAnySimpleType>>();

	public void addNamespace(NamespaceObject ns){
		this.allNamespaces.add(ns);
		this.shortToNs.put(ns.SHORT_NAMESPACE, ns);
		this.longToNs.put(ns.LONG_NAMESPACE, ns);
	}

	/**
	 * adds a new mapping to the namespace
	 */
	public void putForm(String shortForm, String longForm, boolean shortIsDefault) {
		NamespaceObject ns = new NamespaceObject(shortForm,longForm,shortIsDefault);
		this.allNamespaces.add(ns);
		this.shortToNs.put(shortForm, ns);
		this.longToNs.put(longForm, ns);
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
	 * normalizeNamespaceUri() takes a URI (without '<' and '>') and normalizes the
	 * namespace depending on global Namespace.shortIsDefault;
	 * if shortIsDefault == true, normalizeNamespaceUri() tries to replace long forms
	 * by their short forms;
	 * if shortIsDefault == false, normalizeNamespaceUri() tries to replace short forms
	 * by their long forms
	 */
	public String normalizeNamespaceUri(String uri) {
		if (shortIsDefault) {
			// read characters until we find a '#'
			int pos = uri.indexOf("#");
			if (pos == -1)
				// URI already in short form or no type info but instead a language tag
				return uri;
			String prefix = uri.substring(0, pos + 1);
			String suffix = uri.substring(pos + 1);
			String expansion = longToNs.get(prefix).SHORT_NAMESPACE;
			if (expansion == null)
				// there is no namespace mapping
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
			String expansion = shortToNs.get(prefix).LONG_NAMESPACE;
			if (expansion == null)
				// there is no namespace maping
				return uri;
			else
				return expansion + suffix;
		}
	}

	/**
		* normalizeNamespace() not only normalizes the the namespace name of an URI,
		* but also looks at the type specification of an XSD atom (again, a URI);
		* thanks Bernd
		*/
	public String normalizeNamespace(String s) {
		switch (s.charAt(0)) {
			case '<' :
				return '<'
								+ normalizeNamespaceUri(
								s.substring(1, s.length() - 1))
								+ '>';
			case '"' :
				// Atom, possibly with long xsd type spec
				int pos = s.lastIndexOf('^');
				if (pos > 0 && s.charAt(pos - 1) == '^') {
					return s.substring(0, pos + 2)
									+ normalizeNamespaceUri(s.substring(pos + 2, s.length() - 1))
									+ '>';
				}
		}
		return s;
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
		for (NamespaceObject ns : allNamespaces){
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

//	/**
//   * namespace files define two mappings:
//   *   (1) short to long namespace name mappings, using directive &short2long
//   *   (2) short namespace type URIs to HFC classes, using directive &type2class
//   * usually, one starts
//	 * @throws WrongFormatException In case the section name is missing, this
//	 *         exception is thrown
//	 * @throws IOException
//	 */
//	public void readNamespaces(final BufferedReader br) throws WrongFormatException, IOException {
//    String line;
//    StringTokenizer st;
//		int noOfNamespaces = 0;
//    // int noOfTypes = 0;  // no longer necessary
//    // section number:
//    //   0 : no directive read
//    //   1 : SHORT_TO_LONG
//    //   2 : TYPE_TO_CLASS
//    int sectionNo = 1;  // no directive so far
//    // BK: set to one to be compatible with old version
//    // the class object for the class String which is used to specify the unary XSD type constructor (string arg)
//    while ((line = br.readLine()) != null) {
//      line = line.trim();
//      // allow empty lines and comments in lines
//      if ((line.length() == 0) || (line.startsWith("#"))) {
//        continue;
//      }
//      if (line.startsWith(Namespace.SHORT_TO_LONG)) {
//        sectionNo = 1;
//        continue;
//      }
//      if (line.startsWith(Namespace.TYPE_TO_CLASS)) {
//        sectionNo = 2;
//        continue;
//      }
//      if (sectionNo == 1) {
//        st = new StringTokenizer(line);
//        putForm(st.nextToken(), st.nextToken());
//        ++noOfNamespaces;
//      }
//      else if (sectionNo == 2) {
//        /* TODO: obsolete, we should go back to the namespace only files
//        st = new StringTokenizer(line);
//        String first = "<" + st.nextToken() + ">";  // add the angle brackets
//        String second = st.nextToken();
//        try {
//          @SuppressWarnings("rawtypes")
//          Class clazz = Class.forName(Namespace.TYPE_PATH + second);
//          // choose the unary constructor taking String arguments
//          @SuppressWarnings("unchecked")
//          Constructor<XsdAnySimpleType> constructor = clazz.getConstructor(String.class);
//          this.typeToConstructor.put(first, constructor);
//          ++noOfTypes;
//        }
//        catch (SecurityException ex) {
//          throw new WrongFormatException(
//              "XSD to Java class mapping failed for: " + line, ex);
//        }
//        catch (NoSuchMethodException ex) {
//          throw new WrongFormatException(
//              "XSD to Java class mapping failed for: " + line, ex);
//        }
//        catch (ClassNotFoundException ex) {
//          throw new WrongFormatException(
//              "XSD to Java class mapping failed for: " + line, ex);
//        }
//        */
//      }
//      else {
//        throw new WrongFormatException(
//            "Missing directive reading namespace file in line " + line);
//      }
//    }
//    if (this.verbose) {
//			logger.info("\n  read " + noOfNamespaces + " namespace mappings");
//      //System.out.println("  read " + noOfTypes + " type mappings");  // no longer necessary
//    }
//	}
//
//	 public void readNamespaces(String filename)
//	     throws WrongFormatException, IOException {
//	   if (this.verbose)
//	     logger.info("\n  reading namespace & type mappings from " + filename + " ...");
//     readNamespaces(Files.newBufferedReader(new File(filename).toPath()));
//	 }


}
