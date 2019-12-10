package de.dfki.lt.hfc.operators;

import de.dfki.lt.hfc.FunctionalOperator;
import de.dfki.lt.hfc.types.XsdUDateTime;

/**
 * checks whether the first argument is less than the second argument;
 * arguments are assumed to be of type XsdUDateTime;
 *
 * @author (C) Hans-Ulrich Krieger
 * @version Fri May 20 15:49:48 CEST 2011
 * @return FunctionalOperator.TRUE or FunctionalOperator.FALSE
 * @since JDK 1.5
 */
public final class UDTLess extends FunctionalOperator {

  /**
   * note that apply() does NOT check at the moment whether the int args are in fact
   * of type XsdUDateTime;
   * note that apply() does NOT check whether it is given exactly two arguments;
   * due to the use of wildcards ?, UDTLess in fact can be seen as a three-valued
   * predicate (false, don't-know, true);
   * but since we are interested in whether (dt1 UDTLess dt2) is the case, don't-know
   * is interpreted as not true
   */
  public int apply(int[] args) {
    XsdUDateTime firstArg = (XsdUDateTime) getObject(args[0]);
    XsdUDateTime secondArg = (XsdUDateTime) getObject(args[1]);
    // year (int)
    if (XsdUDateTime.isUnderspecified(firstArg.year) || XsdUDateTime.isUnderspecified(secondArg.year) ||
            XsdUDateTime.isUnspecified(firstArg.year) || XsdUDateTime.isUnspecified(secondArg.year))
      return FunctionalOperator.FALSE;
    if (firstArg.year < secondArg.year)
      return FunctionalOperator.TRUE;
    if (firstArg.year > secondArg.year)
      return FunctionalOperator.FALSE;
    //month (int) -- only visited if year in both dates are equal and both are not un(der)specified
    if (XsdUDateTime.isUnderspecified(firstArg.month) || XsdUDateTime.isUnderspecified(secondArg.month) ||
            XsdUDateTime.isUnspecified(firstArg.month) || XsdUDateTime.isUnspecified(secondArg.month))
      return FunctionalOperator.FALSE;
    if (firstArg.month < secondArg.month)
      return FunctionalOperator.TRUE;
    if (firstArg.month > secondArg.month)
      return FunctionalOperator.FALSE;
    // day (int)
    if (XsdUDateTime.isUnderspecified(firstArg.day) || XsdUDateTime.isUnderspecified(secondArg.day) ||
            XsdUDateTime.isUnspecified(firstArg.day) || XsdUDateTime.isUnspecified(secondArg.day))
      return FunctionalOperator.FALSE;
    if (firstArg.day < secondArg.day)
      return FunctionalOperator.TRUE;
    if (firstArg.day > secondArg.day)
      return FunctionalOperator.FALSE;
    // hour (int)
    if (XsdUDateTime.isUnderspecified(firstArg.hour) || XsdUDateTime.isUnderspecified(secondArg.hour) ||
            XsdUDateTime.isUnspecified(firstArg.hour) || XsdUDateTime.isUnspecified(secondArg.hour))
      return FunctionalOperator.FALSE;
    if (firstArg.hour < secondArg.hour)
      return FunctionalOperator.TRUE;
    if (firstArg.hour > secondArg.hour)
      return FunctionalOperator.FALSE;
    // minute (int)
    if (XsdUDateTime.isUnderspecified(firstArg.minute) || XsdUDateTime.isUnderspecified(secondArg.minute) ||
            XsdUDateTime.isUnspecified(firstArg.minute) || XsdUDateTime.isUnspecified(secondArg.minute))
      return FunctionalOperator.FALSE;
    if (firstArg.minute < secondArg.minute)
      return FunctionalOperator.TRUE;
    if (firstArg.minute > secondArg.minute)
      return FunctionalOperator.FALSE;
    // second (float)
    if (XsdUDateTime.isUnderspecified(firstArg.second) || XsdUDateTime.isUnderspecified(secondArg.second) ||
            XsdUDateTime.isUnspecified(firstArg.second) || XsdUDateTime.isUnspecified(secondArg.second))
      return FunctionalOperator.FALSE;
    if (firstArg.second < secondArg.second)
      return FunctionalOperator.TRUE;
    return FunctionalOperator.FALSE;
  }

}
