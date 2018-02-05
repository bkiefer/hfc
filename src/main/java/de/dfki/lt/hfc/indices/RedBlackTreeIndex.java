package de.dfki.lt.hfc.indices;

import de.dfki.lt.hfc.types.AnyType;
import gnu.trove.set.hash.THashSet;

import java.util.*;

/**
 * The simplest index supported by HFC. It uses a simple red-black tree ({@link TreeMap}) as indexing structure.
 * The simpleIndex does not support Interval specific functions such as removing and adding of intervals.
 * <p>
 * Created by christian on 04/03/17.
 */
public class RedBlackTreeIndex extends Index {


    private final TreeMap<Comparable, Set<int[]>> map;

    /**
     * Creates an instance of {@link RedBlackTreeIndex}.
     */
    public RedBlackTreeIndex(Class key, int start, int end) {
        super(key, start, end);
        this.map = new TreeMap<Comparable, Set<int[]>>();
    }


    @Override
    protected void structureSpecificAdd(AnyType key, int[] value) {
        Set<int[]> values;
        if (this.map.containsKey(key))
            values = (HashSet<int[]>) this.map.get(key);
        else
            values = new HashSet<>();
        values.add(value);
        this.map.put(key, values);
    }

    @Override
    protected Set<int[]> structureSpecificRemove(AnyType key, int[] value) {
        Set<int[]> oldValue = this.map.get(key);
        if (value == null)
            this.map.remove(key);
        else {
            HashSet<int[]> values = (HashSet<int[]>) this.map.get(key);
            if (values != null) {
                values.remove(value);
                this.map.replace(key, values);
            }
        }
        return oldValue;
    }

    @Override
    public Set<int[]> structureSpecificSearch(AnyType key) {
        return this.map.get(key);
    }


    private Set<int[]> searchIntervalWithEqualityConstraints(Comparable start, Comparable end, boolean startMustMatch, boolean endMustMatch) {
        Set matches = new THashSet();
        // Get a set of the entries
        Set set = this.map.entrySet();

        // Get an iterator
        Iterator it = set.iterator();

        // Display elements
        while (it.hasNext()) {
            Map.Entry me = (Map.Entry) it.next();
            if (greater((Comparable) me.getKey(), end)) break;
            if (startMustMatch && endMustMatch) {
                if (eq((Comparable) me.getKey(), start) && eq((Comparable) me.getKey(), end)) {
                    matches.addAll((Collection) me.getValue());
                }
            } else {
                if (startMustMatch) {
                    if (eq((Comparable) me.getKey(), start) && !greater((Comparable) me.getKey(), end)) {
                        matches.addAll((Collection) me.getValue());
                    }
                } else {
                    if (endMustMatch) {
                        if (!less((Comparable) me.getKey(), start) && eq((Comparable) me.getKey(), end)) {
                            matches.addAll((Collection) me.getValue());
                        }
                    } else {
                        if (!less((Comparable) me.getKey(), start) && !greater((Comparable) me.getKey(), end)) {
                            matches.addAll((Collection) me.getValue());
                        }
                    }
                }
            }
        }

        return matches;
    }

    @Override
    public Set<int[]> structureSpecificIntervalSearch(AnyType start, AnyType end) {
        return searchIntervalWithEqualityConstraints(start, end, false, false);
    }




    @Override
    public String toString() {
        StringBuilder bld = new StringBuilder();
        for (Map.Entry e : this.map.entrySet()) {
            bld.append(e.getKey() + ":" + e.getValue() + "\n");
        }
        return bld.toString();
    }

    @Override
    public long size() {
        return this.map.size();
    }

    /**
     * This function always returns -1 as we cannot determine the depth of the used TreeMap.
     * However it is worth noting that the depth of a TreeMap, i.e. a Red-Black Tree, is approximately ceiling(log2(table.size())).
     *
     * @return
     */
    @Override
    public int height() {
        return -1;
    }

    @Override
    public void clear() {
        this.map.clear();
        this.numberOfIndexedTuples = 0;
    }

    // comparison functions - make Comparable instead of Key to avoid casts
    private boolean less(Comparable k1, Comparable k2) {
        return k1.compareTo(k2) < 0;
    }

    private boolean eq(Comparable k1, Comparable k2) {
        return k1.compareTo(k2) == 0;
    }

    private boolean greater(Comparable k1, Comparable k2) {
        return k1.compareTo(k2) > 0;
    }
}
