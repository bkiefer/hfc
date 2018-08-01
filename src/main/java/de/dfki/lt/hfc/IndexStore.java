package de.dfki.lt.hfc;

import de.dfki.lt.hfc.indices.IndexingException;
import de.dfki.lt.hfc.indices.Index;
import de.dfki.lt.hfc.indices.AdvancedIndex;
import de.dfki.lt.hfc.qrelations.QRelation;
import de.dfki.lt.hfc.types.AnyType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * The {@link IndexStore} is the junction between the actual indexing structures, i.e., the trees,
 * and the other components of the {@link ForwardChainer}, especially the {@link TupleStore}. <p>
 * The {@link IndexStore} maintains a primary index and an optional secondary index. This was done
 * due to the fact, that this enables the user to specify one indexing structure for general
 * lookups, e.g. point in times representing Transaction times, and a second index for more
 * elaborated queries, e.g. interval relations regarding the Valid time of facts. <p> The {@link
 * IndexStore} can be configured using a so called .idx files, which specified the primary and
 * secondary used indexing structure, as well as the position and type of the values to be indexed.
 * <p> Created by christian on 23/12/16.
 */
public class IndexStore {

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  /**
   * A basic LOGGER.
   */
  private static final Logger logger = LoggerFactory.getLogger(IndexStore.class);



  // last '.' char is OK!
  public static final String INDEX_PATH = "de.dfki.lt.hfc.indices.";

  public static final String TYPE_PATH = "de.dfki.lt.hfc.types.";

  /**
   * this setting is used for input encoding in IndexStore
   */
  private String inputCharacterEncoding = "UTF-8";

  /**
   * this setting is used for output encoding in TupleStore
   */
  private String outputCharacterEncoding = "UTF-8";

  /**
   * The {@link Index} represents the data structure which is used to allow a quick and effective
   * access to the information.
   */
  private Index primaryIndex;

  private Index secundaryIndex;

  /**
   * A Class, representing the key which defines what is to be indexed.
   */
  public Class primIndexKey;

  /**
   * A Class, representing the secondary key which defines what is to be stored in the optional
   * index.
   */
  private Class secIndexKey;

  /**
   * A Class, defining which datastructure is used in the primary index.
   */
  private Class primIndexBackend;

  /**
   * A Class, defining which datastructure is used in the optional secondary index.
   */
  private Class secIndexBackend;


  /**
   * The instance of {@link TupleStore} this instance {@link IndexStore} is associated with.
   */
  private TupleStore tupleStore;


//  /**
//   * Creates a new instance of {@link IndexStore} according to the specification in the given file.
//   */
//  public IndexStore(String indexFile, boolean verbose) throws IndexingException {
//    logger.debug("Initializing indexStore ...");
//    readIndex(indexFile);
//    logger.debug( "... successfully initialized");
//  }


  public IndexStore(Map<String, Object> config) throws IndexingException {
    readIndex(config);
  }




  /**
   * Lookup the values associated with a specific key, by searching the given key in the Index.
   *
   * @param key The date to be looked up.
   * @return A Set of tuples associated with the given key/the given date.
   */
  public Set<int[]> lookup(AnyType key) {
    logger.debug( "Lookup key: {0} ", key);
    return this.primaryIndex.search(key);
  }


  /**
   * Lookup the values associated with all keys between key1 and key2. Values for key1 and key2
   * included. The interval is defined by a start point and an end point.
   *
   * @param key1 The key where the interval starts.
   * @param key2 The key where the interval ends.
   * @return The values associated with all keys between the given ones.
   */
  public Set<int[]> lookup(AnyType key1, AnyType key2) {
    logger.debug( "Lookup interval: {0} - {1} ", key1, key2);
    if (secundaryIndex != null) {
      return this.secundaryIndex.searchInterval(key1, key2);
    }
    return this.primaryIndex.searchInterval(key1, key2);
  }


  /**
   * This method parses the index file to obtain knowledge regarding: 1) The Key used by the index
   * -> Key: <ClassName of XsdType> 2) The Position of the key in a tuple -> Position: <integer
   * representing the key's position> 3) The Indexing structure to be used -> Structure: One of
   * <Simple>, <B-Tree>,<B+Tree>, <R-Tree> or<R+Tree> 4) A simple time stamp for more convenience
   */
  private void readIndex(Map<String, Object> config) throws IndexingException {
    // These int values define the positions in the tuples where the terms to be indexed by the primary index should be located.
    // In case atomic types are indexed, both values are the same.
    int position_prime_start = -1, position_prime_end = -1;
    // These int values define the positions in the tuples where the terms to be indexed by the secondary index should be located.
    // In case atomic types are indexed, both values are the same.
    int position_second_start = -1, position_second_end = -1;
    for(Map.Entry<String, Object> entry : config.entrySet()){
      switch (entry.getKey()) {
        case "Key1": {
          try {
            this.primIndexKey = Class.forName(TYPE_PATH + entry.getValue());
          } catch (ClassNotFoundException e) {
            logger.error(e.getStackTrace().toString());
          }
          break;
        }
        case "Key2": {
          try {
            this.secIndexKey = Class.forName(TYPE_PATH + entry.getValue());
          } catch (ClassNotFoundException e) {
            logger.error(e.getStackTrace().toString());
          }
          break;
        }
        case "Position1": {
          String value = (String) entry.getValue();
          if (value.contains(",")) {
            String[] interval = value.split(",");
            position_prime_start = Integer.parseInt(interval[0]);
            position_prime_end = Integer.parseInt(interval[1]);
          } else {
            position_prime_start = position_prime_end = Integer.parseInt(value);
          }
          break;
        }
        case "Position2": {
          String value = (String) entry.getValue();
          if (value.contains(",")) {
            String[] interval = value.split(",");
            position_second_start = Integer.parseInt(interval[0]);
            position_second_end = Integer.parseInt(interval[1]);
          } else {
            position_second_start = position_second_end = Integer.parseInt(value);
          }
          break;
        }
        case "Structure1": {
          try {
            this.primIndexBackend = Class.forName(INDEX_PATH + entry.getValue());

          } catch (ClassNotFoundException e) {
            logger.error(e.getStackTrace().toString());
          }
          break;
        }
        case "Structure2": {
          try {
            this.secIndexBackend = Class.forName(INDEX_PATH + entry.getValue());

          } catch (ClassNotFoundException e) {
            logger.error(e.getStackTrace().toString());
          }
          break;
        }
        default: {
          logger.warn("Unknown parameter" + entry.getKey() );
        }
      }
    }

    //check whether all necessary values are present
    if (primIndexBackend == null || position_prime_start == -1 || primIndexKey == null) {
      throw new IndexingException("Not able to create Index. Important parameter(s) missing.");
    }
    // In case there is no position for the secondary index defined, use the one from the primary index.
    // This way we can assign two different structures to the same positions.
    if (position_second_start == -1) {
      position_second_start = position_prime_start;
      position_second_end = position_prime_end;
    }

    try {
      this.primaryIndex = (Index) primIndexBackend.getConstructor(Class.class, int.class, int.class)
              .newInstance(primIndexKey, position_prime_start, position_prime_end);
      if (secIndexBackend != null) {
        this.secundaryIndex = (Index) secIndexBackend
                .getConstructor(Class.class, int.class, int.class)
                .newInstance(secIndexKey, position_second_start, position_second_end);
      }
    } catch (Exception e) {
      logger.error(e.getStackTrace().toString());
    }
  }

  /**
   * Writes a representation of the indexStore, meaning, primary (and if used secondary) index to
   * the given destination. The file is structured as follows:
   */
  private void write(String file) {
    Path path = Paths.get(file);
    try (BufferedWriter writer = Files.newBufferedWriter(path)) {
      //TODO
    } catch (IOException e) {
      logger.error("Something went wrong while writing the index to " + file);
    }
  }

  /**
   * This method is used to update the index store whenever a new tuple is added to the {@link
   * TupleStore}. This way, both cases:    1) reading in a .nt file 2) adding new tuples while
   * closure computing can be covered. The method is called from the corresponding functions of the
   * {@link TupleStore}.
   *
   * @param tuple the tuple to be added.
   */
  public void update(int[] tuple) {
    // no specific position for sec index
    if (primaryIndex.isAtomIndex()) {
      addAtomToIndex(this.primaryIndex, tuple, this.primIndexKey);
    }
    // intervals
    else {
      addIntervalToIndex(this.primaryIndex, tuple, this.primIndexKey);
    }
    if (secundaryIndex != null) {
      if (secundaryIndex.isAtomIndex()) {
        addAtomToIndex(this.secundaryIndex, tuple, this.secIndexKey);
      }
      // intervals
      else {
        addIntervalToIndex(this.secundaryIndex, tuple, this.secIndexKey);
      }
    }
  }


  private void addAtomToIndex(Index index, int[] tuple, Class key) {
    AnyType type = this.tupleStore.getJavaObject(tuple[index.indexedPosition_start]);
    if (type.getClass() == key) {
      index.add(type, tuple);
    }
  }

  private void addIntervalToIndex(Index index, int[] tuple, Class key) {
    AnyType type_start = this.tupleStore.getJavaObject(tuple[index.indexedPosition_start]);
    AnyType type_end = this.tupleStore.getJavaObject(tuple[index.indexedPosition_end]);
    if (type_start.getClass() == key && type_end.getClass() == key) {
      if (index.intervalSupport()) {
        ((AdvancedIndex) index).addInterval(type_start, type_end, tuple);
      }
    }
  }


  /**
   * Simple setter method for the tuplestore field.
   */
  public void setTuplestore(TupleStore tuplestore) {
    this.tupleStore = tuplestore;
  }

  /**
   * Simple getter returning the primarily used index.
   *
   * @return the primarily used index.
   */
  public Index getPrimIndex() {
    return primaryIndex;
  }

  /**
   * Simple getter returning the secondarily used index.
   *
   * @return the secondarily used index.
   */
  public Index getSecIndex() {
    return secundaryIndex;
  }

  /**
   * This method returns the size of the primary index. It is mostly used by the JUnit test.
   *
   * @return the size of the primary index.
   */
  public long size() {
    return this.primaryIndex.size();
  }

  /**
   * This method returns the size of the secondary index, if defined. It is mostly used by the JUnit
   * test.
   *
   * @return the size of the primary index.
   */
  public long secSize() {
    if (this.secundaryIndex != null) {
      return this.secundaryIndex.size();
    }
    return 0;
  }


  /**
   * TODO switch lkps and return set such that the description matches the actual function
   * Creates instances of {@link IndexLookup}, if the given close matches either the primary or the
   * secondary index.
   *
   * @param clause The clause to be indexed. This clause is an integer representation of a where
   * clause of a query.
   * @param idToRelation a mapping of ids to instances of {@link QRelation}s.
   * @param lkps
   * @param relationsToBeRewritten
   * @return A Set of {@link IndexLookup}s associated with the given clause.
   */
  public boolean prepareLookup(List<Integer> clause,
                               Map<Integer, QRelation> idToRelation,
                               Set<IndexLookup> lkps, Set<QRelation> relationsToBeRewritten) {
    boolean isApplicable;
    isApplicable = checkIndexApplicability(primaryIndex, clause, idToRelation, lkps, relationsToBeRewritten);
    if (secundaryIndex != null) {
      isApplicable= checkIndexApplicability(secundaryIndex, clause, idToRelation, lkps, relationsToBeRewritten);
    }
    return !isApplicable;
  }

  private boolean checkIndexApplicability(Index index,
                                          List<Integer> clause,
                                          Map<Integer, QRelation> idToRelation, Set<IndexLookup> lkps, Set<QRelation> notApplicableRelations) {
    boolean isApplicable;
    if (index.isAtomIndex()) {
      isApplicable = createAtomLookup(index, clause, lkps, idToRelation, notApplicableRelations);
    } else {
      isApplicable = createIntervalLookup(index, clause, lkps, idToRelation, notApplicableRelations);
    }
    return isApplicable;
  }

  /**
   * Creates an atomic {@link IndexLookup} in case either the given index is matched.
   *  @param index The index the lookup will be associated with.
   * @param clause The clause to be looked up.
   * @param lkps a set of lkps. this set will potentially be extended by this method.
   * @param idToRelation a mapping of ids to instances of {@link QRelation}s.
   * @param notApplicableRelations
   */
  private boolean createAtomLookup(Index index, List<Integer> clause, Set<IndexLookup> lkps,
                                   Map<Integer, QRelation> idToRelation, Set<QRelation> notApplicableRelations) {
    int id = clause.get(index.indexedPosition_start);
    logger.debug( "Create A lookup for {0}",clause);
    QRelation r;
    //Check whether the id is a variable
    if (id < 0) {
      // Check whether the variable is associated with an QRelation
      if (!idToRelation.containsKey(id))
      //if not return;
      {
        return false;
      } else {
        logger.debug("id {0}  is associated with {1}", id,  idToRelation.get(id));
        //create a lookup object for the given represented QRelation
        r = idToRelation.get(id);
        if (r.getType() == index.key) {
          if (!relationIsApplicable(index, r)) {
            logger.debug("Relation is not applicable");
            notApplicableRelations.add(r);
            return false;
          }
          lkps.add(new IndexLookup(this.tupleStore, index, clause, index.indexedPosition_start, r));
          idToRelation.remove(id);
        }
      }
    } else {
      // if the id is no variable but a constant and the constant has the correct type, create a lookup for this constant.
      if (tupleStore.getJavaObject(id).getClass() == index.key) {
        lkps.add(new IndexLookup(this.tupleStore, index, clause, index.indexedPosition_start, null));
      }
    }
    return true;
  }

  /**
   * Creates an interval {@link IndexLookup} in case either the given index is matched.
   *  @param index The index the lookup will be associated with.
   * @param clause The clause to be looked up.
   * @param lkps a set of lkps. this set will potentially be extended by this method.
   * @param idToRelation a mapping of ids to instances of {@link QRelation}s.
   * @param notApplicableRelations
   */
  private boolean createIntervalLookup(Index index, List<Integer> clause,
                                       Set<IndexLookup> lkps, Map<Integer, QRelation> idToRelation, Set<QRelation> notApplicableRelations) {
    int ids = clause.get(index.indexedPosition_start);
    int ide = clause.get(index.indexedPosition_end);
    QRelation r;
    logger.debug("Create B lookup for {0}", clause);
    //Check whether the id is a variable
    if (ids < 0 || ide < 0) {
      // Check whether the variable is associated with an QRelation
      if (!(idToRelation.containsKey(ids) && idToRelation.containsKey(ide))) {
        logger.debug("No match for {0} and {1}", ids, ide);
        return false;
      } else {
        //create a lookup object for the given represented QRelation
        logger.debug("id {0} is associated with {1}", ids , idToRelation.get(ids));
        r = idToRelation.get(ids);
        if (r.getType() == index.key) {
          if (!relationIsApplicable(index, r)) {
            logger.debug("Relation is not applicable");
            notApplicableRelations.add(r);
            return false;
          }
          lkps.add(
                  new IndexLookup(this.tupleStore, index, clause, index.indexedPosition_start, index.indexedPosition_end, r));
          idToRelation.remove(ids);
          idToRelation.remove(ide);
        }
      }
    } else {
      // if the id is no variable but a constant and the constant has the correct type, create a lookup for this constant.
      if (tupleStore.getJavaObject(ids).getClass() == index.key
              && tupleStore.getJavaObject(ide).getClass() == index.key) {
        lkps.add(
                new IndexLookup(this.tupleStore, index, clause, index.indexedPosition_start, index.indexedPosition_end,
                        null));
      }
    }
    return true;
  }

  private boolean relationIsApplicable(Index index, QRelation r) {
    logger.debug("isAllen {0}, isInterval {1}, intervalSupport {2}", r.isAllenRelation(),r.isInterval(),index.intervalSupport());
    if(r.isAllenRelation()) {
      if (!r.isInterval() && index.intervalSupport())
        return true;
      if (r.isInterval() && !index.intervalSupport())
        return true;
    }
    return false;
  }

}