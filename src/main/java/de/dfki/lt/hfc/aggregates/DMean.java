package de.dfki.lt.hfc.aggregates;

import de.dfki.lt.hfc.AggregationalOperator;
import de.dfki.lt.hfc.BindingTable;
import de.dfki.lt.hfc.types.XsdDouble;

import java.util.Map;
import java.util.SortedMap;

/**
 * this aggregational operator DMean
 * = take the mean/average of a column of doubles
 * is supposed to be given a table of _one_ column, even though
 * args (the binding table) might have more than one column;
 * we always take the first column (position 0) for determining
 * the mean
 * <p>
 * example query:
 * SELECT ?val
 * WHERE ?s <value> ?val
 * AGGREGATE ?mean = DMean ?val
 * returns a binding table of one row and one column, headed by
 * the label "?mean"
 *
 * @author (C) Hans-Ulrich Krieger
 * @version Fri Sep 11 14:22:02 CEST 2015
 * @since JDK 1.5
 */
public final class DMean extends AggregationalOperator {

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
    double sum = 0;
    for (int[] elem : args.table)
      sum += ((XsdDouble) getObject(elem[0])).value;
    XsdDouble mean = new XsdDouble(sum / size);
    // always register the corresponding XSD long -- could be new to tuple store
    int id = registerObject(mean.toString(), mean);
    // add this XSD int as the only unary tuple to the resulting table
    bt.table.add(new int[]{id});
    return bt;
  }

}
