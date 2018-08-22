package de.dfki.lt.hfc.aggregates;

import de.dfki.lt.hfc.AggregationalOperator;
import de.dfki.lt.hfc.BindingTable;

import java.util.Map;
import java.util.SortedMap;

/**
 * returns a shallow copy of the input binding table, changing only the heading of the columns
 *
 * @author (C) Hans-Ulrich Krieger
 * @version Tue Dec 22 11:25:19 CET 2015
 * @since JDK 1.5
 */
public final class Identity extends AggregationalOperator {

  /**
   *
   */
  public BindingTable apply(BindingTable args,
                            SortedMap<Integer, Integer> nameToPos,
                            Map<Integer, String> nameToExternalName) {
    return new BindingTable(args.table, nameToPos, nameToExternalName, this.tupleStore);
  }

}
