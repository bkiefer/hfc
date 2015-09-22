package de.dfki.lt.hfc.types;

import de.dfki.lt.hfc.TupleStore;

/**
 * The double datatype is patterned after the IEEE double-precision 64-bit floating point type [IEEE 754-1985]
 * @author (C) Hans-Ulrich Krieger
 * @since JDK 1.5
 * @version Mon May 30 16:46:09 CEST 2011
 */
public final class XsdDouble extends XsdAnySimpleType {
	
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
		int index = value.lastIndexOf('^');
		String doublestring = value.substring(1, index - 2);
		this.value = Double.parseDouble(doublestring);
	}
	
	/**
	 * depending on shortIsDefault, either the suffix
	 *   de.dfki.lt.hfc.Namespace.XSD_DOUBLE_SHORT
	 * or
	 *   de.dfki.lt.hfc.Namespace.XSD_DOUBLE_LONG
	 * is used
	 */
	public String toString(boolean shortIsDefault) {
		StringBuilder sb = new StringBuilder("\"");
		sb.append(this.value);
		sb.append("\"^^");
		if (shortIsDefault)
			sb.append(de.dfki.lt.hfc.Namespace.XSD_DOUBLE_SHORT);
		else
			sb.append(de.dfki.lt.hfc.Namespace.XSD_DOUBLE_LONG);
		return sb.toString();
	}
	
	/**
	 * binary version is given the value directly
	 */
	public static String toString(double val, boolean shortIsDefault) {
		StringBuilder sb = new StringBuilder("\"");
		sb.append(val);
		sb.append("\"^^");
		if (shortIsDefault)
			sb.append(de.dfki.lt.hfc.Namespace.XSD_DOUBLE_SHORT);
		else
			sb.append(de.dfki.lt.hfc.Namespace.XSD_DOUBLE_LONG);
		return sb.toString();
	}
	
	/**
	 * turn double value into a string
	 */
	public String toName() {
		return Double.toString(this.value);
	}
	
  /**
   * returns the value (a double number) for a given XsdDouble object which
   * is refered to by its internal TupleStore ID (a positive int)
   */
  public static Object getValue(int id, TupleStore ts) {
    final XsdDouble xd = (XsdDouble)(ts.getJavaObject(id));
    return new Double(xd.value);
  }
  
}
