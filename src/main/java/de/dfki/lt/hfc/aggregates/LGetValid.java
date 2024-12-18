package de.dfki.lt.hfc.aggregates;

import java.util.Map;
import java.util.SortedMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.dfki.lt.hfc.AggregationalOperator;
import de.dfki.lt.hfc.BindingTable;
import de.dfki.lt.hfc.Query;
import de.dfki.lt.hfc.TIntArrayHashingStrategy;
import de.dfki.lt.hfc.TupleStore;
import de.dfki.lt.hfc.io.QueryParseException;
import gnu.trove.set.hash.TCustomHashSet;

/**
 * Create a projection of timestamped data to standard triple RDF by collecting
 * all triples that are valid at a specific time.
 *
 * Currently only implemented for transaction time data
 */
public class LGetValid extends AggregationalOperator {

  private static final Logger logger = LoggerFactory.getLogger(LGetValid.class);

  /** This only works for special bindingtables, that contain only subject and
   * predicate, in this order. In fact, the only sensible query is the following:
   *
   * select distinct ?s ?p where ?s ?p ?_500 ?t
   *    filter LGreater ?t "0"^^<xsd:long> & LLessEqual ?t "<attime>"^^<xsd:long>
   *    aggregate ?ss ?pp ?oo = LGetValid ?s ?p
   *
   * Returns all tuples that are valid at a specific point in time. If the second
   * filter clause is omitted, it returns all valid triples at the current time.
   * By modifying the time for LGreater, an interval can be specified.
   *
   * @param ts
   * @param args
   * @param nameToPos
   * @param nameToExternalName
   * @param timepoint (currently ignored)
   */
  BindingTable computeSliceTransactionTime(TupleStore ts, BindingTable args,
      SortedMap<Integer, Integer> nameToPos,
      Map<Integer, String> nameToExternalName, long timepoint) {
    tupleStore = ts;
    final TCustomHashSet<int[]> resultTable =
        new TCustomHashSet<int[]>(new TIntArrayHashingStrategy());
    BindingTable bt = new BindingTable(resultTable, nameToPos,
        nameToExternalName, this.tupleStore);
      for (int[] row : args.table) {
        String s = ts.toString(row[0]);
        String p = ts.toString(row[1]);
        String toTime = row.length > 2
            ? " filter LLess ?t " + ts.toString(row[2]) : "";
        try {
          Query q = new Query(ts);
          String query = "select distinct ?s ?p ?o ?t where ?s ?p ?o ?t"
              + " & " + s + " " + p + " ?o ?t" + toTime
              + " aggregate ?ss ?pp ?oo = LGetLatest2 ?s ?p ?o ?t \"1\"^^<xsd:int>";
          BindingTable sp = q.query(query);
          resultTable.addAll(sp.table);
        } catch (QueryParseException e) {
          logger.error("Error computing LGetValid with {} {}: {}",
              s, p, e.getMessage());
        }
      }
    return bt;
  }

  @Override
  public BindingTable apply(BindingTable args,
      SortedMap<Integer, Integer> nameToPos,
      Map<Integer, String> nameToExternalName) {
    return computeSliceTransactionTime(tupleStore, args, nameToPos,
        nameToExternalName, -1);
  }

}
