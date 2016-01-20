package de.dfki.lt.hfc.aggregates;

import java.util.*;
import de.dfki.lt.hfc.*;

/**
 * returns a shallow copy of the input binding table, changing only the heading of the columns
 *
 * @author (C) Hans-Ulrich Krieger
 * @since JDK 1.5
 * @version Tue Dec 22 11:25:19 CET 2015
 */
public final class Identity extends AggregationalOperator {
	
	/**
	 *
	 */
	public BindingTable apply(BindingTable args,
														SortedMap<Integer, Integer> nameToPos,
														Map<Integer, String> nameToExternalName) {
		return new BindingTable(args.table, nameToPos, nameToExternalName, this.tupleStore);
	}
	
}
