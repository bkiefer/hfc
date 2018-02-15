package de.dfki.lt.hfc.operators;

import de.dfki.lt.hfc.FunctionalOperator;
import de.dfki.lt.hfc.types.XsdBoolean;

/**
 * @author Christian Willms - Date: 26.11.17 19:13.
 * @version 26.11.17
 */
public class IsTrue extends FunctionalOperator {


  @Override
  public int apply(int[] args) {
    if (((XsdBoolean)getObject(args[0])).value == true)
      return FunctionalOperator.TRUE;
    else
      return FunctionalOperator.FALSE;
  }
}
