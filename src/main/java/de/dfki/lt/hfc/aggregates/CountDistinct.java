package de.dfki.lt.hfc.aggregates;

import java.util.*;
import de.dfki.lt.hfc.*;
import de.dfki.lt.hfc.types.XsdInt;

/**
 * an example where CountDistinct might be applied:
 *   SELECT ?s
 *   WHERE ?s ?p ?o
 *   AGGREGATE ?card = CountDistinct ?s
 * this is also correct (however variable ?o is not used):
 *   SELECT ?s ?o
 *   WHERE ?s ?p ?o
 *   AGGREGATE ?card = CountDistinct ?s
 * this is even OK, although CountDistinct does NOT use ?o:
 *   SELECT ?s ?o
 *   WHERE ?s ?p ?o
 *   AGGREGATE ?card = CountDistinct ?s ?o
 *
 * @author (C) Hans-Ulrich Krieger
 * @since JDK 1.5
 * @version Tue Nov 24 16:57:07 CET 2009
 */
public final class CountDistinct extends AggregationalOperator {
	
	/**
	 * the resulting binding table (1 row, 1 column) is equipped with the corresponding
	 * tuple store object (as avalable from AggregationalOperator through this.tupleStore)
	 * and the two parameter mapping nameToPos and nameToExternalName for potential later
	 * output;
	 * note that nameToPos and nameToExternalName of args are not used -- in fact, these
	 * fields are assigned the null value
	 */
	public BindingTable apply(BindingTable args,
														SortedMap<Integer, Integer> nameToPos,
														Map<Integer, String> nameToExternalName) {
		// the resulting table
    final BindingTable bt = new BindingTable(nameToPos, nameToExternalName, this.tupleStore);
		// since we do not count multiple elements, it does NOT suffice to ask for the
		// cardinality of args.table (as is implemented by de.dfki.lt.hfc.aggregates.Count);
		// note that this method does not check whether we have more than one column, or
		// even one column
		HashSet<Integer> firstColumnElements = new HashSet<Integer>();
		for (int[] elem : args.table)
			firstColumnElements.add(elem[0]);
		// count distinct elements
		XsdInt card = new XsdInt(firstColumnElements.size());
		// since a table is returned and since we are working with XSD atoms, an XSD int must be constructed
		int id = registerObject(card.toString(this.tupleStore.namespace.shortIsDefault), card);
		// add this XSD int as the only unary tuple to the resulting table
		bt.table.add(new int[]{id});
		return bt;
	}
	
}
