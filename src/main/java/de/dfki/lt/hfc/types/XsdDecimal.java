package de.dfki.lt.hfc.types;


import java.util.Objects;

/**
 * @author Christian Willms - Date: 28.11.17 17:11.
 * @version 28.11.17
 */
public class XsdDecimal extends XsdNumber {

  public final static String NAME = "decimal";

  public final static String SHORT_NAME = '<' + NS.getShort() + ":" + NAME + '>';
  public final static String LONG_NAME = '<' + NS.getLong() + NAME + '>';

  static {
    registerConstructor(XsdDecimal.class, SHORT_NAME, LONG_NAME);
  }

  public double value;

  /**
   * @param value a Java double representation of an XSD double
   */
  public XsdDecimal(double value) {
    this.value = value;
  }

  /**
   * @param value a string, representing an XSD double, e.g., "\"2.71828\"^^<xsd:double>"
   */
  public XsdDecimal(String value) {
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
    if (o instanceof XsdDecimal)
      return Double.compare(this.value, ((XsdDecimal) o).value);
    else if (o instanceof XsdNumber)
      return Double.compare(this.value, ((XsdNumber) o).toNumber().doubleValue());
    throw new IllegalArgumentException("Can't compare " + this.getClass() + " and " + o.getClass());
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    XsdDecimal that = (XsdDecimal) o;
    return Double.compare(that.value, value) == 0;
  }

  @Override
  public int hashCode() {
    return Objects.hash(value);
  }
}
