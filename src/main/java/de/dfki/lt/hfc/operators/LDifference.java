package de.dfki.lt.hfc.operators;

import de.dfki.lt.hfc.FunctionalOperator;
import de.dfki.lt.hfc.types.XsdLong;

/**
 * computes the difference of the two arguments given to apply();
 * arguments are assumed to be of type xsd:long;
 * returns a representation of the new long
 *
 * @see FunctionalOperator
 *
 * @author (C) Hans-Ulrich Krieger
 * @since JDK 1.5
 * @version Tue Sep 29 11:11:19 CEST 2009
 */
public final class LDifference extends FunctionalOperator {
	
	/**
	 * note that apply() does NOT check at the moment whether the int args
	 * represent in fact XSD longs;
	 * note that apply() does NOT check whether it is given exactly two arguments
	 */
	public int apply(int[] args) {
		long l = ((XsdLong)getObject(args[0])).value - ((XsdLong)getObject(args[1])).value;
		XsdLong L = new XsdLong(l);
		return registerObject(L.toString(de.dfki.lt.hfc.Namespace.shortIsDefault), L);
	}
	
}
