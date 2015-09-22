package de.dfki.lt.hfc.operators;

import de.dfki.lt.hfc.FunctionalOperator;
import de.dfki.lt.hfc.types.*;

/**
 * checks whether the first argument given to IsAtom is an XSD atom
 *
 * @see de.dfki.lt.hfc.types.Xsd
 *
 * @author (C) Hans-Ulrich Krieger
 * @since JDK 1.5
 * @version Thu Dec  9 14:01:09 CET 2010
 */
public final class IsAtom extends FunctionalOperator {
	
	/**
	 *
	 */
	public int apply(int[] args) {
		return (getObject(args[0]) instanceof XsdAnySimpleType) ? FunctionalOperator.TRUE : FunctionalOperator.FALSE;
	}
	
}
