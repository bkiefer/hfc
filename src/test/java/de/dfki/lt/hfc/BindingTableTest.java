package de.dfki.lt.hfc;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import org.junit.Test;

import de.dfki.lt.hfc.io.QueryParseException;
import gnu.trove.set.hash.TCustomHashSet;

public class BindingTableTest {

  private TupleStore getTS() {
    try {
      return TestingUtils.getOperatorTestStore();
    } catch (Exception ex) {
      throw new RuntimeException(ex);
    }
  }

  @Test
  public void testBindingTable() {
    // test constructor BindingTable()
    BindingTable bt = new BindingTable();
    assertNotNull(bt);
  }

  @Test
  public void testBindingTable1() {
    // test constructor BindingTable(Set<int[]> table)
    Set<int[]> table;
    table = new TCustomHashSet<int[]>(TupleStore.DEFAULT_HASHING_STRATEGY);
    BindingTable bt1 = new BindingTable(table);
    assertNotNull(bt1);
  }

  @Test
  public void testBindingTable2() {
    // test constructor BindingTable(TupleStore tupleStore)
    TupleStore ts = getTS();
    BindingTable bt2 = new BindingTable(ts);
    assertNotNull(bt2);
  }

  @Test
  public void testBindingTable3() {
    // test constructor BindingTable(Set<int[]> table, SortedMap<Integer,
    // Integer> nameToPos)
    Set<int[]> table0;
    table0 = new TCustomHashSet<int[]>(TupleStore.DEFAULT_HASHING_STRATEGY);
    SortedMap<Integer, Integer> nameToPos = new TreeMap<Integer, Integer>();
    BindingTable bt3 = new BindingTable(table0, nameToPos);
    assertNotNull(bt3);
  }

  @Test
  public void testBindingTable4() {
    // test constructor BindingTable(Set<int[]> table, SortedMap<Integer,
    // Integer> nameToPos, Map<Integer, String> nameToExternalName)
    Set<int[]> table1 = new TCustomHashSet<int[]>(
        TupleStore.DEFAULT_HASHING_STRATEGY);
    SortedMap<Integer, Integer> nameToPos1 = new TreeMap<Integer, Integer>();
    Map<Integer, String> nameToExternalName = new TreeMap<Integer, String>();
    BindingTable bt4 = new BindingTable(table1, nameToPos1, nameToExternalName);
    assertNotNull(bt4);
  }

  @Test
  public void testBindingTable5() {
    /*
     * test constructor BindingTable(Set<int[]> table, SortedMap<Integer,
     * Integer> nameToPos, Map<Integer, String> nameToExternalName, TupleStore
     * tupleStore)
     */
    Set<int[]> table2 = new TCustomHashSet<int[]>(
        TupleStore.DEFAULT_HASHING_STRATEGY);
    SortedMap<Integer, Integer> nameToPos2 = new TreeMap<Integer, Integer>();
    Map<Integer, String> nameToExternalName2 = new TreeMap<Integer, String>();
    TupleStore ts = getTS();
    BindingTable bt5 = new BindingTable(table2, nameToPos2, nameToExternalName2,
        ts);
    assertNotNull(bt5);
  }

  @Test
  public void testBindingTable6() {
    /*
     * test constructor BindingTable(Set<int[]> table, SortedMap<Integer,
     * Integer> nameToPos, Map<Integer, String> nameToExternalName, TupleStore
     * tupleStore, int[] arguments, HashMap<Integer, ArrayList<Integer>>
     * relIdToFunIds, HashMap<String, Integer> varToId)
     */
    Set<int[]> table3 = new TCustomHashSet<int[]>(
        TupleStore.DEFAULT_HASHING_STRATEGY);
    SortedMap<Integer, Integer> nameToPos3 = new TreeMap<Integer, Integer>();
    Map<Integer, String> nameToExternalName3 = new TreeMap<Integer, String>();
    TupleStore ts3 = getTS();
    int[] arguments = new int[2];
    HashMap<Integer, ArrayList<Integer>> relIdToFunIds = new HashMap<Integer, ArrayList<Integer>>();
    HashMap<String, Integer> varToId = new HashMap<String, Integer>();
    BindingTable bt6 = new BindingTable(table3, nameToPos3, nameToExternalName3,
        ts3, arguments, relIdToFunIds, varToId);
    assertNotNull(bt6);
  }

  @Test
  public void testBindingTable7() {
    /*
     * test constructor BindingTable(SortedMap<Integer, Integer> nameToPos,
     * Map<Integer, String> nameToExternalName, TupleStore tupleStore)
     */
    SortedMap<Integer, Integer> nameToPos4 = new TreeMap<Integer, Integer>();
    Map<Integer, String> nameToExternalName4 = new TreeMap<Integer, String>();
    TupleStore ts4 = getTS();
    BindingTable bt7 = new BindingTable(nameToPos4, nameToExternalName4, ts4);
    assertNotNull(bt7);
  }

  @Test
  public void testBindingTable8() {
    /*
     * test copy constructor protected BindingTable(BindingTable bt, TupleStore
     * ts)
     */
    BindingTable bt = new BindingTable();
    TupleStore ts = getTS();
    BindingTable bt8 = new BindingTable(bt, ts);
    assertNotNull(bt8);
  }

  @Test
  public void testobtainPosition() {
    // test method testobtainPosition(String externalName)
    // test for case where the external name is not a valid table heading, -1 is
    // returned
    String externalName = "extname";
    SortedMap<Integer, Integer> nameToPos4 = new TreeMap<Integer, Integer>();
    Map<Integer, String> nameToExternalName4 = new TreeMap<Integer, String>();
    TupleStore ts4 = getTS();
    BindingTable objfortest = new BindingTable(nameToPos4, nameToExternalName4,
        ts4);
    assertEquals(objfortest.obtainPosition(externalName), -1);
    // test for case where the external name is a valid table heading
    String ext_name = "value";
    nameToPos4.put(2, 2);
    nameToExternalName4.put(2, "value");
    BindingTable objectwithvalue = new BindingTable(nameToPos4,
        nameToExternalName4, ts4);
    assertEquals(objectwithvalue.obtainPosition(ext_name), 2);
    // test for case where (elem.getValue().equals(externalName)) is false, -1
    // is returned
    String extname = "gjfjgdfjg";
    nameToPos4.put(1, 1);
    nameToExternalName4.put(1, "fsf");
    BindingTable objectfalse = new BindingTable(nameToPos4, nameToExternalName4,
        ts4);
    assertEquals(objectfalse.obtainPosition(extname), -1);
  }

  @Test
  public void testtoString() {
    // test method toString()
    // nameToPos is empty
    Set<int[]> table0 = new TCustomHashSet<int[]>(
        TupleStore.DEFAULT_HASHING_STRATEGY);
    SortedMap<Integer, Integer> nameToPos = new TreeMap<Integer, Integer>();
    BindingTable bt = new BindingTable(table0, nameToPos);
    assertEquals(bt.toString().substring(0, 1), "=");
    // nameToPos is full
    nameToPos.put(1, 1);
    BindingTable bt000 = new BindingTable(table0, nameToPos);
    assertEquals(bt000.toString().substring(0, 3), "===");
    //
  }

  @Test
  public void testtoString1() {
    // test method toString(boolean expand)
    Set<int[]> table;
    table = new TCustomHashSet<int[]>(TupleStore.DEFAULT_HASHING_STRATEGY);
    SortedMap<Integer, Integer> nameToPos = new TreeMap<Integer, Integer>();
    nameToPos.put(1, 1);
    Map<Integer, String> nameToExternalName = new TreeMap<Integer, String>();
    nameToExternalName.put(1, "value");
    TupleStore ts = getTS();
    BindingTable bt = new BindingTable(table, nameToPos, nameToExternalName,
        ts);
    assertEquals(bt.toString(true).substring(6, 11), "value");
    assertEquals(bt.toString(false).substring(6, 11), "value");
  }

  @Test
  public void testtoString2() {
    // test method toString(int maxLength, boolean expand)
    Set<int[]> table = new TCustomHashSet<int[]>(
        TupleStore.DEFAULT_HASHING_STRATEGY);
    SortedMap<Integer, Integer> nameToPos = new TreeMap<Integer, Integer>();
    nameToPos.put(1, 1);
    Map<Integer, String> nameToExternalName = new TreeMap<Integer, String>();
    nameToExternalName.put(1, "val");
    TupleStore ts = getTS();
    BindingTable bt = new BindingTable(table, nameToPos, nameToExternalName,
        ts);
    assertEquals(bt.toString(3, false).substring(10, 13), "val");
    assertEquals(bt.toString(2, false).substring(9, 12), "val");
    assertEquals(bt.toString(3, true).substring(10, 13), "val");
    assertEquals(bt.toString(0, true).substring(7, 10), "val");
    assertEquals(bt.toString(5, false).substring(12, 15), "val");
    //
    TupleStore objfortest = getTS();
    int[] tuple = new int[3];
    tuple[0] = 2;
    tuple[1] = 2;
    tuple[2] = 2;
    objfortest.addToIndex(tuple);
  }

  @Test
  public void testVars() throws QueryParseException {
    TupleStore ts = getTS();
    Query q = new Query(ts);
    BindingTable bt = q.query("SELECT ?s ?p ?o WHERE ?s ?p ?o");
    String[] vars = bt.getVars();
    String[] expected = { "?s", "?p", "?o" };
    assertArrayEquals(expected, vars);
  }

  @Test
  public void testVarsStar() throws QueryParseException {
    TupleStore ts = getTS();
    Query q = new Query(ts);
    BindingTable bt = q.query("SELECT * WHERE ?s ?p ?o");
    String[] vars = bt.getVars();
    String[] expected = { "?s", "?p", "?o" };
    Set<String> s = new HashSet<String>();
    s.addAll(Arrays.asList(expected));
    assertTrue(s.containsAll(Arrays.asList(vars)));
    assertEquals(Arrays.toString(expected), expected.length, vars.length);
    /*
     * THIS IS NOT GUARANTEED, BY NO MEANS, THE TABLE CAN BE WIDER THAN THE
     * NUMBER OF VARIABLES TODO: MARK THIS SOMEWHERE for (int i = 0; i <
     * expected.length; ++i) { assertEquals(i, bt.obtainPosition(expected[i]));
     * }
     */
  }

  @Test
  public void testVarsFilterStar() throws QueryParseException {
    TupleStore ts = getTS();
    Query q = new Query(ts);
    String[] t = { "<rdf:a>", "<rdf:type>", "<rdf:b>" };
    ts.addTuple(t);
    BindingTable bt = q
        .query("SELECT * WHERE ?s <rdf:type> ?o FILTER ?o != <rdf:b>");
    String[] vars = bt.getVars();
    String[] expected = { "?s", "?o" };
    Set<String> s = new HashSet<String>();
    s.addAll(Arrays.asList(expected));
    assertTrue(s.containsAll(Arrays.asList(vars)));
    assertEquals(Arrays.toString(expected), expected.length, vars.length);
    /*
     * THIS IS NOT GUARANTEED, BY NO MEANS, THE TABLE CAN BE WIDER THAN THE
     * NUMBER OF VARIABLES for (int i = 0; i < expected.length; ++i) {
     * assertEquals(i, bt.obtainPosition(expected[i])); }
     */
  }

  @Test
  public void testVarsFilter() throws QueryParseException {
    TupleStore ts = getTS();
    Query q = new Query(ts);
    String[] t = { "<rdf:a>", "<rdf:type>", "<rdf:b>" };
    ts.addTuple(t);
    BindingTable bt = q
        .query("SELECT ?s ?o WHERE ?s <rdf:type> ?o FILTER ?o != <rdf:b>");
    String[] vars = bt.getVars();
    String[] expected = { "?s", "?o" };
    Set<String> s = new HashSet<String>();
    s.addAll(Arrays.asList(expected));
    assertTrue(s.containsAll(Arrays.asList(vars)));
    assertEquals(Arrays.toString(expected), expected.length, vars.length);
    /*
     * THIS IS NOT GUARANTEED, BY NO MEANS, THE TABLE CAN BE WIDER THAN THE
     * NUMBER OF VARIABLES for (int i = 0; i < expected.length; ++i) {
     * assertEquals(i, bt.obtainPosition(expected[i])); }
     */
  }

  @Test
  public void testVarsAggregate() throws QueryParseException {
    TupleStore ts = getTS();
    Query q = new Query(ts);
    String[] t = { "<rdf:a>", "<rdf:type>", "<rdf:b>" };
    ts.addTuple(t);
    BindingTable bt = q.query(
        "SELECT ?p WHERE ?s ?p ?o AGGREGATE ?number = CountDistinct ?p & ?subject = Identity ?p");
    String[] vars = bt.getVars();
    // TODO: I'm convinced that the order of variables of the aggregates is
    // not guaranteed because the code has not been adapted, like it was done
    // for the projectedVars. This test succeeds, but this is by no means
    // ensuring that it works in all situations
    String[] expected = { "?number", "?subject" };
    Set<String> s = new HashSet<String>();
    s.addAll(Arrays.asList(expected));
    assertTrue(Arrays.toString(expected), s.containsAll(Arrays.asList(vars)));
    assertEquals(Arrays.toString(expected), expected.length, vars.length);
    /*
     * THIS IS NOT GUARANTEED, BY NO MEANS, THE TABLE CAN BE WIDER THAN THE
     * NUMBER OF VARIABLES for (int i = 0; i < expected.length; ++i) {
     * assertEquals(i, bt.obtainPosition(expected[i])); }
     */
  }

  @Test
  public void testVarsAggregateStar() throws QueryParseException {
    TupleStore ts = getTS();
    Query q = new Query(ts);
    String[] t = { "<rdf:a>", "<rdf:type>", "<rdf:b>" };
    ts.addTuple(t);
    BindingTable bt = q.query(
        "SELECT * WHERE ?s ?p ?o AGGREGATE ?number = CountDistinct ?p & ?subject = Identity ?p");
    String[] vars = bt.getVars();
    String[] expected = { "?number", "?subject" };
    Set<String> s = new HashSet<String>();
    s.addAll(Arrays.asList(expected));
    assertTrue(Arrays.toString(expected), s.containsAll(Arrays.asList(vars)));
    assertEquals(Arrays.toString(expected), expected.length, vars.length);
    /*
     * THIS IS NOT GUARANTEED, BY NO MEANS, THE TABLE CAN BE WIDER THAN THE
     * NUMBER OF VARIABLES for (int i = 0; i < expected.length; ++i) {
     * assertEquals(i, bt.obtainPosition(expected[i])); }
     */
  }

}
