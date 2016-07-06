package de.dfki.lt.hfc;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.List;
import java.util.HashMap;

import de.dfki.lt.hfc.types.XsdLong;

public class Hfc {

  /**
   * transaction time time stamp used by Hfc.readTuples(BufferedReader tupleReader)
   */
  public long timeStamp = 0L;

  private ForwardChainer _forwardChainer;

  private TupleStore _tupleStore;

  public Hfc() {
    _tupleStore = new TupleStore(new Namespace());
  }

  // customize HFC, i.e., namespace, tuple store, rule store, and forward chainer
  // via key-value pairs from parameter settings which affect fields in these objects
  public void customizeHfc(HashMap<String, String> settings) {

  }

  public void readNamespaces(BufferedReader nameSpaceReader)
      throws WrongFormatException, IOException {
    _tupleStore.namespace.readNamespaces(nameSpaceReader);
  }

  public void readNamespaces(File namespace)
      throws WrongFormatException, IOException {
    readNamespaces(Files.newBufferedReader(namespace.toPath(),
        Charset.forName(_tupleStore.inputCharacterEncoding)));
  }

  public void addNamespace(String shortForm, String longForm) {
    _tupleStore.namespace.putForm(shortForm, longForm);
  }

  /**
   * HUK: this method _now_ calls the binary readTuples() method
   * from class TupleStore in order to add a further transaction
   * time argument at the end of the original tuple;
   *
   * @param tupleReader
   * @throws WrongFormatException
   * @throws IOException
   */
  public void readTuples(BufferedReader tupleReader, long timeStamp)
      throws WrongFormatException, IOException {
    _tupleStore.readTuples(tupleReader, timeStamp);
  }

  public void readTuples(BufferedReader tupleReader, BufferedReader nameSpaceReader)
      throws WrongFormatException, IOException {
    _tupleStore.namespace.readNamespaces(nameSpaceReader);
    _tupleStore.readTuples(tupleReader);
  }

  public void readTuples(File tuples, File namespace)
      throws WrongFormatException, IOException {
    readTuples(Files.newBufferedReader(tuples.toPath(),
        Charset.forName(_tupleStore.inputCharacterEncoding)),
        Files.newBufferedReader(namespace.toPath(),
            Charset.forName(_tupleStore.inputCharacterEncoding)));
  }

  /**
   * HUK: added time stamp argument
   *
   * @param tuples
   * @throws WrongFormatException
   * @throws IOException
   */
  public void readTuples(File tuples, long timeStamp)
      throws WrongFormatException, IOException {
    readTuples(Files.newBufferedReader(tuples.toPath(),
        Charset.forName(_tupleStore.inputCharacterEncoding)),
            timeStamp);
  }

  String myNormalizeNamespaces(String s) {
    switch (s.charAt(0)) {
    case '<' :
      return '<'
          + _tupleStore.namespace.normalizeNamespaceUri(
              s.substring(1, s.length() - 1))
          + '>';
    case '"' :
      // Atom, possibly with long xsd type spec
      int pos = s.lastIndexOf('^');
      if (pos > 0 && s.charAt(pos - 1) == '^') {
        return s.substring(0, pos + 2)
            + _tupleStore.namespace.normalizeNamespaceUri(s.substring(pos + 2, s.length() - 1))
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

  public void readRules(File rules) throws IOException {
    if (_forwardChainer == null) {
      RuleStore store = new RuleStore(_tupleStore, rules.getAbsolutePath());
      _forwardChainer = new ForwardChainer(_tupleStore, store);
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
