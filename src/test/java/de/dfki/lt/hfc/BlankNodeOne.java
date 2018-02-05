package de.dfki.lt.hfc;


import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static de.dfki.lt.hfc.TestUtils.checkResultBlankNodes;
import static de.dfki.lt.hfc.TestUtils.permuteHelper;
import static de.dfki.lt.hfc.TestUtils.printExpected;

public class BlankNodeOne {
  static ForwardChainer fc;

  private static String getResource(String name) {
    return TestUtils.getTestResource("BlankNode1", name);
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
        getResource("blanknode.nt"),                            // tuple file
        getResource("blanknode.rdl"),                           // rule file
        getResource("blanknode.ns")                             // namespace file
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
    String[] blankNodes = new String[3];
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
      ac1.add(new String[]{   "<test:db>", "<test:new>" ,p[0].toString()});
      ac1.add(new String[]{"<test:dfki>", "<test:new>", p[1].toString()});
      ac1.add(new String[]{"<test:sri>", "<test:new>", p[2].toString()});
      ambiguousClauses.add(ac1);
    }


    String[][] expected = {
            { "<owl:Thing>", "<rdf:type>", "<owl:Class>" },
            { "<test:sri>", "<test:hasName>", "\"Stanford Research Institute\"^^<xsd:string>" },
            { "<owl:Nothing>", "<rdf:type>", "<owl:Class>" },
            { "<xsd:int>", "<rdf:type>", "<rdfs:Datatype>" },
            { "<test:Company>", "<rdfs:subClassOf>", "<owl:Thing>" },
            //{ "<test:db>", "<test:new>", "_:de.dfki.lt.hfc.ForwardChainer@77cd7a02" },
            { "<test:dfki>", "<test:hasName>", "\"DFKI\"^^<xsd:string>" },
            { "<test:dfki>", "<rdf:type>", "<test:Company>" },
            { "<test:dfki>", "<test:hasName>", "\"Deutsches Forschungszentrum für Künstliche Intelligenz\"@de" },
            { "<test:db>", "<rdf:type>", "<test:Company>" },
            { "<xsd:string>", "<rdf:type>", "<rdfs:Datatype>" },
            //{ "<test:dfki>", "<test:new>", "_:de.dfki.lt.hfc.ForwardChainer@77cd7a00" },
            { "<test:db>", "<test:hasName>", "\"Daimler Benz\"^^<xsd:string>" },
            //{ "<test:sri>", "<test:new>", "_:de.dfki.lt.hfc.ForwardChainer@77cd7a01" },
            { "<test:sri>", "<test:hasName>", "\"SRI\"^^<xsd:string>" },
            { "<test:dfki>", "<test:hasName>", "\"DFKI GmbH\"^^<xsd:string>" },
            { "<test:sri>", "<rdf:type>", "<test:Company>" },
            { "<owl:Thing>", "<owl:disjointWith>", "<owl:Nothing>" },
            { "<test:dfki>", "<test:hasName>", "\"German Research Center for Artificial Inteligence\"@en" },
    { "<owl:Nothing>", "<rdfs:subClassOf>", "<owl:Thing>" },
    };

    Query q = new Query(fc.tupleStore);
    BindingTable bt = q.query("SELECT ?s ?p ?o WHERE ?s ?p ?o");
    checkResultBlankNodes(expected, ambiguousClauses,3,bt, bt.getVars());
  }

  @AfterAll
  public static void finish() {
    fc.shutdownNoExit();
  }

}
