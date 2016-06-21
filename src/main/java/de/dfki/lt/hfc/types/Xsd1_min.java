package de.dfki.lt.hfc.types;

/**
 * The xsd:min-1 datatype is supposed to encode heart rate measured in 1/min
 *
 * @author (C) Hans-Ulrich Krieger
 * @since JDK 1.5
 * @version Wed Mar  2 10:07:54 CET 2016
 */
public final class Xsd1_min extends XsdAnySimpleType {
  
  public final static String NAME = "min-1";
  
  public final static String SHORT_NAME = '<' + SHORT_PREFIX + NAME + '>';
  public final static String LONG_NAME = '<' + LONG_PREFIX + NAME + '>';
  
  static {
    registerConstructor(Xsd1_min.class, null, SHORT_NAME, LONG_NAME);
  }
  
  public double value;
  
  /**
   * @param value a Java double representation of the heart rate
   */
  public Xsd1_min(double value) {
    this.value = value;
  }
  
  /**
   * @param value a string, representing length; e.g., "\"84\"^^<xsd:min-1>"
   */
  public Xsd1_min(String value) {
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
   * returns a java.lang.Double container for an HFC Xsd1_min object
   */
  public Object toJava() {
    return this.value;
  }
  
}
