package de.dfki.lt.hfc.indices;

import java.util.ArrayList;
import java.util.Objects;

/**
 * The Z-order (also called morton order) is used to convert 2 and 3 dimensional points in a linearised value.
 * The z-value of a point in multidimensions is simply calculated by interleaving the binary representations of
 * its coordinate values. Once the data are sorted into this ordering, any one-dimensional data structure can be used
 * such as binary search trees, B-trees, skip lists or (with low significant bits truncated) hash tables.
 * The resulting ordering can equivalently be described as the order one would get from a depth-first traversal of a quadtree.
 * <p>
 * Created by chwi02 on 16.03.17.
 */
public class ZOrder {

  private long dimensions;
  private long bits;

  private long[] masks;
  private long[] lshifts;
  private long[] rshifts;

  /**
   * TODO explain what happens here.
   *
   * @param dimensions
   * @param bits
   */
  public ZOrder(long dimensions, long bits) throws ZOrderException {
    if (dimensions <= 0 || bits <= 0 || dimensions * bits > 64) {
      throw new ZOrderException(String.format("can't make morton64 with %d dimensions and %d bits", dimensions, bits));
    }

    this.dimensions = dimensions;
    this.bits = bits;

    long mask = (1L << this.bits) - 1;

    long shift = this.dimensions * (this.bits - 1);
    shift |= shift >>> 1;
    shift |= shift >>> 2;
    shift |= shift >>> 4;
    shift |= shift >>> 8;
    shift |= shift >>> 16;
    shift |= shift >>> 32;
    shift -= shift >>> 1;

    ArrayList<Long> localMasks = new ArrayList<>();
    ArrayList<Long> localLShifts = new ArrayList<>();

    localMasks.add(mask);
    localLShifts.add(0L);

    while (shift > 0) {
      mask = 0;
      long shifted = 0;

      for (long bit = 0; bit < this.bits; bit++) {
        long distance = (dimensions * bit) - bit;
        shifted |= shift & distance;
        mask |= 1L << bit << ((-shift) & distance);
      }

      if (shifted != 0) {
        localMasks.add(mask);
        localLShifts.add(shift);
      }

      shift >>>= 1;
    }

    this.masks = new long[localMasks.size()];
    for (int i = 0; i < localMasks.size(); i++) {
      this.masks[i] = localMasks.get(i);
    }

    this.lshifts = new long[localLShifts.size()];
    for (int i = 0; i < localLShifts.size(); i++) {
      this.lshifts[i] = localLShifts.get(i);
    }

    this.rshifts = new long[localLShifts.size()];
    for (int i = 0; i < localLShifts.size() - 1; i++) {
      this.rshifts[i] = localLShifts.get(i + 1);
    }
    rshifts[rshifts.length - 1] = 0;
  }

  /**
   * Creates the z-value for a bunch of input values.
   *
   * @param values The values representing a point in space. This values will be used to compute the Z-value.
   * @return The z-value for the given values.
   */
  public long pack(long... values) throws ZOrderException {
    dimensionsCheck(values.length);
    for (int i = 0; i < values.length; i++) {
      valueCheck(values[i]);
    }

    long code = 0;
    for (int i = 0; i < values.length; i++) {
      code |= split(values[i]) << i;
    }
    return code;
  }

  /**
   * Creates the z-value for a bunch of input values, respecting the sign of the values.
   *
   * @param values The values representing a point in space. This values will be used to compute the Z-value.
   * @return The z-value for the given values.
   */
  public long spack(long... values) throws ZOrderException {
    long[] uvalues = new long[values.length];
    for (int i = 0; i < values.length; i++) {
      uvalues[i] = shiftSign(values[i]);
    }
    return pack(uvalues);
  }

  /**
   * Unpacks the given z-value, i.e., recreating the values used to create the z-value.
   *
   * @param code The code to be unpacked.
   * @return The values used to create the code (z-value).
   */
  public long[] unpack(long code) {
    long[] values = new long[(int) this.dimensions];
    for (int i = 0; i < values.length; i++) {
      values[i] = compact(code >> i);
    }
    return values;
  }

  /**
   * Unpacks the given z-value, i.e., recreating the values used to create the z-value.
   * It respecting a signed bit notion, i.e. the most significant bit indicates the sign.
   *
   * @param code The code to be unpacked.
   * @return The values used to create the code (z-value).
   */
  public long[] sunpack(long code) {
    long[] values = unpack(code);
    for (int i = 0; i < values.length; i++) {
      values[i] = unshiftSign(values[i]);
    }
    return values;
  }

  private void dimensionsCheck(long dimensions) throws ZOrderException {
    if (this.dimensions != dimensions) {
      throw new ZOrderException(String.format("morton64 with %d dimensions received %d values", this.dimensions, dimensions));
    }
  }

  //////////////////////////////////////////////// Private ///////////////////////////////////////////////////////////////////////

  private void valueCheck(long value) throws ZOrderException {
    if (value < 0 || value >= (1L << this.bits)) {
      throw new ZOrderException(String.format("morton64 with %d bits per dimension received %d to pack", this.bits, value));
    }
  }

  private long shiftSign(long value) throws ZOrderException {
    if (value >= (1L << (bits - 1)) || value <= -(1L << (bits - 1))) {
      throw new ZOrderException(String.format("morton64 with %d bits per dimension received signed %d to pack", this.bits, value));
    }

    if (value < 0) {
      value = -value;
      value |= 1L << (bits - 1);
    }
    return value;
  }

  private long unshiftSign(long value) {
    long sign = value & (1L << (bits - 1));
    value &= (1L << (bits - 1)) - 1;
    if (sign != 0) {
      value = -value;
    }
    return value;
  }

  private long split(long value) {
    for (int o = 0; o < masks.length; o++) {
      value = (value | (value << lshifts[o])) & masks[o];
    }
    return value;
  }

  private long compact(long code) {
    for (int o = masks.length - 1; o >= 0; o--) {
      code = (code | (code >>> rshifts[o])) & masks[o];
    }
    return code;
  }

  @Override
  public String toString() {
    return String.format("morton64{dimensions: %d, bits: %d}", dimensions, bits);
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) return true;
    if (!(obj instanceof ZOrder)) {
      return false;
    }
    if (!ZOrder.class.isAssignableFrom(obj.getClass())) {
      return false;
    }
    ZOrder other = (ZOrder) obj;

    return other.dimensions == dimensions && other.bits == bits;
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(dimensions + bits);
  }

  /**
   * {@link Exception} thrown whenever there is a problem while computing the Z-order.
   */
  public class ZOrderException extends Exception {
    public ZOrderException(String message) {
      super(message);
    }
  }

}
