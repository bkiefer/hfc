package de.dfki.lt.hfc;

import static org.junit.Assert.*;

import static de.dfki.lt.hfc.TestingUtils.getTestResource;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;
import java.util.Set;

import org.junit.Test;

import gnu.trove.set.hash.*;


public class HfcTest {


  /**
   *Test default Config;
   *
   */
   public void testHfc() throws IOException, WrongFormatException {
     Hfc fc = new Hfc(Config.getDefaultConfig());
     assertNotNull(fc);
  }

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
     System.out.println(bt.toString());
  }
  /**
  public void testHfc2() throws IOException, WrongFormatException {
     Config config = Config.getInstance(getTestResource("test.yml"));
     TupleStore tupleStore = new TupleStore(config.namespace);
     RuleStore ruleStore = new RuleStore(Config.getDefaultConfig(), tupleStore);
     Hfc fc = new Hfc(config, tupleStore, ruleStore);
     assertNotNull(fc);
  }
  **/



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
    for (int i = 0; i < fc._tupleStore.idToJavaObject.size(); i++) {
      System.out.println(i + ": " + fc._tupleStore.idToJavaObject.get(i).toString());
    }
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
	public void testaddTuplesToRepository() throws FileNotFoundException, WrongFormatException, IOException{
	//test method addTuplesToRepository(Collection<int[]> tuples)
	  Collection<int[]> tuples = new THashSet<int[]>();

    Hfc fc = new Hfc(Config.getInstance(getTestResource("test_eq.yml")));
    assertEquals(fc.computeClosure(1, true), true);
    Hfc fc1 = new Hfc(Config.getInstance(getTestResource("test_eq.yml")));
    assertEquals(fc1.computeClosure(1, true), true);
    assertEquals(fc1.computeClosure(2, false), true);
    //
    NamespaceManager namespace = NamespaceManager.getInstance();;
    TupleStore tupleStore = new TupleStore(false, true, false, 2, 5,0,1,2, 4, 2, namespace, getTestResource("default.nt"));
    RuleStore ruleStore = new RuleStore(Config.getDefaultConfig(), tupleStore);
    Hfc fc2 = new Hfc(Config.getDefaultConfig(),tupleStore, ruleStore);
    assertEquals(fc2.computeClosure(1, true), true);
    assertEquals(fc2.computeClosure(1, false), true);
    //
    TupleStore tupleStore1 = new TupleStore(false, true, false, 2, 1,0,1,2, 2, 3, namespace, getTestResource("default.nt"));
    Hfc fc3 = new Hfc(Config.getDefaultConfig(),tupleStore1, ruleStore);
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
    NamespaceManager namespace = NamespaceManager.getInstance();;
    TupleStore tupleStore = new TupleStore(false, true, false, 2, 5,0,1,2, 4, 2, namespace, getTestResource("default.nt"));
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
  public void testuploadTuples() throws FileNotFoundException, WrongFormatException, IOException {
    //test method uploadTuples(string filename)
    String filename = getTestResource("default.nt");
    Hfc fc = new Hfc(Config.getInstance(getTestResource("test_eq.yml")));
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
    Hfc fc = new Hfc(Config.getInstance(getTestResource("test_eq.yml")));
    boolean b = fc.addTuples(tuples);
    assertEquals(true, b);
  }

  @Test
  public void testremoveTuplesFromRepository() throws FileNotFoundException, WrongFormatException, IOException{
    //test method removeTuplesFromRepository(Collection<int[]> tuples)
    Collection<int[]> tuples = new THashSet<int[]>();
    Hfc fc = new Hfc(Config.getInstance(getTestResource("test_eq.yml")));
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
    Hfc fc = new Hfc(Config.getInstance(getTestResource("test_eq.yml")));
    assertEquals(false, fc.removeTuples(tuples));
  }

  @Test
  public void testdeleteTuplesFromRepository() throws FileNotFoundException, WrongFormatException, IOException{
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
  public void testcomputeClosureFromRepository() throws FileNotFoundException, WrongFormatException, IOException{
    //test method computeClosureFromRepository()
    Hfc fc = new Hfc(Config.getInstance(getTestResource("computeClosureFromRepository.yml")));
    assertEquals(false, fc.computeClosure());

//    NamespaceManager namespace = new NamespaceManager(getTestResource("default.ns"));
//    TupleStore tupleStore = new TupleStore(1, 2, namespace);
//    RuleStore ruleStore = new RuleStore(tupleStore);
//    Hfc fc = new Hfc(tupleStore, ruleStore);
//    assertEquals(false, fc.computeClosure());
  }

  /* Don't know where this belongs
    NamespaceManager namespace = NamespaceManager.getInstance();;
    TupleStore tupleStore = new TupleStore(1, 2, namespace);
    RuleStore ruleStore = new RuleStore(namespace, tupleStore);
    Hfc fc = new Hfc(tupleStore, ruleStore);
    fc.removeTuples(tuples);
    //TODO create a test
  }
  */

  @Test
  public void testuploadRules() throws FileNotFoundException, WrongFormatException, IOException {
    //test method uploadRules(String filename)
    String filename = getTestResource("default.eqred.rdl");
    Hfc fc = new Hfc(Config.getInstance(getTestResource("test_eq.yml")));
    fc.uploadRules(filename);
    //TODO create a test
  }

  @Test
  public void testshutdown() throws FileNotFoundException, WrongFormatException, IOException {
    //test method shutdown()
    Hfc fcverboseF = new Hfc(Config.getInstance(getTestResource("test_eq.yml")));
    Hfc fcverboseT = new Hfc(Config.getInstance(getTestResource("test_eq.yml")));
    //How to check? It just exits the system, so other tests are not executed
    //fcverboseT.shutdown();
    //fcverboseF.shutdown();
  }

  @Test
  public void testshutdownNoExit() throws FileNotFoundException, WrongFormatException, IOException {
    //test method shutdownNoExit()
    Hfc fcverboseF = new Hfc(Config.getInstance(getTestResource("test_eq.yml")));
    Hfc fcverboseT = new Hfc(Config.getInstance(getTestResource("test_eq.yml")));
    fcverboseT.config.setVerbose(true);
    fcverboseF.shutdownNoExit();
    fcverboseT.shutdownNoExit();
    //TODO create a test
  }

  /**
  @Test
  public void testcompress() throws FileNotFoundException, IOException, WrongFormatException {
    //test method compress(int level)
    int level0 = 0;
    int level1 = 1;
    int level2 = 2;
    int level3 = 3;
    int levelx = 12900;
    Hfc fc = new Hfc(Config.getInstance(getTestResource("test_eq.yml")));
    fc.compress(level0);
    fc.compress(level1);
    fc.compress(level2);
    fc.compress(level3);
    fc.compress(levelx);
    //TODO create tests for the above 5 cases
  }
  **/

  /**
  @Test
  public void testuncompressIndex() throws FileNotFoundException, WrongFormatException, IOException {
    NamespaceManager namespace = NamespaceManager.getInstance();;
    TupleStore tupleStore = new TupleStore(Config.getDefaultConfig());
    RuleStore ruleStore = new RuleStore(Config.getDefaultConfig(), tupleStore);
    Hfc fc = new Hfc(Config.getInstance(getTestResource("test_eq.yml")));
    fc.uncompressIndex();
    //TODO create a test
    TupleStore tupleconstructor5 = new TupleStore(false, true, true, 2, 5,0,1,2, 4, 2, namespace, getTestResource("default.nt"));
    Hfc fc1 = new Hfc(Config.getInstance(getTestResource("test_eq.yml")));
    fc1.uncompressIndex();
    //TODO create a test
  }
   **/

  @Test
  public void testcopyHfc() throws FileNotFoundException, WrongFormatException, IOException {
    //test method copyHfc(int noOfCores, boolean verbose)
    int noOfCores = 1;
    boolean verboseT = false;
    Hfc fc = new Hfc(Config.getInstance(getTestResource("test_eq.yml")));
    fc.copyHFC(noOfCores, verboseT);
    assertFalse(fc.copyHFC(noOfCores, verboseT) == fc);
    //cannot get access to tupleDeletionEnabled (private in TupleStore)
  }

  @Test
  public void testtupleDeletionEnabled() throws FileNotFoundException, WrongFormatException, IOException {
    //test method tupleDeletionEnabled(), returns a boolean

    Hfc fc = new Hfc(Config.getInstance(getTestResource("test_eq.yml")));
    assertEquals(fc.tupleDeletionEnabled(), false);
  }

  @Test
  public void testenableTupleDeletion() {
    //test method enableTupleDeletion()

  }
}
