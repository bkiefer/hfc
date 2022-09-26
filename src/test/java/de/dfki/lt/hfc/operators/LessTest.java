package de.dfki.lt.hfc.operators;

import static de.dfki.lt.hfc.TestingUtils.getOperatorTestStore;
import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import de.dfki.lt.hfc.FunctionalOperator;
import de.dfki.lt.hfc.TupleStore;
import de.dfki.lt.hfc.WrongFormatException;
import de.dfki.lt.hfc.types.XsdDecimal;
import de.dfki.lt.hfc.types.XsdDouble;
import de.dfki.lt.hfc.types.XsdFloat;
import de.dfki.lt.hfc.types.XsdInt;
import de.dfki.lt.hfc.types.XsdLong;

public class LessTest {
  TupleStore store;

  @Before
  public void init() throws IOException, WrongFormatException {
    // create TupleStore
    store = getOperatorTestStore();
  }
  
  @Test
  public void testLess() {
    int[] args = new int[5];

    // create FunctionalOperator
    FunctionalOperator fop =
        (FunctionalOperator)store.checkAndRegisterOperator("de.dfki.lt.hfc.operators.Less");

    args[0] = store.putObject((new XsdDouble(3.1)));
    args[1] = store.putObject((new XsdLong(7l)));
    args[2] = store.putObject((new XsdInt(6)));
    args[3] = store.putObject((new XsdFloat(5.0f)));

    args[4] = store.putObject((new XsdDecimal(4.0)));

    // do operation
    assertEquals(FunctionalOperator.TRUE, fop.apply(new int[]{args[4], args[2]}));
    assertEquals(FunctionalOperator.FALSE, fop.apply(new int[]{args[4], args[0]}));
    assertEquals(FunctionalOperator.TRUE, fop.apply(new int[]{args[4], args[1]}));
    assertEquals(FunctionalOperator.FALSE, fop.apply(new int[]{args[3], args[4]}));
    assertEquals(FunctionalOperator.TRUE, fop.apply(new int[]{args[2], args[1]}));
    assertEquals(FunctionalOperator.FALSE, fop.apply(new int[]{args[1], args[0]}));

  }

  @Test
  public void testLessEq() {
    int[] args = new int[5];

    // create FunctionalOperator
    FunctionalOperator fop =
        (FunctionalOperator)store.checkAndRegisterOperator("de.dfki.lt.hfc.operators.LessEqual");

    args[0] = store.putObject((new XsdDouble(3.1)));
    args[1] = store.putObject((new XsdLong(7l)));
    args[2] = store.putObject((new XsdInt(6)));
    args[3] = store.putObject((new XsdFloat(5.0f)));

    args[4] = store.putObject((new XsdDecimal(4.0)));

    // do operation
    assertEquals(FunctionalOperator.TRUE, fop.apply(new int[]{args[4], args[2]}));
    assertEquals(FunctionalOperator.FALSE, fop.apply(new int[]{args[4], args[0]}));
    assertEquals(FunctionalOperator.TRUE, fop.apply(new int[]{args[4], args[1]}));
    assertEquals(FunctionalOperator.FALSE, fop.apply(new int[]{args[3], args[4]}));
    assertEquals(FunctionalOperator.TRUE, fop.apply(new int[]{args[2], args[1]}));
    assertEquals(FunctionalOperator.FALSE, fop.apply(new int[]{args[1], args[0]}));
    assertEquals(FunctionalOperator.TRUE, fop.apply(new int[]{args[2], args[2]}));

  }

  @Test
  public void testGreater() {
    int[] args = new int[5];

    // create FunctionalOperator
    FunctionalOperator fop =
        (FunctionalOperator)store.checkAndRegisterOperator("de.dfki.lt.hfc.operators.Greater");

    args[0] = store.putObject((new XsdDouble(3.1)));
    args[1] = store.putObject((new XsdLong(7l)));
    args[2] = store.putObject((new XsdInt(6)));
    args[3] = store.putObject((new XsdFloat(5.0f)));

    args[4] = store.putObject((new XsdDecimal(4.0)));

    // do operation
    assertEquals(FunctionalOperator.FALSE, fop.apply(new int[]{args[4], args[2]}));
    assertEquals(FunctionalOperator.TRUE, fop.apply(new int[]{args[4], args[0]}));
    assertEquals(FunctionalOperator.FALSE, fop.apply(new int[]{args[4], args[1]}));
    assertEquals(FunctionalOperator.TRUE, fop.apply(new int[]{args[3], args[4]}));
    assertEquals(FunctionalOperator.FALSE, fop.apply(new int[]{args[2], args[1]}));
    assertEquals(FunctionalOperator.TRUE, fop.apply(new int[]{args[1], args[0]}));

  }

  @Test
  public void testGreaterEq() {
    int[] args = new int[5];

    // create FunctionalOperator
    FunctionalOperator fop =
        (FunctionalOperator)store.checkAndRegisterOperator("de.dfki.lt.hfc.operators.GreaterEqual");

    args[0] = store.putObject((new XsdDouble(3.1)));
    args[1] = store.putObject((new XsdLong(7l)));
    args[2] = store.putObject((new XsdInt(6)));
    args[3] = store.putObject((new XsdFloat(5.0f)));

    args[4] = store.putObject((new XsdDecimal(4.0)));

    // do operation
    assertEquals(FunctionalOperator.FALSE, fop.apply(new int[]{args[4], args[2]}));
    assertEquals(FunctionalOperator.TRUE, fop.apply(new int[]{args[4], args[0]}));
    assertEquals(FunctionalOperator.FALSE, fop.apply(new int[]{args[4], args[1]}));
    assertEquals(FunctionalOperator.TRUE, fop.apply(new int[]{args[3], args[4]}));
    assertEquals(FunctionalOperator.FALSE, fop.apply(new int[]{args[2], args[1]}));
    assertEquals(FunctionalOperator.TRUE, fop.apply(new int[]{args[1], args[0]}));
    assertEquals(FunctionalOperator.TRUE, fop.apply(new int[]{args[2], args[2]}));
  }

  @Test
  public void testEqual() {
    int[] args = new int[5];

    // create FunctionalOperator
    FunctionalOperator fop =
        (FunctionalOperator)store.checkAndRegisterOperator("de.dfki.lt.hfc.operators.GreaterEqual");

    args[0] = store.putObject((new XsdDouble(3.1)));
    args[1] = store.putObject((new XsdLong(7l)));
    args[2] = store.putObject((new XsdInt(6)));
    args[3] = store.putObject((new XsdFloat(5.0f)));

    args[4] = store.putObject((new XsdDecimal(4.0)));

    // do operation
    assertEquals(FunctionalOperator.FALSE, fop.apply(new int[]{args[4], args[2]}));
    assertEquals(FunctionalOperator.TRUE, fop.apply(new int[]{args[4], args[4]}));
    assertEquals(FunctionalOperator.FALSE, fop.apply(new int[]{args[4], args[1]}));
    assertEquals(FunctionalOperator.TRUE, fop.apply(new int[]{args[3], args[3]}));
    assertEquals(FunctionalOperator.FALSE, fop.apply(new int[]{args[2], args[1]}));
    assertEquals(FunctionalOperator.TRUE, fop.apply(new int[]{args[0], args[0]}));
    assertEquals(FunctionalOperator.TRUE, fop.apply(new int[]{args[2], args[2]}));
  }
}
