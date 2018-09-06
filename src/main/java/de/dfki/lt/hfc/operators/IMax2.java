package de.dfki.lt.hfc.operators;

import de.dfki.lt.hfc.FunctionalOperator;
import de.dfki.lt.hfc.types.XsdInt;

/**
 * computes the maximum of the two (2) arguments given to apply();
 * we do NOT check whether there are less than or more than two arguments;
 * arguments are assumed to be of type <xsd:int>;
 * returns the maximal int
 *
 * @author (C) Hans-Ulrich Krieger
 * @version Mon May 30 14:01:54 CEST 2011
 * @see FunctionalOperator
 * @since JDK 1.5
 */
public final class IMax2 extends FunctionalOperator {

  /**
   * note that apply() does NOT check at the moment whether the int args
   * represent in fact XSD ints
   */
  public int apply(int[] args) {
    final int first = ((XsdInt) getObject(args[0])).value;
    final int second = ((XsdInt) getObject(args[1])).value;
    if (first > second)
      return args[0];
    else
      return args[1];
  }
	
	/* this version registers the maximum which is not needed, since the max is contained in the arguments
	public int apply(int[] args) {
		final int first = ((XsdInt)getObject(args[0])).value;
		final int second = ((XsdInt)getObject(args[1])).value;
		final int max = Math.max(first, second);
		XsdInt I = new XsdInt(max);
		return registerObject(I.toString(de.dfki.lt.hfc.NamespaceManager.shortIsDefault), I);
	}
	*/

}
