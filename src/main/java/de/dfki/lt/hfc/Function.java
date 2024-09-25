package de.dfki.lt.hfc;

import java.util.ArrayList;

/**
 * the internal representation of a function as used in the '@action'
 * section of the forward chainer
 *
 * @author (C) Hans-Ulrich Krieger
 * @version Thu Nov 12 16:12:07 CET 2009
 * @since JDK 1.5
 */
public class Function extends Relation {

  /**
   * the result of a function call is always bound to a variable, as
   * given by result (a negative integer)
   */
  public int result;

  /**
   *
   */
  public Function(int result,
                  String name,
                  ArrayList<Integer> args) {
    super(name, args);
    this.result = result;
  }

  /**
   *
   */
  public Function(int result,
                  String name,
                  Operator op,
                  ArrayList<Integer> args) {
    super(name, op, args);
    this.result = result;
  }

}
