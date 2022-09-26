package de.dfki.lt.hfc;


import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static de.dfki.lt.hfc.TestingUtils.checkResult;
import static org.junit.Assert.*;

public class TestApplicabilityOfAllenRelations {

  static Hfc fcWithIntervalTree_Index;

  static Hfc fcWithoutIntervalTree_Index;

  private static String getResource(String name) {
    return TestingUtils.getTestResource("Query", name);
  }

  @BeforeClass
  public static void init() throws Exception {

    fcWithIntervalTree_Index = new Hfc(Config.getInstance(getResource("AllenRelationIndex.yml")));

    fcWithoutIntervalTree_Index = new Hfc(Config.getInstance(getResource("AllenRelationNoIndex.yml")));

  }

  @Test
  public void testValidConfiguration(){
    TupleStore tupleStore = fcWithIntervalTree_Index._tupleStore;
    Query query = new Query(tupleStore);
    String[][] expected = {
        { "<test:Sensor2>",  "\"4\"^^<xsd:int>" },
    { "<test:Sensor2>" , "\"5\"^^<xsd:int>"},
    { "<test:Sensor1>", "\"3\"^^<xsd:int>"    }
    };
    try {
      //Start
      BindingTable bt = query.query(
          "SELECT ?s ?o WHERE  [\"1\"^^<xsd:long>, \"6\"^^<xsd:long>] ?s <test:hasValue> ?o D \"200\"^^<xsd:long>  \"1550\"^^<xsd:long> ");
      assertNotNull(bt);
      assertFalse(bt.isEmpty());
      assertEquals(3, bt.size());
      assertEquals(2, bt.getVars().length);
      checkResult(fcWithIntervalTree_Index, bt, expected, bt.getVars());
      //End

    } catch (QueryParseException e) {
      e.printStackTrace();
      fail("");
    }
  }

  @Test
  public void testInvalidConfiguration(){
    TupleStore tupleStore = fcWithoutIntervalTree_Index._tupleStore;
    Query query = new Query(tupleStore);
    String[][] expected = {
        { "<test:Sensor2>",  "\"4\"^^<xsd:int>" },
        {  "<test:Sensor2>" , "\"5\"^^<xsd:int>"},
        {   "<test:Sensor1>" , "\"3\"^^<xsd:int>" }
    };
    try {
      //Start
      BindingTable bt = query.query(
          "SELECT ?s ?o WHERE  [\"1\"^^<xsd:long>, \"6\"^^<xsd:long>] ?s <test:hasValue> ?o D \"200\"^^<xsd:long>  \"1550\"^^<xsd:long> ");
      assertNotNull(bt);
      assertFalse(bt.isEmpty());
      assertEquals(3, bt.size());
      assertEquals(2, bt.getVars().length);
      checkResult(fcWithoutIntervalTree_Index, bt, expected, bt.getVars());
      //End
      // Print Result
    } catch (QueryParseException e) {
      e.printStackTrace();
      fail("");
    }
  }

  @AfterClass
  public static void finish() {
    fcWithIntervalTree_Index.shutdownNoExit();
  }

}
