package de.dfki.lt.hfc.operators;

import de.dfki.lt.hfc.RelationalOperator;
import de.dfki.lt.hfc.BindingTable;

/**
 * prints the content of the binding tables given to apply() to standard out
 * @return args, the input arguments given to apply() without modifying them
 *
 * @see RelationalOperator
 *
 * @author (C) Hans-Ulrich Krieger
 * @since JDK 1.5
 * @version Fri Mar 15 16:20:24 CET 2013
 */
public final class PrintContent extends RelationalOperator {
	
	/**
	 * 
	 */
	public BindingTable[] apply(BindingTable[] args) {
		for (BindingTable arg : args) {
		  System.out.println(arg.toString());
			System.out.flush();
		}
		return args;
	}
	
}
