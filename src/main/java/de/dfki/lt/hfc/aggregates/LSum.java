package de.dfki.lt.hfc.aggregates;

import de.dfki.lt.hfc.AggregationalOperator;
import de.dfki.lt.hfc.BindingTable;
import de.dfki.lt.hfc.types.XsdLong;

import java.util.Map;
import java.util.SortedMap;

/**
 * this aggregational operator LSum
 * = take the sum of a column of long ints
 * is supposed to be given a table of _one_ column, even though
 * args (the binding table) might have more than one column;
 * we always take the first column (position 0) for determining
 * the sum of the long ints
 * <p>
 * example query:
 * SELECT ?val
 * WHERE ?s <value> ?val
 * AGGREGATE ?sum = LSum ?val
 * returns a binding table of one row and one column, headed by
 * the label "?sum"
 *
 * @author (C) Hans-Ulrich Krieger
 * @version Mon Sep 14 13:21:56 CEST 2015
 * @since JDK 1.5
 */
public final class LSum extends AggregationalOperator {

  /**
   * nameToPos and nameToExternalName of args are not used here
   */
  public BindingTable apply(BindingTable args,
                            SortedMap<Integer, Integer> nameToPos,
                            Map<Integer, String> nameToExternalName) {
    // the resulting table
    final BindingTable bt = new BindingTable(nameToPos, nameToExternalName, this.tupleStore);
    // is args empty? if so, return an _empty_ result table!
    if (args.table.size() == 0)
      return bt;
    // a non-zero input table
    long sum = 0;
    for (int[] elem : args.table)
      sum += ((XsdLong) getObject(elem[0])).value;
    XsdLong lsum = new XsdLong(sum);
    // always register the corresponding XSD long -- could be new to tuple store
    int id = registerObject(lsum.toString(this.tupleStore.namespace.shortIsDefault), lsum);
    // add this XSD int as the only unary tuple to the resulting table
    bt.table.add(new int[]{id});
    return bt;
  }

}
