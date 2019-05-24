package de.dfki.lt.hfc;
import static org.junit.Assert.*;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static de.dfki.lt.hfc.TestingUtils.checkResult;
import static de.dfki.lt.hfc.TestingUtils.checkResult;



public class ISumOneTest {
  static Hfc fc;

  private static String getResource(String name) {
    return TestingUtils.getTestResource("ISum1", name);
  }
  @BeforeClass
  public static void init() throws Exception {

    fc =  new Hfc(Config.getInstance(getResource("ISum1.yml")));

    // compute deductive closure

    fc.computeClosure();

  }

  @Test
  public void test() throws QueryParseException  {

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
    Query q = new Query(fc._tupleStore);
    BindingTable bt = q.query("SELECT ?s ?p ?o WHERE ?s ?p ?o");
    checkResult(expected, bt, bt.getVars());
  }

  @AfterClass
  public static void finish() {
    fc.shutdownNoExit();
  }

}





