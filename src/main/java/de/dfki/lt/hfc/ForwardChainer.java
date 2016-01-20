package de.dfki.lt.hfc;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;
import gnu.trove.*;

/**
 * generates a tuple store, a rule store, and a namespace object in order to
 * compute the deductive closure (the fixpoint) of the derivation relation '->'
 * (and '=>', later!) w.r.t. a set of rules and a set of tuples;
 *
 * NOTE: depending on
 *         @see TupleStore.equivalenceClassReduction
 *       and
 *         @see ForwardChainer.cleanUpRepository
 *       it might be the case that the closure computation needs to be called
 *       again, since the cleanup phase performed afterwards could lead to the
 *       possibility that passive rules become active again !!
 * 
 * @author (C) Hans-Ulrich Krieger
 * @since JDK 1.5
 * @version Thu Jan  7 17:41:55 CET 2016
 */
public final class ForwardChainer {

	/**
	 * HFC version number string
	 */
	public static final String VERSION = "6.1.13";
	
	/**
	 * HFC info string
	 */
	public static final String INFO = "v" + ForwardChainer.VERSION + " (Thu Jan  7 17:41:55 CET 2016)";
	
	/**
	 * a pointer to the tuple store for this forward chainer
	 */
	public TupleStore tupleStore;
	
	/**
	 * a pointer to the rule store for this forward chainer
	 */
	public RuleStore ruleStore;
	
	/**
	 * the namespace object, whose namespaces are used in rules and tuples
	 */
	public Namespace namespace;
	
	/**
	 * default settings that might speed up the forward chainer (actually the tuple store)
	 */
	protected int noOfAtoms = 100000;
	
	/**
	 * default settings that might speed up the forward chainer (actually the tuple store)
	 */
	protected int noOfTuples = 500000;
	
	/**
	 * the default hashing and equals strategy for tuples from the rule output set of the current
	 * iteration: take ALL positions of a tuple into account
	 */
	protected static TIntArrayHashingStrategy DEFAULT_HASHING_STRATEGY = new TIntArrayHashingStrategy();
	
	/**
	 * tells the forward chainer how many top-level loops should be performed
	 * during the computation of the deductive closure;
	 * Integer.MAX_VALUE indicates that the fixpoint iteration is not restricted by
	 * any number
	 */
	public int noOfIterations = Integer.MAX_VALUE;
	
	/**
	 * this Boolean flag is only considered in the closure computation, when the
	 * equivalence class reduction is turned on in the tuple store
	 * @see TupleStore.equivalenceClassReduction
	 * @see computeClosure()
	 */
	public boolean cleanUpRepository = true;
	
	/**
	 * generation counter is incremented during each iteration, independent of how
	 * many times computeClosure() is called;
	 * thus a new call to computeClosure() does NOT reset the counter to 0 (zero),
	 * but instead increments it further by 1;
	 * is used during local clause querying
	 */
	protected int generationCounter = 0;
	
	/**
	 * a constant that controls whether a warning is printed in case an invalid
	 * tuple is read in;
	 * a similar variable exists in class RuleStore
	 * @see #exitOnError
	 */
	public boolean verbose = true;
	
	/**
	 * a constant that controls whether the system is terminated in case an invalid
	 * tuple is read in (exit code = 1);
	 * a similar variable exists in class RuleStore
	 * @see #verbose
	 */
	public boolean exitOnError = false;
	
	/**
	 * call the garbage collector after each iteration step
	 */
	public boolean gc = false;
	
	/**
	 * specifies the number of parallel executed rule threads;
	 * do not specify a number larger than the sum of all cores of your computer
	 */
	public int noOfCores = 4;
	
	/**
	 * specifies the number of tasks (= #rules) that are executed within a single
	 * iteration; will be assigned a value when constructor is executed
	 */
	private int noOfTasks;
	
	/**
	 * container to gather the rule threads
	 */
	private ExecutorService threadPool;
	
	/**
	 * needed to to start a new iteration
	 */
	private CountDownLatch doneSignal;
	
	/**
	 * used to generate unique blank node names for _this_ forward chainer
	 */
	private final String blankNodePrefix = "_:" + this.toString();
	
	/**
	 * used to generate unique blank node names
	 */
	private int blankCounter = 0;
	
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * initialization code of nullary and binary constructors that is outsourced to avoid
	 * code reduplication
	 */
	private void init() {
		if (this.verbose) {
			System.out.println();
			System.out.println("  Welcome to HFC, HUK's Forward Chainer");
			System.out.println("  " + ForwardChainer.INFO);
			System.out.println("  # CPU cores: " + this.noOfCores);
			System.out.println("  " + this.toString());
		}
	}
	 	
	/**
	 * prints a welcome message to standard out
	 */
	private ForwardChainer() {
		init();
	}
	
	/**
	 * used by copyForwardChainer()
	 */
	private ForwardChainer(int noOfCores, boolean verbose) {
		this.noOfCores = noOfCores;
		this.verbose = verbose;
		init();
	}	
	
	/**
	 * generates a new forward chainer with the default namespace for XSD, RDF, RDFS, and OWL
	 */
	public ForwardChainer(String tupleFile, String ruleFile) {
		this();
		this.namespace = new Namespace();		
		this.tupleStore = new TupleStore(this.noOfAtoms, this.noOfTuples, this.namespace, tupleFile);
		this.ruleStore = new RuleStore(this.namespace, this.tupleStore, ruleFile);
		this.threadPool = Executors.newFixedThreadPool(this.noOfCores);
		this.noOfTasks = this.ruleStore.allRules.size();
	}
	
	/**
	 * this version allows to explicitly define the namespace
	 */
	public ForwardChainer(String tupleFile, String ruleFile,	String namespaceFile) {
		this();
		this.namespace = new Namespace(namespaceFile);		
		this.tupleStore = new TupleStore(this.noOfAtoms, this.noOfTuples, this.namespace, tupleFile);
		this.ruleStore = new RuleStore(this.namespace, this.tupleStore, ruleFile);
		this.threadPool = Executors.newFixedThreadPool(this.noOfCores);
		this.noOfTasks = this.ruleStore.allRules.size();
	}
	
	/**
	 * generates a new forward chainer with the default namespace for XSD, RDF, RDFS, and OWL;
	 * noOfAtoms and noOfTuples are important parameters that affects the performance of the
	 * tuple store used by the forward chainer
	 */
	public ForwardChainer(int noOfAtoms, int noOfTuples, String tupleFile, String ruleFile) {
		this();
		this.noOfAtoms = noOfAtoms;
	  this.noOfTuples = noOfTuples;
		this.namespace = new Namespace();		
		this.tupleStore = new TupleStore(this.noOfAtoms, this.noOfTuples, this.namespace, tupleFile);
		this.ruleStore = new RuleStore(this.namespace, this.tupleStore, ruleFile);
		this.threadPool = Executors.newFixedThreadPool(this.noOfCores);
		this.noOfTasks = this.ruleStore.allRules.size();
	}
	
	/**
	 * this version allows to explicitly define the namespace
	 */
	public ForwardChainer(int noOfAtoms, int noOfTuples, String tupleFile, String ruleFile, String namespaceFile) {
		this();
		this.noOfAtoms = noOfAtoms;
	  this.noOfTuples = noOfTuples;																											
		this.namespace = new Namespace(namespaceFile);		
		this.tupleStore = new TupleStore(this.noOfAtoms, this.noOfTuples, this.namespace, tupleFile);
		this.ruleStore = new RuleStore(this.namespace, this.tupleStore, ruleFile);
		this.threadPool = Executors.newFixedThreadPool(this.noOfCores);
		this.noOfTasks = this.ruleStore.allRules.size();
	}
	
	
	/**
	 *  assumes a default of 100,000 atoms and 500,000 tuples
	 */
	public ForwardChainer(Namespace namespace, TupleStore tupleStore, RuleStore ruleStore) {
		this();
		this.noOfAtoms = 100000;
	  this.noOfTuples = 500000;																											
		this.namespace = namespace;		
		this.tupleStore = tupleStore;
		this.ruleStore = ruleStore;
		this.threadPool = Executors.newFixedThreadPool(this.noOfCores);
		this.noOfTasks = this.ruleStore.allRules.size();
	}
	
	/**
	 * more options that will also affect namespace, tuple store, and rule store
	 */
	public ForwardChainer(int noOfCores,
												boolean verbose,
												boolean rdfCheck,
												boolean eqReduction,
												int minNoOfArgs,
												int maxNoOfArgs,
												int noOfAtoms,
												int noOfTuples,
												String tupleFile,
												String ruleFile,
												String namespaceFile) {
		this(noOfCores, verbose);
		this.noOfAtoms = noOfAtoms;
	  this.noOfTuples = noOfTuples;																											
		this.namespace = new Namespace(namespaceFile, verbose);
		this.tupleStore = new TupleStore(verbose, rdfCheck, eqReduction, minNoOfArgs, maxNoOfArgs,
																		 this.noOfAtoms, this.noOfTuples, this.namespace, tupleFile);
		this.ruleStore = new RuleStore(verbose, rdfCheck, minNoOfArgs, maxNoOfArgs,
																	 this.namespace, this.tupleStore, ruleFile);
		this.threadPool = Executors.newFixedThreadPool(this.noOfCores);
		this.noOfTasks = this.ruleStore.allRules.size();
	}
	
	/**
	 * slightly less options as before, but namespace, tuple store, and rule store have already
	 * been created
	 */
	public ForwardChainer(int noOfCores,
												boolean verbose,
												int noOfAtoms,
												int noOfTuples,
												Namespace namespace,
												TupleStore tupleStore,
												RuleStore ruleStore) {
		this(noOfCores, verbose);
		this.noOfAtoms = noOfAtoms;
	  this.noOfTuples = noOfTuples;																											
		this.namespace = namespace;
		this.tupleStore = tupleStore;
		this.ruleStore = ruleStore;
		this.threadPool = Executors.newFixedThreadPool(this.noOfCores);
		this.noOfTasks = this.ruleStore.allRules.size();
	}
	
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * to (dynamically) change the number of processor cores at runtime, use this method
	 */
	public void setNoOfCores(int noOfCores) {
		this.noOfCores = noOfCores;
		this.threadPool = Executors.newFixedThreadPool(this.noOfCores);
	}

	/**
	 * generates a new unique blank node id (an int);
	 * used during forward chaining when unbounded right-hand side variables are introduced;
	 * it is important that the method is synchronized to exclusively lock the blank counter;
	 * @see de.dfki.lt.hfc.ForwardChainer.blankCounter
	 */
	public int nextBlankNode () {
		synchronized (this.tupleStore) {
			return this.tupleStore.putObject(this.blankNodePrefix + this.blankCounter++);
		}
	}
	
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * performs a local match over the LHS clauses;
	 * the result of structurally equivalent clauses is shared between clauses, even
	 * across rules
	 */
	private void executeLocalMatch(Rule rule) {
		//System.out.print(rule.name + " ");
		//System.out.print(rule.properVariables);
		//System.out.print(rule.dontCareVariables);
		//System.out.println(rule.blankNodeVariables);
		Set<int[]> query;
		Table table;
		for (int i = 0; i < rule.ante.length; i++) {
			// first of all, check whether an equivalent clause already exist
			table = rule.localQueryBindings[i];
			// !!! obtain a _lock_ on the table's proxy (since this is shared, and so its content) !!!
			synchronized (table.proxy) {
				if (table.getGeneration() == this.generationCounter) {
					//System.out.print("-");
					continue;
				}
				// not so: query index, assign table/delta the right values, increment counter
				query = this.tupleStore.queryIndex(rule.ante[i], rule.localQueryBindings[i]);
				// null indicates an empty result set, thus cancel rule execution!
				if (query.isEmpty()) {
					//System.out.println("X");
					// no need to check every clause of a rule during global matching
					rule.isApplicable = false;
					return;
				}
				//System.out.print("+");
				// instead of using the "relevant positions" of a clause, we can now use the
				// "proper positions", since duplicate variables have already been checked;
				// the right strategy can be obtained by calling proxy's getStrategy() method
				// NOTE: do NOT destructively modify query, since otherwise the index is modified
				table.setDelta(Calc.difference(query, table.getTable(),
																			 table.getStrategy()));  // = proper positions
				table.setOld(table.getTable());  // old and delta is used in complexJoin()
				table.setTable(query);
				table.setGeneration(this.generationCounter);
				//System.out.println(rule.name + "(" + table + "): " + table.getDelta().size() + " " + table.getOld().size());
			}
		}
		// LOCAL matching does NOT rule out this rule even if there are empty deltas
		rule.isApplicable = true;
		//System.out.println();
	}
	
	/**
	 * performs a global matching over the LHS clauses;
	 * local tables within the LHS of this rule are joined in case they share variables;
	 * good example to see complex interactions:
	 *   $owl_typeBySomeVal
   *  	 ?q <rdf:type> ?c
	 *     ?r <owl:onProperty> ?p
	 *     ?r <owl:someValuesFrom> ?c
	 *     ?i ?p ?q
	 *   ->
	 *     ?i <rdf:type> ?r
	 * instead of
	 *   (0 J 2) J (1 J 2)
	 * avoid second "inner" join:
	 *   (0 J 2) J 1
	 * this is achieved by computing "clusters" of LHS clauses
	 * @see de.dfki.lt.hfc.RuleStore#computeAmalgamation
	 */
	private void executeGlobalMatch(Rule rule) {
		// one LHS clause
		if (rule.ante.length == 1) {
			// nothing to do -- only delta (the new information) needs to be considered
			Table table = rule.localQueryBindings[0];
			// for a single-claused LHS rule, an empty delta means that THIS rule is not applicable
			if (table.getDelta().isEmpty())
				rule.isApplicable = false;
			else {
				rule.isApplicable = true;
				// a special case needs to be considered here: antecendents with a single clause PLUS
				// in-eqs (or predicates) MUST copy their delta info, since Calc.restrict() works destructively
				// on the binding table of the cluster!
				// NOTE: in case predicates are implemented, this needs to be checked also here !!! (done!)
				if (rule.inEqConstraints.isEmpty() && rule.tests.isEmpty())
					// no need to shallow copy delta information
					rule.clusters[0].bindingTable = new BindingTable(table.getDelta(), table.nameToPosProper);
				else
					// shallow copy delta
					rule.clusters[0].bindingTable =
					new BindingTable(new THashSet<int[]>(table.getDelta(), table.getStrategy()),
													 table.nameToPosProper);
			}
		}
		// more than one LHS clause
		else {
			BindingTable result;
			for (Cluster cluster : rule.clusters) {
				// guarantee that we have at least new info for one of the positions in the cluster
				result = complexJoin(rule.localQueryBindings,
														 cluster.positions,
														 0,
														 false,
														 new ArrayList<BindingTable>(),
														 new HashMap<Pair, BindingTable>());
				if (result.table.isEmpty()) {
					// if at least one cluster yields an empty binding table, the whole LHS is not satisfiable
					rule.isApplicable = false;
					return;
				}
				else {
					rule.isApplicable = true;
					cluster.bindingTable = result;
				}
			}
		}
	}
	
	/**
	 * does the recursive job for executeGlobalMatch();
	 * recursively takes the union of the continuation of complexJoin() for both delta and old at
	 * a specific index for a given cluster;
	 * at least one of the tables must be _new_ and of course not empty (as is the case for old
	 * information)
	 */
	private BindingTable complexJoin(Table[] locbind,
																	 ArrayList<Integer> cluster,
																	 int index,
																	 boolean newInfo,
																	 ArrayList<BindingTable> joinit,
																	 HashMap<Pair, BindingTable> memo) {
		if (index == cluster.size()) {
			if (! newInfo)
				// no new info -- no need to do some potential expensive joins
				return new BindingTable(new THashSet<int[]>(), null);
			// joinit is at least of length 1
			Pair pair;
			BindingTable memoResult;
			BindingTable result = joinit.get(0);
			// memoization will work here because join() does NOT modify the input tables
			for (int i = 1; i < joinit.size(); i++) {
				pair = new Pair(result, joinit.get(i));
				memoResult = memo.get(pair);
				if (memoResult == null) {
					result = Calc.join(result, joinit.get(i));
					memo.put(pair, result);
				}
				else
					result = memoResult;
			}
			return result;
		}
		// take the union of the continuation of complexJoin() for both delta and old at index
		else {
			Table current = locbind[cluster.get(index)];
			BindingTable delta;
			if (current.getDelta().isEmpty())
				delta = new BindingTable(new THashSet<int[]>(), null);
			else {
				// add current's non-empty delta to the continuation
				ArrayList<BindingTable> deltaJoinit = new ArrayList<BindingTable>(joinit);
				deltaJoinit.add(new BindingTable(current.getDelta(), current.nameToPosProper));
				delta = complexJoin(locbind, cluster, index + 1, newInfo || true, deltaJoinit, memo);
				//                                               true, of course
			}
			BindingTable old;
			if (current.getOld().isEmpty())
				old = new BindingTable(new THashSet<int[]>(), null);
			else {
				// add current's non-empty old to the continuation
				ArrayList<BindingTable> oldJoinit = new ArrayList<BindingTable>(joinit);
				oldJoinit.add(new BindingTable(current.getOld(), current.nameToPosProper));
				old = complexJoin(locbind, cluster, index + 1, newInfo || false, oldJoinit, memo);
				//                                             newInfo, of course
			}
			// take the union of delta and old
			return new BindingTable(Calc.union(delta.table, old.table),
															delta.nameToPos != null ? delta.nameToPos : old.nameToPos);
		}
	}
	
	/**
	 * in order to speed up rule execution, it is important to check whether the two
	 * variables in an in-eq constraint belong to a same rule cluster from the rule's
	 * cluster list;
	 * if so, the corresponding binding table can be "reduced" using the in-eq constraints;
	 * if not, the in-eq need to be considered later before/during RHS instantiation;
	 * examples
	 *   + 1 LHS clause cluster:  ?x p ?y, ?y p ?z, ?x != ?y, ?y != ?z -> ?x p ?z
	 *     do NOT iterate _twice_ over the binding table to check both in-eqs
	 *   + 2 LHS clause clusters: ?u p ?v, ?x q ?y, ?u != ?y -> ?u r ?y
	 *     in-eq applies to RHS, but RHS in principle requires Cartesian product of
	 *     the two independent LHS clusters
	 *   + 2 LHS clause clusters: ?u p ?v, ?x q ?y, ?u != ?x, ?v != ?y -> ?u r ?y
	 *     both in-eqs can not be applied directly to LHS clusters, nor RHS, but
	 *     in-eqs rule out certain combinations for (?u, ?y) that cooccur with
	 *     (?u, ?x) and (?v, ?y)
	 *   + in-eqs with URIs/XSD atoms: ?s ?p ?o, ?p != <rdf:type> -> .....
	 */
	private void applyTests(Rule rule) {
		for (Cluster cluster : rule.clusters) {
			// _destructively_ restricts binding table using cluster's ineqs and tests
			//System.out.println(rule.name + ": " + cluster.bindingTable.table.size());
			cluster.bindingTable = Calc.restrict(cluster.bindingTable, cluster.varvarIneqs, cluster.varconstIneqs);
			cluster.bindingTable = Calc.restrict(cluster.bindingTable, cluster.tests);
			//System.out.println(rule.name + ": " + cluster.bindingTable.table.size());
			// one empty cluster suffices to let the rule fail overall (no RHS instantiations possible)
			if (cluster.bindingTable.table.isEmpty())
				rule.isApplicable = false;
			else
				rule.isApplicable = true;
		}
	}
		
	/**
	 * computes delta, new, old, etc. for each independent LHS cluster (information
	 * stored in the cluster object);
	 * then (AT THE MOMENT) merges the LHS clusters using Cartesian Product, even if
	 * RHS pattern (or in-eqs/test) would _not_ enforce this;
	 * NOTE: standard OWL Horst rules come up with only one LHS cluster, so that
	 *       Cartesian Product need not be carried out !!
	 */
	private void prepareInstantiation(Rule rule) {
		// check whether we deal with only one or several clusters
		if (rule.clusters.length == 1) {
			// only one cluster, i.e., no remaining in-eqs to be considered
			rule.megaCluster = rule.clusters[0];  // copy in case ... see comment above
		}
		else {
			for (Cluster cluster : rule.clusters) {
				// even if a (LHS) cluster's delta is empty, the RHS _might_ produce new tuples,
				// assuming there is another cluster whose delta isn't empty and the RHS combines
				// old info from first cluster with new info from second cluster;
				// this is checked during RHS instantiation
				cluster.delta = Calc.difference(cluster.bindingTable.table, cluster.table);
				cluster.old = cluster.table;  // old and delta is used in construction of RHS tables
				cluster.table = cluster.bindingTable.table;
				//System.out.println(rule.name + "(delta/old): " + cluster.delta.size() + " " + cluster.old.size());
			}
			// more than one independent LHS cluster: Cartesian Product (at the moment!)
			rule.megaCluster.bindingTable = complexProduct(rule.clusters, 0, false, new ArrayList<BindingTable>());
			// destructively apply *remaining* in-eqs and tests to mega cluster, using Calc.restrict()
			rule.megaCluster.bindingTable = Calc.restrict(rule.megaCluster.bindingTable, rule.megaCluster.varvarIneqs, rule.megaCluster.varconstIneqs);
			rule.megaCluster.bindingTable = Calc.restrict(rule.megaCluster.bindingTable, rule.megaCluster.tests);
			// check whether result is empty (assign rule.isApplicable appropriate value)
			if (rule.megaCluster.bindingTable.table.isEmpty()) {
				rule.isApplicable = false;
				return;
			}
		}
		// now that we have one mega-cluster for LHS, we can project result table, using RHS variables
		SortedMap<Integer, Integer> nameToPos = rule.megaCluster.bindingTable.nameToPos;
		Set<Integer> mcvars = nameToPos.keySet();  // proper LHS vars w/o DC vars
		Set<Integer> rhsvars = rule.rhsVariables;  // proper RHS vars w/o BN vars
		// check whether RHS vars are a subset of the mega cluster vars; if so, call project()
		if (mcvars.size() != rhsvars.size()) {  // testing for the size of both sets suffices
			// in order to call project, I need an ascending int[] of positions
			int[] pos = new int[rhsvars.size()];
			int i = 0;
			for (Integer e : rhsvars)
				pos[i++] = nameToPos.get(e).intValue();
			Arrays.sort(pos);
			// reduce table using project()
			rule.megaCluster.bindingTable.table = Calc.project(rule.megaCluster.bindingTable.table, pos);
			// NOTE: project() might lead to surprising (but correct) results in case LHS vars are all don't
			//       care vars and a blank node var is used on the RHS
		}
		// compute delta, old, table as usual for mega cluster and check whether delta is empty
		rule.megaCluster.delta = Calc.difference(rule.megaCluster.bindingTable.table, rule.megaCluster.table);
		rule.megaCluster.old = rule.megaCluster.table;  // old and delta is used in construction of RHS tables
		rule.megaCluster.table = rule.megaCluster.bindingTable.table;
		// if there is no _new_ LHS-matched and RHS-projected information, no need for instantiation
		if (rule.megaCluster.delta.isEmpty())
			rule.isApplicable = false;
		else
			rule.isApplicable = true;
	}

	/**
	 * does the recursive job for prepareInstantiation();
	 * recursively takes the union of the continuation of complexProduct() for both delta and old
	 * of each cluster;
	 * at least one of the tables must be new and of course not empty (as is the case for old
	 * information) -- this strategy is called "semi-naive evaluation" in logic programming
	 */
	private BindingTable complexProduct(Cluster[] clusters,
																			int index,
																			boolean newInfo,
																			ArrayList<BindingTable> productit) {
		if (index == clusters.length) {
			if (! newInfo)
				return new BindingTable(new THashSet<int[]>(), null);
			// productit is at least of length 1
			BindingTable result = productit.get(0);
			for (int i = 1; i < productit.size(); i++)
				result = Calc.product(result, productit.get(i));
			return result;
		}
		// take the union of the continuation of complexProduct() for both delta and old at index
		else {
			Cluster current = clusters[index];
			BindingTable delta;
			if (current.delta.size() == 0)
				delta = new BindingTable(new THashSet<int[]>(), null);
			else {
				// add current's non-empty delta to the continuation
				ArrayList<BindingTable> deltaProductit = new ArrayList<BindingTable>(productit);
				deltaProductit.add(new BindingTable(current.delta, current.bindingTable.nameToPos));
				delta = complexProduct(clusters, index + 1, newInfo || true, deltaProductit);
				//                                          true, of course
			}
			BindingTable old;
			if (current.old.size() == 0)
				old = new BindingTable(new THashSet<int[]>(), null);
			else {
				// add current's non-empty old to the continuation
				ArrayList<BindingTable> oldProductit = new ArrayList<BindingTable>(productit);
				oldProductit.add(new BindingTable(current.old, current.bindingTable.nameToPos));
				old = complexProduct(clusters, index + 1, newInfo || false, oldProductit);
				//                                        newInfo, of course
			}
			// take the union of delta and old
			return new BindingTable(Calc.union(delta.table, old.table),
															delta.nameToPos != null ? delta.nameToPos : old.nameToPos);
		}
	}
	
	/**
	 * the input table for RHS instantiation has already been projected to the RHS variables
	 * @see #prepareInstantiation
	 */
	protected void performInstantiation(Rule rule) {
		// this version implements RHS instantiation via a top loop over the table of the mega cluster;
		// an alternative version would implement table, delta, and old for each RHS clause;
		// not sure whether this would pay off, since LHS matching is much more expensive than RHS instantiation
		Set<int[]> table = rule.megaCluster.delta;
		SortedMap<Integer, Integer> nameToPos = rule.megaCluster.bindingTable.nameToPos;
		Set<Integer> rhsvars = rule.rhsVariables;  // proper RHS vars w/o BN vars
		Set<Integer> bnvars = rule.blankNodeVariables;
		if (this.verbose) {
			synchronized (System.out) {
				System.out.println("  " + rule.name + ": " + rule.megaCluster.old.size() +
													 " " + rule.megaCluster.delta.size());
			}
		}
		// use a mediator (array) instead of using the slower map; take care of _blank_node_ vars;
		ArrayList<Integer> allrhsvars = new ArrayList<Integer>(rhsvars);
		allrhsvars.addAll(bnvars);
		// adjust array using _all_ RHS vars incl. BN vars; position 0 is unused
		int[] mediator = new int[-Collections.min(allrhsvars).intValue() + 1];
		// initialize mediator with invalid position -1
		for (int i = 0; i < mediator.length; i++)
			mediator[i] = -1;
		// and overwrite the valid (i.e., non BN) positions
		for (Integer var : rhsvars)
			mediator[-var.intValue()] = nameToPos.get(var);
		// also use a mediator for the true BN vars and binder vars
		int[] bnmediator = null;
		TIntHashSet binderVars = null;
		TIntObjectHashMap<Function> varToFunct = null;
		// only compute once
		boolean hasBnVars = (bnvars.size() > 0);
		if (hasBnVars) {
			bnmediator = new int[mediator.length];
			binderVars = new TIntHashSet();                  // might be empty
			varToFunct = new TIntObjectHashMap<Function>();  // ditto
			for (Function function : rule.actions) {
				binderVars.add(function.result);
				varToFunct.put(function.result, function);
			}
		}
		// and finally start RHS instantiation
		int[] newTuple, input;
		Function function;
		for (int[] binding : table) {
			if (hasBnVars) {
				// always reset bnmediator for each rule application
				for (int i = 0; i < bnmediator.length; i++)
					bnmediator[i] = -1;  // -1 indicates that a value has not been assigned yet
			}
			for (int[] clause : rule.cons) {
				newTuple = new int[clause.length];
				for (int i = 0; i < clause.length; i++) {
					// a constant, i.e., an URI, blank node, or XSD atom
					if (clause[i] > 0) {
						newTuple[i] = clause[i];
					}
					// a BLANK NODE var
					else if (mediator[-clause[i]] == -1) {
						// first occurrence?
						if (bnmediator[-clause[i]] == -1) {
							// now check whether it is a binder var or a true BN var
							if (binderVars.contains(clause[i])) {
								function = varToFunct.get(clause[i]);
								input = new int[function.args.length];
								for (int j = 0; j < function.args.length; j++) {
									input[j] = function.args[j] > 0 ? function.args[j] : binding[mediator[-function.args[j]]];
								}
								bnmediator[-clause[i]] =
								this.tupleStore.operatorRegistry.evaluate(function.name, OperatorRegistry.OPERATOR_PATH, input);
							}
							else {
								bnmediator[-clause[i]] = nextBlankNode();
							}
						}
						newTuple[i] = bnmediator[-clause[i]];
					}
					// an ordinary var
					else {
						newTuple[i] = binding[mediator[-clause[i]]];
					}
				}
				rule.output.add(newTuple);
			}
		}
	}	
		
	/**
	 * performs the matching and instantiation for each rule;
	 * keeps track of local information in order to speed up rule execution;
	 * writes generated tuples to local output field of the rule
	 */
	protected void execute(Rule rule) {
		try {
			// reuse rule's output field; make it empty, even for rules that are switched off
			rule.output.clear();
			// do not execute rules which have a priority less or equal 0
			if (rule.priority <= 0) {
				if (this.verbose)
					synchronized (System.out) {
						System.out.println("  " + rule.name + ": off");
					}
				return;
			}
			// query the index for each LHS clause
			executeLocalMatch(rule);
			// and check whether rule is applicable on local grounds
			if (! rule.isApplicable) {
				if (this.verbose)
					synchronized (System.out) {
						System.out.println("  " + rule.name + ": local");
					}
				return;
			}
			// same for the whole antecedent
			executeGlobalMatch(rule);
			if (! rule.isApplicable) {
				if (this.verbose)
					synchronized (System.out) {
						System.out.println("  " + rule.name + ": global");
					}
				return;
			}
			// apply in-eqs if in-eq vars belong to the same cluster
			applyTests(rule);
			if (! rule.isApplicable) {
				if (this.verbose)
					synchronized (System.out) {
						System.out.println("  " + rule.name + ": tests");
					}
				return;
			}
			// compute "deltas" over LHS clusters
			prepareInstantiation(rule);
			if (! rule.isApplicable) {
				if (this.verbose)
					synchronized (System.out) {
						System.out.println("  " + rule.name + ": cluster");
					}
				return;
			}
			// start instantiation phase
			performInstantiation(rule);
		}
		finally {
			// only at the _very end_ decrease count down latch (cf. return statements above)
			this.doneSignal.countDown();
		}
	}
	
	/**
	 * assigns each rule execution a single task which is added to the thread pool
	 */
	private void executeAllRules() {
		Runnable task;
		for (final Rule rule : this.ruleStore.allRules) {
			task = new Runnable() { public void run() { execute(rule); } };
			this.threadPool.submit(task);
		}
	}

	/**
	 * computes the deductive closure given an initial set of tuples as
	 * specified by TupleStore.allTuples;
	 * iterates until a fixpoint is computed or a predefined iteration depth
	 * has been reached as given by the argument
	 * @return true iff new tuples have been generated
	 * @return false, otherwise
	 */
	public boolean computeClosure(int noOfIterations, boolean cleanUpRepository) {
		int noOfAllTuples = this.tupleStore.allTuples.size();
		if (this.verbose)
			System.out.println("\n  number of all tuples: " + noOfAllTuples + "\n");
		int currentIteration = 0;
		long time = System.currentTimeMillis();
		long fullTime = time;
		boolean newInfo = false;
		int noOfNewTuples = 0;
		if (this.verbose)
			System.out.println("  rule name: old new OR failure stage");
		// increment generation counter for deletion here AND also at the very end
		boolean notContained;
		++this.tupleStore.generation;
		do {
			// increment global generation counter (is never reset)
			++this.generationCounter;
			// increment number of local iterations wrt. computeClosure()
			++currentIteration;
			if (this.verbose)
				System.out.println("  " + currentIteration);
			// execute all rules (quasi) in parallel, taking advantage of multi-core CPUs
			this.doneSignal = new CountDownLatch(this.noOfTasks);  // = #rules
			try {
				executeAllRules();
				this.doneSignal.await();  // wait for all tasks to finish
			}
			catch (InterruptedException ie) {
				System.out.println(ie);
			}
			// add rule-generated tuples to set of all tuples and update the index
			newInfo = false;
			noOfNewTuples = 0;
			for (Rule rule : this.ruleStore.allRules) {
				for (int[] tuple : rule.output) {
					notContained = this.tupleStore.addTuple(tuple);
					newInfo = notContained || newInfo;
					++noOfNewTuples;
				}
			}
			if (this.verbose) {
				System.out.println("  " + noOfNewTuples + "/" + this.tupleStore.allTuples.size() +
													 " (" + (System.currentTimeMillis() - time) + "msec)");
				time = System.currentTimeMillis();
			}
			// perhaps trigger a GC after each iteration step
			if (this.gc)
				System.gc();
		} while (newInfo && (currentIteration != noOfIterations));
		// increment the generation  counter for deltion again for further upload
		++this.tupleStore.generation;
		// some statistics
		if (this.verbose) {
			System.out.println("\n  number of all tuples: " + this.tupleStore.allTuples.size());
			System.out.println("  " + (this.tupleStore.allTuples.size() - noOfAllTuples) +
												 " tuples generated");
			System.out.println("  closure computation took " + (System.currentTimeMillis() - fullTime) + "msec");
		}
		// possibly cleanup
		if (this.tupleStore.equivalenceClassReduction && this.cleanUpRepository) {
			System.out.print("\n  cleaning up repository ... ");
			this.tupleStore.cleanUpTupleStore();
			System.out.println("done");
			System.out.println("  number of all tuples: " + this.tupleStore.allTuples.size());
		}
		// and finally the `answer'
		if ((noOfAllTuples - this.tupleStore.allTuples.size()) == 0)
			return false;
		else
			return true;
	}

	/**
	 * the number of iterations is given by global this.noOfIterations;
	 * whether the repository is cleaned up is determined by ForwardChainer.cleanUpRepository
	 * and TupleStore.equivalenceClassReduction
	 */
	public boolean computeClosure() {
		return computeClosure(this.noOfIterations, this.cleanUpRepository);
	}
	
	/**
	 * calls computeClosure(int noOfIterations) again, assuming that a set of
	 * new tuples has been added to the tuple store;
	 * @return true iff new tuples have been generated
	 * @return false, otherwise
	 */
	public boolean computeClosure(Set<int[]> newTuples, int noOfIterations, boolean cleanUpRepository) {
		// note: A \subseteq T <==> A \setminus T = \emptyset
		// use this to avoid subset test, union, and difference operations
		newTuples.removeAll(this.tupleStore.allTuples);  // is a destructive operation
		// is newTuples a subset of allTuples
		if (newTuples.isEmpty()) {
			if (this.verbose)
				System.out.println("\n  no tuples generated");
			return false;
		}
		for (int[] tuple : newTuples) {
			this.tupleStore.addTuple(tuple);
		}
		// and finally call nullary computeClosure()
		return computeClosure(noOfIterations, cleanUpRepository);
	}
	
	/**
	 * the number of iterations is given by field noOfIterations
	 */
	public boolean computeClosure(Set<int[]> newTuples) {
		return computeClosure(newTuples, this.noOfIterations, this.cleanUpRepository);
	}
	
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * uploads further namespaces stored in a file to an already established forward chainer;
	 * this method directly calls readNamespaces() from class Namespace
	 * @see Namespace.readNamespaces()
	 */
	public void uploadNamespaces(String filename) {
		this.namespace.readNamespaces(filename);
	}
	
	
	/**
	 * uploads further tuples stored in a file to an already established forward chainer;
	 * this method directly calls readTuples() from class TupleStore;
	 * if tuple deletion is enabled in the forward chainer, the tuples from the file are
	 * assigned the actual generation TupleStore.generation
	 * @see TupleStore.readTuples()
	 */
	public void uploadTuples(String filename) {
		this.tupleStore.readTuples(filename);
	}
	
	/**
	 * add tuples, represented as int[], to the set of all tuples;
	 * if tuple deletion is enabled in the forward chainer, the tuples from the set are
	 * assigned the actual generation TupleStore.generation
	 * @see TupleStore.addTuple()
	 */
	public void addTuples(Collection<int[]> tuples) {
		for (int[] tuple : tuples)
			this.tupleStore.addTuple(tuple);
	}
	
	/**
	 * remove tuples, represented as int[], from the set of all tuples;
	 * if tuple deletion is enabled in the forward chainer, the tuples from the set are
	 * removed from TupleStore.generation
	 * @see TupleStore.addTuple()
	 */
	public void removeTuples(Collection<int[]> tuples) {
		for (int[] tuple : tuples)
			this.tupleStore.removeTuple(tuple);
	}
	
	/**
	 * uploads further rules stored in a file to an already established forward chainer;
	 * the set of all rules is returned;
	 * NOTE: a similar method readRules() is defined in class RuleStore;
	 *       however, uploadRules set the field noOfTasks in ForwardChainer to the proper value
	 * @see RuleStore.readRules()
	 */
	public void uploadRules(String filename) {
		this.ruleStore.lineNo = 0;
		this.ruleStore.readRules(filename);
		// update noOfTask in order to guarantee proper rule execution in a multi-threaded environment
		this.noOfTasks = this.ruleStore.allRules.size();
	}
	
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * shutdowns the thread pool and exits with value 0
	 */
	public void shutdown() {
		this.threadPool.shutdown();
		if (this.verbose) {
			System.out.println("\n  shutting down thread pool ...");
			System.out.println("  exiting ...\n");
		}
		System.exit(0);
	}
	
	/**
	 * only shutdowns the thread pool, but no System.exit() is called;
	 * used by the XMLRPC server
	 */
	public void shutdownNoExit() {
		this.threadPool.shutdown();
		if (this.verbose) {
			System.out.println("  shutting down thread pool ...");
		}
	}
	
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * if 0, auxiliary structures in forward chainer are not compressed/deleted;
	 * if 1, the old/new separation for clause proxies, clusters, and mega
	 *       clusters is deleted;
	 * if 2, the index structure is deleted;
	 * if 3, old/new structures are deleted AND the index structure is deleted
	 */
	public void compress(int level) {
		switch (level) {
			case 3:
				deleteOldNew();
				deleteIndex();
				break;
			case 2:
				deleteIndex();
				break;
			case 1:
				deleteOldNew();
				break;
			default:
				break;
		}
	}
	
	/**
	 *
	 */
	private void deleteOldNew() {
		// do not call clear() since it does NOT frees the memory (only clears the sets)
		// delete clause-level info shared between rules
		for (Proxy proxy : this.ruleStore.equivalentClauses.values()) {
			proxy.table = new THashSet<int[]>();
			proxy.delta = null;
			proxy.old = null;
		}
		// delete local rule-level cluster/mega-cluster info + output field
		for (Rule rule : this.ruleStore.allRules) {
			rule.output = new THashSet<int[]>(ForwardChainer.DEFAULT_HASHING_STRATEGY);
			for (Cluster cluster : rule.clusters) {
				cluster.table = new THashSet<int[]>();
				cluster.delta = null;
				cluster.old = null;
			}
			rule.megaCluster.table = new THashSet<int[]>();
			rule.megaCluster.delta = null;
			rule.megaCluster.old = null;
		}
	}	
	
	/**
	 *
	 */
	private void deleteIndex() {
		Map<Integer, Set<int[]>>[] index = this.tupleStore.index;
		for (int i = 0; i < index.length; i++)
			// do not call clear() since it does NOT frees the memory (only clears the mappings)
			index[i] = new HashMap<Integer, Set<int[]>>();
	}
	
	/**
	 *
	 */
	public void uncompressIndex() {
		for (int[] tuple : this.tupleStore.allTuples)
			this.tupleStore.addToIndex(tuple);
	}
	
	/**
	 * returns a copy of the forward chainer that can be used to generate "choice points"
	 * during reasoning;
	 * tuples are taken over, but nearly everything else is copied
	 * @param noOfCores an integer, specifying how many parallel threads are used during
	 *        the computation of the deductive closure for the copy of this forward chainer
	 * @see de.dfki.lt.hfc.TupleStore.copyTupleStore()
	 * @see de.dfki.lt.hfc.TupleStore.copyRuleStore()
	 */
	public ForwardChainer copyForwardChainer(int noOfCores, boolean verbose) {
		ForwardChainer copy = new ForwardChainer(noOfCores, verbose);
		// take over namespace object and generation counter
		copy.namespace = this.namespace;
		copy.generationCounter = this.generationCounter;
		// copy tuple store and rule store
		copy.tupleStore = this.tupleStore.copyTupleStore(copy.namespace);
		copy.ruleStore = this.ruleStore.copyRuleStore(copy.namespace, copy.tupleStore);
		// ***WARNING***: do not let work this and copy in PARALLEL !!!!!
		// reuse thread pool of this object
		copy.threadPool = this.threadPool;
		copy.noOfTasks = copy.ruleStore.allRules.size();
		// take over blankCounter, although not necessary
		copy.blankCounter = this.blankCounter;
		// copy over information related to deletion of tuples (independent of whether enabled or disabled)
		copy.tupleStore.generation = this.tupleStore.generation;
		if (tupleDeletionEnabled())
			copy.tupleStore.tupleToGeneration =
			  new THashMap<int[], Integer>(this.tupleStore.tupleToGeneration, ForwardChainer.DEFAULT_HASHING_STRATEGY);
		else
			copy.tupleStore.tupleToGeneration = null;
		// finished!
		return copy;
	}
	
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * @return true iff tuple deletion has been enabled by method enableTupleDeletion()
	 * @return false otherwise
	 *
	 * there is a similar method in class TupleStore
	 * @see TupleStore.tupleDeletionEnabled()
	 */
	public boolean tupleDeletionEnabled() {
		return (this.tupleStore.tupleToGeneration != null);
	}	
	
	/**
	 * enableTupleDeletion() makes use of a special field that is initialized by an empty map
	 * from tuples (int[]) to generations (Integer);
	 * this field is localized in class TupleStore, since it interacts with the equivalence
	 * class reduction mechanism that is also located in TupleStore;
	 * @see TupleStore.tupleToGeneration
	 *
	 * this special case of tuple deletion differs from the functionality in class TupleStore
	 * which only deletes tuples given to method removeTuple(), in that _entailed_ tuples t'
	 * which potentially depend on the deleted tuple t (gen(t') > gen(t)) must also be deleted
	 * AND closure computation needs to be called again
	 *
	 * IMPORTANT:
	 *   use this and only this method to enable tuple deletion in the forward chainer;
	 *
	 * it is IMPORTANT that this method is DIRECTLY called AFTER an instance of ForwardChainer
	 * has been created and before the first closure computation is called;
	 * if it would be called some time later AFTER at least one closure computation has been
	 * called, i.e., if (TupleStore.generation > 0), the method will do NOTHING (and returns
	 * false);
	 * when this method returns true, it is guaranteed that all tuples from the tuple store
	 * are assigned generation 0
	 *
	 * @return true iff tuple deletion has been successfully started
	 * @return false otherwise, i.e., tuple deletion has been made ready TOO LATE = no effect
	 */
	public boolean enableTupleDeletion() {
		if (this.tupleStore.generation == 0) {
			// at the moment, NO closure has been computed
			if (this.tupleStore.tupleToGeneration == null)
				this.tupleStore.tupleToGeneration = new THashMap<int[], Integer>(ForwardChainer.DEFAULT_HASHING_STRATEGY);
			for (int[] tuple : this.tupleStore.allTuples)
				this.tupleStore.tupleToGeneration.put(tuple, 0);
			if (this.verbose)
				System.out.println("  tuple deletion enabled");
			return true;
		}
		else {
			if (this.verbose)
				System.out.println("  tuple deletion can no longer be enabled, since closure computation was already called");
			return false;
		}
	}
	
	/**
	 * deletes a tuple and _potentially_ dependent entailed tuples of specific generations from the
	 * tuples store, followed by a new closure computation;
	 * note that we need to check whether equivalence class reduction has been turned on; if so, the
	 * tuple needs to be replaced by its proxy!
	 *
	 * this strategy implements a compromise between deleting everything and setting up a full
	 * TMS-like structure (that would furthermore make speed-up techniques in HFC obsolete);
	 *
	 * use deleteTuples() below if you want to delete more than one tuple, since deleteTuples()
	 * calls closure computation only once at the very end of the deletion process, whereas this method
	 * calls it for the tuple bound to parameter tuple
	 *
	 * note: this method obtains a lock of this.tupleStore
	 *
	 * @return true iff tuple was successfully deleted, i.e., was an element of the set of all tuples
	 * @return false otherwise
	 */
	public final boolean deleteTuple(int[] tuple) {
		synchronized (this.tupleStore) {
			// use the proxy of tuple elements in case equivalence class reduction has been enabled
			if (this.tupleStore.equivalenceClassReduction) {
				int[] newTuple = new int[tuple.length];
				for (int i = 0; i < tuple.length; i++)
					newTuple[i] = this.tupleStore.getProxy(tuple[i]);
				tuple = newTuple;
			}
			// obtain generation: no need to call containsKey()
			Integer tgen = this.tupleStore.tupleToGeneration.get(tuple);
			// tuple NOT contained in tuple store
			if (tgen == null)
				return false;
			// check whether tuple is an entailed tuple, i.e., it will be introduced later again during
			// closure computation, thus no need to go further here
			if ((tgen % 2) != 0)
				return false;
			if (this.verbose)
				System.out.print("\n  falling back to generation " + tgen + " ... ");
			// remove tuple from the tuple store, also removes tuple-to-generation mapping, if enabled
			this.tupleStore.removeTuple(tuple);
			// remove potentially dependent materialized tuples: first, determine the relevant tuples
			// (side note: seems that iterator plus remove() is not allowed for int[])
			Integer egen;
			final ArrayList<int[]> toBeDeleted = new ArrayList<int[]>();
			// this computation could in principle be speeded up in case we would have a generation-to-tuple mapping
			for (int[] element : this.tupleStore.allTuples) {
				egen = this.tupleStore.tupleToGeneration.get(element);
				// only delete entailed tuples with a greater generation
				if (((egen % 2) != 0) && (egen > tgen))
					toBeDeleted.add(element);
			}
			// then remove those tuples
			for (int[] element : toBeDeleted) {
				this.tupleStore.removeTuple(element);
			}
			// delete old vs. new separation for proxies, clusters, and mega-clusters, and do not modify them
			// separately (too complex and expensive): reduces to compress level = 1
			compress(1);
			// and finally call closure computation again, even for only this single tuple
			if (this.verbose) {
				System.out.println("done");
				System.out.println("  calling closure computation again ...");
			}
			computeClosure();
			// check whether tuple is still in the set of all tuples, i.e., someone tried to delete an
			// entailed tuple which will NOT work (since it is reintroduced through closure computation!
			return this.tupleStore.allTuples.contains(tuple);
		}
	}
	
	/**
	 * use this method if you want to delete SEVERAL tuples at once, since both this and the above method
	 * always call computeClosure() at the very end (expensive!), independent of the number of tuples deleted
	 *
	 * note: this method obtains a lock of this.tupleStore
	 *
	 * @return true iff each tuple from tuples was successfully deleted (OR tuples is the empty collection)
	 * @return false otherwise, i.e., EITHER at least one tuple from tuples was not an element of the set
	 *         of all tuples OR the tuple under deletion is an entailed tuple that is later introduced
	 *         again as the result of calling the closure computation OR at least one tuple is not contained
	 *         in the set of all tuples
	 */
	public final boolean deleteTuples(Collection<int[]> tuples) {
		synchronized (this.tupleStore) {
			// empty collection: return true (like an empty conjunction)
			if (tuples.isEmpty())
				return true;
			// replace URIs by their proxies in tuples in case equivalence class reduction has been enabled
			if (this.tupleStore.equivalenceClassReduction) {
				Collection<int[]> newTuples = new ArrayList<int[]>(tuples.size());
				int[] newTuple;
				for (int[] tuple : tuples) {				
					newTuple = new int[tuple.length];
					for (int i = 0; i < tuple.length; i++)
						newTuple[i] = this.tupleStore.getProxy(tuple[i]);
					newTuples.add(newTuple);				
				}
				tuples = newTuples;
			}
			// obtain _lowest_ generation number: minimum computation
			int lowest = Integer.MAX_VALUE;  // highly unlikely to be found in the set of all tuples
			Integer tgen;
			final ArrayList<int[]> toBeDeleted = new ArrayList<int[]>();
			for (int[] tuple : tuples) {
				tgen = this.tupleStore.tupleToGeneration.get(tuple);
				// only take uploaded (not entailed) tuples into account
				if ((tgen != null) && ((tgen % 2) == 0)) {  // nice unboxing :*)
					toBeDeleted.add(tuple);
					if (tgen < lowest)
						lowest = tgen;
				}
			}
			// none of the tuples are contained in the set of all tuples
			if (lowest == Integer.MAX_VALUE)
				return false;
			if (this.verbose)
				System.out.print("\n  falling back to generation " + lowest + " ... ");
			// return value = true iff card(tuples) == card(toBeDeleted)
			boolean result = (tuples.size() == toBeDeleted.size());
			// delete the uploaded tuples from tuples
			for (int[] tuple : toBeDeleted) {
				this.tupleStore.removeTuple(tuple);
			}
			// delete the potentially relevant entailed tuples
			Integer egen;
			toBeDeleted.clear();  // reuse toBeDeleted
			for (int[] element : this.tupleStore.allTuples) {
				egen = this.tupleStore.tupleToGeneration.get(element);
				if (((egen % 2) != 0) && (egen > lowest))
					toBeDeleted.add(element);
			}
			for (int[] element : toBeDeleted) {
				this.tupleStore.removeTuple(element);
			}
			compress(1);
			if (this.verbose) {
				System.out.println("done");
				System.out.println("  calling closure computation again ...");
			}
			computeClosure();
			return result;		
		}
	}
	
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//
	// NOTE: the four transactions below require that tuple deletion has been enabled in the forward chainer
	// @see ForwardChainer.enableTupleDeletion()
	
	/**
	 * transaction 1: addTuplesToRepository()
	 * adds a collection of tuples to the repository;
	 * this quasi-synchronized method obtains a lock on this.tupleStore;
	 * note that the generation counter from TupleStore is incremented by 2 _before_
	 * the tuples are added in order to distinguish the tuples envolved in this transactions
	 * from `ordinary' tuples that are `only' uploaded;
	 * note further that this transaction does NOT compute the deductive closure of the repository
	 * @return true iff the transaction was successful
	 * @return false iff an error happened during the transaction or in case tuple deletion
	 *         has NOT been enabled;
	 *         note that we gurantee that the effects which have happended during the
	 *         transaction are invalidated
	 */
	public final boolean addTuplesToRepository(Collection<int[]> tuples) {
		if (tupleDeletionEnabled()) {
			synchronized (this.tupleStore) {
				// remember what has been added in case things go wrong
				final ArrayList<int[]> added = new ArrayList<int[]>();
				try {
					// each `positive' transaction increments the generation counter;
					// since NO closure computation is involved, it must be incremented by 2
					this.tupleStore.generation = this.tupleStore.generation + 2;
					for (int[] tuple : tuples) {
						this.tupleStore.addTuple(tuple);
						added.add(tuple);
					}
					return true;
				}
				catch (Exception e) {				
					System.err.println(e);
					// if something went wrong during the transaction, iterate over the remembered tuples and
					// redo the insertions, not necessarily _all_ tuples
					for (int[] tuple : added)
						this.tupleStore.removeTuple(tuple);
					this.tupleStore.generation = this.tupleStore.generation - 2;
					return false;
				}
			}
		}
		// tuple deletion NOT enabled
		else
			return false;
	}
	
	/**
	 * transaction 2: removeTuplesFromRepository()
	 * removes a collection of tuples from the repository;
	 * note that the entailed tuples are NOT deleted, only the specified tuples;
	 * in case closure computation is never called, i.e., the repository is only used for
	 * querying the explicit uploaded information, use this method instead of nethod below;
	 * this quasi-synchronized method obtains a lock on this.tupleStore
	 * @see ForwardChainer.deleteTuplesFromRepository()
	 * @return true iff the transaction was successful
	 * @return false iff an error happened during the transaction or in case tuple deletion
	 *         has NOT been enabled;
	 *         note that we gurantee that the effects which have happended during the
	 *         transaction are invalidated
	 */
	public final boolean removeTuplesFromRepository(Collection<int[]> tuples) {
		if (tupleDeletionEnabled()) {
			synchronized (this.tupleStore) {
				final Hashtable<int[], Integer> tuple2generation = new Hashtable<int[], Integer>();
				try {
					int generation;
					// remove tuples from repository, but remember their generation
					for (int[] tuple : tuples) {
						generation = this.tupleStore.removeTupleReturnGeneration(tuple);
						// only record tuples that were part of the repository
						if (generation != -1)
							tuple2generation.put(tuple, generation);
					}
					return true;
				}
				catch (Exception e) {				
					System.err.println(e);
					// if something went wrong during transaction, iterate over removed tuples
					// and undo the deletions
					for (Map.Entry<int[], Integer> entry : tuple2generation.entrySet())
						this.tupleStore.addTupleWithGeneration(entry.getKey(), entry.getValue());
					return false;
				}
			}
		}
		// tuple deletion NOT enabled
		else
			return false;
	}
	
	/**
	 * this private method differs from deleteTuples() above in that it destructively modifies
	 * the tuple-to-be-deleted-TO-generation mapping as given by parameter mapping and is
	 * exclusively used by deleteFromRepository()
	 */
	private final boolean deleteTuplesRecordGenerations(Collection<int[]> tuples,
																											Hashtable<int[], Integer> mapping) {
		// empty collection: return true (like an empty conjunction)
		if (tuples.isEmpty())
			return true;
		// replace URIs by their proxies in tuples in case equivalence class reduction has been enabled
		if (this.tupleStore.equivalenceClassReduction) {
			Collection<int[]> newTuples = new ArrayList<int[]>(tuples.size());
			int[] newTuple;
			for (int[] tuple : tuples) {				
				newTuple = new int[tuple.length];
				for (int i = 0; i < tuple.length; i++)
					newTuple[i] = this.tupleStore.getProxy(tuple[i]);
				newTuples.add(newTuple);				
			}
			tuples = newTuples;
		}
		// obtain _lowest_ generation number: minimum computation
		int lowest = Integer.MAX_VALUE;  // highly unlikely generation to be found in the set of all tuples
		Integer tgen;
		final ArrayList<int[]> toBeDeleted = new ArrayList<int[]>();
		for (int[] tuple : tuples) {
			tgen = this.tupleStore.tupleToGeneration.get(tuple);
			// only take uploaded (not entailed) tuples into account at this point
			if ((tgen != null) && ((tgen % 2) == 0)) {  // nice unboxing :*)
				toBeDeleted.add(tuple);
				if (tgen < lowest)
					lowest = tgen;
			}
		}
		// none of the tuples are contained in the set of all tuples
		if (lowest == Integer.MAX_VALUE)
			return false;
		if (this.verbose)
			System.out.print("\n  falling back to generation " + lowest + " ... ");
		// return value = true iff card(tuples) == card(toBeDeleted)
		boolean result = (tuples.size() == toBeDeleted.size());
		// delete the uploaded tuples from tuples and record their generation
		int generation;
		for (int[] tuple : toBeDeleted) {
			generation = this.tupleStore.removeTupleReturnGeneration(tuple);
			if (generation != -1)
				mapping.put(tuple, generation);
		}
		// delete the potentially relevant entailed tuples
		Integer egen;
		toBeDeleted.clear();  // reuse toBeDeleted
		for (int[] element : this.tupleStore.allTuples) {
			egen = this.tupleStore.tupleToGeneration.get(element);
			if (((egen % 2) != 0) && (egen > lowest))
				toBeDeleted.add(element);
		}
		for (int[] element : toBeDeleted) {
			generation = this.tupleStore.removeTupleReturnGeneration(element);
			if (generation != -1)
				mapping.put(element, generation);
		}
		// throw away auxiliary data structures and do NOT individually remove tuples
		compress(1);
		if (this.verbose) {
			System.out.println("done");
			System.out.println("  calling closure computation again ...");
		}
		// call closure computation again to reestablish the entailed tuples and auxiliary structures
		computeClosure();
		return result;		
	}
	
	/**
	 * transaction 3: deleteTuplesFromRepository()
	 * deletes a collection of tuples from the repository; not only the direct tuples
	 * are deleted, but also the entailed tuples which solely depend on the deleted
	 * tuples;
	 * this quasi-synchronized method obtains a lock on this.tupleStore
	 * @see ForwardChainer.removeTuplesFromRepository()
	 * @return true iff the transaction was successful
	 * @return false iff an error appeared during the transaction or in case tuple deletion
	 *         has NOT been enabled;
	 *         note that we gurantee that the effects which have happended during the
	 *         transaction are invalidated
	 */
	public final boolean deleteTuplesFromRepository(Collection<int[]> tuples) {
		if (tupleDeletionEnabled()) {
			synchronized (this.tupleStore) {
				final Hashtable<int[], Integer> tuple2generation = new Hashtable<int[], Integer>();
				final int oldGeneration = this.tupleStore.generation;
				try {
					// update tuple2generation from inside deleteTuplesRecordGenerations()
					deleteTuplesRecordGenerations(tuples, tuple2generation);
					return true;
				}
				catch (Exception e) {				
					System.err.println(e);
					// if something went wrong during transaction, iterate over removed tuples
					// and undo the deletions
					this.tupleStore.generation = oldGeneration;
					for (Map.Entry<int[], Integer> entry : tuple2generation.entrySet())
						this.tupleStore.addTupleWithGeneration(entry.getKey(), entry.getValue());
					return false;
				}
			}
		}
		// tuple deletion NOT enabled
		else
			return false;
	}
	
	
	/**
	 * transaction 4: computeClosureFromRepository()
	 * a `nullary' transaction that computes the deductive closure for the repository;
	 * this quasi-synchronized method obtains a lock on this.tupleStore
	 * @see ForwardChainer.removeFromRepository()
	 * @return true iff the transaction was successful
	 * @return false iff an error appeared during the transaction;
	 *         note that we gurantee that the effects which have happended during the
	 *         transaction are invalidated
	 */
	public final boolean computeClosureFromRepository() {
		if (tupleDeletionEnabled()) {
			synchronized (this.tupleStore) {
				final int deleteThatGeneration = this.tupleStore.generation + 1;
				try {
					computeClosure();
					return true;
				}
				catch (Exception e) {				
					System.err.println(e);
					// undo the effects of the closure computation
					for (int[] tuple : this.tupleStore.allTuples) {
						if (this.tupleStore.tupleToGeneration.get(tuple) == deleteThatGeneration)
							// remove the ENTAILED tuple (ODD generation number)
							this.tupleStore.removeTuple(tuple);
					}
					this.tupleStore.generation = deleteThatGeneration - 1;
					return false;
				}
			}
		}
		// tuple deletion NOT enabled
		else
			return false;
	}
	
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
	 * !!! FOR TEST PURPOSES ONLY !!!
	 * !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
	 */
	public static void main(String[] args) throws Exception {
		// for testing the -server option of the JVM:
		//   time java -server -cp .:../lib/trove-2.1.0.jar -Xms800m -Xmx1200m de/dfki/lt/hfc/ForwardChainer
		/*
		 // call THIS with equivalence class reduction DISABLED
		 ForwardChainer fc =	new ForwardChainer(100000, 500000,
																					 "/Users/krieger/Desktop/Java/HFC/hfc/src/resources/default.nt",
																					 "/Users/krieger/Desktop/Java/HFC/hfc/src/resources/default.rdl",
																					 "/Users/krieger/Desktop/Java/HFC/hfc/src/resources/default.ns");
		fc.uploadTuples("/Users/krieger/Desktop/Java/HFC/hfc/src/resources/ltworld.jena.nt");
		fc.computeClosure();
		fc.shutdown();
		*/
		
		// call THIS with equivalence class reduction ENABLED
		
		ForwardChainer fc =	new ForwardChainer(100000, 500000,
																					 "/Users/krieger/Desktop/Java/HFC/hfc/src/resources/default.eqred.nt",
																					 "/Users/krieger/Desktop/Java/HFC/hfc/src/resources/default.eqred.rdl",
																					 "/Users/krieger/Desktop/Java/HFC/hfc/src/resources/default.ns");
		fc.uploadTuples("/Users/krieger/Desktop/Java/HFC/hfc/src/resources/ltworld.jena.nt");
		fc.computeClosure();
		fc.computeClosure();
		fc.shutdown();
		
		
		/*
		ForwardChainer fc =	new ForwardChainer(100000, 500000,
																					 "/Users/krieger/Desktop/Java/HFC/hfc/src/resources/tests/RelationalVariable/relvar.nt",
																					 "/Users/krieger/Desktop/Java/HFC/hfc/src/resources/tests/RelationalVariable/relvar.rdl",
																					 "/Users/krieger/Desktop/Java/HFC/hfc/src/resources/tests/RelationalVariable/relvar.ns");
		fc.computeClosure();
		Query q = new Query(fc.tupleStore);
		BindingTable bt = q.query("select ?s where ?s <rdf:type> <owl:Nothing>");
		System.out.println(bt.toString());
		fc.shutdown();
		*/
		
		/*
		ForwardChainer fc =	new ForwardChainer(100000, 500000,
																					 "/Users/krieger/Desktop/MONNET/server/resources/tuples/0default.eqred.nt",
																					 "/Users/krieger/Desktop/MONNET/server/resources/rules/0default.time.datetime.eqred.quintuple.rdl",
																					 "/Users/krieger/Desktop/MONNET/server/resources/namespaces/0default.ns");
		fc.uploadNamespaces("/Users/krieger/Desktop/MONNET/server/resources/namespaces/custom.ns");
		fc.uploadRules("/Users/krieger/Desktop/MONNET/server/resources/rules/custom.rdl");
		fc.uploadTuples("/Users/krieger/Desktop/MONNET/server/resources/tuples/dax.nt");
		fc.uploadTuples("/Users/krieger/Desktop/MONNET/server/resources/tuples/nace.nt");
		fc.uploadTuples("/Users/krieger/Desktop/MONNET/server/resources/tuples/dc.nt");
		fc.enableTupleDeletion();
		System.out.println("********* GENERATION 0 *********");
		fc.computeClosure();
		fc.uploadTuples("/Users/krieger/Desktop/MONNET/server/resources/tuples/company.2011-07-01.nt");
		System.out.println("********* GENERATION 2 *********");
		fc.computeClosure();
		fc.uploadTuples("/Users/krieger/Desktop/MONNET/server/resources/tuples/company.2011-07-08.nt");
		System.out.println("********* GENERATION 4 *********");
		fc.computeClosure();
		fc.uploadTuples("/Users/krieger/Desktop/MONNET/server/resources/tuples/company.2011-08-02.nt");
		System.out.println("********* GENERATION 6 *********");
		fc.computeClosure();
		fc.uploadTuples("/Users/krieger/Desktop/MONNET/server/resources/tuples/company.2011-08-22.nt");
		System.out.println("********* GENERATION 8 *********");
		fc.computeClosure();
		fc.uploadTuples("/Users/krieger/Desktop/MONNET/server/resources/tuples/company.2011-08-30.nt");
		System.out.println("********* GENERATION 10 *********");
		fc.computeClosure();
		Integer gennum;
		// delete 3 tuples from generation 6 (file company.2011-08-02.nt)
		ArrayList<int[]> toBeDeleted = new ArrayList<int[]>();
		toBeDeleted.add(fc.tupleStore.internalizeTuple(new String[]{"<dax:DE0005190003_1312280572342>", "<dax:transparencyStandard>", "<dax:primeStandard>", "\"2011-08-02T12:22:52\"^^<xsd:dateTime>", "\"2011-08-02T12:22:52\"^^<xsd:dateTime>"}));
		toBeDeleted.add(fc.tupleStore.internalizeTuple(new String[]{"<dax:DE0005190003_1312280572342>", "<dax:sector>", "\"DAXsector All Automobile (Performance)\"@de ", "\"2011-08-02T12:22:52\"^^<xsd:dateTime>", "\"2011-08-02T12:22:52\"^^<xsd:dateTime>"}));
		toBeDeleted.add(fc.tupleStore.internalizeTuple(new String[]{"<dax:Norbert_Reithofer_1312280569356>", "<dax:lastName>", "\"Reithofer\"^^<xsd:string>", "\"2011-08-02T12:22:52\"^^<xsd:dateTime>", "\"2011-08-02T12:22:52\"^^<xsd:dateTime>"}));
		fc.deleteTuples(toBeDeleted);
		// NOTE (very important):
		// even though deleteTuples() calls computeClosure(), if equivalence class reduction is turned on
		// and a cleanup has been performed, FURTHER closure computations might be necessary, since cleanups
		// are performed after a closure computation which potentially make passive rules active again;
		// @see de.dfki.lt.hfc.server.HfcServer() how this is supposed to be properly handled !!
		fc.computeClosure();
		Query q = new Query(fc.tupleStore);
		BindingTable bt = q.query("select * where <dax:Igor_Landau_1310132885939> <dax:worksFor> ?c ?s ?e");
		System.out.println(bt.toString());
	  fc.shutdown();
		*/
		 
		/*
		Query q = new Query(fc.tupleStore);
		BindingTable bt = q.query("SELECT DISTINCT * WHERE ?s <owl:sameAs> <http://www.lt-world.org/ltw.owl#lt-world_Individual_293>");
		//BindingTable bt = q.query("SELECT DISTINCT * WHERE ?s ?p ?o");
		//System.out.println(bt.table.size());
		//bt.expandBindingTable();
		//System.out.println(bt.table.size());
		System.out.println(bt.toString(false));
		System.out.println(bt.toString(true));
		fc.shutdown();
		*/
		//fc.compress(3);
		//fc.uncompressIndex();
		//fc.computeClosure();
		
		// use integrity constraints at the very end ...
		//fc.uploadRules("/Users/krieger/Desktop/Java/HFC/hfc/resources/idefault.rdl");
		// ... and start closure computation again
		//fc.computeClosure();
		//fc.shutdown();
		
		
		//fc.uploadTuples("/Users/krieger/Desktop/Java/HFC/hfc/resources/furniture2/furniture2.rdf.nt");
		//fc.computeClosure();
		//fc.shutdown();
		/*
		ForwardChainer fc =	new ForwardChainer(100000, 500000,
																					 "/Users/krieger/Desktop/Java/HFC/hfc/resources/default.nt",
																					 "/Users/krieger/Desktop/Java/HFC/hfc/resources/default.rdl",
																					 "/Users/krieger/Desktop/Java/HFC/hfc/resources/default.ns");
		fc.uploadTuples("/Users/krieger/Desktop/Java/HFC/hfc/resources/furniture2/furniture2.rdf.nt");
		fc.uploadTuples("/Users/krieger/Desktop/Java/HFC/hfc/resources/gossip-nt/ReligionType.nt");
		fc.uploadTuples("/Users/krieger/Desktop/Java/HFC/hfc/resources/gossip-nt/actedIn.nt");
		fc.uploadTuples("/Users/krieger/Desktop/Java/HFC/hfc/resources/gossip-nt/bornIn.nt");
		fc.uploadTuples("/Users/krieger/Desktop/Java/HFC/hfc/resources/gossip-nt/bornOnDate.nt");
		fc.uploadTuples("/Users/krieger/Desktop/Java/HFC/hfc/resources/gossip-nt/causeOfDeath.nt");
		fc.uploadTuples("/Users/krieger/Desktop/Java/HFC/hfc/resources/gossip-nt/created.nt");
		fc.uploadTuples("/Users/krieger/Desktop/Java/HFC/hfc/resources/gossip-nt/diedIn.nt");
		fc.uploadTuples("/Users/krieger/Desktop/Java/HFC/hfc/resources/gossip-nt/diedOnDate.nt");
		fc.uploadTuples("/Users/krieger/Desktop/Java/HFC/hfc/resources/gossip-nt/directed.nt");
		fc.uploadTuples("/Users/krieger/Desktop/Java/HFC/hfc/resources/gossip-nt/discovered.nt");
		fc.uploadTuples("/Users/krieger/Desktop/Java/HFC/hfc/resources/gossip-nt/gossip.nt");
		fc.uploadTuples("/Users/krieger/Desktop/Java/HFC/hfc/resources/gossip-nt/graduatedFrom.nt");
		fc.uploadTuples("/Users/krieger/Desktop/Java/HFC/hfc/resources/gossip-nt/hasAcademicAdvisor.nt");
		fc.uploadTuples("/Users/krieger/Desktop/Java/HFC/hfc/resources/gossip-nt/hasAlbum.nt");
		fc.uploadTuples("/Users/krieger/Desktop/Java/HFC/hfc/resources/gossip-nt/hasBoyfriend.nt");
		fc.uploadTuples("/Users/krieger/Desktop/Java/HFC/hfc/resources/gossip-nt/hasBrother.nt");
		fc.uploadTuples("/Users/krieger/Desktop/Java/HFC/hfc/resources/gossip-nt/hasChild.nt");
		fc.uploadTuples("/Users/krieger/Desktop/Java/HFC/hfc/resources/gossip-nt/hasDaughter.nt");
		fc.uploadTuples("/Users/krieger/Desktop/Java/HFC/hfc/resources/gossip-nt/hasFamilyName.nt");
		fc.uploadTuples("/Users/krieger/Desktop/Java/HFC/hfc/resources/gossip-nt/hasFather.nt");
		fc.uploadTuples("/Users/krieger/Desktop/Java/HFC/hfc/resources/gossip-nt/hasFullName.nt");
		fc.uploadTuples("/Users/krieger/Desktop/Java/HFC/hfc/resources/gossip-nt/hasGender.nt");
		fc.uploadTuples("/Users/krieger/Desktop/Java/HFC/hfc/resources/gossip-nt/hasGirlfriend.nt");
		fc.uploadTuples("/Users/krieger/Desktop/Java/HFC/hfc/resources/gossip-nt/hasGivenName.nt");
		fc.uploadTuples("/Users/krieger/Desktop/Java/HFC/hfc/resources/gossip-nt/hasHusband.nt");
		fc.uploadTuples("/Users/krieger/Desktop/Java/HFC/hfc/resources/gossip-nt/hasImdbId.nt");
		fc.uploadTuples("/Users/krieger/Desktop/Java/HFC/hfc/resources/gossip-nt/hasImdbPage.nt");
		fc.uploadTuples("/Users/krieger/Desktop/Java/HFC/hfc/resources/gossip-nt/hasMember.nt");
		fc.uploadTuples("/Users/krieger/Desktop/Java/HFC/hfc/resources/gossip-nt/hasMother.nt");
		fc.uploadTuples("/Users/krieger/Desktop/Java/HFC/hfc/resources/gossip-nt/hasName.nt");
		fc.uploadTuples("/Users/krieger/Desktop/Java/HFC/hfc/resources/gossip-nt/hasNationality.nt");
		fc.uploadTuples("/Users/krieger/Desktop/Java/HFC/hfc/resources/gossip-nt/hasParent.nt");
		fc.uploadTuples("/Users/krieger/Desktop/Java/HFC/hfc/resources/gossip-nt/hasPartner.nt");
		fc.uploadTuples("/Users/krieger/Desktop/Java/HFC/hfc/resources/gossip-nt/hasPartyAffiliation.nt");
		fc.uploadTuples("/Users/krieger/Desktop/Java/HFC/hfc/resources/gossip-nt/hasProfession.nt");
		fc.uploadTuples("/Users/krieger/Desktop/Java/HFC/hfc/resources/gossip-nt/hasReligion.nt");
		fc.uploadTuples("/Users/krieger/Desktop/Java/HFC/hfc/resources/gossip-nt/hasRemain.nt");
		fc.uploadTuples("/Users/krieger/Desktop/Java/HFC/hfc/resources/gossip-nt/hasSexualOrientation.nt");
		fc.uploadTuples("/Users/krieger/Desktop/Java/HFC/hfc/resources/gossip-nt/hasSibling.nt");
		fc.uploadTuples("/Users/krieger/Desktop/Java/HFC/hfc/resources/gossip-nt/hasSister.nt");
		fc.uploadTuples("/Users/krieger/Desktop/Java/HFC/hfc/resources/gossip-nt/hasSon.nt");
		fc.uploadTuples("/Users/krieger/Desktop/Java/HFC/hfc/resources/gossip-nt/hasSpouse.nt");
		fc.uploadTuples("/Users/krieger/Desktop/Java/HFC/hfc/resources/gossip-nt/hasWebPage.nt");
		fc.uploadTuples("/Users/krieger/Desktop/Java/HFC/hfc/resources/gossip-nt/hasWife.nt");
		fc.uploadTuples("/Users/krieger/Desktop/Java/HFC/hfc/resources/gossip-nt/hasWikipediaPage.nt");
		fc.uploadTuples("/Users/krieger/Desktop/Java/HFC/hfc/resources/gossip-nt/hasWonPrize.nt");
		fc.uploadTuples("/Users/krieger/Desktop/Java/HFC/hfc/resources/gossip-nt/influences.nt");
		fc.uploadTuples("/Users/krieger/Desktop/Java/HFC/hfc/resources/gossip-nt/interestedIn.nt");
		fc.uploadTuples("/Users/krieger/Desktop/Java/HFC/hfc/resources/gossip-nt/isCitizenOf.nt");
		fc.uploadTuples("/Users/krieger/Desktop/Java/HFC/hfc/resources/gossip-nt/isMemberOf.nt");
		fc.uploadTuples("/Users/krieger/Desktop/Java/HFC/hfc/resources/gossip-nt/locatedIn.nt");
		fc.uploadTuples("/Users/krieger/Desktop/Java/HFC/hfc/resources/gossip-nt/madeCoverFor.nt");
		fc.uploadTuples("/Users/krieger/Desktop/Java/HFC/hfc/resources/gossip-nt/originatedFrom.nt");
		fc.uploadTuples("/Users/krieger/Desktop/Java/HFC/hfc/resources/gossip-nt/participatedIn.nt");
		fc.uploadTuples("/Users/krieger/Desktop/Java/HFC/hfc/resources/gossip-nt/produced.nt");
		fc.uploadTuples("/Users/krieger/Desktop/Java/HFC/hfc/resources/gossip-nt/type.nt");
		fc.uploadTuples("/Users/krieger/Desktop/Java/HFC/hfc/resources/gossip-nt/worksAt.nt");
		fc.uploadTuples("/Users/krieger/Desktop/Java/HFC/hfc/resources/gossip-nt/wrote.nt");
		
		fc.computeClosure();
		fc.shutdown();
		*/
	
	}
	
}
