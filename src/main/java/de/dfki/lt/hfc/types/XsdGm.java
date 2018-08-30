package de.dfki.lt.hfc.types;

import java.util.Objects;

/**
 * The xsd:gm datatype is supposed to encode weight measured in grams
 *
 * @author (C) Hans-Ulrich Krieger
 * @version Mon Feb 29 15:40:22 CET 2016
 * @since JDK 1.5
 */
public final class XsdGm extends XsdAnySimpleType {

  public final static String NAME = "gm";

  public final static String SHORT_NAME = '<' + NS.SHORT_NAMESPACE + ":" + NAME + '>';
  public final static String LONG_NAME = '<' + NS.LONG_NAMESPACE + NAME + '>';

  static {
    registerConstructor(XsdGm.class, SHORT_NAME, LONG_NAME);
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

  @Override
  public int compareTo(Object o) {
    if (o instanceof AnyType.MinMaxValue) {
      AnyType.MinMaxValue minMaxValue = (MinMaxValue) o;
      return minMaxValue.compareTo(this);
    }
    if (!(o instanceof XsdGm)) {
      throw new IllegalArgumentException("Can't compare " + this.getClass() + " and " + o.getClass());
    }
    return Double.compare(this.value, ((XsdGm) o).value);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    XsdGm xsdGm = (XsdGm) o;
    return Double.compare(xsdGm.value, value) == 0;
  }

  @Override
  public int hashCode() {
    return Objects.hash(value);
  }
}
