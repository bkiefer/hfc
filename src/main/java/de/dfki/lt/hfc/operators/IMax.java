package de.dfki.lt.hfc.operators;

import de.dfki.lt.hfc.FunctionalOperator;
import de.dfki.lt.hfc.types.XsdInt;

/**
 * computes the maximum of the arguments given to apply();
 * arguments are assumed to be xsd:ints;
 * returns the maximal int
 *
 * @author (C) Hans-Ulrich Krieger
 * @version Tue Sep 29 11:11:19 CEST 2009
 * @see FunctionalOperator
 * @since JDK 1.5
 */
public final class IMax extends FunctionalOperator {

  /**
   * note that apply() does NOT check at the moment whether the int args
   * represent in fact XSD ints;
   * note that apply() does NOT check whether it is given at least one argument
   */
  public int apply(int[] args) {
    int argument = args[0];
    int max = ((XsdInt) getObject(args[0])).value;
    int current;
    for (int i = 1; i < args.length; i++) {
      current = ((XsdInt) getObject(args[i])).value;
      if (current > max) {
        max = current;
        argument = args[i];
      }
    }
    return argument;
  }
	
	/* this version registers the maximum which is not needed, since the max is contained in the arguments
	public int apply(int[] args) {
		int max = ((XsdInt)getObject(args[0])).value;
		int current;
		for (int i = 1; i < args.length; i++) {
			current = ((XsdInt)getObject(args[i])).value;
			if (current > max)
				max = current;
		}
		XsdInt I = new XsdInt(max);
		return registerObject(I.toString(de.dfki.lt.hfc.Namespace.shortIsDefault), I);
	}
	*/

}
