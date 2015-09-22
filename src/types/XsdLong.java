package de.dfki.lt.hfc.types;

import de.dfki.lt.hfc.TupleStore;

/**
 * note: currently XsdLong is *not* a superclass of XsdInt as
 * the XSD type tree indicates
 *
 * @author (C) Hans-Ulrich Krieger
 * @since JDK 1.5
 * @version Mon May 30 16:46:09 CEST 2011
 */
public final class XsdLong extends XsdAnySimpleType {
	
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
		int index = value.lastIndexOf('^');
		String longstring = value.substring(1, index - 2);
		this.value = Long.parseLong(longstring);
	}
	
	/**
	 * depending on shortIsDefault, either the suffix
	 *   de.dfki.lt.hfc.Namespace.XSD_LONG_SHORT
	 * or
	 *   de.dfki.lt.hfc.Namespace.XSD_LONG_LONG
	 * is used
	 */
	public String toString(boolean shortIsDefault) {
		StringBuilder sb = new StringBuilder("\"");
		sb.append(this.value);
		sb.append("\"^^");
		if (shortIsDefault)
			sb.append(de.dfki.lt.hfc.Namespace.XSD_LONG_SHORT);
		else
			sb.append(de.dfki.lt.hfc.Namespace.XSD_LONG_LONG);
		return sb.toString();
	}
	
	/**
	 * binary version is given the value directly
	 */
	public static String toString(long val, boolean shortIsDefault) {
		StringBuilder sb = new StringBuilder("\"");
		sb.append(val);
		sb.append("\"^^");
		if (shortIsDefault)
			sb.append(de.dfki.lt.hfc.Namespace.XSD_LONG_SHORT);
		else
			sb.append(de.dfki.lt.hfc.Namespace.XSD_LONG_LONG);
		return sb.toString();
	}
	
	/**
	 * turn long value into a string
	 */
	public String toName() {
		return Long.toString(this.value);
	}
  
  /**
   * returns the value (a long int) for a given XsdLong object which is
   * refered to by its internal TupleStore ID (a positive int)
   */
  public static Object getValue(int id, TupleStore ts) {
    final XsdLong xl = (XsdLong)(ts.getJavaObject(id));
    return new Long(xl.value);
  }

}