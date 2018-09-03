package de.dfki.lt.hfc.operators;

import java.io.FileNotFoundException;
import java.io.IOException;

import de.dfki.lt.hfc.*;
import org.junit.Test;

public class LIsValidTest {

  public static String getResource(String name) {
    return TestingUtils.getTestResource("LIsValid", name);
  }

  @Test
  public void testLIsValid()
      throws FileNotFoundException, IOException, WrongFormatException,
      QueryParseException {
    Namespace ns = Namespace.defaultNamespace();
    ns.putForm("logic", "http://www.dfki.de/lt/onto/common/logic.owl#", ns.isShortIsDefault());
    ns.putForm("dom", "http://www.dfki.de/lt/onto/pal/domain.owl#", ns.isShortIsDefault());
    ns.putForm("pal", "http://www.dfki.de/lt/onto/pal/pal.owl#",ns.isShortIsDefault());
    Config config = Config.getDefaultConfig();
    config.namespace = ns;
    config.verbose = false;
    TupleStore ts = new TupleStore(config);
    ts.readTuples(getResource("tuples.nt"));
    Query q = new Query(ts);
    BindingTable bt
        = q.query("SELECT ?o WHERE <logic:true> <dom:prof> <dom:treats> "
            + "?o ?ts FILTER LIsValid <dom:prof> <dom:treats> ?o ?ts");
    String[][] expected = {{"<pal:p2>"}, {"<pal:p3>"}, {"<pal:p1>"}};
    TestingUtils.checkResult(expected, bt, bt.getVars());
  }

}
