package de.dfki.lt.hfc.aggregates;

import de.dfki.lt.hfc.AggregationalOperator;
import de.dfki.lt.hfc.BindingTable;
import de.dfki.lt.hfc.types.XsdInt;

import java.util.Map;
import java.util.SortedMap;

/**
 * an example where CountDistinct might be applied:
 * SELECT ?s
 * WHERE ?s ?p ?o
 * AGGREGATE ?card = CountDistinct ?s
 * this is also correct (however variable ?o is not used):
 * SELECT ?s ?o
 * WHERE ?s ?p ?o
 * AGGREGATE ?card = CountDistinct ?s
 * this is even OK, although CountDistinct does NOT use ?o:
 * SELECT ?s ?o
 * WHERE ?s ?p ?o
 * AGGREGATE ?card = CountDistinct ?s ?o
 *
 * @author (C) Hans-Ulrich Krieger
 * @version Wed Nov 25 15:01:05 CET 2009
 * @since JDK 1.5
 */
public final class Count extends AggregationalOperator {

  /**
   * the resulting binding table (1 row, 1 column) is equipped with the corresponding
   * tuple store object (as available from AggregationalOperator through this.tupleStore)
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
    // since we might even count multiple elements, it suffices to ask for the
    // cardinality of args.table;
    // note that this method does not check whether we have more than one column, or
    // even one column
    XsdInt card = new XsdInt(args.table.size());
    // since a table is returned and since we are working with XSD atoms, an XSD int must be constructed
    int id = registerObject(card.toString(this.tupleStore.namespace.shortIsDefault), card);
    // add this XSD int as the only unary tuple to the resulting table
    bt.table.add(new int[]{id});
    return bt;
  }

}
