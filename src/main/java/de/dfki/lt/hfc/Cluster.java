package de.dfki.lt.hfc;

import java.util.*;
import gnu.trove.set.hash.*;

/**
 * @author (C) Hans-Ulrich Krieger
 * @since JDK 1.5
 * @version Fri Feb 19 10:55:59 CET 2010
 */
public class Cluster {

	/**
	 * the binding table for the proper LHS-only variables of the cluster from the current iteration
	 */
	protected BindingTable bindingTable = null;

	/**
	 * instead of reduplicating the nameToPos mapping from bindingTable, we only store the table for
	 * information from the last iteration
	 */
	protected Set<int[]> table = new THashSet<int[]>();

	/**
	 * insted of reduplicating the nameToPos mapping from bindingTable, we only store the table for
	 * new information
	 */
	protected Set<int[]> delta = null;

	/**
	 * insted of reduplicating the nameToPos mapping from bindingTable, we only store the table for
	 * information from the old iteration
	 */
	protected Set<int[]> old = null;

	/**
	 * a list of position indices telling the system which LHS clauses will go together for this cluster
	 */
	protected ArrayList<Integer> positions;

	/**
	 * a set of proper variables that are involved in this cluster
	 */
	protected HashSet<Integer> variables;

	/**
	 * applicable var-var ineqs
	 */
	protected ArrayList<Integer> varvarIneqs;

	/**
	 * applicable var-const ineqs
	 */
	protected ArrayList<Integer> varconstIneqs;

	/**
	 * applicable predicates (wo/ in-eqs, of course)
	 */
	protected ArrayList<Predicate> tests;

	/**
	 * init forms of fields are used
	 */
	protected Cluster() {
	}

	/**
	 * copy constructor for exclusive use in the copy constructor of Rule
	 */
	protected Cluster(Cluster cluster, TupleStore tstore) {
		// use copy constructor for bindingTable;
		// shallow copy only table, delta and old, but make sure that the table
		// field of bindingTable is identical to table field of the cluster
		if (cluster.bindingTable == null) {
			this.bindingTable = null;
			this.table = new THashSet<int[]>();
			this.delta = null;
			this.old = null;
		}
		else {
			this.bindingTable = new BindingTable(cluster.bindingTable, tstore);
			this.table = new THashSet<int[]>(this.bindingTable.table);
			this.delta = (cluster.delta == null) ? null : new THashSet<int[]>(cluster.delta);
			this.old = (cluster.old == null) ? null : new THashSet<int[]>(cluster.old);
		}
		this.positions = cluster.positions;
		this.variables = cluster.variables;
		this.varvarIneqs = cluster.varvarIneqs;
		this.varconstIneqs = cluster.varconstIneqs;
		this.tests = cluster.tests;
	}

}
