package de.dfki.lt.hfc.types;

/**
 * The xsd:cal datatype is supposed to encode an amount of calories
 *
 * @author (C) Hans-Ulrich Krieger
 * @since JDK 1.5
 * @version Tue Mar  1 13:41:15 CET 2016
 */
public final class XsdCal extends XsdAnySimpleType {
  
  public final static String NAME = "cal";
  
  public final static String SHORT_NAME = '<' + SHORT_PREFIX + NAME + '>';
  public final static String LONG_NAME = '<' + LONG_PREFIX + NAME + '>';
  
  static {
    registerConstructor(XsdCal.class, null, SHORT_NAME, LONG_NAME);
  }
  
  public double value;
  
  /**
   * @param value a Java double representation for calorie intake
   */
  public XsdCal(double value) {
    this.value = value;
  }
  
  /**
   * @param value a string, representing weight; e.g., "\"884.2\"^^<xsd:cal>"
   */
  public XsdCal(String value) {
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
   * returns a java.lang.Double container for an HFC XsdCal object
   */
  public Object toJava() {
    return this.value;
  }
  
}
