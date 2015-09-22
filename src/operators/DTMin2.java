package de.dfki.lt.hfc.operators;

import de.dfki.lt.hfc.FunctionalOperator;

/**
 * computes the minimum of the two (2) arguments given to apply();
 * we do NOT check whether there are less than or more than two arguments;
 * arguments are assumed to be of type <xsd:dateTime>;
 * returns the minimal dateTime instant
 *
 * @see FunctionalOperator
 *
 * @author (C) Hans-Ulrich Krieger
 * @since JDK 1.5
 * @version Wed Jun 22 17:15:17 CEST 2011
 */
public final class DTMin2 extends FunctionalOperator {
	
	/**
	 * note that apply() does NOT check at the moment whether the int args
	 * represent in fact XSD dateTime instants;
	 * note: no need to register the result, since the minimum is one of the two arguments
	 */
	public int apply(int[] args) {
		// note: we call DTLess through the use of callFuntionalOperator() in order to define DTMin2
		final int result = callFunctionalOperator("DTLess", args);
		if (result == FunctionalOperator.TRUE)
			return args[0];
		else
			return args[1];
	}
	
}
