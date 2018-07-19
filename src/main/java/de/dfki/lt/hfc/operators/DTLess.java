package de.dfki.lt.hfc.operators;

import de.dfki.lt.hfc.BooleanOperator;
import de.dfki.lt.hfc.types.XsdDateTime;

/**
 * checks whether the first argument is less than the second argument;
 * arguments are assumed to be of type XsdDateTime;
 *
 * @see BooleanOperator
 *
 * @author (C) Hans-Ulrich Krieger
 * @since JDK 1.5
 * @version Fri May 20 15:49:48 CEST 2011
 */
public final class DTLess extends BooleanOperator {

	/**
	 * note that apply() does NOT check at the moment whether the int args are in fact
	 * of type XsdDateTime;
	 * note that apply() does NOT check whether it is given exactly two arguments;
	 * contrary to XsdUnDateTime, a dateTime object is always _fully_ specified
   *
   * @return true or false
	 */
  protected boolean holds(int[] args) {
		XsdDateTime firstArg = (XsdDateTime)getObject(args[0]);
		XsdDateTime secondArg = (XsdDateTime)getObject(args[1]);
		// year (int)
		if (firstArg.year < secondArg.year)
			return true;
		if (firstArg.year > secondArg.year)
			return false;
		//month (int)
		if (firstArg.month < secondArg.month)
			return true;
		if (firstArg.month > secondArg.month)
			return false;
		// day (int)
		if (firstArg.day < secondArg.day)
			return true;
		if (firstArg.day > secondArg.day)
			return false;
		// hour (int)
		if (firstArg.hour < secondArg.hour)
			return true;
		if (firstArg.hour > secondArg.hour)
			return false;
		// minute (int)
		if (firstArg.minute < secondArg.minute)
			return true;
		if (firstArg.minute > secondArg.minute)
			return false;
		// second (float)
		if (firstArg.second < secondArg.second)
			return true;
		return false;
	}

}
