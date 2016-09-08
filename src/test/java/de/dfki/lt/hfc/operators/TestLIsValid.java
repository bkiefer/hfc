package de.dfki.lt.hfc.operators;

import java.io.FileNotFoundException;
import java.io.IOException;
import de.dfki.lt.hfc.*;
import org.junit.Test;

public class TestLIsValid {

  public static String getResource(String name) {
    return Utils.getTestResource("LIsValid", name);
  }

  @Test
  public void testLIsValid() throws FileNotFoundException, IOException, WrongFormatException, QueryParseException {
    Namespace ns = new Namespace(getResource("namespaces.ns"));
    TupleStore ts = new TupleStore(1000, 5000, ns, getResource("tuples.nt"));
    Query q = new Query(ts);
    BindingTable bt =
      q.query("SELECT ?o WHERE <logic:true> <dom:prof> <dom:treats> ?o ?ts FILTER LIsValid <dom:prof> <dom:treats> ?o ?ts");
    String[][] expected = { {"<pal:p2>"}, {"<pal:p3>"}, {"<pal:p1>"} };
    Utils.checkResult(expected, bt, bt.getVars());
  }

}
