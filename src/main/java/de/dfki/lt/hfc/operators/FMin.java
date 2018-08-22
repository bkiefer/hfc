package de.dfki.lt.hfc.operators;

import de.dfki.lt.hfc.FunctionalOperator;
import de.dfki.lt.hfc.types.XsdFloat;

/**
 * computes the minimum of the arguments given to apply();
 * arguments are assumed to be xsd:floats;
 * returns the minimal float
 *
 * @author (C) Hans-Ulrich Krieger
 * @version Tue Sep 29 11:11:19 CEST 2009
 * @see FunctionalOperator
 * @since JDK 1.5
 */
public final class FMin extends FunctionalOperator {

  /**
   * note that apply() does NOT check at the moment whether the int args
   * represent in fact XSD floats;
   * note that apply() does NOT check whether it is given at least one argument
   */
  public int apply(int[] args) {
    int argument = args[0];
    float min = ((XsdFloat) getObject(args[0])).value;
    float current;
    for (int i = 1; i < args.length; i++) {
      current = ((XsdFloat) getObject(args[i])).value;
      if (current < min) {
        min = current;
        argument = args[i];
      }
    }
    return argument;
  }
	
	
	/* this version registers the minimum which is not needed, since the min is contained in the arguments
	public int apply(int[] args) {
		float min = ((XsdFloat)getObject(args[0])).value;
		float current;
		for (int i = 1; i < args.length; i++) {
			current = ((XsdFloat)getObject(args[i])).value;
			if (current < min)
				min = current;
		}
		XsdFloat F = new XsdFloat(min);
		return registerObject(F.toString(de.dfki.lt.hfc.Namespace.shortIsDefault), F);
	}
	 */

}
