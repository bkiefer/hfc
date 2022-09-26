package de.dfki.lt.hfc.types;

import java.util.Objects;

/**
 * The xsd:mmHg datatype is supposed to encode blood pressure in
 * millimeter of mercury
 *
 * @author (C) Hans-Ulrich Krieger
 * @version Tue Mar  1 13:41:15 CET 2016
 * @since JDK 1.5
 */
public final class XsdMmHg extends XsdAnySimpleType {

  public final static String NAME = "mmHg";

  public final static String SHORT_NAME = '<' + NS.getShort() + ":" + NAME + '>';
  public final static String LONG_NAME = '<' + NS.getLong() + NAME + '>';

  static {
    registerConstructor(XsdMmHg.class, SHORT_NAME, LONG_NAME);
  }

  public double value;

  /**
   * @param value a Java double representation of the blood pressure
   */
  public XsdMmHg(double value) {
    this.value = value;
  }

  /**
   * @param value a string, representing weight; e.g., "\"92.0\"^^<xsd:mmHg>"
   */
  public XsdMmHg(String value) {
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
   * returns a java.lang.Double container for an HFC XsdMmHg object
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
    if (!(o instanceof XsdMmHg)) {
      throw new IllegalArgumentException("Can't compare " + this.getClass() + " and " + o.getClass());
    }
    return Double.compare(this.value, ((XsdMmHg) o).value);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    XsdMmHg xsdMmHg = (XsdMmHg) o;
    return Double.compare(xsdMmHg.value, value) == 0;
  }

  @Override
  public int hashCode() {
    return Objects.hash(value);
  }
}
