package de.dfki.lt.hfc.operators;

import de.dfki.lt.hfc.BooleanOperator;
import de.dfki.lt.hfc.types.XsdLong;

/**
 * checks whether the first argument is not equal to the second argument;
 * arguments are assumed to be numbers of type xsd:long
 *
 * @return FunctionalOperator.TRUE or FunctionalOperator.FALSE
 *
 * @see FunctionalOperator
 *
 * @author (C) Hans-Ulrich Krieger
 * @since JDK 1.5
 * @version Wed Jun 23 11:04:27 CEST 2010
 */
public final class LNotEqual extends BooleanOperator {

	/**
	 * note that apply() does NOT check at the moment whether the int args
	 * represent in fact XSD longs;
	 * note that apply() does NOT check whether it is given exactly two arguments
	 */
	protected boolean holds(int[] args) {
		return (((XsdLong)getObject(args[0])).value
		    != ((XsdLong)getObject(args[1])).value);
	}

}
