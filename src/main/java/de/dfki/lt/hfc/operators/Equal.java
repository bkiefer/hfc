package de.dfki.lt.hfc.operators;

import de.dfki.lt.hfc.BooleanOperator;

/**
 * checks whether the first argument is equal to the second argument;
 *
 * @author (C) Hans-Ulrich Krieger
 * @version Wed Jun 23 11:04:27 CEST 2010
 * @return true or false
 * @see BooleanOperator
 * @since JDK 1.5
 */
public final class Equal extends BooleanOperator {

  /**
   * Make sure the types for the comparison match
   * note that apply() does NOT check whether it is given exactly two arguments
   */
  @SuppressWarnings("unchecked")
  protected boolean holds(int[] args) {
    return getObject(args[0]).compareTo(getObject(args[1])) == 0;
  }

}
