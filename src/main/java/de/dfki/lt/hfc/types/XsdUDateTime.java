package de.dfki.lt.hfc.types;

/**
 * an extension of the XSD dateTime format "yyyy-MM-dd'T'HH:mm:ss.SSS"
 * _without_ time zones;
 * years larger than 9999 and BC years will NOT be handled!
 *
 * note: I do NOT check, e.g., whether month is between 1 and 12, or
 *       whether day is between 1 and 31, given that month is 8 (August),
 *       or that there is a 29th February in a leap year
 *
 * note: this implementation even permits to underspecify year, month,
 *       day, hour, minute, and/or second by using the question mark '?',
 *       as for instance in "\"2000-??-04T??:??:??.???\"^^<xsd:uDateTime>";
 *       internally, this information is represented through integer '-1'
 * note: we also allow to shorten a dateTime description, i.e., avoiding
 *       to write useless '?' characters; this gives us the flexibility
 *       to distinguish between underspecified and unspecified parts
 *
 * note: if the values for year, month, day, hour, minute, and second are
 *       known in advance, the standard type/class dateTime/XsdDateTime
 *       should be used instead
 *
 * @see XsdDateTime
 * @see UNDERSPECIFIED
 * @see UNSPECIFIED
 * @see isUnderspecified()
 * @see isUnspecified()
 *
 * @see http://www.w3.org/TR/xmlschema-2/
 *
 * @author (C) Hans-Ulrich Krieger
 * @since JDK 1.5
 * @version Wed Jun 22 13:53:35 CEST 2011
 */
public final class XsdUDateTime extends XsdAnySimpleType {
	
	/**
	 * these fields are all of type int
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
	public static final int UNDERSPECIFIED = -1;
	
	/**
	 *
	 */
	public static final int UNSPECIFIED = -2;
	
	/**
	 * a value equals to XsdUDateTime.UNDERSPECIFIED indicates that the information is
	 * (currently) not known;
	 * example: "On New Year's eve, I have visited the Eiffel Tower"
	 *          --> "????-12-31T??:??:??"
	 * a value equals to XsdUDateTime.UNSPECIFIED indicates that a specific field and
	 * all "smaller" fields following are not of interets to the representation;
	 * example 1: "31st July is end of business year"
	 *          --> "????-07-31" -- combination of underspecification and unspecification
	 * example 2: "Obama was born in 1961" --> "1961"
	 * when using toString(), these values are replaced by the '?' character;
	 * @param value a Java int representation of an XSD int
	 */
	public XsdUDateTime(int year, int month, int day, int hour, int minute, float second) {
		this.year = year;
		this.month = month;
		this.day = day;
		this.hour = hour;
		this.minute = minute;
		this.second = second;
	}
	
	/**
	 * note that '?' characters indicate that a specific field is underspecified;
	 * as a further extension, XsdUDateTime objects need not be specified down to
	 * milliseconds, so that a certain description can be "cut" off, e.g.,
	 *   2011          // at least, there should be a year, even if it is ...
	 *   ????          // ... underspecified
	 *   2011-         // this is WRONG due to the use of '-' !!!!!!!
	 *   2011-12
	 *   ????-12
	 *   2011-12-31
	 *   2011-12-31T   // this is WRONG -- no 'T' whitout further info !!!!!!!
	 *   ????-12-??T05 // example: "In December, Pete rang me up at 5 in the morning"
	 * @param time a string, representing an XSD uDateTime instant, e.g.,
	 *        "\"????-12-31\"^^<xsd:uDateTime>"
	 *        "\"1961\"^^<xsd:uDateTime>"
	 */
	public XsdUDateTime(String time) {
		// get rid of "^^<xsd:uDateTime>" and leading & trailing '"' chars
		int index = time.lastIndexOf('^');
		time = time.substring(1, index - 2);
		int length = time.length();
		String field;
		field = time.substring(0, 4);
		if (field.equals("????"))
			this.year = XsdUDateTime.UNDERSPECIFIED;
		else
			this.year = Integer.parseInt(field);
		// further info ?
		if (length < 5) {
			this.month = XsdUDateTime.UNSPECIFIED;
			this.day = XsdUDateTime.UNSPECIFIED;
			this.hour = XsdUDateTime.UNSPECIFIED;
			this.minute = XsdUDateTime.UNSPECIFIED;
			this.second = XsdUDateTime.UNSPECIFIED;
			return;
		}
		field = time.substring(5, 7);
		if (field.equals("??"))
			this.month = XsdUDateTime.UNDERSPECIFIED;
		else
			this.month = Integer.parseInt(field);
		if (length < 8) {
			this.day = XsdUDateTime.UNSPECIFIED;
			this.hour = XsdUDateTime.UNSPECIFIED;
			this.minute = XsdUDateTime.UNSPECIFIED;
			this.second = XsdUDateTime.UNSPECIFIED;
			return;
		}
		field = time.substring(8, 10);
		if (field.equals("??"))
			this.day = XsdUDateTime.UNDERSPECIFIED;
		else
			this.day = Integer.parseInt(field);
		if (length < 11) {
			this.hour = XsdUDateTime.UNSPECIFIED;
			this.minute = XsdUDateTime.UNSPECIFIED;
			this.second = XsdUDateTime.UNSPECIFIED;
			return;
		}
		field = time.substring(11, 13);
		if (field.equals("??"))
			this.hour = XsdUDateTime.UNDERSPECIFIED;
		else
			this.hour = Integer.parseInt(field);
		if (length < 14) {
			this.minute = XsdUDateTime.UNSPECIFIED;
			this.second = XsdUDateTime.UNSPECIFIED;
			return;
		}		
		field = time.substring(14, 16);
		if (field.equals("??"))
			this.minute = XsdUDateTime.UNDERSPECIFIED;
		else
			this.minute = Integer.parseInt(field);
		if (length < 17) {
			this.second = XsdUDateTime.UNSPECIFIED;
			return;
		}
		field = time.substring(17);
		if (field.startsWith("?"))
			this.second = XsdUDateTime.UNDERSPECIFIED;
		else
			this.second = Float.parseFloat(field);
	}
	
	/**
	 * @return true iff parameter field is underspecified,
	 * internally represented by value -1;
	 * @return false, otherwise
	 */
	public static boolean isUnderspecified(int field) {
		return (field == -1);
	}
	
	public static boolean isUnderspecified(float field) {
		return (field == -1.0);
	}
	
	/**
	 * @return true iff parameter field is unspecified,
	 * internally represented by value -2;
	 * @return false, otherwise
	 */
	public static boolean isUnspecified(int field) {
		return (field == -2);
	}
	
	public static boolean isUnspecified(float field) {
		return (field == -2.0);
	}
	
	/**
	 * depending on shortIsDefault, either the suffix
	 *   de.dfki.lt.hfc.Namespace.XSD_UDATETIME_SHORT
	 * or
	 *   de.dfki.lt.hfc.Namespace.XSD_UDATETIME_LONG
	 * is used;
	 * note that toString() does NOT check whether the internal
	 * description is well-formed; e.g., we do not check whether
	 * 1 <= month <= 12 or whether 1 <= day <= 29 for month 02
	 * (= February)
	 */
	public String toString(boolean shortIsDefault) {
		final String tail = "\"^^" + (shortIsDefault ? de.dfki.lt.hfc.Namespace.XSD_UDATETIME_SHORT : de.dfki.lt.hfc.Namespace.XSD_UDATETIME_LONG);
		String field;
		StringBuilder sb = new StringBuilder("\"");
		if (this.year >= 1000)
			sb.append(this.year);
		else if (this.year == XsdUDateTime.UNDERSPECIFIED)
			sb.append("????");
		else if (this.year >= 100)
			sb.append("0").append(this.year);
		else if (this.year >= 10)
			sb.append("00").append(this.year);
		else
			sb.append("000").append(this.year);
		// rest of temporal information might be _un_specified
		if (this.month == XsdUDateTime.UNSPECIFIED) {
			return sb.append(tail).toString();
		}
		sb.append("-");
		if (this.month >= 10)
			sb.append(this.month);
		else if (this.month == XsdUDateTime.UNDERSPECIFIED)
			sb.append("??");
		else
			sb.append("0").append(this.month);
		// rest of temporal information might be _un_specified
		if (this.day == XsdUDateTime.UNSPECIFIED) {
			return sb.append(tail).toString();
		}
		sb.append("-");
		if (this.day >= 10)
			sb.append(this.day);
		else if (this.day == XsdUDateTime.UNDERSPECIFIED)
			sb.append("??");
		else
			sb.append("0").append(this.day);
		// rest of temporal information might be _un_specified
		if (this.hour == XsdUDateTime.UNSPECIFIED) {
			return sb.append(tail).toString();
		}
		sb.append("T");
		if (this.hour >= 10)
			sb.append(this.hour);
		else if (this.hour == XsdUDateTime.UNDERSPECIFIED)
			sb.append("??");
		else
			sb.append("0").append(this.hour);
		// rest of temporal information might be _un_specified
		if (this.minute == XsdUDateTime.UNSPECIFIED) {
			return sb.append(tail).toString();
		}
		sb.append(":");
		if (this.minute >= 10)
			sb.append(this.minute);
		else if (this.minute == XsdUDateTime.UNDERSPECIFIED)
			sb.append("??");
		else
			sb.append("0").append(this.minute);
		// rest of temporal information might be _un_specified
		if (this.second == XsdUDateTime.UNSPECIFIED) {
			return sb.append(tail).toString();
		}
		sb.append(":");
		if (this.second >= 10)
			sb.append(this.second);
		else if (this.second == XsdUDateTime.UNDERSPECIFIED)
			sb.append("??.???");
		else
			sb.append("0").append(this.second);
		// final return statement in case everything is specified
		// (even underspecified) down to seconds
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
			sb.append(de.dfki.lt.hfc.Namespace.XSD_UDATETIME_SHORT);
		else
			sb.append(de.dfki.lt.hfc.Namespace.XSD_UDATETIME_LONG);
		return sb.toString();
	}
	
	/**
	 * generates a string representation from the internal fields, but omits
	 * the XSD type specification
	 */
	public String toName() {
		// get rid of "^^<xsd:uDateTime>"
		String time = toString(de.dfki.lt.hfc.Namespace.shortIsDefault);
		int index = time.lastIndexOf('^');
		return time.substring(1, index - 2);
	}

	/*
	 public static void main(String[] args) {
	 // an instance within the fourth day of a month in 2000
	 XsdUDateTime xt = new XsdUDateTime("\"2000-??-04T??:??:??.???\"^^<xsd:uDateTime>");
	 System.out.println(xt.hour);
	 System.out.println(xt.second);
	 System.out.println(xt.toString(true));
	 System.out.println(xt.toString(false));
	 System.out.println();
	 // an instance within the 12th Jan 2009
	 xt = new XsdUDateTime(2009, 1, 12, -1, -1, -1F);
	 System.out.println(xt.toString(true));
	 System.out.println(xt.toString(false));
	 }
	 */
	
}