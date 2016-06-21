package de.dfki.lt.hfc.types;

/**
 * @author (C) Hans-Ulrich Krieger
 * @since JDK 1.5
 * @version Fri Jan 29 18:42:17 CET 2016
 */
public final class XsdBoolean extends XsdAnySimpleType {
  public final static String NAME = "boolean";

  public final static String SHORT_NAME = '<' + SHORT_PREFIX + NAME + '>';
  public final static String LONG_NAME = '<' + LONG_PREFIX + NAME + '>';

  static {
    registerConstructor(XsdBoolean.class, boolean.class, SHORT_NAME, LONG_NAME);
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
	 *   de.dfki.lt.hfc.SHORT_NAME
	 * or
	 *   de.dfki.lt.hfc.LONG_NAME
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
			sb.append(SHORT_NAME);
		else
			sb.append(LONG_NAME);
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
