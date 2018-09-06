package de.dfki.lt.hfc.types;

import java.util.Objects;

/**
 * @author (C) Hans-Ulrich Krieger
 * @version Fri Jan 29 19:30:23 CET 2016
 * @since JDK 1.5
 */
public final class XsdInt extends XsdNumber {
  public final static String NAME = "int";

  public final static String SHORT_NAME = '<' + NS.SHORT_NAMESPACE + ":" + NAME + '>';
  public final static String LONG_NAME = '<' + NS.LONG_NAMESPACE + NAME + '>';

  static {
    registerConstructor(XsdInt.class, SHORT_NAME, LONG_NAME);
    registerConverter(int.class, XsdInt.class);
    registerConverter(Integer.class, XsdInt.class);
  }

  public int value;

  /**
   * @param value a Java int representation of an XSD int
   */
  public XsdInt(int value) {
    this.value = value;
  }

  /**
   * @param value a Java int representation of an XSD int
   */
  public XsdInt(Integer value) {
    this.value = value;
  }

  /**
   * @param value a string, representing an XSD int, e.g., "\"42\"^^<xsd:int>"
   */
  public XsdInt(String value) {
    // get rid of "^^xsd:int" and leading & trailing '"' chars
    this.value = Integer.parseInt(extractValue(value));
  }

  /**
   * binary version is given the value directly
   */
  public static String toString(int val) {
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
  public String toString() {
    return toString(this.value);
  }

  /**
   * turn int value into a string
   */
  public String toName() {
    return Integer.toString(this.value);
  }

  /**
   * returns a java.lang.Integer container for an HFC XsdInt object
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
    if (o instanceof XsdInt)
      return Integer.compare(this.value, ((XsdInt) o).value);
    else if (o instanceof XsdNumber)
      return Integer.compare(this.value, ((XsdNumber) o).toNumber().intValue());
    throw new IllegalArgumentException("Can't compare " + this.getClass() + " and " + o.getClass());
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    XsdInt xsdInt = (XsdInt) o;
    return value == xsdInt.value;
  }

  @Override
  public int hashCode() {
    return Objects.hash(value);
  }
}
