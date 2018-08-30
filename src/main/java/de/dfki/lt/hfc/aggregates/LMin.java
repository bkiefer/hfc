package de.dfki.lt.hfc.aggregates;

import de.dfki.lt.hfc.AggregationalOperator;
import de.dfki.lt.hfc.BindingTable;
import de.dfki.lt.hfc.types.XsdLong;

import java.util.Map;
import java.util.SortedMap;

/**
 * this aggregational operator LMin
 * = take the minimum of a column of long ints
 * is supposed to be given a table of _one_ column, even though
 * args (the binding table) might have more than one column;
 * we always take the first column (position 0) for determining
 * the minimum
 * <p>
 * example query:
 * SELECT ?val
 * WHERE ?s <value> ?val
 * AGGREGATE ?min = LMin ?val
 * returns a binding table of one row and one column, headed by
 * the label "?min"
 *
 * @author (C) Hans-Ulrich Krieger
 * @version Fri Sep 11 18:05:32 CEST 2015
 * @since JDK 1.5
 */
public final class LMin extends AggregationalOperator {

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
    long min = Long.MAX_VALUE;
    long curr;
    for (int[] elem : args.table) {
      curr = ((XsdLong) getObject(elem[0])).value;
      if (curr < min)
        min = curr;
    }
    XsdLong lmin = new XsdLong(min);
    // // no need to register the corresponding XSD long, but method also returns the corresponding ID
    int id = registerObject(lmin.toString(), lmin);
    // add this XSD int as the only unary tuple to the resulting table
    bt.table.add(new int[]{id});
    return bt;
  }

}
