package de.dfki.lt.hfc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import de.dfki.lt.hfc.BindingTable.BindingTableIterator;
import de.dfki.lt.hfc.types.AnyType;

public class TestingUtils {
  private static final File testResourceDir = new File("src/test/data/");
  private static final File tmpDir = new File(System.getProperty("java.io.tmpdir"));

  public static String getTestResource(String subdir, String name) {
    return new File(new File(testResourceDir, subdir), name).getPath();
  }

  public static String getTestResource(String name) {
    return new File(testResourceDir, name).getPath();
  }

  public static String getTempFile(String name) {
    //
    return new File(tmpDir, name).getPath();
  }

  private static String [] neutralizeBlanknodes(String[] in) {
    in = Arrays.copyOf(in, in.length);
    for (int i = 0; i < in.length; ++i) {
      String s = in[i];
      if (s.startsWith("_:")) {
        in[i] = "_:";
      }
    }
    return in;
  }


  /** Check if expected contains all the rows that are in the BindingTable,
   *  even if there are duplicates.
   *
   *  CAVEAT: all blank nodes are reduced to "_:", so if you do structural
   *  tests involving blank nodes, you can NOT use this function.
   */
  private static void checkResult(String[][] expected,
      BindingTable bt, String ... vars) {
    if (vars.length == 0)
      throw new IllegalArgumentException("Variables list to test may not be empty!");
    try {
      // to make sure there can be multiple equal rows, we erase all rows
      // one by one from the List, which must be empty in the end
      List<String[]> exp = new ArrayList<String[]>(Arrays.asList(expected));
      BindingTableIterator bindIt = bt.iterator(vars);
      assertEquals(expected.length, bindIt.hasSize());

      while (bindIt.hasNext()) {
        String[] tuple = neutralizeBlanknodes(bindIt.nextAsString());
        // make sure all tuples from expected are covered: find the right one
        boolean found = false;
        for(Iterator<String[]> expIt = exp.iterator(); expIt.hasNext();) {
          String[] expectedRow = neutralizeBlanknodes(expIt.next());
          // This does not go into the elements of an array, will only return
          // true if tuple == other, which is never the case
          // if (tuple.equals(other)) {
          if (Arrays.equals(tuple, expectedRow)) {
            expIt.remove();
            found = true;
            break; // take the next row
          }
        }
        assertTrue("Row not found "+Arrays.toString(tuple), found);
      }
      assertTrue("Not all expected rows found ", exp.isEmpty());
    }
    catch (BindingTableIteratorException e) {
      throw new RuntimeException(e); // should never happen
    }
  }

  public interface NextCall<T> {
    public T[] next(BindingTableIterator it);
  }

  public static class NextAsStringCall implements NextCall<String> {
    @Override
    public String[] next(BindingTableIterator it) {
      return it.nextAsString();
    }
  }

  public static class NextAsHfcCall implements NextCall<String> {
    @Override
    public String[] next(BindingTableIterator it) {
      AnyType[] in = it.nextAsHfcType();
      String[] res = new String[in.length];
      int i = 0;
      for (AnyType a : in) res[i++] = a.toName();
      return res;
    }
  }

  public static class NextAsObjectCall implements NextCall<Object> {
    @Override
    public Object[] next(BindingTableIterator it) {
      Object[] in = it.nextAsJavaObject();
      Object[] res = new Object[in.length];
      int i = 0;
      for (Object a : in)
        res[i++] = ((a instanceof AnyType) ? ((AnyType)a).toName() : a);
      return res;
    }
  }

  public static class NextAsIntCall implements NextCall<String> {
    private TupleStore _ts;

    public NextAsIntCall(TupleStore ts){
      _ts = ts;
    }

    @Override
    public String[] next(BindingTableIterator it) {
      int[] in = it.next();
      String[] res = new String[in.length];
      int i = 0;
      for (int sym : in) {
        String name = _ts.getObject(sym).toString();
        res[i++] = name;
      }
      return res;
    }
  }

  public static <T> void printNext(BindingTableIterator it, NextCall<T> nc) {
    while (it.hasNext()) {
      T[] next = nc.next(it);
      System.out.print("{ \"");
      for (int i = 0; i < next.length; i++) {
        System.out.print(next[i].toString() + "\"");
        if (i < next.length - 1) {
          System.out.print(", \"");
        }
      }

    }
  }

  static Comparator<int[]> lexSortComp = new Comparator<int[]>() {
    @Override
    public int compare(int[] o1, int[] o2) {
      for (int i = 0; i < o1.length; ++i) {
        int res = o2[i] - o1[i];
        if (res != 0)
          return res;
      }
      return 0;
    }
  };

  /** Neutralize all blank node ids to -1.
   *  TODO: We might want to be more clever to do structural tests with blank
   *  nodes.
   * @param hfc
   * @param tuple
   */
  public static int[] neutralizeBlanks(Hfc hfc, int[] tuple) {
    for (int i = 0 ; i < tuple.length; ++i) {
      if (hfc._tupleStore.isBlankNode(tuple[i])) {
        tuple[i] = -1;
      }
    }
    return tuple;
  }

  public static int[][] internalize(Hfc hfc, String[][] expected,
      boolean neutralizeBlanks) {
    int[][] expInternal = new int[expected.length][];
    int i = 0;
    for (String[] row : expected) {
      int[] irow = expInternal[i] = new int[row.length];
      for (int j = 0; j < row.length; ++j) {
        irow[j] = hfc._tupleStore.putObject(row[j]);
        if (neutralizeBlanks && hfc._tupleStore.isBlankNode(irow[j])) {
          irow[j] = -1;
        }
      }
      ++i;
    }
    return expInternal;
  }

  public static <T> void checkInt(Hfc hfc, BindingTableIterator it, int[][] expected) {
    List<int[]> elist = new ArrayList<int[]>(expected.length);
    elist.addAll(Arrays.asList(expected));
    elist.sort(lexSortComp);
    while (it.hasNext()) {
      int[] next = neutralizeBlanks(hfc, it.next());
      boolean found = false;
      int candidate = Collections.binarySearch(elist, next, lexSortComp);
      if (candidate >= 0) {
        elist.remove(candidate);
        found = true;
      }
      assertTrue(Arrays.toString(next) + " " + elist.size(), found);
    }

    assertTrue("Remaining: " + elist.size(), elist.isEmpty());
  }

  public static <T> void check(BindingTableIterator it, T[][] expected,
      NextCall<T> nc) {
    List<T[]> ee = new ArrayList<T[]>(Arrays.asList(expected));

    while (it.hasNext()) {
      T[]next = nc.next(it);
      boolean found = false;
      for (Iterator<T[]> eit = ee.iterator(); eit.hasNext();) {
        if (Arrays.equals(next, eit.next())) {
          eit.remove();
          found = true;
          break;
        }
      }
      assertTrue(Arrays.toString(next) + " " + ee.size(), found);
    }

    assertTrue("Remaining: " + ee.size(), ee.isEmpty());
  }

  public static void printExpected(BindingTable bt, TupleStore store) {
    printNext(bt.iterator(), new NextAsIntCall(store));
  }

  public static void checkDoubleResult(Hfc hfc, BindingTable bt, Double expected) {
    assertTrue(bt.iterator().hasNext());
    int[] row = bt.iterator().next();
    Double actual = (Double)hfc._tupleStore.getObject(row[0]).toJava();
    assertEquals(expected, actual, 1E-5);
  }

  public static void checkResult(Hfc hfc, BindingTable bt, String[][] expected,
      String ... vars) {
    BindingTableIterator bindIt = null;
    try {
      bindIt = bt.iterator(vars);
    } catch (BindingTableIteratorException e) {
      e.printStackTrace();
      assertTrue("Vars do not match table", false);
    }
    // Turn the expected array into an array of int arrays
    assertEquals(expected.length, bindIt.hasSize());
    int[][] expInternal = internalize(hfc, expected, true);
    checkInt(hfc, bindIt, expInternal);
  }

  public static<T> T[] reverse(T[] in) {
    for (int i = 0, j = in.length - 1; i < in.length >> 1; ++i, --j) {
      T help = in[j];
      in[j] = in[i];
      in[i] = help;
    }
    return in;
  }

  public static int[] reverse(int[] in) {
    for (int i = 0, j = in.length - 1; i < in.length / 2; ++i, --j) {
      int help = in[j];
      in[j] = in[i];
      in[i] = help;
    }
    return in;
  }

  public static String expstring(int[]exp, int[]in) {
    return "Expected " + Arrays.toString(exp) + " but was " + Arrays.toString(in);
  }

  private static String conf = "verbose: false\n"
        + "characterEncoding: UTF-8\n"
        + "noOfCores: 1\n"
        + "noOfTuples: 500000\n"
        + "noOfAtoms: 100000\n"
        + "eqReduction: false\n"
        + "garbageCollection: false\n"
        + "cleanUpRepository: true\n"
        + "shortIsDefault: true\n"
        + "namespaces:\n"
        + "  xsd:  http://www.w3.org/2001/XMLSchema#\n"
        + "  rdf:  http://www.w3.org/1999/02/22-rdf-syntax-ns#\n"
        + "  rdfs:  http://www.w3.org/2000/01/rdf-schema#\n"
        + "  owl:  http://www.w3.org/2002/07/owl#\n"
        + "  test: http://www.dfki.de/lt/onto/test.owl#\n"
        + "  hfc: http://www.dfki.de/lt/hfc.owl#\n"
        + "tupleFiles:\n"
        + "- default.nt\n"
        + "minArgs: 2\n"
        + "maxArgs: 5\n"
        + "subjectPosition: 0\n"
        + "predicatePosition: 1\n"
        + "objectPosition: 2\n"
        + "rdfCheck: true\n"
        + "exitOnError: true\n"
        + "ruleFiles:\n"
        + "- default.rdl\n"
        + "iterations: 2147483647";

  public static TestConfig getOperatorConfig() {
    return TestConfig.getInstance(new ByteArrayInputStream(conf.getBytes()));
  }

  public static TupleStore getOperatorTestStore() throws IOException, WrongFormatException {
    return getStore(getOperatorConfig());
  }

  private static String changeKeyVal(String in, String key, String val) {
    return conf.replaceFirst(key + ": ([^\n]*)\n", key + ": " + val + "\n");
  }

  public static Config getNoRdfCheckConfig() {
    String c = changeKeyVal(conf, "rdfCheck", "false");
    return Config.getInstance(new ByteArrayInputStream(c.getBytes()), null);
  }

  public static TupleStore getNoRdfCheckTestStore() throws IOException, WrongFormatException {
    return getStore(getNoRdfCheckConfig());
  }

  public static TupleStore getDefaultStore() throws IOException, WrongFormatException {
    return getStore(Config.getDefaultConfig());
  }

  public static TupleStore getEmptyStore() throws IOException, WrongFormatException {
    TestConfig c = TestConfig.getDefaultConfig();
    c.put(Config.TUPLEFILES, new ArrayList<>());
    c.put(Config.RULEFILES, new ArrayList<>());
    return getStore(c);
  }

  public static TestHfc getEmptyHfc() throws IOException, WrongFormatException {
    TestConfig c = TestConfig.getDefaultConfig();
    c.put(Config.TUPLEFILES, new ArrayList<>());
    c.put(Config.RULEFILES, new ArrayList<>());
    return new TestHfc(c);
  }

  public static TupleStore getStore(Config cnf) throws IOException, WrongFormatException {
    TestHfc hfc = new TestHfc(cnf);
    return hfc._tupleStore;
  }

  public static RuleStore getRuleStore(Config cnf)
      throws IOException, WrongFormatException {
    TupleStore ts = getStore(cnf);
    return new RuleStore(ts);
  }

  public static RuleStore getRuleStoreConfig(boolean rdfCheck, int min, int max,
      String ruleFile) throws IOException, WrongFormatException {
    String c =
        changeKeyVal("maxArgs", Integer.toString(max),
            changeKeyVal("minArgs", Integer.toString(min),
                changeKeyVal("rdfCheck", rdfCheck ? "true" : "false", conf)));
    c = c.replace("<resources>/default.rdl", ruleFile);
    Config cnf = Config.getInstance(new ByteArrayInputStream(c.getBytes()), null);
    return getRuleStore(cnf);
  }

}
