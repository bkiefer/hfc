package de.dfki.lt.hfc;

import static de.dfki.lt.hfc.TestUtils.*;
import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.Test;

public final class TestFDecrement {

  @Test
  public void testcleanUpTuple() throws FileNotFoundException,
      WrongFormatException, IOException {

    // load Namespace
    Namespace namespace = new Namespace(getTestResource("default.ns"));

    // create TupleStore
    TupleStore store =
        new TupleStore(false, true, true, 2, 5, 4, 2, namespace,
            getTestResource("default.nt"));

    // create example values
    String[] values = { "\"1.01\"^^<xsd:float>",
      "\"0.01\"^^<xsd:float>",
      "\"99.99\"^^<xsd:float>" };

    // store values in TupleStore, save integer-key in database
    int[] ids = new int[3];
    int i = 0;
    for (String val : values) {
      ids[i++] = store.putObject(val);
    }

    // create FunctionalOperator
    FunctionalOperator fop =
        (FunctionalOperator)store.operatorRegistry
        .checkAndRegister("de.dfki.lt.hfc.operators.FDecrement");

    int temp = fop.apply(ids);

    assertEquals("0.01 --", ids[1], temp);
    assertNotEquals("0.01 --", ids[2], fop.apply(ids));
  }
}
