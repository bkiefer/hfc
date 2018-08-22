package de.dfki.lt.hfc.proxy;

/**
 * the class XsdAtomProxy inherits the field name from its abstract
 * superclass Literal
 *
 * @author (C) Hans-Ulrich Krieger
 * @version Fri Mar 18 10:31:30 CET 2016
 * @see de.dfki.lt.hfc.proxy.Literal
 * @since JDK 1.5
 */
public class XsdAtomProxy extends Literal {

  /**
   * assigns a name to this XsdAtomProxy instance
   */
  public XsdAtomProxy(String name) {
    this.name = name;
  }

}
