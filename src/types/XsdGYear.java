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
 * @version Tue Jan  3 12:53:44 CET 2012
 */
public final class XsdGYear extends XsdAnySimpleType {
	
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
		int index = time.lastIndexOf('^');
		time = time.substring(1, index - 2);
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
	 *   de.dfki.lt.hfc.Namespace.XSD_GYEAR_SHORT
	 * or
	 *   de.dfki.lt.hfc.Namespace.XSD_GYEAR_LONG
	 * is used
	 */
	public String toString(boolean shortIsDefault) {
		final String tail = "\"^^" + (shortIsDefault ? de.dfki.lt.hfc.Namespace.XSD_GYEAR_SHORT : de.dfki.lt.hfc.Namespace.XSD_GYEAR_LONG);
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
			sb.append(de.dfki.lt.hfc.Namespace.XSD_GYEAR_SHORT);
		else
			sb.append(de.dfki.lt.hfc.Namespace.XSD_GYEAR_LONG);
		return sb.toString();
	}
	
	/**
	 * generates a string representation from the internal fields, but omits
	 * the XSD type specification
	 */
	public String toName() {
		// get rid of "^^<xsd:gYear>"
		String time = toString(de.dfki.lt.hfc.Namespace.shortIsDefault);
		int index = time.lastIndexOf('^');
		return time.substring(1, index - 2);
	}
	
	/*
	 public static void main(String[] args) {
	 XsdGYear xt = new XsdGYear("\"2000\"^^<xsd:gYear>");
	 System.out.println(xt.year);
	 System.out.println(xt.toString(true));
	 System.out.println(xt.toString(false));
	 xt = new XsdGYear("\"-19999\"^^<xsd:gYear>");
	 System.out.println(xt.year);
	 System.out.println(xt.toString(true));
	 System.out.println(xt.toString(false));
	 xt = new XsdGYear(2009);
	 System.out.println(xt.toString(true));
	 System.out.println(xt.toName());
	 System.out.println(xt.toString(true));
	 System.out.println(xt.toString(false));
	 }
	 */
	
}
