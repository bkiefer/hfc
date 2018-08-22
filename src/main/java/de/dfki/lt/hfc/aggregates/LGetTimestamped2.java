package de.dfki.lt.hfc.aggregates;

import de.dfki.lt.hfc.AggregationalOperator;
import de.dfki.lt.hfc.BindingTable;
import de.dfki.lt.hfc.TupleStore;
import de.dfki.lt.hfc.types.XsdInt;
import de.dfki.lt.hfc.types.XsdLong;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.SortedMap;

/**
 * this aggregational operator LGetLatest2
 * = take the latest time-stamped tuple(s)
 * is supposed to be given a table of several columns of the following form
 * arg1 ... argN time limit
 * where the tuples <arg1, ..., argN> with the k latest TIME STAMPS are
 * returned, e.g, if there are 10 tuples with the newest time stamp and the limit
 * is one, 10 tuples will be returned
 * <p>
 * note that time in this version requires to be filled with XSD long integers
 * <p>
 * example query:
 * <p>
 * // look for the latest 3 dialogue acts (if possible), labelled with a
 * // time stamp greater or equal 548
 * SELECT ?da ?t
 * WHERE ?da <rdf:type> <dafn:DialogueAct> &
 * ?da <dafn:happens> ?t
 * FILTER LGreaterEqual ?t "548"^^<xsd:long>
 * AGGREGATE ?dialact = LGetLatest ?da ?t "3"^^<xsd:int>
 * <p>
 * returns a binding table of one column and 0 to 3 rows (depending on the
 * data stored in the tuple store), where the columns are headed by the labels
 * "?dialact" and "?time"
 * <p>
 * in case time ?t should also be returned (stored under heading "?time"),
 * we have to duplicate the temporal argument in the AGGREGATE section:
 * <p>
 * AGGREGATE ?dialact ?time = LGetLatest ?da ?t ?t "3"^^<xsd:int>
 *
 * @author (C) Hans-Ulrich Krieger
 * @version Thu Jan  7 17:41:55 CET 2016
 * @since JDK 1.5
 */
public abstract class LGetTimestamped2 extends AggregationalOperator {

  /**
   * general form of the aggregate call:  ?arg1' ... ?argN' = LGetLatest ?arg1 ... ?argN ?time ?limit
   */
  BindingTable applyInternal(TupleStore ts,
                             BindingTable args,
                             SortedMap<Integer, Integer> nameToPos,
                             Map<Integer, String> nameToExternalName,
                             final boolean latest) {    // use a linked hash set here to guarantee the "right" iteration ordering
    tupleStore = ts;
    final LinkedHashSet<int[]> resultTable = new LinkedHashSet<int[]>();
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
    final int sortColumnNo = rowLength - 2;
    // supply the sort method with its own comparator
    Arrays.sort(table, (t1, t2) -> {
      final long l1 = ((XsdLong) (getObject(t1[sortColumnNo]))).value;
      final long l2 = ((XsdLong) (getObject(t2[sortColumnNo]))).value;
      // we want a descending, _not_ ascending order
      int res = Long.compare(l2, l1);  // an int must be returned, so (l2 - l1) won't work
      return latest ? res : -res;
    });
    // take the "latest" rows, given by the last argument ?limit (an XSD int)
    // this differs from LGetLatest in that the limit relates to the time stamps,
    // not the number of rows!
    int limit = ((XsdInt) (getObject(table[0][rowLength - 1]))).value;
    // check whether there are at least |limit|-many elements
    if (limit > table.length)
      limit = table.length;
    long lastStamp = ((XsdLong) (getObject(table[0][sortColumnNo]))).value;
    // no need to throw away the last two columns
    for (int i = 0; i < table.length && limit > 0; ++i) {
      int[] row = table[i];
      long currentStamp = ((XsdLong) (getObject(row[sortColumnNo]))).value;
      if (lastStamp != currentStamp) {
        if (--limit == 0) break;
        lastStamp = currentStamp;
      }
      resultTable.add(row);
    }

    return bt;
  }

}
