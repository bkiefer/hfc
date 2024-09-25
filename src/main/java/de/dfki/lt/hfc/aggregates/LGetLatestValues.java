package de.dfki.lt.hfc.aggregates;

import de.dfki.lt.hfc.AggregationalOperator;
import de.dfki.lt.hfc.BindingTable;
import de.dfki.lt.hfc.types.XsdLong;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.SortedMap;

/**
 * the aggregational operator LGetLatestValues only works for the time-stamped
 * triple case and is supposed to be given a table of the following form:
 * property value arg1 ... argN timestamp
 * the operator returns information from the latest time-stamped tuples
 * ?_ property value timestamp
 * note that if property is _not_ functional, there might be several such tuples
 * with the _same_ time stamp, but differing in their value, e.g.,
 * ?_ prop val1 time
 * ?_ prop val2 time
 * in case the associated timestamp should also be returned, duplicate the time
 * argument and make it one of the arguments from arg1, ..., argN
 * <p>
 * quadruple example (we duplicate the bsl value to have a relational property):
 * <pal:lisa> <rdf:type> <dom:Child> "5544"^^<xsd:long> .
 * <pal:lisa> <dom:hasLabValue> <pal:labval22> "5544"^^<xsd:long> .
 * <pal:labval22> <dom:height> "133"^^<xsd:cm> "5544"^^<xsd:long> .
 * <pal:labval22> <dom:weight> "28.2"^^<xsd:kg> "5544"^^<xsd:long> .
 * <pal:labval22> <dom:bmi> "15.9"^^<xsd:kg_m2> "5544"^^<xsd:long> .
 * <pal:labval22> <dom:hr> "75.0"^^<xsd:min-1> "5544"^^<xsd:long> .
 * <pal:labval22> <dom:bsl> "162.0"^^<xsd:mg_dL> "5544"^^<xsd:long> .
 * <pal:labval22> <dom:bsl> "9.0"^^<xsd:mmol_L> "5544"^^<xsd:long> .
 * <pal:lisa> <dom:hasLabValue> <pal:labval33> "5577"^^<xsd:long> .
 * <pal:labval33> <dom:weight> "28.6"^^<xsd:kg> "5577"^^<xsd:long> .
 * <pal:labval33> <dom:bsl> "9.2"^^<xsd:mmol_L> "5577"^^<xsd:long> .
 * <pal:labval33> <dom:bsl> "165.6"^^<xsd:mg_dL> "5577"^^<xsd:long> .
 * <p>
 * example query from the PAL domain:
 * SELECT ?child ?prop ?val ?t
 * WHERE ?child <rdf:type> <dom:Child> ?ts1 &
 * ?child <dom:hasLabValue> ?labvalue ?ts2 &
 * ?labvalue ?prop ?val ?t
 * AGGREGATE ?measurement ?result ?patient ?time = LGetLatestValues ?prop ?val ?child ?t ?t
 * <p>
 * what we would then like to see is
 * =============================================================================================
 * | ?measurement         | ?result              | ?patient             | ?time                |
 * =============================================================================================
 * | <dom:bmi>            | "15.9"^^<xsd:kg_m2>  | <pal:lisa>           | "5544"^^<xsd:long>   |
 * | <dom:bsl>            | "165.6"^^<xsd:mg_dL> | <pal:lisa>           | "5577"^^<xsd:long>   |
 * | <dom:bsl>            | "9.2"^^<xsd:mmol_L>  | <pal:lisa>           | "5577"^^<xsd:long>   |
 * | <dom:height>         | "133"^^<xsd:cm>      | <pal:lisa>           | "5544"^^<xsd:long>   |
 * | <dom:hr>             | "75.0"^^<xsd:min-1>  | <pal:lisa>           | "5544"^^<xsd:long>   |
 * | <dom:weight>         | "28.6"^^<xsd:kg>     | <pal:lisa>           | "5577"^^<xsd:long>   |
 * ---------------------------------------------------------------------------------------------
 *
 * @author (C) Hans-Ulrich Krieger
 * @version Wed Mar 23 16:49:29 CET 2016
 * @since JDK 1.5
 */
public final class LGetLatestValues extends AggregationalOperator {

  /**
   *
   */
  public BindingTable apply(BindingTable args,
                            SortedMap<Integer, Integer> nameToPos,
                            Map<Integer, String> nameToExternalName) {
    // use a linked hash set here to guarantee the "right" iteration ordering
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
    final int proppos = 0;                    // we sort according to the property ...
    final int timepos = table[0].length - 1;  // ... and according to the time argument
    // supply the sort method with its own comparator
    Arrays.sort(table, (row1, row2) -> {
      // properties in HFC are lazily represented as instances of de.dfki.lt.hfc.types.Uri
      final String prop1 = (getObject(row1[proppos])).toName();
      final String prop2 = (getObject(row2[proppos])).toName();
      // we assume time stamps be represented as instances of de.dfki.lt.hfc.types.XsdLong
      final long time1 = ((XsdLong) (getObject(row1[timepos]))).value;
      final long time2 = ((XsdLong) (getObject(row2[timepos]))).value;
      // ascending order for the properties ...
      final int propcomp = prop1.compareTo(prop2);
      if (propcomp == 0) {
        // ... but descending order for the time stamps
        return Long.compare(time2, time1);
      } else
        return propcomp;
    });
    // at least one row as table is not empty (see above)
    resultTable.add(table[0]);
    // now take further latest values for every property if table has more than one row;
    // there might be several values in case property is relational (see example above)
    int newprop = table[0][proppos];
    int newtime = table[0][timepos];
    int oldprop, oldtime;
    boolean isNew = true;
    for (int i = 1; i < table.length; ++i) {
      oldprop = newprop;
      oldtime = newtime;
      newprop = table[i][proppos];
      newtime = table[i][timepos];
      if (newprop != oldprop) {
        // change of property
        resultTable.add(table[i]);
        isNew = true;
      } else if ((newtime == oldtime) && isNew) {
        // same property, same time stamp, but different value
        resultTable.add(table[i]);
      } else
        // lower time stamp: nothing to record
        isNew = false;
    }
    return bt;
  }

}
