package de.dfki.lt.hfc.types;

/**
 * The xsd:kg datatype is supposed to encode weight measured in kilograms
 *
 * @author (C) Hans-Ulrich Krieger
 * @since JDK 1.5
 * @version Tue Mar  1 11:37:05 CET 2016
 */
public final class XsdKg extends XsdAnySimpleType {

  public final static String NAME = "kg";
  
  public final static String SHORT_NAME = '<' + SHORT_PREFIX + NAME + '>';
  public final static String LONG_NAME = '<' + LONG_PREFIX + NAME + '>';
  
  static {
    registerConstructor(XsdKg.class, null, SHORT_NAME, LONG_NAME);
  }
  
  public double value;
  
  /**
   * @param value a Java double representation of weight measured in kilograms
   */
  public XsdKg(double value) {
    this.value = value;
  }
  
  /**
   * @param value a string, representing weight; e.g., "\"73.2\"^^<xsd:kg>"
   */
  public XsdKg(String value) {
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
   * returns a java.lang.Double container for an HFC XsdKg object
   */
  public Object toJava() {
    return this.value;
  }
  
  /**
   * returns the equivalent of this.value measured in xsd:gm;
   * multiplication factor is 1000
   */
  public double toGm() {
    return this.value * 1000.0;
  }
  
}
