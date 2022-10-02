package de.dfki.lt.hfc;

import static de.dfki.lt.hfc.TestingUtils.checkResult;
import static de.dfki.lt.hfc.TestingUtils.printExpected;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.dfki.lt.hfc.io.QueryParseException;

/**
 * @author Christian Willms - Date: 14.09.17 10:25.
 * @version 14.09.17
 */
public class QueryTestEqRed {

 static TestHfc fc;

 private static String getResource(String name) {
  return TestingUtils.getTestResource("LGetLatestValues", name);
 }

 @Before
 public void setUp() throws IOException, WrongFormatException {
  TestConfig config = TestConfig.getDefaultEqRedConfig();
  config.addNamespace("pal", "http://www.dfki.de/lt/onto/pal.owl#");
  config.addNamespace("dom", "http://www.dfki.de/lt/onto/dom.owl#");
  config.put("shortIsDefault", true);
  fc = new TestHfc(config);
  fc.uploadTuples(getResource("test.child.labvalues.nt"));
 }

 @After
 public void tearDown() {
  fc.shutdownNoExit();
 }

 private void checkInvalid(String[] invalid) {
   Query query = fc.getQuery();
   for (String q: invalid) {
     try {
       query.query(q);
       assertFalse(q, true);
     } catch (Throwable qex) {
       assertTrue(q, true);
     }
   }
 }

 @Test
 /** Test select and selectall with equivalence reduction */
 public void query_SELECTALL_WHERE() throws QueryParseException {
  String[][] expected = {{"<pal:labval22>"}, {"<pal:labval33>"},
      {"<pal:labval22>"}, {"<pal:labval33>"},
      {"<dom:labval22>"}, {"<dom:labval33>"},
      {"<dom:labval22>"}, {"<dom:labval33>"}};
  Query query = fc.getQuery();
  BindingTable bt = query.query("SELECTALL ?s WHERE ?s <dom:bsl> ?o ?t");
  checkResult(fc, bt, expected, "?s");
  bt = query.query("SELECTALL DISTINCT ?s WHERE ?s <dom:bsl> ?o ?t");
  assertEquals(4, bt.size());
 }

}