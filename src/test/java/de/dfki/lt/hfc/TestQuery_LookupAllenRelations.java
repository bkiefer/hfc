package de.dfki.lt.hfc;


import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import de.dfki.lt.hfc.io.QueryParseException;

import static de.dfki.lt.hfc.TestingUtils.checkResult;
import static junit.framework.TestCase.*;

/**
 * Created by christian on 08/06/17.
 */
public class TestQuery_LookupAllenRelations {

 static Hfc fc;


 private static String getResource(String name) {
  return TestingUtils.getTestResource("Query", name);
 }

 @BeforeClass
 public static void init() throws Exception {

  fc = new Hfc(Config.getInstance(getResource("LookupAllenRelation.yml")));

  // compute deductive closure
  // TODO move this into extra tests -> fcInterval.computeClosure();
 }

 @Test
 public void testSelectWhere() {
  TupleStore tupleStore = fc._tupleStore;
  Query query = new Query(tupleStore);

  try { // F
   BindingTable bt = query.query("SELECT ?s ?o WHERE ?s <test:hasValue> ?o D \"200\"^^<xsd:long>  \"1600\"^^<xsd:long> ");
   String[][] expected = {{"<test:Sensor2>", "\"4\"^^<xsd:int>"},
           {"<test:Sensor1>", "\"3\"^^<xsd:int>"},
           {"<test:Sensor2>", "\"5\"^^<xsd:int>"}};
   checkResult(fc, bt, expected, bt.getVars());
  } catch (QueryParseException e) {
   e.printStackTrace();
   fail();
  }
 }

 @Test
 public void testSelectDistinctWhere() {
  TupleStore tupleStore = fc._tupleStore;
  Query query = new Query(tupleStore);
  try { // F
   BindingTable bt = query.query("SELECT DISTINCT ?s ?o WHERE ?s <test:hasValue> ?o D \"100\"^^<xsd:long>  \"1550\"^^<xsd:long> ");
   String[][] expected = {{"<test:Sensor2>", "\"4\"^^<xsd:int>"},
           {"<test:Sensor1>", "\"3\"^^<xsd:int>"},
           {"<test:Sensor2>", "\"2\"^^<xsd:int>"},
           {"<test:Sensor1>", "\"2\"^^<xsd:int>"},
           {"<test:Sensor2>", "\"5\"^^<xsd:int>"}};
   checkResult(fc, bt, expected, bt.getVars());
  } catch (QueryParseException e) {
   e.printStackTrace();
   fail();
  }
 }

 @Test
 public void testSelectWhereFilter() {
  TupleStore tupleStore = fc._tupleStore;
  Query query = new Query(tupleStore);
  try { // F
   BindingTable bt = query.query("SELECT DISTINCT ?s ?o WHERE ?s <test:hasValue> ?o D \"200\"^^<xsd:long>  \"1600\"^^<xsd:long> FILTER ?s != <test:Sensor1>");
   String[][] expected = {{"<test:Sensor2>", "\"4\"^^<xsd:int>"},
           {"<test:Sensor2>", "\"5\"^^<xsd:int>"}};
   checkResult(fc, bt, expected, bt.getVars());
  } catch (QueryParseException e) {
   e.printStackTrace();
   fail();
  }
 }


 @Test
 public void testSelectDistinctWhereFilter() {
  TupleStore tupleStore = fc._tupleStore;
  Query query = new Query(tupleStore);
  try { // F
   BindingTable bt = query.query("SELECT DISTINCT ?s ?o WHERE ?s <test:hasValue> ?o D \"200\"^^<xsd:long>  \"1600\"^^<xsd:long> FILTER ?o IGreater \"2\"^^<xsd:int>");
   String[][] expected = {{"<test:Sensor2>", "\"4\"^^<xsd:int>"},
           {"<test:Sensor1>", "\"3\"^^<xsd:int>"},
           {"<test:Sensor2>", "\"5\"^^<xsd:int>"}};
   checkResult(fc, bt, expected, bt.getVars());
  } catch (QueryParseException e) {
   e.printStackTrace();
   fail();
  }
 }


 @Test
 public void testSelectWhereAggregate() {
  TupleStore tupleStore = fc._tupleStore;
  Query query = new Query(tupleStore);
  try { // F
   BindingTable bt = query.query("SELECT DISTINCT ?s ?o WHERE ?s <test:hasValue> ?o D \"200\"^^<xsd:long>  \"1600\"^^<xsd:long> AGGREGATE  ?number = Count ?o");
   String[][] expected = {{"\"3\"^^<xsd:int>"}};
   checkResult(fc, bt, expected, bt.getVars());
  } catch (QueryParseException e) {
   e.printStackTrace();
   fail();
  }
 }


 @AfterClass
 public static void finish() {
  fc.shutdownNoExit();
 }

}
