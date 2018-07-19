package de.dfki.lt.hfc.operators;

import de.dfki.lt.hfc.BooleanOperator;
import de.dfki.lt.hfc.types.XsdDateTime;


public class DTGreaterEqual extends BooleanOperator{
  /**
   * note that apply() does NOT check at the moment whether the int args
   * represent in fact XSD floats;
   * note that apply() does NOT check whether it is given exactly two arguments
   */
  protected boolean holds(int[] args) {
    XsdDateTime date = ((XsdDateTime) getObject(args[0]));
    return date.compareTo(getObject(args[1])) >= 0;
  }
}
