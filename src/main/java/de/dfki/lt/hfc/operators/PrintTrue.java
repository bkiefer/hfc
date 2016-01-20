package de.dfki.lt.hfc.operators;

import de.dfki.lt.hfc.FunctionalOperator;

/**
 * prints the arguments given to apply() to standard out
 * @return FunctionalOperator.TRUE
 *
 * @see FunctionalOperator
 *
 * @author (C) Hans-Ulrich Krieger
 * @since JDK 1.5
 * @version Tue Sep 29 11:11:19 CEST 2009
 */
public final class PrintTrue extends FunctionalOperator {
	
	/**
	 * 
	 */
	public int apply(int[] args) {
		for (int i = 0; i < args.length; i++)
			System.out.print(getExternalRepresentation(args[i]) + " ");
		System.out.println();
		return FunctionalOperator.TRUE;
	}
	
}
