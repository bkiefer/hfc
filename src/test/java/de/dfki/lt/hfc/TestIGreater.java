package de.dfki.lt.hfc;

import static de.dfki.lt.hfc.TestUtils.*;
import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import org.apache.commons.lang3.ArrayUtils;

import org.junit.Test;

public final class TestIGreater {

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
    String[] values = { "\"1\"^^<xsd:int>", "\"2\"^^<xsd:int>" };

    // store values in TupleStore, save integer-key in database
    int[] ids = new int[2];
    int i = 0;
    for (String val : values) {
      ids[i++] = store.putObject(val);
    }

    // create FunctionalOperator
    FunctionalOperator fop =
        (FunctionalOperator)store.operatorRegistry
        .checkAndRegister("de.dfki.lt.hfc.operators.IGreater");

    // do operation
    assertEquals("1 > 2", FunctionalOperator.FALSE, fop.apply(ids));
    ArrayUtils.reverse(ids);
    assertEquals("2 > 1", FunctionalOperator.TRUE, fop.apply(ids));
    ids[0] = ids[1];
    assertEquals("1 > 1", FunctionalOperator.FALSE, fop.apply(ids));
  }
}