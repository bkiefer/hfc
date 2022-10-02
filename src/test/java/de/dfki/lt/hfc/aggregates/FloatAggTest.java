package de.dfki.lt.hfc.aggregates;

import de.dfki.lt.hfc.*;
import de.dfki.lt.hfc.io.QueryParseException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static de.dfki.lt.hfc.TestingUtils.checkResult;

public class FloatAggTest {

 static TestHfc fc;

 public static String getResource(String name) {
  return TestingUtils.getTestResource("LGetLatestValues", name);
 }

 @Before
 public void init() throws Exception {
  fc = new TestHfc().init(getResource("test.child.labvalues_float.nt"));
 }


 @After
 public void cleanup() {
  fc.shutdownNoExit();
 }

 @Test
 public void testFMean() throws QueryParseException, BindingTableIteratorException {
  Query q = fc.getQuery();

  String[][] expected = {
          {"\"5555.0\"^^<xsd:float>"}
  };

  BindingTable bt = q.query("SELECT ?child ?prop ?val ?t "
          + "WHERE ?child <rdf:type> <dom:Child> ?t1 "
          + "& ?child <dom:hasLabValue> ?lv ?t2 "
          + "& ?lv ?prop ?val ?t "
          + "AGGREGATE ?time = FMean ?t ");
  // check whether all expected entries were found
  checkResult(fc, bt, expected, "?time");
 }
 
 @Test
 public void testFSum() throws QueryParseException, BindingTableIteratorException {
  Query q = fc.getQuery();

  String[][] expected = {
          {"\"49995.0\"^^<xsd:float>"}
  };

  BindingTable bt = q.query("SELECT ?child ?prop ?val ?t "
          + "WHERE ?child <rdf:type> <dom:Child> ?t1 "
          + "& ?child <dom:hasLabValue> ?lv ?t2 "
          + "& ?lv ?prop ?val ?t "
          + "AGGREGATE ?time = FSum ?t ");
  // check whether all expected entries were found
  checkResult(fc, bt, expected, "?time");
 }
}