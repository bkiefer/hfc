package de.dfki.lt.hfc.operators;

import de.dfki.lt.hfc.BooleanOperator;
import de.dfki.lt.hfc.types.XsdLong;

/**
 * checks whether the first argument is greater than the second argument;
 * arguments are assumed to be of type xsd:long;
 *
 * @author (C) Hans-Ulrich Krieger
 * @version Tue Sep 29 11:11:19 CEST 2009
 * @return FunctionalOperator.TRUE or FunctionalOperator.FALSE
 * @see FunctionalOperator
 * @since JDK 1.5
 */
public final class LGreaterEqual extends BooleanOperator {

  /**
   * note that apply() does NOT check at the moment whether the int args
   * represent in fact XSD longs;
   * note that apply() does NOT check whether it is given exactly two arguments
   */
  protected boolean holds(int[] args) {
    return ((XsdLong) getObject(args[0])).value >=
            ((XsdLong) getObject(args[1])).value;
  }

}
