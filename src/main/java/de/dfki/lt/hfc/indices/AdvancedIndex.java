package de.dfki.lt.hfc.indices;

import de.dfki.lt.hfc.types.AnyType;

import java.util.Set;

/**
 * An advanced index is generally specialized to perform interval or range related lookups
 * and is thus not that effective when performing classical lookup operations.
 * <p>
 * Created by chwi02 on 19.03.17.
 */
public abstract class AdvancedIndex<K extends AnyType> extends Index {

  /**
   * Creates a new instance of {@link AdvancedIndex}.
   *
   * @param key   a class representing the type of the used keys
   * @param start the id representing the lower end of the interval represented by the root node.
   * @param end   the id representing the higher end of the interval represented by the root node.
   */
  public AdvancedIndex(Class key, int start, int end) {
    super(key, start, end);
  }

  /**
   * Adds a new interval-value mapping to the index. The interval is defined by the given start and end keys.
   *
   * @param start The lower end of the interval.
   * @param end   The upper end of the interval.
   * @param v     the value associated with the interval.
   */
  public abstract void addInterval(K start, K end, int[] v);


  /**
   * Remove the given interval and all values associated with this interval from the index.
   *
   * @param start The lower end of the interval.
   * @param end   The upper end of the interval.
   * @return The values associated with the removed interval.
   */
  public abstract Set<int[]> removeInterval(K start, K end);

  @Override
  public boolean intervalSupport() {
    return true;
  }

  /**
   * This method searches the index for the values of all intervals [i_s, i_e]
   * such that start <= i_s <= i_e  <= end.
   *
   * @param start            The key representing the starting point of the interval.
   * @param end              The key representing the ending point of the interval.
   * @param startMustBeEqual a boolean value indicating that only values of intervals are returned
   *                         where i_s == start
   * @param endMustBeEqual   a boolean value indicating that only values of intervals are returned
   *                         where i_e == end
   * @return The Values associated with all matching entries, represented as a set of objects.
   */
  public abstract Set<int[]> searchIntervalWithEqualityConstraints(K start, K end, boolean startMustBeEqual, boolean endMustBeEqual);

  /**
   * This method searches the index for the values of all intervals [i_s, i_e]
   * that fully contain the given interval.
   * i_s <= start <= end <= i_e .
   *
   * @param start The key representing the starting point of the interval.
   * @param end   The key representing the ending point of the interval.
   * @return The Values associated with all matching entries, represented as a set of objects.
   */
  public abstract Set<int[]> searchIntervalsIncluding(K start, K end, boolean startMustBeEqual, boolean endMustBeEqual);

}
