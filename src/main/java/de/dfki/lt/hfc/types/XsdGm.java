package de.dfki.lt.hfc.types;

/**
 * The xsd:gm datatype is supposed to encode weight measured in grams
 *
 * @author (C) Hans-Ulrich Krieger
 * @since JDK 1.5
 * @version Mon Feb 29 15:40:22 CET 2016
 */
public final class XsdGm extends XsdAnySimpleType {

  public final static String NAME = "gm";

  public final static String SHORT_NAME = '<' + SHORT_PREFIX + NAME + '>';
  public final static String LONG_NAME = '<' + LONG_PREFIX + NAME + '>';

  static {
    registerConstructor(XsdGm.class, null, SHORT_NAME, LONG_NAME);
  }

  public double value;

  /**
   * @param value a Java double representation of weight measured in grams
   */
  public XsdGm(double value) {
    this.value = value;
  }

  /**
   * @param value a string, representing weight; e.g., "\"73948.2\"^^<xsd:gm>"
   */
  public XsdGm(String value) {
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
   * returns a java.lang.Double container for an HFC XsdGm object
   */
  public Object toJava() {
    return this.value;
  }
  
  /**
   * returns the equivalent of this.value measured in xsd:kg;
   * multiplication factor is 1/1000
   */
  public double toKg() {
    return this.value / 1000.0;
  }
  
}
