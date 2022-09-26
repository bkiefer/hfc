package de.dfki.lt.hfc.types;

import java.util.Objects;

/**
 * float is patterned after the IEEE single-precision 32-bit floating point type [IEEE 754-1985]
 *
 * @author (C) Hans-Ulrich Krieger
 * @version Fri Jan 29 19:29:19 CET 2016
 * @since JDK 1.5
 */
public final class XsdFloat extends XsdNumber {
  public final static String NAME = "float";

  public final static String SHORT_NAME = '<' + NS.getShort() + ":" + NAME + '>';
  public final static String LONG_NAME = '<' + NS.getLong() + NAME + '>';

  static {
    registerConstructor(XsdFloat.class, SHORT_NAME, LONG_NAME);
    registerConverter(float.class, XsdFloat.class);
    registerConverter(Float.class, XsdFloat.class);
  }

  public float value;

  /**
   * @param value a Java float representation of an XSD float
   */
  public XsdFloat(float value) {
    this.value = value;
  }

  /**
   * @param value a Java float representation of an XSD float
   */
  public XsdFloat(Float value) {
    this.value = value;
  }

  /**
   * @param value a string, representing an XSD float, e.g., "\"3.1415\"^^<xsd:float>"
   */
  public XsdFloat(String value) {
    // get rid of "^^xsd:float" and leading & trailing '"' chars
    this.value = Float.parseFloat(extractValue(value));
  }

  /**
   * binary version is given the value directly
   */
  public static String toString(float val) {
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
   * depending on shortIsDefault, either the suffix
   * SHORT_NAME
   * or
   * LONG_NAME
   * is used
   */
  public String toString() {
    return toString(this.value);
  }

  /**
   * turn float value into a string
   */
  public String toName() {
    return Float.toString(this.value);
  }

  /**
   * returns a java.lang.Float container for an HFC XsdFloat object
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
    if (o instanceof XsdFloat)
      return Float.compare(this.value, ((XsdFloat) o).value);
    else if (o instanceof XsdNumber)
      return Float.compare(this.value, ((XsdNumber) o).toNumber().floatValue());
    throw new IllegalArgumentException("Can't compare " + this.getClass() + " and " + o.getClass());
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    XsdFloat xsdFloat = (XsdFloat) o;
    return Float.compare(xsdFloat.value, value) == 0;
  }

  @Override
  public int hashCode() {
    return Objects.hash(value);
  }
}
