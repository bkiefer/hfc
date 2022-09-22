package de.dfki.lt.hfc.operators;

import static de.dfki.lt.hfc.TestingUtils.getOperatorTestStore;
import static java.lang.Math.abs;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;

import de.dfki.lt.hfc.FunctionalOperator;
import de.dfki.lt.hfc.TupleStore;
import de.dfki.lt.hfc.WrongFormatException;
import de.dfki.lt.hfc.types.XsdDateTime;
import de.dfki.lt.hfc.types.XsdLong;

/**
 * tests GetDateTime by comparing the fields of the created XsdDateTime and a
 * manually created Date() object. Milliseconds may be different (100 ms
 * tolerance). May be problematic on slower systems
 *
 * @author Christophe Biwer, christophe.biwer@dfki.de
 */
public final class GetTimeTest {
  TupleStore store;

  @Before
  public void init() throws IOException, WrongFormatException {
    // create TupleStore
    store = getOperatorTestStore();
  }

  @Test
  public void testGetDateTime() {

    // empty array to be given to function.
    int[] ids = new int[2];

    // create FunctionalOperator
    FunctionalOperator fop = (FunctionalOperator) store
        .checkAndRegisterOperator("de.dfki.lt.hfc.operators.GetDateTime");

    // do operation
    int temp = fop.apply(ids);
    Date expected = new Date();
    XsdDateTime result = ((XsdDateTime) store.getObject(temp));
    assertEquals(1900 + expected.getYear(), result.year);
    assertEquals(1 + expected.getMonth(), result.month);
    assertEquals(expected.getDate(), result.day);
    assertEquals(expected.getHours(), result.hour);
    assertEquals(expected.getMinutes(), result.minute);
    float expSecondsMillisecond = expected.getSeconds()
        + (float) (expected.getTime() % 1000) / 1000;
    float resSecondsMilliseconds = result.second;
    int diff = (int) (abs(expSecondsMillisecond - resSecondsMilliseconds)
        * 1000);
    assertTrue(500 > diff);
  }

  @Test
  public void testGetLongTime() {

    // empty array to be given to function.
    int[] ids = new int[2];

    // create FunctionalOperator
    FunctionalOperator fop = (FunctionalOperator) store
        .checkAndRegisterOperator("de.dfki.lt.hfc.operators.GetLongTime");

    // do operation
    int temp = fop.apply(ids);
    long expected = System.currentTimeMillis();
    long result = ((XsdLong) store.getObject(temp)).value;
    long diff = (abs(expected - result));
    assertTrue(500 > diff);

  }
}
