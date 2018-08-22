package de.dfki.lt.hfc;

import gnu.trove.set.hash.TCustomHashSet;

import java.util.ArrayList;
import java.util.Set;

/**
 * information that is shared between equivalent patterns, even across rules
 *
 * @author (C) Hans-Ulrich Krieger
 * @version Thu Jan 14 14:57:24 CET 2010
 * @since JDK 1.5
 */
class Proxy {

  /**
   * the query result from the current iteration
   */
  protected Set<int[]> table;

  /**
   * the query result from the last iteration
   */
  protected Set<int[]> old;

  /**
   * the difference ("delta") that is given by set difference between new and old query result
   */
  protected Set<int[]> delta;

  /**
   * needed for clause/proxy resharing;
   *
   * @see Table.getGeneration()
   * @see ForwardChainer.executeLocalMatch()
   */
  protected int generation = 0;

  /**
   * encodes positions of (proper) duplicate variables in a local clause;
   * e.g., <?s, ?t, ?u, ?t, ?s> leads to the following structure: [[0, 4], [1, 3]];
   * note that the sub-arrays are always *SORTED*;
   * this information is
   */
  protected int[][] equalPositions;

  /**
   * duplicates are counted only once, using their FIRST occurrence;
   * note that the int array is always *SORTED*;
   * e.g., assuming that ?_ is a don't care variable, the proper positions for
   * for <?s, ?_, ?s> are [0] (the first occurrence is used);
   * same for <?s, a, ?s>, assuming a is an atom: [0]
   */
  protected int[] properPositions;

  /**
   * proper variables are counted by their occurrence;
   * note that the int array is always *SORTED*;
   * e.g., assuming that ?_ is a don't care variable, the relevant positions
   * for <?s, ?_, ?s> are [0, 2];
   * same for <?s, a, ?s>, assuming a is an atom: [0, 2]
   */
  protected int[] relevantPositions;

  /**
   * the equals and hash code strategy for the sets stored in table and delta
   */
  protected TIntArrayHashingStrategy strategy;

  /**
   * only for the use in TupleStore.query()
   */
  protected Proxy(ArrayList<Integer> relevantPositions,
                  ArrayList<ArrayList<Integer>> equalPositions) {
    this.relevantPositions = new int[relevantPositions.size()];
    for (int i = 0; i < relevantPositions.size(); i++)
      this.relevantPositions[i] = relevantPositions.get(i);
    this.equalPositions = new int[equalPositions.size()][];
    ArrayList<Integer> list;
    int[] array;
    for (int i = 0; i < equalPositions.size(); i++) {
      list = equalPositions.get(i);
      array = new int[list.size()];
      this.equalPositions[i] = array;
      for (int j = 0; j < list.size(); j++)
        array[j] = list.get(j);
    }
  }

  /**
   * set instance fields to the parameter values;
   * note: generation field uses default value 0
   */
  protected Proxy(Set<int[]> table,
                  Set<int[]> old,
                  Set<int[]> delta,
                  int[][] equalPositions,
                  int[] properPositions,
                  int[] relevantPositions,
                  TIntArrayHashingStrategy strategy) {
    this.table = table;
    this.old = old;
    this.delta = delta;
    this.equalPositions = equalPositions;
    this.properPositions = properPositions;
    this.relevantPositions = relevantPositions;
    this.strategy = strategy;
  }

  /**
   * copy contructor for copyRuleStore();
   * table, old, and delta are shallow copied, but everything else is taken over (incl. generation)
   */
  protected Proxy(Proxy proxy) {
    this.table = new TCustomHashSet<int[]>(proxy.strategy, proxy.table);
    if (proxy.old == null)
      this.old = null;
    else
      this.old = new TCustomHashSet<int[]>(proxy.strategy, proxy.old);
    if (proxy.delta == null)
      this.delta = null;
    else
      this.delta = new TCustomHashSet<int[]>(proxy.strategy, proxy.delta);
    this.generation = proxy.generation;
    this.equalPositions = proxy.equalPositions;
    this.properPositions = proxy.properPositions;
    this.relevantPositions = proxy.relevantPositions;
    this.strategy = proxy.strategy;
  }

}
