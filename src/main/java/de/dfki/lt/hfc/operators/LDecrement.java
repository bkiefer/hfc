package de.dfki.lt.hfc.operators;

import de.dfki.lt.hfc.FunctionalOperator;
import de.dfki.lt.hfc.types.XsdLong;

/**
 * computes the decrement of the single argument given to apply();
 * argument is assumed to be an xsd:long;
 * returns a representation of the new long
 *
 * @see FunctionalOperator
 *
 * @author (C) Hans-Ulrich Krieger
 * @since JDK 1.5
 * @version Tue Sep 29 11:11:19 CEST 2009
 */
public final class LDecrement extends FunctionalOperator {
	
	/**
	 * note that apply() does NOT check at the moment whether the int arg
	 * represent in fact an XSD long;
	 * note that apply() does NOT check whether it is given exactly one argument
	 */
	public int apply(int[] args) {
		long l = ((XsdLong)getObject(args[0])).value - 1;
		XsdLong L = new XsdLong(l);
		return registerObject(L.toString(this.tupleStore.namespace.shortIsDefault), L);
	}
	
}
