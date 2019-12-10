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

public final class LMin2Test {

  @Test
  public void testLMin2() throws FileNotFoundException,
          WrongFormatException, IOException {

    // load NamespaceManager
    NamespaceManager namespace = NamespaceManager.getInstance();

    // create TupleStore
    TupleStore store =
        new TupleStore(false, true, true, 2, 5,0,1,2, 4, 2, namespace,
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
        (FunctionalOperator)store.checkAndRegisterOperator("de.dfki.lt.hfc.operators.LMin2");

    int x = fop.apply(ids);
    assertEquals(store.getObject(ids[1]),
        store.getObject(x));

  }
}
