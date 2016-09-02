package de.dfki.lt.hfc;

import static de.dfki.lt.hfc.TestUtils.*;
import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.Test;

import de.dfki.lt.hfc.*;
import de.dfki.lt.hfc.operators.FEqual;;

public final class TestFEqual {

  @Test
  public void testcleanUpTuple() throws FileNotFoundException, WrongFormatException, IOException {
    Namespace namespace = new Namespace(getTestResource("default.ns"));
    TupleStore store =
        new TupleStore(false, true, true, 2, 5, 4, 2, namespace,
            getTestResource("default.nt"));
    String[] values = { "\"0.01\"^^<xsd:float>", "\"0.02\"^^<xsd:float>" };

    int[] ids = new int[2];
    int i = 0;
    for (String val : values) {
      ids[i++] = store.putObject(val);
    }

    FunctionalOperator fop =
        (FunctionalOperator)store.operatorRegistry
        .checkAndRegister("de.dfki.lt.hfc.operators.FEqual");

    assertEquals(FunctionalOperator.FALSE, fop.apply(ids));
    ids[1] = ids[0];
    assertEquals(FunctionalOperator.TRUE, fop.apply(ids));
  }
}
