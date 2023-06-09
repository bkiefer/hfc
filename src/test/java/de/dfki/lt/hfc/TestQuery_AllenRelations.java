package de.dfki.lt.hfc;


import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.dfki.lt.hfc.io.QueryParseException;

import static de.dfki.lt.hfc.TestingUtils.checkResult;
import static junit.framework.TestCase.*;

public class TestQuery_AllenRelations {

 static Hfc fcInterval, fcNoIndex;

 Query queryInterval = fcInterval.getQuery();
 Query queryNoIndex = fcNoIndex.getQuery();

 private static String getResource(String name) {
  return TestingUtils.getTestResource("Query", name);
 }

 @BeforeClass
 public static void init() throws Exception {
  fcInterval = new Hfc(Config.getInstance(getResource("lookupIntervalTestIndex.yml")));
  fcNoIndex = new Hfc(Config.getInstance(getResource("lookupIntervalTestNoIndex.yml")));
 }

 @Before
 public void before() {
   queryInterval = fcInterval.getQuery();
   queryNoIndex = fcNoIndex.getQuery();
 }

 @Test
 public void testTemp() {

  String[][] expected = {
          {"<test:Sensor2>", "\"2\"^^<xsd:int>"},
          {"<test:Sensor1>", "\"8\"^^<xsd:int>"},
          {"<test:Sensor2>", "\"10\"^^<xsd:int>"},
          {"<test:Sensor2>", "\"4\"^^<xsd:int>"},
          {"<test:Sensor1>", "\"3\"^^<xsd:int>"},
          {"<test:Sensor1>", "\"6\"^^<xsd:int>"},
          {"<test:Sensor1>", "\"1\"^^<xsd:int>"},
          {"<test:Sensor1>", "\"2\"^^<xsd:int>"},
          {"<test:Sensor2>", "\"5\"^^<xsd:int>"},
          {"<test:Sensor1>", "\"9\"^^<xsd:int>"},
          {"<test:Sensor1>", "\"10\"^^<xsd:int>"},
          {"<test:Sensor2>", "\"7\"^^<xsd:int>"}
  };

  try { // F
   String input = "SELECT ?s ?o WHERE ?s <rdf:type> <test:sensor> \"0\"^^<xsd:long> \"999\"^^<xsd:long> & ?s <test:hasValue> ?o ?t1 ?t2 ";
   BindingTable btInterval = queryInterval.query(input);
   BindingTable btNoIndex = queryNoIndex.query(input);
   //System.out.println("BtInterval: " + btInterval.toString(true));
   //System.out.println("BtNoIndex: " + btNoIndex.toString(true));
   checkResult(fcInterval, btInterval, expected, "?s", "?o");
   checkResult(fcNoIndex, btNoIndex, expected, "?s", "?o");
  } catch (QueryParseException e) {
   e.printStackTrace();
   fail();
  }
 }

 /**
  * 1)
  * =====================================================================================
  * | ?s                 | ?o                 | ?rel1              | ?rel2              |
  * =====================================================================================
  * | <test:Sensor1>     , ""1"^^<xsd:int>     , ""100"^^<xsd:long>  , ""1100"^^<xsd:long> |
  * | <test:Sensor1>     , ""2"^^<xsd:int>     , ""180"^^<xsd:long>  , ""800"^^<xsd:long>  |
  * | <test:Sensor2>     , ""2"^^<xsd:int>     , ""200"^^<xsd:long>  , ""1200"^^<xsd:long> |
  * | <test:Sensor1>     , ""3"^^<xsd:int>     , ""300"^^<xsd:long>  , ""1300"^^<xsd:long> |
  * ------------------------------------------------------------------------------------
  */
 @Test
 public void testBefore() {
  String[][] expected = {
          {"<test:Sensor1>", "\"1\"^^<xsd:int>"},
          {"<test:Sensor1>", "\"2\"^^<xsd:int>"},
          {"<test:Sensor2>", "\"2\"^^<xsd:int>"},
          {"<test:Sensor1>", "\"3\"^^<xsd:int>"}
  };

  try { // F
   String input = "SELECT ?s ?o WHERE ?s <test:hasValue> ?o Bf \"1400\"^^<xsd:long>  \"1600\"^^<xsd:long> ";
   BindingTable btInterval = queryInterval.query(input);
   BindingTable btNoIndex = queryNoIndex.query(input);
   checkResult(fcInterval, btInterval, expected, btInterval.getVars());//"?s", "?o", "?rel1", "?rel2");
   checkResult(fcNoIndex, btNoIndex, expected, btNoIndex.getVars());//"?s", "?o", "?rel1", "?rel2");
  } catch (QueryParseException e) {
   e.printStackTrace();
   fail();
  }
 }

 /**
  * 2)
  * =====================================================================================
  * | ?s                 | ?o                 | ?rel1              | ?rel2              |
  * =====================================================================================
  * | <test:Sensor1>     , ""8"^^<xsd:int>     , ""800"^^<xsd:long>  , ""1800"^^<xsd:long> |
  * | <test:Sensor2>     , ""7"^^<xsd:int>     , ""700"^^<xsd:long>  , ""1700"^^<xsd:long> |
  * | <test:Sensor1>     , ""9"^^<xsd:int>     , ""900"^^<xsd:long>  , ""1900"^^<xsd:long> |
  * | <test:Sensor2>     , ""10"^^<xsd:int>    , ""1000"^^<xsd:long> , ""2000"^^<xsd:long> |
  * -------------------------------------------------------------------------------------
  */
 @Test
 public void testAfter() {
  String[][] expected = {
          {"<test:Sensor1>", "\"8\"^^<xsd:int>"},
          {"<test:Sensor2>", "\"7\"^^<xsd:int>"},
          {"<test:Sensor1>", "\"9\"^^<xsd:int>"},
          {"<test:Sensor2>", "\"10\"^^<xsd:int>"}};

  try { // F
   String input = "SELECT ?s ?o WHERE ?s <test:hasValue> ?o Af \"0\"^^<xsd:long>  \"600\"^^<xsd:long> ";
   BindingTable btInterval = queryInterval.query(input);
   BindingTable btNoIndex = queryNoIndex.query(input);
   checkResult(fcInterval, btInterval, expected, btInterval.getVars());//"?s", "?o", "?rel1", "?rel2");
   checkResult(fcNoIndex, btNoIndex, expected, btNoIndex.getVars());//"?s", "?o", "?rel1", "?rel2");
  } catch (QueryParseException e) {
   e.printStackTrace();
   fail();
  }
 }

 /**
  * 3)
  * =====================================================================================
  * | ?s                 | ?o                 | ?rel1              | ?rel2              |
  * =====================================================================================
  * | <test:Sensor2>     , ""2"^^<xsd:int>     , ""200"^^<xsd:long>  , ""1200"^^<xsd:long> |
  * -------------------------------------------------------------------------------------
  */
 @Test
 public void testEqual() {
  String[][] expected = {
          {"<test:Sensor2>", "\"2\"^^<xsd:int>"}
  };

  try { // F
   String input = "SELECT ?s ?o WHERE ?s <test:hasValue> ?o EA \"200\"^^<xsd:long>  \"1200\"^^<xsd:long> ";
   BindingTable btInterval = queryInterval.query(input);
   BindingTable btNoIndex = queryNoIndex.query(input);
   checkResult(fcInterval, btInterval, expected, btInterval.getVars());//"?s", "?o", "?rel1", "?rel2");
   checkResult(fcNoIndex, btNoIndex, expected, btNoIndex.getVars());//"?s", "?o", "?rel1", "?rel2");
  } catch (QueryParseException e) {
   e.printStackTrace();
   fail();
  }
 }

 /**
  * 4)
  * =====================================================================================
  * | <test:Sensor2>     , ""2"^^<xsd:int>     , ""200"^^<xsd:long>  , ""1200"^^<xsd:long> |
  * -------------------------------------------------------------------------------------
  */
 @Test
 public void testStart() {
  String[][] expected = {
          {"<test:Sensor2>", "\"2\"^^<xsd:int>"}
  };

  try { // F
   String input = "SELECT ?s ?o WHERE ?s <test:hasValue> ?o S \"200\"^^<xsd:long>  \"1300\"^^<xsd:long> ";
   BindingTable btInterval = queryInterval.query(input);
   BindingTable btNoIndex = queryNoIndex.query(input);
   checkResult(fcInterval, btInterval, expected, btInterval.getVars());//"?s", "?o", "?rel1", "?rel2");
   checkResult(fcNoIndex, btNoIndex, expected, btNoIndex.getVars());//"?s", "?o", "?rel1", "?rel2");
  } catch (QueryParseException e) {
   e.printStackTrace();
   fail();
  }
 }

 /**
  * 5)
  * =====================================================================================
  * | ?s                 | ?o                 | ?rel3              | ?rel4              |
  * =====================================================================================
  * | <test:Sensor2>     , ""2"^^<xsd:int>     , ""200"^^<xsd:long>  , ""1200"^^<xsd:long> |
  * -------------------------------------------------------------------------------------
  */
 @Test
 public void testStartI() {
  String[][] expected = {
          {"<test:Sensor2>", "\"2\"^^<xsd:int>"}
  };

  try { // F
   String input = "SELECT ?s ?o WHERE ?s <test:hasValue> ?o Si \"200\"^^<xsd:long>  \"1100\"^^<xsd:long> ";
   BindingTable btInterval = queryInterval.query(input);
   BindingTable btNoIndex = queryNoIndex.query(input);
   checkResult(fcInterval, btInterval, expected, btInterval.getVars());//"?s", "?o", "?rel1", "?rel2");
   checkResult(fcNoIndex, btNoIndex, expected, btNoIndex.getVars());//"?s", "?o", "?rel1", "?rel2");
  } catch (QueryParseException e) {
   e.printStackTrace();
   fail();
  }
 }

 /**
  * 6)Expected
  * =====================================================================================
  * | ?s                 | ?o                 | ?rel3              | ?rel4              |
  * =====================================================================================
  * | <test:Sensor2>     , ""2"^^<xsd:int>     , ""200"^^<xsd:long>  , ""1200"^^<xsd:long> |
  * -------------------------------------------------------------------------------------
  */
 @Test
 public void testFinish() {
  String[][] expected = {
          {"<test:Sensor2>", "\"2\"^^<xsd:int>"}
  };

  try { // F
   String input = "SELECT ?s ?o WHERE ?s <test:hasValue> ?o F \"100\"^^<xsd:long>  \"1200\"^^<xsd:long> ";
   BindingTable btInterval = queryInterval.query(input);
   BindingTable btNoIndex = queryNoIndex.query(input);
   checkResult(fcInterval, btInterval, expected, btInterval.getVars());//"?s", "?o", "?rel1", "?rel2");
   checkResult(fcNoIndex, btNoIndex, expected, btNoIndex.getVars());//"?s", "?o", "?rel1", "?rel2");
  } catch (QueryParseException e) {
   e.printStackTrace();
   fail();
  }
 }

 /**
  * 7)Expected
  * =====================================================================================
  * | ?s                 | ?o                 | ?rel3              | ?rel4              |
  * =====================================================================================
  * | <test:Sensor2>     , ""2"^^<xsd:int>     , ""200"^^<xsd:long>  , ""1200"^^<xsd:long> |
  * -------------------------------------------------------------------------------------
  */
 @Test
 public void testFinishI() {
  String[][] expected = {
          {"<test:Sensor2>", "\"2\"^^<xsd:int>"}
  };

  try { // F
   String input = "SELECT ?s ?o WHERE ?s <test:hasValue> ?o Fi \"800\"^^<xsd:long>  \"1200\"^^<xsd:long> ";
   BindingTable btInterval = queryInterval.query(input);
   BindingTable btNoIndex = queryNoIndex.query(input);

   checkResult(fcInterval, btInterval, expected, btInterval.getVars());//"?s", "?o", "?rel1", "?rel2");
   checkResult(fcNoIndex, btNoIndex, expected, btNoIndex.getVars());//"?s", "?o", "?rel1", "?rel2");
  } catch (QueryParseException e) {
   e.printStackTrace();
   fail();
  }
 }

 /**
  * 8)
  * =====================================================================================
  * | ?s                 | ?o                 | ?rel1              | ?rel2              |
  * =====================================================================================
  * | <test:Sensor1>     , ""3"^^<xsd:int>     , ""300"^^<xsd:long>  , ""1300"^^<xsd:long> |
  * | <test:Sensor2>     , ""4"^^<xsd:int>     , ""400"^^<xsd:long>  , ""1400"^^<xsd:long> |
  * | <test:Sensor2>     , ""5"^^<xsd:int>     , ""500"^^<xsd:long>  , ""1500"^^<xsd:long> |
  * | <test:Sensor1>     , ""8"^^<xsd:int>     , ""800"^^<xsd:long>  , ""1800"^^<xsd:long> |
  * | <test:Sensor1>     , ""2"^^<xsd:int>     , ""180"^^<xsd:long>  , ""800"^^<xsd:long>  |
  * | <test:Sensor1>     , ""9"^^<xsd:int>     , ""900"^^<xsd:long>  , ""1900"^^<xsd:long> |
  * | <test:Sensor2>     , ""2"^^<xsd:int>     , ""200"^^<xsd:long>  , ""1200"^^<xsd:long> |
  * | <test:Sensor2>     , ""7"^^<xsd:int>     , ""700"^^<xsd:long>  , ""1700"^^<xsd:long> |
  * | <test:Sensor1>     , ""6"^^<xsd:int>     , ""600"^^<xsd:long>  , ""1600"^^<xsd:long> |
  * -------------------------------------------------------------------------------------
  */
 @Test
 public void testDuring() {
  String[][] expected = {
          {"<test:Sensor1>", "\"3\"^^<xsd:int>"},
          {"<test:Sensor2>", "\"4\"^^<xsd:int>"},
          {"<test:Sensor2>", "\"5\"^^<xsd:int>"},
          {"<test:Sensor1>", "\"8\"^^<xsd:int>"},
          {"<test:Sensor1>", "\"2\"^^<xsd:int>"},
          {"<test:Sensor1>", "\"9\"^^<xsd:int>"},
          {"<test:Sensor2>", "\"2\"^^<xsd:int>"},
          {"<test:Sensor2>", "\"7\"^^<xsd:int>"},
          {"<test:Sensor1>", "\"6\"^^<xsd:int>"}
  };

  try { // F
   String input = "SELECT ?s ?o WHERE ?s <test:hasValue> ?o D \"100\"^^<xsd:long>  \"2000\"^^<xsd:long> ";
   BindingTable btInterval = queryInterval.query(input);
   BindingTable btNoIndex = queryNoIndex.query(input);
   checkResult(fcInterval, btInterval, expected, btInterval.getVars());//"?s", "?o", "?rel1", "?rel2");
   checkResult(fcNoIndex, btNoIndex, expected, btNoIndex.getVars());//"?s", "?o", "?rel1", "?rel2");
  } catch (QueryParseException e) {
   e.printStackTrace();
   fail();
  }
 }

 /**
  * 9)
  * =====================================================================================
  * | ?s                 | ?o                 | ?rel1              | ?rel2              |
  * = ====================================================================================
  * | <test:Sensor1>     , ""10"^^<xsd:int>    , ""0"^^<xsd:long>    , ""2100"^^<xsd:long> |
  * -------------------------------------------------------------------------------------
  */
 @Test
 public void testDuringI() {
  String[][] expected = {
          {"<test:Sensor1>", "\"10\"^^<xsd:int>"}
  };

  try { // F
   String input = "SELECT ?s ?o WHERE ?s <test:hasValue> ?o Di \"100\"^^<xsd:long>  \"2000\"^^<xsd:long> ";
   BindingTable btInterval = queryInterval.query(input);
   BindingTable btNoIndex = queryNoIndex.query(input);
   checkResult(fcInterval, btInterval, expected, btInterval.getVars());//"?s", "?o", "?rel1", "?rel2");
   checkResult(fcNoIndex, btNoIndex, expected, btNoIndex.getVars());//"?s", "?o", "?rel1", "?rel2");
  } catch (QueryParseException e) {
   e.printStackTrace();
   fail();
  }
 }

 /**
  * 10)
  * =================================================================================
  * | ?s                | ?o                | ?rel1             | ?rel2             |
  * =================================================================================
  * | <test:Sensor1>    , ""2"^^<xsd:int>    , ""180"^^<xsd:long> , ""800"^^<xsd:long> |
  * ---------------------------------------------------------------------------------
  */
 @Test
 public void testMeeting() {
  String[][] expected = {
          {"<test:Sensor1>", "\"2\"^^<xsd:int>"}
  };

  try { // F
   String input = "SELECT ?s ?o WHERE ?s <test:hasValue> ?o M \"800\"^^<xsd:long>  \"820\"^^<xsd:long> ";
   BindingTable btNoIndex = queryNoIndex.query(input);
   checkResult(fcNoIndex, btNoIndex, expected, btNoIndex.getVars());
  } catch (QueryParseException e) {
   e.printStackTrace();
   fail();
  }
 }

 /**
  * 11)
  * =================================================================================
  * | ?s                | ?o                | ?rel1             | ?rel2             |
  * =================================================================================
  * | <test:Sensor2>    , ""2"^^<xsd:int>    , ""200"^^<xsd:long> , ""1200"^^<xsd:long> |
  * ---------------------------------------------------------------------------------
  */
 @Test
 public void testMeetingI() {
  String[][] expected = {
          {"<test:Sensor2>", "\"2\"^^<xsd:int>"}
  };

  try { // F
   String input = "SELECT ?s ?o WHERE ?s <test:hasValue> ?o Mi \"100\"^^<xsd:long>  \"200\"^^<xsd:long> ";
   BindingTable btInterval = queryInterval.query(input);
   BindingTable btNoIndex = queryNoIndex.query(input);

   checkResult(fcInterval, btInterval, expected, btInterval.getVars());//"?s", "?o", "?rel1", "?rel2");
   checkResult(fcNoIndex, btNoIndex, expected, btNoIndex.getVars());//"?s", "?o", "?rel1", "?rel2");

  } catch (QueryParseException e) {
   e.printStackTrace();
   fail();
  }
 }

 /**
  * 12)
  * =================================================================================
  * | ?s                | ?o                | ?rel1             | ?rel2             |
  * =================================================================================
  * | <test:Sensor1>    , ""2"^^<xsd:int>    , ""180"^^<xsd:long> , ""800"^^<xsd:long> |
  * ---------------------------------------------------------------------------------
  */
 @Test
 public void testOverlap() {
  String[][] expected = {
          {"<test:Sensor1>", "\"2\"^^<xsd:int>"}
  };

  try { // F
   String input = "SELECT ?s ?o WHERE ?s <test:hasValue> ?o O \"400\"^^<xsd:long>  \"900\"^^<xsd:long> ";
   BindingTable btInterval = queryInterval.query(input);
   BindingTable btNoIndex = queryNoIndex.query(input);
   String tempQuery = "SELECT ?s ?o WHERE ?s <test:hasValue> ?o ?t ?t1 FILTER LGreater ?t \"400\"^^<xsd:long>";
   BindingTable tempTable = queryNoIndex.query(tempQuery);
   checkResult(fcInterval, btInterval, expected, btInterval.getVars());//"?s", "?o", "?rel1", "?rel2");
   checkResult(fcNoIndex, btNoIndex, expected, btNoIndex.getVars());//"?s", "?o", "?rel1", "?rel2");

  } catch (QueryParseException e) {
   e.printStackTrace();
   fail();
  }
 }

 /**
  * 13)
  * =====================================================================================
  * | ?s                 | ?o                 | ?rel1              | ?rel2              |
  * =====================================================================================
  * | <test:Sensor2>     , ""10"^^<xsd:int>    , ""1000"^^<xsd:long> , ""2000"^^<xsd:long> |
  * | <test:Sensor1>     , ""9"^^<xsd:int>     , ""900"^^<xsd:long>  , ""1900"^^<xsd:long> |
  * -------------------------------------------------------------------------------------
  */
 @Test
 public void testOverlapI() {
  String[][] expected = {
          {"<test:Sensor2>", "\"10\"^^<xsd:int>"},
          {"<test:Sensor1>", "\"9\"^^<xsd:int>"}
  };

  try { // F
   String input = "SELECT ?s ?o WHERE ?s <test:hasValue> ?o Oi \"800\"^^<xsd:long>  \"1300\"^^<xsd:long> ";
   BindingTable btInterval = queryInterval.query(input);
   BindingTable btNoIndex = queryNoIndex.query(input);

   checkResult(fcInterval, btInterval, expected, btInterval.getVars());//"?s", "?o", "?rel1", "?rel2");
   checkResult(fcNoIndex, btNoIndex, expected, btNoIndex.getVars());//"?s", "?o", "?rel1", "?rel2");

  } catch (QueryParseException e) {
   e.printStackTrace();
   fail();
  }
 }

 @AfterClass
 public static void finish() {
  fcInterval.shutdownNoExit();
 }

}
