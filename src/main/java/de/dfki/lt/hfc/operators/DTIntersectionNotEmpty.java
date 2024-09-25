package de.dfki.lt.hfc.operators;

import de.dfki.lt.hfc.BooleanOperator;
import de.dfki.lt.hfc.FunctionalOperator;
import de.dfki.lt.hfc.types.XsdDateTime;

/**
 * checks whether the intersection of two temporal intervals whose
 * starting and ending time is given by two XSD dateTime instants
 * is empty or not;
 * we do not check whether apply() is given exactly four arguments
 *
 * @author (C) Hans-Ulrich Krieger
 * @version Wed Jun 22 18:05:48 CEST 2011
 * @see FunctionalOperator
 * @since JDK 1.5
 */
public final class DTIntersectionNotEmpty extends BooleanOperator {

  /**
   * apply() assumes that it is given exactly four arguments specifying
   * the starting and ending time of the first temporal interval AND the
   * starting and ending time of the second temporal interval:
   * IntervalNotEmpty start1 end1 start2 end2 :=
   * return (max(start1, start2) <= min(end1, end2))
   * <p>
   * note that apply() does NOT check at the moment whether the int args
   * represent in fact XSD dateTime instants
   */
  public boolean holds(int[] args) {
    // note: we call DTMax2 and DTMin2 which call DTLess through the use of
    //       callFuntionalOperator() in order to define DTIntersectionNotEmpty

    XsdDateTime datestart = ((XsdDateTime) getObject(args[0]));
    XsdDateTime date2start = ((XsdDateTime) getObject(args[2]));
    // get max of datestart
    if (datestart.compareTo(date2start) < 0) {
      datestart = date2start;
    }
    XsdDateTime dateend = ((XsdDateTime) getObject(args[1]));
    XsdDateTime date2end = ((XsdDateTime) getObject(args[3]));
    // get min of dateend
    if (dateend.compareTo(date2end) > 0) {
      dateend = date2end;
    }
    return datestart.compareTo(dateend) <= 0;
  }

}
