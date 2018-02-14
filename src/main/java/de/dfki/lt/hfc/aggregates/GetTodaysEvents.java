package de.dfki.lt.hfc.aggregates;

import de.dfki.lt.hfc.types.XsdInt;
import de.dfki.lt.hfc.types.XsdLong;

import java.util.*;

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
 *   AGGREGATE ?dialact ?time = LGetLatest ?da ?t ?t
 *
 *
 * @author (C) Hans-Ulrich Krieger
 * @since JDK 1.5
 * @version Thu Jan  7 17:41:55 CET 2016
 */
public final class GetTodaysEvents extends GetLastDaysEvents {



  @Override
  protected void internalApply(long currentTime, HashSet<int[]> resultTable, int[][] table, int rowLength) {
    final int sortColumnNo = rowLength - 1;
    long end = computeMidnight(currentTime);
    for (int[] entry : table){
      long currentValue = ((XsdLong)(getObject(entry[sortColumnNo]))).value;
      if ( Long.compare(currentValue,currentTime) <= 0 && Long.compare(currentValue, end) >= 0)
        resultTable.add(entry);
    }
  }

}
