package de.dfki.lt.hfc.operators;

import de.dfki.lt.hfc.FunctionalOperator;
import de.dfki.lt.hfc.types.XsdInt;

/**
 * computes the increment of the single argument given to apply();
 * argument is assumed to be an xsd:int;
 * returns a representation of the new int
 *
 * @author (C) Hans-Ulrich Krieger
 * @version Tue Sep 29 11:11:19 CEST 2009
 * @see FunctionalOperator
 * @since JDK 1.5
 */
public final class IIncrement extends FunctionalOperator {

  /**
   * note that apply() does NOT check at the moment whether the int arg
   * represent in fact an XSD int or float;
   * note that apply() does NOT check whether it is given exactly one argument
   */
  public int apply(int[] args) {
    int i = ((XsdInt) getObject(args[0])).value + 1;
    XsdInt I = new XsdInt(i);
    return registerObject(I.toString(), I);
  }

}
