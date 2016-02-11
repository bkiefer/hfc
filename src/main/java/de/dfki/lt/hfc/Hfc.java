package de.dfki.lt.hfc;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.List;

public class Hfc {
  private ForwardChainer _forwardChainer;

  private TupleStore _tupleStore;

  public Hfc() {
    _tupleStore = new TupleStore(new Namespace());
  }

  public void readNamespaces(BufferedReader nameSpaceReader)
      throws WrongFormatException, IOException {
    _tupleStore.namespace.readNamespaces(nameSpaceReader);
  }

  public void readNamespaces(File namespace)
      throws WrongFormatException, IOException {
    readNamespaces(Files.newBufferedReader(namespace.toPath(),
        Charset.forName(TupleStore.INPUT_CHARACTER_ENCODING)));
  }

  public void addNamespace(String shortForm, String longForm) {
    _tupleStore.namespace.putForm(shortForm, longForm);
  }

  public void readTuples(BufferedReader tupleReader)
      throws WrongFormatException, IOException {
    _tupleStore.readTuples(tupleReader);
  }

  public void readTuples(BufferedReader tupleReader, BufferedReader nameSpaceReader)
      throws WrongFormatException, IOException {
    _tupleStore.namespace.readNamespaces(nameSpaceReader);
    _tupleStore.readTuples(tupleReader);
  }

  public void readTuples(File tuples, File namespace)
      throws WrongFormatException, IOException {
    readTuples(Files.newBufferedReader(tuples.toPath(),
        Charset.forName(TupleStore.INPUT_CHARACTER_ENCODING)),
        Files.newBufferedReader(namespace.toPath(),
            Charset.forName(TupleStore.INPUT_CHARACTER_ENCODING)));
  }

  public void readTuples(File tuples)
      throws WrongFormatException, IOException {
    readTuples(Files.newBufferedReader(tuples.toPath(),
        Charset.forName(TupleStore.INPUT_CHARACTER_ENCODING)));
  }

  public int addTuples(List<List<String>> rows) throws WrongFormatException {
    int result = 0;
    int rowNo = 0;
    for (List<String> row : rows) {
      if (null != _tupleStore.addTuple(row, rowNo++)) {
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
    BindingTable bt = q.query(query);
    return bt;
  }

  public void computeClosure() {
    if (null != _forwardChainer) {
      _forwardChainer.computeClosure();
    }
  }

}
