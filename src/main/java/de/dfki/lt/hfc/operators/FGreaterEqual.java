package de.dfki.lt.hfc.operators;

import de.dfki.lt.hfc.BooleanOperator;
import de.dfki.lt.hfc.types.XsdFloat;

/**
 * checks whether the first argument is greater or equal than the second argument;
 * arguments are assumed to be xsd:floats;
 *
 * @author (C) Hans-Ulrich Krieger
 * @version Tue Sep 29 11:11:19 CEST 2009
 * @return true or false
 * @see BooleanOperator
 * @since JDK 1.5
 */
public final class FGreaterEqual extends BooleanOperator {

  /**
   * note that apply() does NOT check at the moment whether the int args
   * represent in fact XSD floats;
   * note that apply() does NOT check whether it is given exactly two arguments
   */
  protected boolean holds(int[] args) {
    return (Float.compare(((XsdFloat) getObject(args[0])).value,
            ((XsdFloat) getObject(args[1])).value) >= 0);
  }

}
