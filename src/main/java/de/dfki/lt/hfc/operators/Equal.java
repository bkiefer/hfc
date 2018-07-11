package de.dfki.lt.hfc.operators;

import de.dfki.lt.hfc.FunctionalOperator;

/**
 * checks whether the first argument is equal to the second argument;
 * arguments are assumed to be numbers of type xsd:int
 *
 * @return FunctionalOperator.TRUE or FunctionalOperator.FALSE
 *
 * @see FunctionalOperator
 *
 * @author (C) Hans-Ulrich Krieger
 * @since JDK 1.5
 * @version Wed Jun 23 11:04:27 CEST 2010
 */
public final class Equal extends FunctionalOperator {

	/**
	 * note that apply() does NOT check at the moment whether the int args
	 * represent in fact XSD ints;
	 * note that apply() does NOT check whether it is given exactly two arguments
	 */
  public int apply(int[] args) {
    return getObject(args[0]).compareTo(getObject(args[1])) == 0
        ? FunctionalOperator.TRUE : FunctionalOperator.FALSE;
	}

}
