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

public final class FDecrementTest {

  @Test
  public void testFDecrement() throws FileNotFoundException,
          WrongFormatException, IOException {

    // load NamespaceManager
    NamespaceManager namespace = NamespaceManager.getInstance();

    // create TupleStore
    TupleStore store =
        new TupleStore(false, true, true, 2, 5,0,1,2, 4, 2, namespace,
            getTestResource("default.nt"));

    // create example values
    String[] values_eq = { "\"1.01\"^^<xsd:float>",
      "\"0.01\"^^<xsd:float>",};
    String[] values_neq = { "\"1.01\"^^<xsd:float>",
      "\"9.01\"^^<xsd:float>",};

    // store values in TupleStore, save integer-key in database
    int[] ids = new int[2];
    int i = 0;
    for (String val : values_eq) {
      ids[i++] = store.putObject(val);
    }
    int[] nids = new int[2];
    i = 0;
    for (String val : values_neq) {
      nids[i++] = store.putObject(val);
    }

    // create FunctionalOperator
    FunctionalOperator fop =
        (FunctionalOperator)store.checkAndRegisterOperator("de.dfki.lt.hfc.operators.FDecrement");

    // create FEqual
    FunctionalOperator feq =
        (FunctionalOperator)store.checkAndRegisterOperator("de.dfki.lt.hfc.operators.FEqual");

    // do operation
    ids[0] = fop.apply(ids);
    assertEquals("0.01 --", FunctionalOperator.TRUE, feq.apply(ids));
    nids[0] = fop.apply(nids);
    assertEquals("0.01 --", FunctionalOperator.FALSE, feq.apply(nids));
  }
}