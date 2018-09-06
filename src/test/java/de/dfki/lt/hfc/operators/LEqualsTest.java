package de.dfki.lt.hfc.operators;

import static de.dfki.lt.hfc.TestingUtils.*;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.BeforeClass;
import org.junit.Test;

import de.dfki.lt.hfc.*;

public class LEqualsTest {
  static TupleStore ts;
  static Query q;
  public static String getResource(String name) {
    return getTestResource("Equals", name);
  }

  @BeforeClass
  public static void init() throws FileNotFoundException, WrongFormatException, IOException {
    NamespaceManager ns = NamespaceManager.getInstance();
    ns.putForm("logic", "http://www.dfki.de/lt/onto/common/logic.owl#", ns.isShortIsDefault());
    ns.putForm("dom", "http://www.dfki.de/lt/onto/pal/domain.owl#", ns.isShortIsDefault());
    ns.putForm("pal", "http://www.dfki.de/lt/onto/pal/pal.owl#",ns.isShortIsDefault());
    Config config = Config.getDefaultConfig();
    config.namespace = ns;
    config.verbose = false;
    ts = new TupleStore(config);
    ts.readTuples(getResource("tuples.nt"));
    q = new Query(ts);
  }

  @Test
  public void testEquals1() throws QueryParseException {
    BindingTable bt = q.query("SELECT ?o WHERE ?o <dom:prof> ?s ?_ "
        + " FILTER Equals ?s \"abc\"@en");
    String[][] expected = {{"<dom:val0>"}};
    checkResult(expected, bt, bt.getVars());
  }

  @Test
  public void testEquals2() throws QueryParseException {
    BindingTable bt = q.query("SELECT ?o WHERE ?o <dom:prof> ?s ?_ "
        + " FILTER Equals ?s \"def\"@en");
    String[][] expected2 = {{"<dom:val1>"}};
    checkResult(expected2, bt, bt.getVars());
  }

  @Test
  public void testEquals3() throws QueryParseException {
    BindingTable bt = q.query("SELECT ?o WHERE ?o <dom:prof> ?s ?_ "
        + " FILTER Equals ?s \"def\"@nl");
    String[][] expected3 = {{"<dom:val3>"}};
    checkResult(expected3, bt, bt.getVars());
  }

  @Test
  public void testEquals4() throws QueryParseException {
    BindingTable bt = q.query("SELECT ?o WHERE ?o <dom:prof> ?s ?_ "
        + " FILTER Equals ?s \"def\"");
    String[][] expected4 = {{"<dom:val6>"}, {"<dom:val7>"}};
    checkResult(expected4, bt, bt.getVars());
  }

  @Test
  public void testEquals5() throws QueryParseException {
    BindingTable bt = q.query("SELECT ?o WHERE ?o <dom:prof> ?s ?_ "
        + " FILTER Equals ?s \"abc\"");
    String[][] expected5 = {{"<dom:val4>"}, {"<dom:val5>"}};
    checkResult(expected5, bt, bt.getVars());
  }

}
