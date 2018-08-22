package de.dfki.lt.hfc;

import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * aggregates are represented by their name and a sequence of SELECT
 * variables (negative ints) or constants (positive ints)
 *
 * @author (C) Hans-Ulrich Krieger
 * @version Fri Nov 20 12:15:52 CET 2009
 * @since JDK 1.5
 */
public class Aggregate {

  /**
   * the name of the aggregate
   */
  public String name;

  /**
   * the binder variable names, different from args vars (= subset of select vars)
   */
  public String[] vars;

  /**
   * the variables/constants given to the aggregate call are stored here
   */
  public int[] args;

  /**
   * computed from this.vars
   */
  public SortedMap<Integer, Integer> nameToPos = new TreeMap<Integer, Integer>();

  /**
   * computed from this.vars
   */
  protected Map<Integer, String> nameToExternalName = new HashMap<Integer, String>();

  /**
   *
   */
  public Aggregate(String name, String[] vars, int[] args) {
    this.name = name;
    this.vars = vars;
    this.args = args;
    for (int i = 0; i < vars.length; i++) {
      // establish mappings only once
      nameToPos.put(-i - 1, i);
      nameToExternalName.put(-i - 1, vars[i]);
    }
  }

}
