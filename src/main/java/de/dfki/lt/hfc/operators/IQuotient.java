package de.dfki.lt.hfc.operators;

import de.dfki.lt.hfc.FunctionalOperator;
import de.dfki.lt.hfc.types.XsdInt;

/**
 * computes the quotient of the two arguments given to apply();
 * arguments are assumed to be xsd:ints;
 * returns a representation of the new int -- note that the remainder is lost!
 *
 * @see FunctionalOperator
 *
 * @author (C) Hans-Ulrich Krieger
 * @since JDK 1.5
 * @version Tue Sep 29 11:11:19 CEST 2009
 */
public final class IQuotient extends FunctionalOperator {
	
	/**
	 * note that apply() does NOT check at the moment whether the int args
	 * represent in fact XSD ints;
	 * note that apply() does NOT check whether it is given exactly two arguments
	 */
	public int apply(int[] args) {
		int i = ((XsdInt)getObject(args[0])).value / ((XsdInt)getObject(args[1])).value;
		XsdInt I = new XsdInt(i);
		return registerObject(I.toString(this.tupleStore.namespace.shortIsDefault), I);
	}
	
}
