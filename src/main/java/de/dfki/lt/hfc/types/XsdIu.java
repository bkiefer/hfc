package de.dfki.lt.hfc.types;

/**
 * The xsd:iu datatype is supposed to encode the insuline intake measured
 * in international units
 *
 * @author (C) Hans-Ulrich Krieger
 * @since JDK 1.5
 * @version Wed Mar  2 10:07:54 CET 2016
 */
public final class XsdIu extends XsdAnySimpleType {

  public final static String NAME = "iu";
  
  public final static String SHORT_NAME = '<' + SHORT_PREFIX + NAME + '>';
  public final static String LONG_NAME = '<' + LONG_PREFIX + NAME + '>';
  
  static {
  registerConstructor(XsdIu.class, SHORT_NAME, LONG_NAME);
  }

  public double value;

  /**
   * @param value a Java double representation for international units
   */
  public XsdIu(double value) {
    this.value = value;
  }

  /**
   * @param value a string, representing length; e.g., "\"2.4\"^^<xsd:iu>"
   */
  public XsdIu(String value) {
    this.value = Double.parseDouble(extractValue(value));
  }

  /**
   * depending on shortIsDefault, either the suffix SHORT_NAME or LONG_NAME is used
   */
  public String toString(boolean shortIsDefault) {
    return toString(this.value, shortIsDefault);
  }

  /**
   * binary version is given the value directly
   */
  public static String toString(double val, boolean shortIsDefault) {
    StringBuilder sb = new StringBuilder("\"");
    sb.append(val);
    sb.append("\"^^");
    if (shortIsDefault)
      sb.append(SHORT_NAME);
    else
      sb.append(LONG_NAME);
    return sb.toString();
  }

  /**
   * turn double value into a string
   */
  public String toName() {
    return Double.toString(this.value);
  }

  /**
   * returns a java.lang.Double container for an HFC XsdIu object
   */
  public Object toJava() {
    return this.value;
  }

}
