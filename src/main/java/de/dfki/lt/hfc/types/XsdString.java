package de.dfki.lt.hfc.types;

/**
 * @author (C) Hans-Ulrich Krieger
 * @since JDK 1.5
 * @version Fri Jan 29 19:38:30 CET 2016
 */
public final class XsdString extends XsdAnySimpleType {
  public final static String NAME = "string";

  public final static String SHORT_NAME = '<' + SHORT_PREFIX + NAME + '>';
  public final static String LONG_NAME = '<' + LONG_PREFIX + NAME + '>';

  static {
    registerConstructor(XsdString.class, SHORT_NAME, LONG_NAME);
    registerConverter(String.class, XsdString.class);
  }

	public String value;

	public String languageTag;

	/**
	 * @param val a string, representing an XSD string, e.g., "\"hello\"",
	 *              "\"hello\"@en", or "\"hello\"^^<xsd:string>"
	 */
	public XsdString(String val) {
    if (val.isEmpty()) {
      this.value = val;
      return;
    }
		int index = val.lastIndexOf('^');
		if (index == -1) {
			// no suffix "^^<xsd:string>"
			index = val.lastIndexOf('@');
			final int length = val.length();
			if (index == -1) {
				// no language tag
			  if (val.charAt(0) == '"' &&
			      val.charAt(val.length() - 1) == '"') {
			    this.value = val.substring(1, length - 1);
			  } else {
			    this.value = val;
			  }
			  this.languageTag = null;
			}
			else {
				// there is a language tag
				this.value = val.substring(1, index - 1);
				this.languageTag = val.substring(index + 1, length);;
			}
		}
		else {
			this.value = val.substring(1, index - 2);
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
	 *   de.dfki.lt.hfc.Namespace.SHORT_NAME
	 * or
	 *   de.dfki.lt.hfc.Namespace.LONG_NAME
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
				sb.append(SHORT_NAME);
			else
				sb.append(LONG_NAME);
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
			sb.append(SHORT_NAME);
		else
			sb.append(LONG_NAME);
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
   * we return the pure string _without_ the language tag
   */
  public Object toJava() {
    return this.value;
  }

}
