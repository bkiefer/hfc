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
		int index = time.lastIndexOf('^');
		time = time.substring(1, index - 2);
		// month
		this.month = Integer.parseInt(time.substring(2));
	}
	
	/**
	 * depending on shortIsDefault, either the suffix
	 *   de.dfki.lt.hfc.Namespace.XSD_GMONTH_SHORT
	 * or
	 *   de.dfki.lt.hfc.Namespace.XSD_GMONTH_LONG
	 * is used
	 */
	public String toString(boolean shortIsDefault) {
		final String tail = "\"^^" + (shortIsDefault ? de.dfki.lt.hfc.Namespace.XSD_GMONTH_SHORT : de.dfki.lt.hfc.Namespace.XSD_GMONTH_LONG);
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
			sb.append(de.dfki.lt.hfc.Namespace.XSD_GMONTH_SHORT);
		else
			sb.append(de.dfki.lt.hfc.Namespace.XSD_GMONTH_LONG);
		return sb.toString();
	}
	
	/**
	 * generates a string representation from the internal fields, but omits
	 * the XSD type specification
	 */
	public String toName() {
		// get rid of "^^<xsd:gMonth>"
		String time = toString(de.dfki.lt.hfc.Namespace.shortIsDefault);
		int index = time.lastIndexOf('^');
		return time.substring(1, index - 2);
	}
  
  /**
   * even though there exist a java.util.Date and java.util.GregorianCalendar
   * class, these classes do not perfectly fit the intention behind XsdGMonth
   * so return this object
   */
  public Object toJava() {
    return this;
  }
	
  /**
   * for test purposes only
   */
  public static void main(String[] args) {
    XsdGMonth xt = new XsdGMonth("\"--03\"^^<xsd:gMonth>");
    System.out.println(xt.month);
    System.out.println(xt.toString(true));
    System.out.println(xt.toString(false));
    System.out.println();
    xt = new XsdGMonth(1);
    System.out.println(xt.toString(true));
    System.out.println(xt.toName());
    System.out.println(xt.toString(true));
    System.out.println(xt.toString(false));
  }
	
}
