package de.dfki.lt.hfc.indices.IntervalTree;

import de.dfki.lt.hfc.types.AnyType;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Christian Willms - Date: 30.10.17 09:33.
 * @version 30.10.17
 */
public class Interval<Key extends AnyType> implements Comparable {
    /**
     * Start of the interval in genomic coordinates -- this is exposed on purpose, getters have a significant
     * performance penalty for this field.
     */
    final Key  start;

    /**
     * End of the interval in genomic coordinates -- this is exposed on purpose, getters have a significant
     * performance penalty for this field.
     */
    final Key end;

    /**
     * Set of tuples representing the data for this interval
     */
    private Set<int[]> values = new HashSet<>();

    public Interval(Key start, Key end) {
//        assert start.compareTo(end) <= 0;
        if (start.compareTo(end)>0 ) throw new IllegalArgumentException("Start key of interval must be smaller then end key. Start: " + start.toString()+ " vs. End: " + end.toString() );
        this.start = start;
        this.end = end;
    }


    public Interval(Key start, Key end, int[] value) {
        assert start.compareTo(end) <= 0;
        this.start = start;
        this.end = end;
        this.values.add(value);
    }


    public boolean equals(Object other) {
        if (this == other)
            return true;
        if (this.getClass().equals(other.getClass())) {
            Interval otherInterval = (Interval) other;
            return (this.start == otherInterval.start &&
                    this.end == otherInterval.end);
        }
        return false;
    }


    public int hashCode() {
        return start.hashCode();
    }


    @Override
    public int compareTo(Object o) {
        Interval<Key> other = (Interval) o;
        if (this.start.compareTo(other.start) < 0)
            return -1;
        if (this.start.compareTo(other.start) > 0)
            return 1;

        if (this.end.compareTo(other.end) < 0)
            return -1;
        if (this.end.compareTo(other.end) > 0)
            return 1;

        return 0;
    }

    public String toString() {
        return "Interval[" + this.start + ", " + this.end + "]";
    }


    /**
     * @return whether this interval overlaps the other.
     */
    public boolean overlaps(Interval<Key> other) {
        return (this.start.compareTo(other.end) <= 0 &&
                other.start.compareTo(this.end) <= 0);
    }

    /**
     *
     * @param other
     * @param startMustBeEqual
     * @param endMustBeEqual
     * @return whether this interval contains the other
     */
    public boolean contains(Interval<Key> other, boolean startMustBeEqual, boolean endMustBeEqual) {
        if (!startMustBeEqual && !endMustBeEqual){
            return (this.start.compareTo(other.start) <= 0 && other.end.compareTo(this.end)<= 0);
        }
        if (startMustBeEqual && !endMustBeEqual)
            return (this.start.compareTo(other.start) == 0 && other.end.compareTo(this.end)<= 0);
        if (!startMustBeEqual && endMustBeEqual)
            return (this.start.compareTo(other.start) <= 0 && other.end.compareTo(this.end) == 0);

        return (this.start.compareTo(other.start)==0 && other.end.compareTo(this.end)== 0);
    }

    /**
     * @return The file block for this interval
     */
    public Set<int[]> getValues() {
        return values;
    }

    public void addValues(Set values) {
        this.values.addAll(values);
    }
}
