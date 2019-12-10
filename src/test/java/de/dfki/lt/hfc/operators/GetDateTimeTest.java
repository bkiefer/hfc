package de.dfki.lt.hfc.operators;

import static de.dfki.lt.hfc.TestingUtils.*;

import de.dfki.lt.hfc.FunctionalOperator;
import de.dfki.lt.hfc.NamespaceManager;
import de.dfki.lt.hfc.TupleStore;
import de.dfki.lt.hfc.WrongFormatException;
import de.dfki.lt.hfc.types.XsdDateTime;
import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;

import org.junit.Test;
import static java.lang.Math.abs;
import static java.lang.Math.abs;

/**
 * tests GetDateTime by comparing the fields of the created XsdDateTime and a
 * manually created Date() object. Milliseconds may be different (100 ms
 * tolerance). May be problematic on slower systems
 *
 * @author Christophe Biwer, christophe.biwer@dfki.de
 */
public final class GetDateTimeTest {

  @Test
  public void testGetDateTime() throws FileNotFoundException,
          WrongFormatException, IOException {

    // load NamespaceManager
    NamespaceManager namespace = NamespaceManager.getInstance();;

    // create TupleStore
    TupleStore store =
        new TupleStore(false, true, true, 2, 5, 0,1,2,4, 2, namespace,
            getTestResource("default.nt"));

    // empty array to be given to function.
    int[] ids = new int[2];

    // create FunctionalOperator
    FunctionalOperator fop =
        (FunctionalOperator)store.checkAndRegisterOperator("de.dfki.lt.hfc.operators.GetDateTime");

    // do operation
    int temp = fop.apply(ids);
    Date expected = new Date();
    XsdDateTime result = ((XsdDateTime)store.getObject(temp));
    assertEquals(1900 + expected.getYear(), result.year);
    assertEquals(1 + expected.getMonth(), result.month);
    assertEquals(expected.getDate(), result.day);
    assertEquals(expected.getHours(), result.hour);
    assertEquals(expected.getMinutes(), result.minute);
    float expSecondsMillisecond = expected.getSeconds() + (float)(expected.getTime() % 1000) / 1000;
    float resSecondsMilliseconds = result.second;
    int diff = (int)(abs(expSecondsMillisecond - resSecondsMilliseconds) * 1000);
    assertTrue(500 > diff);
  }
}
