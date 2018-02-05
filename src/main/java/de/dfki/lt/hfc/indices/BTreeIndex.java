package de.dfki.lt.hfc.indices;


import de.dfki.lt.hfc.indices.btree.BTree;
import de.dfki.lt.hfc.types.AnyType;
import gnu.trove.set.hash.THashSet;

import java.util.Set;

/**
 * This kind of {@link Index} is mainly suited for temporal data, but can also be used for spatial data in case these are linearized (Z-order Curve).
 * The performance for a simple lookup is quite good, for intervals it is not that good but nevertheless far better than having no index at all. ;)
 *
 * A B-tree of order m is an m-ary search tree with the following properties:
 *  - The root is either a leaf or has at least two children
 *  - Each node, except for the root and the leaves, has between ⎡m/2⎤ and m children
 *  - Each path from the root to a leaf has the same length
 *  - Each internal node has up to (m - 1) key values and up to m pointers (to children)
 *  - The records are stored in leaves and in internal nodes
 *
 * Created by christian on 21/12/16.
 */
public class BTreeIndex extends Index{

    /**
     * Instance of btree
     */
    private final BTree tree;

    /**
     * Creates a new instance of {@link BTreeIndex}
     * @param key The class (AnySimpleType) used as key of the index.
     */
    public BTreeIndex(Class key, int start, int end){
        super(key, start, end);
        this.tree = new BTree();

    }

    @Override
    protected void structureSpecificAdd(AnyType key, int[] value) {
        Set t = tree.search(key);
        if (t != null)
            t.add(value);
        else {
            t = new THashSet();
            t.add(value);
        }
        this.tree.insert(key, t);
    }



    @Override
    protected Set<int[]> structureSpecificRemove(AnyType key, int[] value) {
           return this.tree.delete(key);
    }

    @Override
    public Set<int[]> structureSpecificSearch(AnyType key) {
        return  this.tree.search(key);
    }



    @Override
    public Set<int[]> structureSpecificIntervalSearch(AnyType start, AnyType end) {
        return tree.searchInterval(start, end);
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
        return this.tree.height();
    }

    @Override
    public void clear() {
        this.tree.clear();
        this.numberOfIndexedTuples = 0;
    }

}
