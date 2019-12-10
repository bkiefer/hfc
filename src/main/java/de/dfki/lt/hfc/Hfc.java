package de.dfki.lt.hfc;

import de.dfki.lt.hfc.types.XsdLong;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.*;


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
   * I'm making _all_ the potentially relevant object directly accessable
   */
  public TupleStore _tupleStore = null;

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

  protected   RuleStore _ruleStore = null;

  protected ForwardChainer _forwardChainer = null;


  /**
   * the nullary constructor allocates the minimal object configuration:
   * a namespace and a tuple store are guaranteed to exist
   */
  public Hfc() {
    try {
      this.config = Config.getDefaultConfig();
      _tupleStore = new TupleStore(config);
      _ruleStore = new RuleStore(config,_tupleStore);
      _forwardChainer = new ForwardChainer(_tupleStore, _ruleStore, config);
      init();
    } catch (FileNotFoundException e) {
      logger.error("Was not able to load default configuration");
      e.printStackTrace();
    } catch (IOException | WrongFormatException e) {
      e.printStackTrace();
    }
  }


  public Hfc(String configPath) throws IOException, WrongFormatException {
    this.config = Config.getInstance(configPath);
    _tupleStore = new TupleStore(config);
    _ruleStore = new RuleStore(config,_tupleStore);
    _forwardChainer = new ForwardChainer(_tupleStore, _ruleStore, config);
    init();
  }

  public Hfc(Config config) throws IOException, WrongFormatException {
    this.config = config;
    _tupleStore = new TupleStore(config);
    _ruleStore = new RuleStore(config,_tupleStore);
    _forwardChainer = new ForwardChainer(_tupleStore, _ruleStore, config);
    init();
  }

  /**
   * @deprecated  for testing only
   * @param config
   * @param ts
   * @param rs
   */
  @Deprecated
  public Hfc(Config config, TupleStore ts, RuleStore rs){
    this.config = config;
    _tupleStore = ts;
    _ruleStore = rs;
    _forwardChainer = new ForwardChainer(_tupleStore, _ruleStore, config);
    init();
  }

  private Hfc(Config config, TupleStore tupleStoreCopy, RuleStore ruleStoreCopy, ForwardChainer fcCopy) {
    this.config = config;
    this._tupleStore = tupleStoreCopy;
    this._ruleStore = ruleStoreCopy;
    this._forwardChainer = fcCopy;
  }

  /**
   * initialization code of nullary and binary constructors that is outsourced to avoid
   * code reduplication
   */
  private void init() {
    if (this.config.isVerbose()) {
      logger.info("  Welcome to HFC, HUK's Forward Chainer");
      logger.info("  " + Hfc.INFO);
      logger.info("  # CPU cores: " + config.getNoOfCores());
      logger.info("  " + this.toString());
    }
  }



  public void shutdown() {
    _forwardChainer.shutdown();
  }

  /** customize HFC, i.e., namespace and tuple store via key-value pairs
   * from parameter settings which affect fields in these objects;
   * the fields for the rule store and forward chainer are assigned values
   * the first time rules are uploaded to HFC
   * This method cannot be used to change namespaces, tuplefiles or rulefiles.
   * Please use the dedicated methods to do so.
   */
  private void customizeHfc(Map<String, Object> settings) {
    logger.info("HFC settings: " + settings);
    // make the settings available via protected fields in this class;
    // alphabetical order:
    this.config.updateConfig(settings);
  }

  public void updateConfig(String path){
   try {
    customizeHfc(Config
            .getMapping(path));
   } catch (FileNotFoundException e) {
    e.printStackTrace();
    System.err.println("Config File " + path + " not found. Will be ignored");
   }
  }


  public void addNamespace(String shortForm, String longForm) {
    config.addNamespace(shortForm, longForm);
  }

  public void readTuples(BufferedReader tupleReader)
          throws WrongFormatException, IOException {
    _tupleStore.readTuples(tupleReader);
  }


  public void readTuples(File tuples)
          throws WrongFormatException, IOException {
    readTuples(Files.newBufferedReader(tuples.toPath(),
            Charset.forName(_tupleStore.inputCharacterEncoding)));
  }

  /**
   * uploads further tuples stored in a file to an already established forward chainer;
   * this method directly calls readTuples() from class TupleStore;
   * if tuple deletion is enabled in the forward chainer, the tuples from the file are
   * assigned the actual generation TupleStore.generation
   *
   * @throws IOException
   * @throws FileNotFoundException
   * @throws WrongFormatException
   */
  public void readTuples(String filename) throws FileNotFoundException, IOException, WrongFormatException {
    _tupleStore.readTuples(filename);
  }


  public void readTuples(File tuples, long timestamp)
          throws WrongFormatException, IOException {
    _tupleStore.readTuples(Files.newBufferedReader(tuples.toPath(),
            Charset.forName(_tupleStore.inputCharacterEncoding)),
            null, new XsdLong(timestamp).toString());
  }



  private int getSymbolId(String symbol) {
    int id = _tupleStore.putObject(symbol);
    if (_tupleStore.equivalenceClassReduction) {
      id = _tupleStore.getProxy(id);
    }
    return id;
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
    // normalize namespaces for front and backs
    int frontId = -1;    // Java wants an initial value
    if (front != null)
      frontId = getSymbolId(front);
    int[] backIds = new int[backs.length];
    if (backs.length != 0) {
      for (int i = 0; i < backs.length; ++i)
        backIds[i] = getSymbolId(backs[i]);
    }
    // (front == null) means _no_ front element
    final int frontLength = (front == null) ? 0 : 1;
    final int backLength = backs.length;
    int[] tuple;
    int noOfTuples = 0;
    // extend and add tuples, given by parameters rows, and front and backs
    for (List<String> row : rows) {
      tuple = new int[row.size() + frontLength + backLength];
      int i = 0;
      // front element
      if (front != null) {
        tuple[0] = frontId;
        i = 1;
      }
      // table row
      for (String s : row) {
        tuple[i++] = getSymbolId(s);
      }
      // back elements
      if (backs.length != 0) {
        for (int j = 0; j < backs.length; ++j)
          tuple[i++] = backIds[j];
      }
      if (_tupleStore.addTuple(tuple)) {
        ++noOfTuples;
      }
    }
    return noOfTuples;
  }

  public int addTuples(List<List<String>> tuples){
    int noOfTuples = 0;
    for(List<String> tuple : tuples){
      _tupleStore.addTuple(tuple.toArray(new String[tuple.size()]));
      ++noOfTuples;
    }
    return noOfTuples;

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
  public void uploadTuples(String filename, String front, String... backs)
          throws FileNotFoundException, IOException, WrongFormatException {
    _tupleStore.readTuples(filename, front, backs);
  }


  public void uploadTuples(String filename) throws IOException, WrongFormatException {

      _tupleStore.readTuples(filename);

  }



  /**
   * add tuples, represented as int[], to the set of all tuples;
   * if tuple deletion is enabled in the forward chainer, the tuples from the set are
   * assigned the actual generation TupleStore.generation
   */
  public boolean addTuples(Collection<int[]> tuples) {
    boolean success = true;
    for (int[] tuple : tuples)
      if (!_tupleStore.addTuple(tuple))
        success = false;
    return success;
  }


  /**
   * remove tuples, represented as int[], from the set of all tuples;
   * if tuple deletion is enabled in the forward chainer, the tuples from the set are
   * removed from TupleStore.generation
   */
  public boolean removeTuples(Collection<int[]> tuples) {
    boolean success = true;
    for (int[] tuple : tuples)
      if (!_tupleStore.removeTuple(tuple))
        success = false;
    return success;
  }



  /**
   * @param rules
   * @throws IOException
   */
  public void readRules(File rules) throws IOException {
    // in case no rule store has been defined so far, create one, but also
    // create a forward chainer, as rules alone are useless
    if (_ruleStore == null) {
      // sexternary constructor would suffice here
      _ruleStore = new RuleStore(Config.getDefaultConfig(), _tupleStore);
      // customize rule store settings
      _ruleStore.minNoOfArgs = config.getMinArgs();
      _ruleStore.maxNoOfArgs = config.getMaxArgs();
      _ruleStore.verbose = config.isVerbose();
      _ruleStore.rdfCheck = config.isRdfCheck();
      // after defining the approapriate settings, load the _first_ rule file
      _ruleStore.readRules(rules.getAbsolutePath());
      // create forward chainer
      _forwardChainer = new ForwardChainer(_tupleStore, _ruleStore, config);
    } else {
      // value of noOfTask in the forward chainer needs to be adapted every time
      // new rules are read in !!
      _ruleStore.readRules(rules.getAbsolutePath());
      _forwardChainer.noOfTasks = _ruleStore.allRules.size();
    }
  }


  /**
   * uploads further rules stored in a file to an already established forward chainer;
   * the set of all rules is returned;
   * NOTE: a similar method readRules() is defined in class RuleStore;
   * however, uploadRules set the field noOfTasks in ForwardChainer to the proper value
   *
   * @throws IOException
   */
  public void uploadRules(String filename) throws IOException {
    _ruleStore.lineNo = 0;
    _ruleStore.readRules(filename);
    // update noOfTask in order to guarantee proper rule execution in a multi-threaded environment
    if(_forwardChainer != null)
      _forwardChainer.noOfTasks = _ruleStore.allRules.size();
  }


  public BindingTable executeQuery(String query) throws QueryParseException {
    Query q = new Query(_tupleStore);
    // do we want to generate a new Query object for each call of query() (is not needed)?
    // yes, as executeQuery is used in hfc-db by class HfcDbHandler in method selectQuery();
    // otherwise, we would need to synchronize a class-based Query object here
    return q.query(query);
  }


  public boolean computeClosure() {
    logger.info("Compute closure starting with "+ _tupleStore.allTuples.size()+" tuples");
    if (null != _forwardChainer) {
      boolean result =  _forwardChainer.computeClosure(config.getIterations(), config.isCleanupRepository());
      logger.info("Finished  closure ending with " + _tupleStore.allTuples.size()+ " tuples");
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
      return  false;
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
   */
  public Hfc copyHFC(int noOfCores, boolean verbose) {
    Config configCopy = this.config.getCopy(noOfCores, verbose);
    TupleStore tupleStoreCopy = _tupleStore.copyTupleStore();
    RuleStore ruleStoreCopy = _ruleStore.copyRuleStore(tupleStoreCopy);
    ForwardChainer fcCopy = _forwardChainer.copyForwardChainer(tupleStoreCopy, ruleStoreCopy, noOfCores);
    return new Hfc(configCopy, tupleStoreCopy, ruleStoreCopy, fcCopy);
  }


  public boolean isEquivalenceClassReduction() {
    return config.isEqReduction();
  }

  public boolean isCleanUpRepository() {
    return config.isCleanupRepository();
  }

  public Query getQuery() {
    return new Query(this._tupleStore);
  }

  public Boolean ask(ArrayList<String> tuple) {
    return _tupleStore.ask(tuple);
  }

  public boolean shutdownNoExit() {
    return _forwardChainer.shutdownNoExit();
  }

  public int nextBlankNode() {
    return _forwardChainer.nextBlankNode();
  }

  public void addTuple(String[] tuple) {
    _tupleStore.addTuple(tuple);
  }

  public boolean enableTupleDeletion() {
    return _forwardChainer.enableTupleDeletion();
  }

  public boolean tupleDeletionEnabled() {
    return _forwardChainer.tupleDeletionEnabled();
  }


  public void setNoOfCores(int i) {
    config.updateConfig(Config.NOOFCORES, i);
  }


  public void setVerbose(boolean b) {
    config.setVerbose(b);
    _tupleStore.verbose = b;
    _ruleStore.verbose = b;
  }

  public Config getConfig(){
    return config;
  }

  public HfcStatus status() {
    return new HfcStatus();
  }


  protected class HfcStatus {

    private final String Version = INFO;

    private int tuples = _tupleStore.noOfTuples;

    private int atoms =  _tupleStore.noOfAtoms;

    private int realTuples = _tupleStore.allTuples.size();

    private int rules = _ruleStore.allRules.size();

    private int namespaces = _tupleStore.namespace.longToNs.size();

  }
}
