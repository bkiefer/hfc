package de.dfki.lt.hfc;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * the abstract superclass of the two classes Predicate and Function
 *
 * @author (C) Hans-Ulrich Krieger
 * @version Tue Mar  5 16:59:51 CET 2013
 * @since JDK 1.5
 */
public abstract class Relation {

  /**
   * the name of the relation instance
   */
  public String name;

  /**
   * the functional or relational operator for this object, given the fully-qualified
   * relation/Java class name
   *
   * @see name
   */
  public Operator op;

  /**
   * the arguments to the relation are either variables, URIs, or XSD atoms;
   * variables are represented by negative integers, all other objects by
   * positive ints
   */
  public int[] args;

  /**
   * pos is used to map the args array (variable names & constants) to an
   * array where the variable names are replaced by the positions in the
   * binding table to which the relation is applied;
   * note that this mapping must be established w.r.t. to such a table and
   * is only valid when this relation is applied;
   * note further that the positions are encoded by _negative_ ints in order
   * to distinguish them from the literals (positive ints)
   */
  public int[] pos;

  /**
   * represents a mapping from relational ids to their functional counterparts;
   * note that relational variables in tests and actions might even be complex,
   * leading to the array list in this data structure;
   * note that brand-new unique IDs are already assigned to complex relational
   * variables, such as ??(var1 ... varN)
   * id(??var) -> [id(?var)]
   * id(??(var1 ... varN)) -> [id(var1), ..., id(?varN)]
   */
  public HashMap<Integer, ArrayList<Integer>> relIdToFunIds =
          new HashMap<Integer, ArrayList<Integer>>();

  /**
   * needed by predicates (@test) and functions (@action) to properly access the
   * content of binding tables when using (complex) relational variables (??-vars)
   */
  public HashMap<String, Integer> varToId;

  /**
   *
   */
  public Relation(String name, ArrayList<Integer> args) {
    this.name = name;
    this.args = new int[args.size()];
    for (int i = 0; i < args.size(); i++)
      this.args[i] = args.get(i);
  }

  /**
   *
   */
  public Relation(String name, Operator op, ArrayList<Integer> args) {
    this(name, args);
    this.op = op;
  }

  /**
   *
   */
  public Relation(String name,
                  Operator op,
                  ArrayList<Integer> args,
                  HashMap<Integer, ArrayList<Integer>> relIdToFunIds) {
    this(name, op, args);
    this.relIdToFunIds = relIdToFunIds;
  }

  /**
   *
   */
  public Relation(String name,
                  Operator op,
                  ArrayList<Integer> args,
                  HashMap<Integer, ArrayList<Integer>> relIdToFunIds,
                  HashMap<String, Integer> varToId) {
    this(name, op, args, relIdToFunIds);
    this.varToId = varToId;
  }

}
