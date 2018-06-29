package de.dfki.lt.hfc.types;

import de.dfki.lt.hfc.Utils;

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
    // if the string is in "external" form, meaning it starts with '"' and
    // ends with '"', '"^^<xsd:string>' or '"@langtag', we have to set value
    // to the "internal" form, removing backslashes that escape other
    // backslashes or double quotes
		String ext = null;
		this.languageTag = null;
		if (val.endsWith("^^" + SHORT_NAME)) {
		  ext = val.substring(1, val.length() - SHORT_NAME.length() - 3);
		} else if (val.endsWith("^^" + LONG_NAME)) {
		  ext = val.substring(1, val.length() - LONG_NAME.length() - 3);
		} else {
		  // no suffix "^^<xsd:string>"
			int index = val.lastIndexOf('@');
			final int length = val.length();
			if (index == -1 || index < 2) {
				// no language tag
			  if (val.charAt(0) == '"' &&
			      val.charAt(val.length() - 1) == '"') {
			    ext = val.substring(1, length - 1);
			  } else {
			    this.value = val;
			  }
			}
			else {
				// there is a language tag ?
			  if (val.charAt(0) == '"' && val.charAt(index - 1) == '"') {
			    ext = val.substring(1, index - 1);
			    this.languageTag = val.substring(index + 1, length);;
			  } else {
			    this.value = val;
			  }
			}
		}

		if (ext != null) {
		  this.value = Utils.externalToString(ext);
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

	private static String toString(String val, String languageTag, boolean shortIsDefault) {
		StringBuilder sb = new StringBuilder("\"");
		sb.append(Utils.stringToExternal(val));
		sb.append("\"");
		if (languageTag != null) {
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
	 * depending on shortIsDefault, either the suffix
	 *   de.dfki.lt.hfc.Namespace.SHORT_NAME
	 * or
	 *   de.dfki.lt.hfc.Namespace.LONG_NAME
	 * is used;
	 * shortIsDefault is ignored in case a language tag is available
	 */
	public String toString(boolean shortIsDefault) {
	  return toString(this.value, this.languageTag, shortIsDefault);
	}

	/**
	 * binary version is given the value directly;
	 */
	public static String toString(String val, boolean shortIsDefault) {
	  return toString(val, null, shortIsDefault);
	}

	/**
	 * directly return the string value, but replace " ", "<", and ">"
	 * by "_"
	 */
	public String toName() {
		return this.value.replaceAll("[ <>]", "_");
	}

  /**
   * we return the pure string if there is no the language tag
   */
  public Object toJava() {
    return this.languageTag == null ? this.value : this;
  }

  /**
   * return only the string, ignoring the language tag
   */
  public String toCharSequence() {
    return this.value;
  }

  /** Two XsdString objects are equal if value and language tag are equal
   */
  public boolean equals(Object o) {
    if (o instanceof String)
      return this.languageTag == null && value.equals(o);
    if (o instanceof XsdString) {
      XsdString s = (XsdString)o;
      if (this.languageTag == null) {
        return s.languageTag == null && this.value.equals(s.value);
      } else {
        return this.languageTag.equals(s.languageTag)
            && this.value.equals(s.value);
      }
    }
    return false;
  }

  public int hashCode() {
    return this.value.hashCode()
        + (this.languageTag == null ? 0 : 29 * this.languageTag.hashCode());
  }

	@Override
	public int compareTo(Object o) {
		if(  o instanceof AnyType.MinMaxValue ) {
			AnyType.MinMaxValue minMaxValue = (MinMaxValue) o;
			return minMaxValue.compareTo(this);
		}
		if (!(o instanceof XsdString)) {
			throw new IllegalArgumentException("Can't compare " + this.getClass() + " and " + o.getClass());
		}
		return this.value.compareTo(((XsdString) o).value);
	}
}
