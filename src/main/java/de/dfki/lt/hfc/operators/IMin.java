package de.dfki.lt.hfc.operators;

import de.dfki.lt.hfc.FunctionalOperator;
import de.dfki.lt.hfc.types.XsdInt;

/**
 * computes the minimum of the arguments given to apply();
 * arguments are assumed to be xsd:ints;
 * returns the minimal int
 *
 * @author (C) Hans-Ulrich Krieger
 * @version Tue Sep 29 11:11:19 CEST 2009
 * @see FunctionalOperator
 * @since JDK 1.5
 */
public final class IMin extends FunctionalOperator {

  /**
   * note that apply() does NOT check at the moment whether the int args
   * represent in fact XSD ints;
   * note that apply() does NOT check whether it is given at least one argument
   */
  public int apply(int[] args) {
    int argument = args[0];
    int min = ((XsdInt) getObject(args[0])).value;
    int current;
    for (int i = 1; i < args.length; i++) {
      current = ((XsdInt) getObject(args[i])).value;
      if (current < min) {
        min = current;
        argument = args[i];
      }
    }
    return argument;
  }
	
	/* this version registers the minimum which is not needed, since the min is contained in the arguments
	public int apply(int[] args) {
		int min = ((XsdInt)getObject(args[0])).value;
		int current;
		for (int i = 1; i < args.length; i++) {
			current = ((XsdInt)getObject(args[i])).value;
			if (current < min)
				min = current;
		}
		XsdInt I = new XsdInt(min);
		return registerObject(I.toString(de.dfki.lt.hfc.Namespace.shortIsDefault), I);
	}
	*/

}
