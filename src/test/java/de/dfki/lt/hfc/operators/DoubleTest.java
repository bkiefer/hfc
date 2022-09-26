package de.dfki.lt.hfc.operators;

import static de.dfki.lt.hfc.TestingUtils.getOperatorTestStore;
import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import de.dfki.lt.hfc.FunctionalOperator;
import de.dfki.lt.hfc.TupleStore;
import de.dfki.lt.hfc.WrongFormatException;
import de.dfki.lt.hfc.types.XsdDouble;

public class DoubleTest {
  TupleStore store;

  @Before
  public void init() throws IOException, WrongFormatException {
    // create TupleStore
    store = getOperatorTestStore();
  }

  @Test
  public void testDoubleIncrement() {

    // create FunctionalOperator
    FunctionalOperator fop = (FunctionalOperator) store
        .checkAndRegisterOperator("de.dfki.lt.hfc.operators.DIncrement");

    // store values in TupleStore, save integer-key in database
    int[] args = new int[5];

    args[0] = store.putObject((new XsdDouble(0.09999999999999999)));
    args[1] = store.putObject((new XsdDouble(0.99999999999999999)));

    // do operation
    assertEquals("0.1",
        store.getObject(fop.apply(new int[] { args[0] })).toName());
    assertEquals("1.0000000000000002",
        store.getObject(fop.apply(new int[] { args[1] })).toName());
  }

  @Test
  public void testDoubleDecrement() {

    // create FunctionalOperator
    FunctionalOperator fop = (FunctionalOperator) store
        .checkAndRegisterOperator("de.dfki.lt.hfc.operators.DDecrement");

    // store values in TupleStore, save integer-key in database
    int[] args = new int[5];

    args[0] = store.putObject((new XsdDouble(0.1)));
    args[1] = store.putObject((new XsdDouble(1.0)));

    // do operation
    assertEquals("0.09999999999999999",
        store.getObject(fop.apply(new int[] { args[0] })).toName());
    assertEquals("0.9999999999999999",
        store.getObject(fop.apply(new int[] { args[1] })).toName());
  }
}