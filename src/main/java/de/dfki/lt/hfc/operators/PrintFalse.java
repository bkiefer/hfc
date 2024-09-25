package de.dfki.lt.hfc.operators;

import de.dfki.lt.hfc.FunctionalOperator;

/**
 * prints the arguments given to apply() to standard out
 *
 * @author (C) Hans-Ulrich Krieger
 * @version Tue Sep 29 11:11:19 CEST 2009
 * @return FunctionalOperator.FALSE
 * @see FunctionalOperator
 * @since JDK 1.5
 */
public final class PrintFalse extends FunctionalOperator {

  /**
   *
   */
  public int apply(int[] args) {
    for (int i = 0; i < args.length; i++)
      System.out.print(getExternalRepresentation(args[i]) + " ");
    System.out.println();
    return FunctionalOperator.FALSE;
  }

}
