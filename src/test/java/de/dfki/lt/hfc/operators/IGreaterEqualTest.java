package de.dfki.lt.hfc.operators;

import static de.dfki.lt.hfc.TestingUtils.*;
import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.io.IOException;


import de.dfki.lt.hfc.*;
import org.junit.Test;

public final class IGreaterEqualTest {

  @Test
  public void testIGreaterEqual() throws FileNotFoundException,
          WrongFormatException, IOException {

    // load NamespaceManager
    NamespaceManager namespace = NamespaceManager.getInstance();

    // create TupleStore
    TupleStore store =
        new TupleStore(false, true, true, 2, 5,0,1,2, 4, 2, namespace,
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
        (FunctionalOperator)store.checkAndRegisterOperator("de.dfki.lt.hfc.operators.IGreaterEqual");

    // do operation
    assertEquals("1 >= 2", FunctionalOperator.FALSE, fop.apply(ids));
    TestingUtils.reverse(ids);
    assertEquals("2 >= 1", FunctionalOperator.TRUE, fop.apply(ids));
    ids[0] = ids[1];
    assertEquals("1 >= 1", FunctionalOperator.TRUE, fop.apply(ids));
  }
}