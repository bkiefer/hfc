package de.dfki.lt.hfc;

import java.io.*;
import java.lang.reflect.Array;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.dfki.lt.hfc.types.*;
import gnu.trove.list.array.TIntArrayList;
import gnu.trove.map.hash.*;
import gnu.trove.set.hash.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
 * @see gnu.trove.THashSet
 * @see de.dfki.lt.hfc.TIntArrayHashingStrategy
 * @see de.dfki.lt.hfc.RuleStore
 * @see de.dfki.lt.hfc.ForwardChainer
 * @since JDK 1.5
 */
public final class TupleStore {

    /**
     * this field serves a different purpose compared to field ForwardChainer.generationCounter
     * and is solely used when tuple deletion is enabled in the forward chainer;
     * this field is incremented by 1 before closure computation is called and is incremented
     * again after closure computation;
     * this strategy makes sure that the entailed tuples are assigned an ODD generation number,
     * whereas uploaded tuples are automatically assigned an EVEN number
     *
     * @see ForwardChainer.enableTupleDeletion()
     */
    protected int generation = 0;

    /**
     * a special field that is initialized by an empty map from tuples (int[]) to generations
     * (Integer) in case tuple deletion is switched on through method enableTupleDeletion() in
     * the forward chainer
     *
     * @see ForwardChainer.enableTupleDeletion()
     */
    protected TCustomHashMap<int[], Integer> tupleToGeneration = null;

    /**
     * @return false otherwise
     * <p>
     * there is a similar method in class ForwardChainer
     * @see ForwardChainer.tupleDeletionEnabled()
     */
    private boolean tupleDeletionEnabled() {
        return (this.tupleToGeneration != null);
    }

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

    private TupleParser parser;

    /**
     * this setting is used for input encoding in TupleStore
     *
     * @see TupleStore.outputCharacterEncoding
     * @see Interactive.outputCharacterEncoding
     */
    public String inputCharacterEncoding = "UTF-8";

    /**
     * this setting is used for output encoding in TupleStore
     *
     * @see TupleStore.inputCharacterEncoding
     * @see Interactive.outputCharacterEncoding
     */
    public String outputCharacterEncoding = "UTF-8";

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
     * it seems reasonable to have tuples of at least length 1;
     * use value 3 to be compliant with RDF;
     * a similar variable exists in class RuleStore
     *
     * @see #maxNoOfARgs
     */
    public int minNoOfArgs = 3;

    /**
     * this constant is used to create the right number of index tables;
     * note that the value of this constant has an effect on the index data
     * structure; the larger the number, the more tables are created;
     * reasonable values seem to be 3 (RDF triple), 4 (time or negation, 5 (time),
     * or 6 (time and negation);
     * a similar variable exists in class RuleStore
     *
     * @see #minNoOfARgs
     */
    public int maxNoOfArgs = 5;

    /**
     * a constant that controls whether a warning is printed in case an invalid
     * tuple is read in;
     * a similar variable exists in class RuleStore
     *
     * @see #exitOnError
     */
    public boolean verbose = false;

    /**
     * when tuples are read in, this variable decides whether tuples are compliant with
     * what RDF requests, viz., that the first argument is either an URI or a blank node,
     * and that the second arg is a URI
     */
    public boolean rdfCheck = true;

    /**
     * a constant that controls whether the system is terminated in case an invalid
     * tuple is read in (exit code = 1);
     * a similar variable exists in class RuleStore
     *
     * @see #verbose
     */
    public boolean exitOnError = false;

    /**
     * URIs and XSD literals are replaced when they are read in by their IDs,
     * usually ints (perhaps use longs if there are too many atoms), starting
     * with 0
     * the value 0 is used during rule application to indicate that a variable
     * is unbound or its value is not of interest;
     * negative integers represent (local) variable names
     *
     * @see TupleStore.INTERNAL_UNBOUND
     */
    private int currentId = 0;

    /**
     * When reading multiple files, blank nodes in different files might have the
     * same name. To make it less likely there is a clash of blank node names, we
     * use this id generator to append a unique id for each round of reading.
     * TODO: this is not 100% safe.
     */
    protected int blankNodeSuffixNo = 0;
    protected String blankNodeSuffix = null;

    /**
     * a namespace object used to expand short form namespaces into full forms
     */
    public Namespace namespace;

    public final IndexStore indexStore;

    /**
     * used during input, when URIs, blank nodes, or XSD atoms are replaced by their IDs (ints)
     */
    public HashMap<String, Integer> objectToId;

    /**
     * used during output, when IDs (ints) are replaced by URI or XSD names
     */
    public ArrayList<String> idToObject;

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
     * the default hashing (and equals) strategy for tuples from the tuple store
     * (all positions of a tuple are taken into account);
     * make it static, so that it can be reused for the sets at the leaves of the
     * index!
     */
    protected static TIntArrayHashingStrategy DEFAULT_HASHING_STRATEGY = new TIntArrayHashingStrategy();

    /**
     * this registry object gathers the functional operators potentially used
     * during querying (FILTER) and forward chaining (LHS matching via predicates
     * and RHS generation of new individuals via functions), and so during the
     * interactive mode;
     * since all these modes need a tuple store and since the registry requires
     * the tuple store as an internal state, we automatically constructs a new
     * registry every time a new tuple store is build
     */
    protected OperatorRegistry operatorRegistry = new OperatorRegistry(this);

    /**
     * this registry object gathers the aggregational operators potentially used
     * during querying (AGGREGATE) and forward chaining (all-rules), and so during
     * the interactive mode;
     * since all these modes need a tuple store and since the registry requires
     * the tuple store as an internal state, we automatically constructs a new
     * registry every time a new tuple store is build
     */
    protected AggregateRegistry aggregateRegistry = new AggregateRegistry(this);


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

    private static final Logger logger = LoggerFactory.getLogger(TupleStore.class);

    /**
     * init form that "outsources" initialization code that needs to be duplicated by the
     * binary (that is used be several other constructors) and 10-ary constructor
     */
    private void init(boolean verbose,
                      boolean rdfCheck,
                      boolean eqReduction,
                      int minNoOfArgs,
                      int maxNoOfArgs,
                      int subjectPosition,
                      int predicatePosition,
                      int objectPosition,
                      int noOfAtoms,
                      int noOfTuples) {
        this.noOfAtoms = noOfAtoms;
        this.noOfTuples = noOfTuples;
        this.verbose = verbose;
        this.rdfCheck = rdfCheck;
        this.equivalenceClassReduction = eqReduction;
        this.minNoOfArgs = minNoOfArgs;
        this.maxNoOfArgs = maxNoOfArgs;
        this.subjectPosition = subjectPosition;
        this.predicatePosition = predicatePosition;
        this.objectPosition = objectPosition;
        this.objectToId = new HashMap<String, Integer>(noOfAtoms);
        this.idToObject = new ArrayList<String>(noOfAtoms);
        this.idToJavaObject = new ArrayList<AnyType>(noOfAtoms);
        this.uriToProxy = new TIntIntHashMap();
        this.proxyToUris = new TIntObjectHashMap<TIntArrayList>();
        this.uriToEquivalenceRelation = new TIntIntHashMap();
        // specify mappings here that stay constant for efficiency reasons (e.g., used by operators)
        initializeUriMappings();
        // unfortunately, no generic array creation in Java -- use java.lang.reflect
        // capability instead;  for each array position, generate an empty
        // HashMap<Integer, Set<Tuple>> object
        this.index = (HashMap[]) Array.newInstance(HashMap.class, this.maxNoOfArgs);
        for (int i = 0; i < this.maxNoOfArgs; i++)
            this.index[i] = new HashMap<Integer, Set<int[]>>();
        this.allTuples = new TCustomHashSet<int[]>(TupleStore.DEFAULT_HASHING_STRATEGY, noOfTuples);
        if (this.indexStore != null) {
            indexStore.setTuplestore(this);
        }
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
        this.objectToId.put(Namespace.UNBOUND, Namespace.UNBOUND_ID);
        this.idToObject.add(Namespace.UNBOUND);
        this.idToJavaObject.add(null);
        // maybe make this more flexible by moving this information to a file
        if (this.namespace.shortIsDefault) {
            this.objectToId.put(Namespace.RDFS_SUBCLASSOF_SHORT, Namespace.RDFS_SUBCLASSOF_ID);
            this.idToObject.add(Namespace.RDFS_SUBCLASSOF_SHORT);
            this.idToJavaObject.add(null);
            this.objectToId.put(Namespace.OWL_SAMEAS_SHORT, Namespace.OWL_SAMEAS_ID);
            this.idToObject.add(Namespace.OWL_SAMEAS_SHORT);
            this.idToJavaObject.add(null);
            this.objectToId.put(Namespace.OWL_EQUIVALENTCLASS_SHORT, Namespace.OWL_EQUIVALENTCLASS_ID);
            this.idToObject.add(Namespace.OWL_EQUIVALENTCLASS_SHORT);
            this.idToJavaObject.add(null);
            this.objectToId.put(Namespace.OWL_EQUIVALENTPROPERTY_SHORT, Namespace.OWL_EQUIVALENTPROPERTY_ID);
            this.idToObject.add(Namespace.OWL_EQUIVALENTPROPERTY_SHORT);
            this.idToJavaObject.add(null);
            this.objectToId.put(Namespace.OWL_DISJOINTWITH_SHORT, Namespace.OWL_DISJOINTWITH_ID);
            this.idToObject.add(Namespace.OWL_DISJOINTWITH_SHORT);
            this.idToJavaObject.add(null);
        } else {
            this.objectToId.put(Namespace.RDFS_SUBCLASSOF_LONG, Namespace.RDFS_SUBCLASSOF_ID);
            this.idToObject.add(Namespace.RDFS_SUBCLASSOF_LONG);
            this.idToJavaObject.add(null);
            this.objectToId.put(Namespace.OWL_SAMEAS_LONG, Namespace.OWL_SAMEAS_ID);
            this.idToObject.add(Namespace.OWL_SAMEAS_LONG);
            this.idToJavaObject.add(null);
            this.objectToId.put(Namespace.OWL_EQUIVALENTCLASS_LONG, Namespace.OWL_EQUIVALENTCLASS_ID);
            this.idToObject.add(Namespace.OWL_EQUIVALENTCLASS_LONG);
            this.idToJavaObject.add(null);
            this.objectToId.put(Namespace.OWL_EQUIVALENTPROPERTY_LONG, Namespace.OWL_EQUIVALENTPROPERTY_ID);
            this.idToObject.add(Namespace.OWL_EQUIVALENTPROPERTY_LONG);
            this.idToJavaObject.add(null);
            this.objectToId.put(Namespace.OWL_DISJOINTWITH_LONG, Namespace.OWL_DISJOINTWITH_ID);
            this.idToObject.add(Namespace.OWL_DISJOINTWITH_LONG);
            this.idToJavaObject.add(null);
        }
        // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        // VERY IMPORTANT: UPDATE currentId !!!!!!!
        // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        this.currentId = 5;
    }

    /**
     * (should) only (be) used by copyTupleStore()
     */
    private TupleStore() {
        this.indexStore = null;
    }

    /**
     * choose a proper noOfAtoms/noOfTuples in order not to arrive at copying (at all or
     * too early) the elements into a larger structure; keep in mind that other services
     * (e.g., rule application) can drastically increase the number of tuples;
     * note: assigns an empty Namespace object to this.namespace
     */
    public TupleStore(int noOfAtoms, int noOfTuples) {
        this.namespace = new Namespace();
        this.indexStore = null;
        init(this.verbose, this.rdfCheck, this.equivalenceClassReduction,
                this.minNoOfArgs, this.maxNoOfArgs,
                this.subjectPosition, this.predicatePosition, this.objectPosition,
                noOfAtoms, noOfTuples);
    }

    /**
     * extends the binary constructor with the ability to read in a namespace
     */
    public TupleStore(int noOfAtoms, int noOfTuples, Namespace namespace) {
        this.namespace = namespace;
        this.indexStore = null;
        init(this.verbose, this.rdfCheck, this.equivalenceClassReduction,
                this.minNoOfArgs, this.maxNoOfArgs,
                this.subjectPosition, this.predicatePosition, this.objectPosition,
                noOfAtoms, noOfTuples);
    }

    /**
     * extends the binary constructor with the ability to read in a namespace and a
     * textual representation of facts (basically N-Triples syntax), stored in a file
     *
     * @throws IOException
     * @throws FileNotFoundException
     * @throws WrongFormatException
     * @see #readTuples
     */
    public TupleStore(int noOfAtoms, int noOfTuples, Namespace namespace, String tupleFile)
            throws FileNotFoundException, IOException, WrongFormatException {
        this.namespace = namespace;
        this.indexStore = null;
        init(this.verbose, this.rdfCheck, this.equivalenceClassReduction,
                this.minNoOfArgs, this.maxNoOfArgs,
                this.subjectPosition, this.predicatePosition, this.objectPosition,
                noOfAtoms, noOfTuples);
        readTuples(tupleFile);
    }

    /**
     * extends the binary constructor with the ability to read in a namespace, a index store, as well as a
     * textual representation of facts (basically N-Triples syntax), as well as stored in a file
     *
     * @throws IOException
     * @throws WrongFormatException
     * @see #readTuples
     */
    public TupleStore(int noOfAtoms, int noOfTuples, Namespace namespace, String tupleFile, IndexStore indexStore)
            throws IOException, WrongFormatException {
        this.namespace = namespace;
        this.indexStore = indexStore;
        init(this.verbose, this.rdfCheck, this.equivalenceClassReduction,
                this.minNoOfArgs, this.maxNoOfArgs,
                this.subjectPosition, this.predicatePosition, this.objectPosition,
                noOfAtoms, noOfTuples);
        readTuples(tupleFile);
    }

    /**
     * more options to fully parameterize the tuple store
     *
     * @throws IOException
     * @throws FileNotFoundException
     * @throws WrongFormatException
     */
    public TupleStore(boolean verbose, boolean rdfCheck, boolean eqReduction,
                      int minNoOfArgs, int maxNoOfArgs,
                      int noOfAtoms, int noOfTuples,
                      Namespace namespace, String tupleFile)
            throws FileNotFoundException, IOException, WrongFormatException {
        this.namespace = namespace;
        this.indexStore = null;
        init(verbose, rdfCheck, eqReduction, minNoOfArgs, maxNoOfArgs,
                this.subjectPosition, this.predicatePosition, this.objectPosition,
                noOfAtoms, noOfTuples);
        readTuples(tupleFile);
    }

    /**
     * more options to fully parameterize the tuple store
     *
     * @throws IOException
     * @throws FileNotFoundException
     * @throws WrongFormatException
     */
    public TupleStore(boolean verbose, boolean rdfCheck, boolean eqReduction,
                      int minNoOfArgs, int maxNoOfArgs,
                      int noOfAtoms, int noOfTuples,
                      Namespace namespace, String tupleFile,
                      IndexStore indexStore)
            throws FileNotFoundException, IOException, WrongFormatException {
        this.namespace = namespace;
        this.indexStore = indexStore;
        init(verbose, rdfCheck, eqReduction, minNoOfArgs, maxNoOfArgs,
                this.subjectPosition, this.predicatePosition, this.objectPosition,
                noOfAtoms, noOfTuples);
        readTuples(tupleFile);
    }

    /**
     * more options to fully parameterize the tuple store
     *
     * @throws IOException
     * @throws FileNotFoundException
     * @throws WrongFormatException
     */
    public TupleStore(boolean verbose, boolean rdfCheck, boolean eqReduction,
                      int minNoOfArgs, int maxNoOfArgs,
                      int subjectPosition, int predicatePosition, int objectPosition,
                      int noOfAtoms, int noOfTuples,
                      Namespace namespace, String tupleFile)
            throws FileNotFoundException, IOException, WrongFormatException {
        this.namespace = namespace;
        this.indexStore = null;
        init(verbose, rdfCheck, eqReduction, minNoOfArgs, maxNoOfArgs,
                subjectPosition, predicatePosition, objectPosition,
                noOfAtoms, noOfTuples);
        readTuples(tupleFile);
    }


    /**
     * assumes a default of 100,000 atoms and 500,000 tuples
     */
    public TupleStore(Namespace namespace) {
        this.namespace = namespace;
        this.indexStore = null;
        init(this.verbose, this.rdfCheck, this.equivalenceClassReduction,
                this.minNoOfArgs, this.maxNoOfArgs,
                this.subjectPosition, this.predicatePosition, this.objectPosition,
                100000, 500000);
    }

    /**
     * assumes a default of 100,000 atoms and 500,000 tuples
     *
     * @throws IOException
     * @throws FileNotFoundException
     * @throws WrongFormatException
     */
    public TupleStore(Namespace namespace, String tupleFile)
            throws FileNotFoundException, IOException, WrongFormatException {
        this(namespace);
        readTuples(tupleFile);
    }

    /**
     * a simple STATIC method, translating an int array into something readable (N-tuple syntax)
     */
    public static void printTuple(int[] tuple, ArrayList<String> mapping) {
        logger.info(toString(tuple,mapping));
        //System.out.println(toString(tuple, mapping));
    }

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
                this.proxyToUris.put(left, new TIntArrayList(new int[]{left, right}));
            }
        }
    }

    /**
     * given a tuple, isEquivalenceRelation() determines whether the predicate
     * in position this.predicatePosition is an equivalence relation;
     * currently, owl:sameAs, owl:equivalentClass, and owl:equivalentProperty
     * are recognized here as given by the integers
     * Namespace.OWL_SAMEAS_ID
     * Namespace.OWL_EQUIVALENTCLASS_ID
     * Namespace.OWL_EQUIVALENTPROPERTY_ID
     */
    protected boolean isEquivalenceRelation(int[] tuple) {
        // perhaps use an int set plus an element test here if more relations are involved
        final int pred = tuple[this.predicatePosition];
        return ((pred == Namespace.OWL_SAMEAS_ID) ||
                (pred == Namespace.OWL_EQUIVALENTCLASS_ID) ||
                (pred == Namespace.OWL_EQUIVALENTPROPERTY_ID));
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
     * only keep exactly one triple for each proxy p with its corresponding equivalence relation r:
     * p r p
     *
     * @return the number of tuples being removed
     * @see isEquivalenceRelation()
     * @see RuleStore.generateTest()
     * @see RuleStore.generateAction()
     * @see removeTuple()
     */
    public int cleanUpTupleStore() {
        // do NOT remove tuples from set/index while iteration is going on
        final Set<int[]> toBeRemoved = new THashSet<int[]>();
        // find equivalence class statements: either move over the set of tuples:
        //   for (int[] tuple : this.allTuples)
        //     if (isEquivalenceRelation(tuple))
        //	     toBeRemoved.add(tuple);
        // OR better: use the index (less triples to iterate over)
        toBeRemoved.addAll(getTuples(this.predicatePosition, Namespace.OWL_SAMEAS_ID));
        toBeRemoved.addAll(getTuples(this.predicatePosition, Namespace.OWL_EQUIVALENTCLASS_ID));
        toBeRemoved.addAll(getTuples(this.predicatePosition, Namespace.OWL_EQUIVALENTPROPERTY_ID));
        // remove ER tuples from store (set & index) and update uriToProxy and proxyToUris,
        // but also record the equivalence relation in which subject and object are related to
        for (int[] tuple : toBeRemoved) {
            removeTuple(tuple);
            addEquivalentElements(tuple[this.subjectPosition], tuple[this.objectPosition]);
            // do we need both entries here since subject position is used as THE proxy
            this.uriToEquivalenceRelation.put(tuple[this.subjectPosition], tuple[this.predicatePosition]);
            this.uriToEquivalenceRelation.put(tuple[this.objectPosition], tuple[this.predicatePosition]);
        }
        // iterate over remaining tuples and replace uris by their proxies, if necessary;
        // do NOT modify tuples in set & index, but instead delete old and add new cleaned-up tuples;
        // note: the local rule fields are NOT updated
        final Set<int[]> toBeUpdated = new THashSet<int[]>();
        //  for (int[] tuple : this.allTuples)
        //    for (int i = 0; i < tuple.length; i++)
        //      if (this.uriToProxy.containsKey(tuple[i])) {
        //        toBeUpdated.add(tuple);
        //        break;
        //      }
        // again, use the index, instead of iterating over all tuples
        for (int i : this.uriToProxy.keys()) {
            for (int j = 0; j < this.maxNoOfArgs; j++)
                toBeUpdated.addAll(getTuples(j, i));
        }
        int[] newTuple;
        for (int[] tuple : toBeUpdated) {
            int gennum = removeTupleReturnGeneration(tuple);  // = -1 if tuple deletion is disabled
            // generate brand-new int tuple, since old array might still be shared at several places,
            // e.g., in a local rule field or in a former copy of the forward chainer (@see LTWorld)
            newTuple = new int[tuple.length];
            for (int i = 0; i < tuple.length; i++) {
                if (this.uriToProxy.containsKey(tuple[i]))
                    newTuple[i] = this.uriToProxy.get(tuple[i]);
                else
                    newTuple[i] = tuple[i];
            }
            addTupleWithGeneration(newTuple, gennum);
        }
        // and finally add reflexive ER proxy pattern (see above), but take care of potential
        // front and back elements that also need to be taken over
        int[] newReflexiveTuple;
        int pos;
        for (int i : this.proxyToUris.keys()) {
            for (int[] tuple : toBeRemoved) {
                if ((tuple[this.subjectPosition] == i) || (tuple[this.objectPosition] == i)) {
                    newReflexiveTuple = new int[tuple.length];
                    pos = 0;
                    // is there a front element in tuple?
                    if (this.objectPosition != 2) {
                        newReflexiveTuple[0] = tuple[0];
                        pos = 1;
                    }
                    // add reflexive EQ relation instance; possible forms: EITHER s p o OR p s o
                    newReflexiveTuple[pos++] = i;
                    newReflexiveTuple[pos++] = this.uriToEquivalenceRelation.get(i);
                    newReflexiveTuple[pos++] = i;
                    for (int j = pos; j < tuple.length; j++) {
                        newReflexiveTuple[j] = tuple[j];
                    }
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
    public int putObject(String obj) {
        if (this.objectToId.containsKey(obj)) {
            return this.objectToId.get(obj);
        } else {
            int id = getNextId();
            this.objectToId.put(obj, id);
            this.idToObject.add(obj);
            // lazy strategy: construct Java object iff it is accessed by functional operator
            this.idToJavaObject.add(null);
            return id;
        }
    }

    /**
     * returns the the textual representation for id (an int);
     * notice that since tuples are not always grounded (since they are
     * used in the antecedent and consequent of a rule, where variables occur),
     * we return the id prefixed by '?' in case id encodes a variable
     */
    public String getObject(int id) {
        if (RuleStore.isVariable(id))
            return ("?" + id);
        else
            return this.idToObject.get(id);
    }

    /**
     * returns a Java object for a given literal, internally represented by parameter id;
     * Java classes have been defined for the following types of literals:
     * URI            -> de.dfki.lt.hfc.types.Uri
     * blank node     -> de.dfki.lt.hfc.types.BlankNode
     * xsd:int        -> de.dfki.lt.hfc.types.XsdInt
     * xsd:long       -> de.dfki.lt.hfc.types.XsdLong
     * xsd:float      -> de.dfki.lt.hfc.types.XsdFloat
     * xsd:double     -> de.dfki.lt.hfc.types.XsdDouble
     * xsd:string     -> de.dfki.lt.hfc.types.XsdString
     * xsd:boolean    -> de.dfki.lt.hfc.types.XsdBoolean
     * xsd:dateTime   -> de.dfki.lt.hfc.types.XsdDateTime
     * xsd:date       -> de.dfki.lt.hfc.types.XsdDate
     * xsd:gYear      -> de.dfki.lt.hfc.types.XsdGYear
     * xsd:gYearMonth -> de.dfki.lt.hfc.types.XsdGYearMonth
     * xsd:gMonth     -> de.dfki.lt.hfc.types.XsdGMonth
     * xsd:gMonthDay  -> de.dfki.lt.hfc.types.XsdGMonthDay
     * xsd:gDay       -> de.dfki.lt.hfc.types.XsdGDay
     * xsd:duration   -> de.dfki.lt.hfc.types.XsdDuration
     * xsd:uDateTime  -> de.dfki.lt.hfc.types.XsdUDateTime
     * xsd:monetary   -> de.dfki.lt.hfc.types.XsdMonetary
     * xsd:anyURI     -> de.dfki.lt.hfc.types.XsdAnyURI
     * <p>
     * this is a lazy method, calling makeJavaObject() in case no Java object has been
     * created so far for id
     */
    public AnyType getJavaObject(int id) {
        AnyType obj = this.idToJavaObject.get(id);
        // note that there is at least a mapping from id to the null value
        if (obj == null)
            return makeJavaObject(id);
        else
            return obj;
    }

    /**
     * for each new URI, blank node, or XSD atom, we construct a new Java
     * object on _demand_ when calling makeJavaObject();
     * the idea is that external functional operators will proceed faster
     * when directly working on the Java objects instead on working on the
     * external string representation which needs to be parsed/manipulated
     * <p>
     * NOTE: the predefined (XSD) types need to be specified in a namespace
     * file and need to be headed by the '&type2class' directive;
     * defining a new (XSD) type is done by specifying a short-name
     * URI followed by a Java class which acts as a representative
     * for this type in HFC; e.g.,
     * xsd:duration  XsdDuration
     * NOTE: in case we we have found an unknown literal type, _null_ is
     * returned and a message is printed out to the console
     *
     * @see getJavaObject()
     */
    private AnyType makeJavaObject(int id) {
        // note: I assume that there IS a mapping between id and literal
        final String literal = this.idToObject.get(id);
        if (TupleStore.isUri(literal)) {
            this.idToJavaObject.set(id, new Uri(literal));
        } else if (TupleStore.isBlankNode(literal)) {
            this.idToJavaObject.set(id, new BlankNode(literal));
        } else {
            int idx = literal.lastIndexOf('^');
            if (idx == -1) {
                // note: parseAtom() completes a bare string by adding "^^<xsd:string>",
                //       but if the string has a language tag, nothing is appended, thus
                //       '^' is missing (as is required by the specification)
                this.idToJavaObject.set(id, new XsdString(literal));
            }
            // now do the `clever' dispatch through mapping the type names to Java
            // class constructors:  @see de.dfki.lt.hfc.Namespace.readNamespaces()
            else {
                try {
                    this.idToJavaObject.set(id, XsdAnySimpleType.getXsdObject(literal));
                } catch (WrongFormatException e) {
                    sayItLoud(e.getMessage());
                    return null;
                }
            }
        }
        return this.idToJavaObject.get(id);
    }

    /**
     * this synchronized method can be called by functional operators in order
     * to establish an association between a literal (e.g., URI, blank node, XSD
     * atomes, e.g., string, int, etc.) and a Java object (subtypes of AnyType);
     * furthermore, a new int (an ID) is returned that is internally used in the
     * tuple store
     */
    public synchronized int registerJavaObject(String literal, AnyType javaObject) {
        Integer id = this.objectToId.get(literal);
        if (id == null) {
            id = getNextId();
            this.objectToId.put(literal, id);     // or should we only make these three
            this.idToObject.add(literal);         // lines synchronized, i.e., objectToId
            this.idToJavaObject.add(javaObject);  // idToObject, and idToJavaObject
        }
        return id;
    }

    /**
     * at several places, messages were output depending on this.exitOnError
     * and this.verbose -- unify this in this special private method;
     * perhaps will be replaced by Apache's log4j
     *
     * @throws WrongFormatException
     */
    private boolean sayItLoud(int lineNo, String message) throws WrongFormatException {
        if (this.exitOnError) {
            logger.error("  " + lineNo + message);
            // throw new RuntimeException("FATAL ERROR");
            throw new WrongFormatException("  " + lineNo + message);
        }
        if (this.verbose)
            logger.info("  " + lineNo + message);
        return false;
    }

    /**
     * same method without the line numbering
     */
    private boolean sayItLoud(String message) {
        if (this.exitOnError) {
            logger.error("  " + message);
            throw new RuntimeException("FATAL ERROR");
        }
        if (this.verbose)
            logger.info("  " + message);
        return false;
    }

    /**
     * checks whether a tuple of proper length as specified by this.minNoOfArgs
     * and this.maxNoOfArgs;
     * <p>
     * also checks whether the arguments of a tuple are of the right "kind":
     * arg 1: URI or blank node
     * arg 2: URI
     * arg 3, 4, ... : URI, blank node or XSD atom
     * thus only make sure that first and second arg are OK;
     * <p>
     * finally check whether in case of a XSD atom, the atom is compatible
     * with the specified XSD type
     * ************************ NOT IMPLEMENTED YET ************************
     * <p>
     * depending on this.verbose and this.exitOnError, the method is
     * silent, outputs a warning, or exit the process
     *
     * @throws WrongFormatException
     * @see RuleStore#isValidTuple
     */
    public boolean isValidTuple(List<String> stringTuple, int lineNo)
            throws WrongFormatException {
        // check against min length
        if (stringTuple.size() < this.minNoOfArgs)
            return sayItLoud(lineNo, ": tuple too short: " + stringTuple);
        // check against max length
        if (stringTuple.size() > this.maxNoOfArgs)
            return sayItLoud(lineNo, ": tuple too long");
        // is tuple RDF compliant
        if (rdfCheck) {
            // check for valid first arg
            if ((stringTuple.size() > 0) && (TupleStore.isAtom(stringTuple.get(this.subjectPosition))))
                return sayItLoud(lineNo, ": first arg is an atom");
            // check for valid second arg
            if ((stringTuple.size() > 1) && (!TupleStore.isUri(stringTuple.get(this.predicatePosition))))
                return sayItLoud(lineNo, ": second arg is not an URI");
        }
        return true;
    }

    /**
     * given a string representation of a literal (an argument of a tuple),
     * isUri() returns true iff literal is a URI; false, otherwise
     */
    public static boolean isUri(String literal) {
        return literal.startsWith("<");
    }

    /**
     * given an id (an int), returns true iff id represents a URI;
     * false, otherwise
     */
    public boolean isUri(int id) {
        return (this.idToJavaObject.get(id) instanceof Uri);
    }

    /**
     * given a string representation of a literal (an argument of a tuple),
     * isBlankNode() returns true iff literal is a blank node; false, otherwise
     */
    public static boolean isBlankNode(String literal) {
        return literal.startsWith("_");
    }

    /**
     * given an id (an int), returns true iff id represents a blank node;
     * false, otherwise
     */
    public boolean isBlankNode(int id) {
        return (this.idToJavaObject.get(id) instanceof BlankNode);
    }

    /**
     * given a string representation of a literal (an argument of a tuple),
     * isAtom() returns true iff literal is an XSD atom; false, otherwise
     */
    public static boolean isAtom(String literal) {
        return literal.startsWith("\"");
    }

    /**
     * given an id (an int), returns true iff id represents a XSD atom;
     * false, otherwise
     */
    public boolean isAtom(int id) {
        return (this.idToJavaObject.get(id) instanceof XsdAnySimpleType);
    }

    /**
     * tests whether literal is a constant (i.e., URI, blank node, or XSD atom) as known
     * by the tuple store; i.e., literal must be part of a tuple that has been added to
     * the tuple store;
     * NOTE: false does NOT indicate that literal is a variable!
     */
    public boolean isConstant(String literal) {
        return this.objectToId.containsKey(literal);
    }

    /**
     * internal representation: constants are positive numbers
     */
    public static boolean isConstant(int id) {
        return (id > 0);
    }

    /**
     * internalizeTuple() maps array lists of strings to int arrays of unique
     * ints;
     * uses putObject() to generate new ints in case the string argument is
     * brand new, or retrieves the already generated int in case the string
     * argument has already been seen
     * <p>
     * TODO: LONG TO SHORT MAPPINGS MUST HAVE BEEN DONE BEFOREHAND
     */
    public int[] internalizeTuple(List<String> stringTuple) {
        int[] intTuple = new int[stringTuple.size()];
        for (int i = 0; i < stringTuple.size(); i++)
            intTuple[i] = putObject(stringTuple.get(i));
        return intTuple;
    }

    /**
     * internalizeTuple() maps string arrays to int arrays of unique ints;
     * uses putObject() to generate new ints in case the string argument is
     * brand new, or retrieves the already generated int in case the string
     * argument has already been seen
     * <p>
     * TODO: LONG TO SHORT MAPPINGS MUST HAVE BEEN DONE BEFOREHAND
     */
    public int[] internalizeTuple(String... stringTuple) {
        int[] intTuple = new int[stringTuple.length];
        for (int i = 0; i < stringTuple.length; i++)
            intTuple[i] = putObject(stringTuple[i]);
        return intTuple;
    }

    /**
     * addTuple() assumes a textual tuple representation after tokenization
     * (an array list of strings);
     * the bidirectional mapping is established and the index is updated;
     * this method is used when an external tuple file is read in;
     * lineNo refers to the line number in the file that is read in
     * <p>
     * TODO: LONG TO SHORT MAPPINGS MUST HAVE BEEN DONE BEFOREHAND
     *
     * @return the int[] representation of parameter stringTuple, otherwise
     * @throws WrongFormatException
     */
    protected int[] addTuple(List<String> stringTuple, int lineNo)
            throws WrongFormatException {
        // check whether external representation is valid for a ground tuple
        if (!isValidTuple(stringTuple, lineNo))
            return null;
        // internalize tuple
        int[] intTuple = internalizeTuple(stringTuple);
        if (addTuple(intTuple))
            return intTuple;
        else {
            sayItLoud(lineNo, ": tuple specified twice");
            return null;
        }
    }

    /**
     * addTuple(String[]) performs the internalization and then calls addTuple(int[])
     * <p>
     * TODO: LONG TO SHORT MAPPINGS MUST HAVE BEEN DONE BEFOREHAND
     */
    public synchronized boolean addTuple(String[] stringTuple) {
        return addTuple(internalizeTuple(stringTuple));
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
    protected final synchronized void addTupleWithGeneration(int[] tuple, int gennum) {
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
                ithset = new TCustomHashSet<int[]>(TupleStore.DEFAULT_HASHING_STRATEGY);
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
     * Namespace.shortIsDefault
     */
    public Set<int[]> getTuples(int pos, String obj) {
        final Set<int[]> result = this.index[pos].get(this.objectToId.get(obj));
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
            // the tuple is stored in several sets -- recall that the number of sets is
            // equal to the length of the tuple
            for (int i = 0; i < tuple.length; i++)
                // getTuples() at least returns the EMPTY set, so that remove() can be safely applied!
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
            // the tuple is stored in several sets -- recall that the number of sets is
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
    protected ArrayList<String> extendTupleExternally(final List<String> in, final String front, final String... backs) {
        // make sure to set etuple directly to the right size
        ArrayList<String> etuple = new ArrayList<String>(in.size() + (front == null ? 0 : 1) + backs.length);
        if (front != null)
            etuple.add(front);
        etuple.addAll(in);
        for (String back : backs)
            etuple.add(back);
        return etuple;
    }

    /**
     * similar to extendTupleExternally(), but the extended tuple is furthermore internalized
     *
     * @return the new extended tuple, an int array
     */
    public int[] extendTupleInternally(ArrayList<String> in, String front, String... backs) {
        return internalizeTuple(extendTupleExternally(in, front, backs));
    }

    /**
     * readTuples() reads in a sequence of tuples from a text file;
     * tuples must be finished in a _single_ line, constrained by the following
     * side conditions:
     * + a tuple starts with an URI or a blank node
     * + tuples must have at least one argument
     * + elements of a tuple (URIs, blank nodes, XSD atoms) must be separated
     * by the space character ' '
     * + URIs start with "<" and end with ">" (both for short and long prefixes)
     * + blank nodes start with "_:"
     * + XSD atoms start with '"' and ends with '"', potentially followed by type
     * or language information; inside the leading string value, strings must
     * be enclosed by "\""
     * + comments only start with the '#' character at the very first position of
     * a line
     * + tuples need not end with the '.' character
     * <p>
     * this is essentially N-Triples syntax, see
     * Jan Grant & Dave Beckett: RDF Test Cases, 10 Feb 2004.
     * http://www.w3.org/TR/rdf-testcases/
     * <p>
     * NOTE: if equivalence class reduction is switched on, the cleanup mechanism
     * is always applied AFTER the whole file is read in, but not each time
     * an equivalence relation instance is detected! (more efficient at the
     * very end)
     * <p>
     * NOTE: tuples can be extended by at most one front element and several back
     * elements
     * <p>
     * example
     * <huk> <rdf:type> <Person> .
     * <huk> <dateOfBirth> "1960-08-14"^^<xsd:date> .
     * <huk> <worksFor> <dfki> .
     * <huk> <hasAge> "47"^^<xsd:int> .
     * <huk> <hasName> _:foo42 .
     * _:foo42 <firstName> "Uli" .
     * _:foo42 <lastName> "Krieger" .
     *
     * @param br
     * @param front use null to indicate that there is no front element
     * @param backs use an empty String array that there are no back elements
     * @throws IOException
     * @throws WrongFormatException
     */
    private void readTuplesReally(BufferedReader br, String front, String... backs)
            throws IOException, WrongFormatException {
        this.parser = new TupleParser(br, this);
        this.parser.parse(front, backs);
        logger.info("Store has size {}", allTuples.size());

        //TODO integrate below logic into the rules of the parser
        //TODO parser populates the the tuplestore on the fly
        //TODO no return values to be handled by the TupleStore.

        //TODO test performance against existing code
        //TODO naturally, the new parser has to pass all existing tests
//        String line, token;
//        StringTokenizer st;
//        int noOfTuples = 0, lineNo = 0;
//        ArrayList<String> tuple = new ArrayList<String>();
//        boolean eol = true;
//        while ((line = br.readLine()) != null) {
//            // strip of spaces at begin and end of line
//            line = line.trim();
//            ++lineNo;
//            // empty lines are NOT recognized as tuples of length 0
//            if (line.length() == 0)
//                continue;
//            // skip comments
//            if (line.startsWith("#"))
//                continue;
//            // generate a string tuple representation for each line;
//            // note: variables are not allowed in ground tuples (facts)
//            st = new StringTokenizer(line, " <>_\"\\", true);
//            tuple.clear();
//            // iterate over the tokens of the tuple
//            while (st.hasMoreTokens()) {
//                token = st.nextToken();
//                if (token.equals("<"))
//                    parseURI(st, tuple);
//                else if (token.equals("_"))
//                    parseBlankNode(st, tuple);
//                else if (token.equals("\""))
//                    parseAtom(st, tuple);
//                else if (token.equals(" "))  // keep on parsing ...
//                    continue;
//                    // next is optional: tuple needs not end in '.', end of line is also OK
//                else if (token.equals("."))
//                    break;
//                    // something has gone wrong during read in
//                else {
//                    eol = sayItLoud(lineNo, ": tuple misspelled");
//                    break;
//                }
//            }
//            if (eol) {
//                // now add one potential front element and further potential back elements;
//                // but check whether (front == null) & (backs.length == 0) in order to avoid a
//                // useless copy of 'tuple'
//                if ((front != null) || (backs.length != 0))
//                    tuple = extendTupleExternally(tuple, front, backs);
//                // external tuple representation might be misspelled or the tuple is already contained
//                if (addTuple(tuple, lineNo) != null)
//                    ++noOfTuples;  // everything was fine
//            } else
//                eol = true;
//        }
//        if (this.verbose) {
//            logger.info("\n  read " + noOfTuples + " proper tuples");
//            logger.info("  overall " + this.allTuples.size() + " unique tuples");
//            // some further statistics
//            int noOfURIs = 0, noOfBlanks = 0, noOfAtoms = 0;
//            for (int i = 0; i < this.idToObject.size(); i++) {
//                if (this.idToObject.get(i).startsWith("<"))
//                    ++noOfURIs;
//                else if (this.idToObject.get(i).startsWith("_"))
//                    ++noOfBlanks;
//                else
//                    ++noOfAtoms;
//            }
//            logger.info("  found " + noOfURIs + " URIs");
//            logger.info("  found " + noOfBlanks + " blank nodes");
//            logger.info("  found " + noOfAtoms + " XSD atoms");
//        }
//        // finally cleanup
//        if (this.equivalenceClassReduction) {
//            if (this.verbose)
//                logger.info("\n  applying equivalence class reduction ... ");
//            final int all = this.allTuples.size();
//            final int no = cleanUpTupleStore();
//            if (this.verbose) {
//                logger.info("  removing " + no + " equivalence relation instances");
//                logger.info("  removing " + (all - this.allTuples.size()) + " resulting duplicates");
//                logger.info("  number of all tuples: " + this.allTuples.size() + "\n");
//            }
//        }
    }

    public void readTuples(BufferedReader br, String front, String... backs)
            throws IOException, WrongFormatException {
        try {
            blankNodeSuffix = String.format("%0,3d", blankNodeSuffixNo);
            ++blankNodeSuffixNo;
            readTuplesReally(br, front, backs);
        } finally {
            blankNodeSuffix = null;
        }
    }

    /**
     * this version of readTuples() adds one back element, viz., a time stamp given
     * as a Java long and represented by an XSD long
     *
     * @param br
     * @param timeStamp
     * @throws IOException
     * @throws WrongFormatException
     */
    public void readTuples(BufferedReader br, long timeStamp) throws IOException, WrongFormatException {
        // construct an HFC XSD long for the Java long
        readTuples(br, null, XsdLong.toString(timeStamp, true));
    }

    /**
     * read in tuples from a buffered reader
     *
     * @param br
     * @throws IOException
     * @throws WrongFormatException
     */
    public void readTuples(BufferedReader br) throws IOException, WrongFormatException {
        readTuples(br, null);
    }

    /**
     * read in the tuple file as it is
     *
     * @param filename
     * @throws FileNotFoundException
     * @throws IOException
     * @throws WrongFormatException
     */
    public void readTuples(String filename) throws FileNotFoundException, IOException, WrongFormatException {
        if (this.verbose)
            logger.info("\n  reading tuples from " + filename + " ...");
        readTuples(Files.newBufferedReader(new File(filename).toPath(),
                Charset.forName(this.inputCharacterEncoding)));
    }

    /**
     * read in the tuple file and add potential front and back elements to every tuple
     *
     * @param filename
     * @throws FileNotFoundException
     * @throws IOException
     * @throws WrongFormatException
     */
    public void readTuples(String filename, String front, String... backs)
            throws FileNotFoundException, IOException, WrongFormatException {
        //if (this.verbose)
            logger.info("\n  reading tuples from " + filename + " ...");
        readTuples(Files.newBufferedReader(new File(filename).toPath(),
                Charset.forName(this.inputCharacterEncoding)), front, backs);
    }

    /**
     * reads in a 'compressed' tuple store (extension usually "ts") generated by
     * writeTupleStore(); usage:
     * Namespace ns = new Namespace("...");
     * TupleStore ts = new TupleStore(ns);
     * ts.readTupleStore("...");
     *
     * @return the set of ALL tuples stored in the this TupleStore instance
     */
    protected Set<int[]> readTupleStore(String filename) {
        logger.info("\n  reading tuples from " + filename + " ...");
        String line, token;
        int noOfTuples = 0, lineNo = 0;
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(filename),
                    this.inputCharacterEncoding));
            int noOfLines;
            while ((line = br.readLine()) != null) {
                noOfLines = Integer.parseInt(line.substring(line.indexOf(' ') + 1));
                if (line.startsWith("&object2id"))
                    readObjectToId(br, noOfLines);
                else if (line.startsWith("&id2object"))
                    // also assigns null values for idToJavaObject
                    readIdToObject(br, noOfLines);
                else if (line.startsWith("&tuples"))
                    readAllTuples(br, noOfLines);
                else {
                    logger.info("\nwrong section name: " + line);
                    throw new RuntimeException("FATAL ERROR");
                }
            }
        } catch (IOException e) {
            logger.error("\nerror while reading tuples from " + filename);
            throw new RuntimeException("FATAL ERROR");
        }
        logger.info("\n  read " + noOfTuples + " proper tuples");
        logger.info("  overall " + this.allTuples.size() + " unique tuples");
        return this.allTuples;
    }

    /**
     * helper for readTupleStore()
     */
    private void readObjectToId(BufferedReader br, int noOfLines) throws IOException {
        String line;
        int seppos;
        for (int i = noOfLines; i > 0; i--) {
            line = br.readLine();
            seppos = line.lastIndexOf(' ');
            this.objectToId.put(line.substring(0, seppos),
                    Integer.parseInt(line.substring(seppos + 1)));
        }
    }

    /**
     * helper for readTupleStore()
     */
    private void readIdToObject(BufferedReader br, int noOfLines) throws IOException {
        for (int i = noOfLines; i > 0; i--) {
            this.idToObject.add(br.readLine());
            this.idToJavaObject.add(null);
        }
    }

    /**
     * helper for readTupleStore()
     */
    private void readAllTuples(BufferedReader br, int noOfLines) throws IOException {
        String line;
        int begin, end, length;
        int[] tuple;
        for (int i = noOfLines; i > 0; i--) {
            line = br.readLine();
            end = line.indexOf(' ');
            length = Integer.parseInt(line.substring(0, end));
            tuple = new int[length];
            begin = end + 1;
            for (int j = 0; j < length; j++) {
                end = line.indexOf(' ', begin);
                tuple[j] = Integer.parseInt(line.substring(begin, end));
                begin = end + 1;
            }
            addTuple(tuple);
        }
    }

    /**
     * a URI starts with a '<' and ends with a '>';
     */
    public String parseURI(StringTokenizer st, ArrayList<String> tuple) {
        // the leading '<' char has already been consumed, so consume tokens until we
        // find the closing '>'
        // note: no blanks are allowed inside a URI, but a URI might clearly contain
        // '_' and '\' chars, so reading only the next two tokens would lead to wrong
        // results in general
        StringBuilder sb = new StringBuilder("<");
        String token;
        while (st.hasMoreTokens()) {
            token = st.nextToken();
            if (token.equals(">"))
                break;
            else
                // normalize the namespace
                sb.append(this.namespace.normalizeNamespaceUri(token));
        }
        token = sb.append(">").toString();
        tuple.add(token);
        return token;
    }


    /**
     * a blank node starts with a "_:" and ends before the next whitespace character,
     * i.e., no whitespaces are allowed inside the name of the blank node;
     * since, blank nodes make no reference to a namespace, we make this a static method
     */

    protected void parseBlankNode(StringTokenizer st, ArrayList<String> tuple) {
        // the leading '_' char has already been consumed, so consume tokens until we
        // find the next whitespace char
        StringBuilder sb = new StringBuilder("_");
        sb.append(st.nextToken());  // the rest of the blank node
        if (blankNodeSuffix != null) sb.append('X').append(blankNodeSuffix);
        tuple.add(sb.toString());
    }

    /**
     * XSD atoms are strings optionally followed by either a type identifier (^^type)
     * or a language tag (@lang);
     * within the preceding string, further strings are allowed, surrounded by "\"",
     * as well as spaces, "\\", etc.
     */
    public String parseAtom(StringTokenizer st, ArrayList<String> tuple) {
        StringBuilder sb = new StringBuilder("\"");
        boolean backquote = false;
        String token;
        while (st.hasMoreTokens()) {
            token = st.nextToken();
            if (!backquote && token.equals("\""))
                // string fully parsed
                break;
            if (token.equals("\\"))
                backquote = true;
            else
                backquote = false;
            sb.append(token);
        }
        sb.append("\"");
        // now gather potential additional information (XSD Type or language tag);
        // the first whitespace char terminates XSD atom recognition
        //
        // type checking of XSD atoms should be implemented HERE -- use a second
        // string buffer to separate the bare atom from its type
        boolean bareAtom = true;
        while (st.hasMoreTokens()) {
            token = st.nextToken();
            if (token.equals(" "))
                break;
            else {
                bareAtom = false;
                if (!(token.equals("^^") || token.equals("<") || token.equals(">")))
                    // normalize namespace
                    token = this.namespace.normalizeNamespaceUri(token);
                sb.append(token);
            }
        }
        if (bareAtom) {
            // complete type in order to recognize duplicates (perhaps output a message?)
            if (this.namespace.shortIsDefault)
                sb.append("^^").append(XsdString.SHORT_NAME);
            else
                sb.append("^^").append(XsdString.LONG_NAME);
        }
        token = sb.toString();
        Pattern p = Pattern.compile("\\\\u(\\p{XDigit}{4})");
        Matcher m = p.matcher(token);
        StringBuffer buf = new StringBuffer(token.length());
        while (m.find()) {
            String ch = String.valueOf((char) Integer.parseInt(m.group(1), 16));
            m.appendReplacement(buf, Matcher.quoteReplacement(ch));
        }
        m.appendTail(buf);
        tuple.add(buf.toString());
        return token;
    }

    /**
     * variables in a rule or query tuple start with a '?' char and are followed by a string,
     * containing no whitespace chars;
     * since variables make no reference to a namespace, we make this a static method
     */
    public static String parseVariable(StringTokenizer st, ArrayList<String> tuple) {
        // '?' already consumed
        StringBuilder sb = new StringBuilder("?");
        sb.append(st.nextToken());  // the rest of the URI
        String var = sb.toString();
        tuple.add(var);
        return var;
    }

    /**
     * a simple version that writes all tuple from the tuple store to a file using
     * the external representation;
     * intended file extension is '.nt' (to indicate N-Tiple syntax)
     */
    public synchronized void writeTuples(String filename) {
        writeTuples(this.allTuples, filename);
    }

    /**
     * a simple version that writes all tuple from the tuple store to a file using
     * the external representation (encoding: UTF-16);
     * intended file extension is '.nt' (to indicate N-Tiple syntax)
     * can be used to iterate over lists or sets
     */
    public void writeTuples(Collection<int[]> collection, String filename) {
        if (this.verbose)
            logger.info("  writing tuples to " + filename + " ...");
        try {
            PrintWriter pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(filename),
                    this.outputCharacterEncoding));
            for (int[] tuple : collection)
                pw.println(toString(tuple));
            pw.flush();
            pw.close();
        } catch (IOException e) {
            logger.error("Error while writing tuples to " + filename);
            throw new RuntimeException("FATAL ERROR");
        }
    }

    /**
     * this method differs from writeTuples() above in that it always tries to
     * fully expand the namespace of an URI when writing out tuples
     */
    public void writeExpandedTuples(String filename) {
        if (this.verbose)
            logger.info("  writing tuples to " + filename + " ...");
        try {
            PrintWriter pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(filename),
                    this.outputCharacterEncoding));
            for (int[] tuple : this.allTuples)
                pw.println(toExpandedString(tuple));
            pw.flush();
            pw.close();
        } catch (IOException e) {
            logger.error("Error while writing tuples to " + filename);
            throw new RuntimeException("FATAL ERROR");
        }
    }


    /**
     * a simple version that writes the relevant parts of a tuple store to a file, using
     * a specific format; much faster than Java's serialization;
     * intended file extension is '.ts' (for tuple store)
     */
    public void writeTupleStore(String filename) {
        if (this.verbose)
            logger.info("  writing tuple store to " + filename + " ...");
        try {
            PrintWriter pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(filename),
                    this.outputCharacterEncoding));
            // dump objectToId (null -> 0 given by constructor is overwritten by exactly the same mapping)
            pw.println("&object2id " + this.objectToId.size());
            for (Map.Entry<String, Integer> entry : this.objectToId.entrySet())
                pw.println(entry.getKey() + " " + entry.getValue());
            // dump idToObject
            pw.println("&id2object " + (this.idToObject.size() - 1));
            // start with 1, since TupleStore constructor assigns a special meaning to index 0
            for (int i = 1; i < this.idToObject.size(); i++)
                // no need to write i (ascending order!)
                pw.println(this.idToObject.get(i));
            // do _not_ dump idToJavaObject: null values are assigned (lazy!) when tuple store is read in
            // dump allTuples
            pw.println("&tuples " + this.allTuples.size());
            for (int[] tuple : this.allTuples) {
                pw.print(tuple.length + " ");
                for (int i = 0; i < tuple.length; i++)
                    pw.print(tuple[i] + " ");
                pw.println();
            }
            pw.flush();
            pw.close();
        } catch (IOException e) {
            logger.error("Error while writing tuples to " + filename);
            throw new RuntimeException("FATAL ERROR");
        }
    }

    /**
     * generates an external string representation from the internal int[]
     * representation of a tuple
     */
    public String toString(int[] tuple) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < tuple.length; i++)
            sb.append(toUnicode(getObject(tuple[i])) + " ");
        sb.append(".");
        return sb.toString();
    }

    public String toUnicode(String in) {
        StringBuilder out = new StringBuilder();
        for (int i = 0; i < in.length(); i++) {
            final char ch = in.charAt(i);
            if (ch <= 127) out.append(ch);
            else out.append("\\u").append(String.format("%04x", (int) ch));
        }
        return out.toString();
    }

    /**
     * this method differs from toString() above in that is always tries
     * to fully expand the namespace of an URI (if present)
     */
    public String toExpandedString(int[] tuple) {
        StringBuilder sb = new StringBuilder();
        String literal;
        for (int i = 0; i < tuple.length; i++) {
            // distinguish between URIs vs. XSD atoms or blank nodes
            literal = getObject(tuple[i]);
            if (TupleStore.isAtom(literal) || TupleStore.isBlankNode(literal))
                sb.append(toUnicode(literal.replace("xsd:", this.namespace.getLongForm("xsd")) + " "));
            else
                // a URI
                sb.append(toUnicode(this.namespace.expandUri(literal) + " "));
        }
        sb.append(".");
        return sb.toString();
    }

    /**
     * checks whether a tuple (represented as a string array) is contained in the
     * tuple store
     */
    public boolean ask(String[] externalTuple) {
        int[] internalTuple = new int[externalTuple.length];
        for (int i = 0; i < externalTuple.length; i++) {
            // check whether the external symbols are even known by the tuple store
            if (!this.objectToId.containsKey(externalTuple[i]))
                return false;
            internalTuple[i] = this.objectToId.get(externalTuple[i]);
        }
        return this.allTuples.contains(internalTuple);
    }

    /**
     * checks whether a tuple (represented as an array list of strings) is contained
     * in the tuple store
     */
    public boolean ask(ArrayList<String> externalTuple) {
        return ask(externalTuple.toArray(new String[externalTuple.size()]));
    }

    /**
     * use this method for containment test in order not to make this.allTuples public
     */
    public boolean ask(int[] tuple) {
        return this.allTuples.contains(tuple);
    }

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
     */
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
     */
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
        // first check whether pattern is a _trigger_ clause, i.e., containing only atoms
        if ((table.properVariables.size() == 0) && (table.dontCareVariables.size() == 0)) {
            // if so, directly check whether ground tuple is contained in set of all tuples
            if (this.allTuples.contains(pattern)) {
                // not sure whether I should add a strategy object with an empty int[] for the
                // positions here
                Set<int[]> set = new THashSet<int[]>();
                set.add(pattern);
                return set;
            } else
                // trigger clause not contained in tuple store: return empty set
                return new THashSet<int[]>();
        }
        // note: pattern with don't cares and constants only gets standard handling;
        //       because (properVariables.size() == 0) or (properPositions.length == 0)
        //       we know that tuples returned are not involved in matching, since there
        //       are no proper variables!!
        // then check whether pattern is made only of variables only
        boolean onlyVars = true;
        int length = pattern.length;
        Set<int[]> query;
        Set<int[]> result = null;
        for (int i = 0; i < length; i++) {
            if (!RuleStore.isVariable(pattern[i])) {
                onlyVars = false;
                // note: querying the index with a constant from a rule that is not contained
                //       in the initial fact set, will results in the null value;
                // this is because I do NOT want to exclude rules that refer to constants that
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
        query = result;  // reuse query
        // use relevant position (and NOT proper), since in-eqs might be applied
        result = new TCustomHashSet<int[]>(new TIntArrayHashingStrategy(table.getRelevantPositions()));
        for (int[] tuple : query) {
            if (tuple.length == length)
                result.add(tuple);
        }
        // note: result now no longer refers to sets from the index, thus manipulating
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
            outerloop:
            for (int i = 0; i < duplPos.length; i++) {
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
     * returns a copy of the tuple store that can be used to generate "choice points",
     * e.g., during reasoning, as is done by the forward chainer
     * <p>
     * The copy uses the same namespace object as this object
     *
     * @see de.dfki.lt.hfc.ForwardChainer.copyForwardChainer()
     */
    public TupleStore copyTupleStore() {
        TupleStore copy = new TupleStore();
        copy.currentId = this.currentId;  // means different things in different tuple stores
        copy.minNoOfArgs = this.minNoOfArgs;
        copy.maxNoOfArgs = this.maxNoOfArgs;
        copy.verbose = this.verbose;
        copy.rdfCheck = this.rdfCheck;
        copy.exitOnError = this.exitOnError;
        copy.namespace = this.namespace;
        copy.equivalenceClassReduction = this.equivalenceClassReduction;
        // JavaDoc says clone() returns deep copy in both cases; second clone does not need casting
		/*
		copy.uriToProxy = (TIntIntHashMap)this.uriToProxy.clone();
		copy.proxyToUris = this.proxyToUris.clone();
		copy.uriToEquivalenceRelation = (TIntIntHashMap)this.uriToEquivalenceRelation.clone();
		*/
        copy.uriToProxy = new TIntIntHashMap(this.uriToProxy);
        copy.proxyToUris = new TIntObjectHashMap<TIntArrayList>(this.proxyToUris);
        copy.uriToEquivalenceRelation = new TIntIntHashMap(this.uriToEquivalenceRelation);
        // use copy constructor for objectToId, idToObject, idToJavaObject, and allTuples
        copy.objectToId = new HashMap<String, Integer>(this.objectToId);
        copy.idToObject = new ArrayList<String>(this.idToObject);
        copy.idToJavaObject = new ArrayList<AnyType>(this.idToJavaObject);
        copy.allTuples = new TCustomHashSet<int[]>(TupleStore.DEFAULT_HASHING_STRATEGY, this.allTuples);
        // operatorRegistry and aggregateRegistry need to be copied (above mappings might be different
        // for different tuple stores)
        copy.operatorRegistry = new OperatorRegistry(copy, this.operatorRegistry);
        copy.aggregateRegistry = new AggregateRegistry(copy, this.aggregateRegistry);
        // index needs to be copied
        copy.index = copyIndex();
        // finished!
        return copy;
    }

//    /**
//     * uploads further namespaces stored in a file to an already established forward chainer;
//     * this method directly calls readNamespaces() from class Namespace
//     *
//     * @throws IOException
//     * @throws WrongFormatException
//     * @throws FileNotFoundException
//     * @see Namespace.readNamespaces()
//     */
//    public void uploadNamespaces(String filename)
//            throws FileNotFoundException, WrongFormatException, IOException {
//        this.namespace.readNamespaces(filename);
//    }


    /**
     * returns a nearly-deep copy of the index: everything is copied with the notable
     * exception of the tuples (the int arrays) at the leaves of the index (as their
     * content does not change!
     */
    private Map<Integer, Set<int[]>>[] copyIndex() {
        // the inital creation is taken from the binary constructor
        Map<Integer, Set<int[]>>[] copy = (HashMap[]) Array.newInstance(HashMap.class, this.maxNoOfArgs);
        for (int i = 0; i < this.maxNoOfArgs; i++) {
            // shallow copy this.index[i], but after that also shallow copy the values of the map
            copy[i] = new HashMap<Integer, Set<int[]>>(this.index[i]);
            for (Integer key : copy[i].keySet()) {
                copy[i].put(key, new TCustomHashSet<int[]>(TupleStore.DEFAULT_HASHING_STRATEGY, copy[i].get(key)));
            }
        }
        return copy;
    }



    public Operator getOperator(String name) {
        return this.operatorRegistry.checkAndRegister(name);
    }

}
