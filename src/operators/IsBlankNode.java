package de.dfki.lt.hfc.operators;

import de.dfki.lt.hfc.FunctionalOperator;
import de.dfki.lt.hfc.types.*;

/**
 * checks whether the first argument given to IsBlankNode is a blank node
 *
 * @see de.dfki.lt.hfc.types.BlankNode
 *
 * @author (C) Hans-Ulrich Krieger
 * @since JDK 1.5
 * @version Thu Dec  9 14:01:09 CET 2010
 */
public final class IsBlankNode extends FunctionalOperator {
	
	/**
	 *
	 */
	public int apply(int[] args) {
		return (getObject(args[0]) instanceof BlankNode) ? FunctionalOperator.TRUE : FunctionalOperator.FALSE;
	}
	
}
