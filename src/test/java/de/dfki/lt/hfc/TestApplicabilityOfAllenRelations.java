package de.dfki.lt.hfc;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static de.dfki.lt.hfc.TestUtils.checkResult;
import static org.junit.jupiter.api.Assertions.*;


public class TestApplicabilityOfAllenRelations {

  static ForwardChainer fcWithIntervalTree_Index;

  static ForwardChainer fcWithoutIntervalTree_Index;

  private static String getResource(String name) {
    return TestUtils.getTestResource("Query", name);
  }

  @BeforeAll
  public static void init() throws Exception {

    fcWithIntervalTree_Index = new ForwardChainer(4,                                                    // #cores
        false,                                                 // verbose
        false,                                                 // RDF Check
        false,                                                // EQ reduction disabled
        6,                                                    // min #args
        6,                                                    // max #args
        100000,                                               // #atoms
        500000,                                               // #tuples
        getResource("lookupAtomIntervalTest.nt"),                 // tuple file
        getResource("combiTest.rdl"),                           // rule file  TODO
        getResource("default.ns"),                            // namespace file
        getResource("lookupCombi.idx")
    );

    fcWithoutIntervalTree_Index = new ForwardChainer(4,                                                    // #cores
        false,                                                 // verbose
        false,                                                 // RDF Check
        false,                                                // EQ reduction disabled
        6,                                                    // min #args
        6,                                                    // max #args
        100000,                                               // #atoms
        500000,                                               // #tuples
        getResource("lookupAtomIntervalTest.nt"),                 // tuple file
        getResource("combiTest.rdl"),                           // rule file  TODO
        getResource("default.ns"),                            // namespace file
        getResource("testApplicabilityFalse.idx")
    );
  }

  @Test
  public void testValidConfiguration() throws QueryParseException {
    TupleStore tupleStore = fcWithIntervalTree_Index.tupleStore;
    Query query = new Query(tupleStore);
    String[][] expected = {
        { "<test:Sensor2>",  "\"4\"^^<xsd:int>" },
    { "<test:Sensor2>" , "\"5\"^^<xsd:int>"},
    { "<test:Sensor1>", "\"3\"^^<xsd:int>"    }
    };

      BindingTable bt = query.query(
          "SELECT ?s ?o WHERE  [\"1\"^^<xsd:long>, \"6\"^^<xsd:long>] ?s <test:hasValue> ?o D \"200\"^^<xsd:long>  \"1550\"^^<xsd:long> ");
      assertNotNull(bt);
      assertFalse(bt.isEmpty());
      assertEquals(3, bt.size());
      assertEquals(2, bt.getVars().length);
      checkResult(expected, bt, bt.getVars());
      //End
      // Print Result


  }

  @Test
  public void testInvalidConfiguration() throws QueryParseException {
    TupleStore tupleStore = fcWithoutIntervalTree_Index.tupleStore;
    Query query = new Query(tupleStore);
    String[][] expected = {
        { "<test:Sensor2>",  "\"4\"^^<xsd:int>" },
        {  "<test:Sensor2>" , "\"5\"^^<xsd:int>"},
        {   "<test:Sensor1>" , "\"3\"^^<xsd:int>" }
    };

      BindingTable bt = query.query(
          "SELECT ?s ?o WHERE  [\"1\"^^<xsd:long>, \"6\"^^<xsd:long>] ?s <test:hasValue> ?o D \"200\"^^<xsd:long>  \"1550\"^^<xsd:long> ");
      assertNotNull(bt);
      assertFalse(bt.isEmpty());
      assertEquals(3, bt.size());
      assertEquals(2, bt.getVars().length);
      checkResult(expected, bt, bt.getVars());
      //End

  }

  @AfterAll
  public static void finish() {
    fcWithIntervalTree_Index.shutdownNoExit();
  }

}
