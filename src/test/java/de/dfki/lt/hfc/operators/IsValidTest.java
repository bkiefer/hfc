package de.dfki.lt.hfc.operators;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.BeforeClass;
import org.junit.Test;

import de.dfki.lt.hfc.BindingTable;
import de.dfki.lt.hfc.Query;
import de.dfki.lt.hfc.TestConfig;
import de.dfki.lt.hfc.TestHfc;
import de.dfki.lt.hfc.TestingUtils;
import de.dfki.lt.hfc.WrongFormatException;
import de.dfki.lt.hfc.io.QueryParseException;

public class IsValidTest {
  static TestHfc hfc;
  static Query q;

  public static String getResource(String name) {
    return TestingUtils.getTestResource("LIsValid", name);
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
  public void testLIsValid()
      throws FileNotFoundException, IOException, WrongFormatException,
      QueryParseException {
    BindingTable bt
        = q.query("SELECT ?o WHERE <logic:true> <dom:prof> <dom:treats> "
            + "?o ?ts FILTER LIsValid <dom:prof> <dom:treats> ?o ?ts");
    String[][] expected = {{"<pal:p2>"}, {"<pal:p3>"}, {"<pal:p1>"}};
    TestingUtils.checkResult(hfc, bt, expected, bt.getVars());
  }

}
