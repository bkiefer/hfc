package de.dfki.lt.hfc.aggregates;

import de.dfki.lt.hfc.AggregationalOperator;
import de.dfki.lt.hfc.BindingTable;
import de.dfki.lt.hfc.FunctionalOperator;

import java.util.Iterator;
import java.util.Map;
import java.util.SortedMap;

/**
 * this aggregational operator DTMin
 * = take the minimum of a column of XSD date times
 * is supposed to be given a table of _one_ column, even though
 * args (the binding table) might have more than one column;
 * we always take the first column (position 0) for determining
 * the minimum
 * <p>
 * example query:
 * SELECT ?start
 * WHERE <foo> <bar> <baz> ?start ?end
 * AGGREGATE ?min = DTMin ?start
 * returns a binding table of one row and one column, headed by
 * the label "?min"
 *
 * @author (C) Hans-Ulrich Krieger
 * @version Mon Sep 14 13:21:56 CEST 2015
 * @since JDK 1.5
 */
public final class DTMin extends AggregationalOperator {

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
    // a non-zero input table (note: the internal representation is a set !)
    Iterator<int[]> it = args.table.iterator();
    int minId = it.next()[0];
    int currId;
    int result;
    final int[] argpair = new int[2];  // always reuse outer pair object
    while (it.hasNext()) {
      currId = it.next()[0];
      argpair[0] = currId;
      argpair[1] = minId;
      result = callFunctionalOperator("DTLess", argpair);
      if (result == FunctionalOperator.TRUE)
        minId = currId;
    }
    // minimum already part of input -- no need ro create new XsdDateTime object and to register it
    bt.table.add(new int[]{minId});
    return bt;
  }

}
