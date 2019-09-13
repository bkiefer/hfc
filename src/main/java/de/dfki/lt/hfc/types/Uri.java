package de.dfki.lt.hfc.types;

import de.dfki.lt.hfc.Namespace;

import java.util.Objects;

/**
 * the Java representation of a URI in HFC, either as a short or long form name;
 * for instance
 * + <rdf:type>
 * + <http://www.w3.org/1999/02/22-rdf-syntax-ns#type>
 * note that we are always assuming angle brackets in _both_ cases here, contrary
 * to the XSD data type anyURI !!
 * <p>
 * these instances will only be constructed lazily, i.e., only if their
 * internal content need to be accessed
 * <p>
 * NOTE: at the moment, we do NOT decompose an URI into namespace and `value'
 *
 * @author (C) Hans-Ulrich Krieger
 * @version Fri Jan 29 17:12:34 CET 2016
 * @since JDK 1.5
 */
public class Uri extends AnyType {

  public String value;

  public Uri(String value, Namespace ns) {
    super(ns);
//    System.out.println(value);
    this.value = value;
  }


  /**
   * NOTE: shortIsDefault can be ignored at the moment
   */
  public String toString() {
    StringBuilder strb = new StringBuilder("<");
    strb.append(ns.toString());
    strb.append(this.value);
    strb.append(">");
    return strb.toString();
  }

  /**
   * omit the surrounding angle brackets
   */
  public String toName() {
    return ns.toString() + this.value;
  }

  /**
   * as there is no direct Java counterpart, we return this object
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
    if (!(o instanceof Uri)) {
      throw new IllegalArgumentException("Can't compare " + this.getClass() + " and " + o.getClass());
    }
    return this.value.compareTo(((Uri) o).value);
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Uri uri = (Uri) o;
    return Objects.equals(value, uri.value);
  }

  @Override
  public int hashCode() {
    return Objects.hash(value);
  }
}
