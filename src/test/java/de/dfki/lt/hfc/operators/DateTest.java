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
import de.dfki.lt.hfc.types.XsdDate;

public class DateTest {
  TupleStore store;

  @Before
  public void init() throws IOException, WrongFormatException {
    // create TupleStore
    store = getOperatorTestStore();
  }

  @Test
  public void apply() throws FileNotFoundException, WrongFormatException,
      IOException, InterruptedException {

    // create TupleStore
    TupleStore store = getOperatorTestStore();

    // create FunctionalOperator
    FunctionalOperator fop = (FunctionalOperator) store
        .checkAndRegisterOperator("de.dfki.lt.hfc.operators.DaIncrement");

    // store values in TupleStore, save integer-key in database
    int[] args = new int[5];
    args[0] = store.putObject((new XsdDate(2, 0, 0)));
    args[1] = store.putObject((new XsdDate(4, 0, 0)));

    // do operation
    assertEquals("0002-00-01",
        store.getObject(fop.apply(new int[] { args[0] })).toName());
    assertEquals("0004-00-01",
        store.getObject(fop.apply(new int[] { args[1] })).toName());
  }

  @Test
  public void applyDec() {
    // create FunctionalOperator
    FunctionalOperator fop = (FunctionalOperator) store
        .checkAndRegisterOperator("de.dfki.lt.hfc.operators.DaDecrement");

    // store values in TupleStore, save integer-key in database
    int[] args = new int[5];
    args[0] = store.putObject((new XsdDate(2, 0, 0)));
    args[1] = store.putObject((new XsdDate(4, 0, 0)));

    // do operation
    assertEquals("0001-12-30",
        store.getObject(fop.apply(new int[] { args[0] })).toName());
    assertEquals("0003-12-30",
        store.getObject(fop.apply(new int[] { args[1] })).toName());
  }
}