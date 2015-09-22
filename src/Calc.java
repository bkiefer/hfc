package de.dfki.lt.hfc;

import java.util.*;
import gnu.trove.*;

/**
 * a collection of static methods that deal with sets and binding tables, used by the
 * forward chainer;
 *
 * non-destructive: union(), intersection(), difference()
 * non-destructive: project(), product(), join()
 * destructive: project(), restrict()
 * non-destructive: map()
 * 
 * @author (C) Hans-Ulrich Krieger
 * @since JDK 1.5
 * @version Fri Mar 15 18:26:17 CET 2013
 */
public final class Calc {

	/**
	 * the default hashing and equals strategy for tuples from the output set of the current
	 * iteration: take ALL positions of a tuple into account
	 */
	protected static TIntArrayHashingStrategy DEFAULT_HASHING_STRATEGY = new TIntArrayHashingStrategy();
	
	/**
	 * a non-destructive set union operation (in contrast to addAll) that better
	 * serve our needs when we query the index, perform table joins, etc.;
	 * by default, uses Calc.DEFAULT_HASHING_STRATEGY
	 */
	public static Set<int[]> union(Set<int[]> set1, Set<int[]> set2) {
		// iterate over the _smaller_ set
		if (set1.size() > set2.size()) {
			Set<int[]> set = set1;
			set1 = set2;
			set2 = set;
		}
		Set<int[]> res = new THashSet<int[]>(set2, Calc.DEFAULT_HASHING_STRATEGY);
		for (int[] elem : set1)
			res.add(elem);  // add already checks for containment
		return res;
	}
	
	/**
	 * extends the binary version by a third TIntArrayHashingStrategy argument
	 */
	public static Set<int[]> union(Set<int[]> set1, Set<int[]> set2,
																 TIntArrayHashingStrategy strategy) {
		// iterate over the _smaller_ set
		if (set1.size() > set2.size()) {
			Set<int[]> set = set1;
			set1 = set2;
			set2 = set;
		}
		Set<int[]> res = new THashSet<int[]>(set2, strategy);
		for (int[] elem : set1)
			res.add(elem);  // add already checks for containment
		return res;
	}
	
	/**
	 * a non-destructive set intersection operation (in contrast to retainAll) that
	 * better serve our needs when we query the index, perform table joins, etc.;
	 * by default, uses Calc.DEFAULT_HASHING_STRATEGY
	 */
	public static Set<int[]> intersection(Set<int[]> set1, Set<int[]> set2) {
		// iterate over the _smaller_ set
		if (set1.size() > set2.size()) {
			Set<int[]> set = set1;
			set1 = set2;
			set2 = set;
		}
		Set<int[]> res = new THashSet<int[]>(Calc.DEFAULT_HASHING_STRATEGY);
		for (int[] elem : set1)
			if (set2.contains(elem))
				res.add(elem);
		return res;
	}
	
	/**
	 * extends the binary version by a third TIntArrayHashingStrategy argument
	 */
	public static Set<int[]> intersection(Set<int[]> set1, Set<int[]> set2,
																				TIntArrayHashingStrategy strategy) {
		// iterate over the _smaller_ set
		if (set1.size() > set2.size()) {
			Set<int[]> set = set1;
			set1 = set2;
			set2 = set;
		}
		Set<int[]> res = new THashSet<int[]>(strategy);
		for (int[] elem : set1)
			if (set2.contains(elem))
				res.add(elem);
		return res;
	}
	
	/**
	 * a non-destructive set difference operation (in contrast to removeAll) that
	 * better serve our needs when we query the index, perform table joins, etc.;
	 * should be read as set1\set2;
	 * by default, uses Calc.DEFAULT_HASHING_STRATEGY
	 */
	public static Set<int[]> difference(Set<int[]> set1, Set<int[]> set2) {
		Set<int[]> res = new THashSet<int[]>(Calc.DEFAULT_HASHING_STRATEGY);
		for (int[] elem : set1)
			if (!set2.contains(elem))
				res.add(elem);
		return res;
	}
	
	/**
	 * similar to the binary version of Calc.difference(), but assumes that
	 * set2 is always a SUBSET of set1
	 */
	public static Set<int[]> monotonicDifference(Set<int[]> set1, Set<int[]> set2) {
		if (set1.size() == set2.size())
			return  new THashSet<int[]>(Calc.DEFAULT_HASHING_STRATEGY);
		Set<int[]> res = new THashSet<int[]>(Calc.DEFAULT_HASHING_STRATEGY);
		for (int[] elem : set1)
			if (!set2.contains(elem))
				res.add(elem);
		return res;
	}
	
	/**
	 * extends the binary version by a third TIntArrayHashingStrategy argument
	 */
	public static Set<int[]> difference(Set<int[]> set1, Set<int[]> set2,
																			TIntArrayHashingStrategy strategy) {
		Set<int[]> res = new THashSet<int[]>(strategy);
		for (int[] elem : set1)
			if (!set2.contains(elem))
				res.add(elem);
		return res;
	}
	
	/**
	 * similar to the ternary version of Calc.difference(), but assumes that
	 * set2 is always a SUBSET of set1
	 */
	public static Set<int[]> monotonicDifference(Set<int[]> set1, Set<int[]> set2,
																							 TIntArrayHashingStrategy strategy) {
		if (set1.size() == set2.size())
			return  new THashSet<int[]>(strategy);
		Set<int[]> res = new THashSet<int[]>(strategy);
		for (int[] elem : set1)
			if (!set2.contains(elem))
				res.add(elem);
		return res;
	}
	
	/**
	 * given a table, project() constructs a _new_ table by taking into account only
	 * the columns given by pos (positive ints including 0);
	 * note that project() does NOT check whether the length of the input tuples
	 * is compatible with the largest position given by pos;
	 * note further that the length of the 'surviving' elements of the result set
	 * does NOT change, i.e., some of the columns simply are no longer of interest!!
	 * removes duplicates according to the TIntArrayHashingStrartegy constructed from pos
	 * NOTE: the positions array must always be SORTED in ascending order to guarantee
	 *       compatible hash codes!!!
	 */
	public static Set<int[]> project(Set<int[]> table, int[] pos) {
		THashSet<int[]> projTable = new THashSet<int[]>(new TIntArrayHashingStrategy(pos));
		projTable.addAll(table);
		return projTable;
	}
	
	/**
	 * this is code originally allocated in class Query that turns out to be useful
	 * in ForwardChainer;
	 * given a BindingTable object, the wrapped set served as input to the map()
	 * method which constructs a new BindingTable, taking over columns,
	 * potentially several times, in case of a variable or duplicating constants
	 * otherwise; this is controlled by argument args (an int array), where negative
	 * ints refer to variables, and positive ints to constants;
	 *
	 * example
	 * =======
	 *   input table has three columns with internal var names in this order: -1, -2, -3
	 *   args determines output table with four columns (position 0 and 2 same): [-2, 42, -2, -1]
	 *   input table:
	 *     1 2 3
   *     4 5 6
	 *     7 8 9
	 *   output table:
	 *     2 42 2 1
	 *     5 42 5 4
	 *     8 42 8 7
	 */
	public static BindingTable map(BindingTable oldTable, int[] toMap) {
		int length = toMap.length;
		int[] newTuple;
		Set<int[]> table = new THashSet<int[]>();
		BindingTable newTable = new BindingTable(table);
		SortedMap<Integer, Integer> nameToPos = oldTable.nameToPos;
		for (int[] oldTuple : oldTable.table) {
			newTuple = new int[length];
			for (int i = 0; i < length; i++) {
				if (RuleStore.isVariable(toMap[i]))
					newTuple[i] = oldTuple[nameToPos.get(toMap[i])];
				else
					newTuple[i] = toMap[i];
			}
			table.add(newTuple);
		}
		return newTable;
	}
	
	
	/**
	 * DESTRUCTIVELY modifies the table field of the BindingTable object tt
	 */
	public static BindingTable project(BindingTable tt, int[] pos) {
		tt.table = Calc.project(tt.table, pos);
		return tt;
	}
	
	/**
	 * _destructively_ removes tuples from the _existing_ binding table which do
	 * not satisfy the inequalities;
	 * if the input binding table should _not_ be changed, a shallow copy must be
	 * carried out first (both for the wrapper and the table itself);
	 * varvarineqs is an even (possibly empty) list of Integer objects, representing
	 * variables (int value < 0);
	 * varconstineqs is an even (possibly empty) list of Integer objects, alternating
	 * between variables (int value < 0) and URIs/XSD atoms (int value > 0);
	 * NOTE: restrict() does NOT check whether the variables mentioned in varvarineqs
	 *       or varconstineqs are legal column headings in the binding table
	 */
	public static BindingTable restrict(BindingTable bt, 
																			ArrayList<Integer> varvarIneqs,
																			ArrayList<Integer> varconstIneqs) {
		// check whether both ineq lists are empty in order to avoid iterator
		if (varvarIneqs.isEmpty() && varconstIneqs.isEmpty())
			return bt;
		// instead of using the vars, we use their positions (faster!);
		// use int[] instead of ArrayList<Integer>
		int[] vv = new int[varvarIneqs.size()];
		for (int i = 0; i < varvarIneqs.size(); i++) {
			vv[i] = bt.nameToPos.get(varvarIneqs.get(i));
			++i;  // varvarineqs is of even length
			vv[i] = bt.nameToPos.get(varvarIneqs.get(i));
		}
		int[] vc = new int[varconstIneqs.size()];
		for (int i = 0; i < varconstIneqs.size(); i++) {
			vc[i] = bt.nameToPos.get(varconstIneqs.get(i));
			++i;  // varconstineqs is of even length
			vc[i] = varconstIneqs.get(i);  // = constant, no translation!
		}
		// iterate over the tuples, removing those that do not satisfy the ineqs
		int[] tuple;
		Iterator<int[]> it = bt.table.iterator();
		// might be interesting to have four independent clauses here for empty
		// varvarineqs and/or varconstineqs (instead of potentially repeating
		// empty for-loops million times!)
		while (it.hasNext()) {
		outerloop: {
			tuple = it.next();
			for (int i = 0; i < vv.length; i++) {
				if (tuple[vv[i]] == tuple[vv[++i]]) {
					it.remove();
					break outerloop;
				}
			}
			for (int i = 0; i < vc.length; i++) {
				if (tuple[vc[i]] == vc[++i]) {
					it.remove();
					break outerloop;  // not needed here
				}
			}
		}  // outerloop block
		}
		return bt;
	}
	
	/**
	 * removes tuples from the _existing_ binding table which do not satisfy at least one of the predicates
	 */
	public static BindingTable restrict(BindingTable bt,
																			ArrayList<Predicate> predicates) {
		// no predicate: binding table does not change
		if (predicates.isEmpty())
			return bt;
		// fundamental distinction: functional-only vs. relational-only variables;
		// address the RELATIONAL case first: involved predicates will NOT undergo the double loop below
		ArrayList<Predicate> allPredicates = new ArrayList<Predicate>(predicates);  // do NOT modify predicates
		BindingTable[] intables, outtables;
		SortedMap<Integer, Integer> nameToPos;
		Integer funid;
		int[] projpos;
		Iterator<Predicate> predit = allPredicates.iterator();
		while (predit.hasNext()) {
			Predicate pred = predit.next();
			// a relational-only variable predicate has a non-empty mapping table
			if (pred.relIdToFunIds.size() != 0) {
				// construct the non-destructive table projections from the input binding table
				intables = new BindingTable[pred.args.length];
				for (int i = 0; i < pred.args.length; i++) {
					projpos = new int[pred.relIdToFunIds.get(pred.args[i]).size()];
					nameToPos = new TreeMap<Integer, Integer>();
					for (int j = 0; j < pred.relIdToFunIds.get(pred.args[i]).size(); j++) {
						funid = pred.relIdToFunIds.get(pred.args[i]).get(j);
						projpos[j] = bt.nameToPos.get(funid);
						nameToPos.put(funid, projpos[j]);
					}
					// in order to call project, I need an ascending int[] of positions; cf. TIntArrayHashingStrategy
					Arrays.sort(projpos);
					// call project(), but do NOT modify the original binding table bt at this place
					intables[i] = new BindingTable(Calc.project(bt.table, projpos),
																				 nameToPos,            // nameToPos map important for join() below
																				 null,                 // nameToExternalName of bt is always null
																				 pred.op.tupleStore,   // tupleStore of bt is null, but is accessable via operator
																				 pred.args,
																				 pred.relIdToFunIds,
																				 pred.varToId);
				}
				// call complex predicate
				outtables = ((RelationalOperator)pred.op).apply(intables);
				// successively join outtables with the input binding table bt
				for (BindingTable outtable : outtables)
					bt = Calc.join(outtable, bt);
				// predicate involving relational variables needs to be processed only once for each fixpoint iteration step
				predit.remove();
			}
		}
		if (allPredicates.size() == 0)
			// all the predicates applied here involve only relational variables: finished!
			return bt;
		// FUNCTIONAL case: avoid one level of indirection by moving from var names directly to their positions;
		// note that this the computation of this mapping is carried out only once: test for (pred.pos == null)!
		for (Predicate pred : allPredicates) {
			if (pred.pos == null) {
				pred.pos = new int[pred.args.length];
				for (int i = 0; i < pred.args.length; i++) {
					if (RuleStore.isVariable(pred.args[i]))
						// use _negative_ ints to indicate _positions_ (positive ints are already used for literals)
						pred.pos[i] = -bt.nameToPos.get(pred.args[i]);
					else
						pred.pos[i] = pred.args[i];
				}
			}
		}
		// outer loop: iterate over table, not over predicates (inner loop), so that we need only one iterator
		int[] tuple, input;
		final Iterator<int[]> tableit = bt.table.iterator();
		int pos;
		while (tableit.hasNext()) {
			tuple = tableit.next();
			for (Predicate pred : allPredicates) {
				input = new int[pred.pos.length];
				// construct input to the dynamic application of the predicate
				for (int i = 0; i < pred.pos.length; i++) {
					pos = pred.pos[i];
					input[i] = pos <= 0 ? tuple[-pos] : pos;  // true positions start with 0
				}
				// check whether the application of the predicates instantiated by elements from tuple is successful
				if (((FunctionalOperator)pred.op).apply(input) == FunctionalOperator.FALSE)
					// the predicate fails, thus remove the current table row
					tableit.remove();
			}
		}
		return bt;
	}
	
	/**
	 * performs the Cartesian product of the tables stored under tt1 and tt2
	 * and returns a new binding table;
	 * assumes that tt1 and tt2 do not have variables in common;
	 * otherwise the null value is returned as a sign that tt1 and tt2 are NOT
	 * independent;
	 */
	public static BindingTable product(BindingTable bt1, BindingTable bt2) {
		Set<Integer> vars1 = bt1.nameToPos.keySet();
		Set<Integer> vars2 = bt2.nameToPos.keySet();
		SortedSet<Integer> allVars = new TreeSet<Integer>(vars1);
		allVars.addAll(vars2);
		int newSize = allVars.size();
		int vars1Size = vars1.size();
		int vars2Size = vars2.size();
		if (newSize != (vars1Size + vars2Size))
			// both tables have variables in common: ERROR (signaled by null value)
			return null;
		BindingTable bt = new BindingTable();
		Set<int[]> table = new THashSet<int[]>();
		bt.table = table;
		bt.nameToPos = new TreeMap<Integer, Integer>();
		int[] mediator = new int[newSize];
		int pos = 0;
		// there might be columns in bt1 and bt2 that refer to don't cares or repeating
		// atoms, thus use a "mediator" instead of "appending" the two tuple arrays!
		for (Integer name : vars1) {
			bt.nameToPos.put(name, pos);
			mediator[pos] = bt1.nameToPos.get(name);
			++pos;
		}
		for (Integer name : vars2) {
			bt.nameToPos.put(name, pos);
			mediator[pos] = bt2.nameToPos.get(name);
			++pos;
		}
		int[] tuple;
		for (int[] tuple1 : bt1.table) {
			for (int[] tuple2 : bt2.table) {
				tuple = new int[newSize];
				// NOTE: two for-loops are more efficient for small arrays (approx 5 elements)
				//       than using System.arraycopy()
				for (int k = 0; k < vars1Size; k++)
					tuple[k] = tuple1[mediator[k]];
				for (int k = vars1Size; k < (vars1Size + vars2Size); k++)
					tuple[k] = tuple2[mediator[k]];
				table.add(tuple);
			}
		}
		return bt;
	}
	
	/**
	 *
	 */
	protected static void qsort(int[][] ia, TupleComparator tc) {
		Calc.quickSort(ia, 0, ia.length, tc);
	}
	
	/**
	 * slightly slower than mergeSort();
	 * nevertheless, thanks Bernie!
	 */
	private static void	quickSort(int[][] table, int low, int high, TupleComparator columnOrder) {
		if (high - low <= 2) {
			if (high - low <= 1)
				return;
			int[] lower = table[low];
			int[] higher = table[low + 1];
			if (columnOrder.compare(lower, higher) > 0) {
				table[low] = higher;
				table[low + 1] = lower;
			}
		} else {
			int[] pivotValue = table[low];
			int storeIndex = low + 1;
			for (int i = low + 1; i < high; ++i) {
				if (columnOrder.compare(pivotValue, table[i]) > 0) {
					int[] help = table[storeIndex];
					table[storeIndex] = table[i];
					table[i] = help;
					++storeIndex;
				}
			}
			table[low] = table[--storeIndex];
			if (storeIndex > low) {
				table[storeIndex] = pivotValue;
				quickSort(table, low, storeIndex, columnOrder);
			}
			quickSort(table, storeIndex + 1, high, columnOrder);
		}
	}

	/**
	 * Tuning parameter: list size at or below which insertion sort will be
	 * used in preference to mergesort or quicksort.
	 */
	private static final int INSERTIONSORT_THRESHOLD = 7;
	
	/**
	 * sorts an array ia of int[] (= tuples) according to a given int[] comparator tc
	 */
	/*
	protected static void msort(int[][] ia, TupleComparator tc) {
		int[][] aux = new int[ia.length][];
		// shallow copy ia into aux
		for (int i = 0; i < ia.length; i++)
			aux[i] = ia[i];
		Calc.mergeSort(aux, ia, 0, ia.length, 0, tc);
	}
	 */
	
	/**
	 * adaption of Sun's mergeSort() for Object[] to int[]
	 */
	/*
	private static void mergeSort(int[][] src, int[][] dest,
																int low, int high, int off,
																TupleComparator c) {
		int length = high - low;
		// insertion sort on smallest arrays
		if (length < Calc.INSERTIONSORT_THRESHOLD) {
			int[] aux;
	    for (int i = low; i < high; i++)
				for (int j = i; j > low && c.compare(dest[j-1], dest[j]) > 0; j--) {
					aux = dest[j];
					dest[j] = dest[j - 1];
					dest[j - 1] = aux;
				}
	    return;
		}
		// recursively sort halves of dest into src
		int destLow  = low;
		int destHigh = high;
		low  += off;
		high += off;
		int mid = (low + high) >>> 1;
		Calc.mergeSort(dest, src, low, mid, -off, c);
		Calc.mergeSort(dest, src, mid, high, -off, c);
		// if list is already sorted, just copy from src to dest; this is an
		// optimization that results in faster sorts for nearly ordered lists
		if (c.compare(src[mid - 1], src[mid]) <= 0) {
			System.arraycopy(src, low, dest, destLow, length);
			return;
		}
		// merge sorted halves (now in src) into dest
		for (int i = destLow, p = low, q = mid; i < destHigh; i++) {
			if (q >= high || p < mid && c.compare(src[p], src[q]) <= 0)
				dest[i] = src[p++];
			else
				dest[i] = src[q++];
		}
	}
	 */
	
	/**
	 * sorts an array ia of int[] (= tuples) according to an order established by compare0()
	 */
	protected static void msort0(int[][] ia, int[] col0) {
		int[][] aux = new int[ia.length][];
		// shallow copy ia into aux
		for (int i = 0; i < ia.length; i++)
			aux[i] = ia[i];
		Calc.mergeSort0(aux, ia, 0, ia.length, 0, col0);
	}
	
	/**
	 * adaption of Sun's mergeSort() for Object[] to int[]
	 */
	private static void mergeSort0(int[][] src, int[][] dest,
																int low, int high, int off,
																int[] col0) {
		int length = high - low;
		// insertion sort on smallest arrays
		if (length < Calc.INSERTIONSORT_THRESHOLD) {
			int[] aux;
	    for (int i = low; i < high; i++)
				for (int j = i; j > low && Calc.compare0(dest[j-1], dest[j], col0) > 0; j--) {
					aux = dest[j];
					dest[j] = dest[j - 1];
					dest[j - 1] = aux;
				}
	    return;
		}
		// recursively sort halves of dest into src
		int destLow  = low;
		int destHigh = high;
		low  += off;
		high += off;
		int mid = (low + high) >>> 1;
		Calc.mergeSort0(dest, src, low, mid, -off, col0);
		Calc.mergeSort0(dest, src, mid, high, -off, col0);
		// if list is already sorted, just copy from src to dest; this is an
		// optimization that results in faster sorts for nearly ordered lists
		if (Calc.compare0(src[mid - 1], src[mid], col0) <= 0) {
			System.arraycopy(src, low, dest, destLow, length);
			return;
		}
		// merge sorted halves (now in src) into dest
		for (int i = destLow, p = low, q = mid; i < destHigh; i++) {
			if (q >= high || p < mid && Calc.compare0(src[p], src[q], col0) <= 0)
				dest[i] = src[p++];
			else
				dest[i] = src[q++];
		}
	}
	
	/**
	 * sorts an array ia of int[] (= tuples) according to an order established by compare1()
	 */
	protected static void msort1(int[][] ia, int[] col1) {
		int[][] aux = new int[ia.length][];
		// shallow copy ia into aux
		for (int i = 0; i < ia.length; i++)
			aux[i] = ia[i];
		Calc.mergeSort1(aux, ia, 0, ia.length, 0, col1);
	}
	
	/**
	 * adaption of Sun's mergeSort() for Object[] to int[]
	 */
	private static void mergeSort1(int[][] src, int[][] dest,
																int low, int high, int off,
																int[] col1) {
		int length = high - low;
		// insertion sort on smallest arrays
		if (length < Calc.INSERTIONSORT_THRESHOLD) {
			int[] aux;
	    for (int i = low; i < high; i++)
				for (int j = i; j > low && Calc.compare1(dest[j-1], dest[j], col1) > 0; j--) {
					aux = dest[j];
					dest[j] = dest[j - 1];
					dest[j - 1] = aux;
				}
	    return;
		}
		// recursively sort halves of dest into src
		int destLow  = low;
		int destHigh = high;
		low  += off;
		high += off;
		int mid = (low + high) >>> 1;
		Calc.mergeSort1(dest, src, low, mid, -off, col1);
		Calc.mergeSort1(dest, src, mid, high, -off, col1);
		// if list is already sorted, just copy from src to dest; this is an
		// optimization that results in faster sorts for nearly ordered lists
		if (Calc.compare1(src[mid - 1], src[mid], col1) <= 0) {
			System.arraycopy(src, low, dest, destLow, length);
			return;
		}
		// merge sorted halves (now in src) into dest
		for (int i = destLow, p = low, q = mid; i < destHigh; i++) {
			if (q >= high || p < mid && Calc.compare1(src[p], src[q], col1) <= 0)
				dest[i] = src[p++];
			else
				dest[i] = src[q++];
		}
	}

	/**
	 * adaption of the comparator from TupleComparator as a static class method in Calc
	 */
	private static int compare(int[] tuple0, int[] tuple1, int[][] columns) {
		int[] col0 = columns[0];
		int[] col1 = columns[1];
		// the i's represent the vars!
		int diff;
		for (int i = 0; i < col0.length; ++i) {
			diff = tuple0[col0[i]] - tuple1[col1[i]];
			if (diff != 0)
				return diff;
		}
		return 0;
	}
	
	private static int compare0(int[] tuple0, int[] tuple1, int[] col0) {
		int diff;
		for (int i = 0; i < col0.length; ++i) {
			diff = tuple0[col0[i]] - tuple1[col0[i]];
			if (diff != 0)
				return diff;
		}
		return 0;
	}
	
	private static int compare1(int[] tuple0, int[] tuple1, int[] col1) {
		int diff;
		for (int i = 0; i < col1.length; ++i) {
			diff = tuple0[col1[i]] - tuple1[col1[i]];
			if (diff != 0)
				return diff;
		}
		return 0;
	}
	
	/**
	 * don't know whether this special arraycopy pays off (even though it avoids
	 * src and dest to be of type Object, it is not a native method)
	 */
	protected static void arraycopy(int[][] src, int srcPos,
																	int[][] dest, int destPos,
																	int length) {
		for (int i = srcPos; i < srcPos + length; i++)
			dest[i] = src[i];
	}
	
	/**
	 * private helper for join()
	 */
	private static void mergeAssignments(SortedSet<Integer> commonVars,
																			 SortedMap<Integer, Integer> va1,
																			 SortedMap<Integer, Integer> va2,
																			 SortedMap<Integer, Integer> newAssignment,
																			 int[] transformer) {
		int col = 0;
		for (Integer var : commonVars) {
			newAssignment.put(var, col);
			transformer[col] = va1.get(var);
			++col;
		}
		for (Integer var : va1.keySet()) {
			if (! commonVars.contains(var)) {
				newAssignment.put(var, col);
				transformer[col] = va1.get(var);
				++col;
			}
		}
		for (Integer var : va2.keySet()) {
			if (! commonVars.contains(var)) {
				newAssignment.put(var, col);
				transformer[col] = va2.get(var);
				++col;
			}
		}
	}
	
	/**
	 * @author (C) Bernd Kiefer, Hans-Ulrich Krieger
	 * @return a new table representing the join of tt1 and tt2
	 */
	public static BindingTable join(BindingTable tt1, BindingTable tt2) {
		
		BindingTable result = null;
		int[] transformer = null;
		int newTupleSize = -1;
		int tuple1Size = -1;
		
		SortedSet<Integer> commonVars = new TreeSet<Integer>(tt1.nameToPos.keySet());
		commonVars.retainAll(tt2.nameToPos.keySet());
		int subs = 0;
		if (commonVars.size() >= tt1.nameToPos.keySet().size()) { subs = 1; }
		if (commonVars.size() >= tt2.nameToPos.keySet().size()) { subs = 2; }   
		if (subs == 1) {
			// make table 2 the table to reduce 
			BindingTable help = tt2;
			tt2 = tt1;
			tt2 = help;
		}
		
		// check if one of the variable sets is a subset of the other;
		// if this is not the case, we create a new BindingTable, and the fact that
		// result is not null is an indicator that we collect tuples into the new
		// table instead of reducing tt1
		// NOTE (HUK): since we must guarantee that the shared local bindings are not destroyed,
		//             we always generate a NEW result table
		if (true) {    // was: (subs == 0)
			result = new BindingTable();
			
			result.nameToPos = new TreeMap<Integer, Integer>();
			result.table = new THashSet<int[]>();
			transformer = new int[tt1.nameToPos.size() + tt2.nameToPos.size()	- commonVars.size()];
			
			// create a new variable assignment for the result and a mapping from old
			// to new columns, the transformer
			Calc.mergeAssignments(commonVars, tt1.nameToPos, tt2.nameToPos, result.nameToPos, transformer);
			
			// NOTE (HUK): originally, the following condition was the very first lines of join();
			//             however, even if one of the tables is empty, we need a canonical nameToPos
			//             mapping
			// make sure none of the tables is empty
			if (tt1.table.isEmpty() || tt2.table.isEmpty()) {
				// was: return null;
				return result;
			}
			
			newTupleSize = transformer.length;
			tuple1Size = tt1.nameToPos.size();
		}
		
		// BEGIN of TupleComparator constructor code
		int[][] columns;
		int[] vars = new int[commonVars.size()];
		int ii = 0;
		for (int var : commonVars) {
			vars[ii++] = var;
		}
		columns = new int[2][];
		columns[0] = new int[vars.length];
		columns[1] = new int[vars.length];
		ii = 0;
		for (Integer var : vars) {
			columns[0][ii] = tt1.nameToPos.get(var);
			columns[1][ii] = tt2.nameToPos.get(var);
			++ii;
		}
		// END of TupleComparator constructor code

		// now produce a sorted list view of both tables
		
		int pos;
		int[][] tl1 = new int[tt1.table.size()][];
		pos = 0;
		for (int[] tuple : tt1.table)
			tl1[pos++] = tuple;
		Calc.msort0(tl1, columns[0]);
		
		int[][] tl2 = new int[tt2.table.size()][];
		pos = 0;
		for (int[] tuple : tt2.table)
			tl2[pos++] = tuple;
		Calc.msort1(tl2, columns[1]);
		
		// traverse them in order and remove the non-fitting triples
		
		int current1 = 0;
		int current2 = 0;
		
		// ok 'cause we tested in the beginning
		
		int[] tuple1, tuple2, newTuple;
		int res, start2, end2;
		
		do {
			tuple1 = tl1[current1];
			tuple2 = tl2[current2];
			res = Calc.compare(tuple1, tuple2, columns);
			if (res < 0) { // tuple in 1 not in 2: remove in 1
				// result == null means we reduce tt1 and return it as result
				if (result == null) {
					// remove it in the set, too, since we don't want to create a new set
					tt1.table.remove(tuple1);
				}
				++current1;
				continue;
			}
			if (res > 0) { // tuple in 2 not in 1: skip in 2
				++current2;
				continue;
			}
			// have to be equal now: skip both
			if (result == null) {
				++current1;
				++current2;
			} else {
				// This block produces a 'local' Cartesian product of corresponding triples.
				// Initially, we know that the pointers point to equal elements in the tuple
				// tables. We then take the tuple in the first table and `multiply' it with
				// the equal tuples in the second.  After the first loop iteration, we store
				// the end of the block of equal tuples in the variable end2 (it is rather a
				// pointer to the first tuple that is not equal anymore).  We then proceed to
				// the next tuple in table 1 and try to do the same, and stop if we hit a
				// tuple that is not equal to the first tuple in the block of table 2.
				// Finally, the pointer of table 2 is set to end2
				start2 = current2;  //pointers[1];
				end2 = tl2.length;
				
				int compres = 0;  // result of comparison
				
				do {
					tuple1 = tl1[current1];
					current2 = start2;
					do {
						tuple2 = tl2[current2];
						compres = Calc.compare(tuple1, tuple2, columns);
						if (compres == 0) {
							//addCombined(result, tuple1, tuple2, transformer);
							newTuple = new int[newTupleSize];
							int i = 0;
							while(i < tuple1Size) {
								newTuple[i] = tuple1[transformer[i]];
								++i;
							}
							while(i < newTupleSize) {
								newTuple[i] = tuple2[transformer[i]];
								++i;
							}
							result.table.add(newTuple);
							++current2;
						}
					} while (compres == 0 && current2 < end2);
					// save end pos for final adaption of pointer[1]
					if (current2 != start2) {
						end2 = current2;
						++current1;
					}
				} while ((current1 < tl1.length) && (current2 != start2));
				current2 = end2;
			}
		} while (current1 < tl1.length && current2 < tl2.length);
		if (result == null) {
			// remove all remaining elements of tl1    
			while (current1 < tl1.length) {
				tt1.table.remove(tl1[current1++]);
			}
		}
		return (result == null ? tt1 : result);
	}
	
	/*
	public static BindingTable join(BindingTable tt1, BindingTable tt2) {
		
		BindingTable result = null;
		int[] transformer = null;
		int newTupleSize = -1;
		int tuple1Size = -1;
		
		SortedSet<Integer> commonVars = new TreeSet<Integer>(tt1.nameToPos.keySet());
		commonVars.retainAll(tt2.nameToPos.keySet());
		int subs = 0;
		if (commonVars.size() >= tt1.nameToPos.keySet().size()) { subs = 1; }
		if (commonVars.size() >= tt2.nameToPos.keySet().size()) { subs = 2; }   
		if (subs == 1) {
			// make table 2 the table to reduce 
			BindingTable help = tt2;
			tt2 = tt1;
			tt2 = help;
		}
		
		TupleComparator tc = new TupleComparator(commonVars, tt1.nameToPos, tt2.nameToPos);
		// check if one of the variable sets is a subset of the other;
		// if this is not the case, we create a new BindingTable, and the fact that
		// result is not null is an indicator that we collect tuples into the new
		// table instead of reducing tt1
		// NOTE (HUK): since we must guarantee that the shared local bindings are not destroyed,
		//             we always generate a NEW result table
		if (true) {    // was: (subs == 0)
			result = new BindingTable();
			
			result.nameToPos = new TreeMap<Integer, Integer>();
			result.table = new THashSet<int[]>();
			transformer = new int[tt1.nameToPos.size() + tt2.nameToPos.size()	- commonVars.size()];
			
			// create a new variable assignment for the result and a mapping from old
			// to new columns, the transformer
			Calc.mergeAssignments(commonVars, tt1.nameToPos, tt2.nameToPos, result.nameToPos, transformer);
			
			// NOTE (HUK): originally, the following condition was the very first lines of join();
			//             however, even if one of the tables is empty, we need a canonical nameToPos
			//             mapping
			// make sure none of the tables is empty
			if (tt1.table.isEmpty() || tt2.table.isEmpty()) {
				// was: return null;
				return result;
			}
			
			newTupleSize = transformer.length;
			tuple1Size = tt1.nameToPos.size();
		}
		
		// HUK: tried below code for tl1/tl2 with arrays instead of ArrayList
		//      does NOT give any reproducable advantage using the profiler
		
		// Now produce a sorted list view of both tables
		
		int pos;
		tc.setWhich(0, 0);
		int[][] tl1 = new int[tt1.table.size()][];
		pos = 0;
		for (int[] tuple : tt1.table)
			tl1[pos++] = tuple;
		Calc.msort(tl1, tc);
		
		tc.setWhich(1, 1);
		int[][] tl2 = new int[tt2.table.size()][];
		pos = 0;
		for (int[] tuple : tt2.table)
			tl2[pos++] = tuple;
		Calc.msort(tl2, tc);
		
		// traverse them in order and remove the non-fitting triples
		
		tc.setWhich(0, 1);
		int current1 = 0;
		int current2 = 0;
		// ok 'cause we tested in the beginning
		
		do {
			int[] tuple1 = tl1[current1];
			int[] tuple2 = tl2[current2];
			int res = tc.compare(tuple1, tuple2);
			if (res < 0) { // tuple in 1 not in 2: remove in 1
				// result == null means we reduce tt1 and return it as result
				if (result == null) {
					// remove it in the set, too, since we don't want to create a new set
					tt1.table.remove(tuple1);
				}
				++current1;
				continue;
			}
			if (res > 0) { // tuple in 2 not in 1: skip in 2
				++current2;
				continue;
			}
			// have to be equal now: skip both
			//assert( tc.compare(tuple1, tuple2) == 0 );
			if (result == null) {
				++current1;
				++current2;
			} else {
				// This block produces a 'local' Cartesian product of corresponding triples.
				// Initially, we know that the pointers point to equal elements in the tuple
				// tables. We then take the tuple in the first table and `multiply' it with
				// the equal tuples in the second.  After the first loop iteration, we store
				// the end of the block of equal tuples in the variable end2 (it is rather a
				// pointer to the first tuple that is not equal anymore).  We then proceed to
				// the next tuple in table 1 and try to do the same, and stop if we hit a
				// tuple that is not equal to the first tuple in the block of table 2.
				// Finally, the pointer of table 2 is set to end2
				int start2 = current2;  //pointers[1];
				int end2 = tl2.length;
				
				int compres = 0;  // result of comparison
				
				do {
					tuple1 = tl1[current1];
					current2 = start2;
					do {
						tuple2 = tl2[current2];
						compres = tc.compare(tuple1, tuple2);
						if (compres == 0) {
							//addCombined(result, tuple1, tuple2, transformer);
							int[] newTuple = new int[newTupleSize];
							int i = 0;
							while(i < tuple1Size) {
								newTuple[i] = tuple1[transformer[i]];
								++i;
							}
							while(i < newTupleSize) {
								newTuple[i] = tuple2[transformer[i]];
								++i;
							}
							result.table.add(newTuple);
							++current2;
						}
					} while (compres == 0 && current2 < end2);
					// save end pos for final adaption of pointer[1]
					if (current2 != start2) {
						end2 = current2;
						++current1;
					}
				} while ((current1 < tl1.length) && (current2 != start2));
				current2 = end2;
			}
		} while (current1 < tl1.length && current2 < tl2.length);
		if (result == null) {
			// remove all remaining elements of tl1    
			while (current1 < tl1.length) {
				tt1.table.remove(tl1[current1++]);
			}
		}
		return (result == null ? tt1 : result);
	}
	 */
	
}
