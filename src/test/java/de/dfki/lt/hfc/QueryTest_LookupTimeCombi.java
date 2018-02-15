package de.dfki.lt.hfc;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


/**
 * Created by christian on 08/06/17.
 */
public class QueryTest_LookupTimeCombi {

  static ForwardChainer fc;

  private static String getResource(String name) {
    return TestUtils.getTestResource("Query", name);
  }

  @BeforeAll
  public static void init() throws Exception {

    fc = new ForwardChainer(4,                                                    // #cores
        true,                                                 // verbose
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

    // compute deductive closure
    // TODO move this into extra tests -> fcInterval.computeClosure();
  }

  /**
   * "1"^^<xsd:long> <test:Sensor1> <test:hasValue> "1"^^<xsd:int> "100"^^<xsd:long>
   * "1100"^^<xsd:long>. "2"^^<xsd:long> <test:Sensor2> <test:hasValue> "2"^^<xsd:int>
   * "200"^^<xsd:long> "1200"^^<xsd:long>. "2"^^<xsd:long> <test:Sensor1> <test:hasValue>
   * "2"^^<xsd:int> "180"^^<xsd:long> "800"^^<xsd:long>. "3"^^<xsd:long> <test:Sensor1>
   * <test:hasValue> "3"^^<xsd:int> "300"^^<xsd:long> "1300"^^<xsd:long>. "4"^^<xsd:long>
   * <test:Sensor2> <test:hasValue> "4"^^<xsd:int> "400"^^<xsd:long> "1400"^^<xsd:long>.
   * "5"^^<xsd:long> <test:Sensor2> <test:hasValue> "5"^^<xsd:int> "500"^^<xsd:long>
   * "1500"^^<xsd:long>. "6"^^<xsd:long> <test:Sensor1> <test:hasValue> "6"^^<xsd:int>
   * "600"^^<xsd:long> "1600"^^<xsd:long>. "7"^^<xsd:long> <test:Sensor2> <test:hasValue>
   * "7"^^<xsd:int> "700"^^<xsd:long> "1700"^^<xsd:long>. "8"^^<xsd:long> <test:Sensor1>
   * <test:hasValue> "8"^^<xsd:int> "800"^^<xsd:long> "1800"^^<xsd:long>. "9"^^<xsd:long>
   * <test:Sensor1> <test:hasValue> "9"^^<xsd:int> "900"^^<xsd:long> "1900"^^<xsd:long>.
   * "10"^^<xsd:long> <test:Sensor2> <test:hasValue> "10"^^<xsd:int> "1000"^^<xsd:long>
   * "2000"^^<xsd:long>.
   */
  @Test
  public void testSelectWhere() throws QueryParseException {
    TupleStore tupleStore = fc.tupleStore;
    Query query = new Query(tupleStore);

      //Start
      BindingTable bt = query.query(
          "SELECT ?s ?o WHERE  [\"1\"^^<xsd:long>, \"6\"^^<xsd:long>] ?s <test:hasValue> ?o D \"200\"^^<xsd:long>  \"1550\"^^<xsd:long> ");
      assertNotNull(bt);
      assertFalse(bt.isEmpty());
      assertEquals(3, bt.size());
      assertEquals(2, bt.getVars().length);
      //End

  }

  @Test
  public void testSelectDistinctWhere() throws QueryParseException {
    TupleStore tupleStore = fc.tupleStore;
    Query query = new Query(tupleStore);

      BindingTable bt = query.query(
          "SELECT DISTINCT ?s ?o WHERE [\"1\"^^<xsd:long>, \"6\"^^<xsd:long>] ?s <test:hasValue> ?o D \"100\"^^<xsd:long>  \"1550\"^^<xsd:long> ");
      assertNotNull(bt);
      assertFalse(bt.isEmpty());
      assertEquals(5, bt.size());
      assertEquals(2, bt.getVars().length);

  }

  @Test
  public void testSelectWhereFilter() throws QueryParseException {
    TupleStore tupleStore = fc.tupleStore;
    Query query = new Query(tupleStore);

      BindingTable bt = query.query(
          "SELECT DISTINCT ?s ?o WHERE [\"1\"^^<xsd:long>, \"6\"^^<xsd:long>] ?s <test:hasValue> ?o D \"200\"^^<xsd:long>  \"1550\"^^<xsd:long> FILTER ?s != <test:Sensor1>");
      assertNotNull(bt);
      assertFalse(bt.isEmpty());
      assertEquals(2, bt.size());
      assertEquals(2, bt.getVars().length);

  }


  @Test
  public void testSelectDistinctWhereFilter() throws QueryParseException {
    TupleStore tupleStore = fc.tupleStore;
    Query query = new Query(tupleStore);

      BindingTable bt = query.query(
          "SELECT DISTINCT ?s ?o WHERE [\"1\"^^<xsd:long>, \"6\"^^<xsd:long>] ?s <test:hasValue> ?o D \"200\"^^<xsd:long>  \"1550\"^^<xsd:long> FILTER ?o IGreater \"2\"^^<xsd:int>");
      assertNotNull(bt);
      assertFalse(bt.isEmpty());
      assertEquals(3, bt.size());
      assertEquals(2, bt.getVars().length);

  }


  @Test
  public void testSelectWhereAggregate() throws QueryParseException {
    TupleStore tupleStore = fc.tupleStore;
    Query query = new Query(tupleStore);

      long startTime = System.currentTimeMillis();
      BindingTable bt = query.query(
          "SELECT DISTINCT ?s ?o WHERE [\"1\"^^<xsd:long>, \"6\"^^<xsd:long>] ?s <test:hasValue> ?o D \"200\"^^<xsd:long>  \"1550\"^^<xsd:long> AGGREGATE  ?number = Count ?o");
      assertNotNull(bt);
      assertFalse(bt.isEmpty());
      assertEquals(1, bt.size());
      assertEquals(1, bt.getVars().length);
      System.out.println(bt.toString());
      long endTime = System.currentTimeMillis();
      long totalTime = endTime - startTime;
      System.out.println(totalTime);

  }


  @AfterAll
  public static void finish() {
    fc.shutdownNoExit();
  }

}
