package de.dfki.lt.hfc.aggregates;

import static de.dfki.lt.hfc.TestUtils.checkResult;
import static de.dfki.lt.hfc.aggregates.TestLGetLatest.getResource;
import static org.junit.Assert.assertEquals;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import de.dfki.lt.hfc.*;

public class TestLGetLatest2 {
  static ForwardChainer fc;


  /**
   * for test purposes, I have (currently) switched off the equivalence class reduction
   *   TupleStore.equivalenceClassReduction == false
   * and have restricted ourselves to pure RDF triples:
   *   TupleStore.minNoOfArgs == 3
   *   TupleStore.maxNoOfArgs == 3
   * these settings are already defined below in the below constructor !
   *
   * note that temporal information is represented through the dafn:happens property, so
   * that we can restrict ourselved to the standard RDFS & OWL entailment rule set
   *
   * compile with
   *   javac -cp .:../jars/hfc.jar:../lib/trove-2.1.0.jar Template.java
	 * run with
	 *   java -server -cp .:../jars/hfc.jar:../lib/trove-2.1.0.jar -Xms800m -Xmx1200m Template
   *
   *
   * @author (C) Hans-Ulrich Krieger
   * @version Mon Jan  4 10:11:02 CET 2016
   */
  @BeforeClass
  public static void init() throws Exception {

    fc =	new ForwardChainer(4,                                                    // #cores
        false,                                                 // verbose
        true,                                                 // RDF Check
        false,                                                // EQ reduction disabled
        3,                                                    // min #args
        3,                                                    // max #args
        100000,                                               // #atoms
        500000,                                               // #tuples
        getResource("default.nt"),                            // tuple file
        getResource("default.rdl"),                           // rule file
        getResource("default.ns")                             // namespace file
        );

    // upload instance test files
    fc.uploadTuples(getResource("time.nt"));
    fc.uploadTuples(getResource("upper.nt"));
    fc.uploadTuples(getResource("domain.nt"));
    fc.uploadTuples(getResource("dialframe.nt"));
    fc.uploadTuples(getResource("test.data.nt"));

    // compute deductive closure
    fc.computeClosure();
  }

  @AfterClass
  public static void finish() {
    fc.shutdownNoExit();
  }

  // general form of the aggregate call: ?arg1' ... ?argN' = LGetLatest2 ?arg1 ... ?argN ?time ?limit

  @Test
  public void test14() throws QueryParseException {
    String[][] expected = {
        { "<hst:da8>", "\"755\"^^<xsd:long>" },
        { "<hst:da7>", "\"731\"^^<xsd:long>" },
        { "<hst:da6>", "\"686\"^^<xsd:long>" },
        { "<hst:da5>", "\"548\"^^<xsd:long>" },
    };
    // double-valued aggregate
    /* prints
    unknown constant in FILTER predicate: "540\"^^<xsd:long>
    unknown constant in AGGREGATE function: "5\"^^<xsd:int>
    */
    Query q = new Query(fc.tupleStore);
    BindingTable bt = q.query(
        "SELECT ?da ?t WHERE ?da <rdf:type> <dafn:DialogueAct>"
        + " & ?da <dafn:happens> ?t FILTER LGreaterEqual ?t \"540\"^^<xsd:long>"
        + " AGGREGATE ?dialact ?time = LGetLatest2 ?da ?t ?t \"5\"^^<xsd:int>");
    checkResult(fc, bt, expected, "?dialact", "?time");
  }

  @Test
  public void test15() throws QueryParseException {
    String[][] expected = {
        { "<hst:da8>", "\"755\"^^<xsd:long>" },
        { "<hst:da7>", "\"731\"^^<xsd:long>" },
        { "<hst:da6>", "\"686\"^^<xsd:long>" },
        { "<hst:da5>", "\"548\"^^<xsd:long>" },
    };
    Query q = new Query(fc.tupleStore);
    BindingTable bt = q.query(""
        + "SELECT ?da ?t WHERE ?da <rdf:type> <dafn:DialogueAct>"
        + " & ?da <dafn:happens> ?t FILTER LGreaterEqual ?t \"540\"^^<xsd:long>"
        + " AGGREGATE ?dialact ?time = LGetLatest2 ?da ?t ?t \"4\"^^<xsd:int>");
    checkResult(fc, bt, expected, "?dialact", "?time");
    System.out.println(bt);
  }

  @Test
  public void test16() throws QueryParseException {
    String[][] expected = {
        { "<hst:da8>", "\"755\"^^<xsd:long>" },
        { "<hst:da7>", "\"731\"^^<xsd:long>" },
        { "<hst:da6>", "\"686\"^^<xsd:long>" },
    };
   Query q = new Query(fc.tupleStore);
    BindingTable bt = q.query("SELECT ?da ?t WHERE ?da <rdf:type> <dafn:DialogueAct>"
        + " & ?da <dafn:happens> ?t FILTER LGreaterEqual ?t \"540\"^^<xsd:long>"
        + " AGGREGATE ?dialact ?time = LGetLatest2 ?da ?t ?t \"3\"^^<xsd:int>");
    checkResult(fc, bt, expected, "?dialact", "?time");
    System.out.println(bt);
  }

  @Test
  public void test17() throws QueryParseException {
    String[][] expected = {
        { "<hst:da8>", "\"755\"^^<xsd:long>" },
        { "<hst:da7>", "\"731\"^^<xsd:long>" },
    };
    Query q = new Query(fc.tupleStore);
    BindingTable bt = q.query("SELECT ?da ?t WHERE ?da <rdf:type> <dafn:DialogueAct>"
        + " & ?da <dafn:happens> ?t FILTER LGreaterEqual ?t \"540\"^^<xsd:long>"
        + " AGGREGATE ?dialact ?time = LGetLatest2 ?da ?t ?t \"2\"^^<xsd:int>");
    checkResult(fc, bt, expected, "?dialact", "?time");
    System.out.println(bt);
  }

  @Test
  public void test18() throws QueryParseException {
    String[][] expected = {
        { "<hst:da8>", "\"755\"^^<xsd:long>" },
    };
    Query q = new Query(fc.tupleStore);
    BindingTable bt = q.query("SELECT ?da ?t WHERE ?da <rdf:type> <dafn:DialogueAct>"
        + " & ?da <dafn:happens> ?t FILTER LGreaterEqual ?t \"540\"^^<xsd:long>"
        + " AGGREGATE ?dialact ?time = LGetLatest2 ?da ?t ?t \"1\"^^<xsd:int>");
    checkResult(fc, bt, expected, "?dialact", "?time");
   System.out.println(bt);
  }

  @Test
  public void test19() throws QueryParseException {
    Query q = new Query(fc.tupleStore);
    BindingTable bt = q.query("SELECT ?da ?t WHERE ?da <rdf:type> <dafn:DialogueAct>"
        + " & ?da <dafn:happens> ?t FILTER LGreaterEqual ?t \"540\"^^<xsd:long>"
        + " AGGREGATE ?dialact ?time = LGetLatest2 ?da ?t ?t \"0\"^^<xsd:int>");
    assertEquals(0, bt.size());
  }

  @Test
  public void test20() throws QueryParseException {
   Query q = new Query(fc.tupleStore);
    BindingTable bt = q.query("SELECT ?da ?t WHERE ?da <rdf:type> <dafn:DialogueAct>"
        + " & ?da <dafn:happens> ?t FILTER LGreaterEqual ?t \"1000\"^^<xsd:long>"
        + " AGGREGATE ?dialact ?time = LGetLatest2 ?da ?t ?t \"3\"^^<xsd:int>");
    assertEquals(0, bt.size());
  }

  @Test
  public void test21() throws QueryParseException {
    String[][] expected = {{ "<hst:da8>" },
        { "<hst:da7>" },
        { "<hst:da6>" },
    };
    // single-valued aggregate
    Query q = new Query(fc.tupleStore);
    BindingTable bt = q.query("SELECT ?da ?t WHERE ?da <rdf:type> <dafn:DialogueAct>"
        + " & ?da <dafn:happens> ?t FILTER LGreaterEqual ?t \"548\"^^<xsd:long>"
        + " AGGREGATE ?dialact = LGetLatest2 ?da ?t \"3\"^^<xsd:int>");
    checkResult(fc, bt, expected, "?dialact");
   System.out.println(bt);
  }

  @Test
  public void test22() throws QueryParseException {
    Query q = new Query(fc.tupleStore);
    BindingTable bt = q.query("SELECT ?da ?t WHERE ?da <rdf:type> <dafn:DialogueAct>"
        + " & ?da <dafn:happens> ?t FILTER LGreaterEqual ?t \"1000\"^^<xsd:long>"
        + " AGGREGATE ?dialact = LGetLatest2 ?da ?t \"3\"^^<xsd:int>");
    assertEquals(0, bt.size());
  }

  // a three-valued returner
  @Test
  public void test23() throws QueryParseException {
    String[][] expected = {
        { "<hst:da8>", "\"755\"^^<xsd:long>", "\"755\"^^<xsd:long>" },
        { "<hst:da7>", "\"731\"^^<xsd:long>", "\"731\"^^<xsd:long>" },
        { "<hst:da6>", "\"686\"^^<xsd:long>", "\"686\"^^<xsd:long>" },
    };

    Query q = new Query(fc.tupleStore);
    BindingTable bt = q.query("SELECT ?da ?t WHERE ?da <rdf:type> <dafn:DialogueAct>"
        + " & ?da <dafn:happens> ?t FILTER LGreaterEqual ?t \"548\"^^<xsd:long>"
        + " AGGREGATE ?dialact ?begin ?end = LGetLatest2 ?da ?t ?t ?t  \"3\"^^<xsd:int>");
    checkResult(fc, bt, expected, "?dialact", "?begin", "?end");
  }

  // This tests separates LGetLatest2 from LGetLatest
  @Test
  public void test24()
      throws QueryParseException, FileNotFoundException, WrongFormatException, IOException {
    String[][] expected = {
        { "<hst:da8>", "\"755\"^^<xsd:long>", "\"755\"^^<xsd:long>" },
        { "<hst:da7>", "\"731\"^^<xsd:long>", "\"731\"^^<xsd:long>" },
        { "<hst:da88>", "\"755\"^^<xsd:long>", "\"755\"^^<xsd:long>" },
        { "<hst:da77>", "\"731\"^^<xsd:long>", "\"731\"^^<xsd:long>" },
        { "<hst:da6>", "\"686\"^^<xsd:long>", "\"686\"^^<xsd:long>" },
    };

    String[][] newTuples = {
        {"<hst:da88>", "<rdf:type>", "<dafn:DialogueAct>"},
        {"<hst:da88>", "<dafn:happens>", "\"755\"^^<xsd:long>"},
        {"<hst:da77>", "<rdf:type>", "<dafn:DialogueAct>"},
        {"<hst:da77>", "<dafn:happens>", "\"731\"^^<xsd:long>"},
    };
    ForwardChainer localfc =  new ForwardChainer(4,           // #cores
        false,                                                // verbose
        true,                                                 // RDF Check
        false,                                                // EQ reduction disabled
        3,                                                    // min #args
        5,                                                    // max #args
        100000,                                               // #atoms
        500000,                                               // #tuples
        getResource("default.nt"),                            // tuple file
        getResource("default.rdl"),                           // rule file
        getResource("default.ns")                             // namespace file
        );

    // upload instance test files
    localfc.uploadTuples(getResource("time.nt"));
    localfc.uploadTuples(getResource("upper.nt"));
    localfc.uploadTuples(getResource("domain.nt"));
    localfc.uploadTuples(getResource("dialframe.nt"));
    localfc.uploadTuples(getResource("test.data.nt"));
    localfc.computeClosure();

    for (String[] t : newTuples) {
      localfc.tupleStore.addTuple(t);
    }
    Query q = new Query(localfc.tupleStore);
    BindingTable bt = q.query("SELECT ?da ?t WHERE ?da <rdf:type> <dafn:DialogueAct>"
        + " & ?da <dafn:happens> ?t FILTER LGreaterEqual ?t \"548\"^^<xsd:long>"
        + " AGGREGATE ?dialact ?begin ?end = LGetLatest2 ?da ?t ?t ?t  \"3\"^^<xsd:int>");
    checkResult(localfc, bt, expected, "?dialact", "?begin", "?end");
    localfc.shutdownNoExit();
  }

}
