package de.dfki.lt.hfc.types;

import java.util.Objects;

/**
 * an encoding of the XSD gYear format "[+|-]yyyy",
 * including negative years (= BC)
 *
 * @author (C) Hans-Ulrich Krieger
 * @version Fri Jan 29 19:17:10 CET 2016
 * @see http://www.w3.org/TR/xmlschema-2/
 * @see http://www.schemacentral.com/sc/xsd/t-xsd_gYear.html
 * @since JDK 1.5
 */
public final class XsdGYear extends XsdAnySimpleType {
  public final static String NAME = "gYear";

  public final static String SHORT_NAME = '<' + NS.getShort() + ":" + NAME + '>';
  public final static String LONG_NAME = '<' + NS.getLong() + NAME + '>';


  static {
    registerConstructor(XsdGYear.class, SHORT_NAME, LONG_NAME);
  }

  /**
   * - = BC = false
   * + = AD = true
   * in case NO sign is specified, sign defaults to true
   */
  public boolean sign = true;

  /**
   * I represent the sign in a separate boolean field
   */
  public int year;

  /**
   *
   */
  public XsdGYear(int year) {
    this.year = year;
  }

  /**
   *
   */
  public XsdGYear(boolean sign, int year) {
    this.sign = sign;
    this.year = year;
  }

  /**
   * @param time an XSD gYear expression
   */
  public XsdGYear(String time) {
    // get rid of "^^<xsd:gYear>" and leading & trailing '"' chars
    time = extractValue(time);
    // is there a sign ?
    if (time.charAt(0) == '+')
      time = time.substring(1);
    else if (time.charAt(0) == '-') {
      this.sign = false;
      time = time.substring(1);
    }
    this.year = Integer.parseInt(time.substring(0));
  }

  /**
   * binary version is given the value directly
   */
  public static String toString(String val) {
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
    final String tail = "\"^^" + (NS.isShort() ? SHORT_NAME : LONG_NAME);
    StringBuilder sb = new StringBuilder("\"");
    if (!this.sign)
      sb.append('-');
    if (this.year >= 1000)
      sb.append(this.year);
    else if (this.year >= 100)
      sb.append("0").append(this.year);
    else if (this.year >= 10)
      sb.append("00").append(this.year);
    else
      sb.append("000").append(this.year);
    return sb.append(tail).toString();
  }

  /**
   * even though there exist a java.util.Date and java.util.GregorianCalendar
   * class, these classes do not perfectly fit the intention behind XsdGMonthDay
   * so return this object
   */
  public Object toJava() {
    return this;
  }

  @Override
  public int compareTo(Object o) {
    if (o instanceof AnyType.MinMaxValue) {
      AnyType.MinMaxValue minMaxValue = (MinMaxValue) o;
      return minMaxValue.compareTo(this);
    }
    if (!(o instanceof XsdGYear)) {
      throw new IllegalArgumentException("Can't compare " + this.getClass() + " and " + o.getClass());
    }
    XsdGYear date = (XsdGYear) o;
    return Integer.compare(transform(this.year), date.transform(date.year));

  }

  private int transform(int v) {
    if (!this.sign) {
      return v * -1;
    }
    return v;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    XsdGYear xsdGYear = (XsdGYear) o;
    return sign == xsdGYear.sign &&
            year == xsdGYear.year;
  }

  @Override
  public int hashCode() {
    return Objects.hash(sign, year);
  }
}
