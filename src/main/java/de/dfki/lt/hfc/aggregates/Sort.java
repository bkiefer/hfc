package de.dfki.lt.hfc.aggregates;

import de.dfki.lt.hfc.AggregationalOperator;
import de.dfki.lt.hfc.BindingTable;
import de.dfki.lt.hfc.types.XsdAnySimpleType;

import java.util.*;


/**
 * The Sort aggregate is used to arrange the entries in the given bindingtable in an ascending order with respect to
 * the given key, i.e. the variable according to which the table should be sorted.
 */
public class Sort extends AggregationalOperator{

    @Override
    public BindingTable apply(BindingTable args, SortedMap<Integer, Integer> nameToPos, Map<Integer, String> nameToExternalName) {
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
        final int rowLength = table[0].length;  // rows are of same length
        final int sortColumnNo = rowLength - 1;
        // supply the sort method with its own comparator
        Arrays.sort(table, new Comparator<int[]>() {
            public int compare(int[] t1, int[] t2) {
                final XsdAnySimpleType i1 = ((XsdAnySimpleType)(getObject(t1[sortColumnNo])));
                final XsdAnySimpleType i2 = ((XsdAnySimpleType)(getObject(t2[sortColumnNo])));
                // we want a descending, _not_ ascending order
                return i1.compareTo(i2);  // an int must be returned, so (l2 - l1) won't work
            }
        });

        // no need to throw away the last two columns
        for (int[] tuple : table) {
            resultTable.add(tuple);
        }
        return bt;
    }
}
