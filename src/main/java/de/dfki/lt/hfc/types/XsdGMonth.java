package de.dfki.lt.hfc.types;

/**
 * an encoding of the XSD gMonth format "--MM" _without_ time zones;
 * there are NO negative numbers here, although this might be useful to refer
 * to month before year 0000 (BC period) -- this is currently not provided by
 * the XSD specification
 *
 * @see http://www.w3.org/TR/xmlschema-2/
 * @see http://www.schemacentral.com/sc/xsd/t-xsd_gMonth.html
 *
 * @author (C) Hans-Ulrich Krieger
 * @since JDK 1.5
 * @version Fri Jan 29 19:11:19 CET 2016
 */
public final class XsdGMonth extends XsdAnySimpleType {
  public final static String NAME = "gMonth";

  public final static String SHORT_NAME = '<' + SHORT_PREFIX + NAME + '>';
  public final static String LONG_NAME = '<' + LONG_PREFIX + NAME + '>';

  static {
    registerConstructor(XsdGMonth.class, null, SHORT_NAME, LONG_NAME);
  }

  /**
	 * I do NOT check whether 01 <= month <= 12
	 */
	public int month;

	/**
	 *
	 */
	public XsdGMonth(int month) {
		this.month = month;
	}

	/**
	 * @param time a _fully_ specified XSD gMonth expression
	 */
	public XsdGMonth(String time) {
		// get rid of "^^<xsd:gMonth>" and leading & trailing '"' chars
		time = extractValue(time);
		// month
		this.month = Integer.parseInt(time.substring(2));
	}

	/**
	 * depending on shortIsDefault, either the suffix
	 *   SHORT_NAME
	 * or
	 *   LONG_NAME
	 * is used
	 */
	public String toString(boolean shortIsDefault) {
		final String tail = "\"^^" + (shortIsDefault ? SHORT_NAME : LONG_NAME);
		StringBuilder sb = new StringBuilder("\"--");
		if (this.month >= 10)
			sb.append(this.month);
		else
			sb.append("0").append(this.month);
		return sb.append(tail).toString();
	}

	/**
	 * binary version is given the value directly
	 */
	public static String toString(String val, boolean shortIsDefault) {
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
   * even though there exist a java.util.Date and java.util.GregorianCalendar
   * class, these classes do not perfectly fit the intention behind XsdGMonth
   * so return this object
   */
  public Object toJava() {
    return this;
  }

}
