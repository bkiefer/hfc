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
import de.dfki.lt.hfc.types.XsdLong;

public final class LongTest {
  TupleStore store;

  @Before
  public void init() throws IOException, WrongFormatException {
    // create TupleStore
    store = getOperatorTestStore();
  }

  @Test
  public void testLDecrement() {
    // create example values
    String[] values_eq = { "\"1\"^^<xsd:long>", "\"0\"^^<xsd:long>", };
    String[] values_neq = { "\"9\"^^<xsd:long>", "\"7\"^^<xsd:long>", };

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
    FunctionalOperator fop = (FunctionalOperator) store
        .checkAndRegisterOperator("de.dfki.lt.hfc.operators.LDecrement");

    // create FEqual
    FunctionalOperator feq = (FunctionalOperator) store
        .checkAndRegisterOperator("de.dfki.lt.hfc.operators.LEqual");

    // do operation
    ids[0] = fop.apply(ids);
    assertEquals("1-- = 0", FunctionalOperator.TRUE, feq.apply(ids));
    nids[0] = fop.apply(nids);
    assertEquals("9-- != 7", FunctionalOperator.FALSE, feq.apply(nids));
  }

  @Test
  public void testLDifferenceTest() {
    // create example values
    String[] values_true = { "\"100\"^^<xsd:long>", "\"80\"^^<xsd:long>", };
    String[] values_true_exp = { "\"20\"^^<xsd:long>" };
    String[] values_false = { "\"100\"^^<xsd:long>", "\"70\"^^<xsd:long>", };
    String[] values_false_exp = { "\"20\"^^<xsd:long>" };

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
    FunctionalOperator fop = (FunctionalOperator) store
        .checkAndRegisterOperator("de.dfki.lt.hfc.operators.LDifference");

    // create FEqual
    FunctionalOperator feq = (FunctionalOperator) store
        .checkAndRegisterOperator("de.dfki.lt.hfc.operators.LEqual");

    // do operation
    t_ids[1] = fop.apply(ids);
    assertEquals("100 - 80 = 20", FunctionalOperator.TRUE, feq.apply(t_ids));
    f_nids[1] = fop.apply(nids);
    assertEquals("100 - 70 != 20", FunctionalOperator.FALSE, feq.apply(f_nids));
  }

  @Test
  public void testLEqual() {

    // create example values
    String[] values = { "\"1\"^^<xsd:long>", "\"2\"^^<xsd:long>" };

    // store values in TupleStore, save integer-key in database
    int[] ids = new int[2];
    int i = 0;
    for (String val : values) {
      ids[i++] = store.putObject(val);
    }

    // create FunctionalOperator
    FunctionalOperator fop = (FunctionalOperator) store
        .checkAndRegisterOperator("de.dfki.lt.hfc.operators.LEqual");

    // do operation
    assertEquals("1 != 2", FunctionalOperator.FALSE, fop.apply(ids));
    ids[1] = ids[0];
    assertEquals("1 == 1", FunctionalOperator.TRUE, fop.apply(ids));
  }

  @Test
  public void testLGreaterEqual() {
    // create example values
    String[] values = { "\"1\"^^<xsd:long>", "\"2\"^^<xsd:long>" };

    // store values in TupleStore, save integer-key in database
    int[] ids = new int[2];
    int i = 0;
    for (String val : values) {
      ids[i++] = store.putObject(val);
    }

    // create FunctionalOperator
    FunctionalOperator fop = (FunctionalOperator) store
        .checkAndRegisterOperator("de.dfki.lt.hfc.operators.LGreaterEqual");

    // do operation
    assertEquals("1 >= 2", FunctionalOperator.FALSE, fop.apply(ids));
    reverse(ids);
    assertEquals("2 >= 1", FunctionalOperator.TRUE, fop.apply(ids));
    ids[0] = ids[1];
    assertEquals("1 >= 1", FunctionalOperator.TRUE, fop.apply(ids));
  }

  @Test
  public void testLGreater() {

    // create example values
    String[] values = { "\"1\"^^<xsd:long>", "\"2\"^^<xsd:long>" };

    // store values in TupleStore, save integer-key in database
    int[] ids = new int[2];
    int i = 0;
    for (String val : values) {
      ids[i++] = store.putObject(val);
    }

    // create FunctionalOperator
    FunctionalOperator fop = (FunctionalOperator) store
        .checkAndRegisterOperator("de.dfki.lt.hfc.operators.LGreater");

    // do operation
    assertEquals("1 > 2", FunctionalOperator.FALSE, fop.apply(ids));
    reverse(ids);
    assertEquals("2 > 1", FunctionalOperator.TRUE, fop.apply(ids));
    ids[0] = ids[1];
    assertEquals("1 > 1", FunctionalOperator.FALSE, fop.apply(ids));
  }

  @Test
  public void testLIncerement() {

    // create example values
    String[] values_eq = { "\"1\"^^<xsd:long>", "\"2\"^^<xsd:long>", };
    String[] values_neq = { "\"9\"^^<xsd:long>", "\"7\"^^<xsd:long>", };

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
    FunctionalOperator fop = (FunctionalOperator) store
        .checkAndRegisterOperator("de.dfki.lt.hfc.operators.LIncrement");

    // create FEqual
    FunctionalOperator feq = (FunctionalOperator) store
        .checkAndRegisterOperator("de.dfki.lt.hfc.operators.LEqual");

    // do operation
    ids[0] = fop.apply(ids);
    assertEquals("1++ = 2", FunctionalOperator.TRUE, feq.apply(ids));
    nids[0] = fop.apply(nids);
    assertEquals("9++ != 7", FunctionalOperator.FALSE, feq.apply(nids));
  }

  @Test
  public void testLIntersectionNotEmpty() {

    // create FunctionalOperator
    FunctionalOperator fop = (FunctionalOperator) store
        .checkAndRegisterOperator(
            "de.dfki.lt.hfc.operators.LIntersectionNotEmpty");

    // store values in TupleStore, save integer-key in database
    int[] longs = new int[4];

    longs[0] = store.putObject((new XsdLong(2)).toString());
    longs[1] = store.putObject((new XsdLong(3)).toString());
    longs[2] = store.putObject((new XsdLong(4)).toString());
    longs[3] = store.putObject((new XsdLong(5)).toString());

    // do operation
    assertEquals(FunctionalOperator.FALSE,
        fop.apply(new int[] { longs[0], longs[1], longs[2], longs[3] }));
    assertEquals(FunctionalOperator.TRUE,
        fop.apply(new int[] { longs[0], longs[3], longs[1], longs[2] }));
  }

  @Test
  public void testLLessEqual() {
    // create example values
    String[] values = { "\"1\"^^<xsd:long>", "\"2\"^^<xsd:long>" };

    // store values in TupleStore, save integer-key in database
    int[] ids = new int[2];
    int i = 0;
    for (String val : values) {
      ids[i++] = store.putObject(val);
    }

    // create FunctionalOperator
    FunctionalOperator fop = (FunctionalOperator) store
        .checkAndRegisterOperator("de.dfki.lt.hfc.operators.LLessEqual");

    // do operation
    assertEquals("1 <= 2", FunctionalOperator.TRUE, fop.apply(ids));
    reverse(ids);
    assertEquals("2 <= 1", FunctionalOperator.FALSE, fop.apply(ids));
    ids[0] = ids[1];
    assertEquals("0 <= 1", FunctionalOperator.TRUE, fop.apply(ids));
  }

  @Test
  public void testLLess() {

    // create example values
    String[] values = { "\"1\"^^<xsd:long>", "\"2\"^^<xsd:long>" };

    // store values in TupleStore, save integer-key in database
    int[] ids = new int[2];
    int i = 0;
    for (String val : values) {
      ids[i++] = store.putObject(val);
    }

    // create FunctionalOperator
    FunctionalOperator fop = (FunctionalOperator) store
        .checkAndRegisterOperator("de.dfki.lt.hfc.operators.LLess");

    // do operation
    assertEquals("1 < 2", FunctionalOperator.TRUE, fop.apply(ids));
    reverse(ids);
    assertEquals("2 < 1", FunctionalOperator.FALSE, fop.apply(ids));
    ids[0] = ids[1];
    assertEquals("0 < 1", FunctionalOperator.FALSE, fop.apply(ids));
  }

  @Test
  public void testLMax2() {

    // create example values
    String[] values = { "\"1\"^^<xsd:long>", "\"0\"^^<xsd:long>",
        "\"-5\"^^<xsd:long>", "\"7\"^^<xsd:long>", "\"9\"^^<xsd:long>",
        "\"78\"^^<xsd:long>", "\"3\"^^<xsd:long>", "\"0\"^^<xsd:long>", };

    // store values in TupleStore, save integer-key in database
    int[] ids = new int[8];
    int i = 0;
    for (String val : values) {
      ids[i++] = store.putObject(val);
    }

    // create FunctionalOperator
    FunctionalOperator fop = (FunctionalOperator) store
        .checkAndRegisterOperator("de.dfki.lt.hfc.operators.LMax2");

    int x = fop.apply(ids);
    assertEquals(store.getObject(ids[0]), store.getObject(x));

  }

  @Test
  public void testLMax() {

    // create example values
    String[] values = { "\"1\"^^<xsd:long>", "\"0\"^^<xsd:long>",
        "\"-5\"^^<xsd:long>", "\"7\"^^<xsd:long>", "\"9\"^^<xsd:long>",
        "\"78\"^^<xsd:long>", "\"3\"^^<xsd:long>", "\"0\"^^<xsd:long>", };

    // store values in TupleStore, save integer-key in database
    int[] ids = new int[8];
    int i = 0;
    for (String val : values) {
      ids[i++] = store.putObject(val);
    }

    // create FunctionalOperator
    FunctionalOperator fop = (FunctionalOperator) store
        .checkAndRegisterOperator("de.dfki.lt.hfc.operators.LMax");

    int x = fop.apply(ids);
    assertEquals(store.getObject(ids[5]), store.getObject(x));

  }

  @Test
  public void testLMin2() {

    // create example values
    String[] values = { "\"1\"^^<xsd:long>", "\"0\"^^<xsd:long>",
        "\"-5\"^^<xsd:long>", "\"7\"^^<xsd:long>", "\"9\"^^<xsd:long>",
        "\"78\"^^<xsd:long>", "\"3\"^^<xsd:long>", "\"0\"^^<xsd:long>", };

    // store values in TupleStore, save integer-key in database
    int[] ids = new int[8];
    int i = 0;
    for (String val : values) {
      ids[i++] = store.putObject(val);
    }

    // create FunctionalOperator
    FunctionalOperator fop = (FunctionalOperator) store
        .checkAndRegisterOperator("de.dfki.lt.hfc.operators.LMin2");

    int x = fop.apply(ids);
    assertEquals(store.getObject(ids[1]), store.getObject(x));

  }

  @Test
  public void testLMin() {

    // create example values
    String[] values = { "\"1\"^^<xsd:long>", "\"0\"^^<xsd:long>",
        "\"-5\"^^<xsd:long>", "\"7\"^^<xsd:long>", "\"9\"^^<xsd:long>",
        "\"78\"^^<xsd:long>", "\"3\"^^<xsd:long>", "\"0\"^^<xsd:long>", };

    // store values in TupleStore, save integer-key in database
    int[] ids = new int[8];
    int i = 0;
    for (String val : values) {
      ids[i++] = store.putObject(val);
    }

    // create FunctionalOperator
    FunctionalOperator fop = (FunctionalOperator) store
        .checkAndRegisterOperator("de.dfki.lt.hfc.operators.LMin");

    int x = fop.apply(ids);
    assertEquals(store.getObject(ids[2]), store.getObject(x));

  }

  @Test
  public void testLNotEqual() {
    // create example values
    String[] values = { "\"1\"^^<xsd:long>", "\"2\"^^<xsd:long>" };

    // store values in TupleStore, save integer-key in database
    int[] ids = new int[2];
    int i = 0;
    for (String val : values) {
      ids[i++] = store.putObject(val);
    }

    // create FunctionalOperator
    FunctionalOperator fop = (FunctionalOperator) store
        .checkAndRegisterOperator("de.dfki.lt.hfc.operators.LNotEqual");

    // do operation
    assertEquals(FunctionalOperator.TRUE, fop.apply(ids));
    ids[1] = ids[0];
    assertEquals(FunctionalOperator.FALSE, fop.apply(ids));
  }

  @Test
  public void testLProduct() {

    // create example values
    String[] values_true = { "\"100\"^^<xsd:long>", "\"80\"^^<xsd:long>", };
    String[] values_true_exp = { "\"8000\"^^<xsd:long>" };
    String[] values_false = { "\"100\"^^<xsd:long>", "\"70\"^^<xsd:long>", };
    String[] values_false_exp = { "\"8000\"^^<xsd:long>" };

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
    FunctionalOperator fop = (FunctionalOperator) store
        .checkAndRegisterOperator("de.dfki.lt.hfc.operators.LProduct");

    // create FEqual
    FunctionalOperator feq = (FunctionalOperator) store
        .checkAndRegisterOperator("de.dfki.lt.hfc.operators.LEqual");

    // do operation
    t_ids[1] = fop.apply(ids);
    assertEquals("100 * 80 = 8000", FunctionalOperator.TRUE, feq.apply(t_ids));
    f_nids[1] = fop.apply(nids);
    assertEquals("100 * 70 != 8000", FunctionalOperator.FALSE,
        feq.apply(f_nids));
  }

  @Test
  public void testLQuotient() {

    // create example values
    String[] values_true = { "\"100\"^^<xsd:long>", "\"4\"^^<xsd:long>", };
    String[] values_true_exp = { "\"25\"^^<xsd:long>" };
    String[] values_false = { "\"100\"^^<xsd:long>", "\"4\"^^<xsd:long>", };
    String[] values_false_exp = { "\"20\"^^<xsd:long>" };

    // create example values (special cases)
    String[] values_excepNaN = { "\"0\"^^<xsd:long>", "\"0\"^^<xsd:long>", };

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
    FunctionalOperator fop = (FunctionalOperator) store
        .checkAndRegisterOperator("de.dfki.lt.hfc.operators.LQuotient");

    // create FEqual
    FunctionalOperator feq = (FunctionalOperator) store
        .checkAndRegisterOperator("de.dfki.lt.hfc.operators.LEqual");

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
  public void testLSum() {
    // create example values
    String[] values_true = { "\"100\"^^<xsd:long>", "\"80\"^^<xsd:long>", };
    String[] values_true_exp = { "\"180\"^^<xsd:long>" };
    String[] values_false = { "\"100\"^^<xsd:long>", "\"70\"^^<xsd:long>", };
    String[] values_false_exp = { "\"180\"^^<xsd:long>" };

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
    FunctionalOperator fop = (FunctionalOperator) store
        .checkAndRegisterOperator("de.dfki.lt.hfc.operators.LSum");

    // create FEqual
    FunctionalOperator feq = (FunctionalOperator) store
        .checkAndRegisterOperator("de.dfki.lt.hfc.operators.LEqual");

    // do operation
    t_ids[1] = fop.apply(ids);
    assertEquals("100 + 80 = 180", FunctionalOperator.TRUE, feq.apply(t_ids));
    f_nids[1] = fop.apply(nids);
    assertEquals("100 + 70 != 180", FunctionalOperator.FALSE,
        feq.apply(f_nids));
  }

}
