package de.dfki.lt.hfc.indexingStructures;

import de.dfki.lt.hfc.indices.IndexingException;
import de.dfki.lt.hfc.indices.btree.BTree;
import de.dfki.lt.hfc.types.AnyType;
import gnu.trove.set.hash.THashSet;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * @author Christian Willms - Date: 30.10.17 15:01.
 * @version 30.10.17
 */
public class BTreeTest extends IndexStructureTest {

    private static BTree<AnyType> bTree = new BTree<AnyType>();

    private final Map<AnyType, Set<int[]>> mMap = new TreeMap<AnyType, Set<int[]>>();


    @Override
    protected void add(AnyType key, Set<int[]> value) {
        // This is necessary to model the behavior of the tree
        if (mMap.containsKey(key)){
            mMap.get(key).addAll(value);
        } else {
            mMap.put(key, value);
        }
        Set t = bTree.search(key);
        if (t != null)
            t.addAll(value);
        else {
            t = new THashSet();
            t.addAll(value);
        }
        this.bTree.insert(key, t);
    }


    @Override
    protected void clearData() {
        bTree.clear();
        mMap.clear();
    }

    @Override
    protected void validateData() throws IndexingException {
        for (Map.Entry<AnyType, Set<int[]>> entry : mMap.entrySet()) {
            try {
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

    @Override
    protected void validateSearch(AnyType key) throws IndexingException {
        Set<int[]> val1 = mMap.get(key);
        Set<int[]> val2 = bTree.search(key);
        if (!((val1 == null) && (val2 == null)))
            if (!val1.equals(val2)) {
                throw new IndexingException("Error in validateSearch(): Failed to compare value for key = " + key);
            }
    }


}
