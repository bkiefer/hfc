package de.dfki.lt.hfc.operators;

import static de.dfki.lt.hfc.TestingUtils.*;
import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.io.IOException;

import de.dfki.lt.hfc.FunctionalOperator;
import de.dfki.lt.hfc.NamespaceManager;
import de.dfki.lt.hfc.TupleStore;
import de.dfki.lt.hfc.WrongFormatException;
import org.junit.Test;

public final class FMaxTest {

  @Test
  public void testFMax() throws FileNotFoundException,
          WrongFormatException, IOException {

    // load NamespaceManager
    NamespaceManager namespace = NamespaceManager.getInstance();;

    // create TupleStore
    TupleStore store =
        new TupleStore(false, true, true, 2, 5,0,1,2, 4, 2, namespace,
            getTestResource("default.nt"));

    // create example values
    String[] values = {
      "\"1.01\"^^<xsd:float>",
      "\"0.02\"^^<xsd:float>",
      "\"-5.01\"^^<xsd:float>",
      "\"7.01\"^^<xsd:float>",
      "\"9.01\"^^<xsd:float>",
      "\"78.01\"^^<xsd:float>",
      "\"3.01\"^^<xsd:float>",
      "\"0.01\"^^<xsd:float>",
    };

    // store values in TupleStore, save integer-key in database
    int[] ids = new int[8];
    int i = 0;
    for (String val : values) {
      ids[i++] = store.putObject(val);
    }

    // create FunctionalOperator
    FunctionalOperator fop =
        (FunctionalOperator)store.checkAndRegisterOperator("de.dfki.lt.hfc.operators.FMax");

    // do operation
    int x = fop.apply(ids);
    assertEquals(store.getObject(ids[5]),
        store.getObject(x));

  }
}
