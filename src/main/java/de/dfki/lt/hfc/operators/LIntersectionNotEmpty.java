package de.dfki.lt.hfc.operators;

import de.dfki.lt.hfc.BooleanOperator;
import de.dfki.lt.hfc.types.XsdLong;

/**
 * checks whether the intersection of two temporal intervals whose
 * starting and ending time is given by two XSD longs is empty or not;
 * we do not check whether apply() is given exactly four arguments
 *
 * @see FunctionalOperator
 *
 * @author (C) Hans-Ulrich Krieger
 * @since JDK 1.5
 * @version Mon May 30 14:01:54 CEST 2011
 */
public final class LIntersectionNotEmpty extends BooleanOperator {

	/**
	 * apply() assumes that it is given exactly four arguments specifying
	 * the starting and ending time of the first temporal interval AND the
	 * starting and ending time of the second temporal interval:
	 *   IntervalNotEmpty start1 end1 start2 end2 :=
	 *     return (max(start1, start2) <= min(end1, end2))
	 *
	 * note that apply() does NOT check at the moment whether the int args
	 * represent in fact XSD longs
	 */
	protected boolean holds(int[] args) {
		final long start1 = ((XsdLong)getObject(args[0])).value;
		final long end1 = ((XsdLong)getObject(args[1])).value;
		final long start2 = ((XsdLong)getObject(args[2])).value;
		final long end2 = ((XsdLong)getObject(args[3])).value;
		final long start = Math.max(start1, start2);
		final long end = Math.min(end1, end2);
		return (start <= end);
	}

}
