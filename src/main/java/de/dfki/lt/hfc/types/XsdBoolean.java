package de.dfki.lt.hfc.types;

/**
 * @author (C) Hans-Ulrich Krieger
 * @since JDK 1.5
 * @version Fri Jan 29 18:42:17 CET 2016
 */
public final class XsdBoolean extends XsdAnySimpleType {

  static {
    registerConstructor(XsdBoolean.class, XSD_BOOLEAN_SHORT, XSD_BOOLEAN_LONG);
  }

  public boolean value;

	/**
	 * @param value a Java boolean representation of an XSD boolean
	 */
	public XsdBoolean(boolean value) {
		this.value = value;
	}

	/**
	 * @param value a Java string, representing an XSD boolean, e.g., "\"true\"^^<xsd:boolean>"
	 */
	public XsdBoolean(String value) {
		// get rid of "^^xsd:boolean" and leading & trailing '"' chars
		this.value = Boolean.parseBoolean(extractValue(value));
	}

	/**
	 * depending on shortIsDefault, either the suffix
	 *   de.dfki.lt.hfc.XSD_BOOLEAN_SHORT
	 * or
	 *   de.dfki.lt.hfc.XSD_BOOLEAN_LONG
	 * is used
	 */
	public String toString(boolean shortIsDefault) {
	  return toString(this.value, shortIsDefault);
	}

	/**
	 * binary version is given the value directly
	 */
	public static String toString(boolean val, boolean shortIsDefault) {
		StringBuilder sb = new StringBuilder("\"");
		sb.append(val);
		sb.append("\"^^");
		if (shortIsDefault)
			sb.append(XSD_BOOLEAN_SHORT);
		else
			sb.append(XSD_BOOLEAN_LONG);
		return sb.toString();
	}

	/**
	 * turn Boolean value into a string
	 */
	public String toName() {
		return Boolean.toString(this.value);
	}

  /**
   * returns a java.lang.Boolean container for an HFC XsdBoolean object
   */
  public Object toJava() {
    return new Boolean(this.value);
  }

}
