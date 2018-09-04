package de.dfki.lt.hfc;

import static org.junit.Assert.*;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static de.dfki.lt.hfc.TestingUtils.*;

public class BlankNodeOneTest {
  static ForwardChainer fc;

  private static String getResource(String name) {
    return TestingUtils.getTestResource("BlankNode1", name);
  }

  /**
   * for test purposes, I have (currently) switched off the equivalence class reduction
   *   TupleStore.equivalenceClassReduction == false
   * and have restricted ourselves to pure RDF triples:
   *   TupleStore.minNoOfArgs == 3
   *   TupleStore.maxNoOfArgs == 3
   * these settings are already defined below in the below constructor !
   *
   * note that temporal information is represented through the dafn:happens property, so
   * that we can restrict ourselved to the standard RDFS & OWL entailment rule set
   *
   * compile with
   *   javac -cp .:../jars/hfc.jar:../lib/trove-2.1.0.jar Template.java
   * run with
   *   java -server -cp .:../jars/hfc.jar:../lib/trove-2.1.0.jar -Xms800m -Xmx1200m Template
   *
   *
   * @author (C) Hans-Ulrich Krieger
   * @version Mon Jan  4 10:11:02 CET 2016
   */
  @BeforeClass
  public static void init() throws Exception {

    fc =  new ForwardChainer(Config.getInstance(getResource("BlankNode1.yml")));

    // compute deductive closure
    fc.computeClosure();
  }

  /**
   * @throws QueryParseException
   */
  @Test
  public void test() throws QueryParseException  {

    String[][] expected = {
        { "<test:sri>", "<test:hasName>", "\"SRI\"^^<xsd:string>" },
        { "<owl:Nothing>", "<rdfs:subClassOf>", "<owl:Thing>" },
        { "<xsd:int>", "<rdf:type>", "<rdfs:Datatype>" },
        { "<test:dfki>", "<rdf:type>", "<test:Company>" },
        { "<test:dfki>", "<test:hasName>", "\"DFKI GmbH\"^^<xsd:string>" },
        { "<test:dfki>", "<test:hasName>", "\"German Research Center for Artificial Inteligence\"@en" },
        { "<test:db>", "<test:hasName>", "\"Daimler Benz\"^^<xsd:string>" },
        { "<test:dfki>", "<test:hasName>", "\"DFKI\"^^<xsd:string>" },
        { "<xsd:string>", "<rdf:type>", "<rdfs:Datatype>" },
        { "<owl:Nothing>", "<rdf:type>", "<owl:Class>" },
        { "<test:dfki>", "<test:hasName>", "\"Deutsches Forschungszentrum für Künstliche Intelligenz\"@de" },
        { "<owl:Thing>", "<owl:disjointWith>", "<owl:Nothing>" },
        { "<test:sri>", "<test:hasName>", "\"Stanford Research Institute\"^^<xsd:string>" },
        { "<test:db>", "<rdf:type>", "<test:Company>" },
        { "<test:sri>", "<rdf:type>", "<test:Company>" },
        { "<test:Company>", "<rdfs:subClassOf>", "<owl:Thing>" },
        { "<test:sri>", "<test:new>", "_:de.dfki.lt.hfc.ForwardChainer@77a567e11" },
        { "<test:db>", "<test:new>", "_:de.dfki.lt.hfc.ForwardChainer@77a567e12" },
        { "<owl:Thing>", "<rdf:type>", "<owl:Class>" },
        { "<test:dfki>", "<test:new>", "_:de.dfki.lt.hfc.ForwardChainer@77a567e10" },
        //{ "<hst:da8>", "\"755\"^^<xsd:long>", "\"755\"^^<xsd:long>" },
        //{ "<hst:da7>", "\"731\"^^<xsd:long>", "\"731\"^^<xsd:long>" },
        //{ "<hst:da6>", "\"686\"^^<xsd:long>", "\"686\"^^<xsd:long>" },
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
