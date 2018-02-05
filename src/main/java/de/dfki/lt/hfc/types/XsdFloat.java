package de.dfki.lt.hfc.types;

/**
 * float is patterned after the IEEE single-precision 32-bit floating point type [IEEE 754-1985]
 * @author (C) Hans-Ulrich Krieger
 * @since JDK 1.5
 * @version Fri Jan 29 19:29:19 CET 2016
 */
public final class XsdFloat extends XsdAnySimpleType {
  public final static String NAME = "float";

  public final static String SHORT_NAME = '<' + SHORT_PREFIX + NAME + '>';
  public final static String LONG_NAME = '<' + LONG_PREFIX + NAME + '>';

  static {
    registerConstructor(XsdFloat.class, SHORT_NAME, LONG_NAME);
  }

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
		this.value = Float.parseFloat(extractValue(value));
	}

	/**
	 * depending on shortIsDefault, either the suffix
	 *   SHORT_NAME
	 * or
	 *   LONG_NAME
	 * is used
	 */
	public String toString(boolean shortIsDefault) {
		return toString(this.value, shortIsDefault);
	}

	/**
	 * binary version is given the value directly
	 */
	public static String toString(float val, boolean shortIsDefault) {
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
	 * turn float value into a string
	 */
	public String toName() {
		return Float.toString(this.value);
	}

  /**
   * returns a java.lang.Float container for an HFC XsdFloat object
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
		if (! (o instanceof  XsdFloat)){
			throw new IllegalArgumentException("Can't compare " + this.getClass()+" and " + o.getClass() );
		}
		return Float.compare(this.value,((XsdFloat) o).value);
	}

}
