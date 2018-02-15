package de.dfki.lt.hfc.indices.btree;

import java.util.Arrays;

/**
 * This class is used to represent the Nodes of a btree. It contains fields for an Array of {@link KeyValuePair}s,
 * links to its childs, a link to its right neighbour, as well as a flag indicating whether it is an internal node or a leave.
 * <p>
 * Created by christian on 05/03/17.
 */
public class Node<K extends Comparable> {
    public final static int MIN_DEGREE = 5;
    public final static int LOWER_BOUND_KEYNUM = MIN_DEGREE - 1;
    public final static int UPPER_BOUND_KEYNUM = (MIN_DEGREE * 2) - 1;

    /**
     * boolean flag indicating whether this node is internal or leave
     */
    protected boolean mIsLeaf;

    /**
     * number of keys currently stored in this node
     */
    protected int mCurrentKeyNum;

    /**
     * array of key value pairs, i.e., the data stored in this node
     */
    protected KeyValuePair<K> mKeys[];

    /**
     * links to the children of this node
     */
    protected Node mChildren[];

    /**
     * the right neighbour of this node. This was introduced for convenience when computing values for intervals.
     */
    protected Node rightSiblingNode;


    public Node() {
        mIsLeaf = true;
        mCurrentKeyNum = 0;
        mKeys = new KeyValuePair[UPPER_BOUND_KEYNUM];
        mChildren = new Node[UPPER_BOUND_KEYNUM + 1];
        rightSiblingNode = null;
    }


    /**
     * Returns the child {@link Node} at the given index of the given Node.
     * @param node The node to search.
     * @param keyIdx The index to seacht.
     * @param nDirection TODO
     * @return
     *         The {@link Node} found at the given position.
     */
    protected static Node getChildNodeAtIndex(Node node, int keyIdx, int nDirection) {
        if (node.mIsLeaf) {
            return null;
        }

        keyIdx += nDirection;
        if ((keyIdx < 0) || (keyIdx > node.mCurrentKeyNum)) {
            return null;
        }

        return node.mChildren[keyIdx];
    }

    protected void setRightSiblingNode(Node node) {
        this.rightSiblingNode = node;
    }

    /**
     * Returns the child {@link Node} at the given index of the given Node.
     * @param node The node to search.
     * @param keyIdx The index to search.
     * @return
     *         The {@link Node} found at the given position.
     */
    protected static Node getLeftChildAtIndex(Node node, int keyIdx) {
        return getChildNodeAtIndex(node, keyIdx, 0);
    }


    /**
     * Returns the child {@link Node} at the given index of the given Node.
     * @param node The node to search.
     * @param keyIdx The index to seacht.
     * @return
     *         The {@link Node} found at the given position.
     */
    protected static Node getRightChildAtIndex(Node node, int keyIdx) {
        return getChildNodeAtIndex(node, keyIdx, 1);
    }


    //////////////////////////////// Used for bplustree Implementation ///////////////////////////////////////////////////
    protected static Node getLeftSiblingAtIndex(Node parentNode, int keyIdx) {
        return getChildNodeAtIndex(parentNode, keyIdx, -1);
    }


    protected static Node getRightSiblingAtIndex(Node parentNode, int keyIdx) {
        return getChildNodeAtIndex(parentNode, keyIdx, 1);
    }

    @Override
    public String toString() {
        return "Key = " + Arrays.toString(this.mKeys);
    }

}
