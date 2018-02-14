package de.dfki.lt.hfc;

import static de.dfki.lt.hfc.runnable.Utils.*;
import static org.junit.jupiter.api.Assertions.*;

import java.io.FileNotFoundException;
import java.io.IOException;


import org.junit.jupiter.api.Test;

public final class LGreaterEqualTest {

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
    String[] values = { "\"1\"^^<xsd:long>", "\"2\"^^<xsd:long>" };

    // store values in TupleStore, save integer-key in database
    int[] ids = new int[2];
    int i = 0;
    for (String val : values) {
      ids[i++] = store.putObject(val);
    }

    // create FunctionalOperator
    FunctionalOperator fop =
        (FunctionalOperator)store.operatorRegistry
        .checkAndRegister("de.dfki.lt.hfc.operators.LGreaterEqual");

    // do operation
    assertEquals( FunctionalOperator.FALSE, fop.apply(ids),"1 >= 2");
    reverse(ids);
    assertEquals( FunctionalOperator.TRUE, fop.apply(ids), "2 >= 1");
    ids[0] = ids[1];
    assertEquals( FunctionalOperator.TRUE, fop.apply(ids), "1 >= 1");
  }
}
