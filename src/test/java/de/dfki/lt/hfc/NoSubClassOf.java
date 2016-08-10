package de.dfki.lt.hfc;
import static org.junit.Assert.*;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static de.dfki.lt.hfc.TestUtils.*;

public class NoSubClassOf {
  static ForwardChainer fc;

  private static String getResource(String name) {
    return TestUtils.getTestResource("NoSubClassOf", name);
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
        getResource("nosubclassof.nt"),                            // tuple file
        getResource("nosubclassof.rdl"),                           // rule file
        getResource("nosubclassof.ns")                             // namespace file
        );

    // compute deductive closure

    fc.computeClosure();

  }

  @Test
  public void test() throws QueryParseException  {
    // TODO: FIX EXPECTED DATA
    //test fails
    String[][] expected = {
        { "<test:ResearchInstitute>", "<rdfs:subClassOf>", "<test:Company>" },
        { "<test:a180>", "<test:isAffiliatedWith>", "<test:db>" },
        { "<xsd:int>", "<rdf:type>", "<rdfs:Datatype>" },
        { "_:de.dfki.lt.hfc.ForwardChainer@45283ce21", "<rdf:type>", "<owl:Nothing>" },
        { "<test:dfki>", "<test:isAffiliatedWith>", "<test:livinge>" },
        { "<test:db>", "<rdf:type>", "<test:Company>" },
        { "<test:isAffiliatedWith>", "<rdfs:domain>", "<test:Company>" },
        { "_:de.dfki.lt.hfc.ForwardChainer@45283ce21", "<rdf:predicate>", "<test:isAffiliatedWith>" },
        { "<test:dfki>", "<rdf:type>", "<test:ResearchInstitute>" },
        { "<owl:Thing>", "<owl:disjointWith>", "<owl:Nothing>" },
        { "<test:Car>", "<rdfs:subClassOf>", "<owl:Thing>" },
        { "<test:Company>", "<rdfs:subClassOf>", "<owl:Thing>" },
        { "<test:sri>", "<test:isAffiliatedWith>", "<test:dfki>" },
        { "<test:ResearchInstitute>", "<rdfs:subClassOf>", "<test:ResearchInstitute>" },
        { "<test:livinge>", "<test:isAffiliatedWith>", "<test:dfki>" },
        { "_:de.dfki.lt.hfc.ForwardChainer@45283ce21", "<hfc:reason>", "\"$rangeRestrictionViolated\"^^<xsd:string>" },
        { "<xsd:string>", "<rdf:type>", "<rdfs:Datatype>" },
        { "<test:sri>", "<rdf:type>", "<test:ResearchInstitute>" },
        { "<owl:Nothing>", "<rdf:type>", "<owl:Class>" },
        { "<owl:Thing>", "<rdf:type>", "<owl:Class>" },
        { "<test:isAffiliatedWith>", "<rdfs:range>", "<test:Company>" },
        { "<test:Company>", "<rdfs:subClassOf>", "<test:Company>" },
        { "<test:db>", "<test:isAffiliatedWith>", "<test:a180>" },
        { "_:de.dfki.lt.hfc.ForwardChainer@45283ce20", "<hfc:reason>", "\"$domainRestrictionViolated\"^^<xsd:string>" },
        { "_:de.dfki.lt.hfc.ForwardChainer@45283ce20", "<rdf:predicate>", "<test:isAffiliatedWith>" },
        { "<test:a180>", "<rdf:type>", "<test:Car>" },
        { "<test:dfki>", "<test:isAffiliatedWith>", "<test:sri>" },
        { "_:de.dfki.lt.hfc.ForwardChainer@45283ce20", "<rdf:type>", "<owl:Nothing>" },
        { "_:de.dfki.lt.hfc.ForwardChainer@45283ce21", "<rdf:subject>", "<test:db>" },
        { "_:de.dfki.lt.hfc.ForwardChainer@45283ce21", "<rdf:object>", "<test:a180>" },
        { "_:de.dfki.lt.hfc.ForwardChainer@45283ce20", "<rdf:subject>", "<test:a180>" },
        { "<test:livinge>", "<rdf:type>", "<test:Company>" },
        { "<owl:Nothing>", "<rdfs:subClassOf>", "<owl:Thing>" },
        { "_:de.dfki.lt.hfc.ForwardChainer@45283ce20", "<rdf:object>", "<test:db>" },

    };
    Query q = new Query(fc.tupleStore);
    BindingTable bt = q.query("SELECT ?s ?p ?o WHERE ?s ?p ?o");
    // printExpected(bt, fc.tupleStore); // TODO: THIS SHOULD BE REMOVED WHEN FINISHED
    checkResult(expected, bt, bt.getVars());
  }

  @AfterClass
  public static void finish() {
    fc.shutdownNoExit();
  }

}







