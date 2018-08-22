package de.dfki.lt.hfc.operators;

import de.dfki.lt.hfc.BindingTable;
import de.dfki.lt.hfc.RelationalOperator;

/**
 * prints the content of the binding tables given to apply() to standard out
 *
 * @author (C) Hans-Ulrich Krieger
 * @version Fri Mar 15 16:20:24 CET 2013
 * @see RelationalOperator
 * @since JDK 1.5
 */
public final class PrintContent extends RelationalOperator {

  /**
   * @return args, the input arguments given to apply() without modifying them
   */
  public BindingTable[] apply(BindingTable[] args) {
    for (BindingTable arg : args) {
      System.out.println(arg.toString());
      System.out.flush();
    }
    return args;
  }

}
