package de.dfki.lt.hfc.types;

import de.dfki.lt.hfc.TupleStore;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * NOTE: XsdAnyURI should be used to represent <xsd:anyURI> values;
 *       this must be distinguished from class Uri in HFC, the latter
 *       representing URIs (contrary to blank nodes or XSD atoms) in
 *       the HFC forward chainer;
 * @see de.dfki.lt.hfc.types.Uri
 *
 * @author (C) Hans-Ulrich Krieger
 * @since JDK 1.5
 * @version Wed Sep 23 10:14:27 CEST 2015
 */
public final class XsdAnyURI extends XsdAnySimpleType {
	
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
	 *   de.dfki.lt.hfc.Namespace.XSD_ANYURI_SHORT
	 * or
	 *   de.dfki.lt.hfc.Namespace.XSD_ANYURI_LONG
	 * is used
	 */
	public String toString(boolean shortIsDefault) {
		StringBuilder sb = new StringBuilder("\"");
		sb.append(this.value);
		sb.append("\"^^");
		if (shortIsDefault)
			sb.append(de.dfki.lt.hfc.Namespace.XSD_ANYURI_SHORT);
		else
			sb.append(de.dfki.lt.hfc.Namespace.XSD_ANYURI_LONG);
		return sb.toString();
	}
	
	/**
	 * binary version is given the value directly
	 */
	public static String toString(String val, boolean shortIsDefault) {
		StringBuilder sb = new StringBuilder("\"");
		sb.append(val);
		sb.append("\"^^");
		if (shortIsDefault)
			sb.append(de.dfki.lt.hfc.Namespace.XSD_ANYURI_SHORT);
		else
			sb.append(de.dfki.lt.hfc.Namespace.XSD_ANYURI_LONG);
		return sb.toString();
	}
	
	/**
	 * nothing to change with the internal value
	 */
	public String toName() {
		return this.value;
	}
	
  /**
   * returns a Java URI object for a given XsdAnyURI object which is
   * refered to by its internal TupleStore ID (a positive int)
   */
  public static Object getValue(int id, TupleStore ts) throws URISyntaxException {
    final XsdAnyURI xu = (XsdAnyURI)(ts.getJavaObject(id));
    return new URI(xu.value);
  }
  
}