package de.dfki.lt.hfc;
import static org.junit.Assert.*;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static de.dfki.lt.hfc.TestingUtils.checkResult;



public class IsNotSubtypeOfTest {
  static ForwardChainer fc;

  private static String getResource(String name) {
    return TestingUtils.getTestResource("IsNotSubtypeOf", name);
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
        getResource("isnotsubtypeof.nt"),                            // tuple file
        getResource("isnotsubtypeof.rdl"),                           // rule file
        getResource("isnotsubtypeof.ns")                             // namespace file
        );

    // compute deductive closure
    fc.computeClosure();

  }

  @Test
  public void test() throws QueryParseException  {
    // TODO: FIX EXPECTED DATA

    String[][] expected = {
        { "<test:uno>", "<test:isAffiliatedWith>", "<test:dfki>" },
        { "<test:db>", "<rdf:type>", "<test:Company>" },
        { "<test:db>", "<test:isAffiliatedWith>", "<test:a180>" },
        { "<test:sri>", "<rdf:type>", "<test:ResearchInstitute>" },
        { "<owl:Nothing>", "<rdfs:subClassOf>", "<owl:Thing>" },
        { "<test:dfki>", "<test:isAffiliatedWith>", "<test:livinge>" },
        { "_:de.dfki.lt.hfc.ForwardChainer@45283ce21", "<rdf:subject>", "<test:a180>" },
        { "<owl:Thing>", "<owl:disjointWith>", "<owl:Nothing>" },
        { "<test:isAffiliatedWith>", "<rdfs:range>", "<test:Company>" },
        { "<xsd:string>", "<rdf:type>", "<rdfs:Datatype>" },
        { "_:de.dfki.lt.hfc.ForwardChainer@45283ce21", "<rdf:object>", "<test:db>" },
        { "<test:livinge>", "<test:isAffiliatedWith>", "<test:dfki>" },
        { "<test:sri>", "<test:isAffiliatedWith>", "<test:dfki>" },
        { "<test:Company>", "<owl:disjointWith>", "<test:Car>" },
        { "<test:Car>", "<rdfs:subClassOf>", "<owl:Thing>" },
        { "<owl:Nothing>", "<rdf:type>", "<owl:Class>" },
        { "<test:isAffiliatedWith>", "<rdfs:domain>", "<test:Company>" },
        { "<test:ResearchInstitute>", "<rdfs:subClassOf>", "<test:Company>" },
        { "_:de.dfki.lt.hfc.ForwardChainer@45283ce20", "<rdf:predicate>", "<test:isAffiliatedWith>" },
        { "<test:livinge>", "<rdf:type>", "<test:Company>" },
        { "<xsd:int>", "<rdf:type>", "<rdfs:Datatype>" },
        { "<test:dfki>", "<test:isAffiliatedWith>", "<test:uno>" },
        { "<test:Company>", "<rdfs:subClassOf>", "<owl:Thing>" },
        { "<test:dfki>", "<rdf:type>", "<test:ResearchInstitute>" },
        { "<test:dfki>", "<test:isAffiliatedWith>", "<test:sri>" },
        { "<test:Car>", "<owl:disjointWith>", "<test:Company>" },
        { "<owl:Thing>", "<rdf:type>", "<owl:Class>" },
        { "<test:a180>", "<test:isAffiliatedWith>", "<test:db>" },
        { "_:de.dfki.lt.hfc.ForwardChainer@45283ce20", "<rdf:type>", "<owl:Nothing>" },
        { "_:de.dfki.lt.hfc.ForwardChainer@45283ce20", "<rdf:object>", "<test:a180>" },
        { "<test:uno>", "<type>", "<test:Institution>" },
        { "_:de.dfki.lt.hfc.ForwardChainer@45283ce21", "<hfc:reason>", "\"$domainRestrictionViolated\"^^<xsd:string>" },
        { "_:de.dfki.lt.hfc.ForwardChainer@45283ce21", "<rdf:predicate>", "<test:isAffiliatedWith>" },
        { "_:de.dfki.lt.hfc.ForwardChainer@45283ce20", "<rdf:subject>", "<test:db>" },
        { "_:de.dfki.lt.hfc.ForwardChainer@45283ce21", "<rdf:type>", "<owl:Nothing>" },
        { "<test:Institution>", "<rdfs:subClassOf>", "<owl:Thing>" },
        { "_:de.dfki.lt.hfc.ForwardChainer@45283ce20", "<hfc:reason>", "\"$rangeRestrictionViolated\"^^<xsd:string>" },
        { "<test:a180>", "<rdf:type>", "<test:Car>" },

    };
    Query q = new Query(fc.tupleStore);
    BindingTable bt = q.query("SELECT ?s ?p ?o WHERE ?s ?p ?o");

    checkResult(expected, bt, bt.getVars());
  }

  @AfterClass
  public static void finish() {
    fc.shutdownNoExit();
  }

}




