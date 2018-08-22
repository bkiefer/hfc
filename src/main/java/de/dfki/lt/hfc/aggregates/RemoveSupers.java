package de.dfki.lt.hfc.aggregates;

import de.dfki.lt.hfc.AggregationalOperator;
import de.dfki.lt.hfc.BindingTable;
import de.dfki.lt.hfc.Namespace;
import gnu.trove.set.hash.TIntHashSet;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedMap;

/**
 * an example where FilterSupers might be applied:
 * SELECT ?c
 * WHERE <dom:instance> <rdf:type> ?c
 * AGGREGATE ?baseclass = FilterSupers ?c
 *
 * @author Bernd Kiefer
 */
public final class RemoveSupers extends AggregationalOperator {

  /**
   * the resulting binding table (1 row, 1 column) is equipped with the corresponding
   * tuple store object (as available from AggregationalOperator through this.tupleStore)
   * and the two parameter mapping nameToPos and nameToExternalName for potential later
   * output;
   * note that nameToPos and nameToExternalName of args are not used -- in fact, these
   * fields are assigned the null value
   */
  public BindingTable apply(BindingTable args,
                            SortedMap<Integer, Integer> nameToPos,
                            Map<Integer, String> nameToExternalName) {
    // the resulting table
    final BindingTable bt = new BindingTable(nameToPos, nameToExternalName, this.tupleStore);
    // put all classes into a set
    Iterator<int[]> it = args.iterator();
    TIntHashSet classes = new TIntHashSet();
    int len = 0;
    while (it.hasNext()) {
      int[] r = it.next();
      len = len < r.length ? r.length : len;
      classes.add(r[0]);
    }
    int[] tuple = new int[3 + len - 1];
    Arrays.fill(tuple, 0);
    tuple[1] = Namespace.RDFS_SUBCLASSOF_ID;
    boolean change = true;
    while (change) {
      change = false;
      int[] all = classes.toArray();
      for (int i = 0; i < all.length; ++i) {
        for (int j = 0; j < all.length; ++j) {
          if (i == j) continue;
          tuple[0] = all[i];
          tuple[2] = all[j];
          if (this.tupleStore.ask(tuple)) {
            classes.remove(all[j]);
            change = true;
          }
        }
      }
    }
    classes.forEach((c) -> bt.table.add(new int[]{c}));
    // add this XSD int as the only unary tuple to the resulting table
    return bt;
  }

}
