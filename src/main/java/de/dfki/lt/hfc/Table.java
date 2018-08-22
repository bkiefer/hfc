package de.dfki.lt.hfc;

import gnu.trove.set.hash.TCustomHashSet;

import java.util.*;

/**
 * an auxiliary class that hides query/binding tables (sets of tuples: Set<int[]>)
 * and so-called "delta", subsets of the tables, encoding only the new information
 * from the last iteration step
 *
 * @author (C) Hans-Ulrich Krieger
 * @version Thu Jan 14 14:57:24 CET 2010
 * @since JDK 1.5
 */
class Table {

  /**
   *
   */
  protected HashSet<Integer> properVariables;

  /**
   *
   */
  protected HashSet<Integer> dontCareVariables;

  /**
   * important to record duplicate variables in tuples, e.g., <?x, a, ?x>,
   * since the index does NOT enforce identical values in 1st and 3rd position;
   * this needs to be checked separately!
   */
  protected HashMap<Integer, ArrayList<Integer>> nameToPos;

  /**
   * is used during global clause matching using joins;
   * only takes the first occurrence of a variable into account
   */
  protected TreeMap<Integer, Integer> nameToPosProper;

  /**
   *
   */
  protected HashMap<Integer, Integer> posToName;

  /**
   * hide table, delta, and generation in a proxy to allow clause-resharing, even across rules
   */
  protected Proxy proxy;

  /**
   *
   */
  protected Table() {
    this.properVariables = new HashSet<Integer>();
    this.dontCareVariables = new HashSet<Integer>();
    this.nameToPos = new HashMap<Integer, ArrayList<Integer>>();
    this.nameToPosProper = new TreeMap<Integer, Integer>();
    this.posToName = new HashMap<Integer, Integer>();
    this.proxy = new Proxy(new TCustomHashSet<int[]>(TupleStore.DEFAULT_HASHING_STRATEGY),
            new TCustomHashSet<int[]>(TupleStore.DEFAULT_HASHING_STRATEGY),
            new TCustomHashSet<int[]>(TupleStore.DEFAULT_HASHING_STRATEGY),
            null, null, null, null);
  }

  /**
   * copy constructor for exclusive use in the copy constructor of Rule (needed
   * when copying a rule store)
   *
   * @see Rule()
   * @see copyRuleStore()
   */
  protected Table(Table oldTable, Proxy newProxy) {
    this.properVariables = oldTable.properVariables;
    this.dontCareVariables = oldTable.dontCareVariables;
    this.nameToPos = oldTable.nameToPos;
    this.nameToPosProper = oldTable.nameToPosProper;
    this.posToName = oldTable.posToName;
    this.proxy = newProxy;
  }

  /**
   * only for the use in TupleStore.query()
   */
  protected Table(HashSet<Integer> properVariables,
                  HashSet<Integer> dontCareVariables,
                  ArrayList<Integer> relevantPositions,
                  ArrayList<ArrayList<Integer>> equalPositions,
                  HashMap<Integer, ArrayList<Integer>> nameToPos) {
    this.properVariables = properVariables;
    this.dontCareVariables = dontCareVariables;
    this.proxy = new Proxy(relevantPositions, equalPositions);
    this.nameToPos = nameToPos;
    this.nameToPosProper = new TreeMap<Integer, Integer>();
    for (Integer name : nameToPos.keySet())
      nameToPosProper.put(name, nameToPos.get(name).get(0));
  }

  /**
   *
   */
  protected Table(HashSet<Integer> properVariables,
                  HashSet<Integer> dontCareVariables,
                  HashMap<Integer, ArrayList<Integer>> nameToPos,
                  TreeMap<Integer, Integer> nameToPosProper,
                  HashMap<Integer, Integer> posToName,
                  Set<int[]> table,
                  Set<int[]> old,
                  Set<int[]> delta) {
    this.properVariables = properVariables;
    this.dontCareVariables = dontCareVariables;
    this.nameToPos = nameToPos;
    this.nameToPosProper = nameToPosProper;
    this.posToName = posToName;
    this.proxy = new Proxy(table, old, delta, null, null, null, null);
  }

  /**
   *
   */
  protected Table(HashSet<Integer> properVariables,
                  HashSet<Integer> dontCareVariables,
                  HashMap<Integer, ArrayList<Integer>> nameToPos,
                  TreeMap<Integer, Integer> nameToPosProper,
                  HashMap<Integer, Integer> posToName,
                  Proxy proxy) {
    this.properVariables = properVariables;
    this.dontCareVariables = dontCareVariables;
    this.nameToPos = nameToPos;
    this.nameToPosProper = nameToPosProper;
    this.posToName = posToName;
    this.proxy = proxy;
  }

  /**
   *
   */
  protected Set<int[]> getTable() {
    return this.proxy.table;
  }

  /**
   *
   */
  protected void setTable(Set<int[]> table) {
    this.proxy.table = table;
  }

  /**
   *
   */
  protected Set<int[]> getOld() {
    return this.proxy.old;
  }

  /**
   *
   */
  protected void setOld(Set<int[]> old) {
    this.proxy.old = old;
  }

  /**
   *
   */
  protected Set<int[]> getDelta() {
    return this.proxy.delta;
  }

  /**
   *
   */
  protected void setDelta(Set<int[]> delta) {
    this.proxy.delta = delta;
  }

  /**
   *
   */
  protected int getGeneration() {
    return this.proxy.generation;
  }

  /**
   *
   */
  protected void setGeneration(int generation) {
    this.proxy.generation = generation;
  }

  /**
   *
   */
  protected int[][] getEqualPositions() {
    return this.proxy.equalPositions;
  }

  /**
   *
   */
  protected void setEqualPositions(int[][] equalPositions) {
    this.proxy.equalPositions = equalPositions;
  }

  /**
   *
   */
  protected int[] getProperPositions() {
    return this.proxy.properPositions;
  }

  /**
   *
   */
  protected void setProperPositions(int[] properPositions) {
    this.proxy.properPositions = properPositions;
  }

  /**
   *
   */
  protected int[] getRelevantPositions() {
    return this.proxy.relevantPositions;
  }

  /**
   *
   */
  protected void setRelevantPositions(int[] relevantPositions) {
    this.proxy.relevantPositions = relevantPositions;
  }


  /**
   *
   */
  protected TIntArrayHashingStrategy getStrategy() {
    return this.proxy.strategy;
  }

  /**
   *
   */
  protected void setStrategy(TIntArrayHashingStrategy strategy) {
    this.proxy.strategy = strategy;
  }

}
