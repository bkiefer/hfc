package de.dfki.lt.hfc;
import static org.junit.Assert.*;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static de.dfki.lt.hfc.TestingUtils.*;

public class ISumThreeTest {
  static ForwardChainer fc;

  private static String getResource(String name) {
    return TestingUtils.getTestResource("ISum3", name);
  }
  @BeforeClass
  public static void init() throws Exception {

    fc =  new ForwardChainer(Config.getInstance(getResource("ISum3.yml")));

    // compute deductive closure

    fc.computeClosure();

  }

  @Test
  public void test() throws QueryParseException  {
    // TODO: FIX EXPECTED DATA

    String[][] expected = {
        //no table in the output
    };
    Query q = new Query(fc.tupleStore);
    // WILL NEVER TERMINATE.
    BindingTable bt = q.query("SELECT ?s ?p ?o WHERE ?s ?p ?o");
    //printExpected(bt, fc.tupleStore); // TODO: THIS SHOULD BE REMOVED WHEN FINISHED
    //checkResult(expected, bt, bt.getVars());
  }

  @AfterClass
  public static void finish() {
    fc.shutdownNoExit();
  }

}







