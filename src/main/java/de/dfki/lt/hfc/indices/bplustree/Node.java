package de.dfki.lt.hfc.indices.bplustree;

import java.util.Set;

enum TreeNodeType {
  InnerNode,
  LeafNode
}

/**
 * Created by christian on 06/03/17.
 */
public abstract class Node<Key extends Comparable> {
  protected Object[] keys;
  protected int keyCount;
  protected Node<Key> parentNode;
  protected Node<Key> leftSibling;
  protected Node<Key> rightSibling;


  protected Node() {
    this.keyCount = 0;
    this.parentNode = null;
    this.leftSibling = null;
    this.rightSibling = null;
  }

  /**
   * @return The number of keys currently stored in this node
   */
  public int getKeyCount() {
    return this.keyCount;
  }

  @SuppressWarnings("unchecked")
  /**
   * This methods returns the key at the given index of a node.
   */
  public Key getKey(int index) {
    return (Key) this.keys[index];
  }

  /**
   * This method replaces the key at the given index with the given key.
   *
   * @param index The index to be set.
   * @param key   The key to be set.
   */
  public void setKey(int index, Key key) {
    this.keys[index] = key;
  }

  /**
   * Method mainly for convenience.
   *
   * @return The parent node of this node.
   */
  public Node<Key> getParent() {
    return this.parentNode;
  }

  public void setParent(Node<Key> parent) {
    this.parentNode = parent;
  }

  /**
   * @return The {@link TreeNodeType} indicating whether the node is a leaf ({@link LeafNode}) or an internal node({@link InnerNode}) .
   */
  public abstract TreeNodeType getNodeType();


  /**
   * Search a key on current node, if found the key then return its position,
   * otherwise return -1 for a leaf node,
   * return the child node index which should contain the key for a internal node.
   */
  public abstract int search(Key key);



  /* The codes below are used to support insertion operation */

  /**
   * checks whether a node contains to much keys.
   *
   * @return true, if the node is full, false otherwise
   */
  public boolean isOverflow() {
    return this.getKeyCount() == this.keys.length;
  }

  /**
   * Resolve the overflow issue by splitting the node.
   *
   * @return
   */
  public Node<Key> dealOverflow() {
    int midIndex = this.getKeyCount() / 2;
    Key upKey = this.getKey(midIndex);

    Node<Key> newRNode = this.split();

    if (this.getParent() == null) {
      this.setParent(new InnerNode<Key>());
    }
    newRNode.setParent(this.getParent());

    // maintain links of sibling nodes
    newRNode.setLeftSibling(this);
    newRNode.setRightSibling(this.rightSibling);
    if (this.getRightSibling() != null)
      this.getRightSibling().setLeftSibling(newRNode);
    this.setRightSibling(newRNode);

    // push up a key to parent internal node
    return this.getParent().pushUpKey(upKey, this, newRNode);
  }

  protected abstract Node<Key> split();

  protected abstract Node<Key> pushUpKey(Key key, Node<Key> leftChild, Node<Key> rightNode);






  /* The codes below are used to support deletion operation */

  /**
   * Checks whether the node contains less than 50% of the possible number of keys.
   *
   * @return
   */
  public boolean isUnderflow() {
    return this.getKeyCount() < (this.keys.length / 2);
  }

  /**
   * Checks whether the node is not underflowing after removing a key.
   *
   * @return
   */
  public boolean canLendAKey() {
    return this.getKeyCount() > (this.keys.length / 2);
  }

  public Node<Key> getLeftSibling() {
    if (this.leftSibling != null && this.leftSibling.getParent() == this.getParent())
      return this.leftSibling;
    return null;
  }

  public void setLeftSibling(Node<Key> sibling) {
    this.leftSibling = sibling;
  }

  public Node<Key> getRightSibling() {
    if (this.rightSibling != null && this.rightSibling.getParent() == this.getParent())
      return this.rightSibling;
    return null;
  }

  public void setRightSibling(Node<Key> silbling) {
    this.rightSibling = silbling;
  }

  /**
   * Resolve the overflow issue by merging to nodes, or lending keys from other neighbours.
   *
   * @return
   */
  public Node<Key> dealUnderflow() {
    if (this.getParent() == null)
      return null;

    // try to borrow a key from sibling
    Node<Key> leftSibling = this.getLeftSibling();
    if (leftSibling != null && leftSibling.canLendAKey()) {
      this.getParent().processChildrenTransfer(this, leftSibling, leftSibling.getKeyCount() - 1);
      return null;
    }

    Node<Key> rightSibling = this.getRightSibling();
    if (rightSibling != null && rightSibling.canLendAKey()) {
      this.getParent().processChildrenTransfer(this, rightSibling, 0);
      return null;
    }

    // Can not borrow a key from any sibling, then do fusion with sibling
    if (leftSibling != null) {
      Node n = this.getParent().processChildrenFusion(leftSibling, this);
      return n;
    } else {
      Node n = this.getParent().processChildrenFusion(this, rightSibling);
      return n;
    }
  }

  /**
   * Searches all values associated with keys that are within the given interval.
   *
   * @param start The key where the interval starts, must be smaller than the end key.
   * @param end   The key where the interval ends, must be bigger than the start key.
   * @return All values associated with keys that are equal or greater then the start key and smaller or equal than the end key.
   */
  public abstract Set<int[]> searchForInterval(Key start, Key end);

  /**
   * Handle the consequences of moving a node from one parent to an other. I.e. updating parents and handle potential over/under flows.
   *
   * @param borrower
   * @param lender
   * @param borrowIndex
   */
  protected abstract void processChildrenTransfer(Node<Key> borrower, Node<Key> lender, int borrowIndex);

  /**
   * Handle the consequences of merging two child notes. I.e. updating parents and handle potential over/under flows.
   */
  protected abstract Node<Key> processChildrenFusion(Node<Key> leftChild, Node<Key> rightChild);

  /**
   * Handle the issue of merging the node with one of its neighbours.
   *
   * @param sinkKey
   * @param rightSibling
   */
  protected abstract void fusionWithSibling(Key sinkKey, Node<Key> rightSibling);

  /**
   * Handle the issue of lending a key from a neighbour. i.e. updating/rearranging of children, handling pf potential overflows.
   *
   * @param sinkKey
   * @param sibling
   * @param borrowIndex
   * @return
   */
  protected abstract Key transferFromSibling(Key sinkKey, Node<Key> sibling, int borrowIndex);
}
