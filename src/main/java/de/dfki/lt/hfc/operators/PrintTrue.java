package de.dfki.lt.hfc.operators;

import de.dfki.lt.hfc.FunctionalOperator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * prints the arguments given to apply() to standard out
 *
 * @author (C) Hans-Ulrich Krieger
 * @version Tue Sep 29 11:11:19 CEST 2009
 * @see FunctionalOperator
 * @since JDK 1.5
 */
public final class PrintTrue extends FunctionalOperator {

  /**
   * A basic LOGGER.
   */
  private static final Logger logger = LoggerFactory.getLogger(PrintTrue.class);

  /**
   * @return FunctionalOperator.TRUE
   */
  public int apply(int[] args) {
    for (int i = 0; i < args.length; i++)
      logger.info(getExternalRepresentation(args[i]) + " ");
    logger.info("\n");
    return FunctionalOperator.TRUE;
  }

}
