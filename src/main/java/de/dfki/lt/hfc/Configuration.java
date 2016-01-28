package de.dfki.lt.hfc;

import java.io.*;
import java.util.*;

/**
 * Configuration is a collection of fields and methods which provides
 * convenient access to namespace, tuple store, rule store, forward chainer,
 * and query objects, iniatialized by the values associated with the
 * properties names, tuples, and rules (file names _without_ double quotes):
 *
 *   names = <namespace file>
 *   tuples = <tuple file>
 *   rules = <rule files>
 *
 * this class even support MULTIPLE namespace, tuple, and rule file
 * specification;
 *
 * it is REQUIRED to group namespace, tuple, and rule files AND to
 * to start with the namespaces, then moving to the tuples, and finally
 * to specify the rules;
 *
 * in order to separate the LHS from the RHS, even ":", ":=" or a
 * space character can be used;
 * note that each specification must be written in a separate line!
 *
 * important:
 *
 *   + the first namespace file triggers the introduction of a Namespace
 *     object
 *   + the first tuple file triggers the introduction of a TupleStore and
 *     a Query object
 *   + the first rule file triggers the introduction of a RuleStore and a
 *     ForwardChainer object
 *
 * this section might optionally be _preceeded_ by a control section, specifying
 * various things:
 *
 *   noofcores = <int>
 *   verbose = <boolean>
 *   rdfcheck = <boolean>
 *   eqreduction = <boolean>
 *   minnoofargs = <int>
 *   maxnoofargs = <int>
 *   noofatoms = <int>
 *   nooftuples = <int>
 *   names = <filename>
 *   tuples = <filename>
 *   rules = <filename>
 *
 * the order of these options does _not_ matter and even some of the options
 * might not be specified, meaning that default values are used:
 *
 *   noofcores = 2
 *   verbose = true
 *   rdfcheck = true
 *   minnoofargs = 3
 *   maxnoofargs = 3
 *   noofatoms = 100000
 *   nooftuples = 500000
 *
 * a config file might contain comments which must start with the '#' character
 *
 * @see de.dfki.lt.hfc.Namespace
 * @see de.dfki.lt.hfc.TupleStore
 * @see de.dfki.lt.hfc.Query
 * @see de.dfki.lt.hfc.RuleStore
 * @see de.dfki.lt.hfc.ForwardChainer
 *
 *
 * @author (C) Hans-Ulrich Krieger
 * @since JDK 1.5
 * @version Fri Aug 29 16:56:11 CEST 2014
 */
public class Configuration {

	/**
	 * the following private and public fields are assigned values
	 * when a config file is read in
	 */

	public int noofcores = 4;

	public boolean verbose = true;

	public boolean rdfcheck = true;

	public boolean eqreduction = true;

	public int minnoofargs = 3;

	public int maxnoofargs = 3;

	public int noofatoms = 100000;

	public int nooftuples = 500000;

	public Namespace namespace = null;

	public TupleStore tupleStore = null;

	public RuleStore ruleStore = null;

	public ForwardChainer forwardChainer = null;

	public Query query = null;

	private HashSet<String> files = new HashSet<String>();

	/**
	 * the unary constructor takes a config file as explained above
	 */
	public Configuration (String configFile) {
		String line, token;
		StringTokenizer st;
		try {
      BufferedReader br = new BufferedReader(new FileReader(configFile));
			while ((line = br.readLine()) != null) {
				// strip of spaces at begin and end of line
				line = line.trim();
				if (line.length() == 0 || line.startsWith("#"))
					continue;
				st = new StringTokenizer(line, " =:\t");
				processSpecification(st);
			}
		}
		catch (Exception e) {
			System.err.println("\nerror while reading configuration file " + configFile);
			System.exit(1);
		}
	}

	/**
	 *
	 */
	public void processSpecification(StringTokenizer st) throws Exception {
		// there must be at least one token
		String property = st.nextToken();
		String value;
		if (st.hasMoreTokens())
			value = st.nextToken();
		else
			throw new Exception("missing value for property " + property);
		if (st.hasMoreTokens())
			throw new Exception("more than one value for property " + property);
		else if (property.toUpperCase().equals("NOOFCORES"))
			this.noofcores = Integer.parseInt(value);
		else if (property.toUpperCase().equals("VERBOSE"))
			this.verbose = Boolean.parseBoolean(value);
		else if (property.toUpperCase().equals("RDFCHECK"))
			this.rdfcheck = Boolean.parseBoolean(value);
		else if (property.toUpperCase().equals("MINNOOFARGS"))
			this.minnoofargs = Integer.parseInt(value);
		else if (property.toUpperCase().equals("MAXNOOFARGS"))
			this.maxnoofargs = Integer.parseInt(value);
		else if (property.toUpperCase().equals("NOOFATOMS"))
			this.noofatoms = Integer.parseInt(value);
		else if (property.toUpperCase().equals("NOOFTUPLES"))
			this.nooftuples = Integer.parseInt(value);
		else if (property.toUpperCase().equals("NAMES"))
			processNames(value);
		else if (property.toUpperCase().equals("TUPLES"))
			processTuples(value);
		else if (property.toUpperCase().equals("RULES"))
			processRules(value);
		else if (property.toUpperCase().equals("EQREDUCTION"))
			this.eqreduction = Boolean.parseBoolean(value);
		else
			throw new Exception("wrong property " + property);
	}

	/**
	 * @throws IOException
	 * @throws WrongFormatException
	 * @throws FileNotFoundException
	 *
	 */
	private void processNames(String namespaceFile)
	    throws FileNotFoundException, WrongFormatException, IOException {
		if (this.files.contains(namespaceFile)) {
			System.out.println(namespaceFile + " used twice");
			return;
		}
		if (this.namespace == null)
			this.namespace = new Namespace(namespaceFile, this.verbose);
		else
			this.namespace.readNamespaces(namespaceFile);
	}

	/**
	 *
	 */
	private void processTuples(String tupleFile) {
		if (this.files.contains(tupleFile)) {
			System.out.println(tupleFile + " used twice");
			return;
		}
		if (this.tupleStore == null) {
			this.tupleStore = new TupleStore(this.verbose, this.rdfcheck, this.eqreduction,
																			 this.minnoofargs, this.maxnoofargs,
																			 this.noofatoms, this.nooftuples,
																			 this.namespace, tupleFile);
			this.query = new Query(this.tupleStore);
		}
		else
			this.tupleStore.readTuples(tupleFile);
	}

	/**
	 *
	 */
	private void processRules(String ruleFile) {
		if (this.files.contains(ruleFile)) {
			System.out.println(ruleFile + " used twice");
			return;
		}
		if (this.ruleStore == null) {
			this.ruleStore = new RuleStore(this.verbose, this.rdfcheck,
																		 this.minnoofargs, this.maxnoofargs,
																		 this.namespace, this.tupleStore, ruleFile);
			this.forwardChainer = new ForwardChainer(this.noofcores, this.verbose,
																							 this.noofatoms, this.nooftuples,
																							 this.namespace, this.tupleStore, this.ruleStore);
		}
		else
			this.forwardChainer.uploadRules(ruleFile);  // use uploadRules() to guarantee right number of tasks
	}

	/**
	 * FOR TEST PUPOSES ONLY
	 */
	public static void main(String[] args) {
		Configuration config = new Configuration(args[0]);
	}

}
