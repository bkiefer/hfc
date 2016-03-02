package de.dfki.lt.hfc.types;

/**
 * The xsd:kg_m2 datatype is supposed to encode the body mass index
 *
 * @author (C) Hans-Ulrich Krieger
 * @since JDK 1.5
 * @version Tue Mar  1 13:41:15 CET 2016
 */
public final class XsdKg_m2 extends XsdAnySimpleType {
  
  public final static String NAME = "kg_m2";
  
  public final static String SHORT_NAME = '<' + SHORT_PREFIX + NAME + '>';
  public final static String LONG_NAME = '<' + LONG_PREFIX + NAME + '>';
  
  static {
    registerConstructor(XsdKg_m2.class, SHORT_NAME, LONG_NAME);
  }
  
  public double value;
  
  /**
   * @param value a Java double representation for body mass index
   */
  public XsdKg_m2(double value) {
    this.value = value;
  }
  
  /**
   * @param value a string, representing weight; e.g., "\"20.5\"^^<xsd:kg_m2>"
   */
  public XsdKg_m2(String value) {
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
   * returns a java.lang.Double container for an HFC XsdKg_m2 object
   */
  public Object toJava() {
    return this.value;
  }
  
}
