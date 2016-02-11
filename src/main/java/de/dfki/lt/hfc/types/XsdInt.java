package de.dfki.lt.hfc.types;

/**
 * @author (C) Hans-Ulrich Krieger
 * @since JDK 1.5
 * @version Fri Jan 29 19:30:23 CET 2016
 */
public final class XsdInt extends XsdAnySimpleType {

  static {
    registerConstructor(XsdInt.class, XSD_INT_SHORT, XSD_INT_LONG);
  }

	public int value;

	/**
	 * @param value a Java int representation of an XSD int
	 */
	public XsdInt(int value) {
		this.value = value;
	}

	/**
	 * @param value a string, representing an XSD int, e.g., "\"42\"^^<xsd:int>"
	 */
	public XsdInt(String value) {
		// get rid of "^^xsd:int" and leading & trailing '"' chars
	  this.value = Integer.parseInt(extractValue(value));
	}

	/**
	 * depending on shortIsDefault, either the suffix
	 *   de.dfki.lt.hfc.Namespace.XSD_INT_SHORT
	 * or
	 *   de.dfki.lt.hfc.Namespace.XSD_INT_LONG
	 * is used
	 */
	public String toString(boolean shortIsDefault) {
		return toString(this.value, shortIsDefault);
	}

	/**
	 * binary version is given the value directly
	 */
	public static String toString(int val, boolean shortIsDefault) {
		StringBuilder sb = new StringBuilder("\"");
		sb.append(val);
		sb.append("\"^^");
		if (shortIsDefault)
			sb.append(XSD_INT_SHORT);
		else
			sb.append(XSD_INT_LONG);
		return sb.toString();
	}

	/**
	 * turn int value into a string
	 */
	public String toName() {
		return Integer.toString(this.value);
	}

  /**
   * returns a java.lang.Integer container for an HFC XsdInt object
   */
  public Object toJava() {
    return this.value;
  }

}
