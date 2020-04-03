package de.dfki.lt.hfc;

import static de.dfki.lt.hfc.TestingUtils.*;
import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.logging.Logger;

import de.dfki.lt.hfc.types.Uri;
import de.dfki.lt.hfc.types.Variable;
import org.junit.Test;


public class TupleStoreTest {

 @Test
 public void testConstructors() throws FileNotFoundException, WrongFormatException, IOException {
  // constructor TupleStore(int noOfAtoms, int noOfTuples) is tested
  NamespaceManager namespace = NamespaceManager.getInstance();
  assertNotNull(namespace);

  // constructor TupleStore(boolean verbose, boolean rdfCheck, boolean
  // eqReduction, int minNoOfArgs, int maxNoOfArgs,
  // int noOfAtoms, int noOfTuples, NamespaceManager namespace, String tupleFile) is
  // tested
  TupleStore tupleconstructor5 = new TupleStore(false, true, true, 2, 5, 0, 1, 2, 4, 2, namespace,
          getTestResource("default.nt"));
  assertNotNull(tupleconstructor5);
  // constructor TupleStore(NamespaceManager namespace) is tested
  TupleStore tupleconstructor6 = new TupleStore(namespace);
  assertNotNull(tupleconstructor6);
  // constructor TupleStore(NamespaceManager namespace, String tupleFile) is tested
  TupleStore tupleconstructor7 = new TupleStore(namespace, getTestResource("default.nt"));
  assertNotNull(tupleconstructor7);
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
 public void testcleanUpTuple() throws FileNotFoundException, WrongFormatException, IOException {
  NamespaceManager namespace = NamespaceManager.getInstance();
  TupleStore objfortest = new TupleStore(false, true, true, 2, 5, 0, 1, 2, 4, 2, namespace,
          getTestResource("default.nt"));
  int[] tuple = new int[3];
  tuple[0] = 2;
  tuple[1] = 2;
  tuple[2] = 2;
  objfortest.addToIndex(tuple);
  objfortest.cleanUpTupleStore();
  assertEquals(objfortest.cleanUpTupleStore(), 1);
 }

 @Test
 public void testputObject() throws IOException, WrongFormatException {
  TupleStore objecttotest = new TupleStore(Config.getDefaultConfig());
  int id = objecttotest.putObject("www.bbc.com");
  assertFalse("Compare the returned id with zero", 0 == id);
  assertEquals(objecttotest.putObject("www.bbc.com"), id);
 }

 @Test
 public void testgetObject() throws IOException, WrongFormatException {
  TupleStore objectfortest = new TupleStore(Config.getInstance(getTestResource("Empty.yml")));
  int id = objectfortest.putObject("www.bbc.com");
  assertFalse(objectfortest.getObject(5) == null);
  assertEquals("\"www.bbc.com\"^^<xsd:string>", objectfortest.getObject(id).toString());
  assertFalse(objectfortest.putObject("?") == 0);
  assertEquals("\"?\"^^<xsd:string>", objectfortest.getObject(7).toString());
  assertEquals("?100", objectfortest.getObject(-100), new Variable("?100"));
 }

 @Test
 public void testgetJavaObject() throws IOException, WrongFormatException {
  TupleStore objecttotest = new TupleStore(Config.getDefaultConfig());
  assertNotNull(objecttotest.getObject(3));
  assertNotNull(objecttotest.getObject(0));
  // TODO 1 more branch
 }

 @Test
 public void testregisterJavaObject() throws IOException, WrongFormatException {
  TupleStore objecttotest = new TupleStore(Config.getInstance(getTestResource("Empty.yml")));
  assertEquals(objecttotest.registerJavaObject(new Uri("www.bbc.com", NamespaceManager.TEST)), 6);
 }

 @Test
 public void testisValidTuple() throws FileNotFoundException, WrongFormatException, IOException {
  TupleStore objectfortest = new TupleStore(Config.getDefaultConfig());
  ArrayList<String> stringTuple = new ArrayList<String>();
  stringTuple.add("\"hello\"");
  stringTuple.add("\"world\"");// test for case stringTuple.size < maxNoOfArgs
  assertFalse(objectfortest.isValidTuple(stringTuple, 2));
  ArrayList<String> stringTuple2 = new ArrayList<String>();
  stringTuple2.add("hello");
  stringTuple2.add("world");
  stringTuple2.add("wjfjf");
  stringTuple2.add("djggerhe");
  stringTuple2.add("sf");
  stringTuple2.add("sfsd");// test for case stringTuple.size > maxNoOfArgs
  assertFalse(objectfortest.isValidTuple(stringTuple2, 1));
  NamespaceManager namespace = NamespaceManager.getInstance();
  TupleStore objecToTestRdfTrue = new TupleStore(false, true, true, 2, 5, 0, 1, 2, 4, 2, namespace,
          getTestResource("default.nt"));
  // the second boolean argument is rdfCheck
  // test for case where rdfCheck is manipulated
  assertFalse(objecToTestRdfTrue.isValidTuple(stringTuple, 1));
  TupleStore objecToTestRdfFalse = new TupleStore(false, false, true, 2, 5, 0, 1, 2, 4, 2, namespace,
          getTestResource("default.nt"));
  assertTrue(objecToTestRdfFalse.isValidTuple(stringTuple, 3));
  assertTrue(objectfortest.isAtom(stringTuple.get(0)));
  assertTrue(objectfortest.isAtom(stringTuple.get(1)));
 }

 @Test
 public void testisAtom() throws IOException, WrongFormatException {
  TupleStore objecttotest = new TupleStore(Config.getDefaultConfig());
  assertFalse(objecttotest.isAtom(0));
 }

 @Test
 public void testisUri1() {
  assertTrue(TupleStore.isUri("<http://www.w3.org/2002/07/owl#sameAs>"));
  assertFalse(TupleStore.isUri("hgghdgh"));
 }


 @Test
 public void testisBlankNode1() {
  assertFalse(TupleStore.isBlankNode("hello"));
  assertTrue(TupleStore.isBlankNode("_sjjg"));
 }

 @Test
 public void testisBlankNode2() throws IOException, WrongFormatException {
  TupleStore objecttotest = new TupleStore(Config.getDefaultConfig());
  assertFalse(objecttotest.isBlankNode(2));
 }

 @Test
 public void testisConstant1() throws IOException, WrongFormatException {
  TupleStore objecttotest = new TupleStore(Config.getDefaultConfig());
  assertFalse(objecttotest.isConstant("hi"));

 }

 @Test
 public void testisConstant2() {
  assertFalse(TupleStore.isConstant(0));
  assertTrue(TupleStore.isConstant(1));
 }

 @Test
 public void testParseAtom() throws IOException, WrongFormatException, QueryParseException {
  TupleStore objectToTest = new TupleStore(Config.getInstance(getTestResource("Empty.yml")));
  objectToTest.readTuples(getTestResource("ReadTest", "testAtoms.nt"),false);

  assertEquals("Expected 12 tuples but was " + objectToTest.getAllTuples().size(),
          12, objectToTest.getAllTuples().size());
  String[][] expected = {
          {"\"fo>o\"^^<xsd:string>"},
          {"\"fo<o\"^^<xsd:string>"},
          {"\"f<\\\"o\\\"o\"^^<xsd:string>"},
          {"\"f<\\\"\\\">o^o\"^^<xsd:string>"},
          {"\"foo\"^^<xsd:string>"},
          {"\"f<oo>\"^^<xsd:string>"},
          {"\"fo o\"^^<xsd:string>"},
          {"\"fo<o\"^^<xsd:string>"},
          {"\"fo\\\\o\"^^<xsd:string>"},
          {"\"f<\\\"\\\">o^^\\\"o\"^^<xsd:string>"},
          {"\"fo_|o\"^^<xsd:string>"},
          {"\"f<\\\"\\\">o\\\"o\"^^<xsd:string>"}
  };
  Query q = new Query(objectToTest);
  BindingTable bt = q.query("SELECT ?o WHERE ?s <test:value> ?o");
  //printExpected(bt, objectToTest);
  checkResult(expected, bt, "?o");
 }

 @Test
 public void testUnicodeHandling() throws IOException, WrongFormatException, QueryParseException {
  TupleStore objectToTest = new TupleStore(Config.getInstance(getTestResource("Empty.yml")));
  objectToTest.readTuples(getTestResource("ReadTest", "testUnicodes.nt"),false);

  String[][] expected = {
          {"\"Hello foo\"^^<xsd:string>"},
          {"\"Hello foo!\"^^<xsd:string>"},
          {"\"ü foo\"^^<xsd:string>"}
  };
  Query q = new Query(objectToTest);
  BindingTable bt = q.query("SELECT ?o WHERE ?s <test:value> ?o");
  checkResult(expected, bt, "?o");
  Set<String> expectedOutput = new HashSet<String>() {{
   add("<test:a1> <test:value> \"Hello foo!\"^^<xsd:string> .");
   add("<test:a1> <test:value> \"Hello foo\"^^<xsd:string> .");
   add("<test:a2> <test:value> \"\\u00fc foo\"^^<xsd:string> .");
  }};
  for (int[] tuple : objectToTest.getAllTuples()) {
   expectedOutput.remove(objectToTest.toString(tuple));
  }
  bt = q.query("SELECT ?o WHERE ?s <test:value> ?o");
  checkResult(expected, bt, "?o");
  expectedOutput = new HashSet<String>() {{
   add("<http://www.dfki.de/lt/onto/test.owl#a1> <http://www.dfki.de/lt/onto/test.owl#value> \"Hello foo!\"^^<http://www.w3.org/2001/XMLSchema#string> .");
   add("<http://www.dfki.de/lt/onto/test.owl#a1> <http://www.dfki.de/lt/onto/test.owl#value> \"Hello foo\"^^<http://www.w3.org/2001/XMLSchema#string> .");
   add("<http://www.dfki.de/lt/onto/test.owl#a2> <http://www.dfki.de/lt/onto/test.owl#value> \"\\u00fc foo\"^^<http://www.w3.org/2001/XMLSchema#string> .");
  }};
  for (int[] tuple : objectToTest.getAllTuples()) {
   expectedOutput.remove(objectToTest.toExpandedString(tuple));
  }
  assertEquals(0, expectedOutput.size());
 }

 @Test
 public void testinternalizeTuple1() throws IOException, WrongFormatException {
  TupleStore objectfortest = new TupleStore(Config.getDefaultConfig());
  ArrayList<String> stringTuple = new ArrayList<String>();
  stringTuple.add("hello");
  stringTuple.add("world");
  assertEquals(objectfortest.internalizeTuple(stringTuple).length, stringTuple.size());
 }

 @Test
 public void testinternalizeTuple2() throws IOException, WrongFormatException {
  TupleStore objectfortest = new TupleStore(Config.getDefaultConfig());
  String[] stringTuple = new String[2];
  stringTuple[0] = "hello";
  stringTuple[1] = "world";
  assertEquals(objectfortest.internalizeTuple(stringTuple).length, stringTuple.length);
 }

 @Test
 public void testaddTuple1() throws IOException, WrongFormatException {
  TupleStore objectfortest = new TupleStore(Config.getDefaultConfig());
  String[] stringTuple = new String[2];
  stringTuple[0] = "hello";
  stringTuple[1] = "world";
  assertTrue(objectfortest.addTuple(stringTuple));
 }

 @Test
 public void testaddTuple2() throws IOException, WrongFormatException {
  TupleStore objectfortest = new TupleStore(Config.getDefaultConfig());
  int[] tuple = new int[2];
  tuple[0] = 2;
  tuple[1] = 5;
  assertTrue(objectfortest.addTuple(tuple));
 }


 @Test
 public void testgetTuples1()
         throws FileNotFoundException, WrongFormatException, IOException {
  TupleStore toTest = new TupleStore(Config.getInstance(getTestResource("Empty.yml")));
  // testing for case if (result == null)
  assertTrue(toTest.getTuples(2, 1).isEmpty());
  // testing for case if (result!= null), still uncovered
  int[] myarray = {3, 2, 1};
  toTest.addTuple(myarray);
  assertEquals(1, toTest.getTuples(0, 3).size());
  assertEquals(1, toTest.getTuples(1, 2).size());
  assertEquals(1, toTest.getTuples(2, 1).size());
  assertEquals(0, toTest.getTuples(1, 1).size());
 }

 @Test
 public void testgetTuples2() throws IOException, WrongFormatException {
  TupleStore objectfortest = new TupleStore(Config.getDefaultConfig());
  objectfortest.namespace.putForm("foo", "http://www.dfki.de/lt/onto/foo.owl#", false);
  // test for case if (result == null)
  assertTrue(objectfortest.getTuples(2, "hello").isEmpty());
  String[] tuple = {"<foo:bar>", "<foo:hasValue>", "hello"};
  objectfortest.addTuple(tuple);
  assertEquals(1, objectfortest.getTuples(2, "hello").size());
  assertEquals(0, objectfortest.getTuples(1, "hello").size());
  assertEquals(1, objectfortest.getTuples(1, "<foo:hasValue>").size());
  assertEquals(1, objectfortest.getTuples(0, "<foo:bar>").size());
 }

 @Test
 public void testgetAllTuples() throws FileNotFoundException, WrongFormatException, IOException {
  // test for case when there are no tuples
  TupleStore objectfortest = new TupleStore(Config.getInstance(getTestResource("Empty.yml")));
  assertTrue(objectfortest.getAllTuples().isEmpty());
  // test for case when there are tuples
  NamespaceManager namespace = NamespaceManager.getInstance();
  TupleStore objectfull = new TupleStore(namespace, getTestResource("default.nt"));
  assertFalse(objectfull.getAllTuples().isEmpty());
 }

 @Test
 public void testRemoveTuple() throws IOException, WrongFormatException {
  TupleStore objectForTest = new TupleStore(Config.getDefaultConfig());
  int[] tuple = new int[2];
  // test for case when there are no tuples
  assertFalse(objectForTest.removeTuple(tuple));
  int[] tuple1 = new int[2];
  tuple[0] = 1;
  tuple[1] = 2;
  objectForTest.addTuple(tuple1);
  assertTrue(objectForTest.removeTuple(tuple1));

 }

 @Test
 public void testwriteTuples1()
         throws FileNotFoundException, IOException, WrongFormatException {
  String file = getTempFile("file.nt");
  TupleStore objectfortest = new TupleStore(Config.getDefaultConfig());
  objectfortest.writeTuples(file);
  int out = objectfortest.getAllTuples().size();
  TupleStore in = new TupleStore(Config.getDefaultConfig());
  in.readTuples(file,false);
  assertEquals(out, in.getAllTuples().size());
 }

 @Test
 public void testWriteExpandedTuples()
         throws IOException, WrongFormatException {
  String file = getTempFile("file.nt");

  TupleStore objectfortest = new TupleStore(Config.getDefaultConfig());
  objectfortest.writeExpandedTuples(file);
  int out = objectfortest.getAllTuples().size();

  TupleStore in = new TupleStore(Config.getDefaultConfig());
  in.readTuples(file,false);
  assertEquals(out, in.getAllTuples().size());
  // TODO test for case with exception
 }


 @Test
 public void testWriteTupleStore()
         throws IOException, WrongFormatException {
  String file = getTempFile("file.nt");

  TupleStore objectfortest = new TupleStore(Config.getDefaultConfig());
  objectfortest.writeTupleStore(file);

  int out = objectfortest.getAllTuples().size();

  TupleStore in = new TupleStore(Config.getDefaultConfig());
  in.readTuples(file, false);
  assertEquals(out, in.getAllTuples().size());
  // TODO check for case with exception
  // verbose = false
 }


 @Test
 public void testtoString() throws IOException, WrongFormatException {
  TupleStore objectfortest = new TupleStore(Config.getDefaultConfig());
  int[] tuple = new int[1];
  tuple[0] = 1;
  String s = objectfortest.toString(tuple);
  assertTrue(s.equals("<rdfs:subClassOf> .")
          || s.equals("<http://www.w3.org/2000/01/rdf-schema#subClassOf> ."));
 }

 @Test
 public void testtoExpandedString() throws IOException, WrongFormatException {
  TupleStore objectfortest = new TupleStore(Config.getDefaultConfig());
  int[] tuple = new int[1];
  tuple[0] = 2;
  assertEquals(objectfortest.toExpandedString(tuple), "<http://www.w3.org/2002/07/owl#sameAs> .");
 }

 @Test
 public void testask1() throws IOException, WrongFormatException {
  TupleStore objectfortest = new TupleStore(Config.getDefaultConfig());
  String[] externalTuple = new String[2];
  assertFalse(objectfortest.ask(externalTuple));
  // TODO check for case where the tuple exists
 }

 @Test
 public void testask2() throws IOException, WrongFormatException {
  ArrayList<String> externalTuple = new ArrayList<String>();
  TupleStore objectfortest = new TupleStore(Config.getDefaultConfig());
  assertFalse(objectfortest.ask(externalTuple));
 }

 @Test
 public void testask3() throws IOException, WrongFormatException {
  TupleStore objectfortest = new TupleStore(Config.getDefaultConfig());
  int[] tuple = new int[2];
  assertFalse(objectfortest.ask(tuple));
 }

 @Test
 public void testcontainsPrefixPattern() throws IOException, WrongFormatException {
  TupleStore objectfortest = new TupleStore(Config.getDefaultConfig());
  String[] prefixPattern = new String[1];
  prefixPattern[0] = "gh";
  assertFalse(objectfortest.containsPrefixPattern(prefixPattern));
  // TODO check for case where tuple has prefix
  // prefixPattern[0] = null;
  // System.out.println("PREFIX " +
  // objectfortest.containsPrefixPattern(prefixPattern));
 }

 @Test
 public void testqueryIndex() throws IOException, WrongFormatException {
  TupleStore objectfortest = new TupleStore(Config.getDefaultConfig());
  int[] pattern = new int[2];
  Table table = new Table();
  assertTrue(objectfortest.queryIndex(pattern, table).isEmpty());
  // TODO test for other cases
 }

 @Test
 public void testcopyTupleStore() throws FileNotFoundException, WrongFormatException, IOException {
  NamespaceManager namespace = NamespaceManager.getInstance();
  TupleStore objectfortest = new TupleStore(false, true, true, 2, 5, 0, 1, 2, 4, 2, namespace,
          getTestResource("default.nt"));
  TupleStore copy = objectfortest.copyTupleStore();
  assertTrue(objectfortest.allTuples.size() == copy.allTuples.size());
  assertTrue(objectfortest.indexStore == copy.indexStore);
  assertFalse(objectfortest == objectfortest.copyTupleStore());


 }

 @Test
 public void parseAtom() throws IOException, WrongFormatException {
  String atom = "\"$rangeRestrictionViolated\"^^<xsd:string>";
  ArrayList<String> tuple = new ArrayList<String>();
  TupleStore objectForTest = new TupleStore(Config.getDefaultConfig());
  StringTokenizer tokenizer = new StringTokenizer(atom, " ?<>\"\\", true);
  while (tokenizer.hasMoreTokens()) {
   String token = tokenizer.nextToken();
   if (token.equals("\"")) {
    objectForTest.parseAtom(tokenizer, tuple);
   }
  }
  assertEquals(1, tuple.size());
  assertEquals("\"$rangeRestrictionViolated\"^^<xsd:string>", tuple.get(0));
 }

}
