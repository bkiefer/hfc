package de.dfki.lt.hfc.aggregates;

import de.dfki.lt.hfc.types.XsdLong;

import java.util.HashSet;

/**
 * The aggregational operator GetEventsThisWeek returns a table of all events
 * that occurred during the this week starting at current time and going back to Monday 00:00.
 * It is supposed to be given a table of several columns of the following form
 * arg1 ... argN time
 * where the tuples <arg1, ..., argN> with timestamps during the current week are returned.
 * <p>
 * Note that time is required to be filled with XSD long integers
 * <p>
 * example query:
 * <p>
 * // return all labvalues for children collected in the this week
 * SELECT ?child ?prop ?val ?t
 * WHERE ?child <rdf:type> <dom:Child> ?t1
 * & ?child <dom:hasLabValue> ?lv ?t2
 * & ?lv ?prop ?val ?t
 * AGGREGATE ?measurement ?result ?patient ?time = GetEventsThisWeek ?prop ?val ?child ?t ?t
 *
 * @author (C) Christian Willms
 * @version Tue Feb 13 10:52:05 CET 2018
 * @since JDK 1.8
 */
public final class GetEventsThisWeek extends GetEventsLastNDays {


  @Override
  protected void internalApply(long currentTime, HashSet<int[]> resultTable, int[][] table, int rowLength) {
    final int sortColumnNo = rowLength - 1;
    // supply the sort method with its own comparator
    // take the "latest" rows, given by the last argument ?limit (an XSD int)
    // check whether there are at least |limit|-many elements
    long start = currentTime;
    long end = computeLastSunday(currentTime);
    for (int[] entry : table) {
      long currentValue = ((XsdLong) (getObject(entry[sortColumnNo]))).value;
      if (Long.compare(currentValue, start) <= 0 && Long.compare(currentValue, end) >= 0)
        resultTable.add(entry);
    }
  }

}
