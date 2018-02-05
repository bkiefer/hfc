package de.dfki.lt.hfc.indices;

import de.dfki.lt.hfc.indices.intervaltree_deprecated.Interval;
import de.dfki.lt.hfc.indices.intervaltree_deprecated.IntervalTree;
import de.dfki.lt.hfc.types.AnyType;
import gnu.trove.set.hash.THashSet;

import java.util.*;

/**
 * The interval Tree index is especially suited to handle intervals. Its implementation is based on a self balancing b-Tree.
 * <p>
 * Created by chwi02 on 19.03.17.
 */
@Deprecated
public class    IntervalTreeIndex_Deprecated<Key extends AnyType> extends AdvancedIndex {

    private final IntervalTree tree = new IntervalTree<Key>();

    /**
     * Creates a new instance of {@link IntervalTreeIndex_Deprecated}.
     *
     * @param key The class (AnySimpleType) used as key of the index.
     */
    public IntervalTreeIndex_Deprecated(Class key, int start, int end) {
        super(key,start, end);
    }

    @Override
    protected void structureSpecificAdd(AnyType key, int[] value) {
        addInterval(key,key,value);
    }

    @Override
    protected Set<int[]> structureSpecificRemove(AnyType key, int[] value) {
        return this.tree.remove(new Interval(key,key,value));
    }

    @Override
    public Set<int[]> structureSpecificSearch(AnyType key) {
        Set<int[]> res = new HashSet<int[]>();
        Set<Interval<Key>> intervals  = this.tree.searchInterval(new Interval(key,key, null));
        for (Interval<Key> i : intervals)
            res.addAll(i.getValue());
        return res;
    }

    @Override
    public Set<int[]> searchIntervalWithEqualityConstraints(AnyType start, AnyType end, boolean startMustMatch, boolean endMustMatch) {
        Set<int[]> result = new THashSet<>();
        Interval interval;
        for (Object o : tree.searchInterval(new Interval(start, end, null))) {
            interval = (Interval) o;
            if (startMustMatch && endMustMatch) {
                if (interval.getStart().compareTo(start) == 0 && interval.getEnd().compareTo(end) == 0)
                    result.addAll(interval.getValue());
            } else {
                if (startMustMatch) {
                    if (interval.getStart().compareTo(start) == 0 && interval.getEnd().compareTo(end) <= 0)
                        result.addAll(interval.getValue());
                } else {
                    if (endMustMatch) {
                        if (interval.getStart().compareTo(start) >= 0 && interval.getEnd().compareTo(end) == 0)
                            result.addAll(interval.getValue());
                    } else {
                        if ((interval.getStart().compareTo(start) >= 0 && interval.getEnd().compareTo(end) <= 0))
                            result.addAll(interval.getValue());
                    }
                }
            }
        }
        return result;
    }

    @Override
    public Set<int[]> structureSpecificIntervalSearch(AnyType start, AnyType end) {
        Set<int[]> result = new THashSet<>();
        Interval interval;
        for (Object o : tree.searchInterval(new Interval(start, end, null))) {
            interval = (Interval) o;
            if ((interval.getStart().compareTo(start) >= 0 && interval.getEnd().compareTo(end) <= 0))
                result.addAll(interval.getValue());

        }
        return result;
    }



    @Override
    public Set<int[]> searchIntervalsIncluding(AnyType start, AnyType end, boolean startMustBeEqual, boolean endMustBeEqual) {
        Set<int[]> result = new HashSet<>();
        Interval interval;
        if (startMustBeEqual && endMustBeEqual)
            return searchIntervalWithEqualityConstraints(start, end, true, true);
        else {
            for (Object o : tree.searchIntervalsIncluding(new Interval(start, end, null))) {
                interval = (Interval) o;
                if (startMustBeEqual) {
                    if (interval.getLower().compareTo(start) == 0) {
                        result.addAll(interval.getValue());
                    }
                } else {
                    if (endMustBeEqual) {
                        if (interval.getUpper().compareTo(end) == 0) {
                            result.addAll(interval.getValue());
                        }
                    } else {
                        result.addAll(interval.getValue());
                    }
                }
            }
        }
        return result;
    }

    @Override
    public Set<int[]> removeInterval(AnyType intervalBegin, AnyType interval_End) {
        Interval interval = new Interval(intervalBegin, interval_End, null);
        Set<int[]> res = this.tree.remove(interval);
        return res;
    }

    @Override
    public void addInterval(AnyType intervalBegin, AnyType intervalEnd, int[] value) {
        Interval iNew = new Interval(intervalBegin, intervalEnd, null);
        for (Object o : this.tree.searchInterval(new Interval(intervalBegin, intervalEnd, null))) {
            Interval i = (Interval) o;

            if ((i.getStart().compareTo(intervalBegin) == 0 && i.getEnd().compareTo(intervalEnd) == 0)) {
                i.addValue(Collections.singleton(value));
                return;
            }
        }
        HashSet newValue = new HashSet();
        newValue.add(value);
        iNew.addValue(newValue);
        this.tree.add(iNew);
    }

    @Override
    public String toString() {
        return this.tree.toString();
    }

    @Override
    public long size() {
        return this.tree.size();
    }

    @Override
    public int height() {
        return this.tree.verifyHeight();
    }

    @Override
    public void clear() {
        this.tree.clear();
    }
}
