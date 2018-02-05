package de.dfki.lt.hfc.indices.btree;

import java.util.*;

/**
 * Properties of B-Tree
 * 1) All leaves are at same level.
 * 2) A B-Tree is defined by the term minimum degree ‘t’. The value of t depends upon disk block size.
 * 3) Every node except root must contain at least t-1 keys. Root may contain minimum 1 key.
 * 4) All nodes (including root) may contain at most 2t – 1 keys.
 * 5) Number of children of a node is equal to the number of keys in it plus 1.
 * 6) All keys of a node are sorted in increasing order. The child between two keys k1 and k2 ontologyContainsTuple all keys in range from k1 and k2.
 * 7) The time complexity to search, insert and delete is O(Logn).
 *
 * Created by christian on 05/03/17.
 */
public class BTree<K extends Comparable> {

    private final static int REBALANCE_FOR_LEAF_NODE = 1;

    private final static int REBALANCE_FOR_INTERNAL_NODE = 2;

    private final Stack<StackInfo> mStackTracer = new Stack<StackInfo>();

    private Node<K> mRoot = null;

    private long mSize = 0L;

    private int mHeight = 0;

    private Node<K> mIntermediateInternalNode = null;

    private int mNodeIdx = 0;

    /**
     * Get the root node
     **/
    public Node<K> getRootNode() {
        return mRoot;
    }


    /**
     * The total number of nodes in the tree
     **/
    public long size() {
        return mSize;
    }

    public int height() {
        return this.mHeight;
    }


    /**
     * Clear all the entries in the tree
     */
    public void clear() {
        mSize = 0L;
        mHeight = 0;
        mRoot = null;
    }


    /**
     * Create a node with default values
     */
    private Node<K> createNode() {
        Node<K> Node;
        Node = new Node();
        Node.mIsLeaf = true;
        Node.mCurrentKeyNum = 0;
        return Node;
    }

    /**
     * Search value for a specified key of the tree
     *
     * @param key the key to look up.
     * @return the values associated with the key.
     */
    public Set<int[]> search(K key) {
        Node<K> currentNode = mRoot;
        KeyValuePair<K> currentKey;
        int i, numberOfKeys;

        while (currentNode != null) {
            numberOfKeys = currentNode.mCurrentKeyNum;
            i = 0;
            currentKey = currentNode.mKeys[i];
            while ((i < numberOfKeys) && (key.compareTo(currentKey.mKey) > 0)) {
                ++i;
                if (i < numberOfKeys) {
                    currentKey = currentNode.mKeys[i];
                } else {
                    --i;
                    break;
                }
            }

            if ((i < numberOfKeys) && (key.compareTo(currentKey.mKey) == 0)) {
                return currentKey.mValue;
            }

            if (key.compareTo(currentKey.mKey) > 0) {
                currentNode = Node.getRightChildAtIndex(currentNode, i);
            } else {
                currentNode = Node.getLeftChildAtIndex(currentNode, i);
            }
        }

        return null;
    }


    /**
     * Search value for an interval  specified by a key representing the start and one representing the end of the interval.
     *
     * @param start the key where the interval begins
     * @param end   the key where the interval ends
     * @return
     */
    public Set<int[]> searchInterval(K start, K end) {
        Set<int[]> result = new HashSet<>();
        // Check corner cases
        if (start.compareTo(end) > 0)
            return result;
        if (start.compareTo(end) == 0)
            return search(start);
        Node<K> currentNode = mRoot;
        Stack<Node> parentNodes = new Stack<>();
        Stack<Integer> parentIndices = new Stack<>();
        KeyValuePair<K> currentKey;
        int i, numberOfKeys;
        // find node that should contain the start node
        while (currentNode != null) {
            numberOfKeys = currentNode.mCurrentKeyNum;
            i = 0;
            currentKey = currentNode.mKeys[i];

            while ((i < numberOfKeys) && (start.compareTo(currentKey.mKey) > 0)) {
                ++i;
                if (i < numberOfKeys) {
                    currentKey = currentNode.mKeys[i];
                } else {
                    --i;
                    break;
                }
            }
            if (((i < numberOfKeys) && (start.compareTo(currentKey.mKey) >= 0)) || ((i > 0) && (start.compareTo(currentKey.mKey) < 0))) {
                result.addAll(currentKey.mValue);
                result.addAll(collectValues(currentNode, parentNodes, parentIndices, i, end));

            }
            if (start.compareTo(currentKey.mKey) > 0) {
                parentNodes.push(currentNode);
                parentIndices.push(i);
                currentNode = Node.getRightChildAtIndex(currentNode, i);
            } else {
                parentNodes.push(currentNode);
                parentIndices.push(i);
                currentNode = Node.getLeftChildAtIndex(currentNode, i);
            }
        }
        return result;
    }

    private Set<int[]> collectValues(Node<K> currentNode, Stack<Node> parentNode, Stack<Integer> parentIndices, int i, Comparable end) {
        Set<int[]> result = new HashSet<>();
        // collect matching entries in node
        while (i < currentNode.mCurrentKeyNum - 1) {
            i++;
            KeyValuePair currentKeyValuePair = currentNode.mKeys[i];
            if (currentKeyValuePair.mKey.compareTo(end) <= 0) {
                result.addAll(currentKeyValuePair.mValue);
            } else {
                return result;
            }
        }

        // check parent
        if (!parentNode.empty()) {
            Node parent = parentNode.pop();
            int parentIndex = parentIndices.pop();
            while ((parentIndex < parent.mCurrentKeyNum) && (parent.mKeys[parentIndex].mKey.compareTo(end) <= 0)) {
                result.addAll(parent.mKeys[parentIndex].mValue);
                result.addAll(collectValues(Node.getChildNodeAtIndex(parent, parentIndex, 1), parentNode, parentIndices, -1, end));
                parentIndex++;
            }
        }
        return result;
    }


    /**
     * Insert key and its value into the tree
     *
     * @param key   to be added
     * @param value associated with the key
     * @return The btree after inserting the new key value pair.
     */
    public BTree insert(K key, Set<int[]> value) {
        if (mRoot == null) {
            mRoot = createNode();
            mHeight++;
        }

        ++mSize;
        if (mRoot.mCurrentKeyNum == Node.UPPER_BOUND_KEYNUM) {
            // The root is full, split it
            Node<K> node = createNode();
            mHeight++;
            node.mIsLeaf = false;
            node.mChildren[0] = mRoot;
            mRoot = node;
            splitNode(mRoot, 0, node.mChildren[0]);
        }

        insertKeyAtNode(mRoot, key, value);
        return this;
    }


    /**
     * Insert key and its value to the specified root
     *
     * @param rootNode
     * @param key
     * @param value
     */
    private void insertKeyAtNode(Node rootNode, K key, Set<int[]> value) {
        int i;
        int currentKeyNum = rootNode.mCurrentKeyNum;

        if (rootNode.mIsLeaf) {
            if (rootNode.mCurrentKeyNum == 0) {
                // Empty root
                rootNode.mKeys[0] = new KeyValuePair<K>(key, value);
                ++(rootNode.mCurrentKeyNum);
                return;
            }

            // Verify if the specified key doesn't exist in the node
            for (i = 0; i < rootNode.mCurrentKeyNum; ++i) {
                if (key.compareTo(rootNode.mKeys[i].mKey) == 0) {
                    // Find existing key, overwrite its value only
                    rootNode.mKeys[i].mValue = value;
                    --mSize;
                    return;
                }
            }

            i = currentKeyNum - 1;
            KeyValuePair<K> existingKeyValuePair = rootNode.mKeys[i];
            while ((i > -1) && (key.compareTo(existingKeyValuePair.mKey) < 0)) {
                rootNode.mKeys[i + 1] = existingKeyValuePair;
                --i;
                if (i > -1) {
                    existingKeyValuePair = rootNode.mKeys[i];
                }
            }

            i = i + 1;
            rootNode.mKeys[i] = new KeyValuePair<K>(key, value);

            ++(rootNode.mCurrentKeyNum);
            return;
        }

        // This is an internal node (i.e: not a leaf node)
        // So let find the child node where the key is supposed to belong
        i = 0;
        int numberOfKeys = rootNode.mCurrentKeyNum;
        KeyValuePair<K> currentKey = rootNode.mKeys[i];
        while ((i < numberOfKeys) && (key.compareTo(currentKey.mKey) > 0)) {
            ++i;
            if (i < numberOfKeys) {
                currentKey = rootNode.mKeys[i];
            } else {
                --i;
                break;
            }
        }

        if ((i < numberOfKeys) && (key.compareTo(currentKey.mKey) == 0)) {
            // The key already existed so replace its value and done with it
            currentKey.mValue = value;
            --mSize;
            return;
        }

        Node<K> node;
        if (key.compareTo(currentKey.mKey) > 0) {
            node = Node.getRightChildAtIndex(rootNode, i);
            i = i + 1;
        } else {
            if ((i - 1 >= 0) && (key.compareTo(rootNode.mKeys[i - 1].mKey) > 0)) {
                node = Node.getRightChildAtIndex(rootNode, i - 1);
            } else {
                node = Node.getLeftChildAtIndex(rootNode, i);
            }
        }

        if (node.mCurrentKeyNum == Node.UPPER_BOUND_KEYNUM) {
            // If the child node is a full node then handle it by splitting out
            // then insert key starting at the root node after splitting node
            splitNode(rootNode, i, node);
            insertKeyAtNode(rootNode, key, value);
            return;
        }

        insertKeyAtNode(node, key, value);
    }


    /**
     * Split a child with respect to its parent at a specified node
     *
     * @param parentNode
     * @param nodeIdx
     * @param node
     */
    private void splitNode(Node parentNode, int nodeIdx, Node node) {
        int i;

        Node<K> newNode = createNode();

        newNode.mIsLeaf = node.mIsLeaf;

        // Since the node is full,
        // new node must share LOWER_BOUND_KEYNUM (aka t - 1) keys from the node
        newNode.mCurrentKeyNum = Node.LOWER_BOUND_KEYNUM;

        // Copy right half of the keys from the node to the new node
        for (i = 0; i < Node.LOWER_BOUND_KEYNUM; ++i) {
            newNode.mKeys[i] = node.mKeys[i + Node.MIN_DEGREE];
            node.mKeys[i + Node.MIN_DEGREE] = null;
        }

        // If the node is an internal node (not a leaf),
        // copy the its child pointers at the half right as well
        if (!node.mIsLeaf) {
            for (i = 0; i < Node.MIN_DEGREE; ++i) {
                newNode.mChildren[i] = node.mChildren[i + Node.MIN_DEGREE];
                node.mChildren[i + Node.MIN_DEGREE] = null;
            }
        }

        // The node at this point should have LOWER_BOUND_KEYNUM (aka min degree - 1) keys at this point.
        // We will move its right-most key to its parent node later.
        node.mCurrentKeyNum = node.LOWER_BOUND_KEYNUM;

        // Do the right shift for relevant child pointers of the parent node
        // so that we can put the new node as its new child pointer
        for (i = parentNode.mCurrentKeyNum; i > nodeIdx; --i) {
            parentNode.mChildren[i + 1] = parentNode.mChildren[i];
            parentNode.mChildren[i] = null;
        }
        parentNode.mChildren[nodeIdx + 1] = newNode;

        // Do the right shift all the keys of the parent node the right side of the node index as well
        // so that we will have a slot for move a median key from the splitted node
        for (i = parentNode.mCurrentKeyNum - 1; i >= nodeIdx; --i) {
            parentNode.mKeys[i + 1] = parentNode.mKeys[i];
            parentNode.mKeys[i] = null;
        }
        parentNode.mKeys[nodeIdx] = node.mKeys[Node.LOWER_BOUND_KEYNUM];
        node.mKeys[Node.LOWER_BOUND_KEYNUM] = null;
        ++(parentNode.mCurrentKeyNum);
    }


    /**
     * Find the predecessor node for a specified node
     */
    private Node<K> findPredecessor(Node<K> node, int nodeIdx) {
        if (node.mIsLeaf) {
            return node;
        }

        Node<K> predecessorNode;
        if (nodeIdx > -1) {
            predecessorNode = node.getLeftChildAtIndex(node, nodeIdx);
            if (predecessorNode != null) {
                mIntermediateInternalNode = node;
                mNodeIdx = nodeIdx;
                node = findPredecessor(predecessorNode, -1);
            }

            return node;
        }

        predecessorNode = Node.getRightChildAtIndex(node, node.mCurrentKeyNum - 1);
        if (predecessorNode != null) {
            mIntermediateInternalNode = node;
            mNodeIdx = node.mCurrentKeyNum;
            node = findPredecessorForNode(predecessorNode, -1);
        }

        return node;
    }


    /**
     * Find predecessor node of a specified node
     */
    private Node<K> findPredecessorForNode(Node<K> node, int keyIdx) {
        Node<K> predecessorNode;
        Node<K> originalNode = node;
        if (keyIdx > -1) {
            predecessorNode = Node.getLeftChildAtIndex(node, keyIdx);
            if (predecessorNode != null) {
                node = findPredecessorForNode(predecessorNode, -1);
                rebalanceTreeAtNode(originalNode, predecessorNode, keyIdx, REBALANCE_FOR_LEAF_NODE);
            }

            return node;
        }

        predecessorNode = Node.getRightChildAtIndex(node, node.mCurrentKeyNum - 1);
        if (predecessorNode != null) {
            node = findPredecessorForNode(predecessorNode, -1);
            rebalanceTreeAtNode(originalNode, predecessorNode, keyIdx, REBALANCE_FOR_LEAF_NODE);
        }

        return node;
    }


    /**
    * Do the left rotation
    */
    private void performLeftRotation(Node<K> node, int nodeIdx, Node<K> parentNode, Node<K> rightSiblingNode) {
        int parentKeyIdx = nodeIdx;

        // Move the parent key and relevant child to the deficient node
        node.mKeys[node.mCurrentKeyNum] = parentNode.mKeys[parentKeyIdx];
        node.mChildren[node.mCurrentKeyNum + 1] = rightSiblingNode.mChildren[0];
        ++(node.mCurrentKeyNum);

        // Move the leftmost key of the right sibling and relevant child pointer to the parent node
        parentNode.mKeys[parentKeyIdx] = rightSiblingNode.mKeys[0];
        --(rightSiblingNode.mCurrentKeyNum);
        // Shift all keys and children of the right sibling to its left
        for (int i = 0; i < rightSiblingNode.mCurrentKeyNum; ++i) {
            rightSiblingNode.mKeys[i] = rightSiblingNode.mKeys[i + 1];
            rightSiblingNode.mChildren[i] = rightSiblingNode.mChildren[i + 1];
        }
        rightSiblingNode.mChildren[rightSiblingNode.mCurrentKeyNum] = rightSiblingNode.mChildren[rightSiblingNode.mCurrentKeyNum + 1];
        rightSiblingNode.mChildren[rightSiblingNode.mCurrentKeyNum + 1] = null;
    }


    /**
     * Do the right rotation
     * @param node
     * @param nodeIdx
     * @param parentNode
     * @param leftSiblingNode
     */
    private void performRightRotation(Node<K> node, int nodeIdx, Node<K> parentNode, Node<K> leftSiblingNode) {
        int parentKeyIdx = nodeIdx;
        if (nodeIdx >= parentNode.mCurrentKeyNum) {
            // This shouldn't happen
            parentKeyIdx = nodeIdx - 1;
        }

        // Shift all keys and children of the deficient node to the right
        // So that there will be available left slot for insertion
        node.mChildren[node.mCurrentKeyNum + 1] = node.mChildren[node.mCurrentKeyNum];
        for (int i = node.mCurrentKeyNum - 1; i >= 0; --i) {
            node.mKeys[i + 1] = node.mKeys[i];
            node.mChildren[i + 1] = node.mChildren[i];
        }

        // Move the parent key and relevant child to the deficient node
        node.mKeys[0] = parentNode.mKeys[parentKeyIdx];
        node.mChildren[0] = leftSiblingNode.mChildren[leftSiblingNode.mCurrentKeyNum];
        ++(node.mCurrentKeyNum);

        // Move the leftmost key of the right sibling and relevant child pointer to the parent node
        parentNode.mKeys[parentKeyIdx] = leftSiblingNode.mKeys[leftSiblingNode.mCurrentKeyNum - 1];
        leftSiblingNode.mChildren[leftSiblingNode.mCurrentKeyNum] = null;
        --(leftSiblingNode.mCurrentKeyNum);
    }


    /**
    * Do a left sibling merge
    * Return true if it should continue further
    * Return false if it is done
    */
    private boolean performMergeWithLeftSibling(Node<K> node, int nodeIndex, Node<K> parentNode, Node<K> leftSiblingNode) {
        if (nodeIndex == parentNode.mCurrentKeyNum) {
            // For the case that the node index can be the right most
            nodeIndex = nodeIndex - 1;
        }

        // Here we need to determine the parent node's index based on child node's index (nodeIdx)
        if (nodeIndex > 0) {
            if (leftSiblingNode.mKeys[leftSiblingNode.mCurrentKeyNum - 1].mKey.compareTo(parentNode.mKeys[nodeIndex - 1].mKey) < 0) {
                nodeIndex = nodeIndex - 1;
            }
        }

        // Copy the parent key to the node (on the left)
        leftSiblingNode.mKeys[leftSiblingNode.mCurrentKeyNum] = parentNode.mKeys[nodeIndex];
        ++(leftSiblingNode.mCurrentKeyNum);

        // Copy keys and children of the node to the left sibling node
        for (int i = 0; i < node.mCurrentKeyNum; ++i) {
            leftSiblingNode.mKeys[leftSiblingNode.mCurrentKeyNum + i] = node.mKeys[i];
            leftSiblingNode.mChildren[leftSiblingNode.mCurrentKeyNum + i] = node.mChildren[i];
            node.mKeys[i] = null;
        }
        leftSiblingNode.mCurrentKeyNum += node.mCurrentKeyNum;
        leftSiblingNode.mChildren[leftSiblingNode.mCurrentKeyNum] = node.mChildren[node.mCurrentKeyNum];
        node.mCurrentKeyNum = 0;  // Abandon the node

        // Shift all relevant keys and children of the parent node to the left
        // since it lost one of its keys and children (by moving it to the child node)
        int i;
        for (i = nodeIndex; i < parentNode.mCurrentKeyNum - 1; ++i) {
            parentNode.mKeys[i] = parentNode.mKeys[i + 1];
            parentNode.mChildren[i + 1] = parentNode.mChildren[i + 2];
        }
        parentNode.mKeys[i] = null;
        parentNode.mChildren[parentNode.mCurrentKeyNum] = null;
        --(parentNode.mCurrentKeyNum);

        // Make sure the parent point to the correct child after the merge
        parentNode.mChildren[nodeIndex] = leftSiblingNode;

        if ((parentNode == mRoot) && (parentNode.mCurrentKeyNum == 0)) {
            // Root node is updated.  It should be done
            mRoot = leftSiblingNode;
            return false;
        }

        return true;
    }


    /**
    * Do the right sibling merge
    * Return true if it should continue further
    * Return false if it is done
    */
    private boolean performMergeWithRightSibling(Node<K> node, int nodeIndex, Node<K> parentNode, Node<K> rightSiblingNode) {
        // Copy the parent key to right-most slot of the node
        node.mKeys[node.mCurrentKeyNum] = parentNode.mKeys[nodeIndex];
        ++(node.mCurrentKeyNum);

        // Copy keys and children of the right sibling to the node
        for (int i = 0; i < rightSiblingNode.mCurrentKeyNum; ++i) {
            node.mKeys[node.mCurrentKeyNum + i] = rightSiblingNode.mKeys[i];
            node.mChildren[node.mCurrentKeyNum + i] = rightSiblingNode.mChildren[i];
        }
        node.mCurrentKeyNum += rightSiblingNode.mCurrentKeyNum;
        node.mChildren[node.mCurrentKeyNum] = rightSiblingNode.mChildren[rightSiblingNode.mCurrentKeyNum];
        rightSiblingNode.mCurrentKeyNum = 0;  // Abandon the sibling node

        // Shift all relevant keys and children of the parent node to the left
        // since it lost one of its keys and children (by moving it to the child node)
        int i;
        for (i = nodeIndex; i < parentNode.mCurrentKeyNum - 1; ++i) {
            parentNode.mKeys[i] = parentNode.mKeys[i + 1];
            parentNode.mChildren[i + 1] = parentNode.mChildren[i + 2];
        }
        parentNode.mKeys[i] = null;
        parentNode.mChildren[parentNode.mCurrentKeyNum] = null;
        --(parentNode.mCurrentKeyNum);

        // Make sure the parent point to the correct child after the merge
        parentNode.mChildren[nodeIndex] = node;

        if ((parentNode == mRoot) && (parentNode.mCurrentKeyNum == 0)) {
            // Root node is updated.  It should be done
            mRoot = node;
            return false;
        }

        return true;
    }


    /**
    * Search the specified key within a node
    * Return index of the keys if it finds
    * Return -1 otherwise
    */
    private int searchKey(Node<K> node, K key) {
        for (int i = 0; i < node.mCurrentKeyNum; ++i) {
            if (key.compareTo(node.mKeys[i].mKey) == 0) {
                return i;
            } else if (key.compareTo(node.mKeys[i].mKey) < 0) {
                return -1;
            }
        }

        return -1;
    }


    /**
     * List all the items in the tree
     * TODO is this really needed?
     * /
     **/
    public void list(BTreeIteratorIF<K> iterImpl) {
        if (mSize < 1) {
            return;
        }

        if (iterImpl == null) {
            return;
        }

        listEntriesInOrder(mRoot, iterImpl);
    }


    /**
     * Recursively loop to the tree and list out the keys and their values
     * Return true if it should continues listing out further
     * Return false if it is done
     * TODO is this really needed?
     **/
    private boolean listEntriesInOrder(Node<K> treeNode, BTreeIteratorIF<K> iterator) {
        if ((treeNode == null) ||
                (treeNode.mCurrentKeyNum == 0)) {
            return false;
        }

        boolean bStatus;
        KeyValuePair<K> keyVal;
        int currentKeyNum = treeNode.mCurrentKeyNum;
        for (int i = 0; i < currentKeyNum; ++i) {
            listEntriesInOrder(Node.getLeftChildAtIndex(treeNode, i), iterator);

            keyVal = treeNode.mKeys[i];
            bStatus = iterator.item(keyVal.mKey, keyVal.mValue);
            if (!bStatus) {
                return false;
            }

            if (i == currentKeyNum - 1) {
                listEntriesInOrder(Node.getRightChildAtIndex(treeNode, i), iterator);
            }
        }

        return true;
    }


    /**
    * Delete a key from the tree
    * Return value if it finds the key and delete it
    * Return null if it cannot find the key
    */
    public Set<int[]> delete(K key) {
        mIntermediateInternalNode = null;
        KeyValuePair<K> keyVal = deleteKey(null, mRoot, key, 0);
        if (keyVal == null) {
            return null;
        }
        --mSize;
        return keyVal.mValue;
    }


    /**
    * Delete a key from a tree node
    */
    private KeyValuePair<K> deleteKey(Node<K> parentNode, Node<K> node, K key, int nodeIndex) {
        int i;
        int nIdx;
        KeyValuePair<K> retVal;

        if (node == null) {
            // The tree is empty
            return null;
        }

        if (node.mIsLeaf) {
            nIdx = searchKey(node, key);
            if (nIdx < 0) {
                // Can't find the specified key
                return null;
            }

            retVal = node.mKeys[nIdx];

            if ((node.mCurrentKeyNum > Node.LOWER_BOUND_KEYNUM) || (parentNode == null)) {
                // Remove it from the node
                for (i = nIdx; i < node.mCurrentKeyNum - 1; ++i) {
                    node.mKeys[i] = node.mKeys[i + 1];
                }
                node.mKeys[i] = null;
                --(node.mCurrentKeyNum);

                if (node.mCurrentKeyNum == 0) {
                    // Node is actually the root node
                    mRoot = null;
                }

                return retVal;
            }

            // Find the left sibling
            Node<K> rightSibling;
            Node<K> leftSibling = Node.getLeftSiblingAtIndex(parentNode, nodeIndex);
            if ((leftSibling != null) && (leftSibling.mCurrentKeyNum > Node.LOWER_BOUND_KEYNUM)) {
                // Remove the key and borrow a key from the left sibling
                moveLeftLeafSiblingKeyWithKeyRemoval(node, nodeIndex, nIdx, parentNode, leftSibling);
            } else {
                rightSibling = Node.getRightSiblingAtIndex(parentNode, nodeIndex);
                if ((rightSibling != null) && (rightSibling.mCurrentKeyNum > Node.LOWER_BOUND_KEYNUM)) {
                    // Remove a key and borrow a key the right sibling
                    moveRightLeafSiblingKeyWithKeyRemoval(node, nodeIndex, nIdx, parentNode, rightSibling);
                } else {
                    // Merge to its sibling
                    boolean isRebalanceNeeded = false;
                    boolean bStatus;
                    if (leftSibling != null) {
                        // Merge with the left sibling
                        bStatus = doLeafSiblingMergeWithKeyRemoval(node, nodeIndex, nIdx, parentNode, leftSibling, false);
                        if (!bStatus) {
                            isRebalanceNeeded = false;
                        } else if (parentNode.mCurrentKeyNum < Node.LOWER_BOUND_KEYNUM) {
                            // Need to rebalance the tree
                            isRebalanceNeeded = true;
                        }
                    } else {
                        // Merge with the right sibling
                        bStatus = doLeafSiblingMergeWithKeyRemoval(node, nodeIndex, nIdx, parentNode, rightSibling, true);
                        if (!bStatus) {
                            isRebalanceNeeded = false;
                        } else if (parentNode.mCurrentKeyNum < Node.LOWER_BOUND_KEYNUM) {
                            // Need to rebalance the tree
                            isRebalanceNeeded = true;
                        }
                    }

                    if (isRebalanceNeeded && (mRoot != null)) {
                        rebalanceTree(mRoot, parentNode, parentNode.mKeys[0].mKey);
                    }
                }
            }

            return retVal;  // Done with handling for the leaf node
        }

        //
        // At this point the node is an internal node
        //

        nIdx = searchKey(node, key);
        if (nIdx >= 0) {
            // We found the key in the internal node

            // Find its predecessor
            mIntermediateInternalNode = node;
            mNodeIdx = nIdx;
            Node<K> predecessorNode = findPredecessor(node, nIdx);
            KeyValuePair<K> predecessorKey = predecessorNode.mKeys[predecessorNode.mCurrentKeyNum - 1];

            // Swap the data of the deleted key and its predecessor (in the leaf node)
            KeyValuePair<K> deletedKey = node.mKeys[nIdx];
            node.mKeys[nIdx] = predecessorKey;
            predecessorNode.mKeys[predecessorNode.mCurrentKeyNum - 1] = deletedKey;

            // mIntermediateNode is done in findPrecessor
            return deleteKey(mIntermediateInternalNode, predecessorNode, deletedKey.mKey, mNodeIdx);
        }

        //
        // Find the child subtree (node) that ontologyContainsTuple the key
        //
        i = 0;
        KeyValuePair<K> currentKey = node.mKeys[0];
        while ((i < node.mCurrentKeyNum) && (key.compareTo(currentKey.mKey) > 0)) {
            ++i;
            if (i < node.mCurrentKeyNum) {
                currentKey = node.mKeys[i];
            } else {
                --i;
                break;
            }
        }

        Node<K> childNode;
        if (key.compareTo(currentKey.mKey) > 0) {
            childNode = Node.getRightChildAtIndex(node, i);
            if (childNode.mKeys[0].mKey.compareTo(node.mKeys[node.mCurrentKeyNum - 1].mKey) > 0) {
                // The right-most side of the node
                i = i + 1;
            }
        } else {
            childNode = Node.getLeftChildAtIndex(node, i);
        }

        return deleteKey(node, childNode, key, i);
    }


    /**
    * Remove the specified key and move a key from the right leaf sibling to the node
    * Note: The node and its sibling must be leaves
    */
    private void moveRightLeafSiblingKeyWithKeyRemoval(Node<K> node,
                                                       int nodeIdx,
                                                       int keyIdx,
                                                       Node<K> parentNode,
                                                       Node<K> rightSiblingNode) {
        // Shift to the right where the key is deleted
        for (int i = keyIdx; i < node.mCurrentKeyNum - 1; ++i) {
            node.mKeys[i] = node.mKeys[i + 1];
        }

        node.mKeys[node.mCurrentKeyNum - 1] = parentNode.mKeys[nodeIdx];
        parentNode.mKeys[nodeIdx] = rightSiblingNode.mKeys[0];

        for (int i = 0; i < rightSiblingNode.mCurrentKeyNum - 1; ++i) {
            rightSiblingNode.mKeys[i] = rightSiblingNode.mKeys[i + 1];
        }

        --(rightSiblingNode.mCurrentKeyNum);
    }


    /**
    * Remove the specified key and move a key from the left leaf sibling to the node
    * Note: The node and its sibling must be leaves
    */
    private void moveLeftLeafSiblingKeyWithKeyRemoval(Node<K> node,
                                                      int nodeIdx,
                                                      int keyIdx,
                                                      Node<K> parentNode,
                                                      Node<K> leftSiblingNode) {
        // Use the parent key on the left side of the node
        nodeIdx = nodeIdx - 1;

        // Shift to the right to where the key will be deleted
        for (int i = keyIdx; i > 0; --i) {
            node.mKeys[i] = node.mKeys[i - 1];
        }

        node.mKeys[0] = parentNode.mKeys[nodeIdx];
        parentNode.mKeys[nodeIdx] = leftSiblingNode.mKeys[leftSiblingNode.mCurrentKeyNum - 1];
        --(leftSiblingNode.mCurrentKeyNum);
    }


    /**
    * Do the leaf sibling merge
    * Return true if we need to perform futher re-balancing action
    * Return false if we reach and update the root hence we don't need to go futher for re-balancing the tree
    */
    private boolean doLeafSiblingMergeWithKeyRemoval(Node<K> node,
                                                     int nodeIdx,
                                                     int keyIdx,
                                                     Node<K> parentNode,
                                                     Node<K> siblingNode,
                                                     boolean isRightSibling) {
        int i;

        if (nodeIdx == parentNode.mCurrentKeyNum) {
            // Case node index can be the right most
            nodeIdx = nodeIdx - 1;
        }

        if (isRightSibling) {
            // Shift the remained keys of the node to the left to remove the key
            for (i = keyIdx; i < node.mCurrentKeyNum - 1; ++i) {
                node.mKeys[i] = node.mKeys[i + 1];
            }
            node.mKeys[i] = parentNode.mKeys[nodeIdx];
        } else {
            // Here we need to determine the parent node id based on child node id (nodeIdx)
            if (nodeIdx > 0) {
                if (siblingNode.mKeys[siblingNode.mCurrentKeyNum - 1].mKey.compareTo(parentNode.mKeys[nodeIdx - 1].mKey) < 0) {
                    nodeIdx = nodeIdx - 1;
                }
            }

            siblingNode.mKeys[siblingNode.mCurrentKeyNum] = parentNode.mKeys[nodeIdx];
            // siblingNode.mKeys[siblingNode.mCurrentKeyNum] = parentNode.mKeys[0];
            ++(siblingNode.mCurrentKeyNum);

            // Shift the remained keys of the node to the left to remove the key
            for (i = keyIdx; i < node.mCurrentKeyNum - 1; ++i) {
                node.mKeys[i] = node.mKeys[i + 1];
            }
            node.mKeys[i] = null;
            --(node.mCurrentKeyNum);
        }

        if (isRightSibling) {
            for (i = 0; i < siblingNode.mCurrentKeyNum; ++i) {
                node.mKeys[node.mCurrentKeyNum + i] = siblingNode.mKeys[i];
                siblingNode.mKeys[i] = null;
            }
            node.mCurrentKeyNum += siblingNode.mCurrentKeyNum;
        } else {
            for (i = 0; i < node.mCurrentKeyNum; ++i) {
                siblingNode.mKeys[siblingNode.mCurrentKeyNum + i] = node.mKeys[i];
                node.mKeys[i] = null;
            }
            siblingNode.mCurrentKeyNum += node.mCurrentKeyNum;
            node.mKeys[node.mCurrentKeyNum] = null;
        }

        // Shift the parent keys accordingly after the merge of child nodes
        for (i = nodeIdx; i < parentNode.mCurrentKeyNum - 1; ++i) {
            parentNode.mKeys[i] = parentNode.mKeys[i + 1];
            parentNode.mChildren[i + 1] = parentNode.mChildren[i + 2];
        }
        parentNode.mKeys[i] = null;
        parentNode.mChildren[parentNode.mCurrentKeyNum] = null;
        --(parentNode.mCurrentKeyNum);

        if (isRightSibling) {
            parentNode.mChildren[nodeIdx] = node;
        } else {
            parentNode.mChildren[nodeIdx] = siblingNode;
        }

        if ((mRoot == parentNode) && (mRoot.mCurrentKeyNum == 0)) {
            // Only root left
            mRoot = parentNode.mChildren[nodeIdx];
            mRoot.mIsLeaf = true;
            return false;  // Root has been changed, we don't need to go futher
        }

        return true;
    }


    /**
     * Re-balance the tree at a specified node
     * Params:
     * parentNode = the parent node of the node needs to be re-balanced
     * Node = the node needs to be re-balanced
     * nodeIdx = the index of the parent node's child array where the node belongs
     * balanceType = either REBALANCE_FOR_LEAF_NODE or REBALANCE_FOR_INTERNAL_NODE
     *   REBALANCE_FOR_LEAF_NODE: the node is a leaf
     *   REBALANCE_FOR_INTERNAL_NODE: the node is an internal node
     * Return:
     * true if it needs to continue rebalancing further
     * false if further rebalancing is no longer needed
     */
    private boolean rebalanceTreeAtNode(Node<K> parentNode, Node<K> node, int nodeIdx, int balanceType) {
        if (balanceType == REBALANCE_FOR_LEAF_NODE) {
            if ((node == null) || (node == mRoot)) {
                return false;
            }
        } else if (balanceType == REBALANCE_FOR_INTERNAL_NODE) {
            if (parentNode == null) {
                // Root node
                return false;
            }
        }

        if (node.mCurrentKeyNum >= Node.LOWER_BOUND_KEYNUM) {
            // The node doesn't need to rebalance
            return false;
        }

        Node<K> rightSiblingNode;
        Node<K> leftSiblingNode = Node.getLeftSiblingAtIndex(parentNode, nodeIdx);
        if ((leftSiblingNode != null) && (leftSiblingNode.mCurrentKeyNum > Node.LOWER_BOUND_KEYNUM)) {
            // Do right rotate
            performRightRotation(node, nodeIdx, parentNode, leftSiblingNode);
        } else {
            rightSiblingNode = Node.getRightSiblingAtIndex(parentNode, nodeIdx);
            if ((rightSiblingNode != null) && (rightSiblingNode.mCurrentKeyNum > Node.LOWER_BOUND_KEYNUM)) {
                // Do left rotate
                performLeftRotation(node, nodeIdx, parentNode, rightSiblingNode);
            } else {
                // Merge the node with one of the siblings
                boolean bStatus;
                if (leftSiblingNode != null) {
                    bStatus = performMergeWithLeftSibling(node, nodeIdx, parentNode, leftSiblingNode);
                    mHeight--;
                } else {
                    bStatus = performMergeWithRightSibling(node, nodeIdx, parentNode, rightSiblingNode);
                    mHeight--;
                }

                if (!bStatus) {
                    return false;
                }
            }
        }

        return true;
    }


    /**
    * Re-balance the tree upward from the lower node to the upper node
    */
    private void rebalanceTree(Node<K> upperNode, Node<K> lowerNode, K key) {
        mStackTracer.clear();
        mStackTracer.add(new StackInfo(null, upperNode, 0));

        //
        // Find the child subtree (node) that ontologyContainsTuple the key
        //
        Node<K> parentNode;
        Node<K> childNode;
        KeyValuePair<K> currentKey;
        int i;
        parentNode = upperNode;
        while ((parentNode != lowerNode) && !parentNode.mIsLeaf) {
            currentKey = parentNode.mKeys[0];
            i = 0;
            while ((i < parentNode.mCurrentKeyNum) && (key.compareTo(currentKey.mKey) > 0)) {
                ++i;
                if (i < parentNode.mCurrentKeyNum) {
                    currentKey = parentNode.mKeys[i];
                } else {
                    --i;
                    break;
                }
            }

            if (key.compareTo(currentKey.mKey) > 0) {
                childNode = Node.getRightChildAtIndex(parentNode, i);
                if (childNode.mKeys[0].mKey.compareTo(parentNode.mKeys[parentNode.mCurrentKeyNum - 1].mKey) > 0) {
                    // The right-most side of the node
                    i = i + 1;
                }
            } else {
                childNode = Node.getLeftChildAtIndex(parentNode, i);
            }

            if (childNode == null) {
                break;
            }

            if (key.compareTo(currentKey.mKey) == 0) {
                break;
            }

            mStackTracer.add(new StackInfo(parentNode, childNode, i));
            parentNode = childNode;
        }

        boolean bStatus;
        StackInfo stackInfo;
        while (!mStackTracer.isEmpty()) {
            stackInfo = mStackTracer.pop();
            if ((stackInfo != null) && !stackInfo.mNode.mIsLeaf) {
                bStatus = rebalanceTreeAtNode(stackInfo.mParent,
                        stackInfo.mNode,
                        stackInfo.mNodeIdx,
                        REBALANCE_FOR_INTERNAL_NODE);
                if (!bStatus) {
                    break;
                }
            }
        }
    }

}
