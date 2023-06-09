package de.dfki.lt.hfc.types;

import java.util.Objects;

/**
 * The xsd:m datatype is supposed to encode length, height, etc. measured in centimeters
 *
 * @author (C) Hans-Ulrich Krieger
 * @version Wed Mar  2 10:07:54 CET 2016
 * @since JDK 1.5
 */
public final class XsdCm extends XsdAnySimpleType {

  public final static String NAME = "cm";

  public final static String SHORT_NAME = '<' + NS.getShort() + ":" + NAME + '>';
  public final static String LONG_NAME = '<' + NS.getLong() + NAME + '>';

  static {
    registerConstructor(XsdCm.class, SHORT_NAME, LONG_NAME);
  }

  public double value;

  /**
   * @param value a Java double representation of length measured in centimeters
   */
  public XsdCm(double value) {
    this.value = value;
  }

  /**
   * @param value a string, representing length; e.g., "\"182\"^^<xsd:cm>"
   */
  public XsdCm(String value) {
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
   * returns a java.lang.Double container for an HFC XsdCm object
   */
  public Object toJava() {
    return this.value;
  }

  /**
   * returns the equivalent of this.value measured in xsd:cm;
   * multiplication factor is 1/100
   */
  public double toM() {
    return this.value / 100.0;
  }

  @Override
  public int compareTo(Object o) {
    if (o instanceof AnyType.MinMaxValue) {
      AnyType.MinMaxValue minMaxValue = (MinMaxValue) o;
      return minMaxValue.compareTo(this);
    }
    if (!(o instanceof XsdCm)) {
      throw new IllegalArgumentException("Can't compare " + this.getClass() + " and " + o.getClass());
    }
    return Double.compare(this.value, ((XsdCm) o).value);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    XsdCm xsdCm = (XsdCm) o;
    return Double.compare(xsdCm.value, value) == 0;
  }

  @Override
  public int hashCode() {
    return Objects.hash(value);
  }
}
