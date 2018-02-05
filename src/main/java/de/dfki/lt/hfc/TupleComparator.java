package de.dfki.lt.hfc;

import java.util.*;

/**
 * NOTE: class no longer in use, since the code is inlined into Calc.join in order
 *       to speed up this time-critical method!!
 *
 * @author (C) Hans-Ulrich Krieger, Bernd Kiefer
 * @since JDK 1.5
 * @version 2.0, Wed Jun 24 14:36:20 CEST 2009
 */
public final class TupleComparator implements Comparator<int[]> {
	
	private int[][] columns;

	public TupleComparator(Set<Integer> commonVars,
												 Map<Integer, Integer> va0,
												 Map<Integer, Integer> va1) {
		int[] vars = new int[commonVars.size()];
		int i = 0;
		for (int var : commonVars) {
			vars[i++] = var;
		}
		columns = new int[2][];
		columns[0] = new int[vars.length];
		columns[1] = new int[vars.length];
		i = 0;
		for(Integer var : vars) {
			columns[0][i] = va0.get(var);
			columns[1][i] = va1.get(var);
			++i;
		}
	}


	public int compare(int[] tuple0, int[] tuple1) {
		int[] col0 = columns[0];
		int[] col1 = columns[1];
		// the i's represent the indexToVariable!
		int diff;
		for (int i = 0; i < col0.length; ++i) {
			diff = tuple0[col0[i]] - tuple1[col1[i]];
			if (diff != 0)
				return diff;
		}
		return 0;
	}
	
	public int compare0(int[] tuple0, int[] tuple1) {
		int[] col0 = columns[0];
		int diff;
		for (int i = 0; i < col0.length; ++i) {
			diff = tuple0[col0[i]] - tuple1[col0[i]];
			if (diff != 0)
				return diff;
		}
		return 0;
	}
	
	public int compare1(int[] tuple0, int[] tuple1) {
		int[] col1 = columns[1];
		int diff;
		for (int i = 0; i < col1.length; ++i) {
			diff = tuple0[col1[i]] - tuple1[col1[i]];
			if (diff != 0)
				return diff;
		}
		return 0;
	}


}
