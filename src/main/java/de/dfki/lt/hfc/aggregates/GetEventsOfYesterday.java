package de.dfki.lt.hfc.aggregates;

import de.dfki.lt.hfc.types.XsdInt;
import de.dfki.lt.hfc.types.XsdLong;

import java.util.HashSet;

/**
 * TODO add usage description
 *
 * @author (C) Christian Willms
 * @since JDK 1.8
 * @version Tue Feb 13 10:45:55 CET 2018
 */
public final class GetEventsOfYesterday extends GetLastDaysEvents {

  

  @Override
  protected void internalApply(long currentTime, HashSet<int[]> resultTable, int[][] table, int rowLength) {
    final int sortColumnNo = rowLength - 1;

    long start = computeMidnight(currentTime);
    long end = start - DAY;
    for (int[] entry : table){
      long currentValue = ((XsdLong)(getObject(entry[sortColumnNo]))).value;
      if ( Long.compare(currentValue,start) <= 0 && Long.compare(currentValue, end) >= 0)
        resultTable.add(entry);
    }
  }

}
