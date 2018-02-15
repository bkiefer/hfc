package de.dfki.lt.hfc.indices.bplustree;


import java.util.Set;

/**
 * TODO implement searchIntervalWithEqualityConstraints
 * TODO adapt leaf node update
 *
 * The B tree was first described in the paper
 * Organization and Maintenance of Large Ordered indices. Acta Informatica 1: 173–189 (1972)
 * by Rudolf Bayer and Edward M. McCreight.
 *
 * The most commonly implemented form of the B-Tree is the B+ Tree. The difference between them is that
 * the internal nodes of B+ tree do not store records, they are used for navigation only.
 * A B+ Tree of order m has these properties:
 * - The root is either a leaf or has at least two children;
 * - Each internal node, except for the root, has between ⎡m/2⎤ and m children;
 * - Internal nodes do not store record, only store key values to guild the search;
 * - Each leaf node, has between ⎡m/2⎤ and m keys and values;
 * - Leaf node store keys and records or pointers to records;
 * - All eaves are at the same level in the tree, so the tree is always height balanced.
 *
 * Created by christian on 10/01/17.
 */
public class BPlusTree<K extends Comparable> {
    private Node<K> root;
    private long size = 0;
    private int height = 0;

    public BPlusTree() {
        this.root = new LeafNode<>();
    }

    /**
     * Insert a new k and its associated value into the B+tree.
     *
     * @param k
     *          The k to be added.
     * @param value
     *          The value associated with the k.
     */
    public void insert(K k, int[] value) {
        LeafNode<K> leaf = this.findLeafNodeShouldContainKey(k);
        leaf.insertKey(k, value);
        if (leaf.isOverflow()) {
            Node<K> n = leaf.dealOverflow();
            if (n != null){
                this.root = n;
                height++;}
        }
    }

    /**
     * Search a k value on the tree and return its associated value.
     *
     * @param k
     *          The k to be searched
     * @return
     *          The values associated with the k.
     */
    public Set<int[]> search(K k) {
        LeafNode<K> leaf = this.findLeafNodeShouldContainKey(k);
        int index = leaf.search(k);
        return (index == -1) ? null : leaf.getValue(index);
    }

    /**
     * Delete a k and its associated value from the tree.
     */
    public Set<int[]> delete(K k) {
        LeafNode<K> leaf = this.findLeafNodeShouldContainKey(k);

        Set<int[]> values = leaf.delete(k);
        if (values != null && leaf.isUnderflow()) {
            Node<K> n = leaf.dealUnderflow();
            if (n != null){
                this.root = n;
                height--;
            }
        }
        return values;
    }

    /**
     * Search the leaf node which should contain the specified k
     */
    @SuppressWarnings("unchecked")
    private LeafNode<K> findLeafNodeShouldContainKey(K k) {
        Node<K> node = this.root;
        while (node.getNodeType() == TreeNodeType.InnerNode) {
            node = ((InnerNode<K>)node).getChild( node.search(k) );
        }

        return (LeafNode<K>)node;
    }

    //TODO fix me!
    public long size() {
        return this.size;
    }

    public int height(){
        return this.height;
    }

    public void clear() {
        this.root = new LeafNode<>();
        this.height= 0;
        this.size = 0;
    }

    /**
     * Searches all values associated with keys that are within the given interval.
     *
     * @param start The key where the interval starts, must be smaller than the end key.
     * @param end The key where the interval ends, must be bigger than the start key.
     * @return
     *          All values associated with keys that are equal or greater then the start key and smaller or equal than the end key.
     */
    public Set<int[]> getInterval(K start, K end) {
        LeafNode s = findLeafNodeShouldContainKey(start);
        return s.searchForInterval(start,end);
    }

}