package de.dfki.lt.hfc.aggregates;

import java.util.*;
import de.dfki.lt.hfc.*;

/**
 * this aggregational operator DTMax
 * = take the maximum of a column of XSD date times
 * is supposed to be given a table of _one_ column, even though
 * args (the binding table) might have more than one column;
 * we always take the first column (position 0) for determining
 * the maximum
 *
 * example query:
 *   SELECT ?end
 *   WHERE <foo> <bar> <baz> ?start ?end
 *   AGGREGATE ?max = DTMax ?end
 * returns a binding table of one row and one column, headed by
 * the label "?max"
 *
 * @author (C) Hans-Ulrich Krieger
 * @since JDK 1.5
 * @version Tue Sep 15 13:17:16 CEST 2015
 */
public final class DTMax extends AggregationalOperator {
  
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
    int maxId = it.next()[0];
    int currId;
    int result;
    final int[] argpair = new int[2];  // always reuse outer pair object
    while (it.hasNext()) {
      currId = it.next()[0];
      argpair[0] = maxId;
      argpair[1] = currId;
      result = callFunctionalOperator("DTLess", argpair);
      if (result == FunctionalOperator.TRUE)
        maxId = currId;
    }
    // minimum already part of input -- no need ro create new XsdDateTime object and to register it
    bt.table.add(new int[]{maxId});
    return bt;
  }
  
}
