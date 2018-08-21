package de.dfki.lt.hfc;
import static de.dfki.lt.hfc.TestingUtils.printExpected;
import static org.junit.Assert.*;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static de.dfki.lt.hfc.TestingUtils.checkResult;
import static de.dfki.lt.hfc.TestingUtils.checkResult;



public class RelationalVariableTest {
  static ForwardChainer fc;

  private static String getResource(String name) {
    return TestingUtils.getTestResource("RelationalVariable", name);
  }
  @BeforeClass
  public static void init() throws Exception {

    fc =  new ForwardChainer(Config.getInstance(getResource("RelationalVariable.yml")));
    // compute deductive closure
    fc.computeClosure();
  }

  @Test
  public void test() throws QueryParseException  {
    String[][] expected = {
//      { "_:b2X000", "<owl:onProperty>", "<test:r>" },
//      { "_:b2X001", "<owl:onProperty>", "<test:r>" },
        { "_:b2", "<owl:onProperty>", "<test:r>" },
//      { "_:b1X000", "<rdf:type>", "<owl:Restriction>" },
//      { "_:b1X001", "<rdf:type>", "<owl:Restriction>" },
        { "_:b1", "<rdf:type>", "<owl:Restriction>" },
//      { "<test:foo>", "<rdf:type>", "<owl:Nothing>" },
        { "<test:foo>", "<rdf:type>", "<owl:Nothing>" },
//      { "_:b1X000", "<owl:cardinality>", ""1"^^<xsd:int>" },
//      { "_:b1X001", "<owl:cardinality>", ""1"^^<xsd:int>" },
        { "_:b1", "<owl:cardinality>", "\"1\"^^<xsd:int>" },
//      { "<test:q>", "<rdf:type>", "<owl:DatatypeProperty>" },
        { "<test:q>", "<rdf:type>", "<owl:DatatypeProperty>" },
//      { "<test:r>", "<rdf:type>", "<owl:DatatypeProperty>" },
        { "<test:r>", "<rdf:type>", "<owl:DatatypeProperty>" },
//      { "<test:bar>", "<test:q>", ""40"^^<xsd:int>" },
        { "<test:bar>", "<test:q>", "\"40\"^^<xsd:int>" },
//      { "_:b2X001", "<rdf:type>", "<owl:Restriction>" },
//      { "_:b2X000", "<rdf:type>", "<owl:Restriction>" },
        { "_:b2", "<rdf:type>", "<owl:Restriction>" },
//      { "<test:foo>", "<test:q>", ""20"^^<xsd:int>" },
        { "<test:foo>", "<test:q>", "\"20\"^^<xsd:int>" },
//      { "<test:foo>", "<test:q>", ""10"^^<xsd:int>" },
        { "<test:foo>", "<test:q>", "\"10\"^^<xsd:int>" },
//      { "_:b2X000", "<owl:cardinality>", ""1"^^<xsd:int>" },
//      { "_:b2X001", "<owl:cardinality>", ""1"^^<xsd:int>" },
        { "_:b2", "<owl:cardinality>", "\"1\"^^<xsd:int>" },
//      { "<test:foo>", "<test:r>", ""30"^^<xsd:int>" },
        { "<test:foo>", "<test:r>", "\"30\"^^<xsd:int>" },
//      { "<test:bar>", "<test:r>", ""50"^^<xsd:int>" },
        { "<test:bar>", "<test:r>", "\"50\"^^<xsd:int>" },
//      { "_:b1X000", "<owl:onProperty>", "<test:q>" },
//      { "_:b1X001", "<owl:onProperty>", "<test:q>" },
        { "_:b1", "<owl:onProperty>", "<test:q>" },

    };
    Query q = new Query(fc.tupleStore);
    BindingTable bt = q.query("SELECT ?s ?p ?o WHERE ?s ?p ?o");
    //printExpected(bt, fc.tupleStore); // TODO: THIS SHOULD BE REMOVED WHEN FINISHED
    checkResult(expected, bt, bt.getVars());
  }

  @AfterClass
  public static void finish() {
    fc.shutdownNoExit();
  }

}










