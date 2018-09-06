package de.dfki.lt.hfc.types;

import java.util.Objects;

/**
 * an encoding of the XSD gDay format "---DD" _without_ time zones;
 * there are NO negative numbers here, although this might be useful to refer
 * to days before year 0000 (BC period) -- this is currently not provided by
 * the XSD specification
 *
 * @author (C) Hans-Ulrich Krieger
 * @version Fri Jan 29 19:10:24 CET 2016
 * @see http://www.w3.org/TR/xmlschema-2/
 * @see http://www.schemacentral.com/sc/xsd/t-xsd_gDay.html
 * @since JDK 1.5
 */
public final class XsdGDay extends XsdAnySimpleType {
  public final static String NAME = "gDay";

  public final static String SHORT_NAME = '<' + NS.SHORT_NAMESPACE + ":" + NAME + '>';
  public final static String LONG_NAME = '<' + NS.LONG_NAMESPACE + NAME + '>';

  static {
    registerConstructor(XsdGDay.class, SHORT_NAME, LONG_NAME);
  }

  /**
   * I do NOT check whether 01 <= day <= 31
   */
  public int day;

  /**
   *
   */
  public XsdGDay(int day) {
    this.day = day;
  }

  /**
   * @param time a _fully_ specified XSD gDay expression
   */
  public XsdGDay(String time) {
    // get rid of "^^<xsd:gDay>" and leading & trailing '"' chars
    time = extractValue(time);
    // day is preceeded by three '-' chars
    this.day = Integer.parseInt(time.substring(3));
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
    StringBuilder sb = new StringBuilder("\"---");
    if (this.day >= 10)
      sb.append(this.day);
    else
      sb.append("0").append(this.day);
    return sb.append(tail).toString();
  }

  /**
   * even though there exist a java.util.Date and java.util.GregorianCalendar
   * class, these classes do not perfectly fit the intention behind XsdGDay
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
    if (!(o instanceof XsdGDay)) {
      throw new IllegalArgumentException("Can't compare " + this.getClass() + " and " + o.getClass());
    }
    return Integer.compare(this.day, ((XsdGDay) o).day);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    XsdGDay xsdGDay = (XsdGDay) o;
    return day == xsdGDay.day;
  }

  @Override
  public int hashCode() {
    return Objects.hash(day);
  }
}
