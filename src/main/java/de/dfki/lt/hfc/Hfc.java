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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Hfc {

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
//  protected String characterEncoding = "UTF-8";
//  protected boolean cleanUpRepository = true;
//  protected boolean equivalenceClassReduction = false;
//  protected boolean gc = false;
//  protected int maxNoOfArgs = 4;
//  protected int minNoOfArgs = 3;
//  protected int noOfAtoms = 10000;
//  protected int noOfCores = 2;
//  protected int noOfIterations = Integer.MAX_VALUE;  // aprox. complete materialization
//  protected int noOfTuples = 100000;
//  protected int objectPosition = 2;
//  protected String persistencyFile = "/tmp/tuples.nt";  // not used at the moment
//  protected int predicatePosition = 1;
//  protected boolean rdfCheck = true;
//  protected boolean shortIsDefault = true;
//  protected int subjectPosition = 0;
//  protected boolean verbose = false;
  protected Config config;
  private RuleStore _ruleStore = null;
  private ForwardChainer _forwardChainer = null;

  /**
   * the nullary constructor allocates the minimal object configuration:
   * a namespace and a tuple store are guaranteed to exist
   */
  public Hfc() {
    try {
      this.config = Config.getDefaultConfig();
      _tupleStore = new TupleStore(config.namespace);
    } catch (FileNotFoundException e) {
      logger.error("Was not able to load default configuration");
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void shutdown() {
    _forwardChainer.shutdownNoExit();
  }

  // customize HFC, i.e., namespace and tuple store via key-value pairs
  // from parameter settings which affect fields in these objects;
  // the fields for the rule store and forward chainer are assigned values
  // the first time rules are uploaded to HFC
  public void customizeHfc(Map<String, String> settings) {
    logger.info("HFC settings: " + settings);
    // make the settings available via protected fields in this class;
    // alphabetical order:
    this.config.updateConfig(settings);

    // _tupleStore already bound in constructor
    _tupleStore.verbose = config.verbose;
    _tupleStore.rdfCheck = config.rdfCheck;
    _tupleStore.equivalenceClassReduction = config.eqReduction;
    if (_tupleStore.equivalenceClassReduction) {
      _forwardChainer.config.cleanUpRepository = true;
    }
    _tupleStore.minNoOfArgs = config.minArgs;
    _tupleStore.maxNoOfArgs = config.maxArgs;
    _tupleStore.subjectPosition = config.subjectPosition;
    _tupleStore.predicatePosition = config.predicatePosition;
    _tupleStore.objectPosition = config.objectPosition;
    _tupleStore.noOfAtoms = config.noOfAtoms;
    _tupleStore.noOfTuples = config.noOfTuples;
    _tupleStore.inputCharacterEncoding = config.characterEncoding;
    _tupleStore.outputCharacterEncoding = config.characterEncoding;
  }

//  /**
//   *
//   * @param nameSpaceReader
//   * @throws WrongFormatException
//   * @throws IOException
//   */
//  public void readNamespaces(BufferedReader nameSpaceReader)
//      throws WrongFormatException, IOException {
//    _namespace.readNamespaces(nameSpaceReader);
//  }

//  public void readNamespaces(File namespace)
//      throws WrongFormatException, IOException {
//    readNamespaces(Files.newBufferedReader(namespace.toPath(),
//        Charset.forName(_tupleStore.inputCharacterEncoding)));
//  }

  public void addNamespace(String shortForm, String longForm) {
    config.putNamespace(shortForm, longForm);
  }

  public void readTuples(BufferedReader tupleReader)
          throws WrongFormatException, IOException {
    _tupleStore.readTuples(tupleReader);
  }

  public void readTuples(BufferedReader tupleReader, HashMap<String, String> namespaceMappings)
          throws WrongFormatException, IOException {
    for (Map.Entry<String, String> e : namespaceMappings.entrySet())
      config.putNamespace(e.getKey(), e.getValue());
    _tupleStore.readTuples(tupleReader);
  }

  public void readTuples(File tuples)
          throws WrongFormatException, IOException {
    readTuples(Files.newBufferedReader(tuples.toPath(),
            Charset.forName(_tupleStore.inputCharacterEncoding)));
  }


  public void readTuples(File tuples, long timestamp)
          throws WrongFormatException, IOException {
    _tupleStore.readTuples(Files.newBufferedReader(tuples.toPath(),
            Charset.forName(_tupleStore.inputCharacterEncoding)),
            null, new XsdLong(timestamp).toString());
  }


  String myNormalizeNamespaces(String s) {
    //TODO
    switch (s.charAt(0)) {
      case '<':
        return '<'
                + config.namespace.normalizeNamespaceUri(
                s.substring(1, s.length() - 1))
                + '>';
      case '"':
        // Atom, possibly with long xsd type spec
        int pos = s.lastIndexOf('^');
        if (pos > 0 && s.charAt(pos - 1) == '^') {
          return s.substring(0, pos + 2)
                  + config.namespace.normalizeNamespaceUri(s.substring(pos + 2, s.length() - 1))
                  + '>';
        }
    }
    return s;
  }

  private int getSymbolId(String symbol) {
    int id = _tupleStore.putObject(myNormalizeNamespaces(symbol));
    if (_tupleStore.equivalenceClassReduction) {
      id = _tupleStore.getProxy(id);
    }
    return id;
  }

  /**
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
      _ruleStore.minNoOfArgs = config.minArgs;
      _ruleStore.maxNoOfArgs = config.maxArgs;
      _ruleStore.verbose = config.verbose;
      _ruleStore.rdfCheck = config.rdfCheck;
      // after defining the approapriate settings, load the _first_ rule file
      _ruleStore.readRules(rules.getAbsolutePath());
      // create forward chainer
      _forwardChainer = new ForwardChainer(config, _tupleStore, _ruleStore);
    } else {
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
      _forwardChainer.config.cleanUpRepository = true;
      _forwardChainer.computeClosure();
    } else {
      _tupleStore.cleanUpTupleStore();
    }
  }

}
