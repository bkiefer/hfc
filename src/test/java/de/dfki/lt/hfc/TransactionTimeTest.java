package de.dfki.lt.hfc;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import de.dfki.lt.hfc.io.QueryParseException;

import static org.junit.Assert.assertEquals;

/**
 * a first test of our approach towards mimicing transaction time in
 * an extended RDF setting;
 * for this, we employ quintuples of the following form:
 *   polarity subject predicate object timestamp
 * polarity takes the following singleton instances from the "logic" ontology
 * (there are others as well), viz.,
 *   logic:dontknow, logic:true, logic:false, and logic:error
 *
 *
 * @author (C) Hans-Ulrich Krieger
 * @since JDK 1.5
 * @version Wed Jun 22 15:20:51 CEST 2016
 */
public class TransactionTimeTest {

  static Hfc fc;

  // currently, we were only using these modals from the logic ontology
  static String DONT_KNOW = "<logic:dontknow>";
  static String TRUE = "<logic:true>";
  static String FALSE = "<logic:false>";
  static String ERROR = "<logic:error>";

  // 0 = beginning of time
  static String TIME = "\"0\"^^<xsd:long>";

  public static String getResource(String name) {
    return TestingUtils.getTestResource("TestTransactionTime", name);
  }

  @BeforeClass
  public static void init() throws Exception {
    fc = new Hfc(Config.getInstance(getResource("TestTransaction.yml")));
    // further PAL-specific namespaces (short-to-long mappings, XSD DTs-to-Java class mappings)
    // further PAL-specific tuples (special XSD DT)
    // further PAL-specific triples from the inidividual sub-ontologies: make them 5-tuples
    for (String file : new String[] {
            "dialogue.nt",
            "dmgoals.nt",
            "domain.nt",
            "logic.nt",
            "pal.nt",
            "semantics.nt",
            "time.nt",
            "upper.nt"})
      fc.uploadTuples(getResource(file), TRUE, TIME);  // transform triples into quintuples
  }

  @AfterClass
  public static void finish() {
    fc.shutdownNoExit();
  }

  @Test
  public void test() throws QueryParseException {
    fc.computeClosure();
    Query q = fc.getQuery();
    BindingTable bt = q.query("SELECT * WHERE ?s ?p ?o");  // 0 triples
    assertEquals(0, bt.size());
    bt = q.query("SELECT * WHERE ?s ?p ?o ?ts");  // 0 quadruples
    assertEquals(0, bt.size());
    bt = q.query("SELECT * WHERE ?pol ?s <owl:equivalentClass> ?o ?ts");  // 2 result quadruples
    assertEquals(2, bt.size());
    bt = q.query("SELECTALL * WHERE ?pol ?s <owl:equivalentClass> ?o ?ts");  // 2 result quadruples
    assertEquals(25, bt.size());
    bt = q.query("SELECT * WHERE ?pol ?s ?p ?o ?ts");  // 4057 quintuples
    assertEquals(4057, bt.size());
  }

}
