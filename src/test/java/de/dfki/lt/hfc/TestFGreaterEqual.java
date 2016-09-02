package de.dfki.lt.hfc;

import static de.dfki.lt.hfc.TestUtils.*;
import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import org.apache.commons.lang3.ArrayUtils;

import org.junit.Test;

public final class TestFGreaterEqual {

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
    String[] values = { "\"0.01\"^^<xsd:float>", "\"0.02\"^^<xsd:float>" };

    // store values in TupleStore, save integer-key in database
    int[] ids = new int[2];
    int i = 0;
    for (String val : values) {
      ids[i++] = store.putObject(val);
    }

    // create FunctionalOperator
    FunctionalOperator fop =
        (FunctionalOperator)store.operatorRegistry
        .checkAndRegister("de.dfki.lt.hfc.operators.FGreaterEqual");

    assertEquals("0.01 >= 0.02", FunctionalOperator.FALSE, fop.apply(ids));
    ArrayUtils.reverse(ids);
    assertEquals("0.02 >= 0.01", FunctionalOperator.TRUE, fop.apply(ids));
    ids[0] = ids[1];
    assertEquals("0.01 >= 0.01", FunctionalOperator.TRUE, fop.apply(ids));
  }
}
