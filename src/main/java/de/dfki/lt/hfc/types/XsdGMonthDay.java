package de.dfki.lt.hfc.types;

import java.util.Objects;

/**
 * an encoding of the XSD gMonthDay format "--MM-dd" _without_ time zones;
 * there are NO negative numbers here, although this might be useful to refer
 * to days in a month before year 0000 (BC period) -- this is currently not
 * provided by the XSD specification
 * <p>
 * note: I do NOT check, e.g., whether month is between 1 and 12, or
 * whether day is between 1 and 31, given that month is 8 (August),
 * or that there is a 29th February in a leap year
 *
 * @author (C) Hans-Ulrich Krieger
 * @version Fri Jan 29 19:15:31 CET 2016
 * @see http://www.w3.org/TR/xmlschema-2/
 * @since JDK 1.5
 */
public final class XsdGMonthDay extends XsdAnySimpleType {
  public final static String NAME = "gMonthDay";

  public final static String SHORT_NAME = '<' + NS.SHORT_NAMESPACE + ":" + NAME + '>';
  public final static String LONG_NAME = '<' + NS.LONG_NAMESPACE + NAME + '>';


  static {
    registerConstructor(XsdGMonthDay.class,
            null, SHORT_NAME, LONG_NAME);
  }

  /**
   * these fields are all of type int
   */
  public int month, day;

  /**
   *
   */
  public XsdGMonthDay(int month, int day) {
    this.month = month;
    this.day = day;
  }

  /**
   * @param time a _fully_ specified XSD gMonthDay expression
   */
  public XsdGMonthDay(String time) {
    // get rid of "^^<xsd:gMonthDay>" and leading & trailing '"' chars
    time = extractValue(time);
    // month
    this.month = Integer.parseInt(time.substring(2, 4));
    // day
    this.day = Integer.parseInt(time.substring(5, 7));
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
   * SHORT_NAME
   * or
   * LONG_NAME
   * is used;
   * note that toString() does NOT check whether the internal
   * description is well-formed; e.g., we do not check whether
   * 1 <= month <= 12 or whether 1 <= day <= 29 for month 02
   * (= February)
   */
  public String toString() {
    final String tail = "\"^^" + (NS.isShort() ? SHORT_NAME : LONG_NAME);
    StringBuilder sb = new StringBuilder("\"--");
    if (this.month >= 10)
      sb.append(this.month);
    else
      sb.append("0").append(this.month);
    sb.append("-");
    if (this.day >= 10)
      sb.append(this.day);
    else
      sb.append("0").append(this.day);
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
    if (!(o instanceof XsdGMonthDay)) {
      throw new IllegalArgumentException("Can't compare " + this.getClass() + " and " + o.getClass());
    }
    XsdGMonthDay date = (XsdGMonthDay) o;
    int[] comp = new int[]{Integer.compare(this.month, date.month), Integer.compare(this.day, date.day)};
    for (int r : comp) {
      if (r != 0)
        return r;
    }
    return 0;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    XsdGMonthDay that = (XsdGMonthDay) o;
    return month == that.month &&
            day == that.day;
  }

  @Override
  public int hashCode() {
    return Objects.hash(month, day);
  }
}
