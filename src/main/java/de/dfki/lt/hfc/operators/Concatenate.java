package de.dfki.lt.hfc.operators;

import de.dfki.lt.hfc.FunctionalOperator;
import de.dfki.lt.hfc.types.XsdString;

/**
 * Concatenate is an implementation of the concatenate operator that takes an arbitrary
 * number of arguments (hopefully strings, represented through ids = positive ints) and
 * returns an object that encapsulates the concatenation of the input strings (again an
 * int); note that the input argument strings must be given as an array (of ints)
 *
 * @see FunctionalOperator
 *
 * @author (C) Hans-Ulrich Krieger
 * @since JDK 1.5
 * @version Tue Sep  1 16:16:16 CEST 2009
 */
public final class Concatenate extends FunctionalOperator {
	
	/**
	 * note that apply() does NOT check at the moment whether the int args
	 * represent in fact XSD strings (and not URIs, XSD ints, etc.);
	 * note further that the language tag (if present) is not carried over
	 */
	public int apply(int[] args) {
		StringBuilder sb = new StringBuilder();
		XsdString object;
		for (int i = 0; i < args.length; i++) {
			object = (XsdString)getObject(args[i]);
			sb.append(object.value);
		}
		object = new XsdString(sb.toString(), null);
		return registerObject(object.toString(this.tupleStore.namespace.shortIsDefault), object);
	}

}
