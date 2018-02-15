package de.dfki.lt.hfc.aggregates;

import de.dfki.lt.hfc.types.XsdInt;
import de.dfki.lt.hfc.types.XsdLong;

import java.util.HashSet;

/**
 * TODO add usage description
 *
 * @author (C) Christian Willms
 * @since JDK 1.8
 * @version Tue Feb 13 10:46:38 CET 2018
 */
public final class GetLastTwoWeeksEvents extends GetLastDaysEvents {

  

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
