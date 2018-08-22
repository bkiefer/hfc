package de.dfki.lt.hfc.operators;

import de.dfki.lt.hfc.FunctionalOperator;
import de.dfki.lt.hfc.types.XsdDate;

/**
 * Computes the increment of the single argument given to apply();
 * argument is assumed to be an xsd:date;
 * returns a representation of the new date
 *
 * @see FunctionalOperator
 * <p>
 * Created by christian on 14/06/17.
 */
public class DaIncrement extends FunctionalOperator {

  /**
   * note that apply() does NOT check at the moment whether the int arg
   * represent in fact an XSD int or float;
   * note that apply() does NOT check whether it is given exactly one argument
   */
  @Override
  public int apply(int[] args) {
    XsdDate date = ((XsdDate) getObject(args[0]));
    XsdDate newDate;
    if (date.day < 31)
      newDate = new XsdDate(date.year, date.month, date.day + 1);
    else if (date.month < 12)
      newDate = new XsdDate(date.year, date.month + 1, 1);
    else
      newDate = new XsdDate(date.year + 1, 1, 1);
    return registerObject(newDate.toString(this.tupleStore.namespace.shortIsDefault), newDate);
  }

}
