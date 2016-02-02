package de.dfki.lt.hfc.types;

import de.dfki.lt.hfc.TupleStore;

/**
 * float is patterned after the IEEE single-precision 32-bit floating point type [IEEE 754-1985]
 * @author (C) Hans-Ulrich Krieger
 * @since JDK 1.5
 * @version Fri Jan 29 19:29:19 CET 2016
 */
public final class XsdFloat extends XsdAnySimpleType {
	
	public float value;
	
	/**
	 * @param value a Java float representation of an XSD float
	 */
	public XsdFloat(float value) {
		this.value = value;
	}
	
	/**
	 * @param value a string, representing an XSD float, e.g., "\"3.1415\"^^<xsd:float>"
	 */
	public XsdFloat(String value) {
		// get rid of "^^xsd:float" and leading & trailing '"' chars
		int index = value.lastIndexOf('^');
		String floatstring = value.substring(1, index - 2);
		this.value = Float.parseFloat(floatstring);
	}
	
	/**
	 * depending on shortIsDefault, either the suffix
	 *   de.dfki.lt.hfc.Namespace.XSD_FLOAT_SHORT
	 * or
	 *   de.dfki.lt.hfc.Namespace.XSD_FLOAT_LONG
	 * is used
	 */
	public String toString(boolean shortIsDefault) {
		StringBuilder sb = new StringBuilder("\"");
		sb.append(this.value);
		sb.append("\"^^");
		if (shortIsDefault)
			sb.append(de.dfki.lt.hfc.Namespace.XSD_FLOAT_SHORT);
		else
			sb.append(de.dfki.lt.hfc.Namespace.XSD_FLOAT_LONG);
		return sb.toString();
	}
	
	/**
	 * binary version is given the value directly
	 */
	public static String toString(float val, boolean shortIsDefault) {
		StringBuilder sb = new StringBuilder("\"");
		sb.append(val);
		sb.append("\"^^");
		if (shortIsDefault)
			sb.append(de.dfki.lt.hfc.Namespace.XSD_FLOAT_SHORT);
		else
			sb.append(de.dfki.lt.hfc.Namespace.XSD_FLOAT_LONG);
		return sb.toString();
	}
	
	/**
	 * turn float value into a string
	 */
	public String toName() {
		return Float.toString(this.value);
	}
  
  /**
   * returns a java.lang.Float container for an HFC XsdFloat object
   */
  public Object toJava() {
    return new Float(this.value);
  }
  
  /**
   * for test purposes only
   */
  public static void main(String[] args) {
    XsdFloat xf = new XsdFloat("\"3.1415\"^^<xsd:float>");
    System.out.println(xf.value);
    System.out.println(xf.toString(true));
    System.out.println(xf.toString(false));
	  System.out.println();
		xf = new XsdFloat(2.71828f);
		System.out.println(xf.value);
		System.out.println(xf.toString(true));
		System.out.println(xf.toString(false));
  }
	
}
