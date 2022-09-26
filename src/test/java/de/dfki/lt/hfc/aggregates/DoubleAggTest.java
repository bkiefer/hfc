package de.dfki.lt.hfc.aggregates;

import de.dfki.lt.hfc.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static de.dfki.lt.hfc.TestingUtils.checkDoubleResult;

public class DoubleAggTest {

 static TestHfc fc;

 public static String getResource(String name) {
  return TestingUtils.getTestResource("LGetLatestValues", name);
 }

 @Before
 public void init() throws Exception {
  fc = new TestHfc().init(getResource("test.child.labvalues_double.nt"));
 }

 @After
 public void cleanup() {
   if (fc != null) fc.shutdownNoExit();
 }

 @Test
 public void testDMean() throws QueryParseException, BindingTableIteratorException {
  Query q = fc.getQuery();

  BindingTable bt = q.query("SELECT ?child ?prop ?val ?t "
          + "WHERE ?child <rdf:type> <dom:Child> ?t1 "
          + "& ?child <dom:hasLabValue> ?lv ?t2 "
          + "& ?lv ?prop ?val ?t "
          + "AGGREGATE ?time = DMean ?t ");
  // check whether all expected entries were found
  checkDoubleResult(fc, bt, 55.55);
 }
 
 @Test
 public void testDSum() throws QueryParseException, BindingTableIteratorException {
  Query q = fc.getQuery();

  BindingTable bt = q.query("SELECT ?child ?prop ?val ?t "
          + "WHERE ?child <rdf:type> <dom:Child> ?t1 "
          + "& ?child <dom:hasLabValue> ?lv ?t2 "
          + "& ?lv ?prop ?val ?t "
          + "AGGREGATE ?time = DSum ?t ");
  // check whether all expected entries were found
  checkDoubleResult(fc, bt, 499.95);
 }
}