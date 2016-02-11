package de.dfki.lt.hfc.types;

/**
 * an encoding of the XSD gDay format "---DD" _without_ time zones;
 * there are NO negative numbers here, although this might be useful to refer
 * to days before year 0000 (BC period) -- this is currently not provided by
 * the XSD specification
 *
 * @see http://www.w3.org/TR/xmlschema-2/
 * @see http://www.schemacentral.com/sc/xsd/t-xsd_gDay.html
 *
 * @author (C) Hans-Ulrich Krieger
 * @since JDK 1.5
 * @version Fri Jan 29 19:10:24 CET 2016
 */
public final class XsdGDay extends XsdAnySimpleType {

  static {
    registerConstructor(XsdGDay.class, XSD_GDAY_SHORT, XSD_GDAY_LONG);
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
	 * depending on shortIsDefault, either the suffix
	 *   de.dfki.lt.hfc.Namespace.XSD_GDAY_SHORT
	 * or
	 *   de.dfki.lt.hfc.Namespace.XSD_GDAY_LONG
	 * is used
	 */
	public String toString(boolean shortIsDefault) {
		final String tail = "\"^^" + (shortIsDefault ? XSD_GDAY_SHORT : XSD_GDAY_LONG);
		StringBuilder sb = new StringBuilder("\"---");
		if (this.day >= 10)
			sb.append(this.day);
		else
			sb.append("0").append(this.day);
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
			sb.append(XSD_GDAY_SHORT);
		else
			sb.append(XSD_GDAY_LONG);
		return sb.toString();
	}

  /**
   * even though there exist a java.util.Date and java.util.GregorianCalendar
   * class, these classes do not perfectly fit the intention behind XsdGDay
   * so return this object
   */
  public Object toJava() {
    return this;
  }

}
