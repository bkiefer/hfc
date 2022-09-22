package de.dfki.lt.hfc.operators;

import java.io.FileNotFoundException;
import java.io.IOException;

import de.dfki.lt.hfc.*;
import org.junit.Test;

public class IsValidTest {

  public static String getResource(String name) {
    return TestingUtils.getTestResource("LIsValid", name);
  }

  @Test
  public void testLIsValid()
      throws FileNotFoundException, IOException, WrongFormatException,
      QueryParseException {
    Config config = Config.getDefaultConfig();
    config.addNamespace("logic", "http://www.dfki.de/lt/onto/common/logic.owl#");
    config.addNamespace("dom", "http://www.dfki.de/lt/onto/pal/domain.owl#");
    config.addNamespace("pal", "http://www.dfki.de/lt/onto/pal/pal.owl#");
    config.setVerbose(false);
    TupleStore ts = new TupleStore(config);
    ts.readTuples(getResource("tuples.nt"),false);
    Query q = new Query(ts);
    BindingTable bt
        = q.query("SELECT ?o WHERE <logic:true> <dom:prof> <dom:treats> "
            + "?o ?ts FILTER LIsValid <dom:prof> <dom:treats> ?o ?ts");
    String[][] expected = {{"<pal:p2>"}, {"<pal:p3>"}, {"<pal:p1>"}};
    TestingUtils.checkResult(expected, bt, bt.getVars());
  }

}
