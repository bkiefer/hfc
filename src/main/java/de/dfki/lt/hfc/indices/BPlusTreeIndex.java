package de.dfki.lt.hfc.indices;

import de.dfki.lt.hfc.indices.bplustree.BPlusTree;
import de.dfki.lt.hfc.types.AnyType;

import java.util.Set;

/**
 * This kind of {@link Index} is mainly suited for temporal data, but can also be used for spatial data in case these are linearized (Z-order Curve).
 * The performance for a simple lookup is quite good, for intervals it is not that good but nevertheless far better than having no index at all. ;)
 * <p>
 * The B tree was first described in the paper Organization and Maintenance of Large Ordered indices. Acta Informatica 1: 173–189 (1972) by Rudolf Bayer and Edward M. McCreight.
 * A B+ Tree of order m has these properties:
 * - The root is either a leaf or has at least two children;
 * - Each internal node, except for the root, has between ⎡m/2⎤ and m children;
 * - Internal nodes do not store record, only store key values to guild the search;
 * - Each leaf node, has between ⎡m/2⎤ and m keys and values;
 * - Leaf node store keys and records or pointers to records;
 * - All eaves are at the same level in the tree, so the tree is always height balanced.
 * <p>
 * Created by christian on 11/01/17.
 */
public class BPlusTreeIndex extends Index {


  private final BPlusTree tree;

  /**
   * Creates a new instance of BPlusTreeIndex
   *
   * @param key The class (AnySimpleType) used as key of the index.
   */
  public BPlusTreeIndex(Class key, int start, int end) {
    super(key, start, end);
    this.tree = new BPlusTree();
  }


  @Override
  protected void structureSpecificAdd(AnyType key, int[] value) {
    this.tree.insert(key, value);
  }

  @Override
  protected Set<int[]> structureSpecificRemove(AnyType key, int[] value) {
    return this.tree.delete(key);
  }

  @Override
  public Set<int[]> structureSpecificSearch(AnyType key) {
    return this.tree.search(key);
  }


  @Override
  public Set<int[]> structureSpecificIntervalSearch(AnyType start, AnyType end) {
    return this.tree.getInterval(start, end);
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
