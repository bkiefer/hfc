package de.dfki.lt.hfc.types;

/**
 * an encoding of the XSD date format "[+|-]yyyy-MM-dd" including negative
 * years, but _without_ time zones;
 * if more ``precision'' is needed, use the XsdDateTime class
 *
 * note: I do NOT check, e.g., whether month is between 1 and 12, or
 *       whether day is between 1 and 31, given that month is 8 (August),
 *       or that there is a 29th February in a leap year
 *
 * @see XsdDateTime
 *
 * @see http://www.w3.org/TR/xmlschema-2/
 * @see http://www.schemacentral.com/sc/xsd/t-xsd_date.html
 *
 * @author (C) Hans-Ulrich Krieger
 * @since JDK 1.5
 * @version Fri Jan 29 19:01:44 CET 2016
 */
public final class XsdDate extends XsdAnySimpleType {

  static {
    registerConstructor(XsdDate.class, XSD_DATE_SHORT, XSD_DATE_LONG);
  }

	/**
	 * - = BC = false
	 * + = AD = true
	 * in case NO sign is specified, sign defaults to true
	 */
	public boolean sign = true;

	/**
	 * these fields are all of type int;
	 * I represent the sign in a separate boolean field
	 */
	public int year, month, day;

	/**
	 *
	 */
	public XsdDate(int year, int month, int day) {
		this.year = year;
		this.month = month;
		this.day = day;
	}

	/**
	 *
	 */
	public XsdDate(boolean sign, int year, int month, int day) {
		this.sign = sign;
		this.year = year;
		this.month = month;
		this.day = day;
	}

	/**
	 * @param time a _fully_ specified XSD date expression
	 */
	public XsdDate(String time) {
		// get rid of "^^<xsd:date>" and leading & trailing '"' chars
		int index = time.lastIndexOf('^');
		time = time.substring(1, index - 2);
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
		// month
		time = time.substring(pos + 1);
		pos = time.indexOf('-');
		this.month = Integer.parseInt(time.substring(0, pos));
		// day
		// the rest: NO time zones allowed
		this.day = Integer.parseInt(time.substring(pos + 1));
	}


	/**
	 * depending on shortIsDefault, either the suffix
	 *   de.dfki.lt.hfc.XSD_DATE_SHORT
	 * or
	 *   de.dfki.lt.hfc.XSD_DATE_LONG
	 * is used;
	 * note that toString() does NOT check whether the internal
	 * description is well-formed; e.g., we do not check whether
	 * 1 <= month <= 12 or whether 1 <= day <= 29 for month 02
	 * (= February)
	 */
	public String toString(boolean shortIsDefault) {
		StringBuilder sb = new StringBuilder("\"");
		if (! this.sign)
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
		sb.append("-");
		if (this.day >= 10)
			sb.append(this.day);
		else
			sb.append("0").append(this.day);
		sb.append("\"^^");
		sb.append(shortIsDefault ? XSD_DATE_SHORT : XSD_DATE_LONG);
		return sb.toString();
	}

	/**
	 * binary version is given the value directly
	 */
	public static String toString(String val, boolean shortIsDefault) {
		StringBuilder sb = new StringBuilder("\"");
		sb.append(val);
		sb.append("\"^^");
    sb.append(shortIsDefault ? XSD_DATE_SHORT : XSD_DATE_LONG);
		return sb.toString();
	}

  /**
   * even though there exist a java.util.Date and java.util.GregorianCalendar
   * class, these classes do not perfectly fit the intention behind XsdDate,
   * so return this object
   */
  public Object toJava() {
    return this;
  }

}
