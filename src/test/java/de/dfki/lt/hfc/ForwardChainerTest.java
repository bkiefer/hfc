package de.dfki.lt.hfc;

import static org.junit.Assert.*;

import static de.dfki.lt.hfc.TestingUtils.getTestResource;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;
import java.util.Set;

import org.junit.Test;

import gnu.trove.set.hash.*;


public class ForwardChainerTest {



  /**
   *Test default Config;
   *
   */
   public void testForwardChainer() throws IOException {
     ForwardChainer fc = new ForwardChainer();
     assertNotNull(fc);
  }

  public void testForwardChainer1() throws IOException, WrongFormatException {
     Config config = Config.getInstance(getTestResource("test.yml"));
     ForwardChainer fc = new ForwardChainer(config);
     assertNotNull(fc);
  }

  public void testForwardChainer2() throws IOException, WrongFormatException {
     Config config = Config.getInstance(getTestResource("test.yml"));
     TupleStore tupleStore = new TupleStore(config.namespace);
     RuleStore ruleStore = new RuleStore(Config.getDefaultConfig(), tupleStore);
     ForwardChainer fc = new ForwardChainer(config, tupleStore, ruleStore);
     assertNotNull(fc);
  }

//  @deprecated these constructors are not longer used
//  @Test
//  public void test() throws FileNotFoundException, IOException, WrongFormatException {
//    //test constructor
//    /*ForwardChainer fc =	new ForwardChainer(100000, 500000,
//    		 "/Users/krieger/Desktop/Java/HFC/hfc/src/resources/default.eqred.nt",
//    		 "/Users/krieger/Desktop/Java/HFC/hfc/src/resources/default.eqred.rdl",
//    		 "/Users/krieger/Desktop/Java/HFC/hfc/src/resources/default.ns");
//    fc.uploadTuples("/Users/krieger/Desktop/Java/HFC/hfc/src/resources/ltworld.jena.nt");
//    fc.computeClosure();
//    fc.computeClosure();
//    fc.shutdown();*/
//    //test constructor ForwardChainer(String tupleFile, String ruleFile)
//    String tupleFile = getTestResource("default.nt");
//    String ruleFile = getTestResource("default.eqred.rdl");
//    ForwardChainer fc = new ForwardChainer(tupleFile, ruleFile);
//    assertNotNull(fc);
//  }
//
//  @Test
//  public void testForwardChainer1() throws FileNotFoundException, WrongFormatException, IOException {
//    //test constructor ForwardChainer(String tupleFile, String ruleFile, String namespaceFile)
//    String tupleFile = getTestResource("default.nt");
//    String ruleFile = getTestResource("default.eqred.rdl");
//    String namespaceFile = getTestResource("default.ns");
//    ForwardChainer fc = new ForwardChainer(tupleFile, ruleFile, namespaceFile);
//    assertNotNull(fc);
//  }
//
//  @Test
//  public void testForwardChainer2() throws FileNotFoundException, IOException, WrongFormatException {
//    //test constructor ForwardChainer(int noOfAtoms, int noOfTuples, String tupleFile, String ruleFile)
//    int noOfAtoms = 2;
//    int noOfTuples = 2;
//    String tupleFile = getTestResource("default.nt");
//    String ruleFile = getTestResource("default.eqred.rdl");
//    ForwardChainer fc = new ForwardChainer(noOfAtoms, noOfTuples, tupleFile, ruleFile);
//    assertNotNull(fc);
//  }
//
//  @Test
//  public void testForwardChainer3() throws FileNotFoundException, WrongFormatException, IOException {
//    //test constructor ForwardChainer(int noOfAtoms, int noOfTuples, String tupleFile, String ruleFile, String namespaceFile) {
//    int noOfAtoms = 1;
//    int noOfTuples = 100;
//    String tupleFile = getTestResource("default.nt");
//    String ruleFile = getTestResource("default.eqred.rdl");
//    String namespaceFile = getTestResource("default.ns");
//    ForwardChainer fc = new ForwardChainer(noOfAtoms, noOfTuples, tupleFile, ruleFile, namespaceFile);
//    assertNotNull(fc);
//  }
//
//  @Test
//  public void testForwardChainer4() throws FileNotFoundException, WrongFormatException, IOException {
//    //test constructor ForwardChainer(NamespaceManager namespace, TupleStore tupleStore, RuleStore ruleStore)
//    NamespaceManager namespace = NamespaceManager.getInstance();;
//    TupleStore tupleStore = new TupleStore(1, 2, namespace);
//    RuleStore ruleStore = new RuleStore(tupleStore);
//    ForwardChainer fc = new ForwardChainer(tupleStore, ruleStore);
//    assertNotNull(fc);
//  }
//
//  @Test
//  public void testForwardChainer5() throws FileNotFoundException, WrongFormatException, IOException {
//    /*test constructor ForwardChainer(
//     int noOfCores,
//       boolean verbose,
//    boolean rdfCheck,
//    boolean eqReduction,
//    int minNoOfArgs,
//    int maxNoOfArgs,
//    int noOfAtoms,
//    int noOfTuples,
//    String tupleFile,
//    String ruleFile,
//    String namespaceFile)*/
//    int noOfCores = 1;
//    boolean verboseT = false;
//    boolean verboseF = false;
//    boolean rdfCheck = true;
//    boolean eqReduction = true;
//    int minNoOfArgs = 2;
//    int maxNoOfArgs = 3;
//    int noOfAtoms = 2;
//    int noOfTuples = 3;
//    String tupleFile = getTestResource("default.nt");
//    String ruleFile = getTestResource("default.eqred.rdl");
//    String namespaceFile = getTestResource("default.ns");
//    ForwardChainer fc = new ForwardChainer(noOfCores, false, rdfCheck, eqReduction, minNoOfArgs, maxNoOfArgs,
//        noOfAtoms, noOfTuples, tupleFile, ruleFile, namespaceFile);
//    assertNotNull(fc);
//    ForwardChainer fc0 = new ForwardChainer(noOfCores, verboseF, rdfCheck, eqReduction, minNoOfArgs, maxNoOfArgs,
//        noOfAtoms, noOfTuples, tupleFile, ruleFile, namespaceFile);
//    assertNotNull(fc0);
//
//  }
//
//  @Test
//  public void testForwardChainer6() throws FileNotFoundException, WrongFormatException, IOException {
//    /* test constructor ForwardChainer(
//    int noOfCores,
//    boolean verbose,
//    int noOfAtoms,
//    int noOfTuples,
//    NamespaceManager namespace,
//    TupleStore tupleStore,
//    RuleStore ruleStore
//    ) */
//    int noOfCores = 2;
//    boolean verboseT = false;
//    boolean verboseF = false;
//    int noOfAtoms = 3;
//    int noOfTuples = 4;
//    NamespaceManager namespace = NamespaceManager.getInstance();;
//    TupleStore tupleStore = new TupleStore(2, 4, namespace);
//    RuleStore ruleStore = new RuleStore(tupleStore);
//    ForwardChainer fc = new ForwardChainer(noOfCores, false, noOfAtoms, noOfTuples, tupleStore,
//        ruleStore);
//    assertNotNull(fc);
//    ForwardChainer fc0 = new ForwardChainer(noOfCores, verboseF, noOfAtoms, noOfTuples, tupleStore,
//        ruleStore);
//    assertNotNull(fc0);
//  }

  @Test
  public void testsetNoOfCores() throws FileNotFoundException, IOException, WrongFormatException {
    //test method setNoOfCores(int noOfCores)
    ForwardChainer fc = new ForwardChainer(Config.getInstance(getTestResource("test_eq.yml")));
    fc.setNoOfCores(104);
    assertEquals(fc.config.noOfCores, 104);
  }

  @Test
  public void testnextBlankNode() throws FileNotFoundException, IOException, WrongFormatException {
    //test method nextBlankNode () that returns an int
    ForwardChainer fc = new ForwardChainer(Config.getInstance(getTestResource("nextBlankNode.yml")));
    for (int i = 0; i < fc.tupleStore.idToJavaObject.size(); i++) {
      System.out.println(i + ": " + fc.tupleStore.idToJavaObject.get(i).toString());
    }
    assertEquals(51, fc.nextBlankNode());

    assertTrue(fc.nextBlankNode() > 0);
  }

  @Test
  public void testcomputeClosure() throws FileNotFoundException, WrongFormatException, IOException {
    //test method computeClosure(int noOfIterations, boolean cleanUpRepository)
    ForwardChainer fcverboseT = new ForwardChainer(Config.getInstance(getTestResource("test_eq.yml")));
    assertEquals(true, fcverboseT.enableTupleDeletion());
    ForwardChainer fcverboseF = new ForwardChainer(Config.getInstance(getTestResource("test_eq.yml")));
    assertEquals(true, fcverboseF.enableTupleDeletion());
	}

	@Test
	public void testaddTuplesToRepository() throws FileNotFoundException, WrongFormatException, IOException{
	//test method addTuplesToRepository(Collection<int[]> tuples)
	  Collection<int[]> tuples = new THashSet<int[]>();
    int noOfCores = 1;
    boolean verboseT = false;
    boolean verboseF = false;
    boolean rdfCheck = true;
    boolean eqReduction = true;
    int minNoOfArgs = 2;
    int maxNoOfArgs = 3;
    int noOfAtoms = 2;
    int noOfTuples = 3;

    String tupleFile = getTestResource("default.nt");
    String ruleFile = getTestResource("default.eqred.rdl");
    String namespaceFile = getTestResource("default.ns");
    ForwardChainer fc = new ForwardChainer(Config.getInstance(getTestResource("test_eq.yml")));
    assertEquals(fc.computeClosure(1, true), true);
    ForwardChainer fc1 = new ForwardChainer(Config.getInstance(getTestResource("test_eq.yml")));
    assertEquals(fc1.computeClosure(1, true), true);
    assertEquals(fc1.computeClosure(2, false), true);
    //
    NamespaceManager namespace = NamespaceManager.getInstance();;
    TupleStore tupleStore = new TupleStore(false, true, false, 2, 5,0,1,2, 4, 2, namespace, getTestResource("default.nt"));
    RuleStore ruleStore = new RuleStore(Config.getDefaultConfig(), tupleStore);
    ForwardChainer fc2 = new ForwardChainer(Config.getDefaultConfig(),tupleStore, ruleStore);
    assertEquals(fc2.computeClosure(1, true), true);
    assertEquals(fc2.computeClosure(1, false), true);
    //
    TupleStore tupleStore1 = new TupleStore(false, true, false, 2, 1,0,1,2, 2, 3, namespace, getTestResource("default.nt"));
    ForwardChainer fc3 = new ForwardChainer(Config.getDefaultConfig(),tupleStore1, ruleStore);
    assertEquals(fc3.computeClosure(2, true), false);
  }

  @Test
  public void testcomputeClosure1() throws FileNotFoundException, WrongFormatException, IOException {
    //test method computeClosure() that returns a boolean
    ForwardChainer fc2 = new ForwardChainer(Config.getInstance(getTestResource("nextBlankNode.yml")));
    assertEquals(fc2.computeClosure(), true);
  }

  @Test
  public void testcomputeClosure2() throws FileNotFoundException, WrongFormatException, IOException {
    //test method computeClosure(Set<int[]> newTuples, int noOfIterations, boolean cleanUpRepository)
    Set<int[]> newTuples = new TCustomHashSet<int[]>(TupleStore.DEFAULT_HASHING_STRATEGY);
    int noOfIterations = 2;
    boolean cleanUpRepository = true;
    NamespaceManager namespace = NamespaceManager.getInstance();;
    TupleStore tupleStore = new TupleStore(false, true, false, 2, 5,0,1,2, 4, 2, namespace, getTestResource("default.nt"));
    RuleStore ruleStore = new RuleStore(Config.getDefaultConfig(), tupleStore);
    ForwardChainer fc = new ForwardChainer(Config.getDefaultConfig(), tupleStore, ruleStore);
    //no tuples were generated, so returns false
    assertEquals(fc.computeClosure(newTuples, noOfIterations, cleanUpRepository), false);
    //set verbose to false
    ForwardChainer fc1 = new ForwardChainer(Config.getInstance(getTestResource("test_eq.yml")));
    assertEquals(fc1.computeClosure(newTuples, noOfIterations, cleanUpRepository), false);
    //newTuples not empty:
    Set<int[]> newTuplesfull = new TCustomHashSet<int[]>(TupleStore.DEFAULT_HASHING_STRATEGY);
    int[] toadd = new int[2];
    toadd[0] = 0;
    toadd[1] = 1;
    newTuplesfull.add(toadd);
    ForwardChainer fc2 = new ForwardChainer(Config.getInstance(getTestResource("test_eq.yml")));
    assertEquals(fc2.computeClosure(newTuplesfull, 1, false), true);
    assertEquals(fc2.computeClosure(newTuplesfull, 1, true), false);
  }

  @Test
  public void testcomputeClosure3() throws FileNotFoundException, WrongFormatException, IOException {
    //test method computeClosure(Set<int[]> newTuples)
    Set<int[]> newTuples = new TCustomHashSet<int[]>(TupleStore.DEFAULT_HASHING_STRATEGY);
    ForwardChainer fc = new ForwardChainer(Config.getInstance(getTestResource("test_eq.yml")));
    assertEquals(fc.computeClosure(newTuples), false);
  }

//  @Test
//  public void testuploadNamespaces() throws FileNotFoundException, WrongFormatException, IOException {
//    //test method uploadNamespaces(String filename)
//    String filename = getTestResource("default.ns");
//    String tupleFile = getTestResource("default.nt");
//    String ruleFile = getTestResource("default.eqred.rdl");
//    ForwardChainer fc = new ForwardChainer(Config.getInstance(getTestResource("test_eq.yml")));
//    fc.uploadNamespaces(filename);
//    //TODO create a test
//  }

  @Test
  public void testuploadTuples() throws FileNotFoundException, WrongFormatException, IOException {
    //test method uploadTuples(string filename)
    String filename = getTestResource("default.nt");
    ForwardChainer fc = new ForwardChainer(Config.getInstance(getTestResource("test_eq.yml")));
    fc.uploadTuples(filename);
    //TODO create a test
  }

  @Test
  public void testaddTuples() throws FileNotFoundException, WrongFormatException, IOException {
    //test method addTuples(Collection<int[]> tuples)
    Collection<int[]> tuples = new THashSet<int[]>();
    int[] e = new int[2];
    e[0] = 1;
    e[1] = 2;
    tuples.add(e);
    ForwardChainer fc = new ForwardChainer(Config.getInstance(getTestResource("test_eq.yml")));
    assertEquals(false, fc.addTuplesToRepository(tuples));
  }

  @Test
  public void testremoveTuplesFromRepository() throws FileNotFoundException, WrongFormatException, IOException{
    //test method removeTuplesFromRepository(Collection<int[]> tuples)
    Collection<int[]> tuples = new THashSet<int[]>();
    ForwardChainer fc = new ForwardChainer(Config.getInstance(getTestResource("test_eq.yml")));
    fc.addTuples(tuples);
    //TODO create a test

  }

  @Test
  public void testremoveTuples() throws FileNotFoundException, WrongFormatException, IOException {
    //test method removeTuples(Collection<int[]> tuples)
    Collection<int[]> tuples = new THashSet<int[]>();
    int[] e = new int[2];
    e[0] = 1;
    e[1] = 2;
    tuples.add(e);
    ForwardChainer fc = new ForwardChainer(Config.getInstance(getTestResource("test_eq.yml")));
    assertEquals(false, fc.removeTuplesFromRepository(tuples));
  }

  @Test
  public void testdeleteTuplesFromRepository() throws FileNotFoundException, WrongFormatException, IOException{
    //test method deleteTuplesFromRepository(Collection<int[]> tuples)
    Collection<int[]> tuples = new THashSet<int[]>();
    int[] e = new int[2];
    e[0] = 1;
    e[1] = 2;
    tuples.add(e);
    ForwardChainer fc = new ForwardChainer(Config.getInstance(getTestResource("test_eq.yml")));
    assertEquals(false, fc.deleteTuplesFromRepository(tuples));
  }

  @Test
  public void testcomputeClosureFromRepository() throws FileNotFoundException, WrongFormatException, IOException{
    //test method computeClosureFromRepository()
    ForwardChainer fc = new ForwardChainer(Config.getInstance(getTestResource("computeClosureFromRepository.yml")));
    assertEquals(false, fc.computeClosure());

//    NamespaceManager namespace = new NamespaceManager(getTestResource("default.ns"));
//    TupleStore tupleStore = new TupleStore(1, 2, namespace);
//    RuleStore ruleStore = new RuleStore(tupleStore);
//    ForwardChainer fc = new ForwardChainer(tupleStore, ruleStore);
//    assertEquals(false, fc.computeClosure());
  }

  /* Don't know where this belongs
    NamespaceManager namespace = NamespaceManager.getInstance();;
    TupleStore tupleStore = new TupleStore(1, 2, namespace);
    RuleStore ruleStore = new RuleStore(namespace, tupleStore);
    ForwardChainer fc = new ForwardChainer(tupleStore, ruleStore);
    fc.removeTuples(tuples);
    //TODO create a test
  }
  */

  @Test
  public void testuploadRules() throws FileNotFoundException, WrongFormatException, IOException {
    //test method uploadRules(String filename)
    String filename = getTestResource("default.eqred.rdl");
    ForwardChainer fc = new ForwardChainer(Config.getInstance(getTestResource("test_eq.yml")));
    fc.uploadRules(filename);
    //TODO create a test
  }

  @Test
  public void testshutdown() throws FileNotFoundException, WrongFormatException, IOException {
    //test method shutdown()
    int noOfCores = 1;
    boolean verboseT = false;
    boolean verboseF = false;
    boolean rdfCheck = true;
    boolean eqReduction = true;
    int minNoOfArgs = 2;
    int maxNoOfArgs = 3;
    int noOfAtoms = 2;
    int noOfTuples = 3;
    String tupleFile = getTestResource("default.nt");
    String ruleFile = getTestResource("default.eqred.rdl");
    String namespaceFile = getTestResource("default.ns");
    ForwardChainer fcverboseF = new ForwardChainer(Config.getInstance(getTestResource("test_eq.yml")));
    ForwardChainer fcverboseT = new ForwardChainer(Config.getInstance(getTestResource("test_eq.yml")));
    //How to check? It just exits the system, so other tests are not executed
    //fcverboseT.shutdown();
    //fcverboseF.shutdown();
  }

  @Test
  public void testshutdownNoExit() throws FileNotFoundException, WrongFormatException, IOException {
    //test method shutdownNoExit()
    int noOfCores = 1;
    boolean verboseT = false;
    boolean verboseF = false;
    boolean rdfCheck = true;
    boolean eqReduction = true;
    int minNoOfArgs = 2;
    int maxNoOfArgs = 3;
    int noOfAtoms = 2;
    int noOfTuples = 3;
    String tupleFile = getTestResource("default.nt");
    String ruleFile = getTestResource("default.eqred.rdl");
    String namespaceFile = getTestResource("default.ns");
    ForwardChainer fcverboseF = new ForwardChainer(Config.getInstance(getTestResource("test_eq.yml")));
    fcverboseF.verbose = false;
    ForwardChainer fcverboseT = new ForwardChainer(Config.getInstance(getTestResource("test_eq.yml")));
    fcverboseT.verbose = true;
    fcverboseF.shutdownNoExit();
    fcverboseT.shutdownNoExit();
    //DOTO create a test
  }

  @Test
  public void testcompress() throws FileNotFoundException, IOException, WrongFormatException {
    //test method compress(int level)
    int level0 = 0;
    int level1 = 1;
    int level2 = 2;
    int level3 = 3;
    int levelx = 12900;
    String tupleFile = getTestResource("default.nt");
    String ruleFile = getTestResource("default.eqred.rdl");
    ForwardChainer fc = new ForwardChainer(Config.getInstance(getTestResource("test_eq.yml")));
    fc.compress(level0);
    fc.compress(level1);
    fc.compress(level2);
    fc.compress(level3);
    fc.compress(levelx);
    //TODO create tests for the above 5 cases
  }

  /**
  @Test
  public void testuncompressIndex() throws FileNotFoundException, WrongFormatException, IOException {
    NamespaceManager namespace = NamespaceManager.getInstance();;
    TupleStore tupleStore = new TupleStore(Config.getDefaultConfig());
    RuleStore ruleStore = new RuleStore(Config.getDefaultConfig(), tupleStore);
    ForwardChainer fc = new ForwardChainer(Config.getInstance(getTestResource("test_eq.yml")));
    fc.uncompressIndex();
    //TODO create a test
    TupleStore tupleconstructor5 = new TupleStore(false, true, true, 2, 5,0,1,2, 4, 2, namespace, getTestResource("default.nt"));
    ForwardChainer fc1 = new ForwardChainer(Config.getInstance(getTestResource("test_eq.yml")));
    fc1.uncompressIndex();
    //TODO create a test
  }
   **/

  @Test
  public void testcopyForwardChainer() throws FileNotFoundException, WrongFormatException, IOException {
    //test method copyForwardChainer(int noOfCores, boolean verbose)
    int noOfCores = 1;
    boolean verboseT = false;
    ForwardChainer fc = new ForwardChainer(Config.getInstance(getTestResource("test_eq.yml")));
    fc.copyForwardChainer(noOfCores, verboseT);
    assertFalse(fc.copyForwardChainer(noOfCores, verboseT) == fc);
    //cannot get access to tupleDeletionEnabled (private in TupleStore)
  }

  @Test
  public void testtupleDeletionEnabled() throws FileNotFoundException, WrongFormatException, IOException {
    //test method tupleDeletionEnabled(), returns a boolean
    int noOfCores = 1;
    boolean verboseT = false;
    boolean verboseF = false;
    boolean rdfCheck = true;
    boolean eqReduction = true;
    int minNoOfArgs = 2;
    int maxNoOfArgs = 3;
    int noOfAtoms = 2;
    int noOfTuples = 3;
    String tupleFile = getTestResource("default.nt");
    String ruleFile = getTestResource("default.eqred.rdl");
    String namespaceFile = getTestResource("default.ns");
    ForwardChainer fc = new ForwardChainer(Config.getInstance(getTestResource("test_eq.yml")));
    assertEquals(fc.tupleDeletionEnabled(), false);
  }

  @Test
  public void testenableTupleDeletion() {
    //test method enableTupleDeletion()

  }
}
