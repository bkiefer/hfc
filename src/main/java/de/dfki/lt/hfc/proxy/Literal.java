package de.dfki.lt.hfc.proxy;

/**
 * this abstract class is the superclass of both UriProxy and XsdAtomProxy;
 * it specifies a single field "name" (of type String), the name of the URI
 * or XSD atom, resp.
 * NOTE: we use UriProxy to represent both URIs and blank nodes!
 *
 * @author (C) Hans-Ulrich Krieger
 * @since JDK 1.5
 * @version Fri Mar 18 10:12:21 CET 2016

 */
public abstract class Literal {

  /**
   * the name of the literal
   */
  protected String name;

  /**
   * returns the name of the URI
   */
  public String getName() {
    return this.name;
  }

  /**
   * @return true iff parameter literal is a URI
   * @return false otherwise
   */
  public static boolean isUri(Literal literal) {
    return (literal instanceof UriProxy);
  }

  /**
   * @return true iff parameter literal is a XSD atom
   * @return false otherwise
   */
  public static boolean isXsdAtom(Literal literal) {
    return (literal instanceof UriProxy);
  }

}
