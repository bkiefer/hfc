package de.dfki.lt.hfc.aggregates;

import java.util.*;
import de.dfki.lt.hfc.*;
import de.dfki.lt.hfc.types.XsdFloat;

/**
 * this aggregational operator LSum
 * = take the sum of a column of float
 * is supposed to be given a table of _one_ column, even though
 * args (the binding table) might have more than one column;
 * we always take the first column (position 0) for determining
 * the sum of the float ints
 *
 * example query:
 *   SELECT ?val
 *   WHERE ?s <value> ?val
 *   AGGREGATE ?sum = LSum ?val
 * returns a binding table of one row and one column, headed by
 * the label "?sum"
 *
 * @author (C) Hans-Ulrich Krieger
 * @since JDK 1.5
 * @version Mon Sep 14 13:21:56 CEST 2015
 */
public final class FSum extends AggregationalOperator {

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
    float sum = 0;
    for (int[] elem : args.table)
      sum += ((XsdFloat)getObject(elem[0])).value;
    XsdFloat lsum = new XsdFloat(sum);
    // always register the corresponding XSD float -- could be new to tuple store
    int id = registerObject(lsum.toString(this.tupleStore.namespace.shortIsDefault), lsum);
    // add this XSD int as the only unary tuple to the resulting table
    bt.table.add(new int[]{id});
    return bt;
  }

}
