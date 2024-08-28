package de.dfki.lt.hfc.types;

import java.util.Objects;

/**
 * @author (C) Hans-Ulrich Krieger
 * @version Fri Jan 29 19:30:23 CET 2016
 * @since JDK 1.5
 */
public class XsdByte extends XsdNumber {
  public final static String NAME = "byte";

  public final static String SHORT_NAME = '<' + NS.getShort() + ":" + NAME + '>';
  public final static String LONG_NAME = '<' + NS.getLong() + NAME + '>';

  static {
    registerConstructor(XsdByte.class, SHORT_NAME, LONG_NAME);
    registerConverter(byte.class, XsdByte.class);
    registerConverter(Byte.class, XsdByte.class);
  }

  public byte value;

  /**
   * @param value a Java byte representation of an XSD byte
   */
  public XsdByte(byte value) {
    this.value = value;
  }

  /**
   * @param value a Java byte representation of an XSD byte
   */
  public XsdByte(Byte value) {
    this.value = value;
  }

  /**
   * @param value a string, representing an XSD byte, e.g., "\"42\"^^<xsd:byte>"
   */
  public XsdByte(String value) {
    // get rid of "^^xsd:byte" and leading & trailing '"' chars
    this.value = Byte.parseByte(extractValue(value));
  }

  /**
   * binary version is given the value directly
   */
  public static String toString(byte val) {
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
   * turn byte value into a string
   */
  @Override
  public String toName() {
    return Byte.toString(this.value);
  }

  /**
   * returns a java.lang.Byte container for an HFC XsdByte object
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
    if (o instanceof XsdByte)
      return Byte.compare(this.value, ((XsdByte) o).value);
    else if (o instanceof XsdNumber)
      return Byte.compare(this.value, ((XsdNumber) o).toNumber().byteValue());
    throw new IllegalArgumentException("Can't compare " + this.getClass() + " and " + o.getClass());
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    XsdByte xsdByte = (XsdByte) o;
    return value == xsdByte.value;
  }

  @Override
  public int hashCode() {
    return Objects.hash(value);
  }
}
