package de.dfki.lt.hfc.operators;

import de.dfki.lt.hfc.FunctionalOperator;

/**
 * checks whether the first argument is greater than the second argument;
 * arguments are assumed to be xsd:longs;
 * @return FunctionalOperator.TRUE or FunctionalOperator.FALSE
 *
 * @see FunctionalOperator
 *
 * @author (C) Hans-Ulrich Krieger
 * @since JDK 1.5
 * @version Tue Sep 29 11:11:19 CEST 2009
 */
public final class Greater extends FunctionalOperator {

	/**
	 * note that apply() does NOT check at the moment whether the int args
	 * represent in fact XSD longs;
	 * note that apply() does NOT check whether it is given exactly two arguments
	 */
	public int apply(int[] args) {
		return getObject(args[0]).compareTo(getObject(args[1])) > 0
		    ? FunctionalOperator.TRUE : FunctionalOperator.FALSE;
	}

}
