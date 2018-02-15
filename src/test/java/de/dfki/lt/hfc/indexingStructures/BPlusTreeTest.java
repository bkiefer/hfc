package de.dfki.lt.hfc.indexingStructures;

import de.dfki.lt.hfc.indices.bplustree.BPlusTree;

import de.dfki.lt.hfc.indices.IndexingException;
import de.dfki.lt.hfc.types.AnyType;

import java.util.*;

/**
 * Created by chwi02 on 11.02.17.
 */
public class BPlusTreeTest extends IndexStructureTest{

    private static BPlusTree<AnyType> bTree = new BPlusTree<AnyType>();

    private final Map<AnyType, Set<int[]>> mMap = new TreeMap<AnyType, Set<int[]>>();

    @Override
    protected void add(AnyType key, Set<int[]> value) {
        // This is necessary to model the behavior of the tree
        if (mMap.containsKey(key)){
            mMap.get(key).addAll(value);
        } else {
            mMap.put(key, value);
        }
        for (int[] v: value)
            bTree.insert(key, v);
    }

//    @Override
//    protected void delete(AnyType key) throws IndexingException{
//        System.out.println("Delete key = " + key);
//        Set<int[]> val1 = mMap.remove(key);
//        Set<int[]> val2 = bTree.delete(key);
//        if (!((val1 == null) && (val2 == null)))
//            if (!val1.equals(val2)) {
//                throw new IndexingException("Deleted key = " + key + " has different values: " + val1 + " | " + val2);
//         }
//    }

    @Override
    protected void clearData() {
        bTree.clear();
        mMap.clear();
    }

//    @Override
//    protected void validateIntervalSearch(AnyType[] keys) throws IndexingException{
//        Set<int[]> val1 = new HashSet<>();
//        if (keys[0].compareTo(keys[keys.length-1])<=0)
//            for (AnyType k : keys){
//                //System.out.println("Look for " + k);
//                if (mMap.containsKey(k))
//                    val1.addAll(mMap.get(k));
//            }
//        Set<int[]> val2 = bTree.getInterval(keys[0],keys[keys.length-1]);
//        if (!((val1 == null) && (val2 == null)))
//            if (!val1.equals(val2)) {
//                throw new IndexingException("Error in validateSearch(): Failed to compare value for key = " + Arrays.toString(keys) + "val1 = " +val1 + " val2 = " + val2 );
//            }
//    }

    @Override
    protected void validateSearch(AnyType key) throws IndexingException {
        Set<int[]> val1 = mMap.get(key);
        Set<int[]> val2 = bTree.search(key);
        if (!((val1 == null) && (val2 == null)))
            if (!val1.equals(val2)) {
                throw new IndexingException("Error in validateSearch(): Failed to compare value for key = " + key);
            }
    }

    @Override
    protected void validateData() throws IndexingException {
        for (Map.Entry<AnyType, Set<int[]>> entry : mMap.entrySet()) {
            try {
                //System.out.println("Search key = " + entry.getKey());
                Set<int[]> val = bTree.search(entry.getKey());
                if (!entry.getValue().equals(val)) {
                    throw new IndexingException("Error in validateData(): Failed to compare value for key = " + entry.getKey() + " - " +entry.getValue() + " <> " + val);
                }
            }
            catch (Exception ex) {
                ex.printStackTrace();
                throw new IndexingException("Runtime Error in validateData(): Failed to compare value for key = " + entry.getKey() + " msg = " + ex.getMessage());
            }
        }
    }


}
