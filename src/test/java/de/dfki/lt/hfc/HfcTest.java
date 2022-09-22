package de.dfki.lt.hfc;

import static de.dfki.lt.hfc.TestingUtils.*;
import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;
import java.util.Set;

import org.junit.Test;

import de.dfki.lt.hfc.types.Uri;
import gnu.trove.set.hash.TCustomHashSet;
import gnu.trove.set.hash.THashSet;


public class HfcTest {


 /**
  * Test default Config;
  */
 @Test
 public void testHfc() throws IOException, WrongFormatException {
  Hfc fc = new Hfc(Config.getDefaultConfig());
  assertNotNull(fc);
 }

 @Test
 public void testHfc1() throws IOException, WrongFormatException {
  Config config = Config.getInstance(getTestResource("test.yml"));
  Hfc fc = new Hfc(config);
  assertNotNull(fc);
 }

 @Test
 public void testExecuteQuery() throws QueryParseException {
  Hfc fc = new Hfc();
  assertNotNull(fc);
  BindingTable bt = fc.executeQuery("Select ?x ?y Where ?x <owl:subClassOf> ?y");
  assertNotNull(bt);
  bt.expandBindingTable();
 }

 @Test
 public void testsetNoOfCores() throws FileNotFoundException, IOException, WrongFormatException {
  //test method setNoOfCores(int noOfCores)
  Hfc fc = new Hfc(Config.getInstance(getTestResource("test_eq.yml")));
  fc.setNoOfCores(104);
  assertEquals(fc.config.getNoOfCores(), 104);
 }

 @Test
 public void testnextBlankNode() throws FileNotFoundException, IOException, WrongFormatException {
  //test method nextBlankNode () that returns an int
  Hfc fc = new Hfc(Config.getInstance(getTestResource("nextBlankNode.yml")));
  assertEquals(51, fc.nextBlankNode());
  assertTrue(fc.nextBlankNode() > 0);
 }

 @Test
 public void testcomputeClosure() throws FileNotFoundException, WrongFormatException, IOException {
  //test method computeClosure(int noOfIterations, boolean cleanUpRepository)
  Hfc fcverboseT = new Hfc(Config.getInstance(getTestResource("test_eq.yml")));
  assertEquals(true, fcverboseT.enableTupleDeletion());
  Hfc fcverboseF = new Hfc(Config.getInstance(getTestResource("test_eq.yml")));
  assertEquals(true, fcverboseF.enableTupleDeletion());
 }

 @Test
 public void testaddTuplesToRepository() throws FileNotFoundException, WrongFormatException, IOException {
  //test method addTuplesToRepository(Collection<int[]> tuples)

  Hfc fc = new Hfc(Config.getInstance(getTestResource("test_eq.yml")));
  assertEquals(fc.computeClosure(1, true), true);
  Hfc fc1 = new Hfc(Config.getInstance(getTestResource("test_eq.yml")));
  assertEquals(fc1.computeClosure(1, true), true);
  assertEquals(fc1.computeClosure(2, false), true);
  //
  TupleStore tupleStore = getOperatorTestStore();
  RuleStore ruleStore = new RuleStore(Config.getDefaultConfig(), tupleStore);
  Hfc fc2 = new Hfc(Config.getDefaultConfig(), tupleStore, ruleStore);
  assertEquals(fc2.computeClosure(1, true), true);
  assertEquals(fc2.computeClosure(1, false), true);
  //
  TupleStore tupleStore1 = getOperatorTestStore();
  Hfc fc3 = new Hfc(Config.getDefaultConfig(), tupleStore1, ruleStore);
  assertEquals(fc3.computeClosure(2, true), false);
 }

 @Test
 public void testcomputeClosure1() throws FileNotFoundException, WrongFormatException, IOException {
  //test method computeClosure() that returns a boolean
  Hfc fc2 = new Hfc(Config.getInstance(getTestResource("nextBlankNode.yml")));
  assertEquals(fc2.computeClosure(), true);
 }

 @Test
 public void testcomputeClosure2() throws FileNotFoundException, WrongFormatException, IOException {
  //test method computeClosure(Set<int[]> newTuples, int noOfIterations, boolean cleanUpRepository)
  Set<int[]> newTuples = new TCustomHashSet<int[]>(TupleStore.DEFAULT_HASHING_STRATEGY);
  int noOfIterations = 2;
  boolean cleanUpRepository = true;
  TupleStore tupleStore = getOperatorTestStore();
  RuleStore ruleStore = new RuleStore(Config.getDefaultConfig(), tupleStore);
  Hfc fc = new Hfc(Config.getDefaultConfig(), tupleStore, ruleStore);
  //no tuples were generated, so returns false
  assertEquals(fc.computeClosure(newTuples, noOfIterations, cleanUpRepository), false);
  //set verbose to false
  Hfc fc1 = new Hfc(Config.getInstance(getTestResource("test_eq.yml")));
  assertEquals(fc1.computeClosure(newTuples, noOfIterations, cleanUpRepository), false);
  //newTuples not empty:
  Set<int[]> newTuplesfull = new TCustomHashSet<int[]>(TupleStore.DEFAULT_HASHING_STRATEGY);
  int[] toadd = new int[2];
  toadd[0] = 0;
  toadd[1] = 1;
  newTuplesfull.add(toadd);
  Hfc fc2 = new Hfc(Config.getInstance(getTestResource("test_eq.yml")));
  assertEquals(fc2.computeClosure(newTuplesfull, 1, false), true);
  assertEquals(fc2.computeClosure(newTuplesfull, 1, true), false);
 }

 @Test
 public void testcomputeClosure3() throws FileNotFoundException, WrongFormatException, IOException {
  //test method computeClosure(Set<int[]> newTuples)
  Set<int[]> newTuples = new TCustomHashSet<int[]>(TupleStore.DEFAULT_HASHING_STRATEGY);
  Hfc fc = new Hfc(Config.getInstance(getTestResource("test_eq.yml")));
  assertEquals(fc.computeClosure(newTuples), false);
 }

 @Test
 public void testaddTuples() throws FileNotFoundException, WrongFormatException, IOException {
  //test method addTuples(Collection<int[]> tuples)
  Collection<int[]> tuples = new THashSet<int[]>();
  int[] e = new int[2];
  e[0] = 1;
  e[1] = 2;
  tuples.add(e);
  Hfc fc = new Hfc(Config.getInstance(getTestResource("test_eq.yml")));
  boolean b = fc.addTuples(tuples);
  assertEquals(true, b);
 }

 @Test
 public void testRemoveTuplesFromRepository() throws FileNotFoundException, WrongFormatException, IOException {
  //test method removeTuplesFromRepository(Collection<int[]> tuples)
  Collection<int[]> tuples = new THashSet<int[]>();
  int[] tuple = new int[3];
  Hfc fc = new Hfc(Config.getInstance(getTestResource("test_eq.yml")));
  tuple[0] = fc._tupleStore.putObject(new Uri("<rdf:Foo>", NamespaceManager.RDF).toString());
  tuple[1] = fc._tupleStore.putObject(new Uri("<rdf:Bar>", NamespaceManager.RDF).toString());
  tuple[2] = fc._tupleStore.putObject(new Uri("<rdf:FooBar>", NamespaceManager.RDF).toString());
  tuples.add(tuple);
  fc.addTuples(tuples);
  assertEquals(45, fc._tupleStore.allTuples.size());
  assertTrue(fc.removeTuples(tuples));
  assertEquals(44, fc._tupleStore.allTuples.size());

 }

 @Test
 public void testRemoveTuples() throws FileNotFoundException, WrongFormatException, IOException {
  //test method removeTuples(Collection<int[]> tuples)
  Collection<int[]> tuples = new THashSet<int[]>();
  int[] e = new int[2];
  e[0] = 1;
  e[1] = 2;
  tuples.add(e);
  Hfc fc = new Hfc(Config.getInstance(getTestResource("test_eq.yml")));
  assertEquals(false, fc.removeTuples(tuples));
 }

 @Test
 public void testDeleteTuplesFromRepository() throws FileNotFoundException, WrongFormatException, IOException {
  //test method deleteTuplesFromRepository(Collection<int[]> tuples)
  Collection<int[]> tuples = new THashSet<int[]>();
  int[] e = new int[2];
  e[0] = 1;
  e[1] = 2;
  tuples.add(e);
  Hfc fc = new Hfc(Config.getInstance(getTestResource("test_eq.yml")));
  assertEquals(false, fc.removeTuples(tuples));
 }

 @Test
 public void testComputeClosureFromRepository() throws FileNotFoundException, WrongFormatException, IOException {
  //test method computeClosureFromRepository()
  Hfc fc = new Hfc(Config.getInstance(getTestResource("computeClosureFromRepository.yml")));
  assertEquals(false, fc.computeClosure());
 }


 @Test
 public void testUploadRules() throws FileNotFoundException, WrongFormatException, IOException {
  //test method uploadRules(String filename)
  String filename = getTestResource("default.test.rdl");
  Hfc fc = new Hfc(Config.getInstance(getTestResource("test_eq.yml")));
  // the default rdl file contains 24 rules
  assertEquals(24,fc._ruleStore.allRules.size() );
  fc.uploadRules(filename);
  // the default.test.rdl file contains 2 rules -> 24 + 2 = 26 ;-)
  assertEquals(26,fc._ruleStore.allRules.size() );
 }

 @Test
 public void testShutdown() throws FileNotFoundException, WrongFormatException, IOException {
  //How to check? It just exits the system, so other tests are not executed
  //test method shutdown()
  Hfc fcverboseF = new Hfc(Config.getInstance(getTestResource("test_eq.yml")));
  Hfc fcverboseT = new Hfc(Config.getInstance(getTestResource("test_eq.yml")));
  //fcverboseT.shutdown();
  //fcverboseF.shutdown();
 }

 @Test
 public void testShutdownNoExit() throws FileNotFoundException, WrongFormatException, IOException {
  //test method shutdownNoExit()
  Hfc fcverboseF = new Hfc(Config.getInstance(getTestResource("test_eq.yml")));
  Hfc fcverboseT = new Hfc(Config.getInstance(getTestResource("test_eq.yml")));
  fcverboseT.config.setVerbose(true);
  assertTrue(fcverboseF.shutdownNoExit());
  assertTrue(fcverboseT.shutdownNoExit());
 }

 /*
 @Test
 public void testUpdateConfig() {
  Hfc defaultHfc = new Hfc();
  defaultHfc.updateConfig(getTestResource("test_eq.yml"));
  assertTrue(defaultHfc.getConfig().isEqReduction());
 }
 */

 /*
 @Test
 public void testCopyHfc() throws FileNotFoundException, WrongFormatException, IOException {
  //test method copyHfc(int noOfCores, boolean verbose)
  int noOfCores = 1;
  boolean verboseT = false;
  Hfc fc = new Hfc(Config.getInstance(getTestResource("test_eq.yml")));
  fc.copyHFC(noOfCores, verboseT);
  assertFalse(fc.copyHFC(noOfCores, verboseT) == fc);
  //cannot get access to tupleDeletionEnabled (private in TupleStore)
 }
 */

 @Test
 public void testtupleDeletionEnabled() throws FileNotFoundException, WrongFormatException, IOException {
  //test method tupleDeletionEnabled(), returns a boolean
  Hfc fc = new Hfc(Config.getInstance(getTestResource("test_eq.yml")));
  assertFalse(fc.tupleDeletionEnabled());
  fc.enableTupleDeletion();
  assertTrue(fc.tupleDeletionEnabled());
 }

  @Test
  public void status() {
   Hfc hfc = new Hfc();
   assertNotNull(hfc.status());
  }

  @Test
  public void readTuplesString() throws IOException, WrongFormatException {
   Hfc hfc = new Hfc();
   hfc.readTuples(getTestResource("test.nt"));
   int tuples = hfc._tupleStore.allTuples.size();
   assertEquals(47, tuples);
 }

  /*
  @Test
  public void readTuplesFile() throws IOException, WrongFormatException {
    Hfc hfc = new Hfc();
    hfc.readTuples(new File(getTestResource("test.nt")));
    int tuples = hfc._tupleStore.allTuples.size();
    assertEquals(47, tuples);
  }
  */

  @Test
  public void testUploadTuples() throws IOException, WrongFormatException {
    //test method uploadTuples(string filename)
    String filename = getTestResource("test.nt");
    Hfc fc = new Hfc(Config.getInstance(getTestResource("test_eq.yml")));
    assertEquals(44, fc._tupleStore.allTuples.size());
    // the test.nt file contains 1 tuple
    fc.uploadTuples(filename);
    assertEquals(45, fc._tupleStore.allTuples.size());
  }

  @Test
  public void isEquivalenceClassReduction() {
   Hfc hfc = new Hfc();
   assertFalse(hfc.isEquivalenceClassReduction());
  }

  @Test
  public void isCleanUpRepository() {
   Hfc hfc = new Hfc();
   assertTrue(hfc.isCleanUpRepository());
  }
}
