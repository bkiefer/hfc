package de.dfki.lt.hfc.operators;

import de.dfki.lt.hfc.BooleanOperator;

/**
 * checks whether the first argument is equals to the second argument;
 *
 * @see BooleanOperator
 *
 * @author (C) Hans-Ulrich Krieger
 * @since JDK 1.5
 * @version Wed Jun 23 11:04:27 CEST 2010
 */
public final class Equals extends BooleanOperator {

	/**
   * note that apply() does NOT check whether it is given exactly two arguments
   *
   * @return true or false
   */
  protected boolean holds(int[] args) {
	  return getObject(args[0]).equals(getObject(args[1]));
	}

}
