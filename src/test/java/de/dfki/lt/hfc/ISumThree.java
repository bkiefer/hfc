package de.dfki.lt.hfc;



import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static de.dfki.lt.hfc.TestUtils.printExpected;

public class ISumThree {
  static ForwardChainer fc;

  private static String getResource(String name) {
    return TestUtils.getTestResource("ISum3", name);
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
    // TODO: FIX EXPECTED DATA

    String[][] expected = {
    //no table in the output

    };
    Query q = new Query(fc.tupleStore);
    BindingTable bt = q.query("SELECT ?s ?p ?o WHERE ?s ?p ?o");
    printExpected(bt, fc.tupleStore); // TODO: THIS SHOULD BE REMOVED WHEN FINISHED
    //checkResult(expected, bt, bt.getVars());
  }

  @AfterAll
  public static void finish() {
    fc.shutdownNoExit();
  }

}







