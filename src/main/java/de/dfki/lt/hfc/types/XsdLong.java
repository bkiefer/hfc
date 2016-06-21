package de.dfki.lt.hfc.types;

/**
 * note: currently XsdLong is *not* a superclass of (derived from) XsdInt,
 * as the XSD type tree indicates!
 * @see http://www.w3.org/TR/xmlschema11-2/
 *
 * @author (C) Hans-Ulrich Krieger
 * @since JDK 1.5
 * @version Fri Jan 29 19:32:15 CET 2016
 */
public final class XsdLong extends XsdAnySimpleType {
  public final static String NAME = "long";

  public final static String SHORT_NAME = '<' + SHORT_PREFIX + NAME + '>';
  public final static String LONG_NAME = '<' + LONG_PREFIX + NAME + '>';

  static {
    registerConstructor(XsdLong.class, long.class, SHORT_NAME, LONG_NAME);
  }

	public long value;

	/**
	 * @param value a Java long representation of an XSD long
	 */
	public XsdLong(long value) {
		this.value = value;
	}

	/**
	 * @param value a string, representing an XSD long, e.g., "\"1272539480080\"^^<xsd:long>"
	 */
	public XsdLong(String value) {
		// get rid of "^^xsd:long" and leading & trailing '"' chars
		this.value = Long.parseLong(extractValue(value));
	}

	/**
	 * depending on shortIsDefault, either the suffix
	 *   de.dfki.lt.hfc.Namespace.SHORT_NAME
	 * or
	 *   de.dfki.lt.hfc.Namespace.LONG_NAME
	 * is used
	 */
	public String toString(boolean shortIsDefault) {
		return toString(this.value, shortIsDefault);
	}

	/**
	 * binary version is given the value directly
	 */
	public static String toString(long val, boolean shortIsDefault) {
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
	 * turn long value into a string
	 */
	public String toName() {
		return Long.toString(this.value);
	}

  /**
   * returns a java.lang.Long container for an HFC XsdLong object
   */
  public Object toJava() {
    return this.value;
  }

}