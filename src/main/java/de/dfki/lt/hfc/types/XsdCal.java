package de.dfki.lt.hfc.types;

import java.util.Objects;

/**
 * The xsd:cal datatype is supposed to encode an amount of calories
 *
 * @author (C) Hans-Ulrich Krieger
 * @version Tue Mar  1 13:41:15 CET 2016
 * @since JDK 1.5
 */
public final class XsdCal extends XsdAnySimpleType {

  public final static String NAME = "cal";

  public final static String SHORT_NAME = '<' + NS.getShort() + ":" + NAME + '>';
  public final static String LONG_NAME = '<' + NS.getLong() + NAME + '>';

  static {
    registerConstructor(XsdCal.class, SHORT_NAME, LONG_NAME);
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
   * binary version is given the value directly
   */
  public static String toString(double val) {
    StringBuilder sb = new StringBuilder("\"");
    sb.append(val);
    sb.append("\"^^");
    if (NS.isShort())
      sb.append(SHORT_NAME);
    else
      sb.append(LONG_NAME);
    return sb.toString();
  }

  /**
   * depending on shortIsDefault, either the suffix SHORT_NAME or LONG_NAME is used
   */
  public String toString() {
    return toString(this.value);
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


  @Override
  public int compareTo(Object o) {
    if (o instanceof AnyType.MinMaxValue) {
      AnyType.MinMaxValue minMaxValue = (MinMaxValue) o;
      return minMaxValue.compareTo(this);
    }
    if (!(o instanceof XsdCal)) {
      throw new IllegalArgumentException("Can't compare " + this.getClass() + " and " + o.getClass());
    }
    return Double.compare(this.value, ((XsdCal) o).value);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    XsdCal xsdCal = (XsdCal) o;
    return Double.compare(xsdCal.value, value) == 0;
  }

  @Override
  public int hashCode() {
    return Objects.hash(value);
  }
}
