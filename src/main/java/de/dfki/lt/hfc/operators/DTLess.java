package de.dfki.lt.hfc.operators;

import de.dfki.lt.hfc.FunctionalOperator;
import de.dfki.lt.hfc.types.XsdDateTime;

/**
 * checks whether the first argument is less than the second argument;
 * arguments are assumed to be of type XsdDateTime;
 * @return FunctionalOperator.TRUE or FunctionalOperator.FALSE
 *
 * @see FunctionalOperator
 *
 * @author (C) Hans-Ulrich Krieger
 * @since JDK 1.5
 * @version Fri May 20 15:49:48 CEST 2011
 */
public final class DTLess extends FunctionalOperator {
	
	/**
	 * note that apply() does NOT check at the moment whether the int args are in fact
	 * of type XsdDateTime;
	 * note that apply() does NOT check whether it is given exactly two arguments;
	 * contrary to XsdUnDateTime, a dateTime object is always _fully_ specified
	 */
	public int apply(int[] args) {
		XsdDateTime firstArg = (XsdDateTime)getObject(args[0]);
		XsdDateTime secondArg = (XsdDateTime)getObject(args[1]);
		// year (int)
		if (firstArg.year < secondArg.year)
			return FunctionalOperator.TRUE;
		if (firstArg.year > secondArg.year)
			return FunctionalOperator.FALSE;
		//month (int)
		if (firstArg.month < secondArg.month)
			return FunctionalOperator.TRUE;
		if (firstArg.month > secondArg.month)
			return FunctionalOperator.FALSE;
		// day (int)
		if (firstArg.day < secondArg.day)
			return FunctionalOperator.TRUE;
		if (firstArg.day > secondArg.day)
			return FunctionalOperator.FALSE;
		// hour (int)
		if (firstArg.hour < secondArg.hour)
			return FunctionalOperator.TRUE;
		if (firstArg.hour > secondArg.hour)
			return FunctionalOperator.FALSE;
		// minute (int)
		if (firstArg.minute < secondArg.minute)
			return FunctionalOperator.TRUE;
		if (firstArg.minute > secondArg.minute)
			return FunctionalOperator.FALSE;
		// second (float)
		if (firstArg.second < secondArg.second)
			return FunctionalOperator.TRUE;
		return FunctionalOperator.FALSE;
	}
	
}