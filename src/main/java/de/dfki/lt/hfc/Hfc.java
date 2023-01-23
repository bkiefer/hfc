package de.dfki.lt.hfc;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.dfki.lt.hfc.indices.IndexingException;
import de.dfki.lt.hfc.io.QueryParseException;

public class Hfc {

  /**
   * q
   * HFC version number string
   */
  public static final String VERSION = "6.3.0";

  /**
   * HFC info string
   */
  public static final String INFO = "v" + Hfc.VERSION + " (Fri Jul  8 15:36:17 CEST 2016)";

  /**
   * A basic LOGGER.
   */
  private static final Logger logger = LoggerFactory.getLogger(Hfc.class);


  /**
   * I'm making _all_ the potentially relevant object directly accessible
   */
  protected TupleStore _tupleStore = null;

  /**
   * transaction time time stamp used by Hfc.readTuples(BufferedReader tupleReader)
   */
  public long timeStamp = 0L;

  /**
   * fields we would like to customize in NamespaceManager, TupleStore, RuleStore, and ForwardChainer
   * and which can be altered by calling customizeHfc() below;
   * the collection of field names with their values should be grouped in a Map and should be
   * handed over as settings to customizeHfc();
   * some of the settings affects more than one object; e.g., "verbose" modifies the output
   * in the NamespaceManager, TupleStore, RuleStore, and ForwardChainer objects;
   * here, we are also assigning DEFAULT values which are used in case they are not specified
   * in the settings map;
   * the DEFAULT settings basically address the RDF triple case without equivalence class reduction
   */
  protected Config config;

  protected RuleStore _ruleStore = null;

  protected ForwardChainer _forwardChainer = null;

  /* visibility is package to do tests */
  protected NamespaceManager _namespace;

  /* visibility is package to do tests */
  IndexStore _indexStore;

  private Writer persistencyWriter;

  public Hfc(String configPath) throws IOException, WrongFormatException {
    this(Config.getInstance(configPath));
  }

  public Hfc(Config config) throws IOException, WrongFormatException {
    this.config = config;
    _indexStore = createIndexStore(config);
    _namespace = config.createNamespaceManager();
    _tupleStore = config.createTupleStore(_namespace, _indexStore);
    _ruleStore = config.createRuleStore(_tupleStore);
    _forwardChainer = new ForwardChainer(_tupleStore, _ruleStore, config);
    persistencyWriter = config.treatPersistencyFile(_tupleStore);
    logger.info("  Welcome to HFC, HUK's Forward Chainer");
    logger.info("  " + Hfc.INFO);
    logger.info("  # CPU cores: " + config.getNoOfCores());
    logger.info("  " + this.toString());
  }

  public void shutdown() {
    _forwardChainer.shutdown();
  }

  /** This is for adding namespace mappings from a remote client.
   *
   *  @return false if another namespace exists that contradicts the given
   *  arguments, in this case this mapping is ignored.
   *
   *  TODO: does the client need the namespace mappings, too? Should they be
   *  synchronized?
   */
  public boolean addNamespace(String shortForm, String longForm) {
    return _namespace.putForm(shortForm, longForm,
        config.isShortIsDefault());
  }

  public void setShortIsDefault(boolean shortIsDefault) {
    config.setShortIsDefault(shortIsDefault);
    _namespace.setShortIsDefault(shortIsDefault);
  }

  private IndexStore createIndexStore(HashMap<String, Object> indexSettings)
      throws IndexingException {
    return new IndexStore(indexSettings);
  }

  @SuppressWarnings({ "unchecked", "rawtypes" })
  private IndexStore createIndexStore(Config config) {
    IndexStore indexStore;
    try {
      HashMap<String, Object> settings = new HashMap<>();
      if (config.has("Index")) {
        for (Object e : (ArrayList) config.get("Index")) {
          settings.putAll((Map<? extends String, ?>) e);
        }
        indexStore = createIndexStore(settings);
      } else {
        indexStore = null;
      }
    } catch (IndexingException e) {
      logger.error(e.getMessage());
      if (config.isExitOnError())
        System.exit(-1);
      indexStore = null;
    }
    return indexStore;
  }

  /* TODO: ***copy***
  public Config getCopy(int noOfCores, boolean verbose) {
    Config copy = new Config(this.configs);
    copy.configs.put(NOOFCORES, noOfCores);
    copy.configs.put(VERBOSE, verbose);
    copy.namespace = this.namespace.copy();
    if (indexStore != null)
      copy.indexStore = this.indexStore.copy();
    else
      copy.indexStore = null;
    return copy;
  }
  *

  /**
   * read in tuples stored in a file with name filename;
   * tuples that are goind to be read in are extended by at most one front element front
   * and potentially many back elements back;
   * use null as a value for front to indicate that there is no front element, and an
   * empty String array that there are no back elements
   *
   * @param filename
   * @param front
   * @param backs
   * @throws FileNotFoundException
   * @throws IOException
   * @throws WrongFormatException
   */
  public void uploadTuples(String filename, String front, String... backs)
          throws FileNotFoundException, IOException, WrongFormatException {
    _tupleStore.readTuples(config.readerFromFilename(filename), front, backs);
  }

  public void uploadTuples(String filename) throws IOException, WrongFormatException {
    _tupleStore.readTuples(config.readerFromFilename(filename), null);
  }

  /**
   * read in tuples stored in a file with name filename;
   * tuples that are goind to be read in are extended by at most one front element front
   * and potentially many back elements back;
   * use null as a value for front to indicate that there is no front element, and an
   * empty String array that there are no back elements
   *
   * @param filename
   * @param front
   * @param backs
   * @throws FileNotFoundException
   * @throws IOException
   * @throws WrongFormatException
   */
  public void uploadTuples(Config config, String filename, String front, String... backs)
          throws FileNotFoundException, IOException, WrongFormatException {
    _tupleStore.readTuples(config.readerFromFilename(filename), front, backs);
  }

  public void uploadTuples(Config config, String filename) throws IOException, WrongFormatException {
    _tupleStore.readTuples(config.readerFromFilename(filename), null);
  }

  public BindingTable executeQuery(String query) throws QueryParseException {
    Query q = new Query(_tupleStore);
    // do we want to generate a new Query object for each call of query() (is not needed)?
    // yes, as executeQuery is used in hfc-db by class HfcDbHandler in method selectQuery();
    // otherwise, we would need to synchronize a class-based Query object here
    return q.query(query);
  }

  public boolean computeClosure() {
    logger.info("Compute closure starting with {} tuples",
        _tupleStore.allTuples.size());
    if (null != _forwardChainer) {
      long startClosure = System.currentTimeMillis();
      boolean result =  _forwardChainer.computeClosure(
          config.getIterations(), config.isCleanupRepository());
      long endClosure = System.currentTimeMillis();
      logger.info("Finished closure in {} msec with {} tuples",
          endClosure - startClosure, _tupleStore.allTuples.size());
      return result;
    } else {
      _tupleStore.cleanUpTupleStore();
      return false;
    }
  }

  public boolean computeClosure(int iterations, boolean cleanupRepository) {
    if (null != _forwardChainer){
      return _forwardChainer.computeClosure(iterations, cleanupRepository);
    } else {
      _tupleStore.cleanUpTupleStore();
      return false;
    }
  }


  public boolean computeClosure(Set<int[]> newTuples){
    if (null != _forwardChainer) {
      return _forwardChainer.computeClosure(newTuples, config.getIterations(), true);
    } else {
      _tupleStore.cleanUpTupleStore();
      return false;
    }
  }


  public boolean computeClosure(Set<int[]> newTuples, int iterations, boolean cleanupRepository){
    if (null != _forwardChainer) {
      return _forwardChainer.computeClosure(newTuples,iterations, cleanupRepository);
    } else {
      _tupleStore.cleanUpTupleStore();
      return false;
    }
  }


  /**
   * returns a copy of the forward chainer that can be used to generate "choice points"
   * during reasoning;
   * tuples are taken over, but nearly everything else is copied
   *
   * @param noOfCores an integer, specifying how many parallel threads are used during
   *                  the computation of the deductive closure for the copy of this forward chainer
   *
  public Hfc copyHFC(int noOfCores, boolean verbose) {
    Config configCopy = this.config.getCopy(noOfCores, verbose);
    TupleStore tupleStoreCopy = _tupleIo.copyTupleStore();
    RuleStore ruleStoreCopy = _ruleStore.copyRuleStore(tupleStoreCopy);
    ForwardChainer fcCopy = _forwardChainer.copyForwardChainer(tupleStoreCopy, ruleStoreCopy, noOfCores);
    return new Hfc(configCopy, tupleStoreCopy, ruleStoreCopy, fcCopy);
  }*/


  public boolean isEquivalenceClassReduction() {
    return config.isEqReduction();
  }

  public boolean isCleanUpRepository() {
    return config.isCleanupRepository();
  }

  public boolean shutdownNoExit() {
    return _forwardChainer.shutdownNoExit();
  }

  public boolean enableTupleDeletion() {
    return _forwardChainer.enableTupleDeletion();
  }

  public boolean tupleDeletionEnabled() {
    return _forwardChainer.tupleDeletionEnabled();
  }

  public void setVerbose(boolean b) {
    config.setVerbose(b);
  }

  public Config getConfig(){
    return config;
  }

  /** Currently, it's up to the calling library how to persist new tuples, or
   *  properly remove them, if this allowed.
   * @return A writer to the file where all new tuples are persisted.
   */
  public Writer getPersistencyWriter() {
    return persistencyWriter;
  }

  public HfcStatus status() {
    return new HfcStatus();
  }

  ////////////////////////////////////////////////////////////////////////
  // query and store modification functionality
  ////////////////////////////////////////////////////////////////////////

  public Query getQuery() {
    return new Query(this._tupleStore);
  }

  public int size() {
    return _tupleStore.size();
  }

  public Boolean ask(ArrayList<String> tuple) {
    return _tupleStore.ask(tuple);
  }

  public int nextBlankNode() {
    return _forwardChainer.nextBlankNode();
  }

  public void addTuple(String[] tuple) {
    _tupleStore.addTuple(tuple);
  }

  /**
   * TODO keep this?
   * Normalize namespaces, and get ids directly to put in the tuples without
   * using the hfc internal functions. Also, honor the equivalence reduction
   * by always entering the representative.
   *
   * @param rows  the table that contains the tuples to add to the storage
   * @param front the potentially-empty (== null) front element
   * @param backs arbitrary-many back elements (or an empty array)
   *              <p>
   *              This is done so i can add the <it>now<it/> time stamp transparently
   *              TODO: refactor, and make this part of HFC core
   */
  public int addTuples(List<List<String>> rows, String front, String... backs) {
    return _tupleStore.addTuples(rows, front, backs);
  }

  public int addTuples(List<List<String>> tuples){
    return addTuples(tuples, null);
  }

  public boolean removeIntTuples(Collection<int[]> tuples) {
    boolean success = true;
    for (int[] tuple : tuples) {
      success &= _tupleStore.removeTuple(tuple);
    }
    return success;
  }

  /** For MissionKnowledgeManager */
  public boolean removeTuples(Collection<List<String>> tuples) {
    boolean success = true;
    for (List<String> tuple : tuples) {
      success &= _tupleStore.removeTuple(_tupleStore.internalizeTuple(tuple));
    }
    return success;
  }

  /** For MissionKnowledgeManager */
  public void writeExpandedTuples(String filename) {
    _tupleStore.writeExpandedTuples(filename);
  }

  /** For PAL */
  public void writeTuples(String filename) {
    _tupleStore.writeTuples(filename);
  }

  /**
   * remove tuples, represented as int[], from the set of all tuples;
   * if tuple deletion is enabled in the forward chainer, the tuples from the set are
   * removed from TupleStore.generation
   *
  private boolean removeTuples(Collection<int[]> tuples) {
    boolean success = true;
    for (int[] tuple : tuples)
      if (!_tupleStore.removeTuple(tuple))
        success = false;
    return success;
  }*/

  protected class HfcStatus {

    private final String Version = INFO;

    private int tuples = _tupleStore.noOfTuples;

    private int atoms =  _tupleStore.noOfAtoms;

    private int realTuples = _tupleStore.allTuples.size();

    private int rules = _ruleStore.allRules.size();

    private int namespaces = _tupleStore.namespace.longToNs.size();

  }

  /**
   * at several places, messages were output depending on this.exitOnError
   * and this.verbose -- unify this in this special private method;
   * perhaps will be replaced by Apache's log4j
   */
  public boolean sayItLoud(int lineNo, String message) {
    if (this.config.isExitOnError()) {
      logger.error(" FATAL: " + lineNo + message);
      throw new RuntimeException("FATAL ERROR " + lineNo + " " + message);
    }
    logger.warn(" ERROR(ignored): " + lineNo + message);
    return false;
  }

  public boolean sayItLoud(String rulename, String message) {
    if (this.config.isExitOnError()) {
      logger.error(" FATAL: " + rulename + message);
      throw new RuntimeException("FATAL ERROR " + rulename + " " + message);
    }
    logger.warn(" ERROR(ignored): " + rulename + message);
    return false;
  }

}
