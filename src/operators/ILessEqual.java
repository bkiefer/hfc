package de.dfki.lt.hfc.operators;

import de.dfki.lt.hfc.FunctionalOperator;
import de.dfki.lt.hfc.types.XsdInt;

/**
 * checks whether the first argument is less or equal than the second argument;
 * arguments are assumed to be xsd:ints;
 * @return FunctionalOperator.TRUE or FunctionalOperator.FALSE
 *
 * @see FunctionalOperator
 *
 * @author (C) Hans-Ulrich Krieger
 * @since JDK 1.5
 * @version Tue Sep 29 11:11:19 CEST 2009
 */
public final class ILessEqual extends FunctionalOperator {
	
	/**
	 * note that apply() does NOT check at the moment whether the int args
	 * represent in fact XSD ints;
	 * note that apply() does NOT check whether it is given exactly two arguments
	 */
	public int apply(int[] args) {
		return ((XsdInt)getObject(args[0])).value <= ((XsdInt)getObject(args[1])).value ? FunctionalOperator.TRUE : FunctionalOperator.FALSE;
	}
	
}
