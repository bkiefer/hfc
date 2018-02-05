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
   * the DEFAULT settings basically address the RDF triple case without equivalence class reduction
   */
  protected String characterEncoding = "UTF-8";
  protected boolean cleanUpRepository = true;
  protected boolean equivalenceClassReduction = false;
  protected boolean gc = false;
  protected int maxNoOfArgs = 3;
  protected int minNoOfArgs = 3;
  protected int noOfAtoms = 10000;
  protected int noOfCores = 2;
  protected int noOfIterations = Integer.MAX_VALUE;  // aprox. complete materialization
  protected int noOfTuples = 100000;
  protected int objectPosition = 2;
  protected String persistencyFile = "/tmp/tuples.nt";  // not used at the moment
  protected int predicatePosition = 1;
  protected boolean rdfCheck = true;
  protected boolean shortIsDefault = true;
  protected int subjectPosition = 0;
  protected boolean verbose = true;

  /**
   * Transaction time time stamp used by Hfc.readTuples(BufferedReader tupleReader)
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
    System.out.println("HFC settings: " + settings);
    // make the settings available via protected fields in this class;
    // alphabetical order:
    for (Map.Entry<String, String> pair : settings.entrySet()) {
      switch (pair.getKey()) {
        case "CHARACTER_ENCODING" :
          this.characterEncoding = pair.getValue();
          break;
        case "CLEAN_UP_REPOSITORY" :
          this.cleanUpRepository = Boolean.parseBoolean(pair.getValue());
          break;
        case "EQUIVALENCE_REDUCTION" :
          this.equivalenceClassReduction = Boolean.parseBoolean(pair.getValue());
          break;
        case "GARBAGE_COLLECTION" :
          this.gc = Boolean.parseBoolean(pair.getValue());
          break;
        case "MAX_NO_OF_ARGS" :
          this.maxNoOfArgs = Integer.parseInt(pair.getValue());
          break;
        case "MIN_NO_OF_ARGS" :
          this.minNoOfArgs = Integer.parseInt(pair.getValue());
          break;
        case "NO_OF_ATOMS" :
          this.noOfAtoms = Integer.parseInt(pair.getValue());
          break;
        case "NO_OF_CORES" :
          this.noOfCores = Integer.parseInt(pair.getValue());
          break;
        case "NO_OF_ITERATIONS" :
          this.noOfIterations = Integer.parseInt(pair.getValue());
          break;
        case "NO_OF_TUPLES" :
          this.noOfTuples = Integer.parseInt(pair.getValue());
          break;
        case "OBJECT_POSITION" :
          this.objectPosition = Integer.parseInt(pair.getValue());
          break;
        case "PERSISTENCY_FILE" :
          this.persistencyFile = pair.getValue();
          break;
        case "PREDICATE_POSITION" :
          this.predicatePosition = Integer.parseInt(pair.getValue());
          break;
        case "RDF_CHECK" :
          this.rdfCheck = Boolean.parseBoolean(pair.getValue());
          break;
        case "SHORT_IS_DEFAULT" :
          this.shortIsDefault = Boolean.parseBoolean(pair.getValue());
          break;
        case "SUBJECT_POSITION" :
          this.subjectPosition = Integer.parseInt(pair.getValue());
          break;
        case "VERBOSE" :
          this.verbose = Boolean.parseBoolean(pair.getValue());
          break;
        default :
          if (this.verbose)
            System.out.println("  unknown setting option: " + pair.getKey());
          break;
      }
    }
    // _namespace already bound in constructor
    _namespace.verbose = this.verbose;
    _namespace.shortIsDefault = this.shortIsDefault;
    // _tupleStore already bound in constructor
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
   *  using the hfc internal functions
   *
   * @param rows the table that contains the tuples to add to the storage
   * @param front the potentially-empty (== null) front element
   * @param backs arbitrary-many back elements (or an empty array)
   *
   *  This is done so i can add the <it>now<it/> time stamp transparently
   *  TODO: refactor, and make this part of HFC core
   */
  public int addTuples(List<List<String>> rows, String front, String... backs) {
    // normalize namespaces for front and backs
    int frontId = -1;    // Java wants an initial value
    if (front != null)
      frontId = _tupleStore.putObject(myNormalizeNamespaces(front));
    int[] backIds = new int[backs.length];
    if (backs.length != 0) {
      for (int i = 0; i < backs.length; ++i)
        backIds[i] = _tupleStore.putObject(myNormalizeNamespaces(backs[i]));
    }
    // (front == null) means _no_ front element
    final int frontLength = (front == null) ? 0 : 1;
    final int backLength = backs.length;
    int[] tuple;
    int noOfTuples = 0;
    // extend and add tuples, given by parameters rows, and front and backs
    for (List<String> row : rows) {
      // TODO: LONG TO SHORT MAPPINGS MUST BE DONE HERE BECAUSE THE ADDTUPLES
      // METHODS DON'T DO THAT, WHICH SIMPLY ISN'T RIGHT IF THEY ARE PUBLIC!
      tuple = new int[row.size() + frontLength + backLength];
      int i = 0;
      // front element
      if (front != null) {
        tuple[0] = frontId;
        i = 1;
      }
      // table row
      for (String s : row) {
        tuple[i++] = _tupleStore.putObject(myNormalizeNamespaces(s));
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
