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
 * @version Tue Jan  3 13:33:32 CET 2012
 */
public final class XsdGDay extends XsdAnySimpleType {
	
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
		int index = time.lastIndexOf('^');
		time = time.substring(1, index - 2);
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
		final String tail = "\"^^" + (shortIsDefault ? de.dfki.lt.hfc.Namespace.XSD_GDAY_SHORT : de.dfki.lt.hfc.Namespace.XSD_GDAY_LONG);
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
			sb.append(de.dfki.lt.hfc.Namespace.XSD_GDAY_SHORT);
		else
			sb.append(de.dfki.lt.hfc.Namespace.XSD_GDAY_LONG);
		return sb.toString();
	}
	
	/**
	 * generates a string representation from the internal fields, but omits
	 * the XSD type specification
	 */
	public String toName() {
		// get rid of "^^<xsd:gDay>"
		String time = toString(de.dfki.lt.hfc.Namespace.shortIsDefault);
		int index = time.lastIndexOf('^');
		return time.substring(1, index - 2);
	}
	
	/*
	 public static void main(String[] args) {
	 XsdGDay xt = new XsdGDay("\"---13\"^^<xsd:gDay>");
	 System.out.println(xt.day);
	 System.out.println(xt.toString(true));
	 System.out.println(xt.toString(false));
	 System.out.println();
	 xt = new XsdGDay(1);
	 System.out.println(xt.toString(true));
	 System.out.println(xt.toName());
	 System.out.println(xt.toString(true));
	 System.out.println(xt.toString(false));
	 }
	 */
	
}