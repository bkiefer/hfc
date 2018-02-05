package de.dfki.lt.hfc;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static de.dfki.lt.hfc.TestUtils.checkResult;
import static de.dfki.lt.hfc.TestUtils.checkResultBlankNodes;
import static de.dfki.lt.hfc.TestUtils.permuteHelper;
import static de.dfki.lt.hfc.TestUtils.printExpected;

public class ISumTwo {
  static ForwardChainer fc;

  private static String getResource(String name) {
    return TestUtils.getTestResource("ISum2", name);
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
        getResource("isum.nt"),                            // tuple file
        getResource("isum.rdl"),                           // rule file
        getResource("isum.ns")                             // namespace file
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
    String[] blankNodes = new String[6];
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
      ac1.add(new String[]{ p[0].toString(), "<test:numberOfEmployees>", "\"61000\"^^<xsd:int>" });
      ac1.add(new String[]{ p[1].toString(), "<test:numberOfEmployees>", "\"60500\"^^<xsd:int>" });
      ac1.add(new String[]{ p[2].toString(), "<test:numberOfEmployees>", "\"60500\"^^<xsd:int>" });
      ac1.add(new String[]{ p[3].toString(), "<test:numberOfEmployees>", "\"1500\"^^<xsd:int>" });
      ac1.add(new String[]{ p[4].toString(), "<test:numberOfEmployees>", "\"1500\"^^<xsd:int>" });
      ac1.add(new String[]{ p[5].toString(), "<test:numberOfEmployees>", "\"61000\"^^<xsd:int>" });
      ambiguousClauses.add(ac1);
    }
    String[][] expected = {
        { "<test:sri>", "<test:hasName>", "\"SRI\"^^<xsd:string>" },
//        { "_:de.dfki.lt.hfc.ForwardChainer@45283ce24", "<test:numberOfEmployees>", "\"61000\"^^<xsd:int>" },
        { "<owl:Thing>", "<rdf:type>", "<owl:Class>" },
        { "<test:sri>", "<test:numberOfEmployees>", "\"1000\"^^<xsd:int>" },
        { "<xsd:int>", "<rdf:type>", "<rdfs:Datatype>" },
        { "<test:Company>", "<rdfs:subClassOf>", "<owl:Thing>" },
        { "<test:db>", "<test:numberOfEmployees>", "\"60000\"^^<xsd:int>" },
//        { "_:de.dfki.lt.hfc.ForwardChainer@45283ce22", "<test:numberOfEmployees>", "\"60500\"^^<xsd:int>" },
        { "<test:dfki>", "<test:hasName>", "\"DFKI\"^^<xsd:string>" },
        { "<test:sri>", "<rdf:type>", "<test:Company>" },
        { "<test:dfki>", "<test:hasName>", "\"Deutsches Forschungszentrum für Künstliche Intelligenz\"@de" },
        { "<owl:Thing>", "<owl:disjointWith>", "<owl:Nothing>" },
        { "<test:db>", "<rdf:type>", "<test:Company>" },
//        { "_:de.dfki.lt.hfc.ForwardChainer@45283ce25", "<test:numberOfEmployees>", "\"60500\"^^<xsd:int>" },
//        { "_:de.dfki.lt.hfc.ForwardChainer@45283ce21", "<test:numberOfEmployees>", "\"1500\"^^<xsd:int>" },
        { "<owl:Nothing>", "<rdf:type>", "<owl:Class>" },
//        { "_:de.dfki.lt.hfc.ForwardChainer@45283ce23", "<test:numberOfEmployees>", "\"1500\"^^<xsd:int>" },
        { "<test:sri>", "<test:hasName>", "\"Stanford Research Institute\"^^<xsd:string>" },
        { "<test:dfki>", "<rdf:type>", "<test:Company>" },
//        { "_:de.dfki.lt.hfc.ForwardChainer@45283ce20", "<test:numberOfEmployees>", "\"61000\"^^<xsd:int>" },
        { "<test:dfki>", "<test:hasName>", "\"DFKI GmbH\"^^<xsd:string>" },
        { "<owl:Nothing>", "<rdfs:subClassOf>", "<owl:Thing>" },
        { "<test:dfki>", "<test:hasName>", "\"German Research Center for Artificial Inteligence\"@en" },
        { "<test:db>", "<test:hasName>", "\"Daimler Benz\"^^<xsd:string>" },
        { "<xsd:string>", "<rdf:type>", "<rdfs:Datatype>" },
        { "<test:adder>", "<rdf:type>", "<owl:Thing>" },
        { "<test:dfki>", "<test:numberOfEmployees>", "\"500\"^^<xsd:int>" },

    };
    Query q = new Query(fc.tupleStore);
    BindingTable bt = q.query("SELECT ?s ?p ?o WHERE ?s ?p ?o");
    printExpected(bt, fc.tupleStore); // TODO: THIS SHOULD BE REMOVED WHEN FINISHED
    checkResultBlankNodes(expected, ambiguousClauses,6,bt, bt.getVars());
  }

  @AfterAll
  public static void finish() {
    fc.shutdownNoExit();
  }

}






