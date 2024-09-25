package de.dfki.lt.hfc.operators;

import de.dfki.lt.hfc.BindingTable;
import de.dfki.lt.hfc.RelationalOperator;

/**
 * prints the size of the arguments (binding tables) given to apply() to standard out
 *
 * @author (C) Hans-Ulrich Krieger
 * @version Wed Mar 13 18:12:56 CET 2013
 * @return args, the input arguments given to apply() without modifying them
 * @see RelationalOperator
 * @since JDK 1.5
 */
public final class PrintSize extends RelationalOperator {

  /**
   *
   */
  public BindingTable[] apply(BindingTable[] args) {
    for (int i = 0; i < args.length; i++)
      System.out.print(args[i].table.size() + " ");
    System.out.println();
    return args;
  }

}
