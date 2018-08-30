package de.dfki.lt.hfc.operators;

import de.dfki.lt.hfc.FunctionalOperator;
import de.dfki.lt.hfc.types.XsdUDateTime;

/**
 * Computes the decrement of the single argument given to apply();
 * argument is assumed to be an xsd:dateTime;
 * returns a representation of the new dateTime
 * <p>
 * <p>
 * Created by christian on 14/06/17.
 */
public class UDTDecrement extends FunctionalOperator {
  @Override
  public int apply(int[] args) {
    XsdUDateTime date = ((XsdUDateTime) getObject(args[0]));
    XsdUDateTime newDate;
    if (date.second > 1)
      newDate = new XsdUDateTime(date.year, date.month, date.day, date.hour, date.minute, date.second - 1);
    else if (date.minute > 1)
      newDate = new XsdUDateTime(date.year, date.month, date.day, date.hour, date.minute - 1, 59);
    else if (date.hour > 1)
      newDate = new XsdUDateTime(date.year, date.month, date.day, date.hour - 1, 59, 59);
    else if (date.day > 1)
      newDate = new XsdUDateTime(date.year, date.month, date.day - 1, 23, 59, 59);
    else if (date.month > 1)
      newDate = new XsdUDateTime(date.year, date.month - 1, 30, 23, 59, 59);
    else
      newDate = new XsdUDateTime(date.year - 1, 12, 30, 23, 59, 59);
    return registerObject(newDate.toString(), newDate);
  }
}
