package de.dfki.lt.hfc.types;

/**
 * The xsd:mg_dL datatype is supposed to encode blood sugar level in
 * milligrams per decilitre
 *
 * @author (C) Hans-Ulrich Krieger
 * @version Tue Mar  1 13:41:15 CET 2016
 * @since JDK 1.5
 */
public final class XsdMg_dL extends XsdAnySimpleType {

  public final static String NAME = "mg_dL";

  public final static String SHORT_NAME = '<' + SHORT_PREFIX + NAME + '>';
  public final static String LONG_NAME = '<' + LONG_PREFIX + NAME + '>';

  static {
    registerConstructor(XsdMg_dL.class, SHORT_NAME, LONG_NAME);
  }

  public double value;

  /**
   * @param value a Java double representation of the blood sugar concentration
   */
  public XsdMg_dL(double value) {
    this.value = value;
  }

  /**
   * @param value a string, representing weight; e.g., "\"92.2\"^^<xsd:mg_dL>"
   */
  public XsdMg_dL(String value) {
    this.value = Double.parseDouble(extractValue(value));
  }

  /**
   * binary version is given the value directly
   */
  public static String toString(double val, boolean shortIsDefault) {
    StringBuilder sb = new StringBuilder("\"");
    sb.append(val);
    sb.append("\"^^");
    if (shortIsDefault)
      sb.append(SHORT_NAME);
    else
      sb.append(LONG_NAME);
    return sb.toString();
  }

  /**
   * depending on shortIsDefault, either the suffix SHORT_NAME or LONG_NAME is used
   */
  public String toString(boolean shortIsDefault) {
    return toString(this.value, shortIsDefault);
  }

  /**
   * turn double value into a string
   */
  public String toName() {
    return Double.toString(this.value);
  }

  /**
   * returns a java.lang.Double container for an HFC XsdMg_dL object
   */
  public Object toJava() {
    return this.value;
  }

  /**
   * returns the equivalent of this.value measured in xsd:mmol_L;
   * multiplication factor is 1/18
   */
  public double toMmol_L() {
    return this.value / 18.0;
  }

  @Override
  public int compareTo(Object o) {
    if (o instanceof AnyType.MinMaxValue) {
      AnyType.MinMaxValue minMaxValue = (MinMaxValue) o;
      return minMaxValue.compareTo(this);
    }
    if (!(o instanceof XsdMg_dL)) {
      throw new IllegalArgumentException("Can't compare " + this.getClass() + " and " + o.getClass());
    }
    return Double.compare(this.value, ((XsdMg_dL) o).value);
  }

}
