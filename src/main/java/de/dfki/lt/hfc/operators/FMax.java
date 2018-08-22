package de.dfki.lt.hfc.operators;

import de.dfki.lt.hfc.FunctionalOperator;
import de.dfki.lt.hfc.types.XsdFloat;

/**
 * computes the maximum of the arguments given to apply();
 * arguments are assumed to be xsd:floats;
 * returns the maximal float
 *
 * @author (C) Hans-Ulrich Krieger
 * @version Tue Sep 29 11:11:19 CEST 2009
 * @see FunctionalOperator
 * @since JDK 1.5
 */
public final class FMax extends FunctionalOperator {

  /**
   * note that apply() does NOT check at the moment whether the int args
   * represent in fact XSD floats;
   * note that apply() does NOT check whether it is given at least one argument
   */
  public int apply(int[] args) {
    int argument = args[0];
    float max = ((XsdFloat) getObject(args[0])).value;
    float current;
    for (int i = 1; i < args.length; i++) {
      current = ((XsdFloat) getObject(args[i])).value;
      if (current > max) {
        max = current;
        argument = args[i];
      }
    }
    return argument;
  }
	
	/* this version registers the maximum which is not needed, since the max is contained in the arguments
	public int apply(int[] args) {
		float max = ((XsdFloat)getObject(args[0])).value;
		float current;
		for (int i = 1; i < args.length; i++) {
			current = ((XsdFloat)getObject(args[i])).value;
			if (current > max)
				max = current;
		}
		XsdFloat F = new XsdFloat(max);
		return registerObject(F.toString(de.dfki.lt.hfc.Namespace.shortIsDefault), F);
	}
	 */

}
