package de.dfki.lt.hfc.aggregates;

import de.dfki.lt.hfc.types.XsdLong;

import java.util.HashSet;

/**
 * The aggregational operator GetEventsLastTwoWeeks returns a table of all events
 * that occurred during the last two weeks starting from last monday 00:00.
 * It is supposed to be given a table of several columns of the following form
 *   arg1 ... argN time
 * where the tuples <arg1, ..., argN> with timestamps during the last two weeks are returned.
 *
 * Note that time is required to be filled with XSD long integers
 *
 * example query:
 *
 *   // return all labvalues for children collected in the last 2 weeks
 *   SELECT ?child ?prop ?val ?t
 *   WHERE ?child <rdf:type> <dom:Child> ?t1
 *       & ?child <dom:hasLabValue> ?lv ?t2
 *       & ?lv ?prop ?val ?t
 *   AGGREGATE ?measurement ?result ?patient ?time = GetEventsLastTwoWeeks ?prop ?val ?child ?t ?t
 *
 * @author (C) Christian Willms
 * @since JDK 1.8
 * @version Tue Feb 13 10:46:38 CET 2018
 */
public final class GetEventsLastTwoWeeks extends GetEventsLastNDays {

  

  @Override
  protected void internalApply(long currentTime, HashSet<int[]> resultTable, int[][] table, int rowLength) {
    final int sortColumnNo = rowLength - 1;
    // supply the sort method with its own comparator
    // take the "latest" rows, given by the last argument ?limit (an XSD int)
    // check whether there are at least |limit|-many elements
    long start = computeLastSunday(currentTime);
    long end = start - (2 *WEEK);
    for (int[] entry : table){
      long currentValue = ((XsdLong)(getObject(entry[sortColumnNo]))).value;
      if ( Long.compare(currentValue,start) <= 0 && Long.compare(currentValue, end) >= 0)
        resultTable.add(entry);
    }
  }

}
