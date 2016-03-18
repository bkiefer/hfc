package de.dfki.lt.hfc;

import java.util.*;

/**
 * the class XsdAtomProxy inherits the field name from its abstract
 * superclass Literal
 *
 * @see de.dfki.lt.hfc.Literal
 *
 * @author (C) Hans-Ulrich Krieger
 * @since JDK 1.5
 * @version Fri Mar 18 10:31:30 CET 2016

 */
public class XsdAtomProxy extends Literal {

  /**
   * assigns a name to this XsdAtomProxy instance
   */
  public XsdAtomProxy(String name) {
    this.name = name;
  }

}
