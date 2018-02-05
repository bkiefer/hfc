package de.dfki.lt.hfc.types;

/**
 * an encoding of the XSD duration format "[+|-]PnYnMnDTnHnMnS";
 * note that there is no need to specify each individual literal;
 * examples:
 *   * a period/duration of 4 years can be also specified as 48 months:
 *     P4Y = P48M
 *   * 48 minutes
 *     PT48M
 *   * P1Y3M0D = P1Y3M
 * NOTE:
 *   P3M1Y is not allowed, only P1Y3M, but also other combinations
 *   which do NOT obey the above (transitive) ordering
 *
 * @see http://www.w3.org/TR/xmlschema-2/
 * @see http://www.schemacentral.com/sc/xsd/t-xsd_duration.html
 *
 * @author (C) Hans-Ulrich Krieger
 * @since JDK 1.5
 * @version Fri Jan 29 19:09:59 CET 2016
 */
public final class XsdDuration extends XsdAnySimpleType {
  public final static String NAME = "duration";

  public final static String SHORT_NAME = '<' + SHORT_PREFIX + NAME + '>';
  public final static String LONG_NAME = '<' + LONG_PREFIX + NAME + '>';

  static {
    registerConstructor(XsdDuration.class, SHORT_NAME, LONG_NAME);
  }

	/**
	 * - = negative periods
	 * + = positive periods (optional)
	 * in case NO sign is specified, sign defaults to true (= positive periods)
	 */
	public boolean sign = true;

	/**
	 * these fields are all of type int and are set to 0 by default;
	 * OK, this is the default value anyway, but I make it explicit here;
	 * I represent the sign in a separate boolean field
	 */
	public int year = 0;
	public int month = 0;
	public int day = 0;
	public int hour = 0;
	public int minute = 0;

	/**
	 * second is the only field that is of type float in order to
	 * represent parts of a second
	 */
	public float second = 0;

	/**
	 *
	 */
	public XsdDuration(int year, int month, int day, int hour, int minute, float second) {
		this.year = year;
		this.month = month;
		this.day = day;
		this.hour = hour;
		this.minute = minute;
		this.second = second;
	}

	/**
	 *
	 */
	public XsdDuration(boolean sign, int year, int month, int day, int hour, int minute, float second) {
		this.sign = sign;
		this.year = year;
		this.month = month;
		this.day = day;
		this.hour = hour;
		this.minute = minute;
		this.second = second;
	}

	/**
	 * NOTE: at the moment, I do NOT check whether the expression is syntactically legal
	 * @param time a _fully_ specified XSD duration expression "[+|-]PnYnMnDTnHnMnS"
	 */
	public XsdDuration(String time) {
		// get rid of "^^<xsd:duration>" and leading & trailing '"' chars
		time = extractValue(time);
		// is there a sign ?
		if (time.charAt(0) == '+')
			time = time.substring(1);
		else if (time.charAt(0) == '-') {
			this.sign = false;
			time = time.substring(1);
		}
		// skip over the 'P' -- assume there IS always a 'P' (actually only a character)
		time = time.substring(1);
		// use Boolean flag to see whether we have found the optional 'T'
		boolean tFound = false;
		// use a string builder to obtain the n-values
		StringBuilder val = new StringBuilder();
		char c;
		for (int i = 0; i < time.length(); i++) {
			c = time.charAt(i);
			if (Character.isDigit(c) || c == '.')
				val.append(c);
			else if (c == 'T')
				// there is always another uppercase letter before 'T'
				tFound = true;
			else {
				// c is (should be) 'Y', 'M', 'D', 'H', or 'S' -- other chars are ignored here
				if (c == 'Y')
					this.year = Integer.parseInt(val.toString());
				else if (c == 'M') {
					if (tFound)
						this.minute = Integer.parseInt(val.toString());
					else
						this.month = Integer.parseInt(val.toString());
				}
				else if (c == 'D')
					this.day = Integer.parseInt(val.toString());
				else if (c == 'H')
					this.hour = Integer.parseInt(val.toString());
				else if (c == 'S')
					this.second = Float.parseFloat(val.toString());
				// start with an empty SB again
				val = new StringBuilder();
			}
		}
	}

	/**
	 * depending on shortIsDefault, either the suffix
	 *   de.dfki.lt.hfc.Namespace.SHORT_NAME
	 * or
	 *   de.dfki.lt.hfc.Namespace.LONG_NAME
	 * is used;
	 * format is: "[+|-]PnYnMnDTnHnMnS"
	 */
	public String toString(boolean shortIsDefault) {
		final String tail = "\"^^" + (shortIsDefault ? SHORT_NAME : LONG_NAME);
		StringBuilder sb = new StringBuilder("\"");
		if (! this.sign)
			sb.append('-');
		// 'P' is always required !
		sb.append('P');
		// do not append zero numbers !
		if (this.year != 0)
			sb.append(this.year).append('Y');
		if (this.month != 0)
			sb.append(this.month).append('M');
		if (this.day != 0)
			sb.append(this.day).append('D');
		// add 'T' iff some non-zero value is following
		if (this.hour != 0 || this.minute != 0 || this.second != 0.0F) {
			if (this.hour != 0)
				sb.append(this.hour).append('H');
			if (this.minute != 0)
				sb.append(this.minute).append('M');
			if (this.second != 0)
				sb.append(this.second).append('S');
		}
		return sb.append(tail).toString();
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
   * even though there exist a javax.xml.datatype.Duration class, this
   * class does not perfectly fit the intention behind XsdDuration, so
   * return this object
   */
  public Object toJava() {
    return this;
  }

	/**
   * for test puposes only
   */
  public static void main(String[] args) {
    XsdDuration xt = new XsdDuration("\"-P12000Y0M04D\"^^<xsd:duration>");
    System.out.println(xt.year);
    System.out.println(xt.hour);
    System.out.println(xt.second);
    System.out.println(xt.toString(true));
    System.out.println(xt.toString(false));
    System.out.println();
    xt = new XsdDuration(false, 2009, 1, 12, 1, 0, 3.456F);
    System.out.println(xt.toString(true));
    System.out.println(xt.toName());
    System.out.println(xt.toString(true));
    System.out.println(xt.toString(false));
  }

	@Override
	public int compareTo(Object o) {
		if(  o instanceof AnyType.MinMaxValue ) {
			AnyType.MinMaxValue minMaxValue = (MinMaxValue) o;
			return minMaxValue.compareTo(this);
		}
		if (! (o instanceof  XsdDuration)){
			throw new IllegalArgumentException("Can't compare " + this.getClass()+" and " + o.getClass() );
		}
		XsdDuration dur = (XsdDuration) o;
		int[] comp = new int[]{Integer.compare(this.year,dur.year), Integer.compare(this.month, dur.month),
				Integer.compare(this.day, dur.day), Integer.compare(this.hour, dur.hour),
				Integer.compare(this.minute,dur.minute), Float.compare(this.second, dur.second)};
		for (int r : comp){
			if (r != 0)
				return r;
		}
		return 0;
	}
}
