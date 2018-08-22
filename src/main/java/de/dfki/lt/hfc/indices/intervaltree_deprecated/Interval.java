package de.dfki.lt.hfc.indices.intervaltree_deprecated;


import java.util.HashSet;
import java.util.Set;

/**
 * @param <Key>
 */
public class Interval<Key extends Comparable> implements Comparable<Interval> {

  private Key start;
  private Key end;
  private Set<int[]> value = new HashSet<>();

  public Interval(Key start, Key end, int[] value) {
    this.start = start;
    this.end = end;
    // It is possible that a interval is created with null value. these are usually used as placeholders.
    if (value != null)
      this.value.add(value);

  }

  public Key getStart() {
    return this.start;
  }

  public void setStart(Key start) {
    this.start = start;
  }

  public Key getEnd() {
    return this.end;
  }

  public void setEnd(Key end) {
    this.end = end;
  }

  public void addValue(Set<int[]> value) {
    if (this.value == null)
      this.value = value;
    else
      this.value.addAll(value);
  }

  public String toString() {
    return "[" + this.getStart() + ", " + this.getEnd() + "]";
  }

  public Set<int[]> getValue() {
    return this.value;
  }

  public Key getLower() {
    return start;
  }

  public Key getUpper() {
    return end;
  }

  @Override
  public int compareTo(Interval i) {
    if (this.start.compareTo(i.start) < 0) {
      return -1;
    } else if (this.start == i.start) {
      return this.end.compareTo(i.end) <= 0 ? -1 : 1;
    } else {
      return 1;
    }
  }


}
