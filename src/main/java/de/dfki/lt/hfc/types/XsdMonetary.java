package de.dfki.lt.hfc.types;

/**
 * an encoding of the currently NOT existing XSD type monetary
 * <p>
 * note: I do NOT check whether the currency abbreviation (three uppercase letters) is
 * in fact a legal currency and whether the monetary expression is syntactically
 * correct;
 * <p>
 * syntax: <monetary> ::= "<amount><currency>"
 * <amount>   ::= any (positive/negative) (floating-point) number
 * <currency> ::= a three uppercase letter code, as defined by ISO 4217
 * note: <amount> and <currency> are written together -- there is NO space left between them
 * <p>
 * examples:
 * + a value of 42 US Dollars would be represented as: "42USD"^^<xsd:monetary>
 * + a value of 4.37 Euro would be represented as: "4.37EUR"^^<xsd:monetary>
 *
 * @author (C) Hans-Ulrich Krieger
 * @version Fri Jan 29 19:35:14 CET 2016
 * @see ISO 4217 ISO Standards for Currency Names, International Organization for Standardization (ISO), 1999.
 * @see http://en.wikipedia.org/wiki/ISO_4217
 * @see http://www.w3.org/TR/2001/WD-xforms-20010608/slice4.html#dt-currency
 * @since JDK 1.5
 */
public final class XsdMonetary extends XsdAnySimpleType {
  public final static String NAME = "monetary";

  public final static String SHORT_NAME = '<' + SHORT_PREFIX + NAME + '>';
  public final static String LONG_NAME = '<' + LONG_PREFIX + NAME + '>';
  /**
   * some useful constants
   */
  public static final String USD = "USD";  // US Dollar
  public static final String CNY = "CNY";  // Chinese Yuan
  public static final String JPY = "JPY";  // Japanese Yen
  public static final String EUR = "EUR";  // European Euro
  public static final String GBP = "GBP";  // British Pound Sterling
  public static final String RUB = "RUB";  // Russian Rouble

  static {
    registerConstructor(XsdMonetary.class, SHORT_NAME, LONG_NAME);
  }

  /**
   * the amount part of monetary
   */
  public double amount;

  /**
   * the currency part of monetary
   */
  public String currency;

  /**
   *
   */
  public XsdMonetary(double amount, String currency) {
    this.amount = amount;
    this.currency = currency;
  }

  /**
   * @param monetary a _fully_ specified monetary expression
   */
  public XsdMonetary(String monetary) {
    // get rid of "^^<xsd:Monetary>" and leading & trailing '"' chars
    monetary = extractValue(monetary);
    final int length = monetary.length();
    // amount
    this.amount = Double.parseDouble(monetary.substring(0, length - 3));
    // currency
    this.currency = monetary.substring(length - 3);
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
   * depending on shortIsDefault, either the suffix
   * de.dfki.lt.hfc.Namespace.SHORT_NAME
   * or
   * de.dfki.lt.hfc.Namespace.LONG_NAME
   * is used;
   * note that toString() does NOT check whether the internal
   * description is well-formed; e.g., we do not check whether
   * this.currency is a legal currency (three uppercase letters)
   */
  public String toString(boolean shortIsDefault) {
    StringBuilder sb = new StringBuilder("\"");
    sb.append(this.amount);
    sb.append(this.currency);
    sb.append("\"^^");
    if (shortIsDefault)
      sb.append(SHORT_NAME);
    else
      sb.append(LONG_NAME);
    return sb.toString();
  }

  /**
   * generates a string representation from the internal fields, but omits
   * the XSD type specification
   */
  public String toName() {
    return Double.toString(this.amount) + this.currency;
  }

  /**
   * there exists no Java counterpart, so return this object
   */
  public Object toJava() {
    return this;
  }

  @Override
  public int compareTo(Object o) {
    if (o instanceof AnyType.MinMaxValue) {
      AnyType.MinMaxValue minMaxValue = (MinMaxValue) o;
      return minMaxValue.compareTo(this);
    }
    if (!(o instanceof XsdMonetary)) {
      throw new IllegalArgumentException("Can't compare " + this.getClass() + " and " + o.getClass());
    }
    //TODO include currencies
    return Double.compare(this.amount, ((XsdMonetary) o).amount);
  }

}
