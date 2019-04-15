package de.dfki.lt.hfc.types;

import java.util.Objects;

/**
 * @author (C) Hans-Ulrich Krieger
 * @version Fri Jan 29 18:42:17 CET 2016
 * @since JDK 1.5
 */
public final class XsdBoolean extends XsdAnySimpleType {
  public final static String NAME = "boolean";

  public final static String SHORT_NAME = '<' + NS.SHORT_NAMESPACE + ":" + NAME + '>';
  public final static String LONG_NAME = '<' + NS.LONG_NAMESPACE + NAME + '>';

  static {
    registerConstructor(XsdBoolean.class, SHORT_NAME, LONG_NAME);
    registerConverter(Boolean.class, XsdBoolean.class);
    registerConverter(boolean.class, XsdBoolean.class);
  }

  public boolean value;

  /**
   * @param value a Java boolean representation of an XSD boolean
   */
  public XsdBoolean(boolean value) {
    this.value = value;
  }

  /**
   * @param value a Java boolean representation of an XSD boolean
   */
  public XsdBoolean(Boolean value) {
    this.value = value;
  }

  /**
   * @param value a Java string, representing an XSD boolean, e.g., "\"true\"^^<xsd:boolean>"
   */
  public XsdBoolean(String value) {
    // get rid of "^^xsd:boolean" and leading & trailing '"' chars
    this.value = Boolean.parseBoolean(extractValue(value));
  }

  /**
   * binary version is given the value directly
   */
  public static String toString(boolean val) {
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
   * de.dfki.lt.hfc.SHORT_NAME
   * or
   * de.dfki.lt.hfc.LONG_NAME
   * is used
   */
  public String toString() {
    return toString(this.value);
  }

  /**
   * turn Boolean value into a string
   */
  public String toName() {
    return Boolean.toString(this.value);
  }

  /**
   * returns a java.lang.Boolean container for an HFC XsdBoolean object
   */
  public Object toJava() {
    return Boolean.valueOf(this.value);
  }

  @Override
  public int compareTo(Object o) {
    if (o instanceof AnyType.MinMaxValue) {
      AnyType.MinMaxValue minMaxValue = (MinMaxValue) o;
      return minMaxValue.compareTo(this);
    }
    if (!(o instanceof XsdBoolean)) {
      throw new IllegalArgumentException("Can't compare " + this.getClass() + " and " + o.getClass());
    }
    return Boolean.compare(this.value, ((XsdBoolean) o).value);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    XsdBoolean that = (XsdBoolean) o;
    return value == that.value;
  }

  @Override
  public int hashCode() {
    return Objects.hash(value);
  }
}
