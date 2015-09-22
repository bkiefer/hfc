package de.dfki.lt.hfc.operators;

import de.dfki.lt.hfc.FunctionalOperator;
import de.dfki.lt.hfc.types.XsdFloat;

/**
 * computes the decrement of the single argument given to apply();
 * argument is assumed to be an xsd:float;
 * returns a representation of the new float
 *
 * @see FunctionalOperator
 *
 * @author (C) Hans-Ulrich Krieger
 * @since JDK 1.5
 * @version Tue Sep 29 11:11:19 CEST 2009
 */
public final class FDecrement extends FunctionalOperator {
	
	/**
	 * note that apply() does NOT check at the moment whether the int arg
	 * represent in fact an XSD float;
	 * note that apply() does NOT check whether it is given exactly one argument
	 */
	public int apply(int[] args) {
		float f = ((XsdFloat)getObject(args[0])).value - 1;
		XsdFloat F = new XsdFloat(f);
		return registerObject(F.toString(de.dfki.lt.hfc.Namespace.shortIsDefault), F);
	}
	
}
