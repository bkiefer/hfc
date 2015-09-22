package de.dfki.lt.hfc.types;

/**
 * @author (C) Hans-Ulrich Krieger
 * @since JDK 1.5
 * @version Mon May 30 16:46:09 CEST 2011
 */
public final class XsdBoolean extends XsdAnySimpleType {
	
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
		int index = value.lastIndexOf('^');
		String booleanstring = value.substring(1, index - 2);
		this.value = Boolean.parseBoolean(booleanstring);
	}
	
	/**
	 * depending on shortIsDefault, either the suffix
	 *   de.dfki.lt.hfc.Namespace.XSD_BOOLEAN_SHORT
	 * or
	 *   de.dfki.lt.hfc.Namespace.XSD_BOOLEAN_LONG
	 * is used
	 */
	public String toString(boolean shortIsDefault) {
		StringBuilder sb = new StringBuilder("\"");
		sb.append(this.value);
		sb.append("\"^^");
		if (shortIsDefault)
			sb.append(de.dfki.lt.hfc.Namespace.XSD_BOOLEAN_SHORT);
		else
			sb.append(de.dfki.lt.hfc.Namespace.XSD_BOOLEAN_LONG);
		return sb.toString();
	}
	
	/**
	 * binary version is given the value directly
	 */
	public static String toString(boolean val, boolean shortIsDefault) {
		StringBuilder sb = new StringBuilder("\"");
		sb.append(val);
		sb.append("\"^^");
		if (shortIsDefault)
			sb.append(de.dfki.lt.hfc.Namespace.XSD_BOOLEAN_SHORT);
		else
			sb.append(de.dfki.lt.hfc.Namespace.XSD_BOOLEAN_LONG);
		return sb.toString();
	}
	
	/**
	 * turn Boolean value into a string
	 */
	public String toName() {
		return Boolean.toString(this.value);
	}
	
}
