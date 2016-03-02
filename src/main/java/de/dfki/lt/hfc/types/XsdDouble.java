package de.dfki.lt.hfc.types;

/**
 * The double datatype is patterned after the IEEE double-precision
 * 64-bit floating point type [IEEE 754-1985]
 *
 * @author (C) Hans-Ulrich Krieger
 * @since JDK 1.5
 * @version Fri Jan 29 19:28:31 CET 2016
 */
public final class XsdDouble extends XsdAnySimpleType {
  
  public final static String NAME = "double";

  public final static String SHORT_NAME = '<' + SHORT_PREFIX + NAME + '>';
  public final static String LONG_NAME = '<' + LONG_PREFIX + NAME + '>';

  static {
    registerConstructor(XsdDouble.class, SHORT_NAME, LONG_NAME);
  }

	public double value;

	/**
	 * @param value a Java double representation of an XSD double
	 */
	public XsdDouble(double value) {
		this.value = value;
	}

	/**
	 * @param value a string, representing an XSD double, e.g., "\"2.71828\"^^<xsd:double>"
	 */
	public XsdDouble(String value) {
		this.value = Double.parseDouble(extractValue(value));
	}

	/**
	 * depending on shortIsDefault, either the suffix SHORT_NAME or LONG_NAME is used
	 */
	public String toString(boolean shortIsDefault) {
		return toString(this.value, shortIsDefault);
	}

	/**
	 * binary version is given the value directly
	 */
	public static String toString(double val, boolean shortIsDefault) {
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
	 * turn double value into a string
	 */
	public String toName() {
		return Double.toString(this.value);
	}

  /**
   * returns a java.lang.Double container for an HFC XsdDouble object
   */
  public Object toJava() {
    return this.value;
  }

}
