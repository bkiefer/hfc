package de.dfki.lt.hfc;

import gnu.trove.map.hash.TCustomHashMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import gnu.trove.set.hash.TCustomHashSet;
import gnu.trove.set.hash.THashSet;
import gnu.trove.set.hash.TIntHashSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * generates a tuple store, a rule store, and a namespace object in order to
 * compute the deductive closure (the fixpoint) of the derivation relation '->'
 * w.r.t. a set of rules and a set of tuples;
 * <p>
 * NOTE: depending on
 *
 * @author (C) Hans-Ulrich Krieger
 * @version Fri Jul  8 15:36:17 CEST 2016
 * @author Christian Willms
 * @version Fri Apr 5 10:15:14 CEST 2019
 * it might be the case that the closure computation needs to be called
 * again, since the cleanup phase performed afterwards could lead to the
 * possibility that passive rules become active again !!
 * @since JDK 1.5
 */
public final class ForwardChainer {



  /**
   * A basic LOGGER.
   */
  private static final Logger logger = LoggerFactory.getLogger(ForwardChainer.class);
  /**
   * the default hashing and equals strategy for tuples from the rule output set of the current
   * iteration: take ALL positions of a tuple into account
   */
  protected static TIntArrayHashingStrategy DEFAULT_HASHING_STRATEGY = new TIntArrayHashingStrategy();
  private final TupleStore _tupleStore;
  
  
  private final RuleStore _ruleStore;
    private final Config _config;
    //private  int _noOfIterations;
  //private  Integer _noOfCores;
  //private  boolean _gc;


  /**
   * a constant that controls whether a warning is printed in case an invalid
   * tuple is read in;
   * a similar variable exists in class RuleStore
   */
  //public boolean verbose;

  /**
   * generation counter is incremented during each iteration, independent of how
   * many times computeClosure() is called;
   * thus a new call to computeClosure() does NOT reset the counter to 0 (zero),
   * but instead increments it further by 1;
   * is used during local clause querying
   */
  protected int generationCounter = 0;
  /**
   * specifies the number of tasks (= #rules) that are executed within a single
   * iteration; will be assigned a value when constructor is executed
   */
  protected int noOfTasks;
  /**
   * container to gather the rule threads
   */
  private ExecutorService threadPool;
  /**
   * needed to to start a new iteration
   */
  private CountDownLatch doneSignal;
  //private boolean _cleanUpRepository;
  //private boolean _eqReduction;


  /**
   * used to generate unique blank node names for _this_ forward chainer
   */
  private final String _blankNodePrefix = "_:" + this.toString();

  /**
   * used to generate unique blank node names
   */
  private int _blankCounter = 0;



  /////////////////////////////////////////////////////////////////////////////////////////////////////////////

  
  public ForwardChainer(TupleStore ts, RuleStore rs, Config config) {
    _tupleStore = ts;
    _ruleStore = rs;
    noOfTasks = rs.allRules.size();
    _config = config;
    /**
    _noOfCores = config.getNoOfCores();
    _cleanUpRepository = config.isCleanupRepository();
    _eqReduction = (Boolean) config.isEqReduction();
    _gc = (Boolean) config.isGarbageCollection();
    _noOfIterations = (int) config.get(Config.ITERATIONS);
    verbose = (boolean) config.get(Config.VERBOSE);
    **/
     this.threadPool = Executors.newFixedThreadPool(config.getNoOfCores());

  }


  /**
  private ForwardChainer(TupleStore ts, RuleStore rs){
    _tupleStore = ts;
    _ruleStore = rs;

    noOfTasks = rs.allRules.size();
  }
   **/






  /////////////////////////////////////////////////////////////////////////////////////////////////////////////
  /**
   * to (dynamically) change the number of processor cores at runtime, use this method
   */
  /**
  public void setNoOfCores(int noOfCores) {
    _noOfCores = noOfCores;
    this.threadPool = Executors.newFixedThreadPool(noOfCores);
  }
  **/



  /////////////////////////////////////////////////////////////////////////////////////////////////////////////

  /**
   * performs a local match over the LHS clauses;
   * the result of structurally equivalent clauses is shared between clauses, even
   * across rules
   */
  private void executeLocalMatch(Rule rule) {
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
        query = _tupleStore.queryIndex(rule.ante[i], rule.localQueryBindings[i]);
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
   * $owl_typeBySomeVal
   * ?q <rdf:type> ?c
   * ?r <owl:onProperty> ?p
   * ?r <owl:someValuesFrom> ?c
   * ?i ?p ?q
   * ->
   * ?i <rdf:type> ?r
   * instead of
   * (0 J 2) J (1 J 2)
   * avoid second "inner" join:
   * (0 J 2) J 1
   * this is achieved by computing "clusters" of LHS clauses
   *
   * @see de.dfki.lt.hfc.RuleStore
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
                  new BindingTable(new TCustomHashSet<>(table.getStrategy(), table.getDelta()),
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
                new ArrayList<>(),
                new HashMap<>());
        if (result.isEmpty()) {
          // if at least one cluster yields an empty binding table, the whole LHS is not satisfiable
          rule.isApplicable = false;
          return;
        } else {
          rule.isApplicable = true;
          cluster.bindingTable = result;
        }
      }
    }
  }

  /**
  public boolean getCleanUpRepository() {
    return _cleanUpRepository;
  }

  public boolean isCleanUpRepository() {
    return _cleanUpRepository;
  }

  public boolean isEquivalenceClassReduction() {
    return _eqReduction;
  }
  **/

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
      if (!newInfo)
        // no new info -- no need to do some potential expensive joins
        return new BindingTable(new THashSet<>(), null);
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
        } else
          result = memoResult;
      }
      return result;
    }
    // take the union of the continuation of complexJoin() for both delta and old at index
    else {
      Table current = locbind[cluster.get(index)];
      BindingTable delta;
      if (current.getDelta().isEmpty())
        delta = new BindingTable(new THashSet<>(), null);
      else {
        // add current's non-empty delta to the continuation
        ArrayList<BindingTable> deltaJoinit = new ArrayList<>(joinit);
        deltaJoinit.add(new BindingTable(current.getDelta(), current.nameToPosProper));
        delta = complexJoin(locbind, cluster, index + 1, true, deltaJoinit, memo);
        //                                               true, of course
      }
      BindingTable old;
      if (current.getOld().isEmpty())
        old = new BindingTable(new THashSet<>(), null);
      else {
        // add current's non-empty old to the continuation
        ArrayList<BindingTable> oldJoinit = new ArrayList<>(joinit);
        oldJoinit.add(new BindingTable(current.getOld(), current.nameToPosProper));
        old = complexJoin(locbind, cluster, index + 1, newInfo, oldJoinit, memo);
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
   * + 1 LHS clause cluster:  ?x p ?y, ?y p ?z, ?x != ?y, ?y != ?z -> ?x p ?z
   * do NOT iterate _twice_ over the binding table to check both in-eqs
   * + 2 LHS clause clusters: ?u p ?v, ?x q ?y, ?u != ?y -> ?u r ?y
   * in-eq applies to RHS, but RHS in principle requires Cartesian product of
   * the two independent LHS clusters
   * + 2 LHS clause clusters: ?u p ?v, ?x q ?y, ?u != ?x, ?v != ?y -> ?u r ?y
   * both in-eqs can not be applied directly to LHS clusters, nor RHS, but
   * in-eqs rule out certain combinations for (?u, ?y) that cooccur with
   * (?u, ?x) and (?v, ?y)
   * + in-eqs with URIs/XSD atoms: ?s ?p ?o, ?p != <rdf:type> -> .....
   */
  private void applyTests(Rule rule) {
    for (Cluster cluster : rule.clusters) {
      // _destructively_ restricts binding table using cluster's ineqs and tests
      //System.out.println(rule.name + ": " + cluster.bindingTable.table.size());
      cluster.bindingTable = Calc.restrict(cluster.bindingTable, cluster.varvarIneqs, cluster.varconstIneqs);
      cluster.bindingTable = Calc.restrict(cluster.bindingTable, cluster.tests);
      //System.out.println(rule.name + ": " + cluster.bindingTable.table.size());
      // one empty cluster suffices to let the rule fail overall (no RHS instantiations possible)
      if (cluster.bindingTable.isEmpty())
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
   * Cartesian Product need not be carried out !!
   */
  private void prepareInstantiation(Rule rule) {
    // check whether we deal with only one or several clusters
    if (rule.clusters.length == 1) {
      // only one cluster, i.e., no remaining in-eqs to be considered
      rule.megaCluster = rule.clusters[0];  // copy in case ... see comment above
    } else {
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
      rule.megaCluster.bindingTable = complexProduct(rule.clusters, 0, false, new ArrayList<>());
      // destructively apply *remaining* in-eqs and tests to mega cluster, using Calc.restrict()
      rule.megaCluster.bindingTable = Calc.restrict(rule.megaCluster.bindingTable, rule.megaCluster.varvarIneqs, rule.megaCluster.varconstIneqs);
      rule.megaCluster.bindingTable = Calc.restrict(rule.megaCluster.bindingTable, rule.megaCluster.tests);
      // check whether result is empty (assign rule.isApplicable appropriate value)
      if (rule.megaCluster.bindingTable.isEmpty()) {
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
      if (!newInfo)
        return new BindingTable(new THashSet<>(), null);
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
        delta = new BindingTable(new THashSet<>(), null);
      else {
        // add current's non-empty delta to the continuation
        ArrayList<BindingTable> deltaProductit = new ArrayList<>(productit);
        deltaProductit.add(new BindingTable(current.delta, current.bindingTable.nameToPos));
        delta = complexProduct(clusters, index + 1, true, deltaProductit);
        //                                          true, of course
      }
      BindingTable old;
      if (current.old.size() == 0)
        old = new BindingTable(new THashSet<>(), null);
      else {
        // add current's non-empty old to the continuation
        ArrayList<BindingTable> oldProductit = new ArrayList<>(productit);
        oldProductit.add(new BindingTable(current.old, current.bindingTable.nameToPos));
        old = complexProduct(clusters, index + 1, newInfo, oldProductit);
        //                                        newInfo, of course
      }
      // take the union of delta and old
      return new BindingTable(Calc.union(delta.table, old.table),
              delta.nameToPos != null ? delta.nameToPos : old.nameToPos);
    }
  }

  /**
   * the input table for RHS instantiation has already been projected to the RHS variables
   *
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
    if (_config.isVerbose()) {
      synchronized (logger) {
        logger.info("  " + rule.name + ": " + rule.megaCluster.old.size() +
                " " + rule.megaCluster.delta.size());
      }
    }
    // use a mediator (array) instead of using the slower map; take care of _blank_node_ vars;
    ArrayList<Integer> allrhsvars = new ArrayList<>(rhsvars);
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
      varToFunct = new TIntObjectHashMap<>();  // ditto
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
                        _tupleStore.operatorRegistry.evaluate(function.name, OperatorRegistry.OPERATOR_PATH, input);
              } else {
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
   * generates a new unique blank node id (an int);
   * used during forward chaining when unbounded right-hand side variables are introduced;
   * it is important that the method is synchronized to exclusively lock the blank counter;
   */
  public int nextBlankNode() {
    synchronized (_tupleStore) {
      return _tupleStore.putObject(_blankNodePrefix + _blankCounter++);
    }
  }

  /**
   * performs the matching and instantiation for each rule;
   * keeps track of local information in order to speed up rule execution;
   * writes generated tuples to local output field of the rule
   */
  protected void execute(Rule rule) {
    if(_config.isVerbose())
      synchronized (logger) {
        logger.info(" execute rule " + rule.name);
      }
    try {
      // reuse rule's output field; make it empty, even for rules that are switched off
      rule.output.clear();
      // do not execute rules which have a priority less or equal 0
      if (rule.priority <= 0) {
        if (_config.isVerbose())
          synchronized (logger) {
            logger.info("  " + rule.name + ": off");
          }
        return;
      }
      // query the index for each LHS clause
      executeLocalMatch(rule);
      // and check whether rule is applicable on local grounds
      if (!rule.isApplicable) {
        if (_config.isVerbose())
          synchronized (logger) {
            logger.info("  " + rule.name + ": local");
          }
        return;
      }
      // same for the whole antecedent
      executeGlobalMatch(rule);
      if (!rule.isApplicable) {
        if (_config.isVerbose())
          synchronized (logger) {
            logger.info("  " + rule.name + ": global");
          }
        return;
      }
      // apply in-eqs if in-eq vars belong to the same cluster
      applyTests(rule);
      if (!rule.isApplicable) {
        if (_config.isVerbose())
          synchronized (logger) {
            logger.info("  " + rule.name + ": tests");
          }
        return;
      }
      // compute "deltas" over LHS clusters
      prepareInstantiation(rule);
      if (!rule.isApplicable) {
        if (_config.isVerbose())
          synchronized (logger) {
            logger.info("  " + rule.name + ": cluster");
          }
        return;
      }
      // start instantiation phase
      performInstantiation(rule);
      System.out.println(rule.output);
    } finally {
      // only at the _very end_ decrease count down latch (cf. return statements above)
      this.doneSignal.countDown();
    }
  }

  /**
   * assigns each rule execution a single task which is added to the thread pool
   */
  private void executeAllRules() {
    Runnable task;
    for (final Rule rule : _ruleStore.allRules) {
      task = () -> execute(rule);
      logger.info("Submitting task for rule " + rule);
      this.threadPool.submit(task);
    }
  }

  /**
   * computes the deductive closure given an initial set of tuples as
   * specified by TupleStore.allTuples;
   * iterates until a fixpoint is computed or a predefined iteration depth
   * has been reached as given by the argument
   *
   * @return false, otherwise
   */
  public boolean computeClosure(int noOfIterations, boolean cleanUpRepository) {
    logger.info("Number of Iterations = " + noOfIterations);
    logger.info("CleanUp Repository = " + noOfIterations);
    int noOfAllTuples = _tupleStore.allTuples.size();
    if (_config.isVerbose())
      logger.info("\n  number of all tuples: " + noOfAllTuples + "\n");
    int currentIteration = 0;
    long time = System.currentTimeMillis();
    long fullTime = time;
    boolean newInfo = false;
    int noOfNewTuples = 0;
    if (_config.isVerbose())
      logger.info("  rule name: old new OR failure stage");
    // increment generation counter for deletion here AND also at the very end
    boolean notContained;
    ++_tupleStore.generation;
    do {
      // increment global generation counter (is never reset)
      ++this.generationCounter;
      // increment number of local iterations wrt. computeClosure()
      ++currentIteration;
      if (_config.isVerbose())
        logger.info("  " + currentIteration);
      // execute all rules (quasi) in parallel, taking advantage of multi-core CPUs
      this.doneSignal = new CountDownLatch(this.noOfTasks);  // = #rules
      try {
        logger.info("Execute all ("+ _ruleStore.allRules.size() + ") rules ...");
        executeAllRules();
        this.doneSignal.await();  // wait for all tasks to finish
      } catch (InterruptedException ie) {
        logger.error(ie.toString());
      }
      // add rule-generated tuples to set of all tuples and update the index
      newInfo = false;
      noOfNewTuples = 0;
      for (Rule rule : _ruleStore.allRules) {
        logger.info("Looking at rule " + rule);
        for (int[] tuple : rule.output) {
          notContained = _tupleStore.addTuple(tuple);
          newInfo = notContained || newInfo;
          ++noOfNewTuples;
        }
      }
      if (_config.isVerbose()) {
        logger.info("  " + noOfNewTuples + "/" + _tupleStore.allTuples.size() +
                " (" + (System.currentTimeMillis() - time) + "msec)");
        time = System.currentTimeMillis();
      }
      // perhaps trigger a GC after each iteration step
      if (_config.isGarbageCollection())
        System.gc();
    } while (newInfo && (currentIteration != noOfIterations));
    // increment the generation  counter for deltion again for further upload
    ++_tupleStore.generation;
    // some statistics
    if (_config.isVerbose()) {
      logger.info("\n  number of all tuples: " + _tupleStore.allTuples.size());
      logger.info("  " + (_tupleStore.allTuples.size() - noOfAllTuples) +
              " tuples generated");
      logger.info("  closure computation took " + (System.currentTimeMillis() - fullTime) + "msec");
    }
    // possibly cleanup
    if (_config.isEqReduction() && _config.isCleanupRepository()) {
      if (_config.isVerbose()) {
        logger.info("\n  cleaning up repository ... ");
      }
      _tupleStore.cleanUpTupleStore();
      if (_config.isVerbose()) {
        logger.info("done");
        logger.info("  number of all tuples: " + _tupleStore.allTuples.size());
      }
    }
    logger.info("Generation Counter: " + generationCounter);
    // and finally the `answer'
    if ((noOfAllTuples - _tupleStore.allTuples.size()) == 0)
      return false;
    else
      return true;
  }

  
  /**
   * calls computeClosure(int noOfIterations) again, assuming that a set of
   * new tuples has been added to the tuple store;
   *
   * @return false, otherwise
   */
  public boolean computeClosure(Set<int[]> newTuples, int noOfIterations, boolean cleanUpRepository) {
    // note: A \subseteq T <==> A \setminus T = \emptyset
    // use this to avoid subset test, union, and difference operations
    newTuples.removeAll(_tupleStore.allTuples);  // is a destructive operation
    // is newTuples a subset of allTuples
    if (newTuples.isEmpty()) {
      if (_config.isVerbose())
        logger.info("\n  no tuples generated");
      return false;
    }
    for (int[] tuple : newTuples) {
      _tupleStore.addTuple(tuple);
    }
    // and finally call nullary computeClosure()
    return computeClosure(noOfIterations, cleanUpRepository);
  }
  



  /////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
 



  /**
   * shutdowns the thread pool and exits with value 0
   */
  public void shutdown() {
    this.threadPool.shutdown();
    if (_config.isVerbose()) {
      logger.info("\n  shutting down thread pool ...");
      logger.info("  exiting ...\n");
    }
    System.exit(0);
  }

  /////////////////////////////////////////////////////////////////////////////////////////////////////////////

  /**
   * only shutdowns the thread pool, but no System.exit() is called;
   * used by the XMLRPC server
   */
  public void shutdownNoExit() {
    this.threadPool.shutdown();
    if (_config.isVerbose()) {
      logger.info("  shutting down thread pool ...");
    }
  }

  

 

  /**
   * @return false otherwise
   * <p>
   * there is a similar method in class TupleStore
   */
  public boolean tupleDeletionEnabled() {
    return (_tupleStore.tupleToGeneration != null);
  }


  /**
   * if 0, auxiliary structures in forward chainer are not compressed/deleted;
   * if 1, the old/new separation for clause proxies, clusters, and mega
   * clusters is deleted;
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

  /////////////////////////////////////////////////////////////////////////////////////////////////////////////

  /**
   *
   */
  private void deleteOldNew() {
    // do not call clear() since it does NOT frees the memory (only clears the sets)
    // delete clause-level info shared between rules
    for (Proxy proxy : _ruleStore.equivalentClauses.values()) {
      proxy.table = new THashSet<>();
      proxy.delta = null;
      proxy.old = null;
    }
    // delete local rule-level cluster/mega-cluster info + output field
    for (Rule rule : _ruleStore.allRules) {
      rule.output = new TCustomHashSet<>(ForwardChainer.DEFAULT_HASHING_STRATEGY);
      for (Cluster cluster : rule.clusters) {
        cluster.table = new THashSet<>();
        cluster.delta = null;
        cluster.old = null;
      }
      rule.megaCluster.table = new THashSet<>();
      rule.megaCluster.delta = null;
      rule.megaCluster.old = null;
    }
  }

  /**
   *
   */
  private void deleteIndex() {
    Map<Integer, Set<int[]>>[] index = _tupleStore.index;
    for (int i = 0; i < index.length; i++)
      // do not call clear() since it does NOT frees the memory (only clears the mappings)
      index[i] = new HashMap<>();
  }

  /**
   *
   */
  public void uncompressIndex() {
    for (int[] tuple : _tupleStore.allTuples)
      _tupleStore.addToIndex(tuple);
  }
  /////////////////////////////////////////////////////////////////////////////////////////////////////////////

  /**
   * enableTupleDeletion() makes use of a special field that is initialized by an empty map
   * from tuples (int[]) to generations (Integer);
   * this field is localized in class TupleStore, since it interacts with the equivalence
   * class reduction mechanism that is also located in TupleStore;
   * <p>
   * this special case of tuple deletion differs from the functionality in class TupleStore
   * which only deletes tuples given to method removeTuple(), in that _entailed_ tuples t'
   * which potentially depend on the deleted tuple t (gen(t') > gen(t)) must also be deleted
   * AND closure computation needs to be called again
   * <p>
   * IMPORTANT:
   * use this and only this method to enable tuple deletion in the forward chainer;
   * <p>
   * it is IMPORTANT that this method is DIRECTLY called AFTER an instance of ForwardChainer
   * has been created and before the first closure computation is called;
   * if it would be called some time later AFTER at least one closure computation has been
   * called, i.e., if (TupleStore.generation > 0), the method will do NOTHING (and returns
   * false);
   * when this method returns true, it is guaranteed that all tuples from the tuple store
   * are assigned generation 0
   *
   * @return false otherwise, i.e., tuple deletion has been made ready TOO LATE = no effect
   */
  public boolean enableTupleDeletion() {
    if (_tupleStore.generation == 0) {
      // at the moment, NO closure has been computed
      if (_tupleStore.tupleToGeneration == null)
        _tupleStore.tupleToGeneration = new TCustomHashMap<>(ForwardChainer.DEFAULT_HASHING_STRATEGY);
      for (int[] tuple : _tupleStore.allTuples)
        _tupleStore.tupleToGeneration.put(tuple, 0);
      if (_config.isVerbose())
        logger.info("  tuple deletion enabled");
      return true;
    } else {
      if (_config.isVerbose())
        logger.info("  tuple deletion can no longer be enabled, since closure computation was already called");
      return false;
    }
  }

  /**
   * deletes a tuple and _potentially_ dependent entailed tuples of specific generations from the
   * tuples store, followed by a new closure computation;
   * note that we need to check whether equivalence class reduction has been turned on; if so, the
   * tuple needs to be replaced by its proxy!
   * <p>
   * this strategy implements a compromise between deleting everything and setting up a full
   * TMS-like structure (that would furthermore make speed-up techniques in HFC obsolete);
   * <p>
   * use deleteTuples() below if you want to delete more than one tuple, since deleteTuples()
   * calls closure computation only once at the very end of the deletion process, whereas this method
   * calls it for the tuple bound to parameter tuple
   * <p>
   * note: this method obtains a lock of _tupleStore
   *
   * @return false otherwise
   */
  public final boolean deleteTuple(int[] tuple) {
    synchronized (_tupleStore) {
      // use the proxy of tuple elements in case equivalence class reduction has been enabled
      if (_tupleStore.equivalenceClassReduction) {
        int[] newTuple = new int[tuple.length];
        for (int i = 0; i < tuple.length; i++)
          newTuple[i] = _tupleStore.getProxy(tuple[i]);
        tuple = newTuple;
      }
      // obtain generation: no need to call containsKey()
      Integer tgen = _tupleStore.tupleToGeneration.get(tuple);
      // tuple NOT contained in tuple store
      if (tgen == null)
        return false;
      // check whether tuple is an entailed tuple, i.e., it will be introduced later again during
      // closure computation, thus no need to go further here
      if ((tgen % 2) != 0)
        return false;
      if (_config.isVerbose())
        logger.info("\n  falling back to generation " + tgen + " ... ");
      // remove tuple from the tuple store, also removes tuple-to-generation mapping, if enabled
      _tupleStore.removeTuple(tuple);
      // remove potentially dependent materialized tuples: first, determine the relevant tuples
      // (side note: seems that iterator plus remove() is not allowed for int[])
      Integer egen;
      final ArrayList<int[]> toBeDeleted = new ArrayList<>();
      // this computation could in principle be speeded up in case we would have a generation-to-tuple mapping
      for (int[] element : _tupleStore.allTuples) {
        egen = _tupleStore.tupleToGeneration.get(element);
        // only delete entailed tuples with a greater generation
        if (((egen % 2) != 0) && (egen > tgen))
          toBeDeleted.add(element);
      }
      // then remove those tuples
      for (int[] element : toBeDeleted) {
        _tupleStore.removeTuple(element);
      }
      // delete old vs. new separation for proxies, clusters, and mega-clusters, and do not modify them
      // separately (too complex and expensive): reduces to compress level = 1
      compress(1);
      // and finally call closure computation again, even for only this single tuple
      if (_config.isVerbose()) {
        logger.info("done");
        logger.info("  calling closure computation again ...");
      }
      computeClosure(_config.getIterations(),_config.isCleanupRepository());
      // check whether tuple is still in the set of all tuples, i.e., someone tried to delete an
      // entailed tuple which will NOT work (since it is reintroduced through closure computation!
      return _tupleStore.allTuples.contains(tuple);
    }
  }

  /**
   * use this method if you want to delete SEVERAL tuples at once, since both this and the above method
   * always call computeClosure() at the very end (expensive!), independent of the number of tuples deleted
   * <p>
   * note: this method obtains a lock of _tupleStore
   *
   * @return false otherwise, i.e., EITHER at least one tuple from tuples was not an element of the set
   * of all tuples OR the tuple under deletion is an entailed tuple that is later introduced
   * again as the result of calling the closure computation OR at least one tuple is not contained
   * in the set of all tuples
   */
  public final boolean deleteTuples(Collection<int[]> tuples) {
    synchronized (_tupleStore) {
      // empty collection: return true (like an empty conjunction)
      if (tuples.isEmpty())
        return true;
      // replace URIs by their proxies in tuples in case equivalence class reduction has been enabled
      if (_tupleStore.equivalenceClassReduction) {
        Collection<int[]> newTuples = new ArrayList<>(tuples.size());
        int[] newTuple;
        for (int[] tuple : tuples) {
          newTuple = new int[tuple.length];
          for (int i = 0; i < tuple.length; i++)
            newTuple[i] = _tupleStore.getProxy(tuple[i]);
          newTuples.add(newTuple);
        }
        tuples = newTuples;
      }
      // obtain _lowest_ generation number: minimum computation
      int lowest = Integer.MAX_VALUE;  // highly unlikely to be found in the set of all tuples
      Integer tgen;
      final ArrayList<int[]> toBeDeleted = new ArrayList<>();
      for (int[] tuple : tuples) {
        tgen = _tupleStore.tupleToGeneration.get(tuple);
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
      if (_config.isVerbose())
        logger.info("\n  falling back to generation " + lowest + " ... ");
      // return value = true iff card(tuples) == card(toBeDeleted)
      boolean result = (tuples.size() == toBeDeleted.size());
      // delete the uploaded tuples from tuples
      for (int[] tuple : toBeDeleted) {
        _tupleStore.removeTuple(tuple);
      }
      // delete the potentially relevant entailed tuples
      Integer egen;
      toBeDeleted.clear();  // reuse toBeDeleted
      for (int[] element : _tupleStore.allTuples) {
        egen = _tupleStore.tupleToGeneration.get(element);
        if (((egen % 2) != 0) && (egen > lowest))
          toBeDeleted.add(element);
      }
      for (int[] element : toBeDeleted) {
        _tupleStore.removeTuple(element);
      }
      compress(1);
      if (_config.isVerbose()) {
        logger.info("done");
        logger.info("  calling closure computation again ...");
      }
      computeClosure(_config.getIterations(), _config.isCleanupRepository());
      return result;
    }
  }

  /**
   * transaction 1: addTuplesToRepository()
   * adds a collection of tuples to the repository;
   * this quasi-synchronized method obtains a lock on _tupleStore;
   * note that the generation counter from TupleStore is incremented by 2 _before_
   * the tuples are added in order to distinguish the tuples involved in this transactions
   * from `ordinary' tuples that are `only' uploaded;
   * note further that this transaction does NOT compute the deductive closure of the repository
   *
   * @return false iff an error happened during the transaction or in case tuple deletion
   * has NOT been enabled;
   * note that we gurantee that the effects which have happended during the
   * transaction are invalidated
   */
  public final boolean addTuplesToRepository(Collection<int[]> tuples) {
    if (tupleDeletionEnabled()) {
      synchronized (_tupleStore) {
        // remember what has been added in case things go wrong
        final ArrayList<int[]> added = new ArrayList<>();
        try {
          // each `positive' transaction increments the generation counter;
          // since NO closure computation is involved, it must be incremented by 2
          _tupleStore.generation = _tupleStore.generation + 2;
          for (int[] tuple : tuples) {
            _tupleStore.addTuple(tuple);
            added.add(tuple);
          }
          return true;
        } catch (Exception e) {
          logger.error(e.toString());
          // if something went wrong during the transaction, iterate over the remembered tuples and
          // redo the insertions, not necessarily _all_ tuples
          for (int[] tuple : added)
            _tupleStore.removeTuple(tuple);
          _tupleStore.generation = _tupleStore.generation - 2;
          return false;
        }
      }
    }
    // tuple deletion NOT enabled
    else
      return false;
  }

  /////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // NOTE: the four transactions below require that tuple deletion has been enabled in the forward chainer
  // @see ForwardChainer.enableTupleDeletion()

  /**
   * transaction 2: removeTuplesFromRepository()
   * removes a collection of tuples from the repository;
   * note that the entailed tuples are NOT deleted, only the specified tuples;
   * in case closure computation is never called, i.e., the repository is only used for
   * querying the explicit uploaded information, use this method instead of nethod below;
   * this quasi-synchronized method obtains a lock on _tupleStore
   *
   * @return false iff an error happened during the transaction or in case tuple deletion
   * has NOT been enabled;
   * note that we gurantee that the effects which have happended during the
   * transaction are invalidated
   */
  public final boolean removeTuplesFromRepository(Collection<int[]> tuples) {
    if (tupleDeletionEnabled()) {
      synchronized (_tupleStore) {
        final Hashtable<int[], Integer> tuple2generation = new Hashtable<>();
        try {
          int generation;
          // remove tuples from repository, but remember their generation
          for (int[] tuple : tuples) {
            generation = _tupleStore.removeTupleReturnGeneration(tuple);
            // only record tuples that were part of the repository
            if (generation != -1)
              tuple2generation.put(tuple, generation);
          }
          return true;
        } catch (Exception e) {
          logger.error(e.toString());
          // if something went wrong during transaction, iterate over removed tuples
          // and undo the deletions
          for (Map.Entry<int[], Integer> entry : tuple2generation.entrySet())
            _tupleStore.addTupleWithGeneration(entry.getKey(), entry.getValue());
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
  private boolean deleteTuplesRecordGenerations(Collection<int[]> tuples,
                                                Hashtable<int[], Integer> mapping) {
    // empty collection: return true (like an empty conjunction)
    if (tuples.isEmpty())
      return true;
    // replace URIs by their proxies in tuples in case equivalence class reduction has been enabled
    if (_tupleStore.equivalenceClassReduction) {
      Collection<int[]> newTuples = new ArrayList<>(tuples.size());
      int[] newTuple;
      for (int[] tuple : tuples) {
        newTuple = new int[tuple.length];
        for (int i = 0; i < tuple.length; i++)
          newTuple[i] = _tupleStore.getProxy(tuple[i]);
        newTuples.add(newTuple);
      }
      tuples = newTuples;
    }
    // obtain _lowest_ generation number: minimum computation
    int lowest = Integer.MAX_VALUE;  // highly unlikely generation to be found in the set of all tuples
    Integer tgen;
    final ArrayList<int[]> toBeDeleted = new ArrayList<>();
    for (int[] tuple : tuples) {
      tgen = _tupleStore.tupleToGeneration.get(tuple);
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
    if (_config.isVerbose())
      logger.info("\n  falling back to generation " + lowest + " ... ");
    // return value = true iff card(tuples) == card(toBeDeleted)
    boolean result = (tuples.size() == toBeDeleted.size());
    // delete the uploaded tuples from tuples and record their generation
    int generation;
    for (int[] tuple : toBeDeleted) {
      generation = _tupleStore.removeTupleReturnGeneration(tuple);
      if (generation != -1)
        mapping.put(tuple, generation);
    }
    // delete the potentially relevant entailed tuples
    Integer egen;
    toBeDeleted.clear();  // reuse toBeDeleted
    for (int[] element : _tupleStore.allTuples) {
      egen = _tupleStore.tupleToGeneration.get(element);
      if (((egen % 2) != 0) && (egen > lowest))
        toBeDeleted.add(element);
    }
    for (int[] element : toBeDeleted) {
      generation = _tupleStore.removeTupleReturnGeneration(element);
      if (generation != -1)
        mapping.put(element, generation);
    }
    // throw away auxiliary data structures and do NOT individually remove tuples
    compress(1);
    if (_config.isVerbose()) {
      logger.info("done");
      logger.info("  calling closure computation again ...");
    }
    // call closure computation again to reestablish the entailed tuples and auxiliary structures
    computeClosure(_config.getIterations(), _config.isCleanupRepository());
    return result;
  }

  /**
   * transaction 3: deleteTuplesFromRepository()
   * deletes a collection of tuples from the repository; not only the direct tuples
   * are deleted, but also the entailed tuples which solely depend on the deleted
   * tuples;
   * this quasi-synchronized method obtains a lock on _tupleStore
   *
   * @return false iff an error appeared during the transaction or in case tuple deletion
   * has NOT been enabled;
   * note that we gurantee that the effects which have happended during the
   * transaction are invalidated
   */
  public final boolean deleteTuplesFromRepository(Collection<int[]> tuples) {
    if (tupleDeletionEnabled()) {
      synchronized (_tupleStore) {
        final Hashtable<int[], Integer> tuple2generation = new Hashtable<>();
        final int oldGeneration = _tupleStore.generation;
        try {
          // update tuple2generation from inside deleteTuplesRecordGenerations()
          deleteTuplesRecordGenerations(tuples, tuple2generation);
          return true;
        } catch (Exception e) {
          logger.error(e.toString());
          // if something went wrong during transaction, iterate over removed tuples
          // and undo the deletions
          _tupleStore.generation = oldGeneration;
          for (Map.Entry<int[], Integer> entry : tuple2generation.entrySet())
            _tupleStore.addTupleWithGeneration(entry.getKey(), entry.getValue());
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
   * this quasi-synchronized method obtains a lock on _tupleStore
   *
   * @return false iff an error appeared during the transaction;
   * note that we gurantee that the effects which have happended during the
   * transaction are invalidated
   */
  public final boolean computeClosureFromRepository() {
    if (tupleDeletionEnabled()) {
      synchronized (_tupleStore) {
        final int deleteThatGeneration = _tupleStore.generation + 1;
        try {
          computeClosure(_config.getIterations(),_config.isCleanupRepository());
          return true;
        } catch (Exception e) {
          logger.error(e.toString());
          // undo the effects of the closure computation
          for (int[] tuple : _tupleStore.allTuples) {
            if (_tupleStore.tupleToGeneration.get(tuple) == deleteThatGeneration)
              // remove the ENTAILED tuple (ODD generation number)
              _tupleStore.removeTuple(tuple);
          }
          _tupleStore.generation = deleteThatGeneration - 1;
          return false;
        }
      }
    }
    // tuple deletion NOT enabled
    else
      return false;
  }

  public ForwardChainer copyForwardChainer(TupleStore tupleStoreCopy, RuleStore ruleStoreCopy, int noOfCores) {

    ForwardChainer copy = new ForwardChainer(tupleStoreCopy, ruleStoreCopy, _config);
    copy.generationCounter = generationCounter;
    // copy tuple store and rule store
    // ***WARNING***: do not let work this and copy in PARALLEL !!!!!
    // reuse thread pool of this object
    copy.threadPool = this.threadPool;
    copy.noOfTasks = _ruleStore.allRules.size();
    // take over blankCounter, although not necessary
    // copy over information related to deletion of tuples (independent of whether enabled or disabled)
    copy._tupleStore.generation = _tupleStore.generation;
    if (tupleDeletionEnabled())
      copy._tupleStore.tupleToGeneration =
              new TCustomHashMap<>(ForwardChainer.DEFAULT_HASHING_STRATEGY, _tupleStore.tupleToGeneration);
    else
      copy._tupleStore.tupleToGeneration = null;
    return copy;
  }

  /**
  public void setcleanUpRepository(boolean b) {
    _cleanUpRepository = true;
  }
  **/

  /**
   * used below to memoize the result of a join of two tables
   * in complexJoin()
   */


}
