package de.dfki.lt.hfc.operators;

import de.dfki.lt.hfc.Calc;
import de.dfki.lt.hfc.FunctionalOperator;

import java.util.Set;

/**
 * NoValue checks whether a specific tuple or a partial description of a
 * tuple (potentially describing several tuples) exist in the tuple store;
 * variables have already been replaced by their bindings, thus we are dealing
 * with positive ints;
 * in order to address a partial description, we do allow a special don't care
 * value "*" that is internally represented by FunctionalOperator.UNBOUND;
 * note that the length of args as given to apply() indicates that we are searching
 * for tuples of EXACTLY this length, thus
 * (1) NoValue ?s ?p ?o
 * (2) NoValue ?s <foo:hasValue> "Hi"^^<xsd:string>
 * (3) NoValue * * *
 * (4) NoValue ?s ?p *
 * (5) NoValue ?s * "42"
 * (6) NoValue ?s * *
 * each pattern always looks for _triples_; even pattern (3) makes sense, since
 * it checks for the (non)existence of triples in the tuple store -- remember,
 * we are not forced to deal with tuples of length three only
 *
 * @author (C) Hans-Ulrich Krieger
 * @version Wed Jun 23 16:45:34 CEST 2010
 * @since JDK 1.5
 */
public final class NoValue extends FunctionalOperator {

  /**
   * the above five examples exhibit three cases (variables are _always_ assigned a value):
   * case 1 (example (1) + (2)): no '*' in pattern
   * case 2 (example(3)):        only '*' in pattern
   * case 3 (example (4) - (6)): at least one true value
   */
  public int apply(int[] args) {
    int noOfStars = 0;
    for (int i = 0; i < args.length; i++)
      if (args[i] == FunctionalOperator.UNBOUND)
        ++noOfStars;
    // three cases
    if (noOfStars == 0) {
      // no stars: check for (non)existence of ground tuple
      return ask(args) ? FunctionalOperator.FALSE : FunctionalOperator.TRUE;
    } else if (noOfStars == args.length) {
      // only stars: check for at least ONE tuple of length noOfArgs
      for (int[] tuple : ask()) {
        if (tuple.length == noOfStars)
          return FunctionalOperator.FALSE;
      }
      return FunctionalOperator.TRUE;
    } else {
      // at least one true value: perform successive intersections
      Set<int[]> result = null;
      Set<int[]> query;
      for (int i = 0; i < args.length; i++) {
        if (args[i] != FunctionalOperator.UNBOUND) {
          query = ask(i, args[i]);
          if (result == null)
            result = query;
          else
            result = Calc.intersection(result, query);
          if (result.isEmpty())
            return FunctionalOperator.TRUE;
        }
      }
      return FunctionalOperator.FALSE;
    }
  }

}
