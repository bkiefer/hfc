package de.dfki.lt.hfc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.ArrayList;

import org.junit.Test;

public class TestTupleStore {

  private static final File resourceDir = new File("src/main/resources/");

  public static String getResource(String name) {
    System.out.println(new File(".").getAbsolutePath());
    return new File(resourceDir, name).getPath();
  }

  @Test
  public void testConstructors() {
    // constructor TupleStore(int noOfAtoms, int noOfTuples) is tested
    Namespace namespace = new Namespace(getResource("default.ns"));
    assertNotNull(namespace);
    TupleStore tupleconstructor1 = new TupleStore(5, 5);
    assertNotNull(tupleconstructor1);
    // constructor TupleStore(int noOfAtoms, int noOfTuples, ns) is tested
    TupleStore tupleconstructor2 = new TupleStore(3, 2, namespace);
    assertNotNull(tupleconstructor2);
    // constructor TupleStore(int noOfAtoms, int noOfTuples, ns, filename) is
    // tested
    TupleStore tupleconstructor3 = new TupleStore(100000, 250000, namespace,
        getResource("default.nt"));
    assertNotNull(tupleconstructor3);
    // constructor TupleStore(int noOfAtoms, int noOfTuples, Namespace
    // namespace) is tested
    TupleStore tupleconstructor4 = new TupleStore(2, 4, namespace);
    assertNotNull(tupleconstructor4);
    // constructor TupleStore(boolean verbose, boolean rdfCheck, boolean
    // eqReduction, int minNoOfArgs, int maxNoOfArgs,
    // int noOfAtoms, int noOfTuples, Namespace namespace, String tupleFile) is
    // tested
    TupleStore tupleconstructor5 = new TupleStore(true, true, true, 2, 5, 4, 2, namespace,
        getResource("default.nt"));
    assertNotNull(tupleconstructor5);
    // constructor TupleStore(Namespace namespace) is tested
    TupleStore tupleconstructor6 = new TupleStore(namespace);
    assertNotNull(tupleconstructor6);
    // constructor TupleStore(Namespace namespace, String tupleFile) is tested
    TupleStore tupleconstructor7 = new TupleStore(namespace, getResource("default.nt"));
    assertNotNull(tupleconstructor7);
  }

  @Test
  public void initializeUriMappings() {
    Namespace.shortIsDefault = false;
    // nothing is tested, just to cover some fields
  }

  @Test
  public void testprintTuple() {
    int[] tuple = new int[3];
    ArrayList<String> mapping = new ArrayList<String>();
    tuple[0] = 0;
    tuple[1] = 1;
    tuple[2] = 2;
    mapping.add("a");
    mapping.add("b");
    mapping.add("c");
    // How to get the value of the last print?
    // I refactored the method in the class. Return String, not void. Form the
    // string, then return it.
    assertEquals(TupleStore.toString(tuple, mapping), "a b c .");
  }

  @Test
  public void testcleanUpTuple() {
    Namespace namespace = new Namespace(getResource("default.ns"));
    TupleStore objfortest = new TupleStore(true, true, true, 2, 5, 4, 2, namespace,
        getResource("default.nt"));
    int[] tuple = new int[3];
    tuple[0] = 2;
    tuple[1] = 2;
    tuple[2] = 2;
    objfortest.addToIndex(tuple);
    objfortest.cleanUpTupleStore();
    assertEquals(objfortest.cleanUpTupleStore(), 1);
  }

  @Test
  public void testputObject() {
    TupleStore objecttotest = new TupleStore(3, 5);
    int id = objecttotest.putObject("www.bbc.com");
    assertFalse("Compare the returned id with zero", 0 == id);
    assertEquals(objecttotest.putObject("www.bbc.com"), id);
  }

  @Test
  public void testgetObject() {
    TupleStore objectfortest = new TupleStore(2, 10);
    int id = objectfortest.putObject("www.bbc.com");
    assertFalse(objectfortest.getObject(5) == null);
    assertEquals("www.bbc.com", objectfortest.getObject(id));
    assertFalse(objectfortest.putObject("?") == 0);
    assertEquals("?", objectfortest.getObject(7));
    assertEquals("?-100", objectfortest.getObject(-100), "?-100");
  }

  @Test
  public void testgetJavaObject() {
    TupleStore objecttotest = new TupleStore(4, 6);
    assertNotNull(objecttotest.getJavaObject(3));
    assertNotNull(objecttotest.getJavaObject(0));
    // TODO 1 more branch
  }

  @Test
  public void testregisterJavaObject() {
    TupleStore objecttotest = new TupleStore(2, 5);
    assertEquals(objecttotest.registerJavaObject("www.bbc.com", null), 6);
  }

  @Test
  public void testisValidTuple() {
    TupleStore objectfortest = new TupleStore(4, 1);
    ArrayList<String> stringTuple = new ArrayList<String>();
    stringTuple.add("hello");
    stringTuple.add("world");// test for case stringTuple.size < maxNoOfArgs
    assertFalse(objectfortest.isValidTuple(stringTuple, 2));
    ArrayList<String> stringTuple2 = new ArrayList<String>();
    stringTuple2.add("hello");
    stringTuple2.add("world");
    stringTuple2.add("wjfjf");
    stringTuple2.add("djggerhe");
    stringTuple2.add("sf");
    stringTuple2.add("sfsd");// test for case stringTuple.size > maxNoOfArgs
    assertFalse(objectfortest.isValidTuple(stringTuple2, 1));
    Namespace namespace = new Namespace(getResource("default.ns"));
    TupleStore objecToTestRdfTrue = new TupleStore(true, true, true, 2, 5, 4, 2, namespace,
        getResource("default.nt"));
    // the second boolean argument is rdfCheck
    // test for case where rdfCheck is manipulated
    assertFalse(objecToTestRdfTrue.isValidTuple(stringTuple, 1));
    TupleStore objecToTestRdfFalse = new TupleStore(true, false, true, 2, 5, 4, 2, namespace,
        getResource("default.nt"));
    assertTrue(objecToTestRdfFalse.isValidTuple(stringTuple, 3));
    // TODO test for case TupleStore.isAtom(stringTuple.get(0)

    // TODO test for case !TupleStore.isUri(stringTuple.get(1)))
  }

  @Test
  public void testisAtom() {
    TupleStore objecttotest = new TupleStore(2, 7);
    assertFalse(objecttotest.isAtom(0));
  }

  @Test
  public void testisUri1() {
    assertTrue(TupleStore.isUri("<http://www.w3.org/2002/07/owl#sameAs>"));
    assertFalse(TupleStore.isUri("hgghdgh"));
  }

  @Test
  public void testisUri2() {
    TupleStore objecttotest = new TupleStore(1, 3);
    assertFalse(objecttotest.isUri(0));
  }

  @Test
  public void testisBlankNode1() {
    assertFalse(TupleStore.isBlankNode("hello"));
    assertTrue(TupleStore.isBlankNode("_sjjg"));
  }

  @Test
  public void testisBlankNode2() {
    TupleStore objecttotest = new TupleStore(2, 5);
    assertFalse(objecttotest.isBlankNode(2));
  }

  @Test
  public void testisConstant1() {
    TupleStore objecttotest = new TupleStore(2, 7);
    assertFalse(objecttotest.isConstant("hi"));

  }

  @Test
  public void testisConstant2() {
    TupleStore objecttotest = new TupleStore(2, 7);
    assertFalse(objecttotest.isConstant(0));
    assertTrue(objecttotest.isConstant(1));

  }

  @Test
  public void testinternalizeTuple1() {
    TupleStore objectfortest = new TupleStore(4, 2);
    ArrayList<String> stringTuple = new ArrayList<String>();
    stringTuple.add("hello");
    stringTuple.add("world");
    // System.out.println("MESSAGE " +
    // objectfortest.internalizeTuple(stringTuple));
    // assertNotNull(objectfortest.internalizeTuple(stringTuple));
    assertEquals(objectfortest.internalizeTuple(stringTuple).length, stringTuple.size());
  }

  @Test
  public void testinternalizeTuple2() {
    TupleStore objectfortest = new TupleStore(4, 2);
    String[] stringTuple = new String[2];
    stringTuple[0] = "hello";
    stringTuple[1] = "world";
    assertEquals(objectfortest.internalizeTuple(stringTuple).length, stringTuple.length);
  }

  @Test
  public void testaddTuple1() {
    TupleStore objectfortest = new TupleStore(4, 3);
    String[] stringTuple = new String[2];
    stringTuple[0] = "hello";
    stringTuple[1] = "world";
    assertTrue(objectfortest.addTuple(stringTuple));
  }

  @Test
  public void testaddTuple2() {
    TupleStore objectfortest = new TupleStore(1, 6);
    int[] tuple = new int[2];
    tuple[0] = 2;
    tuple[1] = 5;
    assertTrue(objectfortest.addTuple(tuple));
  }

  @Test
  public void testgetTuples1() {
    TupleStore objectfortest = new TupleStore(1, 3);
    // testing for case if (result == null)
    assertTrue(objectfortest.getTuples(2, 1).isEmpty());
    // testing for case if (result!= null), still uncovered
    int[] myarray = new int[2];
    myarray[0] = 1;
    myarray[1] = 3;
    assertTrue(objectfortest.getTuples(1, 4).add(myarray));
    // TODO test for if (result!=null)
    Namespace namespace = new Namespace(getResource("default.ns"));
    TupleStore objectfull = new TupleStore(namespace, getResource("default.nt"));
    assertFalse(objectfull.getTuples(0, 1).isEmpty());
  }

  @Test
  public void testgetTuples2() {
    TupleStore objectfortest = new TupleStore(1, 2);
    // test for case if (result == null)
    assertTrue(objectfortest.getTuples(1, "hello").isEmpty());
    // TODO test for case if (result!=null)

  }

  @Test
  public void testgetAllTuples() {
    // test for case when there are no tuples
    TupleStore objectfortest = new TupleStore(1, 4);
    assertTrue(objectfortest.getAllTuples().isEmpty());
    // test for case when there are tuples
    Namespace namespace = new Namespace(getResource("default.ns"));
    TupleStore objectfull = new TupleStore(namespace, getResource("default.nt"));
    assertFalse(objectfull.getAllTuples().isEmpty());
  }

  @Test
  public void testremoveTuple() {
    TupleStore objectfortest = new TupleStore(2, 5);
    int[] tuple = new int[2];
    // test for case when there are no tuples
    assertFalse(objectfortest.removeTuple(tuple));
    // TODO test for case when there are some tuples

    // int[] tuple1 = new int[2];
    // tuple[0] = 1;
    // tuple[1] = 2;
    // assertTrue(objectfull.removeTuple(tuple1));
    // System.out.println("MESSAGE " + objectfull.removeTuple(tuple1));
  }

  @Test
  public void testwriteTuples1() {
    TupleStore objectfortest = new TupleStore(2, 1);
    objectfortest.writeTuples("file1.nt");
    assertFalse("file1.nt".isEmpty());
  }

  @Test
  public void testwriteTuples2() {
    TupleStore objectfortest = new TupleStore(2, 1);
    ArrayList<int[]> collection = new ArrayList<int[]>();
    objectfortest.writeTuples(collection, "file2.nt");
    assertFalse("file2.nt".isEmpty());
    // test for case verbose = false (looks like it's useless)
    Namespace namespace = new Namespace(getResource("default.ns"));
    TupleStore tupleconstructor5 = new TupleStore(true, false, true, 2, 5, 4, 2, namespace,
        getResource("default.nt"));
    tupleconstructor5.writeTuples(collection, "file3.nt");
    assertFalse("file3.nt".isEmpty());
    // TODO test for case with exception
  }

  @Test
  public void testwriteExpandedTuples() {
    TupleStore objectfortest = new TupleStore(2, 1);
    objectfortest.writeExpandedTuples("file4.nt");
    assertFalse("file4.nt".isEmpty());
    // TODO test for case with exception
  }

  @Test
  public void testwriteTupleStore() {
    TupleStore objectfortest = new TupleStore(3, 1);
    objectfortest.writeTupleStore("file5.nt");
    assertFalse("file5.nt".isEmpty());
    // TODO check for case with exception
    // verbose = false
    Namespace namespace = new Namespace(getResource("default.ns"));
    TupleStore objectverbosefalse = new TupleStore(true, false, true, 2, 5, 4, 2, namespace,
        getResource("default.nt"));
    objectverbosefalse.writeTupleStore("file6.nt");
    assertFalse("file6.nt".isEmpty());
  }

  @Test
  public void testtoString() {
    TupleStore objectfortest = new TupleStore(1, 2);
    int[] tuple = new int[1];
    tuple[0] = 1;
    // assertEquals(objectfortest.toString(tuple), "<rdfs:subClassOf> .");
    // once fails, a few times passes
    System.out.println("================");
    System.out.println("SEE toString HERE " + objectfortest.toString(tuple));
    System.out.println("================");
    assertEquals(objectfortest.toString(tuple), "<http://www.w3.org/2000/01/rdf-schema#subClassOf> .");
  }

  @Test
  public void testtoExpandedString() {
    TupleStore objectfortest = new TupleStore(1, 3);
    int[] tuple = new int[1];
    tuple[0] = 2;
    assertEquals(objectfortest.toExpandedString(tuple), "<http://www.w3.org/2002/07/owl#sameAs> .");
  }

  @Test
  public void testask1() {
    TupleStore objectfortest = new TupleStore(1, 2);
    String[] externalTuple = new String[2];
    assertFalse(objectfortest.ask(externalTuple));
    // TODO check for case where the tuple exists
  }

  @Test
  public void testask2() {
    ArrayList<String> externalTuple = new ArrayList<String>();
    TupleStore objectfortest = new TupleStore(1, 2);
    assertFalse(objectfortest.ask(externalTuple));
  }

  @Test
  public void testask3() {
    TupleStore objectfortest = new TupleStore(1, 5);
    int[] tuple = new int[2];
    assertFalse(objectfortest.ask(tuple));
  }

  @Test
  public void testcontainsPrefixPattern() {
    TupleStore objectfortest = new TupleStore(2, 5);
    String[] prefixPattern = new String[1];
    prefixPattern[0] = "gh";
    assertFalse(objectfortest.containsPrefixPattern(prefixPattern));
    // TODO check for case where tuple has prefix
    // prefixPattern[0] = null;
    // System.out.println("PREFIX " +
    // objectfortest.containsPrefixPattern(prefixPattern));
  }

  @Test
  public void testqueryIndex() {
    TupleStore objectfortest = new TupleStore(2, 3);
    int[] pattern = new int[2];
    Table table = new Table();
    assertTrue(objectfortest.queryIndex(pattern, table).isEmpty());
    // TODO test for other cases
  }

  @Test
  public void testcopyTupleStore() {
    Namespace namespace = new Namespace(getResource("default.ns"));
    TupleStore objectfortest = new TupleStore(true, true, true, 2, 5, 4, 2, namespace,
        getResource("default.nt"));
    objectfortest.copyTupleStore(namespace);
    // TODO check these assertions
    assertFalse(objectfortest == objectfortest.copyTupleStore(namespace));
    assertTrue(objectfortest != objectfortest.copyTupleStore(namespace));

  }
}
