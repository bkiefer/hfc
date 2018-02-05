package de.dfki.lt.hfc.indices.btree;

/**
 * StackInfo for tracing-back purpose
 * Structure contains parent node and node index
 * Created by christian on 06/03/17.
 */
public class StackInfo <K extends Comparable>{
    public Node<K> mParent = null;
    public Node<K> mNode = null;
    public int mNodeIdx = -1;

    public StackInfo(Node<K> parent, Node<K> node, int nodeIdx) {
        mParent = parent;
        mNode = node;
        mNodeIdx = nodeIdx;
    }
}
