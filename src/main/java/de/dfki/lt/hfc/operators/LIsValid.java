package de.dfki.lt.hfc.operators;

import de.dfki.lt.hfc.Calc;
import de.dfki.lt.hfc.FunctionalOperator;
import de.dfki.lt.hfc.types.XsdLong;

import java.util.Set;

/**
 * call this test with
 *   LIsValid subj pred obj obj2 ... objN time
 *
 * @return FunctionalOperator.TRUE iff there is *no* tuple
 *           <logic:false> subj pred obj obj2 ... objN time'
 *         such that time' > time
 * @return FunctionalOperator.FALSE otherwise
 *
 * NOTE: this filter operator is usually employed in queries such as
 *         SELECT ?o
 *         WHERE <logic:true> subj pred obj ?ts
 *         FILTER LIsValid subj pred obj ?ts
 *       this requires, however, that _inside_ the test, the repository
 *       is queried for each individual incarnation of the WHERE pattern!
 *
 *       alternatively, the same effect can be achieved through an aggregate
 *       LIsValid which is given the whole "set of possibilities" by now
 *       employing a polarity _variable_ in front of the WHERE pattern:
 *         SELECT ?o
 *         WHERE ?pol subj pred obj ?ts
 *         AGGREGATE LIsValid subj pred obj ?ts
 *       this aggregate might be given a huge binding table, but the
 *       advantage is that the repository needs no longer be queried
 *
 * given the following tuples
 *   true  prof treats p1 t1
 *   true  prof treats p2 t2
 *   false prof treats p1 t3
 *   true  prof treats p3 t4
 *   true  prof treats p1 t5  // p1 treated again by prof
 *   true  prof treats p4 t6
 *   false prof treats p4 t7
 * given
 *   t7 > t6 > t5 > t4 > t3 > t2 > t1
 * and query
 *   SELECT ?o
 *   WHERE <logic:true> subj pred obj ?ts
 *   FILTER LIsValid subj pred obj ?ts
 * we obtain the following values:
 *   ?o = {p2, p3, p1}
 *
 * @author (C) Hans-Ulrich Krieger
 * @since JDK 1.5
 * @version Fri Aug 26 11:50:07 CEST 2016
 */
public final class LIsValid extends FunctionalOperator {

  /**
   * note that his functional operator assumes that the temporal
   * parameter is given by the last argument (see above) _and_ that
   * they encode xsd:long values !!
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
    // perform successive intersections with result (do not consider temporal arg)
    for (int i = 0; i < (args.length - 2); i++) {
      query = ask(i + 1, args[i]);
      result = Calc.intersection(result, query);
      if (result.isEmpty())
        // _no_ tuples with prefix "<logic:false> subj pred obj obj2 ..." found
        return FunctionalOperator.TRUE;
    }
    // at least tuples with prefix "<logic:false> subj pred obj obj2" exist;
    // so now check whether their time stamps are greater than the value bound to ?ts
    final long time = ((XsdLong)(getObject(args[length - 1]))).value;
    long timeprime;
    for (int[] tuple: result) {
      // time is the last argument of a tuple
      timeprime = ((XsdLong)(getObject(tuple[length - 1]))).value;
      if (time < timeprime)
        return FunctionalOperator.FALSE;
    }
    // no invalidated tuple matches input specification
    return FunctionalOperator.TRUE;
  }

}
