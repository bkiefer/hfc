package de.dfki.lt.hfc.aggregates;

import de.dfki.lt.hfc.types.XsdInt;
import de.dfki.lt.hfc.types.XsdLong;

import java.util.*;

/**
 * TODO add usage description
 *
 * @author (C) Christian Willms
 * @since JDK 1.8
 * @version Tue Feb 13 10:58:29 CET 2018
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
