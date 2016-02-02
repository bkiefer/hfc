package de.dfki.lt.hfc.types;

/**
 * an encoding of the XSD gYearMonth format "[+|-]yyyy-MM" _without_ time zones,
 * including negative years (= BC)
 *
 * @see http://www.w3.org/TR/xmlschema-2/
 * @see http://www.schemacentral.com/sc/xsd/t-xsd_gYearMonth.html
 *
 * @author (C) Hans-Ulrich Krieger
 * @since JDK 1.5
 * @version Fri Jan 29 19:20:53 CET 2016
 */
public final class XsdGYearMonth extends XsdAnySimpleType {
	
	/**
	 * - = BC = false
	 * + = AD = true
	 * in case NO sign is specified, sign defaults to true
	 */
	public boolean sign = true;
	
	/**
	 * these two fields are of type int;
	 * I represent the sign in a separate boolean field
	 */
	public int year, month;
	
	/**
	 *
	 */
	public XsdGYearMonth(int year, int month) {
		this.year = year;
		this.month = month;
	}
	
	/**
	 *
	 */
	public XsdGYearMonth(boolean sign, int year, int month) {
		this.sign = sign;
		this.year = year;
		this.month = month;
	}
	
	/**
	 * @param time a _fully_ specified XSD gYearMonth expression
	 */
	public XsdGYearMonth(String time) {
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
		// month, no time zones allowed
		this.month = Integer.parseInt(time.substring(pos + 1));
	}
	
	
	/**
	 * depending on shortIsDefault, either the suffix
	 *   de.dfki.lt.hfc.Namespace.XSD_GYEARMONTH_SHORT
	 * or
	 *   de.dfki.lt.hfc.Namespace.XSD_GYEARMONTH_LONG
	 * is used
	 */
	public String toString(boolean shortIsDefault) {
		final String tail = "\"^^" + (shortIsDefault ? de.dfki.lt.hfc.Namespace.XSD_GYEARMONTH_SHORT : de.dfki.lt.hfc.Namespace.XSD_GYEARMONTH_LONG);
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
			sb.append(de.dfki.lt.hfc.Namespace.XSD_GYEARMONTH_SHORT);
		else
			sb.append(de.dfki.lt.hfc.Namespace.XSD_GYEARMONTH_LONG);
		return sb.toString();
	}
	
	/**
	 * generates a string representation from the internal fields, but omits
	 * the XSD type specification
	 */
	public String toName() {
		// get rid of "^^<xsd:gYearMonth>"
		String time = toString(de.dfki.lt.hfc.Namespace.shortIsDefault);
		int index = time.lastIndexOf('^');
		return time.substring(1, index - 2);
	}
  
  /**
   * even though there exist a java.util.Date and java.util.GregorianCalendar
   * class, these classes do not perfectly fit the intention behind XsdGYearMonth
   * so return this object
   */
  public Object toJava() {
    return this;
  }
	
  /**
   * for test purposes only
   */
  public static void main(String[] args) {
    XsdGYearMonth xt = new XsdGYearMonth("\"-12000-03\"^^<xsd:gYearMonth>");
    System.out.println(xt.year);
    System.out.println(xt.month);
    System.out.println(xt.toString(true));
    System.out.println(xt.toString(false));
    System.out.println();
    xt = new XsdGYearMonth(2009, 1);
    System.out.println(xt.toString(true));
    System.out.println(xt.toName());
    System.out.println(xt.toString(true));
    System.out.println(xt.toString(false));
  }
	
}
