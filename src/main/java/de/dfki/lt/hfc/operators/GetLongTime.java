package de.dfki.lt.hfc.operators;

import de.dfki.lt.hfc.FunctionalOperator;
import de.dfki.lt.hfc.types.XsdLong;

/**
 * returns the current time encoded as an xsd:long
 *
 * @see http://www.w3.org/TR/xmlschema-2/
 *
 * @see de.dfki.lt.hfc.FunctionalOperator
 * @see de.dfki.lt.hfc.types.XsdDateTime
 *
 * @author (C) Hans-Ulrich Krieger
 * @since JDK 1.5
 * @version Tue Sep 29 11:11:19 CEST 2009
 */
public final class GetLongTime extends FunctionalOperator {
	
	/**
	 * none of the args are considered -- in fact, there should be NO args;
	 * returns the difference between current time and midnight, January 1, 1970 UTC,
	 * represented as an xsd:long, represented by an int identifier
	 */
	public int apply(int[] args) {
		XsdLong time = new XsdLong(System.currentTimeMillis());
		return registerObject(time.toString(this.tupleStore.namespace.shortIsDefault), time);
	}
	
}
