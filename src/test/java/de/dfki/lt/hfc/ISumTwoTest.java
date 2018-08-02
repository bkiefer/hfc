package de.dfki.lt.hfc;
import static org.junit.Assert.*;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static de.dfki.lt.hfc.TestingUtils.checkResult;
import static de.dfki.lt.hfc.TestingUtils.checkResult;



public class ISumTwoTest {
  static ForwardChainer fc;

  private static String getResource(String name) {
    return TestingUtils.getTestResource("ISum2", name);
  }
  @BeforeClass
  public static void init() throws Exception {

    fc =  new ForwardChainer(Config.getInstance(getResource("ISum2.yml")));

    // compute deductive closure

    fc.computeClosure();

  }

  @Test
  public void test() throws QueryParseException  {
    // TODO: FIX EXPECTED DATA

    String[][] expected = {
        { "<test:sri>", "<test:hasName>", "\"SRI\"^^<xsd:string>" },
        { "_:de.dfki.lt.hfc.ForwardChainer@45283ce24", "<test:numberOfEmployees>", "\"61000\"^^<xsd:int>" },
        { "<owl:Thing>", "<rdf:type>", "<owl:Class>" },
        { "<test:sri>", "<test:numberOfEmployees>", "\"1000\"^^<xsd:int>" },
        { "<xsd:int>", "<rdf:type>", "<rdfs:Datatype>" },
        { "<test:Company>", "<rdfs:subClassOf>", "<owl:Thing>" },
        { "<test:db>", "<test:numberOfEmployees>", "\"60000\"^^<xsd:int>" },
        { "_:de.dfki.lt.hfc.ForwardChainer@45283ce22", "<test:numberOfEmployees>", "\"60500\"^^<xsd:int>" },
        { "<test:dfki>", "<test:hasName>", "\"DFKI\"^^<xsd:string>" },
        { "<test:sri>", "<rdf:type>", "<test:Company>" },
        { "<test:dfki>", "<test:hasName>", "\"Deutsches Forschungszentrum für Künstliche Intelligenz\"@de" },
        { "<owl:Thing>", "<owl:disjointWith>", "<owl:Nothing>" },
        { "<test:db>", "<rdf:type>", "<test:Company>" },
        { "_:de.dfki.lt.hfc.ForwardChainer@45283ce25", "<test:numberOfEmployees>", "\"60500\"^^<xsd:int>" },
        { "_:de.dfki.lt.hfc.ForwardChainer@45283ce21", "<test:numberOfEmployees>", "\"1500\"^^<xsd:int>" },
        { "<owl:Nothing>", "<rdf:type>", "<owl:Class>" },
        { "_:de.dfki.lt.hfc.ForwardChainer@45283ce23", "<test:numberOfEmployees>", "\"1500\"^^<xsd:int>" },
        { "<test:sri>", "<test:hasName>", "\"Stanford Research Institute\"^^<xsd:string>" },
        { "<test:dfki>", "<rdf:type>", "<test:Company>" },
        { "_:de.dfki.lt.hfc.ForwardChainer@45283ce20", "<test:numberOfEmployees>", "\"61000\"^^<xsd:int>" },
        { "<test:dfki>", "<test:hasName>", "\"DFKI GmbH\"^^<xsd:string>" },
        { "<owl:Nothing>", "<rdfs:subClassOf>", "<owl:Thing>" },
        { "<test:dfki>", "<test:hasName>", "\"German Research Center for Artificial Inteligence\"@en" },
        { "<test:db>", "<test:hasName>", "\"Daimler Benz\"^^<xsd:string>" },
        { "<xsd:string>", "<rdf:type>", "<rdfs:Datatype>" },
        { "<test:adder>", "<rdf:type>", "<owl:Thing>" },
        { "<test:dfki>", "<test:numberOfEmployees>", "\"500\"^^<xsd:int>" },

    };
    Query q = new Query(fc.tupleStore);
    BindingTable bt = q.query("SELECT ?s ?p ?o WHERE ?s ?p ?o");
    //TestUtils.printExpected(bt, fc.tupleStore); // TODO: THIS SHOULD BE REMOVED WHEN FINISHED
    checkResult(expected, bt, bt.getVars());
  }

  @AfterClass
  public static void finish() {
    fc.shutdownNoExit();
  }

}






