package de.dfki.lt.hfc.aggregates;

import java.util.*;
import de.dfki.lt.hfc.*;
import de.dfki.lt.hfc.types.XsdLong;

/**
 * this aggregational operator LMax
 * = take the maximum of a column of long ints
 * is supposed to be given a table of _one_ column, even though
 * args (the binding table) might have more than one column;
 * we always take the first column (position 0) for determining
 * the maximum
 *
 * example query:
 *   SELECT ?val
 *   WHERE ?s <value> ?val
 *   AGGREGATE ?max = LMax ?val
 * returns a binding table of one row and one column, headed by
 * the label "?max"
 *
 * @author (C) Hans-Ulrich Krieger
 * @since JDK 1.5
 * @version Fri Sep 11 18:02:57 CEST 2015
 */
public final class LMax extends AggregationalOperator {
  
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
    // a non-zero input table (note: the internal representation is a set !)
    long max = Long.MIN_VALUE;
    long curr;
    for (int[] elem : args.table) {
      curr = ((XsdLong)getObject(elem[0])).value;
      if (curr > max)
        max = curr;
    }
    XsdLong lmax = new XsdLong(max);
    // no need to register the corresponding XSD long, but method returns the corresponding ID
    int id = registerObject(lmax.toString(Namespace.shortIsDefault), lmax);
    // add this XSD int as the only unary tuple to the resulting table
    bt.table.add(new int[]{id});
    return bt;
  }
  
}
