package de.dfki.lt.hfc.operators;

import de.dfki.lt.hfc.Calc;
import de.dfki.lt.hfc.FunctionalOperator;
import de.dfki.lt.hfc.types.XsdLong;

import java.util.Set;

/**
 * call this test operator with
 *   LValidInBetween subj pred obj obj2 ... objN time1 time2
 *
 * @return FunctionalOperator.TRUE iff there is *no* tuple
 *           <logic:false> subj pred obj obj2 ... objN time
 *         such that min(time1, time2) <= time <= max(time1, time2)
 * @return FunctionalOperator.FALSE otherwise
 *
 * NOTE: this test is usually employed in temporal entailment rules
 *       for Transaction time, such as
 *         <logic:true> ?p <rdf:type> <owl:TransitiveProperty> "0"^^<xsd:long>
 *         <logic:true> ?x ?p ?y ?time1
 *         <logic:true> ?y ?p ?z ?time2
 *         ->
 *         <logic:true> ?x ?p ?z ?time
 *         @test
 *         ?x != ?y
 *         ?y != ?z
 *         LValidInBetween ?x ?p ?y ?time1 ?time2
 *         LValidInBetween ?y ?p ?z ?time1 ?time2
 *         @action
 *         ?time = LMax2 ?time1 ?time2
 *
 * @author (C) Hans-Ulrich Krieger
 * @since JDK 1.5
 * @version Thu Aug 25 15:31:48 CEST 2016
 */
public final class LValidInBetween extends FunctionalOperator {

  /**
   * note that his functional operator assumes that the temporal
   * parameters are given by the last two arguments (see above)
   * _and_ that they encode xsd:long values !!
   */
  public int apply(int[] args) {
    final int length = args.length;
    // we make the assumption that the value for polarity FALSE is given
    // by some URI from a _logic_ ontology;
    // we use instance <http://www.dfki.de/lt/onto/common/logic.owl#false>
    // for this at the moment and assume that short namespace name "logic"
    // refers to /http://www.dfki.de/lt/onto/common/logic.owl#
    final int falseId = getId("<logic:false>");
    // ask for tuples with polarity false
    Set<int[]> result = ask(0, falseId);
    // look for early success
    if (result.isEmpty())
      // _no_ tuples with prefix "<logic:false>  ..." found
      return FunctionalOperator.TRUE;
    // tuples headed by <logic:false> will undergo successive intersections
    Set<int[]> query;
    // perform successive intersections with result (do not consider temporal args)
    for (int i = 0; i < (args.length - 2); i++) {
      query = ask(i + 1, args[i]);
      result = Calc.intersection(result, query);
      if (result.isEmpty())
        // _no_ tuples with prefix "<logic:false> subj pred obj obj2 ..." found
        return FunctionalOperator.TRUE;
    }
    // at least tuples with prefix "<logic:false> subj pred obj obj2" exist;
    // so now check whether their time stamps are between time1 and time2
    final long time1 = ((XsdLong)(getObject(args[length - 2]))).value;
    final long time2 = ((XsdLong)(getObject(args[length - 1]))).value;
    long time;
    for (int[] tuple: result) {
      // time is the last argument of a tuple
      time = ((XsdLong)(getObject(tuple[length - 1]))).value;
      if ((Long.min(time1, time2) <= time) && (time <= Long.max(time1, time2)))
        return FunctionalOperator.FALSE;
    }
    // no invalidated tuple matches input specification
    return FunctionalOperator.TRUE;
  }

}
