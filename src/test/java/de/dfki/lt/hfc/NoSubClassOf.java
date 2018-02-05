package de.dfki.lt.hfc;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static de.dfki.lt.hfc.TestUtils.checkResultBlankNodes;
import static de.dfki.lt.hfc.TestUtils.permuteHelper;

public class NoSubClassOf {
  static ForwardChainer fc;

  private static String getResource(String name) {
    return TestUtils.getTestResource("NoSubClassOf", name);
  }

  @BeforeAll
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
  public void test() throws QueryParseException {
    // hardcoding the blank nodes, e.g. _:de.dfki.lt.hfc.ForwardChainer@6bf2d08e0 is problematic as
    // the namespace extension changes each time the test is performed
    // therefore lookup currently used blank nodes and insert into expected data
    // TEST still fails
    int i = 0;
    String[] blankNodes = new String[2];
    Set<List<String[]>> ambiguousClauses = new HashSet<List<String[]>>();
    for (String s:fc.tupleStore.objectToId.keySet()) {
      if (s.startsWith("_")) {
        blankNodes[i] = s;
        i++;
      }
    }
    Set<Object[]> permuts = new HashSet<>();
    permuteHelper(blankNodes, 0, permuts);
    // Ugly but does work. So what?
    for (Object[] p: permuts) {
      List<String[]> ac1 = new ArrayList<String[]>();
      ac1.add(new String[]{ p[1].toString(), "<rdf:type>", "<owl:Nothing>"  });
      ac1.add(new String[]{ p[0].toString(), "<rdf:predicate>", "<test:isAffiliatedWith>" });
      ac1.add(new String[]{ p[1].toString(), "<hfc:reason>", "\"$rangeRestrictionViolated\"^^<xsd:string>" });
      ac1.add(new String[]{ p[0].toString(), "<hfc:reason>", "\"$domainRestrictionViolated\"^^<xsd:string>" });
      ac1.add(new String[]{ p[1].toString(), "<rdf:predicate>", "<test:isAffiliatedWith>" });
      ac1.add(new String[]{ p[0].toString(), "<rdf:type>", "<owl:Nothing>" });
      ac1.add(new String[]   { p[1].toString(), "<rdf:subject>", "<test:db>" });
      ac1.add(new String[]{ p[1].toString(), "<rdf:object>", "<test:a180>" });
      ac1.add(new String[] { p[0].toString(), "<rdf:subject>", "<test:a180>" });
      ac1.add(new String[]{ p[0].toString(), "<rdf:object>", "<test:db>" });
      ambiguousClauses.add(ac1);
    }
    String[][] expected = {
        { "<test:ResearchInstitute>", "<rdfs:subClassOf>", "<test:Company>" },
        { "<test:a180>", "<test:isAffiliatedWith>", "<test:db>" },
        { "<xsd:int>", "<rdf:type>", "<rdfs:Datatype>" },
       // { blankNodes[0], "<rdf:type>", "<owl:Nothing>" },
        { "<test:dfki>", "<test:isAffiliatedWith>", "<test:livinge>" },
        { "<test:db>", "<rdf:type>", "<test:Company>" },
        { "<test:isAffiliatedWith>", "<rdfs:domain>", "<test:Company>" },
        //{ blankNodes[1], "<rdf:predicate>", "<test:isAffiliatedWith>" },
        { "<test:dfki>", "<rdf:type>", "<test:ResearchInstitute>" },
        { "<owl:Thing>", "<owl:disjointWith>", "<owl:Nothing>" },
        { "<test:Car>", "<rdfs:subClassOf>", "<owl:Thing>" },
        { "<test:Company>", "<rdfs:subClassOf>", "<owl:Thing>" },
        { "<test:sri>", "<test:isAffiliatedWith>", "<test:dfki>" },
        { "<test:ResearchInstitute>", "<rdfs:subClassOf>", "<test:ResearchInstitute>" },
        { "<test:livinge>", "<test:isAffiliatedWith>", "<test:dfki>" },
//        { blankNodes[0], "<hfc:reason>", "\"$rangeRestrictionViolated\"^^<xsd:string>" },
        { "<xsd:string>", "<rdf:type>", "<rdfs:Datatype>" },
        { "<test:sri>", "<rdf:type>", "<test:ResearchInstitute>" },
        { "<owl:Nothing>", "<rdf:type>", "<owl:Class>" },
        { "<owl:Thing>", "<rdf:type>", "<owl:Class>" },
        { "<test:isAffiliatedWith>", "<rdfs:range>", "<test:Company>" },
        { "<test:Company>", "<rdfs:subClassOf>", "<test:Company>" },
        { "<test:db>", "<test:isAffiliatedWith>", "<test:a180>" },
//        { blankNodes[1], "<hfc:reason>", "\"$domainRestrictionViolated\"^^<xsd:string>" },
//        { blankNodes[0], "<rdf:predicate>", "<test:isAffiliatedWith>" },
        { "<test:a180>", "<rdf:type>", "<test:Car>" },
        { "<test:dfki>", "<test:isAffiliatedWith>", "<test:sri>" },
//        { blankNodes[1], "<rdf:type>", "<owl:Nothing>" },
//        { blankNodes[1], "<rdf:subject>", "<test:db>" },
//        { blankNodes[0], "<rdf:object>", "<test:a180>" },
//        { blankNodes[1], "<rdf:subject>", "<test:a180>" },
        { "<test:livinge>", "<rdf:type>", "<test:Company>" },
        { "<owl:Nothing>", "<rdfs:subClassOf>", "<owl:Thing>" },
     //   { blankNodes[1], "<rdf:object>", "<test:db>" },
    };
    Query q = new Query(fc.tupleStore);
    BindingTable bt = q.query("SELECT ?s ?p ?o WHERE ?s ?p ?o");
    // printExpected(bt, fc.tupleStore); // TODO: THIS SHOULD BE REMOVED WHEN FINISHED
    checkResultBlankNodes(expected, ambiguousClauses,10,bt, bt.getVars());
  }

  @AfterAll
  public static void finish() {
    fc.shutdownNoExit();
  }

}
