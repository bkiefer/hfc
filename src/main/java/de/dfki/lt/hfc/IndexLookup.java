package de.dfki.lt.hfc;

import de.dfki.lt.hfc.indices.Index;
import de.dfki.lt.hfc.qrelations.QRelation;
import de.dfki.lt.hfc.types.XsdAnySimpleType;
import gnu.trove.set.hash.TCustomHashSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.log4j.Level;
import org.apache.log4j.LogMF;
import org.apache.log4j.Logger;

/**
 * The {@link IndexLookup} is created while parsing queries in case the {@link IndexStore}
 * ontologyContainsTuple appropriate indices (correct position and type). Eventually all created
 * lookups are then applied within the queryAndJoin Method of {@link Query}
 */
public class IndexLookup {

  /**
   * A basic LOGGER.
   */
  private final static Logger LOGGER = Logger.getLogger(IndexLookup.class.getName());

  static {
    LOGGER.setLevel(Level.ERROR);
  }

  private TupleStore tupleStore;
  final Index index;
  final int[] clause;
  final int indexedPosition;
  final int indexedPositionEnd;
  final QRelation relation;
  Table table;

  /**
   * Creates a new instance of {@link IndexLookup} dedicated to atomic indices, e.g. BTrees
   *
   * @param index The {@link Index} associated with the lookup instance
   * @param clause the clause to be searched for
   * @param position the position in the clause where the indexed term is
   * @param r possibly an QRelation such as Allens Overlaps relation, but might also be null
   */
  public IndexLookup(TupleStore tupleStore, Index index, List<Integer> clause, int position,
      QRelation r) {
    this.tupleStore = tupleStore;
    this.index = index;
    this.clause = Utils.toPrimitive(clause);
    this.indexedPosition = this.indexedPositionEnd = position;
    this.relation = r;
  }

  /**
   * Creates a new instance of {@link IndexLookup} dedicated to interval indices, e.g. BTrees
   *
   * @param index The {@link Index} associated with the lookup instance
   * @param clause the clause to be searched for
   * @param position_start the position where the indexed interval starts
   * @param position_end the position in the clause where the indexed interval ends
   * @param r possibly an QRelation such as Allens Overlaps relation, but might also be null
   */
  public IndexLookup(TupleStore tupleStore, Index index, List<Integer> clause, int position_start,
      int position_end,
      QRelation r) {
    this.tupleStore = tupleStore;
    this.index = index;
    this.clause = Utils.toPrimitive(clause);
    this.indexedPosition = position_start;
    this.indexedPositionEnd = position_end;
    this.relation = r;
  }

  /**
   * This method performs the lookup and uses its results to populate the given {@link
   * BindingTable}
   *
   * @param bindingTable the {@link BindingTable} to be populated.
   */
  void apply(BindingTable bindingTable) {
    LogMF.debug(LOGGER, "Apply Lookup with relation {0} on index {1}", relation, this.index);
    Set<int[]> result = new HashSet<>();
    // The actual index lookup, depending on whether or not relations are used
    if (relation != null) {
      result = relation.apply(this.index);
    } else {
      XsdAnySimpleType t1, t2;
      t1 = (XsdAnySimpleType) tupleStore.getJavaObject(clause[this.indexedPosition]);
      if (indexedPosition == indexedPositionEnd) {
        result = this.index.search(t1);
      } else {
        t2 = (XsdAnySimpleType) tupleStore.getJavaObject(clause[this.indexedPositionEnd]);
        result = this.index.searchInterval(t1, t2);
      }
    }
    // check whether there were appropriate tuples. This speeds up the process.
    if (result.isEmpty()) {
      bindingTable.table = result;
      bindingTable.nameToPos = table.nameToPosProper;
      return;
    }
       int id;
//    // Lookup the Tuplestore.index to filter not matching tuples.
//    for (int i = 0; i < clause.length; i++) {
//      id = clause[i];
//      if (id >= 0) {
//        result = Calc.intersection(result, tupleStore.index[i].get(id));
//        if (result.isEmpty()) {
//          bindingTable.table = result;
//          bindingTable.nameToPos = table.nameToPosProper;
//          return;
//        }
//      }
//    }
    // alternative implementation
    Set<int[]> temp;
    for (int i = 0; i<clause.length;i++) {
      temp = new HashSet<>();
      id = clause[i];
      if (id >= 0){
        for (int[] tuple : result){
          if(tuple[i] == id)
            temp.add(tuple);
        }
        if (temp.isEmpty()){
          bindingTable.table = result;
          bindingTable.nameToPos = table.nameToPosProper;
          return;
        } else {
          result = temp;
        }
      }
    }

    // use relevant position (and NOT proper), since in-eqs might be applied
    TCustomHashSet query = new TCustomHashSet<int[]>(
        new TIntArrayHashingStrategy(table.getRelevantPositions()));
    for (int[] tuple : result) {
      if (tuple.length == clause.length) {
        query.add(tuple);
      }
    }
    // note: result now no longer refers to sets from the index, thus manipulating
    // result destructively has NO effect on the index or the set of all tuples;
    // querying the index does NOT guarantee that duplicate variables hold the
    // same value after local querying: <?x, a, ?x>;
    // this needs to be corrected here by throwing away tuples from the result
    // set, using getEqualPositions();
    if (table.getEqualPositions().length != 0) {
      result = tupleStore.ensureEqualVariables(query, table.getEqualPositions());
    }
    bindingTable.table = result;
    bindingTable.nameToPos = table.nameToPosProper;

  }
}
