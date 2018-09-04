package de.dfki.lt.hfc;

import static de.dfki.lt.hfc.TestingUtils.*;
import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.Test;

public final class IDecrementTest {

  @Test
  public void testcleanUpTuple() throws FileNotFoundException,
      WrongFormatException, IOException {

    // load Namespace
    Namespace namespace = new Namespace();;

    // create TupleStore
    TupleStore store =
        new TupleStore(false, true, true, 2, 5,0,1,2, 4, 2, namespace,
            getTestResource("default.nt"));

    // create example values
    String[] values_eq = { "\"1\"^^<xsd:int>",
      "\"0\"^^<xsd:int>",};
    String[] values_neq = { "\"9\"^^<xsd:int>",
      "\"7\"^^<xsd:int>",};

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
        .checkAndRegister("de.dfki.lt.hfc.operators.IDecrement");

    // create FEqual
    FunctionalOperator feq =
        (FunctionalOperator)store.operatorRegistry
        .checkAndRegister("de.dfki.lt.hfc.operators.IEqual");

    // do operation
    ids[0] = fop.apply(ids);
    assertEquals("1-- = 0", FunctionalOperator.TRUE, feq.apply(ids));
    nids[0] = fop.apply(nids);
    assertEquals("9-- != 7", FunctionalOperator.FALSE, feq.apply(nids));
  }
}
