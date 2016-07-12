package de.dfki.lt.hfc.types;

/**
 * an encoding of the XSD dateTime format "[+|-]yyyy-MM-dd'T'HH:mm:ss.SSS"
 * _without_ time zones;
 * if less ``precision'' is needed, use the XsdDate class
 *
 * note: I do NOT check, e.g., whether month is between 1 and 12, or
 *       whether day is between 1 and 31, given that month is 8 (August),
 *       or that there is a 29th February in a leap year
 *
 * note: the class XsdUDateTime even provides un(der)specified time
 *
 * @see XsdUnDateTime
 *
 * @see http://www.w3.org/TR/xmlschema-2/
 * @see http://www.schemacentral.com/sc/xsd/t-xsd_dateTime.html
 *
 * @author (C) Hans-Ulrich Krieger
 * @since JDK 1.5
 * @version Fri Jan 29 19:02:02 CET 2016
 */
public final class XsdDateTime extends XsdAnySimpleType {
  public final static String NAME = "dateTime";

  public final static String SHORT_NAME = '<' + SHORT_PREFIX + NAME + '>';
  public final static String LONG_NAME = '<' + LONG_PREFIX + NAME + '>';

  static {
    registerConstructor(XsdDateTime.class, SHORT_NAME, LONG_NAME);
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
	public int year, month, day, hour, minute;

	/**
	 * second is the only field that is of type float in order to
	 * represent parts of a second
	 */
	public float second;

	/**
	 *
	 */
	public XsdDateTime(int year, int month, int day, int hour, int minute, float second) {
		this.year = year;
		this.month = month;
		this.day = day;
		this.hour = hour;
		this.minute = minute;
		this.second = second;
	}

	/**
	 *
	 */
	public XsdDateTime(boolean sign, int year, int month, int day, int hour, int minute, float second) {
		this.sign = sign;
		this.year = year;
		this.month = month;
		this.day = day;
		this.hour = hour;
		this.minute = minute;
		this.second = second;
	}

	/**
	 * @param time a _fully_ specified XSD dateTime expression
	 */
	public XsdDateTime(String time) {
		// get rid of "^^<xsd:dateTime>" and leading & trailing '"' chars
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
		time = time.substring(pos + 1);
		pos = time.indexOf('T');
		this.day = Integer.parseInt(time.substring(0, pos));
		// hour
		time = time.substring(pos + 1);
		pos = time.indexOf(':');
		this.hour = Integer.parseInt(time.substring(0, pos));
		// minute
		time = time.substring(pos + 1);
		pos = time.indexOf(':');
		this.minute = Integer.parseInt(time.substring(0, pos));
		// second
		this.second = Float.parseFloat(time.substring(pos + 1));
	}

	/**
	 * depending on shortIsDefault, either the suffix
	 *   de.dfki.lt.hfc.SHORT_NAME
	 * or
	 *   de.dfki.lt.hfc.LONG_NAME
	 * is used;
	 * note that toString() does NOT check whether the internal
	 * description is well-formed; e.g., we do not check whether
	 * 1 <= month <= 12 or whether 1 <= day <= 29 for month 02
	 * (= February)
	 */
	public String toString(boolean shortIsDefault) {
		final String tail = "\"^^" + (shortIsDefault ? SHORT_NAME : LONG_NAME);
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
		sb.append("T");
		if (this.hour >= 10)
			sb.append(this.hour);
		else
			sb.append("0").append(this.hour);
		sb.append(":");
		if (this.minute >= 10)
			sb.append(this.minute);
		else
			sb.append("0").append(this.minute);
		sb.append(":");
		if (this.second >= 10)
			sb.append(this.second);
		else
			sb.append("0").append(this.second);
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
	 * generates a string representation from the internal fields, but omits
	 * the XSD type specification
	 */
	public String toName() {
		// get rid of "^^<xsd:dateTime>"
		String time = toString(true);  // it doesn't matter here whether long or short form is chosen
		int index = time.lastIndexOf('^');
		return time.substring(1, index - 2);
	}

  /**
   * even though there exist a java.util.Date and java.util.GregorianCalendar
   * class, these classes do not perfectly fit the intention behind XsdDateTime,
   * so return this object
   */
  public Object toJava() {
    return this;
  }

}
