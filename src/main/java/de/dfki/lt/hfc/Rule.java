package de.dfki.lt.hfc;

import gnu.trove.set.hash.TCustomHashSet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * a Rule object consists of an antecedent and a consequent;
 * both antecedent and consequent are represented as a sequence of tuples,
 * interpreted conjunctively;
 * internally, we use integers to represent the literals: for URIs, blank
 * nodes, and XSD atoms, we use positive ints (incl. 0), whereas variables
 * are assigned negative numbers
 *
 * @author (C) Hans-Ulrich Krieger
 * @version Wed Feb 27 19:54:22 CET 2013
 * @see de.dfki.lt.hfc.RuleStore
 * @since JDK 1.5
 */
public class Rule {

  /**
   * the _smaller_ the priority value is, the more important a rule is;
   * priorities might gain importannce during the parallel execution of rules
   * to speed up processing;
   * default value in case no priority is specified: Integer.MAX_VALUE
   * <p>
   * NOTE: a priority value less or equal 0 switches off rule execution
   * in the forward chainer
   *
   * @see de.dfki.lt.hfc.ForwardChainer#noOfCores
   */
  protected int priority = Integer.MAX_VALUE;

  /**
   * the name of a rule, mainly used for debugging purposes
   */
  protected String name;

  /**
   * the internal representation of the antecedent of a rule
   */
  protected int[][] ante;

  /**
   * the internal representation of the consequent of a rule
   */
  protected int[][] cons;

  /**
   * the set of ALL (local) rule variables
   */
  protected HashSet<Integer> properVariables;

  /**
   * LHS variables that will only occur once are refered to as don't cares;
   * no binding during the matching phase is needed
   */
  protected HashSet<Integer> dontCareVariables;

  /**
   * proper RHS-only variables (does NOT contain any blank node vars)
   */
  protected HashSet<Integer> rhsVariables;

  /**
   * free RHS-only variables that will introduce fresh new individuals
   * (viz., blank nodes);
   * a first occurence introduce the individual, further RHS mentionings
   * (inside the same rule) refer to the same newly introduced individual
   */
  protected HashSet<Integer> blankNodeVariables;

  /**
   * represents a mapping from relational ids to their functional counterparts;
   * note that relational variables in tests and actions might even be complex,
   * leading to the array list in this data structure
   * id(??var) -> [id(?var)]
   * id(??(var1 ... varN)) -> [id(var1), ..., id(?varN)]
   */
  protected HashMap<Integer, ArrayList<Integer>> relIdToFunIds;

  /**
   * a list of Integer objects, either representing variables (< 0) or
   * URIs/XSD atoms (> 0);
   * Integers at odd positions always represent variables;
   * the list is of even length, so that the (1st, 2nd), (3rd, 4th), etc.
   * objects stand in the inequality relation
   */
  protected ArrayList<Integer> inEqConstraints = new ArrayList<Integer>();

  /**
   * an internal representation of the predicates occuring in the @test section
   * of a rule
   */
  protected ArrayList<Predicate> tests = new ArrayList<Predicate>();

  /**
   * an internal representation of the functions occuring in the @action section
   * of a rule
   */
  protected ArrayList<Function> actions = new ArrayList<Function>();

  /**
   * during the local matching phase, we partially detect (as a consequence
   * of local matching) whether rules are applicable w.r.t. the set of tuples
   */
  protected boolean isApplicable = true;

  /**
   * an array of "local" binding tables for each clause of the antecedent of the rule
   */
  protected Table[] localQueryBindings;

  /**
   * an array of int arrays (elements are called key) that is used to share local
   * query bindings across structurally-equivalent clauses, even across rules
   */
  protected int[][] keys;

  /**
   * an array of clusters, encoding which LHS clauses will go together, as well as
   * which in-eqs are applicable;
   * clusters furthermore wrap the binding table(s) which originate from joining
   * the local clauses belonging to a cluster
   */
  protected Cluster[] clusters;

  /**
   * a cluster encoding _the_ LHS binding table, originating from taking the Cartesian
   * Product of the set of all independent LHS clusters
   */
  protected Cluster megaCluster;

  /**
   * contains the set of all tuples that are generated within an iteration by this rule
   */
  protected Set<int[]> output = new TCustomHashSet<int[]>(ForwardChainer.DEFAULT_HASHING_STRATEGY);

  /**
   * a pointer to the tuple store; useful to have for later decoding of int arguments
   */
  protected TupleStore tstore;

  /**
   * a pointer to the rule store; useful to work with several rule systems at the same time
   */
  protected RuleStore rstore;

  /**
   *
   */
  protected Rule() {
  }

  /**
   * copy contructor for exclusive use in copyRuleStore()
   */
  protected Rule(Rule rule, TupleStore tstore, RuleStore rstore) {
    // rule is old; tstore and rstore are new copies
    this.tstore = tstore;
    this.rstore = rstore;
    // take over the following
    this.priority = rule.priority;
    this.name = rule.name;
    this.ante = rule.ante;
    this.cons = rule.cons;
    this.properVariables = rule.properVariables;
    this.dontCareVariables = rule.dontCareVariables;
    this.rhsVariables = rule.rhsVariables;
    this.blankNodeVariables = rule.blankNodeVariables;
    this.inEqConstraints = rule.inEqConstraints;
    this.tests = rule.tests;
    this.actions = rule.actions;
    this.isApplicable = rule.isApplicable;
    this.keys = rule.keys;
    // copy localQueryBindings, clusters, and megaCluster
    this.localQueryBindings = new Table[rule.localQueryBindings.length];
    for (int i = 0; i < rule.localQueryBindings.length; i++) {
      this.localQueryBindings[i] = new Table(rule.localQueryBindings[i],  // old table
              rstore.equivalentClauses.get(rule.keys[i]));  // new proxy
    }
    this.clusters = new Cluster[rule.clusters.length];
    for (int i = 0; i < rule.clusters.length; i++) {
      // tstsore is use by copy constructor of BindingTable
      this.clusters[i] = new Cluster(rule.clusters[i], tstore);
    }
    this.megaCluster = new Cluster(rule.megaCluster, tstore);
    // note: empty output field is constructed by field's init form (which is correct!)
  }

  /**
   *
   */
  public Rule(String name, int[][] ante, int[][] cons, TupleStore tstore, RuleStore rstore) {
    this.name = name;
    this.ante = ante;
    this.cons = cons;
    this.tstore = tstore;
    this.rstore = rstore;
  }

  /**
   *
   */
  public String getName() {
    return this.name;
  }

  /**
   *
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   *
   */
  public void setAntecedent(int[][] ante) {
    this.ante = ante;
  }

  /**
   * returns the internal representation of the antecedent of a rule
   */
  public int[][] getAntecedent() {
    return this.ante;
  }

  /**
   *
   */
  public void setAntecedent(ArrayList<int[]> anteList) {
    int[][] anteArray = new int[anteList.size()][];
    for (int i = 0; i < anteList.size(); i++)
      anteArray[i] = anteList.get(i);
    this.ante = anteArray;
  }

  /**
   * returns the ith clause of the antecedent of a rule;
   * no checks are performed whether index i is valid
   */
  public int[] getAntecedent(int i) {
    return this.ante[i];
  }

  /**
   *
   */
  public void setConsequent(int[][] cons) {
    this.cons = cons;
  }

  /**
   * returns the internal representation of the consequent of a rule
   */
  public int[][] getConsequent() {
    return this.cons;
  }

  /**
   *
   */
  public void setConsequent(ArrayList<int[]> consList) {
    int[][] consArray = new int[consList.size()][];
    for (int i = 0; i < consList.size(); i++)
      consArray[i] = consList.get(i);
    this.cons = consArray;
  }

  /**
   * returns the ith clause of the consequent of a rule;
   * no checks are performed whether index i is valid
   */
  public int[] getConsequent(int i) {
    return this.cons[i];
  }

  /**
   * returns a reference to the TupleStore object, used for decoding by the
   * toString() method
   */
  public TupleStore getTupleStore() {
    return this.tstore;
  }

  /**
   *
   */
  public void setTupleStore(TupleStore tstore) {
    this.tstore = tstore;
  }

  /**
   * returns a reference to _this_ RuleStore object
   */
  public RuleStore getRuleStore() {
    return rstore;
  }

  /**
   *
   */
  public void setRuleStore(RuleStore rstore) {
    this.rstore = rstore;
  }


  /**
   * to have a proper working toString() method, a rule must be associated with
   * its tuple store to perform external decoding
   */
    public String toString() {
    return toString(this.tstore);
  }

  /**
   * use this method if tuple store field has NOT been set for _this_ tuple
   * NOTE: printer is incomplete as it misses general tests and actions
   */
  public String toString(TupleStore store) {
    StringBuilder sb = new StringBuilder();
    sb.append(getName()).append("\n");
    for (int[] tuple : ante)
      sb.append(store.toString(tuple, false)).append("\n");
    sb.append("->\n");
    for (int[] tuple : cons)
      sb.append(store.toString(tuple, false)).append("\n");
    sb.append("@test\n");
    for (int i = 0; i < inEqConstraints.size(); i++) {
      sb.append(store.getObject(inEqConstraints.get(i)));
      sb.append(" != ");
      sb.append(store.getObject(inEqConstraints.get(++i)));
      sb.append("\n");
    }
    // NOTE: general tests and actions are not printed at the moment
    return sb.toString();
  }


}
