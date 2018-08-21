package de.dfki.lt.hfc;

import junit.framework.Assert;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;


import static junit.framework.TestCase.*;
import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.fail;

/**
 * Created by christian on 08/06/17.
 */
public class QueryTest_LookupTimeCombi {

  static ForwardChainer fc;

  private static String getResource(String name) {
    return TestingUtils.getTestResource("Query", name);
  }

  @BeforeClass
  public static void init() throws Exception {

    fc = new ForwardChainer(Config.getInstance(getResource("TimeCombi.yml")));

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
  public void testSelectWhere() {
    TupleStore tupleStore = fc.tupleStore;
    Query query = new Query(tupleStore);
    try {
      //Start
      BindingTable bt = query.query(
          "SELECT ?s ?o WHERE  [\"1\"^^<xsd:long>, \"6\"^^<xsd:long>] ?s <test:hasValue> ?o D \"200\"^^<xsd:long>  \"1550\"^^<xsd:long> ");
      assertNotNull(bt);
      assertFalse(bt.isEmpty());
      assertEquals(3, bt.size());
      assertEquals(2, bt.getVars().length);
      //End
    } catch (QueryParseException e) {
      e.printStackTrace();
      fail();
    }
  }

  @Test
  public void testSelectDistinctWhere() {
    TupleStore tupleStore = fc.tupleStore;
    Query query = new Query(tupleStore);
    try { // F
      BindingTable bt = query.query(
          "SELECT DISTINCT ?s ?o WHERE [\"1\"^^<xsd:long>, \"6\"^^<xsd:long>] ?s <test:hasValue> ?o D \"100\"^^<xsd:long>  \"1550\"^^<xsd:long> ");
      assertNotNull(bt);
      assertFalse(bt.isEmpty());
      Assert.assertEquals(5, bt.size());
      assertEquals(2, bt.getVars().length);
    } catch (QueryParseException e) {
      e.printStackTrace();
      fail();
    }
  }

  @Test
  public void testSelectWhereFilter() {
    TupleStore tupleStore = fc.tupleStore;
    Query query = new Query(tupleStore);
    try { // F
      BindingTable bt = query.query(
          "SELECT DISTINCT ?s ?o WHERE [\"1\"^^<xsd:long>, \"6\"^^<xsd:long>] ?s <test:hasValue> ?o D \"200\"^^<xsd:long>  \"1550\"^^<xsd:long> FILTER ?s != <test:Sensor1>");
      assertNotNull(bt);
      assertFalse(bt.isEmpty());
      assertEquals(2, bt.size());
      assertEquals(2, bt.getVars().length);
    } catch (QueryParseException e) {
      e.printStackTrace();
      fail();
    }
  }


  @Test
  public void testSelectDistinctWhereFilter() {
    TupleStore tupleStore = fc.tupleStore;
    Query query = new Query(tupleStore);
    try { // F
      BindingTable bt = query.query(
          "SELECT DISTINCT ?s ?o WHERE [\"1\"^^<xsd:long>, \"6\"^^<xsd:long>] ?s <test:hasValue> ?o D \"200\"^^<xsd:long>  \"1550\"^^<xsd:long> FILTER ?o IGreater \"2\"^^<xsd:int>");
      assertNotNull(bt);
      assertFalse(bt.isEmpty());
      assertEquals(3, bt.size());
      assertEquals(2, bt.getVars().length);
    } catch (QueryParseException e) {
      e.printStackTrace();
      fail();
    }
  }


  @Test
  public void testSelectWhereAggregate() {
    TupleStore tupleStore = fc.tupleStore;
    Query query = new Query(tupleStore);
    try { // F

      BindingTable bt = query.query(
          "SELECT DISTINCT ?s ?o WHERE [\"1\"^^<xsd:long>, \"6\"^^<xsd:long>] ?s <test:hasValue> ?o D \"200\"^^<xsd:long>  \"1550\"^^<xsd:long> AGGREGATE  ?number = Count ?o");
      assertNotNull(bt);
      assertFalse(bt.isEmpty());
      assertEquals(1, bt.size());
      assertEquals(1, bt.getVars().length);
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
