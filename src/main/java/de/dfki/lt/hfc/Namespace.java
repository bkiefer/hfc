package de.dfki.lt.hfc;

import java.io.*;
import java.util.*;
import java.lang.reflect.Constructor;
import java.nio.file.Files;

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
   * Now in XsdAnySimpleType, where it belongs.
   */
  //protected HashMap<String, Constructor<XsdAnySimpleType>> typeToConstructor = new HashMap<String, Constructor<XsdAnySimpleType>>();

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
	 * @throws IOException
	 * @throws WrongFormatException
	 * @throws FileNotFoundException
	 */
	public Namespace(String namespaceFile)
	    throws FileNotFoundException, WrongFormatException, IOException {
		readNamespaces(namespaceFile);
	}

	/**
	 * @throws IOException
	 * @throws WrongFormatException
	 * @throws FileNotFoundException
	 *
	 */
	public Namespace(String namespaceFile, boolean verbose)
	    throws FileNotFoundException, WrongFormatException, IOException {
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
	 * @throws WrongFormatException In case the section name is missing, this
	 *         exception is thrown
	 * @throws IOException
	 */
	public void readNamespaces(final BufferedReader br) throws WrongFormatException, IOException {
    String line, first, second;
    StringTokenizer st;
		int noOfNamespaces = 0;
    int noOfTypes = 0;
    // section number:
    //   0 : no directive read
    //   1 : SHORT_TO_LONG
    //   2 : TYPE_TO_CLASS
    int sectionNo = 0;  // no directive so far
    // the class object for the class String which is used to specify the unary XSD type constructor (string arg)
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
        /* TODO: obsolete, we should go back to the namespace only files
        st = new StringTokenizer(line);
        first = "<" + st.nextToken() + ">";  // add the angle brackets
        second = st.nextToken();
        try {
          @SuppressWarnings("rawtypes")
          Class clazz = Class.forName(Namespace.TYPE_PATH + second);
          // choose the unary constructor taking String arguments
          @SuppressWarnings("unchecked")
          Constructor<XsdAnySimpleType> constructor = clazz.getConstructor(String.class);
          this.typeToConstructor.put(first, constructor);
          ++noOfTypes;
        }
        catch (SecurityException ex) {
          throw new WrongFormatException(
              "XSD to Java class mapping failed for: " + line, ex);
        }
        catch (NoSuchMethodException ex) {
          throw new WrongFormatException(
              "XSD to Java class mapping failed for: " + line, ex);
        }
        catch (ClassNotFoundException ex) {
          throw new WrongFormatException(
              "XSD to Java class mapping failed for: " + line, ex);
        }
        */
      }
      else {
        throw new WrongFormatException(
            "Missing directive reading namespace file in line " + line);
      }
    }
    if (this.verbose) {
			System.out.println("\n  read " + noOfNamespaces + " namespace mappings");
      System.out.println("  read " + noOfTypes + " type mappings");
    }
	}

	 public void readNamespaces(String filename)
	     throws WrongFormatException, IOException {
	   if (this.verbose)
	     System.out.println("\n  reading namespace & type mappings from " + filename + " ...");
     readNamespaces(Files.newBufferedReader(new File(filename).toPath()));
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
