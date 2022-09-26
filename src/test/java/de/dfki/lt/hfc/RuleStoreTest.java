package de.dfki.lt.hfc;

import static de.dfki.lt.hfc.TestingUtils.getRuleStore;
import static de.dfki.lt.hfc.TestingUtils.getRuleStoreConfig;
import static de.dfki.lt.hfc.TestingUtils.getTempFile;
import static de.dfki.lt.hfc.TestingUtils.getTestResource;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

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
    RuleStore rs1 = getRuleStore(Config.getDefaultConfig());
    assertNotNull(rs1);
  }

  @Test
  public void testRuleStore1() throws FileNotFoundException, WrongFormatException, IOException {
    //test constructor that takes NamespaceManager namespace, TupleStore tupleStore, String ruleFile
    String ruleFile = new String(getTestResource("default.test.rdl"));
    RuleStore rs2 = getRuleStoreConfig(false, 2, 6, ruleFile);
    assertNotNull(rs2);
  }

  @Test
  public void testRuleStore2() throws FileNotFoundException, WrongFormatException, IOException {
    /*test constructor that takes RuleStore(boolean verbose, boolean rdfCheck, int minNoOfArgs, int maxNoOfArgs,
    NamespaceManager namespace, TupleStore tupleStore, String ruleFile)*/
    String ruleFile = new String(getTestResource("default.test.rdl"));
    RuleStore rs3 = getRuleStoreConfig(true, 2, 3, ruleFile);
    assertNotNull(rs3);
    RuleStore rs4 = getRuleStoreConfig(true, 2, 5, ruleFile);
    assertNotNull(rs4);
    RuleStore rs5 = getRuleStoreConfig(false, 2, 6, ruleFile);
    assertNotNull(rs5);
    RuleStore rs6 = getRuleStoreConfig(false, 1, 4, ruleFile);
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
    String ruleFile = getTestResource("default.test.rdl");
    RuleStore rstest = getRuleStoreConfig(true, 2, 3, ruleFile);
    rstest.writeRules(getTempFile("fileRules"));
    //test for case verbose != true
    RuleStore rsverbosefalse = getRuleStoreConfig(true, 2, 3, ruleFile);
    rsverbosefalse.writeRules(getTempFile("fileRules1"));

    File file1 = new File(getTempFile("fileRules"));
    File file2 = new File(getTempFile("fileRules1"));
    assertTrue(FileUtils.contentEquals(file1, file2));
    RuleStore reload =  getRuleStoreConfig(true, 2, 3, getTempFile("fileRules1"));
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

  /** TODO: ***copy***
  @Test
  public void testcopyRuleStore() throws FileNotFoundException, WrongFormatException, IOException {
    RuleStore rs = getRuleStore(Config.getDefaultConfig());
    RuleStore copy = rs.copyRuleStore(ts);
    assertFalse(rs == copy);
    assertTrue(rs.tupleStore == copy.tupleStore);
    assertTrue(rs.rdfCheck == copy.rdfCheck);
  }
  */

}
