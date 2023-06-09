package de.dfki.lt.hfc.operators;

import de.dfki.lt.hfc.FunctionalOperator;
import de.dfki.lt.hfc.types.XsdInt;

/**
 * computes the difference of the two arguments given to apply();
 * arguments are assumed to be xsd:ints;
 * returns a representation of the new int
 *
 * @author (C) Hans-Ulrich Krieger
 * @version Tue Sep 29 11:11:19 CEST 2009
 * @see FunctionalOperator
 * @since JDK 1.5
 */
public final class IDifference extends FunctionalOperator {

  /**
   * note that apply() does NOT check at the moment whether the int args
   * represent in fact XSD ints;
   * note that apply() does NOT check whether it is given exactly two arguments
   */
  public int apply(int[] args) {
    int i = ((XsdInt) getObject(args[0])).value - ((XsdInt) getObject(args[1])).value;
    XsdInt I = new XsdInt(i);
    return registerObject(I.toString(), I);
  }

}
