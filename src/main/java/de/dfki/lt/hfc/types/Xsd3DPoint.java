package de.dfki.lt.hfc.types;

import de.dfki.lt.hfc.indices.ZOrder;

/**
 * An encoding of a new XSD format  representing a point in a three dimensional space "[+|-]x,[+|-]y,[+|-]z"
 * if less ``precision'' is needed, use the Xsd2DPoint class
 *
 * @author (C) Christian Willms
 * @version 14.03.17.
 * @see Xsd2DPoint
 * @since JDK 1.8
 */
public class Xsd3DPoint extends XsdPoint {

  public final static String NAME = "3DPoint";

  public final static String SHORT_NAME = '<' + SHORT_PREFIX + NAME + '>';
  public final static String LONG_NAME = '<' + LONG_PREFIX + NAME + '>';

  static {
    registerConstructor(Xsd3DPoint.class, SHORT_NAME, LONG_NAME);
  }

  /**
   * these fields are all of type int;
   * I represent the sign in a separate boolean field
   */
  public long x, y, z;


  /**
   *
   */
  public Xsd3DPoint(long x, long y, long z) {
    this.x = x;
    this.y = y;
    this.z = z;
    if (x < 0 || y < 0 || z < 0)
      throw new IllegalArgumentException("No neg Values for Xsd3DPoint");
    computeZOrder();
  }

  /**
   * @param point a _fully_ specified XSD 2DPoint expression
   */
  public Xsd3DPoint(String point) {
    // get rid of "^^<xsd:2DPoint>" and leading & trailing '"' chars
    int index = point.lastIndexOf('^');
    point = point.substring(2, index - 3);

    // x
    int pos = point.indexOf(',');
    this.x = Integer.parseInt(point.substring(0, pos));
    // y
    point = point.substring(pos + 1);
    pos = point.indexOf(',');
    this.y = Integer.parseInt(point.substring(0, pos));
    // z
    // the rest
    this.z = Integer.parseInt(point.substring(pos + 1));
    if (x < 0 || y < 0 || z < 0)
      throw new IllegalArgumentException("No neg Values for Xsd3DPoint");
    computeZOrder();
  }

  @Override
  public String toString(boolean shortIsDefault) {
    StringBuilder sb = new StringBuilder("\"");
    sb.append(this.x);
    sb.append(",");
    sb.append(this.y);
    sb.append(",");
    sb.append(this.z);
    sb.append("\"^^");
    sb.append(shortIsDefault ? SHORT_NAME : LONG_NAME);
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
    if (!(o instanceof Xsd3DPoint)) {
      throw new IllegalArgumentException("Can't compare " + this.getClass() + " and " + o.getClass());
    }
    return Long.compare(this.zOrder, ((Xsd3DPoint) o).getZOrder());
  }

  @Override
  protected void computeZOrder() {
    try {
      ZOrder order = new ZOrder(3, 21);
      this.zOrder = order.spack(x, y, z);
    } catch (ZOrder.ZOrderException e) {
      e.printStackTrace();
    }
  }
}
