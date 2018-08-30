package de.dfki.lt.hfc.types;

import java.util.Objects;

/**
 * The xsd:iu datatype is supposed to encode the insuline intake measured
 * in international units
 *
 * @author (C) Hans-Ulrich Krieger
 * @version Wed Mar  2 10:07:54 CET 2016
 * @since JDK 1.5
 */
public final class XsdIu extends XsdAnySimpleType {

  public final static String NAME = "iu";

  public final static String SHORT_NAME = '<' + NS.SHORT_NAMESPACE + ":" + NAME + '>';
  public final static String LONG_NAME = '<' + NS.LONG_NAMESPACE + NAME + '>';

  static {
    registerConstructor(XsdIu.class, SHORT_NAME, LONG_NAME);
  }

  public double value;

  /**
   * @param value a Java double representation for international units
   */
  public XsdIu(double value) {
    this.value = value;
  }

  /**
   * @param value a string, representing length; e.g., "\"2.4\"^^<xsd:iu>"
   */
  public XsdIu(String value) {
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
   * returns a java.lang.Double container for an HFC XsdIu object
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
    if (!(o instanceof XsdIu)) {
      throw new IllegalArgumentException("Can't compare " + this.getClass() + " and " + o.getClass());
    }
    return Double.compare(this.value, ((XsdIu) o).value);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    XsdIu xsdIu = (XsdIu) o;
    return Double.compare(xsdIu.value, value) == 0;
  }

  @Override
  public int hashCode() {
    return Objects.hash(value);
  }
}
