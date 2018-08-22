package de.dfki.lt.hfc.operators;

import de.dfki.lt.hfc.FunctionalOperator;
import de.dfki.lt.hfc.types.XsdFloat;

/**
 * computes the difference of the two arguments given to apply();
 * arguments are assumed to be xsd:floats;
 * returns a representation of the new float
 *
 * @author (C) Hans-Ulrich Krieger
 * @version Tue Sep 29 11:11:19 CEST 2009
 * @see FunctionalOperator
 * @since JDK 1.5
 */
public final class FDifference extends FunctionalOperator {

  /**
   * note that apply() does NOT check at the moment whether the int args
   * represent in fact XSD floats;
   * note that apply() does NOT check whether it is given exactly two arguments
   */
  public int apply(int[] args) {
    float f = ((XsdFloat) getObject(args[0])).value - ((XsdFloat) getObject(args[1])).value;
    XsdFloat F = new XsdFloat(f);
    return registerObject(F.toString(this.tupleStore.namespace.shortIsDefault), F);
  }

}
