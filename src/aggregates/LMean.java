package de.dfki.lt.hfc.aggregates;

import java.util.*;
import de.dfki.lt.hfc.*;
import de.dfki.lt.hfc.types.XsdLong;

/**
 * this aggregational operator LMean
 * = take the mean/average of a column of long ints
 * is supposed to be given a table of _one_ column, even though
 * args (the binding table) might have more than one column;
 * we always take the first column (position 0) for determining
 * the mean
 *
 * example query:
 *   SELECT ?val
 *   WHERE ?s <value> ?val
 *   AGGREGATE ?mean = LMean ?val
 * returns a binding table of one row and one column, headed by
 * the label "?mean"
 *
 * @author (C) Hans-Ulrich Krieger
 * @since JDK 1.5
 * @version Fri Sep 11 14:22:02 CEST 2015
 */
public final class LMean extends AggregationalOperator {

  /**
   * nameToPos and nameToExternalName of args are not used here
   */
  public BindingTable apply(BindingTable args,
                            SortedMap<Integer, Integer> nameToPos,
                            Map<Integer, String> nameToExternalName) {
    // the resulting table
    final BindingTable bt = new BindingTable(nameToPos, nameToExternalName, this.tupleStore);
    final int size = args.table.size();
    // is args empty? if so, return an _empty_ result table!
    if (size == 0)
      return bt;
    // a non-zero input table
    long sum = 0;
    for (int[] elem : args.table)
      sum += ((XsdLong)getObject(elem[0])).value;
    XsdLong mean = new XsdLong(sum / size);
    // always register the corresponding XSD long -- could be new to tuple store
    int id = registerObject(mean.toString(Namespace.shortIsDefault), mean);
    // add this XSD int as the only unary tuple to the resulting table
    bt.table.add(new int[]{id});
    return bt;
  }

}
