package de.dfki.lt.hfc;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static de.dfki.lt.hfc.TestingUtils.checkResult;
import static de.dfki.lt.hfc.TestingUtils.printExpected;


public class PrintTrueTest {
  static Hfc fc;

  private static String getResource(String name) {
    return TestingUtils.getTestResource("PrintTrue", name);
  }

  @BeforeClass
  public static void init() throws Exception {

    fc =  new Hfc(Config.getInstance(getResource("PrintTrue.yml")));

    // compute deductive closure

    fc.computeClosure();

  }

  @Test
  public void test() throws QueryParseException  {

    String[][] expected = {
        { "<xsd:int>", "<rdf:type>", "<rdfs:Datatype>" },
        { "<owl:Thing>", "<owl:disjointWith>", "<owl:Nothing>" },
        { "<owl:Thing>", "<rdf:type>", "<owl:Class>" },
        { "<test:db>", "<test:hasName>", "\"Daimler Benz\"^^<xsd:string>" },
        { "<owl:Nothing>", "<rdf:type>", "<owl:Class>" },
        { "<owl:Nothing>", "<rdfs:subClassOf>", "<owl:Thing>" },
        { "<test:dfki>", "<test:new>", "_:de.dfki.lt.hfc.ForwardChainer@2669b1990" },
        { "<xsd:string>", "<rdf:type>", "<rdfs:Datatype>" },
        { "<test:dfki>", "<test:hasName>", "\"DFKI GmbH\"^^<xsd:string>" },
        { "<test:dfki>", "<test:hasName>", "\"German Research Center for Artificial Inteligence\"@en" },
        { "<test:dfki>", "<test:hasName>", "\"DFKI\"^^<xsd:string>" },
        { "<test:dfki>", "<test:hasName>", "\"Deutsches Forschungszentrum für Künstliche Intelligenz\"@de" },
        { "<test:sri>", "<rdf:type>", "<test:Company>" },
        { "<test:sri>", "<test:hasName>", "\"Stanford Research Institute\"^^<xsd:string>" },
        { "<test:db>", "<rdf:type>", "<test:Company>" },
        { "<test:sri>", "<test:new>", "_:de.dfki.lt.hfc.ForwardChainer@2669b1991" },
        { "<test:Company>", "<rdfs:subClassOf>", "<owl:Thing>" },
        { "<test:sri>", "<test:hasName>", "\"SRI\"^^<xsd:string>" },
        { "<test:dfki>", "<rdf:type>", "<test:Company>" },

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









