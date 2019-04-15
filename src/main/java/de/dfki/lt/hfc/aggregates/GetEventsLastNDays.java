package de.dfki.lt.hfc.aggregates;

import de.dfki.lt.hfc.AggregationalOperator;
import de.dfki.lt.hfc.BindingTable;
import de.dfki.lt.hfc.types.XsdInt;
import de.dfki.lt.hfc.types.XsdLong;

import java.util.HashSet;
import java.util.Map;
import java.util.SortedMap;

/**
 * The aggregational operator GetEventsLastNDays returns a table of all events
 * that occurred during the last n days starting from today 00:00.
 * It is supposed to be given a table of several columns of the following form
 * arg1 ... argN time n
 * where the tuples <arg1, ..., argN> with timestamps during the last n days are returned.
 * <p>
 * Note that time is required to be filled with XSD long integers
 * <p>
 * example query:
 * <p>
 * // return all labvalues for children collected in the last 3 days
 * SELECT ?child ?prop ?val ?t
 * WHERE ?child <rdf:type> <dom:Child> ?t1
 * & ?child <dom:hasLabValue> ?lv ?t2
 * & ?lv ?prop ?val ?t
 * AGGREGATE ?measurement ?result ?patient ?time = GetEventsLastNDays ?prop ?val ?child ?t ?t "3"^^<xsd:int>
 *
 * @author (C) Christian Willms
 * @version Tue Feb 13 10:41:55 CET 2018
 * @since JDK 1.8
 */
public class GetEventsLastNDays extends AggregationalOperator {

  //////////////////////////////////////////////////
  // Some useful constants
  //////////////////////////////////////////////////
  /**
   * One day (24 hours) represented as milliseconds
   */
  static final long DAY = 86400000;
  static final long WEEK = 604800000;

  /**
   * general form of the aggregate call:  ?arg1' ... ?argN' = LGetLatest ?arg1 ... ?argN ?time ?n
   */
  public BindingTable apply(BindingTable args,
                            SortedMap<Integer, Integer> nameToPos,
                            Map<Integer, String> nameToExternalName) {
    final long currentTime = System.currentTimeMillis();
    // use a linked hash set here to guarantee the "right" iteration ordering
    final HashSet<int[]> resultTable = new HashSet<int[]>();
    final BindingTable bt = new BindingTable(resultTable,
            nameToPos,
            nameToExternalName,
            this.tupleStore);
    // is args empty? if so, return an _empty_ result table!
    if (args.table.size() == 0)
      return bt;
    // move from the set representation of the input table to an array for efficient sorting;
    int[][] table = args.table.toArray(new int[args.table.size()][]);
    final int rowLength = table[0].length;  // rows are of same length
    internalApply(currentTime, resultTable, table, rowLength);
    return bt;
  }

  protected void internalApply(long currentTime, HashSet<int[]> resultTable, int[][] table, int rowLength) {
    final int sortColumnNo = rowLength - 2;
    int n = ((XsdInt) (getObject(table[0][rowLength - 1]))).value;
    long start = computeMidnight(currentTime);
    long end = start - (n * DAY);
    for (int[] entry : table) {
      long currentValue = ((XsdLong) (getObject(entry[sortColumnNo]))).value;
      if (currentValue <= start && currentValue >= end)
        resultTable.add(entry);
    }
  }


  protected long computeMidnight(long currentTime) {
    return currentTime - (currentTime % DAY);
  }

  protected long computeLastSunday(long currentTime) {
    return (currentTime - (currentTime % WEEK)) - (3 * DAY);
  }
}
