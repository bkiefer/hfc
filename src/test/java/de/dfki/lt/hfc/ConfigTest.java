package de.dfki.lt.hfc;

import org.junit.Test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ConfigTest {

 private static String getResource(String name) {
  return TestingUtils.getTestResource("Index_Parsing", name);
 }

 @Test
 public void test_ValidConfig() throws IOException {
  Config config = Config.getDefaultConfig();
  assertNotNull(config);
  assertEquals(true, config.isVerbose());
  assertEquals("UTF-8", config.getCharacterEncoding());
  assertEquals(4, config.getNoOfCores());
  assertEquals(500000, config.getNoOfTuples());
  assertEquals(100000, config.getNoOfAtoms());
  assertEquals(false, config.isEqReduction());
  assertEquals(false, config.isGarbageCollection());
  assertEquals(true, config.isCleanupRepository());
  assertEquals(true, config.isShortIsDefault());
  assertEquals("<resources>/default.nt", config.getTupleFiles().get(0));
 }

 @Test(expected = java.lang.NullPointerException.class)
 public void test_InvalidConfig() throws FileNotFoundException {
  @SuppressWarnings("unused")
  Config config = Config.getInstance(TestingUtils.getTestResource("invalid.yml"));
 }

 @Test
 public void test_ConfigWithIndex() throws FileNotFoundException {
  Config config = Config.getInstance(getResource("index_Parsing1.yml"));
  assertNotNull(config);
 }

 @Test
 public void test_getMapping() throws FileNotFoundException {
  @SuppressWarnings("rawtypes")
  Map mapping = Config.getMapping(getResource("index_Parsing1.yml"));
  // we only test some parts of the mapping here
  assertEquals(false, mapping.get(Config.VERBOSE));
  List<String> expected = new ArrayList<>();
  expected.add("./src/test/data/Index_Parsing/transaction0.rdl");
  assertEquals(expected, mapping.get(Config.RULEFILES));
 }

 @Test
 public void test_addRuleFile() throws IOException {
  Config config = Config.getDefaultConfig();
  config.addRuleFile("test.rdl");
  assertTrue(config.getRuleFiles().contains("test.rdl"));
 }

 @Test
 public void test_addRuleFiles() throws IOException {
  Config config = Config.getDefaultConfig();
  List<String> rulefiles = new ArrayList<>();
  rulefiles.add("test1.rdl");
  rulefiles.add("test2.rdl");
  config.addRuleFiles(rulefiles);
  assertTrue(config.getRuleFiles().contains("test1.rdl"));
  assertTrue(config.getRuleFiles().contains("test2.rdl"));

 }

 @Test
 public void test_addTupleFile() throws IOException {
  Config config = Config.getDefaultConfig();
  config.addTupleFile("test.nt");
  assertTrue(config.getTupleFiles().contains("test.nt"));
 }

 @Test
 public void test_addTupleFiles() throws IOException {
  Config config = Config.getDefaultConfig();
  List<String> tupleFiles = new ArrayList<>();
  tupleFiles.add("test1.nt");
  tupleFiles.add("test2.nt");
  config.addTupleFiles(tupleFiles);
  assertTrue(config.getTupleFiles().contains("test1.nt"));
  assertTrue(config.getTupleFiles().contains("test2.nt"));

 }

 @Test
 public void testCopy() throws IOException {
  Config config = Config.getDefaultConfig();
  Config copy = config.getCopy(4, true);
  assertTrue(copy.isVerbose());
  assertEquals(copy.getRuleFiles(), config.getRuleFiles());
  assertEquals(copy.getTupleFiles(), config.getTupleFiles());
  assertEquals(copy.getMinArgs(), config.getMinArgs());
  assertEquals(copy.getMaxArgs(), config.getMaxArgs());
  assertEquals(copy.getIterations(), config.getIterations());
  Config indexConfig = Config.getInstance(getResource("index_Parsing1.yml"));
  Config indexCopy = indexConfig.getCopy(4, false);
  assertFalse(indexCopy.isVerbose());
  assertEquals(indexCopy.getRuleFiles(), indexConfig.getRuleFiles());
  assertEquals(indexCopy.getTupleFiles(), indexConfig.getTupleFiles());
  assertEquals(indexCopy.getMinArgs(), indexConfig.getMinArgs());
  assertEquals(indexCopy.getMaxArgs(), indexConfig.getMaxArgs());
  assertEquals(indexCopy.getIterations(), indexConfig.getIterations());
  assertNotNull(indexCopy.indexStore);
 }

 @Test
 public void testShortIsDefault() throws IOException {
  Config config = Config.getDefaultConfig();
  assertTrue(config.isShortIsDefault());
 }

 @Test
 public void testSetShortIsDefault() throws IOException {
  Config config = Config.getDefaultConfig();
  assertTrue(config.isShortIsDefault());
  config.setShortIsDefault(false);
  assertFalse(config.isShortIsDefault());
  assertFalse(config.namespace.isShortIsDefault());
 }

}
