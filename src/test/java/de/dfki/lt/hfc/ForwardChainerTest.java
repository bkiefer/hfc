package de.dfki.lt.hfc;

import static org.junit.Assert.*;

import static de.dfki.lt.hfc.TestUtils.getResource;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;
import java.util.Set;

import org.junit.Test;

import gnu.trove.THashSet;

public class ForwardChainerTest {

  @Test
  public void test() {
    //test constructor
    /*ForwardChainer fc =	new ForwardChainer(100000, 500000,
    		 "/Users/krieger/Desktop/Java/HFC/hfc/src/resources/default.eqred.nt",
    		 "/Users/krieger/Desktop/Java/HFC/hfc/src/resources/default.eqred.rdl",
    		 "/Users/krieger/Desktop/Java/HFC/hfc/src/resources/default.ns");
    fc.uploadTuples("/Users/krieger/Desktop/Java/HFC/hfc/src/resources/ltworld.jena.nt");
    fc.computeClosure();
    fc.computeClosure();
    fc.shutdown();*/
    //test constructor ForwardChainer(String tupleFile, String ruleFile)
    String tupleFile = getResource("default.nt");
    String ruleFile = getResource("default.eqred.rdl");
    ForwardChainer fc = new ForwardChainer(tupleFile, ruleFile);
    assertNotNull(fc);
  }

  @Test
  public void testForwardChainer1() throws FileNotFoundException, WrongFormatException, IOException {
    //test constructor ForwardChainer(String tupleFile, String ruleFile, String namespaceFile)
    String tupleFile = getResource("default.nt");
    String ruleFile = getResource("default.eqred.rdl");
    String namespaceFile = getResource("default.ns");
    ForwardChainer fc = new ForwardChainer(tupleFile, ruleFile, namespaceFile);
    assertNotNull(fc);
  }

  @Test
  public void testForwardChainer2() {
    //test constructor ForwardChainer(int noOfAtoms, int noOfTuples, String tupleFile, String ruleFile)
    int noOfAtoms = 2;
    int noOfTuples = 2;
    String tupleFile = getResource("default.nt");
    String ruleFile = getResource("default.eqred.rdl");
    ForwardChainer fc = new ForwardChainer(noOfAtoms, noOfTuples, tupleFile, ruleFile);
    assertNotNull(fc);
  }

  @Test
  public void testForwardChainer3() throws FileNotFoundException, WrongFormatException, IOException {
    //test constructor ForwardChainer(int noOfAtoms, int noOfTuples, String tupleFile, String ruleFile, String namespaceFile) {
    int noOfAtoms = 1;
    int noOfTuples = 100;
    String tupleFile = getResource("default.nt");
    String ruleFile = getResource("default.eqred.rdl");
    String namespaceFile = getResource("default.ns");
    ForwardChainer fc = new ForwardChainer(noOfAtoms, noOfTuples, tupleFile, ruleFile, namespaceFile);
    assertNotNull(fc);
  }

  @Test
  public void testForwardChainer4() throws FileNotFoundException, WrongFormatException, IOException {
    //test constructor ForwardChainer(Namespace namespace, TupleStore tupleStore, RuleStore ruleStore)
    Namespace namespace = new Namespace(getResource("default.ns"));
    TupleStore tupleStore = new TupleStore(1, 2);
    RuleStore ruleStore = new RuleStore(namespace, tupleStore);
    ForwardChainer fc = new ForwardChainer(namespace, tupleStore, ruleStore);
    assertNotNull(fc);
  }

  @Test
  public void testForwardChainer5() throws FileNotFoundException, WrongFormatException, IOException {
    /*test constructor ForwardChainer(
     int noOfCores,
       boolean verbose,
    boolean rdfCheck,
    boolean eqReduction,
    int minNoOfArgs,
    int maxNoOfArgs,
    int noOfAtoms,
    int noOfTuples,
    String tupleFile,
    String ruleFile,
    String namespaceFile)*/
    int noOfCores = 1;
    boolean verboseT = true;
    boolean verboseF = false;
    boolean rdfCheck = true;
    boolean eqReduction = true;
    int minNoOfArgs = 2;
    int maxNoOfArgs = 3;
    int noOfAtoms = 2;
    int noOfTuples = 3;
    String tupleFile = getResource("default.nt");
    String ruleFile = getResource("default.eqred.rdl");
    String namespaceFile = getResource("default.ns");
    ForwardChainer fc = new ForwardChainer(noOfCores, verboseT, rdfCheck, eqReduction, minNoOfArgs, maxNoOfArgs,
        noOfAtoms, noOfTuples, tupleFile, ruleFile, namespaceFile);
    assertNotNull(fc);
    ForwardChainer fc0 = new ForwardChainer(noOfCores, verboseF, rdfCheck, eqReduction, minNoOfArgs, maxNoOfArgs,
        noOfAtoms, noOfTuples, tupleFile, ruleFile, namespaceFile);
    assertNotNull(fc0);

  }

  @Test
  public void testForwardChainer6() throws FileNotFoundException, WrongFormatException, IOException {
    /* test constructor ForwardChainer(
    int noOfCores,
    boolean verbose,
    int noOfAtoms,
    int noOfTuples,
    Namespace namespace,
    TupleStore tupleStore,
    RuleStore ruleStore
    ) */
    int noOfCores = 2;
    boolean verboseT = true;
    boolean verboseF = false;
    int noOfAtoms = 3;
    int noOfTuples = 4;
    Namespace namespace = new Namespace(getResource("default.ns"));
    TupleStore tupleStore = new TupleStore(2, 4);
    RuleStore ruleStore = new RuleStore(namespace, tupleStore);
    ForwardChainer fc = new ForwardChainer(noOfCores, verboseT, noOfAtoms, noOfTuples, namespace, tupleStore,
        ruleStore);
    assertNotNull(fc);
    ForwardChainer fc0 = new ForwardChainer(noOfCores, verboseF, noOfAtoms, noOfTuples, namespace, tupleStore,
        ruleStore);
    assertNotNull(fc0);
  }

  @Test
  public void testsetNoOfCores() {
    //test method setNoOfCores(int noOfCores)
    String tupleFile = getResource("default.nt");
    String ruleFile = getResource("default.eqred.rdl");
    ForwardChainer fc = new ForwardChainer(tupleFile, ruleFile);
    fc.setNoOfCores(104);
    assertEquals(fc.noOfCores, 104);
  }

  @Test
  public void testnextBlankNode() {
    //test method nextBlankNode () that returns an int
    String tupleFile = getResource("default.nt");
    String ruleFile = getResource("default.eqred.rdl");
    ForwardChainer fc = new ForwardChainer(tupleFile, ruleFile);
    assertEquals(fc.nextBlankNode(), 53);
    assertTrue(fc.nextBlankNode() > 0);
  }

  @Test
  public void testcomputeClosure() throws FileNotFoundException, WrongFormatException, IOException {
    //test method computeClosure(int noOfIterations, boolean cleanUpRepository)
    int noOfCores = 1;
    boolean verboseT = true;
    boolean verboseF = false;
    boolean rdfCheck = true;
    boolean eqReduction = true;
    int minNoOfArgs = 2;
    int maxNoOfArgs = 3;
    int noOfAtoms = 2;
    int noOfTuples = 3;

    String tupleFile = getResource("default.nt");
    String ruleFile = getResource("default.eqred.rdl");
    String namespaceFile = getResource("default.ns");
    ForwardChainer fcverboseT = new ForwardChainer(noOfCores, verboseT, rdfCheck,eqReduction, minNoOfArgs,
        maxNoOfArgs, noOfAtoms, noOfTuples, tupleFile, ruleFile,namespaceFile);
    assertEquals(true, fcverboseT.enableTupleDeletion());
    ForwardChainer fcverboseF = new ForwardChainer(noOfCores, verboseF, rdfCheck,eqReduction, minNoOfArgs,
        maxNoOfArgs, noOfAtoms, noOfTuples, tupleFile, ruleFile,namespaceFile);
    assertEquals(true, fcverboseF.enableTupleDeletion());
	}

	@Test
	public void testdeleteTuple(){
	//test method deleteTuple(int[] tuple)
    int[] tuple = new int[2];
    tuple[0] = 2;
    tuple[1] = 2;
    Namespace namespace = new Namespace(getResource("default.ns"));
    TupleStore tupleStore = new TupleStore(2, 2);
    RuleStore ruleStore = new RuleStore(namespace, tupleStore);
    ForwardChainer fc = new ForwardChainer(namespace, tupleStore, ruleStore);
    //fc.deleteTuple(tuple); Null pointer exception
	}

	@Test
	public void testdeleteTuples(){
	  //test method deleteTuples(Collection<int[]> tuples)
	  Collection<int[]> tuples = new THashSet<int[]>();
    int[] e = new int[2];
    e[0] = 1;
    e[1] = 2;
    tuples.add(e);
    Namespace namespace = new Namespace(getResource("default.ns"));
    TupleStore tupleStore = new TupleStore(1, 2);
    RuleStore ruleStore = new RuleStore(namespace, tupleStore);
    ForwardChainer fc = new ForwardChainer(namespace, tupleStore, ruleStore);
    //fc.deleteTuples(tuples); Null pointer exception
	}

	@Test
	public void testaddTuplesToRepository(){
	//test method addTuplesToRepository(Collection<int[]> tuples)
	  Collection<int[]> tuples = new THashSet<int[]>();
    int noOfCores = 1;
    boolean verboseT = true;
    boolean verboseF = false;
    boolean rdfCheck = true;
    boolean eqReduction = true;
    int minNoOfArgs = 2;
    int maxNoOfArgs = 3;
    int noOfAtoms = 2;
    int noOfTuples = 3;

    String tupleFile = getResource("default.nt");
    String ruleFile = getResource("default.eqred.rdl");
    String namespaceFile = getResource("default.ns");
    ForwardChainer fc = new ForwardChainer(noOfCores, verboseT, rdfCheck, eqReduction, minNoOfArgs, maxNoOfArgs,
        noOfAtoms, noOfTuples, tupleFile, ruleFile, namespaceFile);
    assertEquals(fc.computeClosure(1, true), true);
    ForwardChainer fc1 = new ForwardChainer(noOfCores, verboseF, rdfCheck, eqReduction, minNoOfArgs, maxNoOfArgs,
        noOfAtoms, noOfTuples, tupleFile, ruleFile, namespaceFile);
    assertEquals(fc1.computeClosure(1, true), true);
    assertEquals(fc1.computeClosure(2, false), true);
    //
    Namespace namespace = new Namespace(getResource("default.ns"));
    TupleStore tupleStore = new TupleStore(true, true, true, 2, 5, 4, 2, namespace, getResource("default.nt"));
    RuleStore ruleStore = new RuleStore(namespace, tupleStore);
    ForwardChainer fc2 = new ForwardChainer(namespace, tupleStore, ruleStore);
    assertEquals(fc2.computeClosure(1, true), false);
    assertEquals(fc2.computeClosure(1, false), false);
    //
    TupleStore tupleStore1 = new TupleStore(true, true, false, 2, 1, 2, 3, namespace, getResource("default.nt"));
    ForwardChainer fc3 = new ForwardChainer(namespace, tupleStore1, ruleStore);
    assertEquals(fc3.computeClosure(2, true), false);
  }

  @Test
  public void testcomputeClosure1() throws FileNotFoundException, WrongFormatException, IOException {
    //test method computeClosure() that returns a boolean
    Namespace namespace = new Namespace(getResource("default.ns"));
    TupleStore tupleStore = new TupleStore(true, true, true, 2, 5, 4, 2, namespace, getResource("default.nt"));
    RuleStore ruleStore = new RuleStore(namespace, tupleStore);
    ForwardChainer fc2 = new ForwardChainer(namespace, tupleStore, ruleStore);
    assertEquals(fc2.computeClosure(), false);
  }

  @Test
  public void testcomputeClosure2() throws FileNotFoundException, WrongFormatException, IOException {
    //test method computeClosure(Set<int[]> newTuples, int noOfIterations, boolean cleanUpRepository)
    Set<int[]> newTuples = new THashSet<int[]>(TupleStore.DEFAULT_HASHING_STRATEGY);
    int noOfIterations = 2;
    boolean cleanUpRepository = true;
    Namespace namespace = new Namespace(getResource("default.ns"));
    TupleStore tupleStore = new TupleStore(true, true, true, 2, 5, 4, 2, namespace, getResource("default.nt"));
    RuleStore ruleStore = new RuleStore(namespace, tupleStore);
    ForwardChainer fc = new ForwardChainer(namespace, tupleStore, ruleStore);
    //no tuples were generated, so returns false
    assertEquals(fc.computeClosure(newTuples, noOfIterations, cleanUpRepository), false);
    //set verbose to false
    int noOfCores = 1;
    boolean verboseF = false;
    boolean rdfCheck = true;
    boolean eqReduction = true;
    int minNoOfArgs = 2;
    int maxNoOfArgs = 3;
    int noOfAtoms = 2;
    int noOfTuples = 3;
    String tupleFile = getResource("default.nt");
    String ruleFile = getResource("default.eqred.rdl");
    String namespaceFile = getResource("default.ns");
    ForwardChainer fc1 = new ForwardChainer(noOfCores, verboseF, rdfCheck, eqReduction, minNoOfArgs, maxNoOfArgs,
        noOfAtoms, noOfTuples, tupleFile, ruleFile, namespaceFile);
    assertEquals(fc1.computeClosure(newTuples, noOfIterations, cleanUpRepository), false);
    //newTuples not empty:
    Set<int[]> newTuplesfull = new THashSet<int[]>(TupleStore.DEFAULT_HASHING_STRATEGY);
    int[] toadd = new int[2];
    toadd[0] = 0;
    toadd[1] = 1;
    newTuplesfull.add(toadd);
    ForwardChainer fc2 = new ForwardChainer(namespace, tupleStore, ruleStore);
    assertEquals(fc2.computeClosure(newTuplesfull, 1, false), false);
    assertEquals(fc2.computeClosure(newTuplesfull, 1, true), false);
  }

  @Test
  public void testcomputeClosure3() throws FileNotFoundException, WrongFormatException, IOException {
    //test method computeClosure(Set<int[]> newTuples)
    Set<int[]> newTuples = new THashSet<int[]>(TupleStore.DEFAULT_HASHING_STRATEGY);
    int noOfCores = 1;
    boolean verboseF = false;
    boolean rdfCheck = true;
    boolean eqReduction = true;
    int minNoOfArgs = 2;
    int maxNoOfArgs = 3;
    int noOfAtoms = 2;
    int noOfTuples = 3;
    String tupleFile = getResource("default.nt");
    String ruleFile = getResource("default.eqred.rdl");
    String namespaceFile = getResource("default.ns");
    ForwardChainer fc = new ForwardChainer(noOfCores, verboseF, rdfCheck, eqReduction, minNoOfArgs, maxNoOfArgs,
        noOfAtoms, noOfTuples, tupleFile, ruleFile, namespaceFile);
    assertEquals(fc.computeClosure(newTuples), false);
  }

  @Test
  public void testuploadNamespaces() throws FileNotFoundException, WrongFormatException, IOException {
    //test method uploadNamespaces(String filename)
    String filename = getResource("default.ns");
    String tupleFile = getResource("default.nt");
    String ruleFile = getResource("default.eqred.rdl");
    ForwardChainer fc = new ForwardChainer(tupleFile, ruleFile);
    fc.uploadNamespaces(filename);
    //TODO create a test
  }

  @Test
  public void testuploadTuples() throws FileNotFoundException, WrongFormatException, IOException {
    //test method uploadTuples(string filename)
    String filename = getResource("default.nt");
    Namespace namespace = new Namespace(getResource("default.ns"));
    TupleStore tupleStore = new TupleStore(1, 2);
    RuleStore ruleStore = new RuleStore(namespace, tupleStore);
    ForwardChainer fc = new ForwardChainer(namespace, tupleStore, ruleStore);
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

    Namespace namespace = new Namespace(getResource("default.ns"));
    TupleStore tupleStore = new TupleStore(1, 2);
    RuleStore ruleStore = new RuleStore(namespace, tupleStore);
    ForwardChainer fc = new ForwardChainer(namespace, tupleStore, ruleStore);
    assertEquals(false, fc.addTuplesToRepository(tuples));
  }

  @Test
  public void testremoveTuplesFromRepository(){
    //test method removeTuplesFromRepository(Collection<int[]> tuples)
    Collection<int[]> tuples = new THashSet<int[]>();

    Namespace namespace = new Namespace(getResource("default.ns"));
    TupleStore tupleStore = new TupleStore(1, 2);
    RuleStore ruleStore = new RuleStore(namespace, tupleStore);
    ForwardChainer fc = new ForwardChainer(namespace, tupleStore, ruleStore);
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

    Namespace namespace = new Namespace(getResource("default.ns"));
    TupleStore tupleStore = new TupleStore(1, 2);
    RuleStore ruleStore = new RuleStore(namespace, tupleStore);
    ForwardChainer fc = new ForwardChainer(namespace, tupleStore, ruleStore);
    assertEquals(false, fc.removeTuplesFromRepository(tuples));
  }

  @Test
  public void testdeleteTuplesFromRepository(){
    //test method deleteTuplesFromRepository(Collection<int[]> tuples)
    Collection<int[]> tuples = new THashSet<int[]>();
    int[] e = new int[2];
    e[0] = 1;
    e[1] = 2;
    tuples.add(e);
    Namespace namespace = new Namespace(getResource("default.ns"));
    TupleStore tupleStore = new TupleStore(1, 2);
    RuleStore ruleStore = new RuleStore(namespace, tupleStore);
    ForwardChainer fc = new ForwardChainer(namespace, tupleStore, ruleStore);
    assertEquals(false, fc.deleteTuplesFromRepository(tuples));
  }

  @Test
  public void testcomputeClosureFromRepository(){
    //test method computeClosureFromRepository()
    Namespace namespace = new Namespace(getResource("default.ns"));
    TupleStore tupleStore = new TupleStore(1, 2);
    RuleStore ruleStore = new RuleStore(namespace, tupleStore);
    ForwardChainer fc = new ForwardChainer(namespace, tupleStore, ruleStore);
    assertEquals(false, fc.computeClosure());
  }

  /* Don't know where this belongs
    Namespace namespace = new Namespace(getResource("default.ns"));
    TupleStore tupleStore = new TupleStore(1, 2);
    RuleStore ruleStore = new RuleStore(namespace, tupleStore);
    ForwardChainer fc = new ForwardChainer(namespace, tupleStore, ruleStore);
    fc.removeTuples(tuples);
    //TODO create a test
  }
  */

  @Test
  public void testuploadRules() throws FileNotFoundException, WrongFormatException, IOException {
    //test method uploadRules(String filename)
    String filename = getResource("default.eqred.rdl");
    Namespace namespace = new Namespace(getResource("default.ns"));
    TupleStore tupleStore = new TupleStore(1, 2);
    RuleStore ruleStore = new RuleStore(namespace, tupleStore);
    ForwardChainer fc = new ForwardChainer(namespace, tupleStore, ruleStore);
    fc.uploadRules(filename);
    //TODO create a test
  }

  @Test
  public void testshutdown() throws FileNotFoundException, WrongFormatException, IOException {
    //test method shutdown()
    int noOfCores = 1;
    boolean verboseT = true;
    boolean verboseF = false;
    boolean rdfCheck = true;
    boolean eqReduction = true;
    int minNoOfArgs = 2;
    int maxNoOfArgs = 3;
    int noOfAtoms = 2;
    int noOfTuples = 3;
    String tupleFile = getResource("default.nt");
    String ruleFile = getResource("default.eqred.rdl");
    String namespaceFile = getResource("default.ns");
    ForwardChainer fcverboseF = new ForwardChainer(noOfCores, verboseF, rdfCheck, eqReduction, minNoOfArgs, maxNoOfArgs,
        noOfAtoms, noOfTuples, tupleFile, ruleFile, namespaceFile);
    ForwardChainer fcverboseT = new ForwardChainer(noOfCores, verboseT, rdfCheck, eqReduction, minNoOfArgs, maxNoOfArgs,
        noOfAtoms, noOfTuples, tupleFile, ruleFile, namespaceFile);
    //How to check? It just exits the system, so other tests are not executed
    //fcverboseT.shutdown();
    //fcverboseF.shutdown();
  }

  @Test
  public void testshutdownNoExit() throws FileNotFoundException, WrongFormatException, IOException {
    //test method shutdownNoExit()
    int noOfCores = 1;
    boolean verboseT = true;
    boolean verboseF = false;
    boolean rdfCheck = true;
    boolean eqReduction = true;
    int minNoOfArgs = 2;
    int maxNoOfArgs = 3;
    int noOfAtoms = 2;
    int noOfTuples = 3;
    String tupleFile = getResource("default.nt");
    String ruleFile = getResource("default.eqred.rdl");
    String namespaceFile = getResource("default.ns");
    ForwardChainer fcverboseF = new ForwardChainer(noOfCores, verboseF, rdfCheck, eqReduction, minNoOfArgs, maxNoOfArgs,
        noOfAtoms, noOfTuples, tupleFile, ruleFile, namespaceFile);
    ForwardChainer fcverboseT = new ForwardChainer(noOfCores, verboseT, rdfCheck, eqReduction, minNoOfArgs, maxNoOfArgs,
        noOfAtoms, noOfTuples, tupleFile, ruleFile, namespaceFile);
    fcverboseF.shutdownNoExit();
    fcverboseT.shutdownNoExit();
    //DOTO create a test
  }

  @Test
  public void testcompress() {
    //test method compress(int level)
    int level0 = 0;
    int level1 = 1;
    int level2 = 2;
    int level3 = 3;
    int levelx = 12900;
    String tupleFile = getResource("default.nt");
    String ruleFile = getResource("default.eqred.rdl");
    ForwardChainer fc = new ForwardChainer(tupleFile, ruleFile);
    fc.compress(level0);
    fc.compress(level1);
    fc.compress(level2);
    fc.compress(level3);
    fc.compress(levelx);
    //TODO create tests for the above 5 cases
  }

  @Test
  public void testuncompressIndex() throws FileNotFoundException, WrongFormatException, IOException {
    Namespace namespace = new Namespace(getResource("default.ns"));
    TupleStore tupleStore = new TupleStore(1, 2);
    RuleStore ruleStore = new RuleStore(namespace, tupleStore);
    ForwardChainer fc = new ForwardChainer(namespace, tupleStore, ruleStore);
    fc.uncompressIndex();
    //TODO create a test
    TupleStore tupleconstructor5 = new TupleStore(true, true, true, 2, 5, 4, 2, namespace, getResource("default.nt"));
    ForwardChainer fc1 = new ForwardChainer(namespace, tupleconstructor5, ruleStore);
    fc1.uncompressIndex();
    //TODO create a test
  }

  @Test
  public void testcopyForwardChainer() throws FileNotFoundException, WrongFormatException, IOException {
    //test method copyForwardChainer(int noOfCores, boolean verbose)
    int noOfCores = 1;
    boolean verboseT = true;
    boolean verboseF = false;
    boolean rdfCheck = true;
    boolean eqReduction = true;
    int minNoOfArgs = 2;
    int maxNoOfArgs = 3;
    int noOfAtoms = 2;
    int noOfTuples = 3;
    String tupleFile = getResource("default.nt");
    String ruleFile = getResource("default.eqred.rdl");
    String namespaceFile = getResource("default.ns");
    ForwardChainer fc = new ForwardChainer(noOfCores, verboseT, rdfCheck, eqReduction, minNoOfArgs, maxNoOfArgs,
        noOfAtoms, noOfTuples, tupleFile, ruleFile, namespaceFile);
    fc.copyForwardChainer(noOfCores, verboseT);
    assertFalse(fc.copyForwardChainer(noOfCores, verboseT) == fc);
    //cannot get access to tupleDeletionEnabled (private in TupleStore)
  }

  @Test
  public void testtupleDeletionEnabled() throws FileNotFoundException, WrongFormatException, IOException {
    //test method tupleDeletionEnabled(), returns a boolean
    int noOfCores = 1;
    boolean verboseT = true;
    boolean verboseF = false;
    boolean rdfCheck = true;
    boolean eqReduction = true;
    int minNoOfArgs = 2;
    int maxNoOfArgs = 3;
    int noOfAtoms = 2;
    int noOfTuples = 3;
    String tupleFile = getResource("default.nt");
    String ruleFile = getResource("default.eqred.rdl");
    String namespaceFile = getResource("default.ns");
    ForwardChainer fc = new ForwardChainer(noOfCores, verboseT, rdfCheck, eqReduction, minNoOfArgs, maxNoOfArgs,
        noOfAtoms, noOfTuples, tupleFile, ruleFile, namespaceFile);
    assertEquals(fc.tupleDeletionEnabled(), false);
  }

  @Test
  public void testenableTupleDeletion() {
    //test method enableTupleDeletion()

  }
}
