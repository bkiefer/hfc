package de.dfki.lt.hfc.indices.btree;

import java.util.Set;

/**
 * Key value pair used alongside pointers in the tree nodes of {@link BTree}
 *
 * Created by christian on 05/03/17.
 */
public class KeyValuePair <K extends Comparable> {
        protected K mKey;
        protected Set<int[]> mValue;

        public KeyValuePair(K key, Set<int[]> value) {
            mKey = key;
            mValue = value;
        }

        @Override
        public String toString(){
            return "["+this.mKey + ", "+ this.mValue+"]";
        }
    }

