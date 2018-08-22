package de.dfki.lt.hfc.operators;

import de.dfki.lt.hfc.FunctionalOperator;

/**
 * computes the maximum of the two (2) arguments given to apply();
 * we do NOT check whether there are less than or more than two arguments;
 * arguments are assumed to be of type <xsd:dateTime>;
 * returns the maximal dateTime instant
 *
 * @author (C) Hans-Ulrich Krieger
 * @version Wed Jun 22 17:15:17 CEST 2011
 * @see FunctionalOperator
 * @since JDK 1.5
 */
public final class DTMax2 extends FunctionalOperator {

  /**
   * note that apply() does NOT check at the moment whether the int args
   * represent in fact XSD dateTime instants;
   * note: no need to register the result, since the maximum is one of the two arguments
   */
  public int apply(int[] args) {
    // note: we call DTLess through the use of callFuntionalOperator() in order to define DTMax2
    final int result = callFunctionalOperator("DTLess", args);
    if (result == FunctionalOperator.TRUE)
      return args[1];
    else
      return args[0];
  }

}
