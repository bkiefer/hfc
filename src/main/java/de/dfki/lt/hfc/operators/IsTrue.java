package de.dfki.lt.hfc.operators;

import de.dfki.lt.hfc.BooleanOperator;
import de.dfki.lt.hfc.types.XsdBoolean;

/**
 * @author Christian Willms - Date: 26.11.17 19:13.
 * @version 26.11.17
 */
public class IsTrue extends BooleanOperator {

  @Override
  protected boolean holds(int[] args) {
    if ( !(getObject(args[0]) instanceof XsdBoolean))
      return false;
    return ((XsdBoolean) getObject(args[0])).value;
  }
}
