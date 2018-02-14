package de.dfki.lt.hfc;

import static de.dfki.lt.hfc.runnable.Utils.*;
import static org.junit.jupiter.api.Assertions.*;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.jupiter.api.Test;

public final class FDecrementTest {

  @Test
  public void testcleanUpTuple() throws FileNotFoundException,
      WrongFormatException, IOException {

    // load Namespace
    Namespace namespace = new Namespace(getTestResource("default.ns"), false);

    // create TupleStore
    TupleStore store =
        new TupleStore(false, true, true, 2, 5, 4, 2, namespace,
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
        (FunctionalOperator)store.operatorRegistry
        .checkAndRegister("de.dfki.lt.hfc.operators.FDecrement");

    // create FEqual
    FunctionalOperator feq =
        (FunctionalOperator)store.operatorRegistry
        .checkAndRegister("de.dfki.lt.hfc.operators.FEqual");

    // do operation
    ids[0] = fop.apply(ids);
    assertEquals(FunctionalOperator.TRUE, feq.apply(ids), "0.01 --");
    nids[0] = fop.apply(nids);
    assertEquals(FunctionalOperator.FALSE, feq.apply(nids), "0.01 --");
  }
}
