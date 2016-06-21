package de.dfki.lt.hfc.types;

/**
 * an encoding of the XSD gYear format "[+|-]yyyy",
 * including negative years (= BC)
 *
 * @see http://www.w3.org/TR/xmlschema-2/
 * @see http://www.schemacentral.com/sc/xsd/t-xsd_gYear.html
 *
 * @author (C) Hans-Ulrich Krieger
 * @since JDK 1.5
 * @version Fri Jan 29 19:17:10 CET 2016
 */
public final class XsdGYear extends XsdAnySimpleType {
  public final static String NAME = "gYear";

  public final static String SHORT_NAME = '<' + SHORT_PREFIX + NAME + '>';
  public final static String LONG_NAME = '<' + LONG_PREFIX + NAME + '>';


  static {
    registerConstructor(XsdGYear.class, null, SHORT_NAME, LONG_NAME);
  }

	/**
	 * - = BC = false
	 * + = AD = true
	 * in case NO sign is specified, sign defaults to true
	 */
	public boolean sign = true;

	/**
	 * I represent the sign in a separate boolean field
	 */
	public int year;

	/**
	 *
	 */
	public XsdGYear(int year) {
		this.year = year;
	}

	/**
	 *
	 */
	public XsdGYear(boolean sign, int year) {
		this.sign = sign;
		this.year = year;
	}

	/**
	 * @param time an XSD gYear expression
	 */
	public XsdGYear(String time) {
		// get rid of "^^<xsd:gYear>" and leading & trailing '"' chars
		time = extractValue(time);
		// is there a sign ?
		if (time.charAt(0) == '+')
			time = time.substring(1);
		else if (time.charAt(0) == '-') {
			this.sign = false;
			time = time.substring(1);
		}
		this.year = Integer.parseInt(time.substring(0));
	}

	/**
	 * depending on shortIsDefault, either the suffix
	 *   de.dfki.lt.hfc.Namespace.SHORT_NAME
	 * or
	 *   de.dfki.lt.hfc.Namespace.LONG_NAME
	 * is used
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
   * class, these classes do not perfectly fit the intention behind XsdGMonthDay
   * so return this object
   */
  public Object toJava() {
    return this;
  }

}
