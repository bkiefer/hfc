package de.dfki.lt.hfc.operators;

import de.dfki.lt.hfc.BooleanOperator;
import de.dfki.lt.hfc.types.XsdInt;

/**
 * checks whether the first argument is equal to the second argument;
 * arguments are assumed to be numbers of type xsd:int
 *
 * @author (C) Hans-Ulrich Krieger
 * @version Wed Jun 23 11:04:27 CEST 2010
 * @return BooleanOperator.TRUE or BooleanOperator.FALSE
 * @see BooleanOperator
 * @since JDK 1.5
 */
public final class IEqual extends BooleanOperator {

  /**
   * note that apply() does NOT check at the moment whether the int args
   * represent in fact XSD ints;
   * note that apply() does NOT check whether it is given exactly two arguments
   */
  protected boolean holds(int[] args) {
    return ((XsdInt) getObject(args[0])).value
            == ((XsdInt) getObject(args[1])).value;
  }

}
