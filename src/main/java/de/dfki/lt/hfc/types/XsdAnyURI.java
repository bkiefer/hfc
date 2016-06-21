package de.dfki.lt.hfc.types;

/**
 * NOTE: XsdAnyURI should be used to represent true URI values, i.e.,
 *       _without_ angle brackets !
 *       this must be distinguished from class Uri in HFC, the latter
 *       representing URIs (contrary to blank nodes or XSD atoms) in
 *       the HFC forward chainer, and written _with_ angle brackets !
 *
 * @see de.dfki.lt.hfc.types.Uri
 *
 * @author (C) Hans-Ulrich Krieger
 * @since JDK 1.5
 * @version Fri Jan 29 17:16:44 CET 2016
 */
public final class XsdAnyURI extends XsdAnySimpleType {
  public final static String NAME = "anyURI";

  public final static String SHORT_NAME = '<' + SHORT_PREFIX + NAME + '>';
  public final static String LONG_NAME = '<' + LONG_PREFIX + NAME + '>';

  static {
    registerConstructor(XsdAnyURI.class, null, SHORT_NAME, LONG_NAME );
  }

	public String value;

	/**
	 * @param value a Java string, representing an XSD anyURI atom, e.g.,
	 * "\"http://www.w3.org/1999/02/22-rdf-syntax-ns#type\"^^<xsd:anyURI>"
	 */
	public XsdAnyURI(String value) {
		// get rid of "^^xsd:anyURI" and leading & trailing '"' chars
		final int index = value.lastIndexOf('^');
		this.value = value.substring(1, index - 2);
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
	public static String toString(String val, boolean shortIsDefault) {
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
	 * nothing to change with the internal value
	 */
	public String toName() {
		return this.value;
	}

  /**
   * returns a java.net.URI object for a given XsdAnyURI object from HFC if
   * the this.value is compliant with the syntax specification of URIs;
   * null otherwise as the java.net.URISyntaxException is caught in toJava();
   * an error message is furthermore printed to System.err
   */
  public Object toJava() {
    try {
     return new java.net.URI(this.value);
    }
    catch (java.net.URISyntaxException e) {
      System.err.println("  " + this.value + " not compliant with URI syntax specification");
    }
    return null;
  }

}
