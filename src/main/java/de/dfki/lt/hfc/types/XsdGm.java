package de.dfki.lt.hfc.types;


/**
 * The gm datatype is supposed to encode weight measured in grams
 * @author (C) Hans-Ulrich Krieger
 * @since JDK 1.5
 * @version Mon Feb 29 15:40:22 CET 2016
 */
public final class XsdGm extends XsdAnySimpleType {
  public final static String NAME = "gm";

  public final static String SHORT_NAME = '<' + SHORT_PREFIX + NAME + '>';
  public final static String LONG_NAME = '<' + LONG_PREFIX + NAME + '>';

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
   * @param value a string, representing weight; e.g., "\"73948.2\"^^<xsd:gr>"
   */
  public XsdGm(String value) {
    this.value = Double.parseDouble(extractValue(value));
  }

  /**
   * depending on shortIsDefault, either the suffix
   *   de.dfki.lt.hfc.Namespace.XSD_GM_SHORT
   * or
   *   de.dfki.lt.hfc.Namespace.XSD_GM_LONG
   * is used
   */
  public String toString(boolean shortIsDefault) {
    StringBuilder sb = new StringBuilder("\"");
    sb.append(this.value);
    sb.append("\"^^");
    if (shortIsDefault)
      sb.append(SHORT_NAME);
    else
      sb.append(LONG_NAME);
    return sb.toString();
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
   * generates a string representation from the internal fields, but omits
   * the XSD type specification
   */
  public String toName() {
    // get rid of "^^<xsd:gm>"
    String grams = toString(de.dfki.lt.hfc.Namespace.shortIsDefault);
    int index = grams.lastIndexOf('^');
    return grams.substring(1, index - 2);
  }

  /**
   * there is _no_ Java data type available, so return this
   */
  public Object toJava() {
    return this.value;
  }

}
