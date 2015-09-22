package de.dfki.lt.hfc.operators;

import de.dfki.lt.hfc.FunctionalOperator;
import de.dfki.lt.hfc.types.XsdFloat;

/**
 * computes the product of the arguments given to apply();
 * arguments are assumed to be xsd:floats;
 * returns a representation of the new float
 *
 * @see FunctionalOperator
 *
 * @author (C) Hans-Ulrich Krieger
 * @since JDK 1.5
 * @version Tue Sep 29 11:11:19 CEST 2009
 */
public final class FProduct extends FunctionalOperator {
	
	/**
	 * note that apply() does NOT check at the moment whether the int args
	 * represent in fact XSD floats;
	 * note that apply() does NOT check whether it is given at least one argument
	 */
	public int apply(int[] args) {
		float f = ((XsdFloat)getObject(args[0])).value;
		for (int i = 1; i < args.length; i++)
			f = f * ((XsdFloat)getObject(args[i])).value;
		XsdFloat F = new XsdFloat(f);
		return registerObject(F.toString(de.dfki.lt.hfc.Namespace.shortIsDefault), F);
	}
	
}
