package de.dfki.lt.hfc;
import static org.junit.Assert.*;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static de.dfki.lt.hfc.TestUtils.checkResult;
import static de.dfki.lt.hfc.TestUtils.checkResult;

public class RelationalVariableTest {
  static ForwardChainer fc;

  private static String getResource(String name) {
    return TestUtils.getTestResource("RelationalVariable", name);
  }
  @BeforeClass
  public static void init() throws Exception {

    fc =  new ForwardChainer(4,                                                    // #cores
        false,                                                 // verbose
        true,                                                 // RDF Check
        false,                                                // EQ reduction disabled
        3,                                                    // min #args
        3,                                                    // max #args
        100000,                                               // #atoms
        500000,                                               // #tuples
        getResource("relvar.nt"),                            // tuple file
        getResource("relvar.rdl"),                           // rule file
        getResource("relvar.ns")                             // namespace file
        );

    // compute deductive closure

    fc.computeClosure();

  }

  @Test
  public void test() throws QueryParseException  {
    // TODO: FIX EXPECTED DATA

    String[][] expected = {
        { "_:b2", "<owl:onProperty>", "<test:r>" },
        { "_:b1", "<rdf:type>", "<owl:Restriction>" },
        { "<test:foo>", "<rdf:type>", "<owl:Nothing>" },
        { "_:b1", "<owl:cardinality>", "\"1\"^^<xsd:int>" },
        { "<test:q>", "<rdf:type>", "<owl:DatatypeProperty>" },
        { "<test:r>", "<rdf:type>", "<owl:DatatypeProperty>" },
        { "<test:bar>", "<test:q>", "\"40\"^^<xsd:int>" },
        { "_:b2", "<rdf:type>", "<owl:Restriction>" },
        { "<test:foo>", "<test:q>", "\"20\"^^<xsd:int>" },
        { "<test:foo>", "<test:q>", "\"10\"^^<xsd:int>" },
        { "_:b2", "<owl:cardinality>", "\"1\"^^<xsd:int>" },
        { "<test:foo>", "<test:r>", "\"30\"^^<xsd:int>" },
        { "<test:bar>", "<test:r>", "\"50\"^^<xsd:int>" },
        { "_:b1", "<owl:onProperty>", "<test:q>" },

    };
    Query q = new Query(fc.tupleStore);
    BindingTable bt = q.query("SELECT ?s ?p ?o WHERE ?s ?p ?o");
    //TestLGetLatest.printExpected(bt, fc.tupleStore); // TODO: THIS SHOULD BE REMOVED WHEN FINISHED
    checkResult(expected, bt, bt.getVars());
  }

  @AfterClass
  public static void finish() {
    fc.shutdownNoExit();
  }

}










