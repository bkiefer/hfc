package de.dfki.lt.hfc.types;

import java.util.Objects;

/**
 * an encoding of the XSD gYearMonth format "[+|-]yyyy-MM" _without_ time zones,
 * including negative years (= BC)
 *
 * @author (C) Hans-Ulrich Krieger
 * @version Fri Jan 29 19:20:53 CET 2016
 * @see http://www.w3.org/TR/xmlschema-2/
 * @see http://www.schemacentral.com/sc/xsd/t-xsd_gYearMonth.html
 * @since JDK 1.5
 */
public final class XsdGYearMonth extends XsdAnySimpleType {
  public final static String NAME = "gYearMonth";

  public final static String SHORT_NAME = '<' + NS.getShort() + ":" + NAME + '>';
  public final static String LONG_NAME = '<' + NS.getLong() + NAME + '>';

  static {
    registerConstructor(XsdGYearMonth.class,
            null, SHORT_NAME, LONG_NAME);
  }

  /**
   * - = BC = false
   * + = AD = true
   * in case NO sign is specified, sign defaults to true
   */
  public boolean sign = true;

  /**
   * these two fields are of type int;
   * I represent the sign in a separate boolean field
   */
  public int year, month;

  /**
   *
   */
  public XsdGYearMonth(int year, int month) {
    this.year = year;
    this.month = month;
  }

  /**
   *
   */
  public XsdGYearMonth(boolean sign, int year, int month) {
    this.sign = sign;
    this.year = year;
    this.month = month;
  }

  /**
   * @param time a _fully_ specified XSD gYearMonth expression
   */
  public XsdGYearMonth(String time) {
    // get rid of "^^<xsd:date>" and leading & trailing '"' chars
    time = extractValue(time);
    // is there a sign ?
    if (time.charAt(0) == '+')
      time = time.substring(1);
    else if (time.charAt(0) == '-') {
      this.sign = false;
      time = time.substring(1);
    }
    // year
    int pos = time.indexOf('-');
    this.year = Integer.parseInt(time.substring(0, pos));
    // month, no time zones allowed
    this.month = Integer.parseInt(time.substring(pos + 1));
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
    sb.append("-");
    if (this.month >= 10)
      sb.append(this.month);
    else
      sb.append("0").append(this.month);
    return sb.append(tail).toString();
  }

  /**
   * even though there exist a java.util.Date and java.util.GregorianCalendar
   * class, these classes do not perfectly fit the intention behind XsdGYearMonth
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
    if (!(o instanceof XsdGYearMonth)) {
      throw new IllegalArgumentException("Can't compare " + this.getClass() + " and " + o.getClass());
    }
    XsdGYearMonth date = (XsdGYearMonth) o;

    int[] comp = new int[]{Integer.compare(transform(this.year), date.transform(date.year)), Integer.compare(transform(this.month), date.transform(date.month))};
    for (int r : comp) {
      if (r != 0)
        return r;
    }
    return 0;
  }

  public int transform(int v) {
    if (!this.sign) {
      return v * -1;
    }
    return v;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    XsdGYearMonth that = (XsdGYearMonth) o;
    return sign == that.sign &&
            year == that.year &&
            month == that.month;
  }

  @Override
  public int hashCode() {
    return Objects.hash(sign, year, month);
  }
}
