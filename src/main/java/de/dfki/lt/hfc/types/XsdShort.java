package de.dfki.lt.hfc.types;

import java.util.Objects;

/**
 * @author (C) Hans-Ulrich Krieger
 * @version Fri Jan 29 19:30:23 CET 2016
 * @since JDK 1.5
 */
public class XsdShort extends XsdNumber {
  public final static String NAME = "short";

  public final static String SHORT_NAME = '<' + NS.getShort() + ":" + NAME + '>';
  public final static String LONG_NAME = '<' + NS.getLong() + NAME + '>';

  static {
    registerConstructor(XsdShort.class, SHORT_NAME, LONG_NAME);
    registerConverter(short.class, XsdShort.class);
    registerConverter(Short.class, XsdShort.class);
  }

  public short value;

  /**
   * @param value a Java short representation of an XSD short
   */
  public XsdShort(short value) {
    this.value = value;
  }

  /**
   * @param value a Java short representation of an XSD short
   */
  public XsdShort(Short value) {
    this.value = value;
  }

  /**
   * @param value a string, representing an XSD short, e.g., "\"42\"^^<xsd:short>"
   */
  public XsdShort(String value) {
    // get rid of "^^xsd:short" and leading & trailing '"' chars
    this.value = Short.parseShort(extractValue(value));
  }

  /**
   * binary version is given the value directly
   */
  public static String toString(short val) {
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
   * de.dfki.lt.hfc.NamespaceManager.SHORT_NAME
   * or
   * de.dfki.lt.hfc.NamespaceManager.LONG_NAME
   * is used
   */
  @Override
  public String toString() {
    return toString(this.value);
  }

  /**
   * turn short value into a string
   */
  @Override
  public String toName() {
    return Short.toString(this.value);
  }

  /**
   * returns a java.lang.Short container for an HFC XsdShort object
   */
  @Override
  public Object toJava() {
    return this.value;
  }

  @Override
  public int compareTo(Object o) {
    if (o instanceof AnyType.MinMaxValue) {
      AnyType.MinMaxValue minMaxValue = (MinMaxValue) o;
      return minMaxValue.compareTo(this);
    }
    if (o instanceof XsdShort)
      return Short.compare(this.value, ((XsdShort) o).value);
    else if (o instanceof XsdNumber)
      return Short.compare(this.value, ((XsdNumber) o).toNumber().shortValue());
    throw new IllegalArgumentException("Can't compare " + this.getClass() + " and " + o.getClass());
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    XsdShort xsdShort = (XsdShort) o;
    return value == xsdShort.value;
  }

  @Override
  public int hashCode() {
    return Objects.hash(value);
  }
}
