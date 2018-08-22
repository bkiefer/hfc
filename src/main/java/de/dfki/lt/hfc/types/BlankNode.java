package de.dfki.lt.hfc.types;

/**
 * the Java representation of a blank node in HFC;
 * these instances will be constructed lazily, i.e., only if their
 * internal content need to be accessed
 *
 * @author (C) Hans-Ulrich Krieger
 * @version Fri Jan 29 17:04:22 CET 2016
 * @since JDK 1.5
 */
public class BlankNode extends AnyType {

  public String value;

  public BlankNode(String value) {
    this.value = value;
  }

  /**
   * NOTE: shortIsDefault can be ignored at the moment
   */
  public String toString(boolean shortIsDefault) {
    return this.value;
  }

  /**
   * omit the preceeding "_:"
   */
  public String toName() {
    return this.value.substring(2);
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
    if (!(o instanceof BlankNode)) {
      throw new IllegalArgumentException("Can't compare " + this.getClass() + " and " + o.getClass());
    }
    return this.value.compareTo(((BlankNode) o).value);
  }
}
