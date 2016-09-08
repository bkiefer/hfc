package de.dfki.lt.hfc;
import static org.junit.Assert.*;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static de.dfki.lt.hfc.Utils.checkResult;
import static de.dfki.lt.hfc.Utils.checkResult;



public class MegaclusterTest {
  static ForwardChainer fc;

  private static String getResource(String name) {
    return Utils.getTestResource("Megacluster", name);
  }
  @BeforeClass
  public static void init() throws Exception {

    fc = new ForwardChainer(4,         // #cores
      false,                           // verbose
      true,                            // RDF Check
      false,                           // EQ reduction disabled
      3,                               // min #args
      3,                               // max #args
      100000,                          // #atoms
      500000,                          // #tuples
      getResource("megacluster.nt"),   // tuple file
      getResource("megacluster.rdl"),  // rule file
      getResource("megacluster.ns")    // namespace file
      );

    // compute deductive closure

    fc.computeClosure();

  }

  @Test
  public void test() throws QueryParseException  {
    // TODO: FIX EXPECTED DATA

    String[][] expected = {
        { "<test:dfki>", "<test:hasName>", "\"DFKI\"^^<xsd:string>" },
        { "<test:sri>", "<test:hasName>", "\"SRI\"^^<xsd:string>" },
        { "<test:db>", "<test:hasName>", "\"Daimler Benz\"^^<xsd:string>" },
        { "<test:sri>", "<test:test>", "<owl:Class>" },
        { "<test:dfki>", "<test:test>", "<rdfs:Datatype>" },
        { "<test:db>", "<test:test>", "<rdfs:Datatype>" },
        { "<owl:Thing>", "<owl:disjointWith>", "<owl:Nothing>" },
        { "<test:dfki>", "<rdf:type>", "<test:Company>" },
        { "<test:dfki>", "<test:hasName>", "\"German Research Center for Artificial Inteligence\"@en" },
        { "<test:dfki>", "<test:hasName>", "\"DFKI GmbH\"^^<xsd:string>" },
        { "<owl:Nothing>", "<rdf:type>", "<owl:Class>" },
        { "<test:db>", "<rdf:type>", "<test:Company>" },
        { "<test:dfki>", "<test:test>", "<test:Company>" },
        { "<test:sri>", "<rdf:type>", "<test:Company>" },
        { "<owl:Thing>", "<rdf:type>", "<owl:Class>" },
        { "<test:db>", "<test:test>", "<owl:Class>" },
        { "<xsd:string>", "<rdf:type>", "<rdfs:Datatype>" },
        { "<test:db>", "<test:test>", "<test:Company>" },
        { "<test:dfki>", "<test:hasName>", "\"Deutsches Forschungszentrum für Künstliche Intelligenz\"@de" },
        { "<test:sri>", "<test:test>", "<rdfs:Datatype>" },
        { "<xsd:int>", "<rdf:type>", "<rdfs:Datatype>" },
        { "<test:sri>", "<test:test>", "<test:Company>" },
        { "<owl:Nothing>", "<rdfs:subClassOf>", "<owl:Thing>" },
        { "<test:Company>", "<rdfs:subClassOf>", "<owl:Thing>" },
        { "<test:sri>", "<test:hasName>", "\"Stanford Research Institute\"^^<xsd:string>" },
        { "<test:dfki>", "<test:test>", "<owl:Class>" },
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







