package de.dfki.lt.hfc;

import static de.dfki.lt.hfc.TestingUtils.*;
import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import org.junit.Test;

public class RuleStoreTest {

  @Test
  public void testRuleStore() throws FileNotFoundException, WrongFormatException, IOException {
    //test constructor that takes Namespace namespace, TupleStore tupleStore
    TupleStore tupleStore = new TupleStore(Config.getDefaultConfig());
    RuleStore rs1 = new RuleStore(Config.getDefaultConfig(),tupleStore);
    assertNotNull(rs1);
  }

  @Test
  public void testRuleStore1() throws FileNotFoundException, WrongFormatException, IOException {
    //test constructor that takes Namespace namespace, TupleStore tupleStore, String ruleFile
    TupleStore tupleStore = new TupleStore(Config.getDefaultConfig());
    String ruleFile = new String(getTestResource("default.test.rdl"));
    RuleStore rs2 = new RuleStore(false, false, 2, 6, tupleStore, ruleFile);
    assertNotNull(rs2);
  }

  @Test
  public void testRuleStore2() throws FileNotFoundException, WrongFormatException, IOException {
    /*test constructor that takes RuleStore(boolean verbose, boolean rdfCheck, int minNoOfArgs, int maxNoOfArgs,
    Namespace namespace, TupleStore tupleStore, String ruleFile)*/
    TupleStore tupleStore = new TupleStore(Config.getDefaultConfig());
    String ruleFile = new String(getTestResource("default.test.rdl"));
    RuleStore rs3 = new RuleStore(false, true, 2, 3, tupleStore, ruleFile);
    assertNotNull(rs3);
    RuleStore rs4 = new RuleStore(false, true, 2, 5, tupleStore, ruleFile);
    assertNotNull(rs4);
    RuleStore rs5 = new RuleStore(false, false, 2, 6, tupleStore, ruleFile);
    assertNotNull(rs5);
    RuleStore rs6 = new RuleStore(false, false, 1, 4, tupleStore, ruleFile);
    assertNotNull(rs6);
  }

  @Test
  public void testisFunctionalVariable1() {
    //test method that takes String literal
    assertFalse(RuleStore.isFunctionalVariable("literal"));
    assertTrue(RuleStore.isFunctionalVariable("?dgdfgdfgj"));
    assertFalse(RuleStore.isFunctionalVariable("??jjgjg"));
    assertFalse(RuleStore.isFunctionalVariable("f?jddgjffjg"));
  }

  @Test
  public void testisFunctionalVariable2() {
    //test method that takes int id
    assertFalse(RuleStore.isFunctionalVariable(1));
    assertTrue(RuleStore.isFunctionalVariable(-1));
    assertFalse(RuleStore.isFunctionalVariable(-1000000001));
  }

  @Test
  public void testisRelationalVariable() {
    //test method that takes int id
    assertFalse(RuleStore.isRelationalVariable(1));
  }

  @Test
  public void testwriteRules() throws FileNotFoundException, WrongFormatException, IOException {
    TupleStore tupleStore = new TupleStore(Config.getDefaultConfig());
    String ruleFile = new String(getTestResource("default.test.rdl"));
    RuleStore rstest = new RuleStore(false, true, 2, 3, tupleStore, ruleFile);
    rstest.writeRules(getTempFile("fileRules"));
    // TODO: this test is nonsense, it tests if the string is empty, which is
    // never the case
    assertFalse("fileRules".isEmpty());
    //test for case verbose != true
    RuleStore rsverbosefalse = new RuleStore(false, true, 2, 3, tupleStore, ruleFile);
    rsverbosefalse.writeRules(getTempFile("fileRules1"));
    // TODO: this test is nonsense, it tests if the string is empty, which is
    // never the case
    assertFalse("fileRules1".isEmpty());
  }

  @Test
  public void testcopyRuleStore() throws FileNotFoundException, WrongFormatException, IOException {
    Namespace namespace = new Namespace();
    //TupleStore tupleStore = new TupleStore(2, 4);
    TupleStore ts = new TupleStore(false, true, false, 2, 5,0,1,2, 4, 2, namespace, getTestResource("default.nt"));
    RuleStore rs = new RuleStore(Config.getDefaultConfig(),ts);
    rs.copyRuleStore(ts);
    // TODO: this test is nonsense, it tests that the two rule stores are not
    // the same object
    assertFalse(rs == rs.copyRuleStore(ts));
    //TODO one more branch
  }

}
