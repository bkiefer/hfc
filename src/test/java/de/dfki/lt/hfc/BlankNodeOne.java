package de.dfki.lt.hfc;

import static org.junit.Assert.*;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static de.dfki.lt.hfc.TestUtils.checkResult;

public class BlankNodeOne {
  static ForwardChainer fc;

  private static String getResource(String name) {
    return TestUtils.getTestResource("BlankNode1", name);
  }

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

    fc =  new ForwardChainer(4,                                                    // #cores
        true,                                                 // verbose
        true,                                                 // RDF Check
        false,                                                // EQ reduction disabled
        3,                                                    // min #args
        3,                                                    // max #args
        100000,                                               // #atoms
        500000,                                               // #tuples
        getResource("blanknode.nt"),                            // tuple file
        getResource("blanknode.rdl"),                           // rule file
        getResource("blanknode.ns")                             // namespace file
        );

    // compute deductive closure
    fc.computeClosure();
  }

  @Test
  public void test() throws QueryParseException  {
    // TODO: FIX EXPECTED DATA
    String[][] expected = {
        { "<hst:da8>", "\"755\"^^<xsd:long>", "\"755\"^^<xsd:long>" },
        { "<hst:da7>", "\"731\"^^<xsd:long>", "\"731\"^^<xsd:long>" },
        { "<hst:da6>", "\"686\"^^<xsd:long>", "\"686\"^^<xsd:long>" },
    };

    Query q = new Query(fc.tupleStore);
    BindingTable bt = q.query("SELECT ?s ?p ?o WHERE ?s ?p ?o");
    System.out.println(bt); // TODO: THIS SHOULD BE REMOVED WHEN FINISHED
    assertTrue(checkResult(expected, bt, "?s", "?p", "?o"));
  }

  @AfterClass
  public static void finish() {
    fc.shutdownNoExit();
  }

}
