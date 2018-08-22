package de.dfki.lt.hfc;
import static de.dfki.lt.hfc.TestingUtils.printExpected;
import static org.junit.Assert.*;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static de.dfki.lt.hfc.TestingUtils.checkResult;
import static de.dfki.lt.hfc.TestingUtils.checkResult;



public class ConcatenateFourTest {
  static ForwardChainer fc;

  private static String getResource(String name) {
    return TestingUtils.getTestResource("Concatenate4", name);
  }
  @BeforeClass
  public static void init() throws Exception {

    fc =  new ForwardChainer(Config.getInstance(getResource("Concatenate4.yml")));

    // compute deductive closure
    fc.computeClosure();
  }

  @Test
  public void test() throws QueryParseException  {
    // TODO: FIX EXPECTED DATA
    System.out.println(fc.tupleStore.idToJavaObject);
    System.out.println(fc.tupleStore.objectToId);
    String[][] expected = {
        { "<test:dfki>", "<test:hasName>", "\"DFKI\"^^<xsd:string>" },
        { "<test:dfki>", "<test:hasDoubleName>", "\"German Research Center for Artificial InteligenceGerman Research Center for Artificial Inteligence\"^^<xsd:string>" },
        { "<owl:Nothing>", "<rdf:type>", "<owl:Class>" },
        { "<test:sri>", "<test:hasName>", "\"SRI\"^^<xsd:string>" },
        { "<test:dfki>", "<test:hasName>", "\"German Research Center for Artificial Inteligence\"@en" },
        { "<test:db>", "<test:hasDoubleName>", "\"Daimler BenzDaimler Benz\"^^<xsd:string>" },
        { "<test:dfki>", "<test:hasName>", "\"DFKI GmbH\"^^<xsd:string>" },
        { "<xsd:string>", "<rdf:type>", "<rdfs:Datatype>" },
        { "<test:dfki>", "<test:hasName>", "\"Deutsches Forschungszentrum für Künstliche Intelligenz\"@de" },
        { "<test:dfki>", "<test:hasDoubleName>", "\"DFKI GmbHDFKI GmbH\"^^<xsd:string>" },
        { "<test:sri>", "<test:hasName>", "\"Stanford Research Institute\"^^<xsd:string>" },
        { "<test:dfki>", "<rdf:type>", "<test:Company>" },
        { "<test:sri>", "<test:hasDoubleName>", "\"SRISRI\"^^<xsd:string>" },
        { "<test:db>", "<test:hasName>", "\"Daimler Benz\"^^<xsd:string>" },
        { "<test:Company>", "<rdfs:subClassOf>", "<owl:Thing>" },
        { "<xsd:int>", "<rdf:type>", "<rdfs:Datatype>" },
        { "<test:sri>", "<rdf:type>", "<test:Company>" },
        { "<owl:Nothing>", "<rdfs:subClassOf>", "<owl:Thing>" },
        { "<test:dfki>", "<test:hasDoubleName>", "\"DFKIDFKI\"^^<xsd:string>" },
        { "<test:dfki>", "<test:hasDoubleName>", "\"Deutsches Forschungszentrum für Künstliche IntelligenzDeutsches Forschungszentrum für Künstliche Intelligenz\"^^<xsd:string>" },
        { "<owl:Thing>", "<owl:disjointWith>", "<owl:Nothing>" },
        { "<test:db>", "<rdf:type>", "<test:Company>" },
        { "<test:sri>", "<test:hasDoubleName>", "\"Stanford Research InstituteStanford Research Institute\"^^<xsd:string>" },
        { "<owl:Thing>", "<rdf:type>", "<owl:Class>" },

    };

    Query q = new Query(fc.tupleStore);
    BindingTable bt = q.query("SELECT ?s ?p ?o WHERE ?s ?p ?o");
    printExpected(bt, fc.tupleStore); // TODO: THIS SHOULD BE REMOVED WHEN FINISHED
    checkResult(expected, bt, bt.getVars());
  }

  @AfterClass
  public static void finish() {
    fc.shutdownNoExit();
  }

}


