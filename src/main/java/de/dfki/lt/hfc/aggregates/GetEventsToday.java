package de.dfki.lt.hfc.aggregates;

import de.dfki.lt.hfc.types.XsdLong;

import java.util.*;

/**
 * The aggregational operator GetEventsToday returns a table of all events
 * that occurred during the current day starting at current time and going back to 00:00.
 * It is supposed to be given a table of several columns of the following form
 *   arg1 ... argN time
 * where the tuples <arg1, ..., argN> with timestamps during the duration specified above are returned.
 *
 * Note that time is required to be filled with XSD long integers
 *
 * example query:
 *
 *   // return all labvalues for children collected today
 *   SELECT ?child ?prop ?val ?t
 *   WHERE ?child <rdf:type> <dom:Child> ?t1
 *       & ?child <dom:hasLabValue> ?lv ?t2
 *       & ?lv ?prop ?val ?t
 *   AGGREGATE ?measurement ?result ?patient ?time = GetEventsToday ?prop ?val ?child ?t ?t
 *
 * @author (C) Christian Willms
 * @since JDK 1.8
 * @version Tue Feb 13 10:58:29 CET 2018
 */
public final class GetEventsToday extends GetEventsLastNDays {



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
