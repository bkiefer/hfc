package de.dfki.lt.hfc.operators;

import de.dfki.lt.hfc.RelationalOperator;
import de.dfki.lt.hfc.BindingTable;

/**
 * prints the size of the arguments (binding tables) given to apply() to standard out
 * @return args, the input arguments given to apply() without modifying them
 *
 * @see RelationalOperator
 *
 * @author (C) Hans-Ulrich Krieger
 * @since JDK 1.5
 * @version Wed Mar 13 18:12:56 CET 2013
 */
public final class PrintSize extends RelationalOperator {
	
	/**
	 * 
	 */
	public BindingTable[] apply(BindingTable[] args) {
		for (int i = 0; i < args.length; i++)
			System.out.print(args[i].table.size() + " ");
		System.out.println();
		return args;
	}
	
}
