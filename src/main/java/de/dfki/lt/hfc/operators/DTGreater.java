package de.dfki.lt.hfc.operators;

import de.dfki.lt.hfc.BooleanOperator;
import de.dfki.lt.hfc.types.XsdDateTime;

public class DTGreater extends BooleanOperator {

  @Override
  protected boolean holds(int[] args) {
    XsdDateTime date = ((XsdDateTime) getObject(args[0]));
    return date.compareTo(getObject(args[1])) > 0;
  }
}
