package de.dfki.lt.hfc.types;

/**
 * The double datatype is patterned after the IEEE double-precision 64-bit floating point type [IEEE 754-1985]
 * @author (C) Hans-Ulrich Krieger
 * @since JDK 1.5
 * @version Fri Jan 29 19:28:31 CET 2016
 */
public final class XsdDouble extends XsdAnySimpleType {
  static {
    registerConstructor(XsdDouble.class, XSD_DOUBLE_SHORT, XSD_DOUBLE_LONG);
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
		// get rid of "^^xsd:double" and leading & trailing '"' chars
		this.value = Double.parseDouble(extractValue(value));
	}

	/**
	 * depending on shortIsDefault, either the suffix
	 *   de.dfki.lt.hfc.Namespace.XSD_DOUBLE_SHORT
	 * or
	 *   de.dfki.lt.hfc.Namespace.XSD_DOUBLE_LONG
	 * is used
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
			sb.append(XSD_DOUBLE_SHORT);
		else
			sb.append(XSD_DOUBLE_LONG);
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
