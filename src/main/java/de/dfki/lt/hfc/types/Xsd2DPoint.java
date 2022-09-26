package de.dfki.lt.hfc.types;

import de.dfki.lt.hfc.indices.ZOrder;

import java.util.Objects;

/**
 * An encoding of a new XSD format  representing a point in a two dimensional space "[+|-]x,[+|-]y"
 * if more ``precision'' is needed, use the Xsd3DPoint class
 *
 * @author (C) Christian Willms
 * @version 14.03.17.
 * @see Xsd3DPoint
 * @since JDK 1.8
 */
public class Xsd2DPoint extends XsdPoint {

  public final static String NAME = "2DPoint";

  public final static String SHORT_NAME = '<' + NS.getShort() + ":" + NAME + '>';
  public final static String LONG_NAME = '<' + NS.getLong() + NAME + '>';


  static {
    registerConstructor(Xsd2DPoint.class, SHORT_NAME, LONG_NAME);
  }

  /**
   * these fields are all of type int;
   * I represent the sign in a separate boolean field
   */
  public int x, y;


  /**
   *
   */
  public Xsd2DPoint(int x, int y) {
    this.x = x;
    this.y = y;
    if (x < 0 || y < 0)
      throw new IllegalArgumentException("No neg Values for Xsd2DPoint");
    computeZOrder();
  }

  /**
   * @param point a _fully_ specified XSD 2DPoint expression
   */
  public Xsd2DPoint(String point) {
    // get rid of "^^<xsd:2DPoint>" and leading & trailing '"' chars
    int index = point.lastIndexOf('^');
    point = point.substring(2, index - 3);

    // x
    int pos = point.indexOf(',');
    this.x = Integer.parseInt(point.substring(0, pos));
    // y
    // the rest
    this.y = Integer.parseInt(point.substring(pos + 1));
    if (x < 0 || y < 0)
      throw new IllegalArgumentException("No neg Values for Xsd2DPoint");
    computeZOrder();
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder("\"");
    sb.append(this.x);
    sb.append(",");
    sb.append(this.y);
    sb.append("\"^^");
    sb.append(NS.isShort() ? SHORT_NAME : LONG_NAME);
    return sb.toString();
  }

  @Override
  public Object toJava() {
    return this;
  }

  @Override
  public int compareTo(Object o) {
    if (o instanceof AnyType.MinMaxValue) {
      AnyType.MinMaxValue minMaxValue = (MinMaxValue) o;
      return minMaxValue.compareTo(this);
    }
    if (!(o instanceof Xsd2DPoint)) {
      throw new IllegalArgumentException("Can't compare " + this.getClass() + " and " + o.getClass());
    }
    if (this.neg || ((Xsd2DPoint) o).neg)
      return Long.compare(((Xsd2DPoint) o).getZOrder(), this.zOrder);
    return Long.compare(this.zOrder, ((Xsd2DPoint) o).getZOrder());
  }

  @Override
  protected void computeZOrder() {
    try {
      ZOrder order = new ZOrder(2, 32);
      this.zOrder = order.spack(x, y);
    } catch (ZOrder.ZOrderException e) {
      e.printStackTrace();
    }
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Xsd2DPoint that = (Xsd2DPoint) o;
    return x == that.x &&
            y == that.y;
  }

  @Override
  public int hashCode() {
    return Objects.hash(x, y);
  }
}
