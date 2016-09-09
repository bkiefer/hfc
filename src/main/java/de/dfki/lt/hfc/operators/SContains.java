package de.dfki.lt.hfc.operators;

import de.dfki.lt.hfc.FunctionalOperator;
import de.dfki.lt.hfc.types.XsdString;

/**
 * checks whether the first argument contains the second argument;
 * arguments are assumed to be strings of type xsd:string
 *
 * @see FunctionalOperator
 *
 * @author (C) Hans-Ulrich Krieger
 * @since JDK 1.5
 * @version Wed Jun 23 11:04:27 CEST 2010
 */
public final class SContains extends FunctionalOperator {

	/**
	 * note that apply() does NOT check at the moment whether the int args
   * represent in fact XSD strings (and not URIs, XSD ints, etc.);
   * note that apply() does NOT check whether it is given exactly two arguments
   *
   * @return FunctionalOperator.TRUE or FunctionalOperator.FALSE
   */
	public int apply(int[] args) {
		if (getObject(args[0]).toString()
        .contains((String)((XsdString)getObject(args[1])).toJava()))
			return FunctionalOperator.TRUE;
		else
			return FunctionalOperator.FALSE;
	}

}
