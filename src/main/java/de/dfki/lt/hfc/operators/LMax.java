package de.dfki.lt.hfc.operators;

import de.dfki.lt.hfc.FunctionalOperator;
import de.dfki.lt.hfc.types.XsdLong;

/**
 * computes the maximum of the arguments given to apply();
 * arguments are assumed to be of type xsd:long;
 * returns the maximal long
 *
 * @author (C) Hans-Ulrich Krieger
 * @version Tue Sep 29 11:11:19 CEST 2009
 * @see FunctionalOperator
 * @since JDK 1.5
 */
public final class LMax extends FunctionalOperator {

  /**
   * note that apply() does NOT check at the moment whether the int args
   * represent in fact XSD longs;
   * note that apply() does NOT check whether it is given at least one argument
   */
  public int apply(int[] args) {
    int argument = args[0];
    long max = ((XsdLong) getObject(args[0])).value;
    long current;
    for (int i = 1; i < args.length; i++) {
      current = ((XsdLong) getObject(args[i])).value;
      if (current > max) {
        max = current;
        argument = args[i];
      }
    }
    return argument;
  }
	
	/* this version registers the maximum which is not needed, since the max is contained in the arguments
	public int apply(int[] args) {
		long max = ((XsdLong)getObject(args[0])).value;
		long current;
		for (int i = 1; i < args.length; i++) {
			current = ((XsdLong)getObject(args[i])).value;
			if (current > max)
				max = current;
		}
		XsdLong L = new XsdLong(max);
		return registerObject(L.toString(de.dfki.lt.hfc.Namespace.shortIsDefault), L);
	}
	*/

}
