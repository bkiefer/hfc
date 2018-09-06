package de.dfki.lt.hfc.operators;

import de.dfki.lt.hfc.FunctionalOperator;
import de.dfki.lt.hfc.types.XsdLong;

/**
 * computes the minimum of the two (2) arguments given to apply();
 * we do NOT check whether there are less than or more than two arguments;
 * arguments are assumed to be of type <xsd:long>;
 * returns the minimal long
 *
 * @author (C) Hans-Ulrich Krieger
 * @version Mon May 30 14:01:54 CEST 2011
 * @see FunctionalOperator
 * @since JDK 1.5
 */
public final class LMin2 extends FunctionalOperator {

  /**
   * note that apply() does NOT check at the moment whether the int args
   * represent in fact XSD longs
   */
  public int apply(int[] args) {
    final long first = ((XsdLong) getObject(args[0])).value;
    final long second = ((XsdLong) getObject(args[1])).value;
    if (first < second)
      return args[0];
    else
      return args[1];
  }
	
	/* this version registers the minimum which is not needed, since the min is contained in the arguments
	public int apply(int[] args) {
		final long first = ((XsdLong)getObject(args[0])).value;
		final long second = ((XsdLong)getObject(args[1])).value;
		final long min = Math.min(first, second);
		XsdLong L = new XsdLong(min);
		return registerObject(L.toString(de.dfki.lt.hfc.NamespaceManager.shortIsDefault), L);
	}
	*/

}
