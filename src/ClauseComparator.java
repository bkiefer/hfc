package de.dfki.lt.hfc;

import java.util.*;

/**
 * implements a heuristic in order to establish a linear order on the
 * clauses of the antecedent of a rule;
 * to achieve this, I use the Arrays.sort() method together with instances
 * of this ClauseComparator class;
 *
 * the heuristic realizes a cost function that takes into account the nature
 * of tuple elements, i.e., whether they are variables or constants;
 * regarding variables, we distinguish between proper variables P (bad!) and
 * don't care variables DC (good!);
 * constants split into XSD atoms XA (good) and URIs U (bad!);
 * we further distinguish between proper variables in first position (bad)
 * and those _not_ in first position (good);
 * we further take into account duplicate variables (the more, the better);
 * in principle, we _might_ also distinguish between URIs, e.g., rdf:type is
 * probably a `worse' URI when comparing to, say, owl:equivalentClass, since
 * every URI comes with several rdf:type triples;
 * note also that compare() below must be able to compare tuples of different
 * length (which could, in principle, be the case);
 *
 * THE COST FUNCTION:
 *   cost(DC)           =    0
 *   cost(XA)           =    1
 *   cost(P | #occ > 1) =    2  // only one occurrence is counted
 *   cost(U)            =   10
 *   cost(P | pos > 1)  =  100
 *   cost(P | pos = 1)  = 1000
 *   cost(<t1, ..., tn>) = cost(t1) + ... + cost(tn)
 *
 * @author (C) Hans-Ulrich Krieger
 * @since JDK 1.5
 * @version 2.0, Wed Jun 24 14:36:20 CEST 2009
 */
public class ClauseComparator implements Comparator<int[]> {

	private HashSet<Integer> dontCareVariables;
	
	private TupleStore tupleStore;
	
	/**
	 * at the moment only a binary constructor taking into account
	 * proper variables and don't care variables;
	 * blank node vars are not of interest, since they only occur on
	 * the RHS of a rule;
	 */
	public ClauseComparator(HashSet<Integer> dontCareVariables,
													TupleStore tupleStore) {
		this.dontCareVariables = dontCareVariables;
		this.tupleStore = tupleStore;
	}
	
	/**
	 * compares tuples of potentially different length according to the above
	 * cost function
	 */
	public int compare(int[] clause1, int[] clause2) {
		return (computeCost(clause1) - computeCost(clause2));
	}
	
	private int computeCost(int[] clause) {
		int id;
		// first of all, record any duplicates
		HashMap<Integer, Integer> varToNoOcc = new HashMap<Integer, Integer>();
		for (int i = 0; i < clause.length; i++) {
			id = clause[i];
			if (varToNoOcc.containsKey(id))
				varToNoOcc.put(id, varToNoOcc.get(id) + 1);
			else
				varToNoOcc.put(id, 1);
		}
		// then compute the cost
		int c = 0;
		for (int i = 0; i < clause.length; i++) {
			id = clause[i];
			if (RuleStore.isVariable(id)) {
				// don't care var
				if (this.dontCareVariables.contains(id))
					continue;
				// duplicate var
				else if (varToNoOcc.get(id) > 1)
					continue;
				else {
					if (i == 0)
						// proper var in first position
						c = c + 1000;
					else
						// all other proper vars
						c = c + 100;
				}
			}
			else if (this.tupleStore.isAtom(id))
				// XSD atom
				c = c + 1;
			else
				// URI
				c = c + 10;
		}
		// and finally add costs for duplicates
		for (Integer i : varToNoOcc.keySet()) {
			if (varToNoOcc.get(i) > 1)
				c = c + 2;
		}
		return c;
	}
	
}
