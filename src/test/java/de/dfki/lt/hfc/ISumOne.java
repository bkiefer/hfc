package de.dfki.lt.hfc;
import static org.junit.Assert.*;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static de.dfki.lt.hfc.TestUtils.checkResult;

public class ISumOne {
  static ForwardChainer fc;

  private static String getResource(String name) {
    return TestUtils.getTestResource("ISum1", name);
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
        getResource("isum.nt"),                            // tuple file
        getResource("isum.rdl"),                           // rule file
        getResource("isum.ns")                             // namespace file
        );

    // compute deductive closure

    fc.computeClosure();

  }

  @Test
  public void test() throws QueryParseException  {
    // TODO: FIX EXPECTED DATA

    String[][] expected = {

        { "<test:dfki>", "<rdf:type>", "<test:Company>" },
        { "<xsd:string>", "<rdf:type>", "<rdfs:Datatype>" },
        { "<owl:Nothing>", "<rdf:type>", "<owl:Class>" },
        { "<test:adder>", "<test:numberOfEmployees>", "\"60500\"^^<xsd:int>" },
        { "<test:dfki>", "<test:hasName>", "\"German Research Center for Artificial Inteligence\"@en" },
        { "<owl:Thing>", "<owl:disjointWith>", "<owl:Nothing>" },
        { "<test:db>", "<rdf:type>", "<test:Company>" },
        { "<test:dfki>", "<test:numberOfEmployees>", "\"500\"^^<xsd:int>" },
        { "<test:sri>", "<test:hasName>", "\"Stanford Research Institute\"^^<xsd:string>" },
        { "<test:sri>", "<test:numberOfEmployees>", "\"1000\"^^<xsd:int>" },
        { "<test:dfki>", "<test:hasName>", "\"DFKI\"^^<xsd:string>" },
        { "<owl:Thing>", "<rdf:type>", "<owl:Class>" },
        { "<test:sri>", "<rdf:type>", "<test:Company>" },
        { "<test:db>", "<test:numberOfEmployees>", "\"60000\"^^<xsd:int>" },
        { "<test:dfki>", "<test:hasName>", "\"Deutsches Forschungszentrum für Künstliche Intelligenz\"@de" },
        { "<xsd:int>", "<rdf:type>", "<rdfs:Datatype>" },
        { "<test:sri>", "<test:hasName>", "\"SRI\"^^<xsd:string>" },
        { "<owl:Nothing>", "<rdfs:subClassOf>", "<owl:Thing>" },
        { "<test:adder>", "<rdf:type>", "<owl:Thing>" },
        { "<test:adder>", "<test:numberOfEmployees>", "\"61000\"^^<xsd:int>" },
        { "<test:dfki>", "<test:hasName>", "\"DFKI GmbH\"^^<xsd:string>" },
        { "<test:Company>", "<rdfs:subClassOf>", "<owl:Thing>" },
        { "<test:db>", "<test:hasName>", "\"Daimler Benz\"^^<xsd:string>" },
        { "<test:adder>", "<test:numberOfEmployees>", "\"1500\"^^<xsd:int>" },

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





