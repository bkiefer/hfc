package de.dfki.lt.hfc;
import static org.junit.Assert.*;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static de.dfki.lt.hfc.TestingUtils.checkResult;
import static de.dfki.lt.hfc.TestingUtils.checkResult;



public class ConcatenateTwoTest {
  static Hfc fc;

  private static String getResource(String name) {
    return TestingUtils.getTestResource("Concatenate2", name);
  }
  @BeforeClass
  public static void init() throws Exception {


      Config config = Config.getInstance(getResource("Concatenate2.yml"));
      /**
      Map update = new HashMap<>();
      update.put(Config.NOOFCORES, 4);
      update.put(Config.VERBOSE, false);
      update.put(Config.RDFCHECK, true);
      update.put(Config.EQREDUCTION, false);
      update.put(Config.MINARGS, 3);
      update.put(Config.MAXARGS, 3);
      update.put(Config.NOOFATOMS, 100000);
      update.put(Config.NOOFTUPLES, 500000);
      config.updateConfig(update);
        **/

    fc =  new Hfc(config);

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

    Query q = new Query(fc._tupleStore);
    BindingTable bt = q.query("SELECT ?s ?p ?o WHERE ?s ?p ?o");
    //TestLGetLatest.printExpected(bt, fc._tupleStore); // TODO: THIS SHOULD BE REMOVED WHEN FINISHED
    checkResult(expected, bt, bt.getVars());
  }

  @AfterClass
  public static void finish() {
    fc.shutdownNoExit();
  }

}

