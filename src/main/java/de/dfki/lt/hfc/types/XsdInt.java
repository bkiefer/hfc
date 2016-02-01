package de.dfki.lt.hfc.types;

import de.dfki.lt.hfc.TupleStore;

/**
 * @author (C) Hans-Ulrich Krieger
 * @since JDK 1.5
 * @version Fri Jan 29 19:30:23 CET 2016
 */
public final class XsdInt extends XsdAnySimpleType {
	
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
		int index = value.lastIndexOf('^');
		String intstring = value.substring(1, index - 2);
		this.value = Integer.parseInt(intstring);
	}
	
	/**
	 * depending on shortIsDefault, either the suffix
	 *   de.dfki.lt.hfc.Namespace.XSD_INT_SHORT
	 * or
	 *   de.dfki.lt.hfc.Namespace.XSD_INT_LONG
	 * is used
	 */
	public String toString(boolean shortIsDefault) {
		StringBuilder sb = new StringBuilder("\"");
		sb.append(this.value);
		sb.append("\"^^");
		if (shortIsDefault)
			sb.append(de.dfki.lt.hfc.Namespace.XSD_INT_SHORT);
		else
			sb.append(de.dfki.lt.hfc.Namespace.XSD_INT_LONG);
		return sb.toString();
	}
	
	/**
	 * binary version is given the value directly
	 */
	public static String toString(int val, boolean shortIsDefault) {
		StringBuilder sb = new StringBuilder("\"");
		sb.append(val);
		sb.append("\"^^");
		if (shortIsDefault)
			sb.append(de.dfki.lt.hfc.Namespace.XSD_INT_SHORT);
		else
			sb.append(de.dfki.lt.hfc.Namespace.XSD_INT_LONG);
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
    return new Integer(this.value);
  }
	
  /**
   * for test purposes only
   */
  public static void main(String[] args) {
    XsdInt xi = new XsdInt("\"42\"^^<xsd:int>");
    System.out.println(xi.value);
		System.out.println(xi.toString(true));
		System.out.println(xi.toString(false));
	  System.out.println();
		xi = new XsdInt(2);
		System.out.println(xi.value);
		System.out.println(xi.toString(true));
		System.out.println(xi.toString(false));
  }
	
}
