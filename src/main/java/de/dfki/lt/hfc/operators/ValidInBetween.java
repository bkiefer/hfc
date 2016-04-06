package de.dfki.lt.hfc.operators;

import de.dfki.lt.hfc.Calc;
import de.dfki.lt.hfc.FunctionalOperator;
import de.dfki.lt.hfc.types.XsdLong;

import java.util.Set;

/**
 * call this test with
 *   ValidInBetween subj pred obj obj2 ... time1 time2
 *
 * @return FunctionalOperator.TRUE iff there is _no_ tuple
 *           <logic:false> subj pred obj obj2 ... time
 *         such that min(time1, time2) <= time <= max(time1, time2)
 * @return FunctionalOperator.FALSE otherwise
 *
 * @author (C) Hans-Ulrich Krieger
 * @since JDK 1.5
 * @version Thu Mar 17 15:25:31 CET 2016
 */
public final class ValidInBetween extends FunctionalOperator {

  /**
   * we make the assumption that the value for polarity FALSE is given
   * by some URI from a _logic_ ontology;
   * we use instance <http://www.dfki.de/lt/onto/common/logic.owl#false>
   * for this at the moment and assume that short namespace name "logic"
   * refers to /http://www.dfki.de/lt/onto/common/logic.owl#
   */
  final int falseId = getId("<logic:false>");

  /**
   * note that his functional operator assumes that the temporal
   * parameters are given by the last two arguments (see above)
   * _and_ that they encode xsd:long values !!
   */
  public int apply(int[] args) {
    final int length = args.length;
    // ask for tuples with polarity false
    Set<int[]> result = ask(0, this.falseId);
    Set<int[]> query;
    // perform successive intersections with result
    for (int i = 0; i < (args.length - 2); i++) {
      query = ask(i + 1, args[i]);
      result = Calc.intersection(result, query);
      if (result.isEmpty())
        // _no_ tuples with prefix "<logic:false> subj pred obj obj2 ..." found
        return FunctionalOperator.TRUE;
    }
    // at least tuples with prefix "<logic:false> subj pred obj obj2" exist;
    // so now check whether their time stamps are between time1 and time2
    long time;
    final long time1 = ((XsdLong)(getObject(args[length - 2]))).value;
    final long time2 = ((XsdLong)(getObject(args[length - 1]))).value;
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
