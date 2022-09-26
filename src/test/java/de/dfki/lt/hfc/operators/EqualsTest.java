package de.dfki.lt.hfc.operators;

import static de.dfki.lt.hfc.TestingUtils.checkResult;
import static de.dfki.lt.hfc.TestingUtils.getTestResource;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.BeforeClass;
import org.junit.Test;

import de.dfki.lt.hfc.BindingTable;
import de.dfki.lt.hfc.Query;
import de.dfki.lt.hfc.QueryParseException;
import de.dfki.lt.hfc.TestConfig;
import de.dfki.lt.hfc.TestHfc;
import de.dfki.lt.hfc.WrongFormatException;

public class EqualsTest {
  static TestHfc hfc;
  static Query q;

  public static String getResource(String name) {
    return getTestResource("Equals", name);
  }

  @BeforeClass
  public static void init() throws FileNotFoundException, WrongFormatException, IOException {
    TestConfig config = TestConfig.getDefaultConfig();
    config.addNamespace("logic", "http://www.dfki.de/lt/onto/common/logic.owl#");
    config.addNamespace("dom", "http://www.dfki.de/lt/onto/pal/domain.owl#");
    config.addNamespace("pal", "http://www.dfki.de/lt/onto/pal/pal.owl#");
    config.setVerbose(false) ;
    hfc = new TestHfc(config);
    hfc.uploadTuples(getResource("tuples.nt"));
    q = hfc.getQuery();
  }

  @Test
  public void testEquals1() throws QueryParseException {
    BindingTable bt = q.query("SELECT ?o WHERE ?o <dom:prof> ?s ?_ "
        + " FILTER Equals ?s \"abc\"@en");
    String[][] expected = {{"<dom:val0>"}};
    checkResult(hfc, bt, expected, bt.getVars());
  }

  @Test
  public void testEquals2() throws QueryParseException {
    BindingTable bt = q.query("SELECT ?o WHERE ?o <dom:prof> ?s ?_ "
        + " FILTER Equals ?s \"def\"@en");
    String[][] expected = {{"<dom:val1>"}};
    checkResult(hfc, bt, expected, bt.getVars());
  }

  @Test
  public void testEquals3() throws QueryParseException {
    BindingTable bt = q.query("SELECT ?o WHERE ?o <dom:prof> ?s ?_ "
        + " FILTER Equals ?s \"def\"@nl");
    String[][] expected = {{"<dom:val3>"}};
    checkResult(hfc, bt, expected, bt.getVars());
  }

  @Test
  public void testEquals4() throws QueryParseException {
    BindingTable bt = q.query("SELECT ?o WHERE ?o <dom:prof> ?s ?_ "
        + " FILTER Equals ?s \"def\"");
    String[][] expected = {{"<dom:val6>"}, {"<dom:val7>"}};
    checkResult(hfc, bt, expected, bt.getVars());
  }

  @Test
  public void testEquals5() throws QueryParseException {
    BindingTable bt = q.query("SELECT ?o WHERE ?o <dom:prof> ?s ?_ "
        + " FILTER Equals ?s \"abc\"");
    String[][] expected = {{"<dom:val4>"}, {"<dom:val5>"}};
    checkResult(hfc, bt, expected, bt.getVars());
  }

}
