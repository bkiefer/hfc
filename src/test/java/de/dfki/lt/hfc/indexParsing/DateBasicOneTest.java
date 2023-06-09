package de.dfki.lt.hfc.indexParsing;

import static org.junit.Assert.assertEquals;

import java.util.Set;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import de.dfki.lt.hfc.Config;
import de.dfki.lt.hfc.TestHfc;
import de.dfki.lt.hfc.TestingUtils;
import de.dfki.lt.hfc.types.XsdDate;

/**
 * This tests ensure that the basic parsing for Transaction time encoded by
 * xsd:date works. Here the Transaction time is stored on the first position of
 * the tuple, e.g. "0000-00-00"^^<xsd:date> <rdf:type> <rdf:type> <rdf:Property>
 * . Created by christian on 26/02/17.
 */
public class DateBasicOneTest {

  static TestHfc fc;

  static String[] testSub1 = new String[] {
      "<rdf:type>", "<rdf:type>", "<rdf:Property>" };
  static String[] testSub2 = new String[] {
      "<test:sensor>", "<rdfs:subClassOf>", "<owl:Thing>" };

  private static String getResource(String name) {
    return TestingUtils.getTestResource("Index_Parsing", name);
  }

  @BeforeClass
  public static void init() throws Exception {
    fc = new TestHfc(
        Config.getInstance(getResource("basic_transaction_date0.yml")));

    // compute deductive closure
    // TODO move this into extra tests -> fc.computeClosure();
  }

  @Test
  public void testIndexClosure() {
    assertEquals(1, fc.getIndex().size());
    XsdDate key = new XsdDate(0, 0, 0);
    Set<int[]> values = fc.getIndex().lookup(key);
    assertEquals(74, values.size());
    fc.computeClosure();
    assertEquals(1, fc.getIndex().size());
    assertEquals(0, fc.getIndex().secSize());
    key = new XsdDate(0, 0, 0);
    values = fc.getIndex().lookup(key);
    assertEquals(156, values.size());
  }

  @AfterClass
  public static void finish() {
    fc.shutdownNoExit();
  }
}
