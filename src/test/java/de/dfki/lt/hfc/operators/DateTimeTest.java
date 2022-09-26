package de.dfki.lt.hfc.operators;

import static de.dfki.lt.hfc.TestingUtils.getOperatorTestStore;
import static org.junit.Assert.assertEquals;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import de.dfki.lt.hfc.FunctionalOperator;
import de.dfki.lt.hfc.TupleStore;
import de.dfki.lt.hfc.WrongFormatException;
import de.dfki.lt.hfc.types.XsdDateTime;

public class DateTimeTest {
  TupleStore store;

  @Before
  public void init() throws IOException, WrongFormatException {
    // create TupleStore
    store = getOperatorTestStore();
  }

  @Test
  public void applyDec() {

    // create FunctionalOperator
    FunctionalOperator fop = (FunctionalOperator) store
        .checkAndRegisterOperator("de.dfki.lt.hfc.operators.DTDecrement");

    // store values in TupleStore, save integer-key in database
    int[] args = new int[5];
    args[0] = store.putObject((new XsdDateTime(2, 0, 0, 0, 0, 0)));
    args[1] = store.putObject((new XsdDateTime(4, 0, 0, 0, 0, 0)));

    // do operation
    assertEquals("0001-12-30T23:59:59.0",
        store.getObject(fop.apply(new int[] { args[0] })).toName());
    assertEquals("0003-12-30T23:59:59.0",
        store.getObject(fop.apply(new int[] { args[1] })).toName());
  }

  @Test
  public void applyGreaterEq() {

    // create FunctionalOperator
    FunctionalOperator fop = (FunctionalOperator) store
        .checkAndRegisterOperator("de.dfki.lt.hfc.operators.DTGreaterEqual");

    // store values in TupleStore, save integer-key in database
    int[] args = new int[5];
    args[0] = store.putObject((new XsdDateTime(2, 0, 0, 0, 0, 0)));
    args[1] = store.putObject((new XsdDateTime(4, 0, 0, 0, 0, 0)));

    // do operation
    assertEquals("0002-00-00T00:00:01.0", FunctionalOperator.FALSE,
        fop.apply(new int[] { args[0], args[1] }));
    assertEquals("0002-00-00T00:00:01.0", FunctionalOperator.TRUE,
        fop.apply(new int[] { args[0], args[0] }));
    assertEquals("0002-00-00T00:00:01.0", FunctionalOperator.TRUE,
        fop.apply(new int[] { args[1], args[0] }));

  }

  @Test
  public void applyGreater() throws FileNotFoundException, WrongFormatException,
      IOException, InterruptedException {

    // create FunctionalOperator
    FunctionalOperator fop = (FunctionalOperator) store
        .checkAndRegisterOperator("de.dfki.lt.hfc.operators.DTGreater");

    // store values in TupleStore, save integer-key in database
    int[] args = new int[5];
    args[0] = store.putObject((new XsdDateTime(2, 0, 0, 0, 0, 0)));
    args[1] = store.putObject((new XsdDateTime(4, 0, 0, 0, 0, 0)));

    // do operation
    assertEquals("0002-00-00T00:00:01.0", FunctionalOperator.FALSE,
        fop.apply(new int[] { args[0], args[1] }));
    assertEquals("0002-00-00T00:00:01.0", FunctionalOperator.FALSE,
        fop.apply(new int[] { args[0], args[0] }));
    assertEquals("0002-00-00T00:00:01.0", FunctionalOperator.TRUE,
        fop.apply(new int[] { args[1], args[0] }));

  }

  @Test
  public void applyIncrement() throws FileNotFoundException,
      WrongFormatException, IOException, InterruptedException {

    // create FunctionalOperator
    FunctionalOperator fop = (FunctionalOperator) store
        .checkAndRegisterOperator("de.dfki.lt.hfc.operators.DTIncrement");

    // store values in TupleStore, save integer-key in database
    int[] args = new int[5];
    args[0] = store.putObject((new XsdDateTime(2, 0, 0, 0, 0, 0)));
    args[1] = store.putObject((new XsdDateTime(4, 0, 0, 0, 0, 0)));

    // do operation
    assertEquals("0002-00-00T00:00:01.0",
        store.getObject(fop.apply(new int[] { args[0] })).toName());
    assertEquals("0004-00-00T00:00:01.0",
        store.getObject(fop.apply(new int[] { args[1] })).toName());
  }

  @Test
  public void testIntersectionNotEmpty() {
    // create FunctionalOperator
    FunctionalOperator fop = (FunctionalOperator) store
        .checkAndRegisterOperator(
            "de.dfki.lt.hfc.operators.DTIntersectionNotEmpty");

    // store values in TupleStore, save integer-key in database
    int[] dates = new int[4];

    dates[0] = store.putObject((new XsdDateTime(2000, 8, 19, 22, 19, 33.123f)));
    dates[1] = store.putObject((new XsdDateTime(2001, 8, 19, 22, 19, 33.123f)));
    dates[2] = store.putObject((new XsdDateTime(2002, 8, 19, 22, 19, 33.123f)));
    dates[3] = store.putObject((new XsdDateTime(2003, 8, 19, 22, 19, 33.123f)));

    // do operation
    assertEquals(FunctionalOperator.FALSE,
        fop.apply(new int[] { dates[0], dates[1], dates[2], dates[3] }));
    assertEquals(FunctionalOperator.TRUE,
        fop.apply(new int[] { dates[0], dates[3], dates[1], dates[2] }));
  }

  @Test
  public void testDTLessEqual() throws FileNotFoundException,
      WrongFormatException, IOException, InterruptedException {

    // create FunctionalOperator
    FunctionalOperator fop = (FunctionalOperator) store
        .checkAndRegisterOperator("de.dfki.lt.hfc.operators.DTLessEqual");

    FunctionalOperator gdt = (FunctionalOperator) store
        .checkAndRegisterOperator("de.dfki.lt.hfc.operators.GetDateTime");

    // store values in TupleStore, save integer-key in database
    // dates[0]: now
    int[] temp = new int[1];
    int[] dates = new int[5];

    dates[0] = gdt.apply(temp);
    // diffs in ms
    dates[1] = store.putObject((new XsdDateTime(2016, 8, 19, 22, 19, 33.123f)));
    dates[2] = store.putObject((new XsdDateTime(2016, 8, 19, 22, 19, 33.122f)));
    // identical
    dates[3] = store.putObject((new XsdDateTime(2048, 8, 19, 22, 19, 33.123f)));
    dates[4] = store.putObject((new XsdDateTime(2048, 8, 19, 22, 19, 33.123f)));

    // do operation
    assertEquals(FunctionalOperator.FALSE,
        fop.apply(new int[] { dates[0], dates[1] }));
    assertEquals(FunctionalOperator.TRUE,
        fop.apply(new int[] { dates[1], dates[0] }));

    assertEquals(FunctionalOperator.FALSE,
        fop.apply(new int[] { dates[1], dates[2] }));
    assertEquals(FunctionalOperator.TRUE,
        fop.apply(new int[] { dates[2], dates[1] }));

    assertEquals(FunctionalOperator.TRUE,
        fop.apply(new int[] { dates[2], dates[3] }));
    assertEquals(FunctionalOperator.FALSE,
        fop.apply(new int[] { dates[3], dates[2] }));

    assertEquals(FunctionalOperator.TRUE,
        fop.apply(new int[] { dates[3], dates[4] }));
  }

  @Test
  public void testDTLess() throws FileNotFoundException, WrongFormatException,
      IOException, InterruptedException {

    // create FunctionalOperator
    FunctionalOperator fop = (FunctionalOperator) store
        .checkAndRegisterOperator("de.dfki.lt.hfc.operators.DTLess");

    FunctionalOperator gdt = (FunctionalOperator) store
        .checkAndRegisterOperator("de.dfki.lt.hfc.operators.GetDateTime");

    // store values in TupleStore, save integer-key in database
    // dates[0]: now
    int[] temp = new int[1];
    int[] dates = new int[5];

    dates[0] = gdt.apply(temp);
    // diffs in ms
    dates[1] = store.putObject((new XsdDateTime(2016, 8, 19, 22, 19, 33.123f)));
    dates[2] = store.putObject((new XsdDateTime(2016, 8, 19, 22, 19, 33.122f)));
    // identical
    dates[3] = store.putObject((new XsdDateTime(2048, 8, 19, 22, 19, 33.123f)));
    dates[4] = store.putObject((new XsdDateTime(2048, 8, 19, 22, 19, 33.123f)));

    // do operation
    assertEquals(FunctionalOperator.FALSE,
        fop.apply(new int[] { dates[0], dates[1] }));
    assertEquals(FunctionalOperator.TRUE,
        fop.apply(new int[] { dates[1], dates[0] }));

    assertEquals(FunctionalOperator.FALSE,
        fop.apply(new int[] { dates[1], dates[2] }));
    assertEquals(FunctionalOperator.TRUE,
        fop.apply(new int[] { dates[2], dates[1] }));

    assertEquals(FunctionalOperator.TRUE,
        fop.apply(new int[] { dates[2], dates[3] }));
    assertEquals(FunctionalOperator.FALSE,
        fop.apply(new int[] { dates[3], dates[2] }));

    assertEquals(FunctionalOperator.FALSE,
        fop.apply(new int[] { dates[3], dates[4] }));
  }

  @Test
  public void testDTMax2() throws FileNotFoundException, WrongFormatException,
      IOException, InterruptedException {

    // create FunctionalOperator
    FunctionalOperator fop = (FunctionalOperator) store
        .checkAndRegisterOperator("de.dfki.lt.hfc.operators.DTMax2");

    FunctionalOperator gdt = (FunctionalOperator) store
        .checkAndRegisterOperator("de.dfki.lt.hfc.operators.GetDateTime");

    // store values in TupleStore, save integer-key in database
    // dates[0]: now
    int[] temp = new int[1];
    int[] dates = new int[5];

    dates[0] = gdt.apply(temp);
    // diffs in ms
    dates[1] = store.putObject((new XsdDateTime(2016, 8, 19, 22, 19, 33.123f)));
    dates[2] = store.putObject((new XsdDateTime(2016, 8, 19, 22, 19, 33.122f)));
    // identical
    dates[3] = store.putObject((new XsdDateTime(2048, 8, 19, 22, 19, 33.123f)));
    dates[4] = store.putObject((new XsdDateTime(2048, 8, 19, 22, 19, 33.123f)));

    // do operation
    assertEquals(dates[0], fop.apply(new int[] { dates[0], dates[1] }));
    assertEquals(dates[0], fop.apply(new int[] { dates[1], dates[0] }));
    assertEquals(dates[1], fop.apply(new int[] { dates[2], dates[1] }));
    assertEquals(dates[3], fop.apply(new int[] { dates[3], dates[4] }));
    assertEquals(dates[4], fop.apply(new int[] { dates[3], dates[4] }));
  }

  @Test
  public void testDTMin2() throws FileNotFoundException, WrongFormatException,
      IOException, InterruptedException {

    // create FunctionalOperator
    FunctionalOperator fop = (FunctionalOperator) store
        .checkAndRegisterOperator("de.dfki.lt.hfc.operators.DTMin2");

    FunctionalOperator gdt = (FunctionalOperator) store
        .checkAndRegisterOperator("de.dfki.lt.hfc.operators.GetDateTime");

    // store values in TupleStore, save integer-key in database
    // dates[0]: now
    int[] temp = new int[1];
    int[] dates = new int[5];

    dates[0] = gdt.apply(temp);
    // diffs in ms
    dates[1] = store.putObject((new XsdDateTime(2016, 8, 19, 22, 19, 33.123f)));
    dates[2] = store.putObject((new XsdDateTime(2016, 8, 19, 22, 19, 33.122f)));
    // identical
    dates[3] = store.putObject((new XsdDateTime(2048, 8, 19, 22, 19, 33.123f)));
    dates[4] = store.putObject((new XsdDateTime(2048, 8, 19, 22, 19, 33.123f)));

    // do operation
    assertEquals(dates[1], fop.apply(new int[] { dates[0], dates[1] }));
    assertEquals(dates[1], fop.apply(new int[] { dates[1], dates[0] }));
    assertEquals(dates[2], fop.apply(new int[] { dates[2], dates[1] }));
    assertEquals(dates[3], fop.apply(new int[] { dates[3], dates[4] }));
    assertEquals(dates[4], fop.apply(new int[] { dates[3], dates[4] }));
  }
}