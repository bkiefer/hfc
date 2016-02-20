package de.dfki.lt.hfc;
import static org.junit.Assert.*;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static de.dfki.lt.hfc.TestUtils.checkResult;

public class ConcatenateTwo {
  static ForwardChainer fc;

  private static String getResource(String name) {
    return TestUtils.getTestResource("Concatenate2", name);
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
        getResource("concatenate.nt"),                            // tuple file
        getResource("concatenate.rdl"),                           // rule file
        getResource("concatenate.ns")                             // namespace file
        );

    // compute deductive closure
    fc.computeClosure();
  }

  @Test
  public void test() throws QueryParseException  {
    // TODO: FIX EXPECTED DATA

    String[][] expected = {
        { "<test:db>", "<test:hasName>", "\"Daimler Benz\"^^<xsd:string>" },
        { "<owl:Thing>", "<rdf:type>", "<owl:Class>" },
        { "<owl:Thing>", "<owl:disjointWith>", "<owl:Nothing>" },
        { "<test:Company>", "<rdfs:subClassOf>", "<owl:Thing>" },
        { "<test:sri>", "<test:hasName>", "\"SRI\"^^<xsd:string>" },
        { "<xsd:int>", "<rdf:type>", "<rdfs:Datatype>" },
        { "<test:dfki>", "<test:hasName>", "\"German Research Center for Artificial Inteligence\"@en" },
        { "<test:dfki>", "<test:hasName>", "\"Deutsches Forschungszentrum für Künstliche Intelligenz\"@de" },
        { "<test:db>", "<rdf:type>", "<test:Company>" },
        { "<owl:Nothing>", "<rdfs:subClassOf>", "<owl:Thing>" },
        { "<test:sri>", "<test:hasName>", "\"Stanford Research Institute\"^^<xsd:string>" },
        { "<test:sri>", "<rdf:type>", "<test:Company>" },
        { "<test:dfki>", "<test:hasDoubleName>", "\"German Research Center for Artificial InteligenceGerman Research Center for Artificial Inteligence\"^^<xsd:string>" },
        { "<xsd:string>", "<rdf:type>", "<rdfs:Datatype>" },
        { "<test:sri>", "<test:hasDoubleName>", "\"Stanford Research InstituteStanford Research Institute\"^^<xsd:string>" },
        { "<test:dfki>", "<test:hasName>", "\"DFKI GmbH\"^^<xsd:string>" },
        { "<test:dfki>", "<test:hasDoubleName>", "\"DFKIDFKI\"^^<xsd:string>" },
        { "<test:sri>", "<test:hasDoubleName>", "\"SRISRI\"^^<xsd:string>" },
        { "<test:dfki>", "<test:hasDoubleName>", "\"Deutsches Forschungszentrum für Künstliche IntelligenzDeutsches Forschungszentrum für Künstliche Intelligenz\"^^<xsd:string>" },
        { "<test:dfki>", "<test:hasDoubleName>", "\"DFKI GmbHDFKI GmbH\"^^<xsd:string>" },
        { "<test:dfki>", "<rdf:type>", "<test:Company>" },
        { "<test:dfki>", "<test:hasName>", "\"DFKI\"^^<xsd:string>" },
        { "<owl:Nothing>", "<rdf:type>", "<owl:Class>" },

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

