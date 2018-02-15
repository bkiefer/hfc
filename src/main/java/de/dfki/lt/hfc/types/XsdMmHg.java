package de.dfki.lt.hfc.types;

/**
 * The xsd:mmHg datatype is supposed to encode blood pressure in
 * millimeter of mercury
 *
 * @author (C) Hans-Ulrich Krieger
 * @since JDK 1.5
 * @version Tue Mar  1 13:41:15 CET 2016
 */
public final class XsdMmHg extends XsdAnySimpleType {
  
  public final static String NAME = "mmHg";
  
  public final static String SHORT_NAME = '<' + SHORT_PREFIX + NAME + '>';
  public final static String LONG_NAME = '<' + LONG_PREFIX + NAME + '>';
  
  static {
    registerConstructor(XsdMmHg.class, SHORT_NAME, LONG_NAME);
  }
  
  public double value;
  
  /**
   * @param value a Java double representation of the blood pressure
   */
  public XsdMmHg(double value) {
    this.value = value;
  }
  
  /**
   * @param value a string, representing weight; e.g., "\"92.0\"^^<xsd:mmHg>"
   */
  public XsdMmHg(String value) {
    this.value = Double.parseDouble(extractValue(value));
  }
  
  /**
   * depending on shortIsDefault, either the suffix SHORT_NAME or LONG_NAME is used
   */
  public String toString(boolean shortIsDefault) {
    return toString(this.value, shortIsDefault);
  }
  
  /**
   * binary version is given the value directly
   */
  public static String toString(double val, boolean shortIsDefault) {
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
   * turn double value into a string
   */
  public String toName() {
    return Double.toString(this.value);
  }
  
  /**
   * returns a java.lang.Double container for an HFC XsdMmHg object
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
    if (! (o instanceof  XsdMmHg)){
      throw new IllegalArgumentException("Can't compare " + this.getClass()+" and " + o.getClass() );
    }
    return Double.compare(this.value,((XsdMmHg) o).value);
  }
  
}
