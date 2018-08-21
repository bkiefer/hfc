package de.dfki.lt.hfc;


import static de.dfki.lt.hfc.TestingUtils.printExpected;
import static org.junit.Assert.*;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;



import static de.dfki.lt.hfc.TestingUtils.checkResult;
import static de.dfki.lt.hfc.TestingUtils.checkResult;



public class BlankNodeTwoTest {
  static ForwardChainer fc;

  private static String getResource(String name) {
    return TestingUtils.getTestResource("BlankNode2", name);
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

    fc =  new ForwardChainer(Config.getInstance(getResource("BlankNode2.yml")));

    // compute deductive closure
    fc.computeClosure();
  }

  @Test
  public void test() throws QueryParseException  {
    // TODO: FIX EXPECTED DATA

    String[][] expected = {
//      { "<xsd:int>", "<rdf:type>", "<rdfs:Datatype>" },
        { "<xsd:int>", "<rdf:type>", "<rdfs:Datatype>" },
//      { "<test:sri>", "<test:hasName>", ""Stanford Research Institute"^^<xsd:string>" },
        { "<test:sri>", "<test:hasName>", "\"Stanford Research Institute\"^^<xsd:string>"},
//      { "<test:dfki>", "<rdf:type>", "<test:Company>" },
        { "<test:dfki>", "<rdf:type>", "<test:Company>" },
//      { "<test:Company>", "<rdfs:subClassOf>", "<owl:Thing>" },
        { "<test:Company>", "<rdfs:subClassOf>", "<owl:Thing>" },
//      { "<test:db>", "<test:hasName>", ""Daimler Benz"^^<xsd:string>" },
        { "<test:db>", "<test:hasName>", "\"Daimler Benz\"^^<xsd:string>" },
//      { "<test:dfki>", "<test:hasName>", ""DFKI"^^<xsd:string>" },
        { "<test:dfki>", "<test:hasName>", "\"DFKI\"^^<xsd:string>" },
//      { "<test:db>", "<rdf:type>", "<test:Company>" },
        { "<test:db>", "<rdf:type>", "<test:Company>" },
//      { "<test:dfki>", "<test:hasName>", ""DFKI GmbH"^^<xsd:string>" },
        { "<test:dfki>", "<test:hasName>", "\"DFKI GmbH\"^^<xsd:string>" },
//      { "<test:sri>", "<test:new1>", "_:de.dfki.lt.hfc.ForwardChainer@61a485d21" },
//      { "<test:sri>", "<test:new1>", "_:de.dfki.lt.hfc.ForwardChainer@61a485d24" },
        { "<test:sri>", "<test:new1>", "_:de.dfki.lt.hfc.ForwardChainer@45283ce21" },
//      { "<xsd:string>", "<rdf:type>", "<rdfs:Datatype>" },
        { "<xsd:string>", "<rdf:type>", "<rdfs:Datatype>" },
//      { "<owl:Nothing>", "<rdfs:subClassOf>", "<owl:Thing>" },
        { "<owl:Nothing>", "<rdfs:subClassOf>", "<owl:Thing>" },
//      { "<owl:Nothing>", "<rdf:type>", "<owl:Class>" },
        { "<owl:Nothing>", "<rdf:type>", "<owl:Class>" },
//      { "<test:sri>", "<test:hasName>", ""SRI"^^<xsd:string>" },
        { "<test:sri>", "<test:hasName>", "\"SRI\"^^<xsd:string>" },
//      { "<owl:Thing>", "<owl:disjointWith>", "<owl:Nothing>" },
        { "<owl:Thing>", "<owl:disjointWith>", "<owl:Nothing>" },
//      { "<test:sri>", "<test:new2>", "_:de.dfki.lt.hfc.ForwardChainer@61a485d24" },
//      { "<test:sri>", "<test:new2>", "_:de.dfki.lt.hfc.ForwardChainer@61a485d21" },
        { "<test:sri>", "<test:new2>", "_:de.dfki.lt.hfc.ForwardChainer@45283ce21" },
//      { "<test:dfki>", "<test:hasName>", ""Deutsches Forschungszentrum für Künstliche Intelligenz"@de" },
        { "<test:dfki>", "<test:hasName>", "\"Deutsches Forschungszentrum für Künstliche Intelligenz\"@de" },
//      { "<test:sri>", "<rdf:type>", "<test:Company>" },
        { "<test:sri>", "<rdf:type>", "<test:Company>" },
//      { "<test:db>", "<test:new2>", "_:de.dfki.lt.hfc.ForwardChainer@61a485d22" },
//      { "<test:db>", "<test:new2>", "_:de.dfki.lt.hfc.ForwardChainer@61a485d25" },
        { "<test:db>", "<test:new2>", "_:de.dfki.lt.hfc.ForwardChainer@45283ce22" },
//      { "<test:dfki>", "<test:hasName>", ""German Research Center for Artificial Inteligence"@en" },
        { "<test:dfki>", "<test:hasName>", "\"German Research Center for Artificial Inteligence\"@en" },
//      { "<test:db>", "<test:new1>", "_:de.dfki.lt.hfc.ForwardChainer@61a485d22" },
//      { "<test:db>", "<test:new1>", "_:de.dfki.lt.hfc.ForwardChainer@61a485d25" },
        { "<test:db>", "<test:new1>", "_:de.dfki.lt.hfc.ForwardChainer@45283ce22" },
//      { "<owl:Thing>", "<rdf:type>", "<owl:Class>" },
        { "<owl:Thing>", "<rdf:type>", "<owl:Class>" },
//      { "<test:dfki>", "<test:new1>", "_:de.dfki.lt.hfc.ForwardChainer@61a485d23" },
//      { "<test:dfki>", "<test:new1>", "_:de.dfki.lt.hfc.ForwardChainer@61a485d20" },
        { "<test:dfki>", "<test:new1>", "_:de.dfki.lt.hfc.ForwardChainer@45283ce20" },
//      { "<test:dfki>", "<test:new2>", "_:de.dfki.lt.hfc.ForwardChainer@61a485d20" },
//      { "<test:dfki>", "<test:new2>", "_:de.dfki.lt.hfc.ForwardChainer@61a485d23" },
        { "<test:dfki>", "<test:new2>", "_:de.dfki.lt.hfc.ForwardChainer@45283ce20" },

    };

    Query q = new Query(fc.tupleStore);
    BindingTable bt = q.query("SELECT ?s ?p ?o WHERE ?s ?p ?o");
    //printExpected(bt, fc.tupleStore); // TODO: THIS SHOULD BE REMOVED WHEN FINISHED
    checkResult(expected, bt, bt.getVars());
  }

  @AfterClass
  public static void finish() {
    fc.shutdownNoExit();
  }

}

