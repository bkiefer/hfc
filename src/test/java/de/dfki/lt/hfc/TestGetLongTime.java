package de.dfki.lt.hfc;

import static de.dfki.lt.hfc.TestUtils.*;
import de.dfki.lt.hfc.types.XsdLong;
import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.Test;

/**
 * tests GetLongTime by comparing the functions result and a manual
 * System.currentTimeMillis(); could be problematic on slow systems.
 * @author Christophe Biwer, christophe.biwer@dfki.de
 */
public final class TestGetLongTime {

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
    String[] values = {};

    // store values in TupleStore, save integer-key in database
    int[] ids = new int[2];
    int i = 0;
    for (String val : values) {
      ids[i++] = store.putObject(val);
    }

    // create FunctionalOperator
    FunctionalOperator fop =
        (FunctionalOperator)store.operatorRegistry
        .checkAndRegister("de.dfki.lt.hfc.operators.GetLongTime");

    // do operation
    int temp = fop.apply(ids);
    long expected = System.currentTimeMillis();
    long result = ((XsdLong)store.idToJavaObject.get(temp)).value;
    assertEquals(0, Long.compare(expected, result));

  }
}