package de.dfki.lt.hfc;

import static de.dfki.lt.hfc.LiteralManager.*;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import de.dfki.lt.hfc.types.AnyType;
import de.dfki.lt.hfc.types.BlankNode;
import de.dfki.lt.hfc.types.Uri;
import de.dfki.lt.hfc.types.Variable;
import de.dfki.lt.hfc.types.XsdAnySimpleType;
import gnu.trove.list.array.TIntArrayList;
import gnu.trove.map.hash.TCustomHashMap;
import gnu.trove.map.hash.TIntIntHashMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import gnu.trove.set.hash.TCustomHashSet;
import gnu.trove.set.hash.THashSet;

/**
 * tuples are represented as int[] in order to save space;
 * although container objects (embodying the int[]) are easier to
 * handle -- we can define proper equals() and hashCode() methods for
 * them -- I have opted for simple plain int[];
 * this, however, requires that we need our own tuple set class, originally
 * called IntArrayHashSet (plus IntArrayHashMap), since the standard equals()
 * and hashCode() for int[] yield wrong results;
 * later, I switched to Trove's THashSet and THashMap classes which further
 * speed up runtime performance
 * <p>
 * since we allow for tuples of arbitrary length, we have decided
 * against reification, i.e., against tuples containing (sub-)tuples;
 * reification furthermore makes the matching phase of a forward chainer
 * (destructuring!) more complex, sinces simple and effective matching
 * techniques are no longer applicable
 * <p>
 * the index data structure is used to find all those tuples which
 * contain an object represented as an int at a specific position int
 * the tuple -- use getTuples(int pos, int obj) to obtain a Set<Tuple>;
 * in order to perform more complex queries we can either use set operations
 * on the returned Set<Tuple> or by creating more complex index keys,
 * e.g., in case of a triple to have keys that look at the 1st+2nd, 1st+3rd,
 * 2nd+3rd, and 1st+2nd+3rd position, resp.
 * it is very likely that complex indexes are too expensive in terms of memory,
 * since the corresponding sets are only sparsely populated;
 * note that value identity between tuple positions is NOT encoded in the index;
 * furthermore, a restriction on the length of a tuple is also NOT built into
 * the index
 *
 * @author (C) Hans-Ulrich Krieger
 * @version Wed Jun 22 15:20:51 CEST 2016
 * @see de.dfki.lt.hfc.TIntArrayHashingStrategy
 * @see de.dfki.lt.hfc.RuleStore
 * @see de.dfki.lt.hfc.ForwardChainer
 * @since JDK 1.5
 */
public class TupleIntStore {

  //private static final Logger logger = LoggerFactory.getLogger(TupleStore.class);

  /**
  * the default hashing (and equals) strategy for tuples from the tuple store
  * (all positions of a tuple are taken into account);
  * make it static, so that it can be reused for the sets at the leaves of the
  * index!
  */
  protected static TIntArrayHashingStrategy DEFAULT_HASHING_STRATEGY = new TIntArrayHashingStrategy();

  public IndexStore indexStore;

  /**
  * an optimization, currently applicable only to
  * + owl:sameAs
  * + owl:equivalentClass
  * + owl:equivalentProperty
  * but not to general equivalence relations, i.e., relations which are
  * reflexive, symmetric, and transitive;
  * this optimization influences input/output of methods from the following
  * Java classes:
  * + TupleStore
  * + RuleStore
  * + Query
  * NOTE: once tuples have been read into the tuple store, do NOT change the
  * value of _this_ field !!
  * NOTE: assigning a URI (in subject or object position) to different equivalence
  * relations, e.g.,
  * <a> <owl:sameAs> <b>
  * <a> <owl:equivalentProperty> <c>
  * might lead to wrong results when querying a repository (at the same time,
  * this is only allowed in OWL Full);
  * NOTE: make sure to load the right rule file, depending on the value of this
  * field!!
  */
  public boolean equivalenceClassReduction = true;

  /**
  * specifies the position of the subject, predicate, and object in an RDF triple
  * 0 : subject
  * 1 : predicate
  * 2 : object
  * in case we would move the predicate to the front, predicatePosition must be
  * set to 0, subject to 1, and object to 2, even if we would allow for tuples
  * of length > 3 (interpret this as "the object, given as a Cartesian product,
  * starts at position 2);
  * other settings would include polarity information, transaction and valid time
  */
  public int subjectPosition = 0;
  public int predicatePosition = 1;
  public int objectPosition = 2;

  /**
  * this constant is used to create the right number of index tables;
  * note that the value of this constant has an effect on the index data
  * structure; the larger the number, the more tables are created;
  * reasonable values seem to be 3 (RDF triple), 4 (time or negation, 5 (time),
  * or 6 (time and negation);
  * a similar variable exists in class RuleStore
  */
  public int maxNoOfArgs = 5;

  /**
  * used during input, when URIs, blank nodes, or XSD atoms are replaced by their IDs (ints)
  */
  public HashMap<AnyType, Integer> objectToId;

  /**
  * noOfAtoms initializes internal data structures in TupleStore with a predefined size;
  * making a good guess how many atoms will be employed in an application avoid re-sizing
  * and copying
  */
  public int noOfAtoms = 100000;

  /**
  * noOfTuples initializes internal data structures in TupleStore with a predefined size;
  * making a good guess how many atoms will be employed in an application avoid re-sizing
  * and copying
  */
  public int noOfTuples = 500000;

  /**
  * this field serves a different purpose compared to field ForwardChainer.generationCounter
  * and is solely used when tuple deletion is enabled in the forward chainer;
  * this field is incremented by 1 before closure computation is called and is incremented
  * again after closure computation;
  * this strategy makes sure that the entailed tuples are assigned an ODD generation number,
  * whereas uploaded tuples are automatically assigned an EVEN number
  */
  protected int generation = 0;

  /**
  * a special field that is initialized by an empty map from tuples (int[]) to generations
  * (Integer) in case tuple deletion is switched on through method enableTupleDeletion() in
  * the forward chainer
  */
  protected TCustomHashMap<int[], Integer> tupleToGeneration = null;

  /**
  * a mapping from the internal representation of URIs (ints) to their
  * representatives/proxies, again internal representation of URIs;
  * only URIs mentioned in sameAs, equivalentClass, and equivalentProperty
  * statements are entered here (at the moment)
  */
  protected TIntIntHashMap uriToProxy;

  /**
  * a mapping from a proxy to its equivalence class, represented as an
  * array list of ints
  */
  protected TIntObjectHashMap<TIntArrayList> proxyToUris;

  /**
  * a mapping from URI in subject/object position to the equivalence
  * relation name (internal ID) in predicate position
  */
  protected TIntIntHashMap uriToEquivalenceRelation;

  /**
  * a mapping used by tests & actions to speed up processing
  */
  protected ArrayList<AnyType> idToJavaObject;
  /**
  * the index data structure is used to find all those tuples which contain an
  * object represented as an int at a specific position;
  * <p>
  * NOTE: it _might_ be better to use Trove's TIntObjectHashMap class here
  * checked it, even slightly _worse_ (HUK)
  */
  protected Map<Integer, Set<int[]>>[] index;

  /**
  * a set of all tuples known to TupleStore
  */
  protected Set<int[]> allTuples;

  /**
  * this registry object gathers the functional operators potentially used
  * during querying (FILTER) and forward chaining (LHS matching via predicates
  * and RHS generation of new individuals via functions), and so during the
  * interactive mode;
  * since all these modes need a tuple store and since the registry requires
  * the tuple store as an internal state, we automatically constructs a new
  * registry every time a new tuple store is build
  */
  private OperatorRegistry operatorRegistry;

  /**
  * this registry object gathers the aggregational operators potentially used
  * during querying (AGGREGATE) and forward chaining (all-rules), and so during
  * the interactive mode;
  * since all these modes need a tuple store and since the registry requires
  * the tuple store as an internal state, we automatically constructs a new
  * registry every time a new tuple store is build
  */
  protected AggregateRegistry aggregateRegistry;

  /**
  * URIs and XSD literals are replaced when they are read in by their IDs,
  * usually ints (perhaps use longs if there are too many atoms), starting
  * with 0
  * the value 0 is used during rule application to indicate that a variable
  * is unbound or its value is not of interest;
  * negative integers represent (local) variable names
  */
  private int currentId = 0;

  /**
   * init form that "outsources" initialization code that needs to be duplicated by the
   * binary (that is used be several other constructors) and 10-ary constructor
   */
  @SuppressWarnings("unchecked")
  protected void init(Config config, IndexStore is,
      OperatorRegistry opRegistry, AggregateRegistry aggOpRegistry) {
    this.operatorRegistry = opRegistry;
    this.aggregateRegistry = aggOpRegistry;

    this.noOfAtoms = config.getNoOfAtoms();
    this.noOfTuples = config.getNoOfTuples();
    this.equivalenceClassReduction = config.isEqReduction();
    this.maxNoOfArgs = config.getMaxArgs();
    this.subjectPosition = config.getSubjectPosition();
    this.predicatePosition = config.getPredicatePosition();
    this.objectPosition = config.getObjectPosition();

    this.objectToId = new HashMap<AnyType, Integer>(noOfAtoms);
    this.idToJavaObject = new ArrayList<AnyType>(noOfAtoms);
    this.uriToProxy = new TIntIntHashMap();
    this.proxyToUris = new TIntObjectHashMap<TIntArrayList>();
    this.uriToEquivalenceRelation = new TIntIntHashMap();
    // specify mappings here that stay constant for efficiency reasons (e.g.,
    // used by operators)
    initializeUriMappings();
    // unfortunately, no generic array creation in Java -- use java.lang.reflect
    // capability instead; for each array position, generate an empty
    // HashMap<Integer, Set<Tuple>> object
    this.index = (HashMap[]) Array.newInstance(HashMap.class, maxNoOfArgs);
    for (int i = 0; i < maxNoOfArgs; i++)
      this.index[i] = new HashMap<Integer, Set<int[]>>();
    this.allTuples = new TCustomHashSet<int[]>(
        TupleIntStore.DEFAULT_HASHING_STRATEGY, noOfTuples);
    this.indexStore = is;
  }

  /**
  * (should) only (be) used by copyTupleStore()
  */
  protected TupleIntStore() {}

  /**
  * a simple STATIC method, translating an int array into something readable (N-tuple syntax)
  *
  public static void printTuple(int[] tuple, ArrayList<String> mapping) {
    logger.debug(toString(tuple, mapping));
  }
  */

  /**
  * a simple STATIC method, translating an int array into something readable (N-tuple syntax)
  */
  public static String toString(int[] tuple, ArrayList<String> mapping) {
    StringBuilder sb = new StringBuilder();
    for (int element : tuple)
      sb.append(mapping.get(element)).append(' ');
    sb.append('.');
    return sb.toString();
  }

  /**
  * internal representation: constants are positive numbers
  */
  public static boolean isConstant(int id) {
    return (id > 0);
  }

  /**
  * variables in a rule or query tuple start with a '?' char and are followed by a string,
  * containing no whitespace chars;
  * since variables make no reference to a namespace, we make this a static method
  */
  public static String parseVariable(StringTokenizer st,
      ArrayList<String> tuple) {
    // '?' already consumed
    StringBuilder sb = new StringBuilder("?");
    sb.append(st.nextToken()); // the rest of the URI
    String var = sb.toString();
    tuple.add(var);
    return var;
  }

  /**
  * @return false otherwise
  * <p>
  * there is a similar method in class ForwardChainer
  */
  private boolean tupleDeletionEnabled() {
    return (this.tupleToGeneration != null);
  }

  /**
  * specify mappings here that stay constant for efficiency reasons, independent of
  * whether these URIs are seen first at different places in a file that is read in;
  * currently, the following mappings are predefined for the following URIs (URI : id):
  * NULL : 0  (not used at the moment)
  * rdfs:subClassOf : 1
  * owl:sameAs : 2
  * owl:equivalentClass : 3
  * owl:equivalentProperty : 4
  * owl:disjointWith : 5
  */
  private void initializeUriMappings() {
    this.objectToId.put(NamespaceManager.UNBOUND, NamespaceManager.UNBOUND_ID);
    this.idToJavaObject.add(NamespaceManager.UNBOUND);
    this.objectToId.put(NamespaceManager.RDFS_SUBCLASSOF_SHORT,
        NamespaceManager.RDFS_SUBCLASSOF_ID);
    this.idToJavaObject.add(NamespaceManager.RDFS_SUBCLASSOF_SHORT);
    this.objectToId.put(NamespaceManager.OWL_SAMEAS_SHORT,
        NamespaceManager.OWL_SAMEAS_ID);
    this.idToJavaObject.add(NamespaceManager.OWL_SAMEAS_SHORT);
    this.objectToId.put(NamespaceManager.OWL_EQUIVALENTCLASS_SHORT,
        NamespaceManager.OWL_EQUIVALENTCLASS_ID);
    this.idToJavaObject.add(NamespaceManager.OWL_EQUIVALENTCLASS_SHORT);
    this.objectToId.put(NamespaceManager.OWL_EQUIVALENTPROPERTY_SHORT,
        NamespaceManager.OWL_EQUIVALENTPROPERTY_ID);
    this.idToJavaObject.add(NamespaceManager.OWL_EQUIVALENTPROPERTY_SHORT);
    this.objectToId.put(NamespaceManager.OWL_DISJOINTWITH_SHORT,
        NamespaceManager.OWL_DISJOINTWITH_ID);
    this.idToJavaObject.add(NamespaceManager.OWL_DISJOINTWITH_SHORT);

    // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    // VERY IMPORTANT: UPDATE currentId !!!!!!!
    // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    this.currentId = 5;
  }

  /**
  * three cases to distinguish here:
  * (1) left and right are _not_ keys in uriToProxy
  * (2) left xor right is a key in uriToProxy
  * (3) left and right are keys in uriToProxy
  * <p>
  * NOTE: we do make this method synchronized, since certain rule operators
  * use this method
  */
  protected synchronized void addEquivalentElements(int left, int right) {
    if (this.uriToProxy.containsKey(left)) {
      if (this.uriToProxy.containsKey(right)) {
        // (3)
        final int leftProxy = this.uriToProxy.get(left);
        final int rightProxy = this.uriToProxy.get(right);
        // in case both proxies are the same: nothing to do
        if (leftProxy == rightProxy)
          return;
        // now choose leftProxy as _the_ proxy
        final int[] rightElements = this.proxyToUris.get(rightProxy).toArray();
        for (int e : rightElements) {
          this.uriToProxy.put(e, leftProxy);
        }
        this.proxyToUris.get(leftProxy).add(rightElements);
        this.proxyToUris.remove(rightProxy);
      } else {
        // (2)
        final int proxy = this.uriToProxy.get(left);
        this.uriToProxy.put(right, proxy);
        this.proxyToUris.get(proxy).add(right);
      }
    } else {
      if (this.uriToProxy.containsKey(right)) {
        // (2) -- duplicate code with different arguments
        final int proxy = this.uriToProxy.get(right);
        this.uriToProxy.put(left, proxy);
        this.proxyToUris.get(proxy).add(left);
      } else {
        // (1)
        // always choose left as _the_ proxy
        this.uriToProxy.put(left, left);
        this.uriToProxy.put(right, left);
        this.proxyToUris.put(left,
            new TIntArrayList(new int[] { left, right }));
      }
    }
  }

  /**
  * given a tuple, isEquivalenceRelation() determines whether the predicate
  * in position this.predicatePosition is an equivalence relation;
  * currently, owl:sameAs, owl:equivalentClass, and owl:equivalentProperty
  * are recognized here as given by the integers
  * NamespaceManager.OWL_SAMEAS_ID
  * NamespaceManager.OWL_EQUIVALENTCLASS_ID
  * NamespaceManager.OWL_EQUIVALENTPROPERTY_ID
  */
  protected boolean isEquivalenceRelation(int[] tuple) {
    // perhaps use an int set plus an element test here if more relations are
    // involved
    final int pred = tuple[this.predicatePosition];
    return ((pred == NamespaceManager.OWL_SAMEAS_ID)
        || (pred == NamespaceManager.OWL_EQUIVALENTCLASS_ID)
        || (pred == NamespaceManager.OWL_EQUIVALENTPROPERTY_ID));
  }

  /**
  * returns the proxy for URI uri iff uri is a key in uriToProxy;
  * otherwise, uri is returned
  */
  protected final int getProxy(int uri) {
    if (this.uriToProxy.containsKey(uri))
      return this.uriToProxy.get(uri);
    else
      return uri;
  }

  /**
  * remove those tuples from the set of all tuples and from the index which have
  * an equivalence relation in predicate position;
  * only keep exactly one triple for each proxy p with its corresponding
  * equivalence relation r:
  * p r p
  *
  * @return the number of tuples being removed
  */
  public int cleanUpTupleStore() {
    // do NOT remove tuples from set/index while iteration is going on
    final Set<int[]> toBeRemoved = new THashSet<int[]>();
    // find equivalence class statements: either move over the set of tuples:
    // for (int[] tuple : this.allTuples)
    // if (isEquivalenceRelation(tuple))
    // toBeRemoved.add(tuple);
    // OR better: use the index (less triples to iterate over)
    toBeRemoved.addAll(
        getTuples(this.predicatePosition, NamespaceManager.OWL_SAMEAS_ID));
    toBeRemoved.addAll(getTuples(this.predicatePosition,
        NamespaceManager.OWL_EQUIVALENTCLASS_ID));
    toBeRemoved.addAll(getTuples(this.predicatePosition,
        NamespaceManager.OWL_EQUIVALENTPROPERTY_ID));
    // remove ER tuples from store (set & index) and update uriToProxy and
    // proxyToUris,
    // but also record the equivalence relation in which subject and object are
    // related to
    for (int[] tuple : toBeRemoved) {
      removeTuple(tuple);
      addEquivalentElements(tuple[this.subjectPosition],
          tuple[this.objectPosition]);
      // do we need both entries here since subject position is used as THE
      // proxy
      this.uriToEquivalenceRelation.put(tuple[this.subjectPosition],
          tuple[this.predicatePosition]);
      this.uriToEquivalenceRelation.put(tuple[this.objectPosition],
          tuple[this.predicatePosition]);
    }
    // iterate over remaining tuples and replace uris by their proxies, if
    // necessary;
    // do NOT modify tuples in set & index, but instead delete old and add new
    // cleaned-up tuples;
    // note: the local rule fields are NOT updated
    final Set<int[]> toBeUpdated = new THashSet<int[]>();
    // for (int[] tuple : this.allTuples)
    // for (int i = 0; i < tuple.length; i++)
    // if (this.uriToProxy.containsKey(tuple[i])) {
    // toBeUpdated.add(tuple);
    // break;
    // }
    // again, use the index, instead of iterating over all tuples
    for (int i : this.uriToProxy.keys()) {
      for (int j = 0; j < this.maxNoOfArgs; j++)
        toBeUpdated.addAll(getTuples(j, i));
    }
    int[] newTuple;
    for (int[] tuple : toBeUpdated) {
      int gennum = removeTupleReturnGeneration(tuple); // = -1 if tuple deletion
                                                       // is disabled
      // generate brand-new int tuple, since old array might still be shared at
      // several places,
      // e.g., in a local rule field or in a former copy of the forward chainer
      // (@see LTWorld)
      newTuple = new int[tuple.length];
      for (int i = 0; i < tuple.length; i++) {
        if (this.uriToProxy.containsKey(tuple[i]))
          newTuple[i] = this.uriToProxy.get(tuple[i]);
        else
          newTuple[i] = tuple[i];
      }
      addTupleWithGeneration(newTuple, gennum);
    }
    // and finally add reflexive ER proxy pattern (see above), but take care of
    // potential
    // front and back elements that also need to be taken over
    int[] newReflexiveTuple;
    int pos;
    for (int i : this.proxyToUris.keys()) {
      for (int[] tuple : toBeRemoved) {
        if ((tuple[this.subjectPosition] == i)
            || (tuple[this.objectPosition] == i)) {
          newReflexiveTuple = new int[tuple.length];
          pos = 0;
          // is there a front element in tuple?
          if (this.objectPosition != 2) {
            newReflexiveTuple[0] = tuple[0];
            pos = 1;
          }
          // add reflexive EQ relation instance; possible forms: EITHER s p o OR
          // p s o
          newReflexiveTuple[pos++] = i;
          newReflexiveTuple[pos++] = this.uriToEquivalenceRelation.get(i);
          newReflexiveTuple[pos++] = i;
          if (tuple.length - pos >= 0)
            System.arraycopy(tuple, pos, newReflexiveTuple, pos,
                tuple.length - pos);
          addTuple(newReflexiveTuple);
          break;
        }
      }
    }
    return toBeRemoved.size();
  }

  /**
  * return a brand new id, resulting from the very last id, incremeted by 1 (values 0--5
  * are reserved)
  */
  private int getNextId() {
    return ++this.currentId;
  }

  /**
  * obj is either a URI or an XSD literal, encoded as a string;
  * in case obj has _not_ been seen before, the method assigns a new id (an int) to obj,
  * establishes a bidirectional mapping between obj and id, and returns the new id;
  * otherwise, the already existing id for obj is returned
  */
  public Integer putObject(AnyType t) {
    // AnyType t = makeJavaObject(obj);
    if (this.objectToId.containsKey(t)) {
      return this.objectToId.get(t);
    } else {
      int id = getNextId();
      this.objectToId.put(t, id);
      this.idToJavaObject.add(t);
      return id;
    }
  }

  /**
  * returns the the textual representation for id (an int);
  * notice that since tuples are not always grounded (since they are
  * used in the antecedent and consequent of a rule, where variables occur),
  * we return the id prefixed by '?' in case id encodes a variable
  */
  public AnyType getObject(int id) {
    if (RuleStore.isVariable(id))
      return new Variable("?" + Math.abs(id));
    else
      return this.idToJavaObject.get(id);
  }

  /**
  * this synchronized method can be called by functional operators in order
  * to establish an association between a literal (e.g., URI, blank node, XSD
  * atomes, e.g., string, int, etc.) and a Java object (subtypes of AnyType);
  * furthermore, a new int (an ID) is returned that is internally used in the
  * tuple store
  */
  public synchronized int registerJavaObject(AnyType javaObject) {
    Integer id = this.objectToId.get(javaObject);
    if (id == null) {
      id = getNextId();
      this.objectToId.put(javaObject, id); // or should we only make these three
      this.idToJavaObject.add(javaObject); // idToObject, and idToJavaObject
    }
    return id;
  }

  /**
  * given an id (an int), returns true iff id represents a URI;
  * false, otherwise
  */
  public boolean isUri(int id) {
    return (this.idToJavaObject.get(id) instanceof Uri);
  }

  /**
  * given an id (an int), returns true iff id represents a blank node;
  * false, otherwise
  */
  public boolean isBlankNode(int id) {
    return (this.idToJavaObject.get(id) instanceof BlankNode);
  }

  /**
  * given an id (an int), returns true iff id represents a XSD atom;
  * false, otherwise
  */
  public boolean isAtom(int id) {
    return (this.idToJavaObject.get(id) instanceof XsdAnySimpleType);
  }

  /**
  * addTuple() assumes an int[] as input (the tuple);
  * the mappings are established and the index is updated;
  * this method does not check whether the representation is valid;
  * addTuple() also adds the tuple to the set of all tuples;
  * if tuple deletion is enabled, addTuple() also updates the tuple-
  * to-generation mapping;
  * returns true iff the set of all tuple did not already contain the
  * argument tuple
  */
  public final boolean addTuple(int[] tuple) {
    // add the int tuple itself to the list of all tuples
    final boolean isNew = this.allTuples.add(tuple);
    // no need to add tuple to index if its already in the set of all tuples;
    // we also strick to the old/lower generation number if it is not new
    if (isNew) {
      addToIndex(tuple);
      if (tupleDeletionEnabled())
        this.tupleToGeneration.put(tuple, this.generation);
    }
    if (indexStore != null)
      indexStore.update(tuple);
    return isNew;
  }

  /**
  * helper for cleanUpTupleStore;
  * differs from addTuple(int[]) in that it carries a second argument, the
  * generation (an int) that is used as the generation for the tuple argument
  * in case tuple deletion is enabled
  */
  protected final synchronized void addTupleWithGeneration(int[] tuple,
      int gennum) {
    if (this.allTuples.add(tuple)) {
      addToIndex(tuple);
      if (tupleDeletionEnabled())
        this.tupleToGeneration.put(tuple, gennum);
    }
    if (indexStore != null)
      indexStore.update(tuple);
  }

  /**
  * I separated this code from the addTuple method above to make it
  * accessible from other classes
  */
  public void addToIndex(int[] tuple) {
    Map<Integer, Set<int[]>> ithmap;
    Set<int[]> ithset;
    int itharg;
    for (int i = 0; i < tuple.length; i++) {
      ithmap = this.index[i];
      itharg = tuple[i];
      // is there a set for itharg?
      if (ithmap.containsKey(itharg))
        ithset = ithmap.get(itharg);
      else {
        // if not, create an empty one
        ithset = new TCustomHashSet<int[]>(TupleIntStore.DEFAULT_HASHING_STRATEGY);
        ithmap.put(itharg, ithset);
      }
      // and finally add tuple to this set
      ithset.add(tuple);
    }
  }

  /**
  * obtains all those tuples which contain an object obj (represented as an int)
  * at a specific position pos in a tuple (an int);
  * NOTE: the set which is returned is part of the index and thus should NOT be
  * modified!!!
  *
  * @return the empty set if there are no tuples matching the constraints
  */
  public Set<int[]> getTuples(int pos, int obj) {
    final Set<int[]> result = this.index[pos].get(obj);
    if (result == null)
      return Collections.emptySet();
    else
      return result;
  }

  /**
  * instead of using the obj id (an int), one can alternatively specify the object
  * directly;
  * NOTE: check whether you must use the short or long namespace prefix depending on
  * NamespaceManager.shortIsDefault
  */
  public Set<int[]> getTuples(int pos, AnyType literal) {
    // AnyType t = makeJavaObject(literal);
    final Set<int[]> result = this.index[pos].get(this.objectToId.get(literal));
    if (result == null)
      return Collections.emptySet();
    else
      return result;
  }

  /**
  * returns the set of all tuples
  */
  public Set<int[]> getAllTuples() {
    return this.allTuples;
  }

  /**
  * removes the tuple from the set of all tuples and cleans up the index structure;
  * if tuple deletion is enabled, the tuple-generation pair is also removed from the
  * tuple-to-generation mapping;
  * returns true iff the set of all tuples contained the specified tuple;
  * false otherwise (= nothing is removed)
  */
  public final synchronized boolean removeTuple(int[] tuple) {
    final boolean contained = this.allTuples.remove(tuple);
    // if tuple is not present, no clean up needs to be performed
    if (contained) {
      // the tuple is stored in several sets -- recall that the number of sets
      // is
      // equal to the length of the tuple
      for (int i = 0; i < tuple.length; i++)
        // getTuples() at least returns the EMPTY set, so that remove() can be
        // safely applied!
        getTuples(i, tuple[i]).remove(tuple);
      if (tupleDeletionEnabled())
        this.tupleToGeneration.remove(tuple);
    }
    return contained;
  }

  /**
  * differs from removeTuple() in that it returns the generation of the tuple
  * that is going to be deleted;
  * returns -1 in case the tuple is not part of the repository
  */
  protected final synchronized int removeTupleReturnGeneration(int[] tuple) {
    int gennum = -1;
    // if tuple is not present, no clean up needs to be performed
    if (this.allTuples.remove(tuple)) {
      // the tuple is stored in several sets -- recall that the number of sets
      // is
      // equal to the length of the tuple
      for (int i = 0; i < tuple.length; i++)
        getTuples(i, tuple[i]).remove(tuple);
      if (tupleDeletionEnabled()) {
        gennum = this.tupleToGeneration.get(tuple);
        this.tupleToGeneration.remove(tuple);
      }
    }
    return gennum;
  }

  /**
  * a method that extends a tuple, given as a ArrayList<String>, by further
  * arguments: at most <it>one</it> in front and <it>several</it> following
  * the original tuple;
  * use <it>null</it> as a value for <it>front</it> to signal that there is
  * <it>no</it> front element and an <it>empty array</it> that there are no
  * <it>back</it> elements;
  * this method is particularly useful for extending tuples with a notion of
  * valid time, transaction time, or gradation/modal operators
  *
  * @return the new extended tuple
  */
  protected ArrayList<String> extendTupleExternally(final List<String> in,
      final String front, final String... backs) {
    // make sure to set etuple directly to the right size
    ArrayList<String> etuple = new ArrayList<String>(
        in.size() + (front == null ? 0 : 1) + backs.length);
    if (front != null)
      etuple.add(front);
    etuple.addAll(in);
    etuple.addAll(Arrays.asList(backs));
    return etuple;
  }

  /**
  * generates an external string representation from the internal int[]
  * representation of a tuple
  */
  public String toString(int[] tuple, boolean dot) {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < tuple.length; i++)
      sb.append(toUnicode(getObject(tuple[i]).toString()) + " ");
    if (dot)
      sb.append(".");
    return sb.toString();
  }

  public String toString(int[] tuple) {
    return toString(tuple, true);
  }

  public boolean ask(int[] tuple) {
    return this.allTuples.contains(tuple);
  }

  public boolean isInFactBase(AnyType literal) {
    return this.objectToId.containsKey(literal);
  }

  // TODO: NOT SURE WHAT TO DO WITH THIS, MAYBE MOVE TO HFC
  /**
  * returns those ground tuples g = p+s, such that p matches the prefix pattern parameter;
  * a pattern is composed of variables, URIs, and XSD atoms;
  * note that the name of a variable does NOT matter, and thus a pattern such as
  * ?s <foo> ?s <bar>
  * does NOT enforce that the matching tuples must have identical values in first and third
  * position;
  * note that tuples must be at least as long as the pattern to be successfully matching
  * candidates; so, for instance, w.r.t. the above pattern
  * NO match: <bar> <foo>
  * match: <bar> <foo> <baz> <bar>
  * match: <bar> <foo> <baz> <bar> <yep>
  * note further that a pattern should NOT be longer than the longest tuples in the
  * repository;
  * <p>
  * NOTE: we will return the EMPTY SET for patterns longer than the longest tuple
  * <p>
  * NOTE: we will return the NULL VALUE for the EMPTY prefix pattern (even though ALL
  * tuples match this pattern)
  * <p>
  * NOTE: it makes perfect sense that the pattern contains AT LEAST ONE URI or XSD atom;
  * checking for patterns containing only variables can be done more efficiently
  * than by calling this method
  * we will return the NULL VALUE for patterns consisting of variables only
  *
  protected Set<int[]> matchPrefixPattern(String[] prefixPattern) {
  // this check is also needed to avoid positions larger than the size of the array
  if (this.index.length < prefixPattern.length)
   return new THashSet<int[]>();
  Set<int[]> result = null;
  int id;
  for (int i = 0; i < prefixPattern.length; i++) {
   if (!RuleStore.isVariable(prefixPattern[i])) {
    id = putObject(prefixPattern[i]);
    if (result == null)
     result = getTuples(i, id);
    else
     result = Calc.intersection(result, getTuples(i, id));
    if (result.isEmpty())
     return result;
   }
  }
  return result;
  }

  /**
  * checks whether matchPrefixPattern() returns a NON-empty set
  * NOTE: this method will only work for patterns not longer than the size of the index
  * and for non-empty patterns and patterns not consisting of variables only
  *
  public boolean containsPrefixPattern(String[] prefixPattern) {
  return (!matchPrefixPattern(prefixPattern).isEmpty());
  }

  /**
  * queries the index, given a rule pattern, consisting of constants and variables;
  * potentially performs several calls to the index using getTuples(), followed by
  * non-destructive set intersection operations that take into account the "relevant
  * positions" of the input pattern which influences the hashing strategy;
  * NOTE: the result set is NOT shared by the index, thus can be destructively
  * modified without having any effect on the index !!
  *
  * @return a set of tuples satisfying the conditions given by pattern
  */
  public Set<int[]> queryIndex(int[] pattern, Table table) {
    // first check whether pattern is a _trigger_ clause, i.e., containing only
    // atoms
    if ((table.properVariables.size() == 0)
        && (table.dontCareVariables.size() == 0)) {
      // if so, directly check whether ground tuple is contained in set of all
      // tuples
      if (this.allTuples.contains(pattern)) {
        // not sure whether I should add a strategy object with an empty int[]
        // for the
        // positions here
        Set<int[]> set = new THashSet<int[]>();
        set.add(pattern);
        return set;
      } else
        // trigger clause not contained in tuple store: return empty set
        return new THashSet<int[]>();
    }
    // note: pattern with don't cares and constants only gets standard handling;
    // because (properVariables.size() == 0) or (properPositions.length == 0)
    // we know that tuples returned are not involved in matching, since there
    // are no proper variables!!
    // then check whether pattern is made only of variables only
    boolean onlyVars = true;
    int length = pattern.length;
    Set<int[]> query;
    Set<int[]> result = null;
    for (int i = 0; i < length; i++) {
      if (!RuleStore.isVariable(pattern[i])) {
        onlyVars = false;
        // note: querying the index with a constant from a rule that is not
        // contained
        // in the initial fact set, will results in the null value;
        // this is because I do NOT want to exclude rules that refer to
        // constants that
        // are potentially introduced later in a tuple after an upload!
        query = getTuples(i, pattern[i]);
        if (query.isEmpty())
          return query;
        if (result == null)
          result = query;
        else
          // all positions are considered during intersection
          result = Calc.intersection(result, query);
        // result might be empty -- no need to perform further intersections
        if (result.isEmpty())
          return result;
      }
    }
    if (onlyVars)
      result = this.allTuples;
    // result now is always assigned a non-null value;
    // ensure proper length and consider don't care variables here which usually
    // lead to "redundant" tuples w.r.t. the "relevant positions"!
    // note: even though atoms do not show up in the "relevant positions", they
    // do _not_ blow up the set of "right" tuples w.r.t. the input pattern
    query = result; // reuse query
    // use relevant position (and NOT proper), since in-eqs might be applied
    result = new TCustomHashSet<int[]>(
        new TIntArrayHashingStrategy(table.getRelevantPositions()));
    for (int[] tuple : query) {
      if (tuple.length == length)
        result.add(tuple);
    }
    // note: result now no longer refers to sets from the index, thus
    // manipulating
    // result destructively has NO effect on the index or the set of all tuples;
    // querying the index does NOT guarantee that duplicate variables hold the
    // same value after local querying: <?x, a, ?x>;
    // this needs to be corrected here by throwing away tuples from the result
    // set, using getEqualPositions();
    if (table.getEqualPositions().length != 0)
      result = ensureEqualVariables(result, table.getEqualPositions());
    // finished !!
    return result;
  }

  /**
  * destructively reduces the input set by making sure that columns headed
  * by the same variable hold the same value;
  * example (duplPos vectors are _sorted_):
  * rule pattern: <?x, ?y, ?x, ?y, ?x>
  * duplPos: [[0, 2, 4], [1, 3]]
  * input: <a, b, a, b, a>  OK!
  */
  protected Set<int[]> ensureEqualVariables(Set<int[]> input, int[][] duplPos) {
    // need _iterator_ here, since tuples are removed from input set
    Iterator<int[]> it = input.iterator();
    int[] tuple, dupl;
    while (it.hasNext()) {
      tuple = it.next();
      outerloop: for (int i = 0; i < duplPos.length; i++) {
        dupl = duplPos[i];
        // note: dupl contains at least two elements
        for (int j = 1; j < dupl.length; j++) {
          if (tuple[dupl[j - 1]] != tuple[dupl[j]]) {
            it.remove();
            // break to outer for loop
            break outerloop;
          }
        }
      }
    }
    return input;
  }

  /**
  * returns a nearly-deep copy of the index: everything is copied with the notable
  * exception of the tuples (the int arrays) at the leaves of the index (as their
  * content does not change!
  */
  @SuppressWarnings("unchecked")
  private Map<Integer, Set<int[]>>[] copyIndex() {
    // the inital creation is taken from the binary constructor
    Map<Integer, Set<int[]>>[] copy = (HashMap[]) Array
        .newInstance(HashMap.class, this.maxNoOfArgs);
    for (int i = 0; i < this.maxNoOfArgs; i++) {
      // shallow copy this.index[i], but after that also shallow copy the values
      // of the map
      copy[i] = new HashMap<>(this.index[i]);
      for (Integer key : copy[i].keySet()) {
        copy[i].put(key, new TCustomHashSet<int[]>(
            TupleIntStore.DEFAULT_HASHING_STRATEGY, copy[i].get(key)));
      }
    }
    return copy;
  }

  public Operator checkAndRegisterOperator(String className) {
    return operatorRegistry.checkAndRegister(className);
  }

  public int evaluateOperator(String name, String operatorPath, int[] args) {
    return operatorRegistry.evaluate(name, operatorPath, args);
  }

  public BindingTable[] evaluateRelOperator(String name, String operatorPath,
      BindingTable[] args) {
    return operatorRegistry.evaluate(name, operatorPath, args);
  }
}
