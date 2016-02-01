package de.dfki.lt.hfc.types;

/**
 * an encoding of the currently NOT existing XSD type monetary
 *
 * note: I do NOT check whether the currency abbreviation (three uppercase letters) is
 *       in fact a legal currency and whether the monetary expression is syntactically
 *       correct;
 *
 * syntax: <monetary> ::= "<amount><currency>"
 *         <amount>   ::= any (positive/negative) (floating-point) number
 *         <currency> ::+ a three uppercase letter code, as defined by ISO 4217
 * note: <amount> and <currency> are written together -- there is NO space left between them
 *
 * examples:
 *   + a value of 42 US Dollars would be represented as: "42USD"^^<xsd:monetary>
 *   + a value of 4.37 Euro would be represented as: "4.37EUR"^^<xsd:monetary>
 *
 * @see ISO 4217 ISO Standards for Currency Names, International Organization for Standardization (ISO), 1999.
 * @see http://en.wikipedia.org/wiki/ISO_4217
 * @see http://www.w3.org/TR/2001/WD-xforms-20010608/slice4.html#dt-currency
 *
 * @author (C) Hans-Ulrich Krieger
 * @since JDK 1.5
 * @version Fri Jan 29 19:35:14 CET 2016
 */
public final class XsdMonetary extends XsdAnySimpleType {
	
	/**
	 * some useful constants
	 */
	public static final String USD = "USD";  // US Dollar

	public static final String CNY = "CNY";  // Chinese Yuan

	public static final String JPY = "JPY";  // Japanese Yen
	
	public static final String EUR = "EUR";  // European Euro
	
	public static final String GBP = "GBP";  // British Pound Sterling

	public static final String RUB = "RUB";  // Russian Rouble
	
	/**
	 * the amount part of monetary
	 */
	public double amount;
	
	/**
	 * the currency part of monetary
	 */
	public String currency;
	
	/**
	 *
	 */
	public XsdMonetary(double amount, String currency) {
		this.amount = amount;
		this.currency = currency;
	}
	
	/**
	 * @param monetary a _fully_ specified monetary expression
	 */
	public XsdMonetary(String monetary) {
		// get rid of "^^<xsd:Monetary>" and leading & trailing '"' chars
		final int index = monetary.lastIndexOf('^');
		monetary = monetary.substring(1, index - 2);
		final int length = monetary.length();
		// amount
		this.amount = Double.parseDouble(monetary.substring(0, length - 3));
		// currency
		this.currency = monetary.substring(length - 3);
	}
	
	/**
	 * depending on shortIsDefault, either the suffix
	 *   de.dfki.lt.hfc.Namespace.XSD_MONETARY_SHORT
	 * or
	 *   de.dfki.lt.hfc.Namespace.XSD_MONETARY_LONG
	 * is used;
	 * note that toString() does NOT check whether the internal
	 * description is well-formed; e.g., we do not check whether
	 * this.currency is a legal currency (three uppercase letters)
	 */
	public String toString(boolean shortIsDefault) {
		StringBuilder sb = new StringBuilder("\"");
		sb.append(this.amount);
		sb.append(this.currency);
		sb.append("\"^^");
		if (shortIsDefault)
			sb.append(de.dfki.lt.hfc.Namespace.XSD_MONETARY_SHORT);
		else
			sb.append(de.dfki.lt.hfc.Namespace.XSD_MONETARY_LONG);
		return sb.toString();
	}
	
	/**
	 * binary version is given the value directly
	 */
	public static String toString(String val, boolean shortIsDefault) {
		StringBuilder sb = new StringBuilder("\"");
		sb.append(val);
		sb.append("\"^^");
		if (shortIsDefault)
			sb.append(de.dfki.lt.hfc.Namespace.XSD_MONETARY_SHORT);
		else
			sb.append(de.dfki.lt.hfc.Namespace.XSD_MONETARY_LONG);
		return sb.toString();
	}
	
	/**
	 * generates a string representation from the internal fields, but omits
	 * the XSD type specification
	 */
	public String toName() {
		return Double.toString(this.amount) + this.currency;
	}
	
  /**
   * there exists no Java counterpart, so return this object
   */
  public Object toJava() {
    return this;
  }
  
	/**
   * for test purposes only
   */
  public static void main(String[] args) {
    XsdMonetary mon = new XsdMonetary("\"42USD\"^^<xsd:monetary>");
    System.out.println(mon.amount);
    System.out.println(mon.currency);
    System.out.println(mon.toString(true));
    System.out.println(mon.toString(false));
    System.out.println();
    mon = new XsdMonetary(4.31, "EUR");
    System.out.println(mon.toString(true));
    System.out.println(mon.toName());
    System.out.println(mon.toString(true));
    System.out.println(mon.toString(false));
  }

}
