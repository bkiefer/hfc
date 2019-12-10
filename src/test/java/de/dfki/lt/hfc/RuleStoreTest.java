package de.dfki.lt.hfc;

import static de.dfki.lt.hfc.TestingUtils.*;
import static org.junit.Assert.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

public class RuleStoreTest {

  @Test
  public void testRuleStore() throws FileNotFoundException, WrongFormatException, IOException {
    //test constructor that takes NamespaceManager namespace, TupleStore tupleStore
    TupleStore tupleStore = new TupleStore(Config.getDefaultConfig());
    RuleStore rs1 = new RuleStore(Config.getDefaultConfig(),tupleStore);
    assertNotNull(rs1);
  }

  @Test
  public void testRuleStore1() throws FileNotFoundException, WrongFormatException, IOException {
    //test constructor that takes NamespaceManager namespace, TupleStore tupleStore, String ruleFile
    TupleStore tupleStore = new TupleStore(Config.getDefaultConfig());
    String ruleFile = new String(getTestResource("default.test.rdl"));
    RuleStore rs2 = new RuleStore(false, false, 2, 6, tupleStore, ruleFile);
    assertNotNull(rs2);
  }

  @Test
  public void testRuleStore2() throws FileNotFoundException, WrongFormatException, IOException {
    /*test constructor that takes RuleStore(boolean verbose, boolean rdfCheck, int minNoOfArgs, int maxNoOfArgs,
    NamespaceManager namespace, TupleStore tupleStore, String ruleFile)*/
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
    String ruleFile = getTestResource("default.test.rdl");
    File file0 = new File(getTestResource("default.test.rdl"));
    RuleStore rstest = new RuleStore(false, true, 2, 3, tupleStore, ruleFile);
    rstest.writeRules(getTempFile("fileRules"));
    //test for case verbose != true
    RuleStore rsverbosefalse = new RuleStore(false, true, 2, 3, tupleStore, ruleFile);
    rsverbosefalse.writeRules(getTempFile("fileRules1"));

    File file1 = new File(getTempFile("fileRules"));
    File file2 = new File(getTempFile("fileRules1"));
    assertTrue(FileUtils.contentEquals(file1, file2));
    RuleStore reload =  new RuleStore(false, true, 2, 3, tupleStore, getTempFile("fileRules1"));
    Set<String> ruleNames = new HashSet<String>();
    for (Rule rule : rstest.allRules){
      ruleNames.add(rule.name);
    }
    Set<String> reloadedRuleNames = new HashSet<String>();
    for (Rule rule : reload.allRules){
      reloadedRuleNames.add(rule.name);
    }
    assertEquals(reloadedRuleNames, ruleNames);
  }

  @Test
  public void testcopyRuleStore() throws FileNotFoundException, WrongFormatException, IOException {
    NamespaceManager namespace = NamespaceManager.getInstance();
    //TupleStore tupleStore = new TupleStore(2, 4);
    TupleStore ts = new TupleStore(false, true, false, 2, 5,0,1,2, 4, 2, namespace, getTestResource("default.nt"));
    RuleStore rs = new RuleStore(Config.getDefaultConfig(),ts);
    RuleStore copy = rs.copyRuleStore(ts);
    assertFalse(rs == copy);
    assertTrue(rs.tupleStore == copy.tupleStore);
    assertTrue(rs.rdfCheck == copy.rdfCheck);
  }

}
