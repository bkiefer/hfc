package de.dfki.lt.hfc.operators;

import de.dfki.lt.hfc.FunctionalOperator;
import de.dfki.lt.hfc.types.XsdDate;

/**
 * Computes the decrement of the single argument given to apply();
 * argument is assumed to be an xsd:date;
 * returns a representation of the new date
 *
 * @see FunctionalOperator
 * <p>
 * Created by christian on 14/06/17.
 */
public class DaDecrement extends FunctionalOperator {
  @Override
  public int apply(int[] args) {
    XsdDate date = ((XsdDate) getObject(args[0]));
    XsdDate newDate;
    if (date.day > 1)
      newDate = new XsdDate(date.year, date.month, date.day - 1);
    else if (date.month > 1)
      newDate = new XsdDate(date.year, date.month - 1, 30);
    else
      newDate = new XsdDate(date.year - 1, 12, 30);
    return registerObject(newDate.toString(this.tupleStore.namespace.shortIsDefault), newDate);
  }
}
