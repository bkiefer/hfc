package de.dfki.lt.hfc.indices.btree;

import java.util.Set;

/**
 * Created by christian on 05/03/17.
 */
public interface BTreeIteratorIF<K extends Comparable> {

  public boolean item(K key, Set<int[]> value);

}
