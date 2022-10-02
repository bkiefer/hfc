package de.dfki.lt.hfc.aggregates;

import de.dfki.lt.hfc.*;
import de.dfki.lt.hfc.io.QueryParseException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static de.dfki.lt.hfc.TestingUtils.checkResult;

public class LargeAggTest {

 static TestHfc fc;

 public static String getResource(String name) {
  return TestingUtils.getTestResource("LGetLatestValues", name);
 }

 @Before
 public void init() throws Exception {
  fc = new TestHfc().init(getResource("test.child.labvalues.nt"));
 }


 @After
 public void cleanup() {
  fc.shutdownNoExit();
 }

 @Test
 public void testLMax() throws QueryParseException, BindingTableIteratorException {
  Query q = fc.getQuery();


  String[][] expected = {
          {"\"5577\"^^<xsd:long>"}
  };

  BindingTable bt = q.query("SELECT ?child ?prop ?val ?t "
          + "WHERE ?child <rdf:type> <dom:Child> ?t1 "
          + "& ?child <dom:hasLabValue> ?lv ?t2 "
          + "& ?lv ?prop ?val ?t "
          + "AGGREGATE ?time = LMax ?t ");
  // check whether all expected entries were found
  checkResult(fc, bt, expected, "?time");
 }
 
 @Test
 public void testLMin() throws QueryParseException, BindingTableIteratorException {
  Query q = fc.getQuery();


  String[][] expected = {
          {"\"5544\"^^<xsd:long>"}
  };

  BindingTable bt = q.query("SELECT ?child ?prop ?val ?t "
          + "WHERE ?child <rdf:type> <dom:Child> ?t1 "
          + "& ?child <dom:hasLabValue> ?lv ?t2 "
          + "& ?lv ?prop ?val ?t "
          + "AGGREGATE ?time = LMin ?t ");
  // check whether all expected entries were found
  checkResult(fc, bt, expected, "?time");
 }
 
 @Test
 public void testLMean() throws QueryParseException, BindingTableIteratorException {
  Query q = fc.getQuery();


  String[][] expected = {
          {"\"5555\"^^<xsd:long>"}
  };

  BindingTable bt = q.query("SELECT ?child ?prop ?val ?t "
          + "WHERE ?child <rdf:type> <dom:Child> ?t1 "
          + "& ?child <dom:hasLabValue> ?lv ?t2 "
          + "& ?lv ?prop ?val ?t "
          + "AGGREGATE ?time = LMean ?t ");
  // check whether all expected entries were found
  checkResult(fc, bt, expected, "?time");
 }
 
 @Test
 public void testLSum() throws QueryParseException, BindingTableIteratorException {
  Query q = fc.getQuery();


  String[][] expected = {
          {"\"49995\"^^<xsd:long>"}
  };

  BindingTable bt = q.query("SELECT ?child ?prop ?val ?t "
          + "WHERE ?child <rdf:type> <dom:Child> ?t1 "
          + "& ?child <dom:hasLabValue> ?lv ?t2 "
          + "& ?lv ?prop ?val ?t "
          + "AGGREGATE ?time = LSum ?t ");
  // check whether all expected entries were found
  checkResult(fc, bt, expected, "?time");
 }
}