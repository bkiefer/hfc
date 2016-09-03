package de.dfki.lt.hfc.operators;

import de.dfki.lt.hfc.FunctionalOperator;
import de.dfki.lt.hfc.types.XsdInt;

/**
 * checks whether the first argument is not equal to the second argument;
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
public final class INotEqual extends FunctionalOperator {

	/**
	 * note that apply() does NOT check at the moment whether the int args
	 * represent in fact XSD ints;
	 * note that apply() does NOT check whether it is given exactly two arguments
	 */
	public int apply(int[] args) {
		if (((XsdInt)getObject(args[0])).value != ((XsdInt)getObject(args[1])).value)
			return FunctionalOperator.TRUE;
		else
			return FunctionalOperator.FALSE;
	}

}
