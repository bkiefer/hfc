package de.dfki.lt.hfc.types;

import de.dfki.lt.hfc.TupleStore;

/**
 * @author (C) Hans-Ulrich Krieger
 * @since JDK 1.5
 * @version Mon May 30 16:46:09 CEST 2011
 */
public final class XsdString extends XsdAnySimpleType {
	
	public String value;
	
	public String languageTag;
	
	/**
	 * @param value a string, representing an XSD string, e.g., "\"hello\"",
	 *              "\"hello\"@en", or "\"hello\"^^<xsd:string>"
	 */
	public XsdString(String value) {
		String string;
		int index = value.lastIndexOf('^');
		if (index == -1) {
			// no suffix "^^<xsd:string>"
			index = value.lastIndexOf('@');
			final int length = value.length();
			if (index == -1) {
				// no language tag
				this.value = value.substring(1, length - 1);
				this.languageTag = null;
			}
			else {
				// there is a language tag
				this.value = value.substring(1, index - 1);
				this.languageTag = value.substring(index + 1, length);;
			}
		}
		else {
			this.value = value.substring(1, index - 2);
			this.languageTag = null;
		}
	}
	
	/**
	 * @param value a Java string representation of an XSD string
	 * @param languageTag a language tag (e.g., "en");
	 *                    use null to indicate that there is no language
	 */
	public XsdString(String value, String languageTag) {
		this.value = value;
		this.languageTag = languageTag;
	}
	
	/**
	 * depending on shortIsDefault, either the suffix
	 *   de.dfki.lt.hfc.Namespace.XSD_STRING_SHORT
	 * or
	 *   de.dfki.lt.hfc.Namespace.XSD_STRING_LONG
	 * is used;
	 * shortIsDefault is ignored in case a language tag is available
	 */
	public String toString(boolean shortIsDefault) {
		StringBuilder sb = new StringBuilder("\"");
		sb.append(this.value);
		sb.append("\"");
		if (this.languageTag != null) {
			sb.append("@");
			sb.append(languageTag);
		}
		else {
			sb.append("^^");
			if (shortIsDefault)
				sb.append(de.dfki.lt.hfc.Namespace.XSD_STRING_SHORT);
			else
				sb.append(de.dfki.lt.hfc.Namespace.XSD_STRING_LONG);
		}
		return sb.toString();
	}
	
	/**
	 * binary version is given the value directly;
	 */
	public static String toString(String val, boolean shortIsDefault) {
		StringBuilder sb = new StringBuilder("\"");
		sb.append(val);
		sb.append("\"^^");
		if (shortIsDefault)
			sb.append(de.dfki.lt.hfc.Namespace.XSD_STRING_SHORT);
		else
			sb.append(de.dfki.lt.hfc.Namespace.XSD_STRING_LONG);
		return sb.toString();
	}
	
	/**
	 * directly return the string value, but replace " ", "<", and ">"
	 * by "_"
	 */
	public String toName() {
		return this.value.replaceAll("[ <>]", "_");
	}
  
  /**
   * returns the value (the Java string; note: language tag _not_ used) for a
   * given XsdString object which is refered to by its internal TupleStore ID
   * (a positive int)
   */
  public static Object getValue(int id, TupleStore ts) {
    final XsdString xs = (XsdString)(ts.getJavaObject(id));
    return xs.value;
  }
	
	/*
	public static void main(String[] args) {
		XsdString xs = new XsdString("\"hel <lo>\"^^<xsd:string>");
		System.out.println(xs.toName());
		xs = new XsdString("\"hello\"^^<xsd:string>");
		System.out.println(xs.value);
		System.out.println(xs.languageTag);
		System.out.println(xs.toString(true));
		System.out.println(xs.toString(false));
		System.out.println();
		xs = new XsdString("\"hello\"");
		System.out.println(xs.value);
		System.out.println(xs.languageTag);
		System.out.println(xs.toString(true));
		System.out.println(xs.toString(false));
		System.out.println();
		xs = new XsdString("\"hello\"@en");
		System.out.println(xs.value);
		System.out.println(xs.languageTag);
		System.out.println(xs.toString(true));
		System.out.println(xs.toString(false));
		System.out.println();
		xs = new XsdString("hello", null);
		System.out.println(xs.value);
		System.out.println(xs.languageTag);
		System.out.println(xs.toString(true));
		System.out.println(xs.toString(false));
		System.out.println();
		xs = new XsdString("hello", "en");
		System.out.println(xs.value);
		System.out.println(xs.languageTag);
		System.out.println(xs.toString(true));
		System.out.println(xs.toString(false));
	}
	*/
	
}
