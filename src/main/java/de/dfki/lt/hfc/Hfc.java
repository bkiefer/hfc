package de.dfki.lt.hfc;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;

import de.dfki.lt.hfc.types.XsdLong;

public class Hfc {

  /**
   * I'm making _all_ the potentially relevant object directly accessable
   */
  private Namespace _namespace = null;
  private TupleStore _tupleStore = null;
  private RuleStore _ruleStore = null;
  private ForwardChainer _forwardChainer = null;

  /**
   * fields we would like to customize in Namespace, TupleStore, RuleStore, and ForwardChainer
   * and which can be altered by calling customizeHfc() below;
   * the collection of field names with their values should be grouped in a Map and should be
   * handed over as settings to customizeHfc();
   * some of the settings affects more than one object; e.g., "verbose" modifies the output
   * in the Namespace, TupleStore, RuleStore, and ForwardChainer objects;
   * here, we are also assigning DEFAULT values which are used in case they are not specified
   * in the settings map;
   * the DEFAULT settings basically address the RDF triple case without equivalence class reduction;
   */
  protected int noOfCores = 2;
  protected boolean gc = false;
  protected boolean verbose = true;
  protected boolean rdfCheck = true;
  protected boolean equivalenceClassReduction = false;
  protected boolean cleanUpRepository = true;
  protected int noOfIterations = Integer.MAX_VALUE;
  protected int minNoOfArgs = 3;
  protected int maxNoOfArgs = 3;
  protected int subjectPosition = 0;
  protected int predicatePosition = 1;
  protected int objectPosition = 2;
  protected int noOfAtoms = 10000;
  protected int noOfTuples = 100000;
  protected String characterEncoding = "UTF-8";
  protected String persistencyFile = "/tmp/tuples.nt";

  /**
   * transaction time time stamp used by Hfc.readTuples(BufferedReader tupleReader)
   */
  public long timeStamp = 0L;

  /**
   * the nullary constructor allocates the minimal object configuration:
   * a namespace and a tuple store are guaranteed to exist
   */
  public Hfc() {
    _namespace = new Namespace();
    _tupleStore = new TupleStore(_namespace);
  }

  // customize HFC, i.e., namespace and tuple store via key-value pairs
  // from parameter settings which affect fields in these objects;
  // the fields for the rule store and forward chainer are assigned values
  // the first time rules are uploaded to HFC
  public void customizeHfc(Map<String, String> settings) {
    System.out.println(settings);
    // make the settings available via protected fields in this class
    if (settings.containsKey("NO_OF_CORES"))
      this.noOfCores = Integer.parseInt(settings.get("NO_OF_CORES"));
    if (settings.containsKey("GARBAGE_COLLECTION"))
      this.gc = Boolean.parseBoolean(settings.get("GARBAGE_COLLECTION"));
    if (settings.containsKey("VERBOSE"));
      this.verbose = Boolean.parseBoolean(settings.get("VERBOSE"));
    if (settings.containsKey("RDF_CHECK"))
      this.rdfCheck = Boolean.parseBoolean(settings.get("RDF_CHECK"));
    if (settings.containsKey("EQUIVALENCE_REDUCTION"))
      this.equivalenceClassReduction = Boolean.parseBoolean(settings.get("EQUIVALENCE_REDUCTION"));
    if (settings.containsKey("CLEAN_UP_REPOSITORY"))
      this.cleanUpRepository = Boolean.parseBoolean(settings.get("CLEAN_UP_REPOSITORY"));
    if (settings.containsKey("NO_OF_ITERATIONS"))
      this.noOfIterations = Integer.parseInt(settings.get("NO_OF_ITERATIONS"));
    if (settings.containsKey("MIN_NO_OF_ARGS"))
      this.minNoOfArgs = Integer.parseInt(settings.get("MIN_NO_OF_ARGS"));
    if (settings.containsKey("MAX_NO_OF_ARGS"))
      this.maxNoOfArgs = Integer.parseInt(settings.get("MAX_NO_OF_ARGS"));
    if (settings.containsKey("SUBJECT_POSITION"))
      this.subjectPosition = Integer.parseInt(settings.get("SUBJECT_POSITION"));
    if (settings.containsKey("PREDICATE_POSITION"))
      this.predicatePosition = Integer.parseInt(settings.get("PREDICATE_POSITION"));
    if (settings.containsKey("OBJECT_POSITION"))
      this.objectPosition = Integer.parseInt(settings.get("OBJECT_POSITION"));
    if (settings.containsKey("NO_OF_ATOMS"))
      this.noOfAtoms = Integer.parseInt(settings.get("NO_OF_ATOMS"));
    if (settings.containsKey("NO_OF_TUPLES"))
      this.noOfTuples = Integer.parseInt(settings.get("NO_OF_TUPLES"));
    if (settings.containsKey("CHAR_ENCODING"))
      this.characterEncoding = settings.get("CHAR_ENCODING");
    if (settings.containsKey("PERSISTENCY_FILE"))
      this.persistencyFile = settings.get("PERSISTENCY_FILE");
    // as _namespace is already bound in the constructor, set "verbose" to the right value, if available
    _namespace.verbose = this.verbose;
    // as _tupleStore is already bound in the constructor, set all the public fields of the tuple store
    _tupleStore.verbose = this.verbose;
    _tupleStore.rdfCheck = this.rdfCheck;
    _tupleStore.equivalenceClassReduction = this.equivalenceClassReduction;
    _tupleStore.minNoOfArgs = this.minNoOfArgs;
    _tupleStore.maxNoOfArgs = this.maxNoOfArgs;
    _tupleStore.subjectPosition = this.subjectPosition;
    _tupleStore.predicatePosition = this.predicatePosition;
    _tupleStore.objectPosition = this.objectPosition;
    _tupleStore.noOfAtoms = this.noOfAtoms;
    _tupleStore.noOfTuples = this.noOfTuples;
    _tupleStore.inputCharacterEncoding = this.characterEncoding;
    _tupleStore.outputCharacterEncoding = this.characterEncoding;
  }

  /**
   *
   * @param nameSpaceReader
   * @throws WrongFormatException
   * @throws IOException
   */
  public void readNamespaces(BufferedReader nameSpaceReader)
      throws WrongFormatException, IOException {
    _namespace.readNamespaces(nameSpaceReader);
  }

  public void readNamespaces(File namespace)
      throws WrongFormatException, IOException {
    readNamespaces(Files.newBufferedReader(namespace.toPath(),
        Charset.forName(_tupleStore.inputCharacterEncoding)));
  }

  public void addNamespace(String shortForm, String longForm) {
    _namespace.putForm(shortForm, longForm);
  }

  public void readTuples(BufferedReader tupleReader)
      throws WrongFormatException, IOException {
    _tupleStore.readTuples(tupleReader);
  }

  public void readTuples(BufferedReader tupleReader, BufferedReader nameSpaceReader)
      throws WrongFormatException, IOException {
    _namespace.readNamespaces(nameSpaceReader);
    _tupleStore.readTuples(tupleReader);
  }

  public void readTuples(File tuples, File namespace)
      throws WrongFormatException, IOException {
    readTuples(Files.newBufferedReader(tuples.toPath(),
        Charset.forName(_tupleStore.inputCharacterEncoding)),
        Files.newBufferedReader(namespace.toPath(),
            Charset.forName(_tupleStore.inputCharacterEncoding)));
  }

  public void readTuples(File tuples)
      throws WrongFormatException, IOException {
    readTuples(Files.newBufferedReader(tuples.toPath(),
        Charset.forName(_tupleStore.inputCharacterEncoding)));
  }

  String myNormalizeNamespaces(String s) {
    switch (s.charAt(0)) {
    case '<' :
      return '<'
          + _namespace.normalizeNamespaceUri(
              s.substring(1, s.length() - 1))
          + '>';
    case '"' :
      // Atom, possibly with long xsd type spec
      int pos = s.lastIndexOf('^');
      if (pos > 0 && s.charAt(pos - 1) == '^') {
        return s.substring(0, pos + 2)
            + _namespace.normalizeNamespaceUri(s.substring(pos + 2, s.length() - 1))
            + '>';
      }
    }
    return s;
  }

  /** Normalize namespaces, and get ids directly to put in the tuples without
   *  using the hfc internal functions.
   *
   *  This is done so i can add the <it>now<it/> time stamp transparently
   *  TODO: refactor, and make this part of HFC core
   */
  public int addTuples(List<List<String>> rows, long timeStamp) {
    int time = -1;
    int timeslot = 0;
    if (timeStamp >= 0) {
      time = _tupleStore.putObject(
          new XsdLong(System.currentTimeMillis()).toString());
      timeslot = 1;
    }
    int result = 0;
    for (List<String> row : rows) {
      // TODO: LONG TO SHORT MAPPINGS MUST BE DONE HERE BECAUSE THE ADDTUPLES
      // METHODS DON'T DO THAT, WHICH SIMPLY ISN'T RIGHT IF THEY ARE PUBLIC!
      int[] tuple = new int[row.size() + timeslot];
      int i = 0;
      for (String s : row) {
        tuple[i] = _tupleStore.putObject(myNormalizeNamespaces(s));
        ++i;
      }
      if (time >= 0) {
        tuple[i] = time;
      }
      if (_tupleStore.addTuple(tuple)) {
        ++result;
      }
    }
    return result;
  }

  /**
   *
   * @param rules
   * @throws IOException
   */
  public void readRules(File rules) throws IOException {
    // in case no rule store has been defined so far, create one, but also
    // create a forward chainer, as rules alone are useless
    if (_ruleStore == null) {
      // sexternary constructor would suffice here
      _ruleStore = new RuleStore(_tupleStore);
      // customize rule store settings
      _ruleStore.minNoOfArgs = this.minNoOfArgs;
      _ruleStore.maxNoOfArgs = this.maxNoOfArgs;
      _ruleStore.verbose = this.verbose;
      _ruleStore.rdfCheck = this.rdfCheck;
      // after defining the approapriate settings, load the _first_ rule file
      _ruleStore.readRules(rules.getAbsolutePath());
      // create forward chainer
      _forwardChainer = new ForwardChainer(_tupleStore, _ruleStore);
      // customize forward chainer
      _forwardChainer.gc = this.gc;
      _forwardChainer.noOfCores = this.noOfCores;
      _forwardChainer.noOfIterations = this.noOfIterations;
      _forwardChainer.cleanUpRepository = this.cleanUpRepository;
      _forwardChainer.verbose = this.verbose;
    }
    else {
      // value of noOfTask in the forward chainer needs to be adapted every time
      // new rules are read in !!
      _ruleStore.readRules(rules.getAbsolutePath());
      _forwardChainer.noOfTasks = _ruleStore.allRules.size();
    }
  }

  public BindingTable executeQuery(String query) throws QueryParseException {
    Query q = new Query(_tupleStore);
    // do we want to generate a new Query object for each call of query() (is not needed)?
    // yes, as executeQuery is used in hfc-db by class HfcDbHandler in method selectQuery();
    // otherwise, we would need to synchronize a class-based Query object here
    BindingTable bt = q.query(query);
    return bt;
  }

  public void computeClosure() {
    if (null != _forwardChainer) {
      _forwardChainer.computeClosure();
    }
  }

}
