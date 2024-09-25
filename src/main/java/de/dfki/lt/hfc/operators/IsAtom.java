package de.dfki.lt.hfc.operators;

import de.dfki.lt.hfc.BooleanOperator;
import de.dfki.lt.hfc.types.XsdAnySimpleType;

/**
 * checks whether the first argument given to IsAtom is an XSD atom
 *
 * @author (C) Hans-Ulrich Krieger
 * @version Thu Dec  9 14:01:09 CET 2010
 * @see de.dfki.lt.hfc.types.Xsd
 * @since JDK 1.5
 */
public final class IsAtom extends BooleanOperator {

  /**
   *
   */
  protected boolean holds(int[] args) {
    return (getObject(args[0]) instanceof XsdAnySimpleType);
  }

}
