package de.dfki.lt.hfc.types;

/**
 * an encoding of the XSD gMonthDay format "--MM-dd" _without_ time zones;
 * there are NO negative numbers here, although this might be useful to refer
 * to days in a month before year 0000 (BC period) -- this is currently not
 * provided by the XSD specification
 *
 * note: I do NOT check, e.g., whether month is between 1 and 12, or
 *       whether day is between 1 and 31, given that month is 8 (August),
 *       or that there is a 29th February in a leap year
 *
 * @see http://www.w3.org/TR/xmlschema-2/
 *
 * @author (C) Hans-Ulrich Krieger
 * @since JDK 1.5
 * @version Fri Jan 29 19:15:31 CET 2016
 */
public final class XsdGMonthDay extends XsdAnySimpleType {
	
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
		int index = time.lastIndexOf('^');
		time = time.substring(1, index - 2);
		// month
		this.month = Integer.parseInt(time.substring(2, 4));
		// day
		this.day = Integer.parseInt(time.substring(5, 7));
	}

	/**
	 * depending on shortIsDefault, either the suffix
	 *   de.dfki.lt.hfc.Namespace.XSD_GMONTHDAY_SHORT
	 * or
	 *   de.dfki.lt.hfc.Namespace.XSD_GMONTHDAY_LONG
	 * is used;
	 * note that toString() does NOT check whether the internal
	 * description is well-formed; e.g., we do not check whether
	 * 1 <= month <= 12 or whether 1 <= day <= 29 for month 02
	 * (= February)
	 */
	public String toString(boolean shortIsDefault) {
		final String tail = "\"^^" + (shortIsDefault ? de.dfki.lt.hfc.Namespace.XSD_GMONTHDAY_SHORT : de.dfki.lt.hfc.Namespace.XSD_GMONTHDAY_LONG);
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
	 * binary version is given the value directly
	 */
	public static String toString(String val, boolean shortIsDefault) {
		StringBuilder sb = new StringBuilder("\"");
		sb.append(val);
		sb.append("\"^^");
		if (shortIsDefault)
			sb.append(de.dfki.lt.hfc.Namespace.XSD_GMONTHDAY_SHORT);
		else
			sb.append(de.dfki.lt.hfc.Namespace.XSD_GMONTHDAY_LONG);
		return sb.toString();
	}
	
	/**
	 * generates a string representation from the internal fields, but omits
	 * the XSD type specification
	 */
	public String toName() {
		// get rid of "^^<xsd:gMonthDay>"
		String time = toString(de.dfki.lt.hfc.Namespace.shortIsDefault);
		int index = time.lastIndexOf('^');
		return time.substring(1, index - 2);
	}
  
  /**
   * even though there exist a java.util.Date and java.util.GregorianCalendar
   * class, these classes do not perfectly fit the intention behind XsdGMonthDay
   * so return this object
   */
  public Object toJava() {
    return this;
  }
	
	/**
   * for test purposes only
   */
	public static void main(String[] args) {
		XsdGMonthDay xt = new XsdGMonthDay("\"--03-04\"^^<xsd:gMonthDay>");
		System.out.println(xt.month);
		System.out.println(xt.day);
		System.out.println(xt.toString(true));
		System.out.println(xt.toString(false));
		System.out.println();
		xt = new XsdGMonthDay(1, 12);
		System.out.println(xt.toString(true));
		System.out.println(xt.toName());
		System.out.println(xt.toString(true));
		System.out.println(xt.toString(false));
	}
	
}
