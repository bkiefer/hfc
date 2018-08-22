package de.dfki.lt.hfc.operators;

import de.dfki.lt.hfc.BooleanOperator;
import de.dfki.lt.hfc.FunctionalOperator;
import de.dfki.lt.hfc.types.XsdString;

/**
 * checks whether the first argument contains the second argument;
 * arguments are assumed to be strings of type xsd:string
 *
 * @author (C) Hans-Ulrich Krieger
 * @version Wed Jun 23 11:04:27 CEST 2010
 * @see FunctionalOperator
 * @since JDK 1.5
 */
public final class SContains extends BooleanOperator {

  /**
   * note that apply() does NOT check at the moment whether the int args
   * represent in fact XSD strings (and not URIs, XSD ints, etc.);
   * note that apply() does NOT check whether it is given exactly two arguments
   *
   * @return FunctionalOperator.TRUE or FunctionalOperator.FALSE
   */
  protected boolean holds(int[] args) {
    return ((XsdString) getObject(args[0])).toCharSequence()
            .contains(((XsdString) getObject(args[1])).toCharSequence());
  }

}
