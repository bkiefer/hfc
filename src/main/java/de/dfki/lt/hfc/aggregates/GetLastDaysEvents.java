package de.dfki.lt.hfc.aggregates;

import de.dfki.lt.hfc.AggregationalOperator;
import de.dfki.lt.hfc.BindingTable;
import de.dfki.lt.hfc.types.XsdInt;
import de.dfki.lt.hfc.types.XsdLong;

import java.util.*;
import java.util.stream.Collectors;

/**
 * //TODO rewrite
 *
 * this aggregational operator LGetLatest
 *   = take the latest time-stamped tuple(s)
 * is supposed to be given a table of several columns of the following form
 *   arg1 ... argN time limit
 * where the k latest (k = limit) tuples <arg1, ..., argN> in an ordered
 * sequence are returned;
 * note that time in this version requires to be filled with XSD long integers
 *
 * example query:
 *
 *   // look for the latest 3 dialogue acts (if possible), labelled with a
 *   // time stamp greater or equal 548
 *   SELECT ?da ?t
 *   WHERE ?da <rdf:type> <dafn:DialogueAct> &
 *         ?da <dafn:happens> ?t
 *   FILTER LGreaterEqual ?t "548"^^<xsd:long>
 *   AGGREGATE ?dialact = LGetLatest ?da ?t "3"^^<xsd:int>
 *
 * returns a binding table of one column and 0 to 3 rows (depending on the
 * data stored in the tuple store), where the columns are headed by the labels
 * "?dialact" and "?time"
 *
 * in case time ?t should also be returned (stored under heading "?time"),
 * we have to duplicate the temporal argument in the AGGREGATE section:
 *
 *   AGGREGATE ?dialact ?time = LGetLatest ?da ?t ?t "3"^^<xsd:int>
 *
 *
 * @author (C) Christian Willms
 * @since JDK 1.8
 * @version Tue Feb  12 10:41:55 CET 2018
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
    return (currentTime - (currentTime%WEEK)) + (4*DAY);
  }
}
