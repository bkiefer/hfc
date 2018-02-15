package de.dfki.lt.hfc;

import static de.dfki.lt.hfc.runnable.Utils.*;
import static org.junit.jupiter.api.Assertions.*;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.jupiter.api.Test;

public final class LMinTest {

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
    String[] values = {
      "\"1\"^^<xsd:long>",
      "\"0\"^^<xsd:long>",
      "\"-5\"^^<xsd:long>",
      "\"7\"^^<xsd:long>",
      "\"9\"^^<xsd:long>",
      "\"78\"^^<xsd:long>",
      "\"3\"^^<xsd:long>",
      "\"0\"^^<xsd:long>",
    };

    // store values in TupleStore, save integer-key in database
    int[] ids = new int[8];
    int i = 0;
    for (String val : values) {
      ids[i++] = store.putObject(val);
    }

    // create FunctionalOperator
    FunctionalOperator fop =
        (FunctionalOperator)store.operatorRegistry
        .checkAndRegister("de.dfki.lt.hfc.operators.LMin");

    int x = fop.apply(ids);
    assertEquals(store.idToJavaObject.get(ids[2]),
        store.idToJavaObject.get(x));

  }
}
