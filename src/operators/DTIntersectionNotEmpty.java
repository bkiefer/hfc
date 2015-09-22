package de.dfki.lt.hfc.operators;

import de.dfki.lt.hfc.FunctionalOperator;

/**
 * checks whether the intersection of two temporal intervals whose
 * starting and ending time is given by two XSD dateTime instants
 * is empty or not;
 * we do not check whether apply() is given exactly four arguments
 *
 * @see FunctionalOperator
 *
 * @author (C) Hans-Ulrich Krieger
 * @since JDK 1.5
 * @version Wed Jun 22 18:05:48 CEST 2011
 */
public final class DTIntersectionNotEmpty extends FunctionalOperator {
	
	/**
	 * apply() assumes that it is given exactly four arguments specifying
	 * the starting and ending time of the first temporal interval AND the
	 * starting and ending time of the second temporal interval:
	 *   IntervalNotEmpty start1 end1 start2 end2 :=
	 *     return (max(start1, start2) <= min(end1, end2))
	 *
	 * note that apply() does NOT check at the moment whether the int args
	 * represent in fact XSD dateTime instants
	 */
	public int apply(int[] args) {
		// note: we call DTMax2 and DTMin2 which call DTLess through the use of
		//       callFuntionalOperator() in order to define DTIntersectionNotEmpty
		final int start = callFunctionalOperator("DTMax2", new int[] {args[0], args[2]});
		final int end = callFunctionalOperator("DTMin2", new int[] {args[1], args[3]});
		return callFunctionalOperator("DTLessEqual", new int[] {start, end});
	}
	
}
