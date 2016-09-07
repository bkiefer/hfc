package de.dfki.lt.hfc;

import static de.dfki.lt.hfc.TestUtils.*;
import de.dfki.lt.hfc.types.XsdDateTime;
import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import static java.lang.Math.abs;
import java.util.Date;

import org.junit.Test;

/**
 * tests GetDateTime by comparing the fields of the created XsdDateTime and
 * a manually created Date() object. Milliseconds may be different
 * (100 ms tolerance). May be problematic on slower systems
 * @author Christophe Biwer, christophe.biwer@dfki.de
 */
public final class TestGetDateTime {

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
        .checkAndRegister("de.dfki.lt.hfc.operators.GetDateTime");

    // do operation
    int temp = fop.apply(ids);
    Date expected = new Date();
    XsdDateTime result = ((XsdDateTime)store.idToJavaObject.get(temp));
    assertEquals(1900 + expected.getYear(), result.year);
    assertEquals(1 + expected.getMonth(), result.month);
    assertEquals(expected.getDate(), result.day);
    assertEquals(expected.getHours(), result.hour);
    assertEquals(expected.getMinutes(), result.minute);
    float expSecondsMillisecond = expected.getSeconds() + (float)(expected.getTime() % 1000) / 1000;
    float resSecondsMilliseconds = result.second;
    int diff = (int)(abs(expSecondsMillisecond - resSecondsMilliseconds) * 1000);
    assertTrue(100 > diff);
  }
}
