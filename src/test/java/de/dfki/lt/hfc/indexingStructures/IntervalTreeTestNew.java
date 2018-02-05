package de.dfki.lt.hfc.indexingStructures;

import de.dfki.lt.hfc.indices.IndexingException;
import de.dfki.lt.hfc.indices.IntervalTree.Interval;
import de.dfki.lt.hfc.indices.IntervalTree.IntervalTree;
import de.dfki.lt.hfc.types.AnyType;
import de.dfki.lt.hfc.types.XsdInt;
import org.junit.jupiter.api.Test;

import java.util.*;

/**
 * @author Christian Willms - Date: 30.10.17 15:14.
 * @version 30.10.17
 */
public class IntervalTreeTestNew extends IndexStructureTest {

    private static IntervalTree<AnyType> iTree = new IntervalTree<>();

    private final Map<AnyType, Set<int[]>> mMap = new TreeMap<AnyType, Set<int[]>>();

    private final Map<IntervalT, Set<int[]>> iMap = new TreeMap<>();


    @Override
    protected void add(AnyType key, Set<int[]> value) {
        // This is necessary to model the behavior of the tree
        if (mMap.containsKey(key)) {
            mMap.get(key).addAll(value);
        } else {
            mMap.put(key, value);
        }
        for (int[] v : value)
            iTree.insert(new Interval(key, key, v));
    }

    protected void addInterval(AnyType start, AnyType end, Set<int[]> value) {
        IntervalT key = new IntervalT(start, end);
        if (iMap.containsKey(key)) {
            iMap.get(key).addAll(value);
        } else {
            iMap.put(key, value);
        }
        for (int[] v : value)
            iTree.insert(new Interval(start, end, v));

    }

    @Override
    protected void clearData() {
        iTree.clear();
        mMap.clear();
        iMap.clear();
    }


    /**
     * Rewritten as original one took about an hour with the interval tree.
     * @throws IndexingException
     */
    @Override
    @Test
    public void validateTestCase5() throws IndexingException {
        clearData();
        addRandomKeys(-1000,10000, 40000);
        for (int i = 666; i < 12220; ++i) {
            validateSearch(new XsdInt(i));
        }
        validateAll();
    }

    @Override
    protected void validateData() throws IndexingException {
        for (Map.Entry<AnyType, Set<int[]>> entry : mMap.entrySet()) {
            try {
                Set<int[]> val = iTree.findOverlapping(new Interval(entry.getKey(), entry.getKey()));
                if (!entry.getValue().equals(val)) {
                    throw new IndexingException("Error in validateData(): Failed to compare value for key = " + entry.getKey() + " - " + entry.getValue() + " <> " + val);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                throw new IndexingException("Runtime Error in validateData(): Failed to compare value for key = " + entry.getKey() + " msg = " + ex.getMessage());
            }
        }
    }

    @Override
    protected void validateSearch(AnyType key) throws IndexingException {
        Set<int[]> val1 = mMap.get(key);
        if (val1 == null) {
            val1 = Collections.emptySet();
        }
        Set<int[]> val2 = iTree.findOverlapping(new Interval(key, key));
        if (!((val1 == null) && (val2 == null)))
            if (!val1.equals(val2)) {
                throw new IndexingException("Error in validateSearch(): Failed to compare value for key = " + key);
            }
    }


    private void validateIntervalSearch_Overlap(XsdInt start, XsdInt end) throws IndexingException {
        Set<int[]> val1 = new HashSet<>();
        for (Map.Entry<IntervalT, Set<int[]>> entry : iMap.entrySet()) {
            IntervalT key = entry.getKey();
            if (start.compareTo(key.end) <= 0 &&
                    key.start.compareTo(end) <= 0)
                val1.addAll(entry.getValue());
        }
        Set<int[]> val2 = iTree.findOverlapping(new Interval(start, end));
        if (!((val1 == null) && (val2 == null)))
            if (!val1.equals(val2)) {
                throw new IndexingException("Error in validateSearch(): Failed to compare value for keys = " + start.toString(true) + " and " + end.toString(true));
            }
    }

    private void validateIntervalSearch_During(XsdInt start, XsdInt end, boolean startEqual, boolean endEqual) throws IndexingException {
        Set<int[]> val1 = new HashSet<>();
        for (Map.Entry<IntervalT, Set<int[]>> entry : iMap.entrySet()) {
            IntervalT key = entry.getKey();
            if (!startEqual && !endEqual){
                if (key.start.compareTo(start) >= 0 && key.end.compareTo(end) <= 0) {
                    val1.addAll(entry.getValue());
                }
            }
            else if (startEqual && !endEqual){
                if (key.start.compareTo(start) == 0 && key.end.compareTo(end) <= 0) {
                    val1.addAll(entry.getValue());
                }
            }
            else if (!startEqual && endEqual){
                if (key.start.compareTo(start) >= 0 && key.end.compareTo(end) == 0) {
                    val1.addAll(entry.getValue());
                }
            }
            else
                if (key.start.compareTo(start) == 0 && key.end.compareTo(end) == 0) {
                    val1.addAll(entry.getValue());
                }
        }
        Set<int[]> val2 = iTree.findWithEquality(new Interval(start, end), startEqual, endEqual);
        if (!((val1 == null) && (val2 == null)))
            if (!val1.equals(val2)) {
                throw new IndexingException("Error in validateSearch(): Failed to compare value for keys = " + start.toString(true) + " and " + end.toString(true));
            }
    }

    private void validateIntervalSearch_Containing(XsdInt start, XsdInt end) throws IndexingException {
        Set<int[]> val1 = new HashSet<>();
        for (Map.Entry<IntervalT, Set<int[]>> entry : iMap.entrySet()) {
            IntervalT key = entry.getKey();
            if (key.start.compareTo(start) <= 0 && key.end.compareTo(end) >= 0) {
                val1.addAll(entry.getValue());
            }
        }
        Set<int[]> val2 = iTree.findIntervalsContaining(new Interval(start, end), false, false);
        if (!((val1 == null) && (val2 == null)))
        if (!val1.equals(val2)) {
            throw new IndexingException("Error in validateSearch(): Failed to compare value for keys = " + start.toString(true) + " and " + end.toString(true));
        }
    }

    public void addRandomInterval(int min, int max, int iterations) {
        int minNum = min;
        int maxNum = max;
        int nVal, nVal2;
        for (int i = 0; i < iterations; ++i) {
            nVal = randInt(minNum, maxNum);
            nVal2 = randInt(nVal, maxNum);
            addInterval(new XsdInt(nVal), new XsdInt(nVal2), new HashSet<>(Arrays.asList(new int[]{nVal, nVal, nVal2, nVal2})));
        }
    }


    @Test
    public void validateTestCase_Overlap() throws IndexingException {
        clearData();
        addRandomInterval(0, 1000, 4000);
        int start, end;
        for (int i = -10; i < 1000; ++i) {
            start = randInt(-10, 1000);
            end = randInt(start, 1000);
            validateIntervalSearch_Overlap(new XsdInt(start), new XsdInt(end));
        }
    }

    @Test
    public void validateTestCase_DuringManual() throws IndexingException {
        clearData();
        HashSet<int[]> data1 = new HashSet();
        data1.add(new int[]{10, 11, 12, 13, 14, 15});
        HashSet<int[]> data2 = new HashSet();
        data2.add(new int[]{12, 13, 14});
        HashSet<int[]> data3 = new HashSet();
        data3.add(new int[]{7, 8, 9, 10, 11});
        addInterval(new XsdInt(10), new XsdInt(15), data1);
        addInterval(new XsdInt(12), new XsdInt(14), data2);
        addInterval(new XsdInt(7), new XsdInt(11), data3);
        validateIntervalSearch_During(new XsdInt(5), new XsdInt(16), false, false);

        HashSet<int[]> data4 = new HashSet();
        data4.add(new int[]{14, 15, 16, 17, 18, 19, 20});
        addInterval(new XsdInt(14), new XsdInt(20), data4);
        validateIntervalSearch_During(new XsdInt(9), new XsdInt(24), false, false);

        HashSet<int[]> data5 = new HashSet();
        data5.add(new int[]{14, 15, 16, 17, 18, 19, 20, 21});
        addInterval(new XsdInt(14), new XsdInt(21), data5);
        validateIntervalSearch_During(new XsdInt(13), new XsdInt(24), false, false);
    }


    @Test
    public void validateTestCase_ContainingManual() throws IndexingException {
        clearData();
        HashSet<int[]> data1 = new HashSet();
        data1.add(new int[]{10, 11, 12, 13, 14, 15});
        HashSet<int[]> data2 = new HashSet();
        data2.add(new int[]{12, 13, 14});
        HashSet<int[]> data3 = new HashSet();
        data3.add(new int[]{7, 8, 9, 10, 11});
        addInterval(new XsdInt(10), new XsdInt(15), data1);
        addInterval(new XsdInt(12), new XsdInt(14), data2);
        addInterval(new XsdInt(7), new XsdInt(11), data3);
        validateIntervalSearch_Containing(new XsdInt(11), new XsdInt(14));

        HashSet<int[]> data4 = new HashSet();
        data4.add(new int[]{14, 15, 16, 17, 18, 19, 20});
        addInterval(new XsdInt(14), new XsdInt(20), data4);
        validateIntervalSearch_Containing(new XsdInt(9), new XsdInt(24));

        HashSet<int[]> data5 = new HashSet();
        data5.add(new int[]{14, 15, 16, 17, 18, 19, 20, 21});
        addInterval(new XsdInt(14), new XsdInt(21), data5);
        validateIntervalSearch_Containing(new XsdInt(13), new XsdInt(24));

        HashSet<int[]> data6 = new HashSet<>();
        data6.add(new int[]{19});
        addInterval(new XsdInt(19), new XsdInt(19), data6);
        validateIntervalSearch_Containing(new XsdInt(19), new XsdInt(19));
    }

    @Test
    public void validateTestCase_During_False_False() throws IndexingException {
        clearData();
        addRandomInterval(0, 1000, 4000);
        int start, end;
        for (int i = -10; i < 1000; ++i) {
            start = randInt(-10, 1000);
            end = randInt(start, 1000);
            validateIntervalSearch_During(new XsdInt(start), new XsdInt(end), false, false);
        }
    }


    @Test
    public void validateTestCase_Including_False_True() throws IndexingException {
        clearData();
        addRandomInterval(0, 1000, 4000);
        int start, end;
        for (int i = -10; i < 1000; ++i) {
            start = randInt(-10, 1000);
            end = randInt(start, 1000);
            validateIntervalSearch_During(new XsdInt(start), new XsdInt(end), false, true);
        }
    }

    @Test
    public void validateTestCase_Including_True_False() throws IndexingException {
        clearData();
        addRandomInterval(0, 1000, 4000);
        int start, end;
        for (int i = -10; i < 1000; ++i) {
            start = randInt(-10, 1000);
            end = randInt(start, 1000);
            validateIntervalSearch_During(new XsdInt(start), new XsdInt(end), true, false);
        }
    }

    @Test
    public void validateTestCase_Including_True_True() throws IndexingException {
        clearData();
        addRandomInterval(0, 1000, 4000);
        int start, end;
        for (int i = -10; i < 1000; ++i) {
            start = randInt(-10, 1000);
            end = randInt(start, 1000);
            validateIntervalSearch_During(new XsdInt(start), new XsdInt(end), true, true);
        }
    }

    @Test
    public void validateTestCase_Containing() throws IndexingException {
        clearData();
        addRandomInterval(0, 1000, 4000);
        int start, end;
        for (int i = -10; i < 1000; ++i) {
            start = randInt(-10, 1000);
            end = randInt(start, 1000);
            validateIntervalSearch_Containing(new XsdInt(start), new XsdInt(end));
        }
    }

}
