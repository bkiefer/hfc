package de.dfki.lt.hfc.operators;

import de.dfki.lt.hfc.BooleanOperator;
import de.dfki.lt.hfc.types.XsdInt;

/**
 * checks whether the intersection of two temporal intervals whose
 * starting and ending time is given by two XSD ints is empty or not;
 * we do not check whether apply() is given exactly four arguments
 *
 * @author (C) Hans-Ulrich Krieger
 * @version Mon May 30 14:01:54 CEST 2011
 * @see BooleanOperator
 * @since JDK 1.5
 */
public final class IIntersectionNotEmpty extends BooleanOperator {

  /**
   * apply() assumes that it is given exactly four arguments specifying
   * the starting and ending time of the first temporal interval AND the
   * starting and ending time of the second temporal interval:
   * IntervalNotEmpty start1 end1 start2 end2 :=
   * return (max(start1, start2) <= min(end1, end2))
   * <p>
   * note that apply() does NOT check at the moment whether the int args
   * represent in fact XSD ints
   */
  protected boolean holds(int[] args) {
    final int start1 = ((XsdInt) getObject(args[0])).value;
    final int end1 = ((XsdInt) getObject(args[1])).value;
    final int start2 = ((XsdInt) getObject(args[2])).value;
    final int end2 = ((XsdInt) getObject(args[3])).value;
    final int start = Math.max(start1, start2);
    final int end = Math.min(end1, end2);
    return start <= end;
  }

}
