package de.dfki.lt.hfc.operators;

import java.io.FileNotFoundException;
import java.io.IOException;
import de.dfki.lt.hfc.BindingTable;
import de.dfki.lt.hfc.Namespace;
import de.dfki.lt.hfc.Query;
import de.dfki.lt.hfc.QueryParseException;
import de.dfki.lt.hfc.TupleStore;
import de.dfki.lt.hfc.Utils;
import de.dfki.lt.hfc.WrongFormatException;
import org.junit.Test;

public class LIsValidTest {

  public static String getResource(String name) {
    return Utils.getTestResource("LIsValid", name);
  }

  @Test
  public void testLIsValid()
      throws FileNotFoundException, IOException, WrongFormatException,
      QueryParseException {
    Namespace ns = new Namespace(getResource("namespaces.ns"), false);
    TupleStore ts = new TupleStore(1000, 5000, ns);
    ts.verbose = false;
    ts.readTuples(getResource("tuples.nt"));
    Query q = new Query(ts);
    BindingTable bt
        = q.query("SELECT ?o WHERE <logic:true> <dom:prof> <dom:treats> "
            + "?o ?ts FILTER LIsValid <dom:prof> <dom:treats> ?o ?ts");
    String[][] expected = {{"<pal:p2>"}, {"<pal:p3>"}, {"<pal:p1>"}};
    Utils.checkResult(expected, bt, bt.getVars());
  }

}
