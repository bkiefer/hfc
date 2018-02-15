package de.dfki.lt.hfc.types;

/**
 * the Java representation of a URI in HFC, either as a short or long form name;
 * for instance
 *   + <rdf:type>
 *   + <http://www.w3.org/1999/02/22-rdf-syntax-ns#type>
 * note that we are always assuming angle brackets in _both_ cases here, contrary
 * to the XSD data type anyURI !!
 
 * these instances will only be constructed lazily, i.e., only if their
 * internal content need to be accessed
 *
 * NOTE: at the moment, we do NOT decompose an URI into namespace and `value'
 *
 * @author (C) Hans-Ulrich Krieger
 * @since JDK 1.5
 * @version Fri Jan 29 17:12:34 CET 2016
 */
public class Uri extends AnyType {
	
	public String value;
	
	public Uri(String value) {
		this.value = value;
	}
	
	/**
	 * NOTE: shortIsDefault can be ignored at the moment
	 */
	public String toString(boolean shortIsDefault) {
		return this.value;
	}
	
	/**
	 * omit the surrounding angle brackets
	 */
	public String toName() {
		return this.value.substring(1, this.value.length() - 1);
	}
  
  /**
   * as there is no direct Java counterpart, we return this object
   */
  public Object toJava() {
    return this;
  }

	@Override
	public int compareTo(Object o) {
		if(  o instanceof AnyType.MinMaxValue ) {
			AnyType.MinMaxValue minMaxValue = (MinMaxValue) o;
			return minMaxValue.compareTo(this);
		}
  		if (! (o instanceof  Uri)){
  			throw new IllegalArgumentException("Can't compare " + this.getClass()+" and " + o.getClass() );
		}
		return this.value.compareTo(((Uri) o).value);
	}
}
