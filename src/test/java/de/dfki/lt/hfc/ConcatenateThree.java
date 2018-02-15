package de.dfki.lt.hfc;


import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static de.dfki.lt.hfc.TestUtils.checkResult;

public class ConcatenateThree {
  static ForwardChainer fc;

  private static String getResource(String name) {
    return TestUtils.getTestResource("Concatenate3", name);
  }

  @BeforeAll
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
  public void test() throws QueryParseException {
    // TODO: FIX EXPECTED DATA

    String[][] expected = {
        { "<test:sri>", "<test:hasName>", "\"Stanford Research Institute\"^^<xsd:string>" },
        { "<test:sri>", "<test:hasDoubleName>", "\"SRIStanford Research Institute\"^^<xsd:string>" },
        { "<test:dfki>", "<test:hasDoubleName>", "\"German Research Center for Artificial InteligenceDFKI GmbH\"^^<xsd:string>" },
        { "<test:sri>", "<rdf:type>", "<test:Company>" },
        { "<test:db>", "<test:hasName>", "\"Daimler Benz\"^^<xsd:string>" },
        { "<xsd:int>", "<rdf:type>", "<rdfs:Datatype>" },
        { "<test:dfki>", "<test:hasDoubleName>", "\"Deutsches Forschungszentrum für Künstliche IntelligenzGerman Research Center for Artificial Inteligence\"^^<xsd:string>" },
        { "<xsd:string>", "<rdf:type>", "<rdfs:Datatype>" },
        { "<test:dfki>", "<test:hasDoubleName>", "\"DFKIGerman Research Center for Artificial Inteligence\"^^<xsd:string>" },
        { "<test:dfki>", "<test:hasDoubleName>", "\"DFKIDFKI\"^^<xsd:string>" },
        { "<test:dfki>", "<test:hasDoubleName>", "\"DFKIDFKI GmbH\"^^<xsd:string>" },
        { "<test:dfki>", "<test:hasName>", "\"Deutsches Forschungszentrum für Künstliche Intelligenz\"@de" },
        { "<test:dfki>", "<test:hasName>", "\"DFKI GmbH\"^^<xsd:string>" },
        { "<test:dfki>", "<test:hasDoubleName>", "\"German Research Center for Artificial InteligenceDFKI\"^^<xsd:string>" },
        { "<test:db>", "<test:hasDoubleName>", "\"Daimler BenzDaimler Benz\"^^<xsd:string>" },
        { "<test:dfki>", "<test:hasDoubleName>", "\"Deutsches Forschungszentrum für Künstliche IntelligenzDFKI GmbH\"^^<xsd:string>" },
        { "<test:sri>", "<test:hasName>", "\"SRI\"^^<xsd:string>" },
        { "<test:dfki>", "<test:hasDoubleName>", "\"DFKI GmbHGerman Research Center for Artificial Inteligence\"^^<xsd:string>" },
        { "<test:dfki>", "<test:hasDoubleName>", "\"DFKI GmbHDFKI GmbH\"^^<xsd:string>" },
        { "<test:dfki>", "<test:hasName>", "\"German Research Center for Artificial Inteligence\"@en" },
        { "<test:sri>", "<test:hasDoubleName>", "\"Stanford Research InstituteSRI\"^^<xsd:string>" },
        { "<owl:Thing>", "<rdf:type>", "<owl:Class>" },
        { "<test:dfki>", "<test:hasDoubleName>", "\"Deutsches Forschungszentrum für Künstliche IntelligenzDFKI\"^^<xsd:string>" },
        { "<test:Company>", "<rdfs:subClassOf>", "<owl:Thing>" },
        { "<test:dfki>", "<test:hasDoubleName>", "\"DFKIDeutsches Forschungszentrum für Künstliche Intelligenz\"^^<xsd:string>" },
        { "<test:sri>", "<test:hasDoubleName>", "\"Stanford Research InstituteStanford Research Institute\"^^<xsd:string>" },
        { "<owl:Thing>", "<owl:disjointWith>", "<owl:Nothing>" },
        { "<test:dfki>", "<rdf:type>", "<test:Company>" },
        { "<test:dfki>", "<test:hasDoubleName>", "\"DFKI GmbHDeutsches Forschungszentrum für Künstliche Intelligenz\"^^<xsd:string>" },
        { "<test:sri>", "<test:hasDoubleName>", "\"SRISRI\"^^<xsd:string>" },
        { "<owl:Nothing>", "<rdf:type>", "<owl:Class>" },
        { "<test:dfki>", "<test:hasDoubleName>", "\"DFKI GmbHDFKI\"^^<xsd:string>" },
        { "<test:dfki>", "<test:hasDoubleName>", "\"Deutsches Forschungszentrum für Künstliche IntelligenzDeutsches Forschungszentrum für Künstliche Intelligenz\"^^<xsd:string>" },
        { "<test:db>", "<rdf:type>", "<test:Company>" },
        { "<test:dfki>", "<test:hasName>", "\"DFKI\"^^<xsd:string>" },
        { "<test:dfki>", "<test:hasDoubleName>", "\"German Research Center for Artificial InteligenceGerman Research Center for Artificial Inteligence\"^^<xsd:string>" },
        { "<test:dfki>", "<test:hasDoubleName>", "\"German Research Center for Artificial InteligenceDeutsches Forschungszentrum für Künstliche Intelligenz\"^^<xsd:string>" },
        { "<owl:Nothing>", "<rdfs:subClassOf>", "<owl:Thing>" },


    };

    Query q = new Query(fc.tupleStore);
    BindingTable bt = q.query("SELECT ?s ?p ?o WHERE ?s ?p ?o");
    //TestLGetLatest.printExpected(bt, fc.tupleStore); // TODO: THIS SHOULD BE REMOVED WHEN FINISHED
    checkResult(expected, bt, bt.getVars());
  }

  @AfterAll
  public static void finish() {
    fc.shutdownNoExit();
  }

}


