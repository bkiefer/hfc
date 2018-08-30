package de.dfki.lt.hfc.types;

import java.util.Objects;

/**
 * The double datatype is patterned after the IEEE double-precision
 * 64-bit floating point type [IEEE 754-1985]
 *
 * @author (C) Hans-Ulrich Krieger
 * @version Fri Jan 29 19:28:31 CET 2016
 * @since JDK 1.5
 */
public final class XsdDouble extends XsdNumber {

  public final static String NAME = "double";

  public final static String SHORT_NAME = '<' + NS.SHORT_NAMESPACE + ":" + NAME + '>';
  public final static String LONG_NAME = '<' + NS.LONG_NAMESPACE + NAME + '>';

  static {
    registerConstructor(XsdDouble.class, SHORT_NAME, LONG_NAME);
    registerConverter(double.class, XsdDouble.class);
    registerConverter(Double.class, XsdDouble.class);
  }

  public double value;

  /**
   * @param value a Java double representation of an XSD double
   */
  public XsdDouble(double value) {
    this.value = value;
  }

  /**
   * @param value a Java double representation of an XSD double
   */
  public XsdDouble(Double value) {
    this.value = value;
  }

  /**
   * @param value a string, representing an XSD double, e.g., "\"2.71828\"^^<xsd:double>"
   */
  public XsdDouble(String value) {
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
   * returns a java.lang.Double container for an HFC XsdDouble object
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
    if (o instanceof XsdDouble)
      return Double.compare(this.value, ((XsdDouble) o).value);
    else if (o instanceof XsdNumber)
      return Double.compare(this.value, ((XsdNumber) o).toNumber().doubleValue());
    throw new IllegalArgumentException("Can't compare " + this.getClass() + " and " + o.getClass());
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    XsdDouble xsdDouble = (XsdDouble) o;
    return Double.compare(xsdDouble.value, value) == 0;
  }

  @Override
  public int hashCode() {
    return Objects.hash(value);
  }
}
