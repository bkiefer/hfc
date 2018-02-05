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

public class PrintTrue {
  static ForwardChainer fc;

  private static String getResource(String name) {
    return TestUtils.getTestResource("PrintTrue", name);
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
        getResource("printtrue.nt"),                            // tuple file
        getResource("printtrue.rdl"),                           // rule file
        getResource("printtrue.ns")                             // namespace file
        );

    // compute deductive closure

    fc.computeClosure();

  }

  @Test
  public void test() throws QueryParseException {
    // TODO: FIX EXPECTED DATA
    // hardcoding the blank nodes, e.g. _:de.dfki.lt.hfc.ForwardChainer@6bf2d08e0 is problematic as
    // the namespace extension changes each time the test is performed
    // therefore lookup currently used blank nodes and insert into expected data
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
      ac1.add(new String[]{ "<test:dfki>", "<test:new>", p[0].toString() });
      ac1.add(new String[]{"<test:sri>", "<test:new>", p[1].toString()});
      ambiguousClauses.add(ac1);
    }

    String[][] expected = {
        { "<test:db>", "<test:hasName>", "\"Daimler Benz\"^^<xsd:string>" },
        { "<owl:Thing>", "<owl:disjointWith>", "<owl:Nothing>" },
        { "<test:Company>", "<rdfs:subClassOf>", "<owl:Thing>" },
        { "<test:sri>", "<rdf:type>", "<test:Company>" },
        { "<test:dfki>", "<test:hasName>", "\"DFKI\"^^<xsd:string>" },
        { "<test:sri>", "<test:hasName>", "\"Stanford Research Institute\"^^<xsd:string>" },
        { "<test:db>", "<rdf:type>", "<test:Company>" },
        { "<xsd:int>", "<rdf:type>", "<rdfs:Datatype>" },
        { "<owl:Thing>", "<rdf:type>", "<owl:Class>" },
        { "<owl:Nothing>", "<rdfs:subClassOf>", "<owl:Thing>" },
        { "<test:dfki>", "<rdf:type>", "<test:Company>" },
        { "<xsd:string>", "<rdf:type>", "<rdfs:Datatype>" },
        { "<test:dfki>", "<test:hasName>", "\"Deutsches Forschungszentrum für Künstliche Intelligenz\"@de" },
        { "<test:sri>", "<test:hasName>", "\"SRI\"^^<xsd:string>" },
        { "<test:dfki>", "<test:hasName>", "\"German Research Center for Artificial Inteligence\"@en" },
        { "<test:dfki>", "<test:hasName>", "\"DFKI GmbH\"^^<xsd:string>" },
        { "<owl:Nothing>", "<rdf:type>", "<owl:Class>" },
    };
    Query q = new Query(fc.tupleStore);
    BindingTable bt = q.query("SELECT ?s ?p ?o WHERE ?s ?p ?o");
    //TestLGetLatest.printExpected(bt, fc.tupleStore); // TODO: THIS SHOULD BE REMOVED WHEN FINISHED
    checkResultBlankNodes(expected, ambiguousClauses,2,bt, bt.getVars());
  }

  @AfterAll
  public static void finish() {
    fc.shutdownNoExit();
  }

}
