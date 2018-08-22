package de.dfki.lt.hfc.operators;

import de.dfki.lt.hfc.FunctionalOperator;
import de.dfki.lt.hfc.types.XsdBoolean;
import de.dfki.lt.hfc.types.XsdString;


/**
 * converts its single argument (which is assumed to be the internal
 * representation of a string) into the internal representation of a
 * Boolean value, where "0" is interpreted as the "false" value and
 * everything else as "true"
 * <p>
 * returns the representation of the new boolean
 *
 * @author (C) Hans-Ulrich Krieger
 * @version Thu Feb  4 18:14:41 CET 2010
 * @see FunctionalOperator
 * @since JDK 1.5
 */
public final class IntStringToBoolean extends FunctionalOperator {

  /**
   * note that apply() does NOT check at the moment whether the int arg
   * represent in fact an XSD string, interpreted as a number; note that apply()
   * does NOT check whether it is given exactly one argument; 0 is interpreted
   * as false, non-zero as true (usually is 1)
   *
   * @param args values behind given argument ints should be IntStrings
   */
  public int apply(int[] args) {
    String str = ((XsdString) getObject(args[0])).value;
    XsdBoolean b = new XsdBoolean(Integer.parseInt(str) == 0 ? false : true);
    return registerObject(b.toString(this.tupleStore.namespace.shortIsDefault), b);
  }

}
