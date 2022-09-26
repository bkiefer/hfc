package de.dfki.lt.hfc.types;

import java.util.Objects;

/**
 * The xsd:min-1 datatype is supposed to encode heart rate measured in 1/min
 *
 * @author (C) Hans-Ulrich Krieger
 * @version Wed Mar  2 10:07:54 CET 2016
 * @since JDK 1.5
 */
public final class Xsd1_min extends XsdAnySimpleType {

  public final static String NAME = "min-1";

  public final static String SHORT_NAME = '<' + NS.getShort() + ":" + NAME + '>';
  public final static String LONG_NAME = '<' + NS.getLong() + NAME + '>';

  static {
    registerConstructor(Xsd1_min.class, SHORT_NAME, LONG_NAME);
  }

  public double value;

  /**
   * @param value a Java double representation of the heart rate
   */
  public Xsd1_min(double value) {
    this.value = value;
  }

  /**
   * @param value a string, representing length; e.g., "\"84\"^^<xsd:min-1>"
   */
  public Xsd1_min(String value) {
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
   * returns a java.lang.Double container for an HFC Xsd1_min object
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
    if (!(o instanceof Xsd1_min)) {
      throw new IllegalArgumentException("Can't compare " + this.getClass() + " and " + o.getClass());
    }
    return Double.compare(this.value, ((Xsd1_min) o).value);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Xsd1_min xsd1_min = (Xsd1_min) o;
    return Double.compare(xsd1_min.value, value) == 0;
  }

  @Override
  public int hashCode() {
    return Objects.hash(value);
  }
}
