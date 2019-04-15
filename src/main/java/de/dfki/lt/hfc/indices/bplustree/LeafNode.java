package de.dfki.lt.hfc.indices.bplustree;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by christian on 06/03/17.
 */
public class LeafNode<Key extends Comparable> extends Node<Key> {
  protected final static int LEAFORDER = 4;
  private Object[] values;

  public LeafNode() {
    this.keys = new Object[LEAFORDER + 1];
    this.values = new Object[LEAFORDER + 1];
  }

  @SuppressWarnings("unchecked")
  public Set<int[]> getValue(int index) {
    return (Set<int[]>) this.values[index];
  }

  public void setValue(int index, Set<int[]> value) {
    this.values[index] = value;
  }

  @Override
  public TreeNodeType getNodeType() {
    return TreeNodeType.LeafNode;
  }

  @Override
  public int search(Key key) {
    for (int i = 0; i < this.getKeyCount(); ++i) {
      int cmp = this.getKey(i).compareTo(key);
      if (cmp == 0) {
        return i;
      } else if (cmp > 0) {
        return -1;
      }
    }

    return -1;
  }


  /* The codes below are used to support insertion operation */

  /**
   * Inserts the given key value pair into this node. If the key already exists the value is added to the former ones.
   *
   * @param key
   * @param value
   */
  public void insertKey(Key key, int[] value) {
    int index = 0;
    while (index < this.getKeyCount() && this.getKey(index).compareTo(key) < 0)
      ++index;
    this.insertAt(index, key, value);
  }

  /**
   * Inserts the given key value pair into this node. If the key already exists the value is added to the former ones.
   *
   * @param key
   * @param value
   */
  public void insertKey(Key key, Set<int[]> value) {
    int index = 0;
    while (index < this.getKeyCount() && this.getKey(index).compareTo(key) < 0)
      ++index;
    for (int[] v : value)
      this.insertAt(index, key, v);
  }

  private void insertAt(int index, Key key, int[] value) {
    if (search(key) == index) {
      this.getValue(index).add(value);
      return;
    }
    // move space for the new key
    for (int i = this.getKeyCount() - 1; i >= index; --i) {
      this.setKey(i + 1, this.getKey(i));
      this.setValue(i + 1, this.getValue(i));
    }

    // insert new key and value
    this.setKey(index, key);
    this.setValue(index, new HashSet<int[]>(Collections.singletonList(value)));
    ++this.keyCount;
  }


  /**
   * When splits a leaf node, the middle key is kept on new node and be pushed to parent node.
   */
  @Override
  protected Node<Key> split() {
    int midIndex = this.getKeyCount() / 2;

    LeafNode<Key> newRNode = new LeafNode<Key>();
    for (int i = midIndex; i < this.getKeyCount(); ++i) {
      newRNode.setKey(i - midIndex, this.getKey(i));
      newRNode.setValue(i - midIndex, this.getValue(i));
      this.setKey(i, null);
      this.setValue(i, null);
    }
    newRNode.keyCount = this.getKeyCount() - midIndex;
    this.keyCount = midIndex;

    return newRNode;
  }

  @Override
  protected Node<Key> pushUpKey(Key key, Node<Key> leftChild, Node<Key> rightNode) {
    throw new UnsupportedOperationException();
  }




  /* The codes below are used to support deletion operation */

  /**
   * This method removes the given key from this node and thus the whole index.
   *
   * @param key The key to be deleted.
   * @return the values associated with the deleted key. These are mainly returned for testing purposes.
   */
  public Set<int[]> delete(Key key) {
    int index = this.search(key);
    if (index == -1)
      return null;
    Set<int[]> v = this.getValue(index);
    this.deleteAt(index);
    return v;
  }

  /**
   * This method removes the key stored at the given index from this node and thus the whole index.
   *
   * @param index the index where the key should be deleted.
   * @return the values associated with the deleted key. These are mainly returned for testing purposes.
   */
  private void deleteAt(int index) {
    int i = index;
    for (i = index; i < this.getKeyCount() - 1; ++i) {
      this.setKey(i, this.getKey(i + 1));
      this.setValue(i, this.getValue(i + 1));
    }
    this.setKey(i, null);
    this.setValue(i, null);
    --this.keyCount;
  }

  @Override
  protected void processChildrenTransfer(Node<Key> borrower, Node<Key> lender, int borrowIndex) {
    throw new UnsupportedOperationException();
  }

  @Override
  protected Node<Key> processChildrenFusion(Node<Key> leftChild, Node<Key> rightChild) {
    throw new UnsupportedOperationException();
  }

  /**
   * Notice that the key sunk from parent is be abandoned.
   */
  @Override
  @SuppressWarnings("unchecked")
  protected void fusionWithSibling(Key sinkKey, Node<Key> rightSibling) {
    LeafNode<Key> siblingLeaf = (LeafNode<Key>) rightSibling;

    int j = this.getKeyCount();
    for (int i = 0; i < siblingLeaf.getKeyCount(); ++i) {
      this.setKey(j + i, siblingLeaf.getKey(i));
      this.setValue(j + i, siblingLeaf.getValue(i));
    }
    this.keyCount += siblingLeaf.getKeyCount();

    this.setRightSibling(siblingLeaf.rightSibling);
    if (siblingLeaf.rightSibling != null)
      siblingLeaf.rightSibling.setLeftSibling(this);
  }

  @Override
  @SuppressWarnings("unchecked")
  protected Key transferFromSibling(Key sinkKey, Node<Key> sibling, int borrowIndex) {
    LeafNode<Key> siblingNode = (LeafNode<Key>) sibling;

    this.insertKey(siblingNode.getKey(borrowIndex), siblingNode.getValue(borrowIndex));
    siblingNode.deleteAt(borrowIndex);

    return borrowIndex == 0 ? sibling.getKey(0) : this.getKey(0);
  }

  /**
   * Searches all values associated with keys that are within the given interval.
   *
   * @param start The key where the interval starts, must be smaller than the end key.
   * @param end   The key where the interval ends, must be bigger than the start key.
   * @return All values associated with keys that are equal or greater then the start key and smaller or equal than the end key.
   */
  public Set<int[]> searchForInterval(Key start, Key end) {
    Set<int[]> result = new HashSet<>();
    int index;
    for (index = 0; index < keyCount; index++) {
      if ((((Key) keys[index]).compareTo(start) >= 0) && (((Key) keys[index]).compareTo(end) <= 0))
        result.addAll((Set<int[]>) values[index]);
      if (((Key) keys[index]).compareTo(end) > 0)
        return result;
    }
    result.addAll(rightSibling.searchForInterval(start, end));
    return result;
  }
}
