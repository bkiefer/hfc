package de.dfki.lt.hfc.aggregates;

import static de.dfki.lt.hfc.TestingUtils.checkResult;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.dfki.lt.hfc.BindingTable;
import de.dfki.lt.hfc.BindingTableIteratorException;
import de.dfki.lt.hfc.Query;
import de.dfki.lt.hfc.QueryParseException;
import de.dfki.lt.hfc.TestHfc;
import de.dfki.lt.hfc.TestingUtils;

public class DateTimeAggTest {

 static TestHfc fc;

 public static String getResource(String name) {
  return TestingUtils.getTestResource("LGetLatestValues", name);
 }

 @Before
 public void init() throws Exception {
  fc = new TestHfc().init(getResource("test.child.labvalues_DT.nt"));
 }


 @After
 public void cleanup() {
  fc.shutdownNoExit();
 }

 @Test
 public void testDTMax() throws QueryParseException, BindingTableIteratorException {
  Query q = fc.getQuery();

  String[][] expected = {
          {"\"0002-01-00T00:00:00.0\"^^<xsd:dateTime>"}
  };

  BindingTable bt = q.query("SELECT ?child ?prop ?val ?t "
          + "WHERE ?child <rdf:type> <dom:Child> ?t1 "
          + "& ?child <dom:hasLabValue> ?lv ?t2 "
          + "& ?lv ?prop ?val ?t "
          + "AGGREGATE ?time = DTMax ?t ");
  // check whether all expected entries were found
  checkResult(fc, bt, expected, "?time");
 }
 
 @Test
 public void testDTMin() throws QueryParseException, BindingTableIteratorException {
  Query q = fc.getQuery();

  String[][] expected = {
          {"\"0002-00-00T00:00:00.0\"^^<xsd:dateTime>"}
  };

  BindingTable bt = q.query("SELECT ?child ?prop ?val ?t "
          + "WHERE ?child <rdf:type> <dom:Child> ?t1 "
          + "& ?child <dom:hasLabValue> ?lv ?t2 "
          + "& ?lv ?prop ?val ?t "
          + "AGGREGATE ?time = DTMin ?t ");
  // check whether all expected entries were found
  checkResult(fc, bt, expected, "?time");
 }
}