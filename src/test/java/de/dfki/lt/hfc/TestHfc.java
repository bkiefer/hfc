package de.dfki.lt.hfc;

import java.io.IOException;
import java.util.Collection;

public class TestHfc extends Hfc {

  public TestHfc() throws IOException, WrongFormatException {
    this(Config.getDefaultConfig());
  }

  public TestHfc(Config c) throws IOException, WrongFormatException {
    super(c);
  }

  public TestHfc init(String res) throws IOException, WrongFormatException {
    uploadTuples(res);
    return this;
  }

  public static TestHfc getPalDomHfc(String res) throws IOException, WrongFormatException {
    TestHfc hfc = new TestHfc(TestConfig.getPalDomConfig());
    return hfc.init(res);
  }

  public IndexStore getIndex() {
    return _tupleStore.indexStore;
  }

  public boolean addTuples(Collection<int[]> tuples) {
    boolean changed = false;
    for (int[] tuple : tuples) changed |= _tupleStore.addTuple(tuple);
    return changed;
  }

  public Query getQuery() {
    return new Query(_tupleStore);
  }

  public Collection<int[]> getAllTuples() {
    return _tupleStore.getAllTuples();
  }

  public TupleStore getStore() {
    return _tupleStore;
  }

  public NamespaceManager getNSManager() {
    return _namespace;
  }

}
