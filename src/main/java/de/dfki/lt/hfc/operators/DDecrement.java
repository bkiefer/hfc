package de.dfki.lt.hfc.operators;

import de.dfki.lt.hfc.FunctionalOperator;
import de.dfki.lt.hfc.types.XsdDouble;

/**
 * Computes the decrement of the single argument given to apply();
 * argument is assumed to be an xsd:double;
 * returns a representation of the new double
 *
 * @see FunctionalOperator
 * <p>
 * Created by christian on 14/06/17.
 */
public class DDecrement extends FunctionalOperator {
  @Override
  public int apply(int[] args) {
    double d = Math.nextDown(((XsdDouble) getObject(args[0])).value);
    XsdDouble D = new XsdDouble(d);
    return registerObject(D.toString(this.tupleStore.namespace.shortIsDefault), D);
  }
}
