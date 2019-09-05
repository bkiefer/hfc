package de.dfki.lt.hfc;


import org.junit.AfterClass;
import org.junit.BeforeClass;

import static de.dfki.lt.hfc.TestingUtils.*;

public class ISumThreeTest {
  static Hfc fc;

  private static String getResource(String name) {
    return TestingUtils.getTestResource("ISum3", name);
  }
  @BeforeClass
  public static void init() throws Exception {

    fc =  new Hfc(Config.getInstance(getResource("ISum3.yml")));

    // compute deductive closure
    // does not terminate until max iterations are reached (Integer.Max)
    fc.computeClosure();

  }


  public void test() throws QueryParseException  {

    String[][] expected = {
            { "<test:db>", "<test:hasName>", "\"Daimler Benz\"^^<xsd:string>" },
            { "<test:dfki>", "<test:hasName>", "\"Deutsches Forschungszentrum für Künstliche Intelligenz\"@de" },
            { "<test:Company>", "<rdfs:subClassOf>", "<owl:Thing>" },
            { "<test:sri>", "<test:numberOfEmployees>", "\"1000\"^^<xsd:int>" },
            { "<test:sri>", "<test:hasName>", "\"Stanford Research Institute\"^^<xsd:string>" },
            { "<owl:Nothing>", "<rdfs:subClassOf>", "<owl:Thing>" },
            { "<test:adder>", "<rdf:type>", "<owl:Thing>" },
            { "<owl:Thing>", "<rdf:type>", "<owl:Class>" },
            { "<test:db>", "<test:numberOfEmployees>", "\"60000\"^^<xsd:int>" },
            { "<test:dfki>", "<test:hasName>", "\"DFKI\"^^<xsd:string>" },
            { "<test:dfki>", "<test:numberOfEmployees>", "\"500\"^^<xsd:int>" },
            { "<test:db>", "<rdf:type>", "<test:Company>" },
            { "<owl:Nothing>", "<rdf:type>", "<owl:Class>" },
            { "<test:sri>", "<test:hasName>", "\"SRI\"^^<xsd:string>" },
            { "<test:dfki>", "<test:hasName>", "\"German Research Center for Artificial Inteligence\"@en" },
            { "<xsd:string>", "<rdf:type>", "<rdfs:Datatype>" },
            { "<owl:Thing>", "<owl:disjointWith>", "<owl:Nothing>" },
            { "<xsd:int>", "<rdf:type>", "<rdfs:Datatype>" },
            { "<test:dfki>", "<rdf:type>", "<test:Company>" },
            { "<test:dfki>", "<test:hasName>", "\"DFKI GmbH\"^^<xsd:string>" },
            { "<test:sri>", "<rdf:type>", "<test:Company>" },
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







