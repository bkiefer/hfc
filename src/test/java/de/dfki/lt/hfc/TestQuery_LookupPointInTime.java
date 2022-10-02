package de.dfki.lt.hfc;


import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import de.dfki.lt.hfc.io.QueryParseException;

import static de.dfki.lt.hfc.TestingUtils.checkResult;
import static junit.framework.TestCase.fail;

/**
 * Created by christian on 29/05/17.
 */
public class TestQuery_LookupPointInTime {

 static Hfc hfc;

 private static String getResource(String name) {
  return TestingUtils.getTestResource("Query", name);
 }

 @BeforeClass
 public static void init() throws Exception {

  hfc = new Hfc(getResource("LookupPointInTime.yml"));

  // compute deductive closure
  // TODO move this into extra tests -> fcInterval.computeClosure();
 }


 @Test
 public void testWhereNoInterval() {
  TupleStore tupleStore = hfc._tupleStore;
  Query query = new Query(tupleStore);
  String[][] expected = {{"<test:Sensor1>", "\"1\"^^<xsd:int>"}};
  try {
   BindingTable bt = query.query("SELECT ?s ?o WHERE \"1\"^^<xsd:long> ?s <test:hasValue> ?o ");
   checkResult(hfc, bt, expected, "?s", "?o");
  } catch (QueryParseException e) {
   e.printStackTrace();
   fail();
  }
 }


 @Test
 public void testSelectWhere() {
  TupleStore tupleStore = hfc._tupleStore;
  Query query = new Query(tupleStore);
  String[][] expected = {{"<test:Sensor2>", "\"2\"^^<xsd:int>"},
          {"<test:Sensor1>", "\"2\"^^<xsd:int>"},
          {"<test:Sensor2>", "\"4\"^^<xsd:int>"},
          {"<test:Sensor1>", "\"3\"^^<xsd:int>"}};
  try { // F
   BindingTable bt = query.query("SELECT ?s ?o WHERE [\"1\"^^<xsd:long>, \"5\"^^<xsd:long>] ?s <test:hasValue> ?o ");

   checkResult(hfc, bt, expected, "?s", "?o");
  } catch (QueryParseException e) {
   e.printStackTrace();
   fail();
  }
 }

 @Test
 public void testSelectDistinctWhere() {
  TupleStore tupleStore = hfc._tupleStore;
  Query query = new Query(tupleStore);
  String[][] expected = {{"<test:Sensor2>", "\"2\"^^<xsd:int>"},
          {"<test:Sensor1>", "\"2\"^^<xsd:int>"},
          {"<test:Sensor2>", "\"4\"^^<xsd:int>"},
          {"<test:Sensor1>", "\"3\"^^<xsd:int>"}};
  try { // F
   BindingTable bt = query.query("SELECT DISTINCT ?s ?o WHERE [\"1\"^^<xsd:long>, \"5\"^^<xsd:long>] ?s <test:hasValue> ?o");

   checkResult(hfc, bt, expected, "?s", "?o");
  } catch (QueryParseException e) {
   e.printStackTrace();
   fail();
  }
 }

 @Test
 public void testSelectWhereFilter() {
  TupleStore tupleStore = hfc._tupleStore;
  Query query = new Query(tupleStore);
  String[][] expected = {{"<test:Sensor2>", "\"2\"^^<xsd:int>"},
          {"<test:Sensor2>", "\"4\"^^<xsd:int>"}};
  try { // F
   BindingTable bt = query.query("SELECT DISTINCT ?s ?o WHERE [\"1\"^^<xsd:long>, \"5\"^^<xsd:long>] ?s <test:hasValue> ?o  FILTER ?s != <test:Sensor1>");

   checkResult(hfc, bt, expected, "?s", "?o");
  } catch (QueryParseException e) {
   e.printStackTrace();
   fail();
  }
 }


 @Test
 public void testSelectDistinctWhereFilter() {
  TupleStore tupleStore = hfc._tupleStore;
  Query query = new Query(tupleStore);
  String[][] expected = {
          {"<test:Sensor1>", "\"3\"^^<xsd:int>"}, {"<test:Sensor2>", "\"4\"^^<xsd:int>"}};
  try { // F
   BindingTable bt = query.query("SELECT DISTINCT ?s ?o WHERE [\"1\"^^<xsd:long>, \"5\"^^<xsd:long>] ?s <test:hasValue> ?o  FILTER IGreater ?o  \"2\"^^<xsd:int>");

   checkResult(hfc, bt, expected, "?s", "?o");
  } catch (QueryParseException e) {
   e.printStackTrace();
   fail();
  }
 }


 @Test
 public void testSelectWhereAggregate() {
  TupleStore tupleStore = hfc._tupleStore;
  Query query = new Query(tupleStore);
  String[][] expected = {{"\"4\"^^<xsd:int>"}};
  try { // F
   BindingTable bt = query.query("SELECT DISTINCT ?s ?o WHERE [\"1\"^^<xsd:long>, \"5\"^^<xsd:long>] ?s <test:hasValue> ?o AGGREGATE  ?number = Count ?o");

   checkResult(hfc, bt, expected, "?number");
  } catch (QueryParseException e) {
   e.printStackTrace();
   fail();
  }
 }


 @AfterClass
 public static void finish() {
  hfc.shutdownNoExit();
 }

}
