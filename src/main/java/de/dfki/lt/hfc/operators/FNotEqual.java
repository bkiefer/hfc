package de.dfki.lt.hfc.operators;

import de.dfki.lt.hfc.FunctionalOperator;
import de.dfki.lt.hfc.types.XsdFloat;

/**
 * checks whether the first argument is not equal to the second argument;
 * arguments are assumed to be numbers of type xsd:float
 *
 * @return FunctionalOperator.TRUE or FunctionalOperator.FALSE
 *
 * @see FunctionalOperator
 *
 * @author (C) Hans-Ulrich Krieger
 * @since JDK 1.5
 * @version Wed Jun 23 15:19:56 CEST 2010
 */
public final class FNotEqual extends FunctionalOperator {

	/**
	 * note that apply() does NOT check at the moment whether the int args
	 * represent in fact XSD floats;
	 * note that apply() does NOT check whether it is given exactly two arguments
	 */
	public int apply(int[] args) {
		if (Float.compare(
        ((XsdFloat)getObject(args[0])).value,
        ((XsdFloat)getObject(args[1])).value) != 0)
			return FunctionalOperator.TRUE;
		else
			return FunctionalOperator.FALSE;
	}

}
