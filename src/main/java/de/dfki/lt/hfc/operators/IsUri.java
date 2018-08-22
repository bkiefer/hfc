package de.dfki.lt.hfc.operators;

import de.dfki.lt.hfc.FunctionalOperator;
import de.dfki.lt.hfc.types.Uri;

/**
 * checks whether the first argument given to IsUri is a URI
 *
 * @author (C) Hans-Ulrich Krieger
 * @version Thu Dec  9 14:01:09 CET 2010
 * @see de.dfki.lt.hfc.types.Uri
 * @since JDK 1.5
 */
public final class IsUri extends FunctionalOperator {

  /**
   *
   */
  public int apply(int[] args) {
    return (getObject(args[0]) instanceof Uri) ? FunctionalOperator.TRUE : FunctionalOperator.FALSE;
  }

}
