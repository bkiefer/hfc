package de.dfki.lt.hfc.operators;

import de.dfki.lt.hfc.BooleanOperator;

/**
 * checks whether the first argument is less or equal than the second argument;
 * @return true or false
 *
 * @see BooleanOperator
 *
 * @author (C) Hans-Ulrich Krieger
 * @since JDK 1.5
 * @version Tue Sep 29 11:11:19 CEST 2009
 */
public final class LessEqual extends BooleanOperator {

	/** Make sure the types for the comparison match
	 * note that apply() does NOT check whether it is given exactly two arguments
	 */
	@SuppressWarnings("unchecked")
  protected boolean holds(int[] args) {
		return getObject(args[0]).compareTo(getObject(args[1])) <= 0;
	}

}
