package de.dfki.lt.hfc.aggregates;

import de.dfki.lt.hfc.AggregationalOperator;
import de.dfki.lt.hfc.BindingTable;
import de.dfki.lt.hfc.types.XsdInt;
import de.dfki.lt.hfc.types.XsdLong;

import java.util.*;
import java.util.stream.Collectors;

/**
 * TODO add usage description
 *
 * @author (C) Christian Willms
 * @since JDK 1.8
 * @version Tue Feb 13 10:41:55 CET 2018
 */
public  class GetLastDaysEvents extends AggregationalOperator {

  //////////////////////////////////////////////////
  // Some useful constants
  //////////////////////////////////////////////////
  /**
   * One day (24 hours) represented as milliseconds
   */
  static final long DAY = 86400000;
  //TODO compute the last sunday using currentime % Week
  static final long WEEK = 604800000;
  static final long MONTH = 0;

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
    int n = ((XsdInt)(getObject(table[0][rowLength - 1]))).value;
    long start = computeMidnight(currentTime);
    long end = start - (n * DAY);
    for (int[] entry : table){
      long currentValue = ((XsdLong)(getObject(entry[sortColumnNo]))).value;
      if ( Long.compare(currentValue,start) <= 0 && Long.compare(currentValue, end) >= 0)
        resultTable.add(entry);
    }
  }


  protected long computeMidnight(long currentTime){
    return currentTime - (currentTime%DAY);
  }

  protected long computeLastSunday(long currentTime){
    return (currentTime - (currentTime%WEEK)) - (3*DAY);
  }
}
