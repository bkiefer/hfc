package de.dfki.lt.hfc.operators;

import static de.dfki.lt.hfc.TestingUtils.getOperatorTestStore;
import static de.dfki.lt.hfc.TestingUtils.reverse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import de.dfki.lt.hfc.FunctionalOperator;
import de.dfki.lt.hfc.TupleStore;
import de.dfki.lt.hfc.WrongFormatException;
import de.dfki.lt.hfc.types.XsdInt;

public final class IntTest {
  TupleStore store;

  @Before
  public void init() throws IOException, WrongFormatException {
    // create TupleStore
    store = getOperatorTestStore();
  }
  
  @Test
  public void testIDecrement() {
    // create example values
    String[] values_eq = { "\"1\"^^<xsd:int>",
      "\"0\"^^<xsd:int>",};
    String[] values_neq = { "\"9\"^^<xsd:int>",
      "\"7\"^^<xsd:int>",};

    // store values in TupleStore, save integer-key in database
    int[] ids = new int[2];
    int i = 0;
    for (String val : values_eq) {
      ids[i++] = store.putObject(val);
    }
    int[] nids = new int[2];
    i = 0;
    for (String val : values_neq) {
      nids[i++] = store.putObject(val);
    }

    // create FunctionalOperator
    FunctionalOperator fop =
        (FunctionalOperator)store.checkAndRegisterOperator("de.dfki.lt.hfc.operators.IDecrement");

    // create FEqual
    FunctionalOperator feq =
        (FunctionalOperator)store.checkAndRegisterOperator("de.dfki.lt.hfc.operators.IEqual");

    // do operation
    ids[0] = fop.apply(ids);
    assertEquals("1-- = 0", FunctionalOperator.TRUE, feq.apply(ids));
    nids[0] = fop.apply(nids);
    assertEquals("9-- != 7", FunctionalOperator.FALSE, feq.apply(nids));
  }

  @Test
  public void testIDifference() {

    // create example values
    String[] values_true = { "\"100\"^^<xsd:int>",
      "\"80\"^^<xsd:int>",};
    String[] values_true_exp = {"\"20\"^^<xsd:int>" };
    String[] values_false = { "\"100\"^^<xsd:int>",
      "\"70\"^^<xsd:int>",};
    String[] values_false_exp = {"\"20\"^^<xsd:int>"};

    // store values in TupleStore, save integer-key in database
    int[] ids = new int[2];
    int i = 0;
    for (String val : values_true) {
      ids[i++] = store.putObject(val);
    }
    int[] nids = new int[2];
    i = 0;
    for (String val : values_false) {
      nids[i++] = store.putObject(val);
    }
    int[] t_ids = new int[2];
    i = 0;
    for (String val : values_true_exp) {
      t_ids[i++] = store.putObject(val);
    }
    int[] f_nids = new int[2];
    i = 0;
    for (String val : values_false_exp) {
      f_nids[i++] = store.putObject(val);
    }

    // create FunctionalOperator
    FunctionalOperator fop =
        (FunctionalOperator)store.checkAndRegisterOperator("de.dfki.lt.hfc.operators.IDifference");

    // create FEqual
    FunctionalOperator feq =
        (FunctionalOperator)store.checkAndRegisterOperator("de.dfki.lt.hfc.operators.IEqual");

    // do operation
    t_ids[1] = fop.apply(ids);
    assertEquals("100 - 80 = 20", FunctionalOperator.TRUE, feq.apply(t_ids));
    f_nids[1] = fop.apply(nids);
    assertEquals("100 - 70 != 20", FunctionalOperator.FALSE, feq.apply(f_nids));
  }

  @Test
  public void testIEqual() {

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
        (FunctionalOperator)store.checkAndRegisterOperator("de.dfki.lt.hfc.operators.IEqual");

    // do operation
    assertEquals("1 != 2", FunctionalOperator.FALSE, fop.apply(ids));
    ids[1] = ids[0];
    assertEquals("1 == 1", FunctionalOperator.TRUE, fop.apply(ids));
  }

  @Test
  public void testIGreaterEqual() {

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
    reverse(ids);
    assertEquals("2 >= 1", FunctionalOperator.TRUE, fop.apply(ids));
    ids[0] = ids[1];
    assertEquals("1 >= 1", FunctionalOperator.TRUE, fop.apply(ids));
  }

  @Test
  public void testIGreater() {

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
        (FunctionalOperator)store.checkAndRegisterOperator("de.dfki.lt.hfc.operators.IGreater");

    // do operation
    assertEquals("1 > 2", FunctionalOperator.FALSE, fop.apply(ids));
    reverse(ids);
    assertEquals("2 > 1", FunctionalOperator.TRUE, fop.apply(ids));
    ids[0] = ids[1];
    assertEquals("1 > 1", FunctionalOperator.FALSE, fop.apply(ids));
  }

  @Test
  public void testIIncrement() {

    // create example values
    String[] values_eq = { "\"1\"^^<xsd:int>",
      "\"2\"^^<xsd:int>",};
    String[] values_neq = { "\"9\"^^<xsd:int>",
      "\"7\"^^<xsd:int>",};

    // store values in TupleStore, save integer-key in database
    int[] ids = new int[2];
    int i = 0;
    for (String val : values_eq) {
      ids[i++] = store.putObject(val);
    }
    int[] nids = new int[2];
    i = 0;
    for (String val : values_neq) {
      nids[i++] = store.putObject(val);
    }

    // create FunctionalOperator
    FunctionalOperator fop =
        (FunctionalOperator)store.checkAndRegisterOperator("de.dfki.lt.hfc.operators.IIncrement");

    // create FEqual
    FunctionalOperator feq =
        (FunctionalOperator)store.checkAndRegisterOperator("de.dfki.lt.hfc.operators.IEqual");

    // do operation
    ids[0] = fop.apply(ids);
    assertEquals("1++ = 2", FunctionalOperator.TRUE, feq.apply(ids));
    nids[0] = fop.apply(nids);
    assertEquals("9++ != 7", FunctionalOperator.FALSE, feq.apply(nids));
  }

  @Test
  public void testIIntersectionNotEmpty() {

    // create FunctionalOperator
    FunctionalOperator fop =
        (FunctionalOperator)store.checkAndRegisterOperator("de.dfki.lt.hfc.operators.IIntersectionNotEmpty");

    // store values in TupleStore, save integer-key in database
    int[] ints = new int[4];

    ints[0] = store.putObject((new XsdInt(2)).toString());
    ints[1] = store.putObject((new XsdInt(3)).toString());
    ints[2] = store.putObject((new XsdInt(4)).toString());
    ints[3] = store.putObject((new XsdInt(5)).toString());

    // do operation
    assertEquals(FunctionalOperator.FALSE, fop.apply(new int[]{ints[0], ints[1],ints[2], ints[3]}));
    assertEquals(FunctionalOperator.TRUE, fop.apply(new int[]{ints[0], ints[3],ints[1], ints[2]}));
  }

  @Test
  public void testILessEqual() {

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
        (FunctionalOperator)store.checkAndRegisterOperator("de.dfki.lt.hfc.operators.ILessEqual");

    // do operation
    assertEquals("1 <= 2", FunctionalOperator.TRUE, fop.apply(ids));
    reverse(ids);
    assertEquals("2 <= 1", FunctionalOperator.FALSE, fop.apply(ids));
    ids[0] = ids[1];
    assertEquals("0 <= 1", FunctionalOperator.TRUE, fop.apply(ids));
  }

  @Test
  public void testILess() {

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
        (FunctionalOperator)store.checkAndRegisterOperator("de.dfki.lt.hfc.operators.ILess");

    // do operation
    assertEquals("1 < 2", FunctionalOperator.TRUE, fop.apply(ids));
    reverse(ids);
    assertEquals("2 < 1", FunctionalOperator.FALSE, fop.apply(ids));
    ids[0] = ids[1];
    assertEquals("0 < 1", FunctionalOperator.FALSE, fop.apply(ids));
  }

  @Test
  public void testIMax2() {
    // create example values
    String[] values = {
      "\"1\"^^<xsd:int>",
      "\"0\"^^<xsd:int>",
      "\"-5\"^^<xsd:int>",
      "\"7\"^^<xsd:int>",
      "\"9\"^^<xsd:int>",
      "\"78\"^^<xsd:int>",
      "\"3\"^^<xsd:int>",
      "\"0\"^^<xsd:int>",
    };

    // store values in TupleStore, save integer-key in database
    int[] ids = new int[8];
    int i = 0;
    for (String val : values) {
      ids[i++] = store.putObject(val);
    }

    // create FunctionalOperator
    FunctionalOperator fop =
        (FunctionalOperator)store.checkAndRegisterOperator("de.dfki.lt.hfc.operators.IMax2");

    int x = fop.apply(ids);
    assertEquals(store.getObject(ids[0]),
        store.getObject(x));

  }

  @Test
  public void testIMax() {

    // create example values
    String[] values = {
      "\"1\"^^<xsd:int>",
      "\"0\"^^<xsd:int>",
      "\"-5\"^^<xsd:int>",
      "\"7\"^^<xsd:int>",
      "\"9\"^^<xsd:int>",
      "\"78\"^^<xsd:int>",
      "\"3\"^^<xsd:int>",
      "\"0\"^^<xsd:int>",
    };

    // store values in TupleStore, save integer-key in database
    int[] ids = new int[8];
    int i = 0;
    for (String val : values) {
      ids[i++] = store.putObject(val);
    }

    // create FunctionalOperator
    FunctionalOperator fop =
        (FunctionalOperator)store.checkAndRegisterOperator("de.dfki.lt.hfc.operators.IMax");

    int x = fop.apply(ids);
    assertEquals(store.getObject(ids[5]),
        store.getObject(x));

  }

  @Test
  public void testIMin2() {
    // create example values
    String[] values = {
      "\"1\"^^<xsd:int>",
      "\"0\"^^<xsd:int>",
      "\"-5\"^^<xsd:int>",
      "\"7\"^^<xsd:int>",
      "\"9\"^^<xsd:int>",
      "\"78\"^^<xsd:int>",
      "\"3\"^^<xsd:int>",
      "\"0\"^^<xsd:int>",
    };

    // store values in TupleStore, save integer-key in database
    int[] ids = new int[8];
    int i = 0;
    for (String val : values) {
      ids[i++] = store.putObject(val);
    }

    // create FunctionalOperator
    FunctionalOperator fop =
        (FunctionalOperator)store.checkAndRegisterOperator("de.dfki.lt.hfc.operators.IMin2");

    int x = fop.apply(ids);
    assertEquals(store.getObject(ids[1]),
        store.getObject(x));

  }

  @Test
  public void testIMin() {

    // create example values
    String[] values = {
      "\"1\"^^<xsd:int>",
      "\"0\"^^<xsd:int>",
      "\"-5\"^^<xsd:int>",
      "\"7\"^^<xsd:int>",
      "\"9\"^^<xsd:int>",
      "\"78\"^^<xsd:int>",
      "\"3\"^^<xsd:int>",
      "\"0\"^^<xsd:int>",
    };

    // store values in TupleStore, save integer-key in database
    int[] ids = new int[8];
    int i = 0;
    for (String val : values) {
      ids[i++] = store.putObject(val);
    }

    // create FunctionalOperator
    FunctionalOperator fop =
        (FunctionalOperator)store.checkAndRegisterOperator("de.dfki.lt.hfc.operators.IMin");

    int x = fop.apply(ids);
    assertEquals(store.getObject(ids[2]),
        store.getObject(x));

  }

  @Test
  public void testINotEqual() {
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
        (FunctionalOperator)store.checkAndRegisterOperator("de.dfki.lt.hfc.operators.INotEqual");

    // do operation
    assertEquals(FunctionalOperator.TRUE, fop.apply(ids));
    ids[1] = ids[0];
    assertEquals(FunctionalOperator.FALSE, fop.apply(ids));
  }

  @Test
  public void testIProduct() {

    // create example values
    String[] values_true = { "\"100\"^^<xsd:int>",
      "\"80\"^^<xsd:int>",};
    String[] values_true_exp = {"\"8000\"^^<xsd:int>" };
    String[] values_false = { "\"100\"^^<xsd:int>",
      "\"70\"^^<xsd:int>",};
    String[] values_false_exp = {"\"8000\"^^<xsd:int>" };

    // store values in TupleStore, save integer-key in database
    int[] ids = new int[2];
    int i = 0;
    for (String val : values_true) {
      ids[i++] = store.putObject(val);
    }
    int[] nids = new int[2];
    i = 0;
    for (String val : values_false) {
      nids[i++] = store.putObject(val);
    }
    int[] t_ids = new int[2];
    i = 0;
    for (String val : values_true_exp) {
      t_ids[i++] = store.putObject(val);
    }
    int[] f_nids = new int[2];
    i = 0;
    for (String val : values_false_exp) {
      f_nids[i++] = store.putObject(val);
    }

    // create FunctionalOperator
    FunctionalOperator fop =
        (FunctionalOperator)store.checkAndRegisterOperator("de.dfki.lt.hfc.operators.IProduct");

    // create FEqual
    FunctionalOperator feq =
        (FunctionalOperator)store.checkAndRegisterOperator("de.dfki.lt.hfc.operators.IEqual");

    // do operation
    t_ids[1] = fop.apply(ids);
    assertEquals("100 * 80 = 8000", FunctionalOperator.TRUE, feq.apply(t_ids));
    f_nids[1] = fop.apply(nids);
    assertEquals("100 * 70 != 8000", FunctionalOperator.FALSE, feq.apply(f_nids));
  }

  @Test
  public void testIQuotient() {

    // create example values
    String[] values_true = { "\"100\"^^<xsd:int>",
      "\"4\"^^<xsd:int>",};
    String[] values_true_exp = {"\"25\"^^<xsd:int>" };
    String[] values_false = { "\"100\"^^<xsd:int>",
      "\"4\"^^<xsd:int>",};
    String[] values_false_exp = {"\"20\"^^<xsd:int>" };

    // create example values (special cases)
    String[] values_excepNaN = { "\"0\"^^<xsd:int>",
      "\"0\"^^<xsd:int>", };

    // store values in TupleStore, save integer-key in database
    int[] ids = new int[2];
    int i = 0;
    for (String val : values_true) {
      ids[i++] = store.putObject(val);
    }
    int[] nids = new int[2];
    i = 0;
    for (String val : values_false) {
      nids[i++] = store.putObject(val);
    }
    int[] t_ids = new int[2];
    i = 0;
    for (String val : values_true_exp) {
      t_ids[i++] = store.putObject(val);
    }
    int[] f_nids = new int[2];
    i = 0;
    for (String val : values_false_exp) {
      f_nids[i++] = store.putObject(val);
    }
    int[] edsNaN = new int[2];
    i = 0;
    for (String val : values_excepNaN) {
      edsNaN[i++] = store.putObject(val);
    }

    // create FunctionalOperator
    FunctionalOperator fop =
        (FunctionalOperator)store.checkAndRegisterOperator("de.dfki.lt.hfc.operators.IQuotient");

    // create FEqual
    FunctionalOperator feq =
        (FunctionalOperator)store.checkAndRegisterOperator("de.dfki.lt.hfc.operators.IEqual");

    // do operation
    t_ids[1] = fop.apply(ids);
    assertEquals("100 / 4 = 25", FunctionalOperator.TRUE, feq.apply(t_ids));
    f_nids[1] = fop.apply(nids);
    assertEquals("100 / 4 != 20", FunctionalOperator.FALSE, feq.apply(f_nids));

    try {
    fop.apply(edsNaN);
    } catch (ArithmeticException e) {
      assertTrue(true);
    }
  }

  @Test
  public void testISumTest() {

    // create example values
    String[] values_true = { "\"100\"^^<xsd:int>",
      "\"80\"^^<xsd:int>",};
    String[] values_true_exp = {"\"180\"^^<xsd:int>" };
    String[] values_false = { "\"100\"^^<xsd:int>",
      "\"70\"^^<xsd:int>",};
    String[] values_false_exp = {"\"180\"^^<xsd:int>" };

    // store values in TupleStore, save integer-key in database
    int[] ids = new int[2];
    int i = 0;
    for (String val : values_true) {
      ids[i++] = store.putObject(val);
    }
    int[] nids = new int[2];
    i = 0;
    for (String val : values_false) {
      nids[i++] = store.putObject(val);
    }
    int[] t_ids = new int[2];
    i = 0;
    for (String val : values_true_exp) {
      t_ids[i++] = store.putObject(val);
    }
    int[] f_nids = new int[2];
    i = 0;
    for (String val : values_false_exp) {
      f_nids[i++] = store.putObject(val);
    }

    // create FunctionalOperator
    FunctionalOperator fop =
        (FunctionalOperator)store.checkAndRegisterOperator("de.dfki.lt.hfc.operators.ISum");

    // create FEqual
    FunctionalOperator feq =
        (FunctionalOperator)store.checkAndRegisterOperator("de.dfki.lt.hfc.operators.IEqual");

    // do operation
    t_ids[1] = fop.apply(ids);
    assertEquals("100 + 80 = 180", FunctionalOperator.TRUE, feq.apply(t_ids));
    f_nids[1] = fop.apply(nids);
    assertEquals("100 + 70 != 180", FunctionalOperator.FALSE, feq.apply(f_nids));
  }
  
  
  @Test
  public void testISum1() {
    // store values in TupleStore, save integer-key in database
    int[] args = new int[5];

    args[0] = store.putObject((new XsdInt(100)).toString());
    args[1] = store.putObject((new XsdInt(80)).toString());
    args[2] = store.putObject((new XsdInt(180)).toString());

    // create FunctionalOperator
    FunctionalOperator fop = (FunctionalOperator) store
        .checkAndRegisterOperator("de.dfki.lt.hfc.operators.ISum");

    // do operation
    int[] ids = new int[2];
    ids[0] = args[0];
    ids[1] = args[1];
    int sum = fop.apply(ids);
    // System.out.println(store.getObject(sum));
    assertEquals(180, ((XsdInt) store.getObject(sum)).value);
  }

  @Test
  public void testISum2() {
    // store values in TupleStore, save integer-key in database
    int[] args = new int[3];

    args[0] = store.putObject((new XsdInt(100)).toString());
    args[1] = store.putObject((new XsdInt(80)).toString());
    args[2] = store.putObject((new XsdInt(180)).toString());

    // create FunctionalOperator
    FunctionalOperator fop = (FunctionalOperator) store
        .checkAndRegisterOperator("de.dfki.lt.hfc.operators.ISum");

    // do operation
    int sum = fop.apply(args);
    // System.out.println(store.getObject(sum));
    assertEquals(360, ((XsdInt) store.getObject(sum)).value);
  }

  @Test
  public void testISum0() {
    // store values in TupleStore, save integer-key in database
    int[] args = new int[3];

    args[0] = store.putObject((new XsdInt(100)).toString());
    args[1] = store.putObject((new XsdInt(80)).toString());
    args[2] = store.putObject((new XsdInt(180)).toString());

    // create FunctionalOperator
    FunctionalOperator fop = (FunctionalOperator) store
        .checkAndRegisterOperator("de.dfki.lt.hfc.operators.ISum");

    // do operation
    int[] ids = new int[1];
    ids[0] = args[0];
    int sum = fop.apply(ids);
    // System.out.println(store.getObject(sum));
    assertEquals(100, ((XsdInt) store.getObject(sum)).value);
  }
}
