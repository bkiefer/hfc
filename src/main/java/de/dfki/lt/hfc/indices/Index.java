package de.dfki.lt.hfc.indices;

import de.dfki.lt.hfc.types.AnyType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Set;


/**
 * This interface is used as a baseline for all kinds of indices supported by HFC.
 * It defines some basic functions each implementation must provide.
 * An index is used to significantly speed-up the query processing, which is especially useful for reasoning on streams.
 *
 * @see de.dfki.lt.hfc.Query
 * Created by christian on 21/12/16.
 */
public abstract class Index<Key extends AnyType> {


    /**
     * A basic LOGGER.
     */
    private static final Logger logger = LoggerFactory.getLogger(Index.class);


    public final int indexedPosition_start;
    public final int indexedPosition_end;
    /**
     * The number of indexed tuple.
     */
    int numberOfIndexedTuples = 0;



    public final  Class key;

    private final boolean atomIndex;

    public boolean intervalSupport(){
        return false;
    }

    public Index(Class key, int start, int end){
        this.key = key;
        this.indexedPosition_start = start;
        this.indexedPosition_end = end;
        atomIndex = start == end;
    }

    /**
     * Add a new entry to the Index.
     *
     * @param key the key  to be added.
     * @param value the value associated with the key
     */
    public void add(Key key, int[] value) {
      logger.debug("Adding key {0} value {1} mapping", key, Arrays.toString(value));
        numberOfIndexedTuples++;
        structureSpecificAdd(key, value);
    }

    /**
     * This is the data structure specific implementation of the add operation. Depending on which structure is used ,
     * e.g B-Tree or R-Tree, the process of adding an entry to the index might be different.
     *
     * @param key The key to be added, a key is always an instance of {@link Comparable}, in general an int
     * @param value The value associated with the key.
     */
    protected abstract void structureSpecificAdd(Key key, int[] value);

    /**
     * Removes the given entry from the Index. If the  key is not associated to other values, the node associated with the key will be entirely removed.
     *
     * @param key the key the value to be removed is associated with
     * @param value the value to be removed
     */
    public Set<int[]> remove(Key key, int[] value){
      logger.debug( "Removing key {0} value {1} mapping", key, Arrays.toString(value));
        numberOfIndexedTuples--;
        return structureSpecificRemove(key,value);
    }

    /**
     * Removes the given node associated with the given key from the index.
     *
     * @param key the key the value to be removed is associated with
     */
    public Set<int[]> remove(Key key){
      logger.debug("Removing all values for key {0} ", key);
        numberOfIndexedTuples--;
        return structureSpecificRemove(key, null);
    }

    /**
     * This is the data structure specific implementation of the remove operation. Depending on which structure is used ,
     * e.g B-Tree or R-Tree, the process of removing an entry from the index might be different.
     * @param key The key the value to be removed is associated with.
     * @param value The value to be removed from the index.
     */
    protected abstract Set<int[]> structureSpecificRemove(Key key, int[] value);

    /**
     * This method searches the index for the value(s) associated with the given Key.
     *
     * @param key The key to be looked up.
     * @return The Values associated with the key.
     */
    public  Set<int[]> search(Key key){
      logger.debug( "Searching values associated with key {0}", key);
      return structureSpecificSearch(key);
    }

    protected abstract Set<int[]> structureSpecificSearch(Key key);



    public  Set<int[]> searchInterval(Key start, Key end){
      logger.debug("Searching values associated with interval {0} - {1}", start, end);
      return structureSpecificIntervalSearch(start,end);
    }

  protected abstract Set<int[]> structureSpecificIntervalSearch(Key start, Key end);


    /**
     * Overrides the default toString() method of {@link Object}.
     *
     * @return A {@link String} representation of the index, containing all mappings
     * in the index.
     */
    public abstract String toString();


    /**
     *
     * @return The number of keys used in the index.
     */
    public abstract long size();

    /**
     *
     * @return The height/depth of the indexing structure
     */
    public abstract int height();

    /**
     * Clear the index.
     */
    public abstract void clear();

  public boolean isAtomIndex() {
    return atomIndex;
  }
}
