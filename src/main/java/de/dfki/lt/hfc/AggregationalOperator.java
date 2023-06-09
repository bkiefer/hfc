package de.dfki.lt.hfc;

import java.util.Map;
import java.util.SortedMap;

/**
 * AggregationalOperator is assumed to be the abstract superclass of all aggregational
 * operators (aggregates) that can be used during querying;
 * every subclass of AggregationalOperator MUST implement exactly one unary method,
 * called apply(), together with a package specification and (at least) one import
 * statement:
 * <p>
 * // put your AO in _this_ package:
 * package de.dfki.lt.hfc.aggregates;
 * <p>
 * // give access to methods from AggregationalOperator which safely access the tuple store
 * import de.dfki.lt.hfc.AggregationalOperator;
 * <p>
 * // put your code for the AO in here with exactly the same signature
 * public BindingTable apply(BindingTable args,
 * SortedMap<Integer, Integer> nameToPos,
 * Map<Integer, String> nameToExternalName) {
 * ...
 * }
 * <p>
 * the args (an int array) given to apply() are exactly the arguments given to the AO
 * specified in the query;
 * the return value is usually a BindingTable object, encoding the the application of the AO
 * to its arguments;
 * even a single value _must_ be captured in a table, since aggregates are not required
 * to return single values -- they are N1 x ... x Nn to M1 x .. x Mm mappings, i.e.,
 * mapping binding tables to binding tables
 * <p>
 * NOTE: an aggregational operator is required _not_ to implement any constructor;
 * <p>
 * IMPORTANT: an aggregational operator is _required_ to assign the nameToPos,
 * nameToExternalName, and tupleStore fields of the resulting binding
 * table with proper values!!
 * this can be easily achieved by calling the right ternary constructor from
 * class BindingTable;
 * tupleStore is accessable via the superclass Operator, nameToPos and
 * nameToExternalName via instance fields in this class
 *
 * @author (C) Hans-Ulrich Krieger
 * @version Tue Sep 15 13:02:21 CEST 2015
 * @see de.dfki.lt.hfc.Operator
 * the abstract superclass providing useful, already implemented methods, and access
 * to this.tupleStore
 * this is the proper constructor that should be called when generating the resulting
 * binding table
 * @see de.dfki.lt.hfc.aggregates.CountDistinct for an example
 * @since JDK 1.5
 */
public abstract class AggregationalOperator extends Operator {

  /**
   * this method offers the possibility that an aggregational operator can call a functional
   * operator in its own definition;
   * note: an identical operator exist in (abstract) class FunctionalOperator
   */
  public int callFunctionalOperator(String name, int[] args) {
    return this.tupleStore.evaluateOperator(name, OperatorRegistry.OPERATOR_PATH, args);
  }

  /**
   * !!! this is the one and only method that you MUST implement !!!
   * <p>
   * NOTE for a future implementation:
   * in order to make this comparable to relational variables ??(...) in the
   *
   * @test section of HFC's RDL rules, parameter args below should be of type
   * BindingTable[] args
   * thus the other two arguments must also be extended into an array:
   * SortedMap<Integer, Integer>[] nameToPos,
   * Map<Integer, String>[] nameToExternalName
   */
  public abstract BindingTable apply(BindingTable args,
                                     SortedMap<Integer, Integer> nameToPos,
                                     Map<Integer, String> nameToExternalName);

}
