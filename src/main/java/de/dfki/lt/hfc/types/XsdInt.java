package de.dfki.lt.hfc.types;

/**
 * @author (C) Hans-Ulrich Krieger
 * @since JDK 1.5
 * @version Fri Jan 29 19:30:23 CET 2016
 */
public final class XsdInt extends XsdAnySimpleType {
  public final static String NAME = "int";

  public final static String SHORT_NAME = '<' + SHORT_PREFIX + NAME + '>';
  public final static String LONG_NAME = '<' + LONG_PREFIX + NAME + '>';

  static {
    registerConstructor(XsdInt.class, SHORT_NAME, LONG_NAME);
    registerConverter(int.class, XsdInt.class);
    registerConverter(Integer.class, XsdInt.class);
  }

	public int value;

	/**
	 * @param value a Java int representation of an XSD int
	 */
	public XsdInt(int value) {
		this.value = value;
	}

  /**
   * @param value a Java int representation of an XSD int
   */
  public XsdInt(Integer value) {
    this.value = value;
  }

	/**
	 * @param value a string, representing an XSD int, e.g., "\"42\"^^<xsd:int>"
	 */
	public XsdInt(String value) {
		// get rid of "^^xsd:int" and leading & trailing '"' chars
	  this.value = Integer.parseInt(extractValue(value));
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
	public static String toString(int val, boolean shortIsDefault) {
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
	 * turn int value into a string
	 */
	public String toName() {
		return Integer.toString(this.value);
	}

  /**
   * returns a java.lang.Integer container for an HFC XsdInt object
   */
  public Object toJava() {
    return this.value;
  }

	@Override
	public int compareTo(Object o) {
  		if(  o instanceof AnyType.MinMaxValue ) {
  			AnyType.MinMaxValue minMaxValue = (MinMaxValue) o;
  			return minMaxValue.compareTo(this);
		}
  		if (! (o instanceof  XsdInt )){
			throw new IllegalArgumentException("Can't compare " + this.getClass()+" and " + o.getClass() );
		}
		return Integer.compare(this.value,((XsdInt) o).value);
	}

}
