package de.dfki.lt.hfc.operators;

import de.dfki.lt.hfc.BooleanOperator;
import de.dfki.lt.hfc.types.XsdInt;

/**
 * checks whether the first argument is greater than the second argument;
 * arguments are assumed to be xsd:ints;
 *
 * @author (C) Hans-Ulrich Krieger
 * @version Tue Sep 29 11:11:19 CEST 2009
 * @return true or false
 * @see BooleanOperator
 * @since JDK 1.5
 */
public final class IGreater extends BooleanOperator {

  /**
   * note that apply() does NOT check at the moment whether the int args
   * represent in fact XSD ints;
   * note that apply() does NOT check whether it is given exactly two arguments
   */
  protected boolean holds(int[] args) {
    return ((XsdInt) getObject(args[0])).value > ((XsdInt) getObject(args[1])).value;
  }

}
