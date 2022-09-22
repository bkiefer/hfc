package de.dfki.lt.hfc.operators;

import static de.dfki.lt.hfc.TestingUtils.getOperatorTestStore;
import static de.dfki.lt.hfc.TestingUtils.reverse;
import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import de.dfki.lt.hfc.FunctionalOperator;
import de.dfki.lt.hfc.TupleStore;
import de.dfki.lt.hfc.WrongFormatException;

public final class FloatTest {
  TupleStore store;

  @Before
  public void init() throws IOException, WrongFormatException {
    // create TupleStore
    store = getOperatorTestStore();
  }

  @Test
  public void testFDecrement() {
    // create example values
    String[] values_eq = { "\"1.01\"^^<xsd:float>", "\"0.01\"^^<xsd:float>", };
    String[] values_neq = { "\"1.01\"^^<xsd:float>", "\"9.01\"^^<xsd:float>", };

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
        .checkAndRegisterOperator("de.dfki.lt.hfc.operators.FDecrement");

    // create FEqual
    FunctionalOperator feq = (FunctionalOperator) store
        .checkAndRegisterOperator("de.dfki.lt.hfc.operators.FEqual");

    // do operation
    ids[0] = fop.apply(ids);
    assertEquals("0.01 --", FunctionalOperator.TRUE, feq.apply(ids));
    nids[0] = fop.apply(nids);
    assertEquals("0.01 --", FunctionalOperator.FALSE, feq.apply(nids));
  }

  @Test
  public void testFDifference() {

    // create example values
    String[] values_true = { "\"100.0\"^^<xsd:float>",
        "\"80.0\"^^<xsd:float>", };
    String[] values_true_exp = { "\"20.0\"^^<xsd:float>" };
    String[] values_false = { "\"100.0\"^^<xsd:float>",
        "\"70.0\"^^<xsd:float>", };
    String[] values_false_exp = { "\"20.0\"^^<xsd:float>" };

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
        .checkAndRegisterOperator("de.dfki.lt.hfc.operators.FDifference");

    // create FEqual
    FunctionalOperator feq = (FunctionalOperator) store
        .checkAndRegisterOperator("de.dfki.lt.hfc.operators.FEqual");

    // do operation
    t_ids[1] = fop.apply(ids);
    assertEquals("100.0 - 80.0 = 20.0", FunctionalOperator.TRUE,
        feq.apply(t_ids));
    f_nids[1] = fop.apply(nids);
    assertEquals("100.0 - 70.0 != 20.0", FunctionalOperator.FALSE,
        feq.apply(f_nids));
  }

  @Test
  public void testFEqual() {

    // create example values
    String[] values = { "\"0.01\"^^<xsd:float>", "\"0.02\"^^<xsd:float>" };

    // store values in TupleStore, save integer-key in database
    int[] ids = new int[2];
    int i = 0;
    for (String val : values) {
      ids[i++] = store.putObject(val);
    }

    // create FunctionalOperator
    FunctionalOperator fop = (FunctionalOperator) store
        .checkAndRegisterOperator("de.dfki.lt.hfc.operators.FEqual");

    // do operation
    assertEquals(FunctionalOperator.FALSE, fop.apply(ids));
    ids[1] = ids[0];
    assertEquals(FunctionalOperator.TRUE, fop.apply(ids));
  }

  @Test
  public void testFGreaterEqual() {

    // create example values
    String[] values = { "\"0.01\"^^<xsd:float>", "\"0.02\"^^<xsd:float>" };

    // store values in TupleStore, save integer-key in database
    int[] ids = new int[2];
    int i = 0;
    for (String val : values) {
      ids[i++] = store.putObject(val);
    }

    // create FunctionalOperator
    FunctionalOperator fop = (FunctionalOperator) store
        .checkAndRegisterOperator("de.dfki.lt.hfc.operators.FGreaterEqual");

    // do operation
    assertEquals("0.01 >= 0.02", FunctionalOperator.FALSE, fop.apply(ids));
    reverse(ids);
    assertEquals("0.02 >= 0.01", FunctionalOperator.TRUE, fop.apply(ids));
    ids[0] = ids[1];
    assertEquals("0.01 >= 0.01", FunctionalOperator.TRUE, fop.apply(ids));
  }

  @Test
  public void testFGreater() {

    // create example values
    String[] values = { "\"0.01\"^^<xsd:float>", "\"0.02\"^^<xsd:float>" };

    // store values in TupleStore, save integer-key in database
    int[] ids = new int[2];
    int i = 0;
    for (String val : values) {
      ids[i++] = store.putObject(val);
    }

    // create FunctionalOperator
    FunctionalOperator fop = (FunctionalOperator) store
        .checkAndRegisterOperator("de.dfki.lt.hfc.operators.FGreater");

    // do operation
    assertEquals("0.01 > 0.02", FunctionalOperator.FALSE, fop.apply(ids));
    reverse(ids);
    assertEquals("0.02 > 0.01", FunctionalOperator.TRUE, fop.apply(ids));
    ids[0] = ids[1];
    assertEquals("0.01 > 0.01", FunctionalOperator.FALSE, fop.apply(ids));
  }

  @Test
  public void testFIncrement() {

    // create example values
    String[] values_eq = { "\"0.01\"^^<xsd:float>", "\"1.01\"^^<xsd:float>", };
    String[] values_neq = { "\"1.01\"^^<xsd:float>", "\"9.01\"^^<xsd:float>", };

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
        .checkAndRegisterOperator("de.dfki.lt.hfc.operators.FIncrement");

    // create FEqual
    FunctionalOperator feq = (FunctionalOperator) store
        .checkAndRegisterOperator("de.dfki.lt.hfc.operators.FEqual");

    // do operation
    ids[0] = fop.apply(ids);
    assertEquals("0.01 ++", FunctionalOperator.TRUE, feq.apply(ids));
    nids[0] = fop.apply(nids);
    assertEquals("0.01 ++", FunctionalOperator.FALSE, feq.apply(nids));
  }

  @Test
  public void testFLessEqual() {

    // create example values
    String[] values = { "\"0.01\"^^<xsd:float>", "\"0.02\"^^<xsd:float>" };

    // store values in TupleStore, save integer-key in database
    int[] ids = new int[2];
    int i = 0;
    for (String val : values) {
      ids[i++] = store.putObject(val);
    }

    // create FunctionalOperator
    FunctionalOperator fop = (FunctionalOperator) store
        .checkAndRegisterOperator("de.dfki.lt.hfc.operators.FLessEqual");

    // do operation
    assertEquals("0.01 <= 0.02", FunctionalOperator.TRUE, fop.apply(ids));
    reverse(ids);
    assertEquals("0.02 <= 0.01", FunctionalOperator.FALSE, fop.apply(ids));
    ids[0] = ids[1];
    assertEquals("0.01 <= 0.01", FunctionalOperator.TRUE, fop.apply(ids));
  }

  @Test
  public void testFLess() {
    // create example values
    String[] values = { "\"0.01\"^^<xsd:float>", "\"0.02\"^^<xsd:float>" };

    // store values in TupleStore, save integer-key in database
    int[] ids = new int[2];
    int i = 0;
    for (String val : values) {
      ids[i++] = store.putObject(val);
    }

    // create FunctionalOperator
    FunctionalOperator fop = (FunctionalOperator) store
        .checkAndRegisterOperator("de.dfki.lt.hfc.operators.FLess");

    // do operation
    assertEquals("0.01 < 0.02", FunctionalOperator.TRUE, fop.apply(ids));
    reverse(ids);
    assertEquals("0.02 < 0.01", FunctionalOperator.FALSE, fop.apply(ids));
    ids[0] = ids[1];
    assertEquals("0.01 < 0.01", FunctionalOperator.FALSE, fop.apply(ids));
  }

  @Test
  public void testFMax() {

    // create example values
    String[] values = { "\"1.01\"^^<xsd:float>", "\"0.02\"^^<xsd:float>",
        "\"-5.01\"^^<xsd:float>", "\"7.01\"^^<xsd:float>",
        "\"9.01\"^^<xsd:float>", "\"78.01\"^^<xsd:float>",
        "\"3.01\"^^<xsd:float>", "\"0.01\"^^<xsd:float>", };

    // store values in TupleStore, save integer-key in database
    int[] ids = new int[8];
    int i = 0;
    for (String val : values) {
      ids[i++] = store.putObject(val);
    }

    // create FunctionalOperator
    FunctionalOperator fop = (FunctionalOperator) store
        .checkAndRegisterOperator("de.dfki.lt.hfc.operators.FMax");

    // do operation
    int x = fop.apply(ids);
    assertEquals(store.getObject(ids[5]), store.getObject(x));

  }

  @Test
  public void testFMin() {

    // create example values
    String[] values = { "\"1.01\"^^<xsd:float>", "\"0.02\"^^<xsd:float>",
        "\"-5.01\"^^<xsd:float>", "\"7.01\"^^<xsd:float>",
        "\"9.01\"^^<xsd:float>", "\"78.01\"^^<xsd:float>",
        "\"3.01\"^^<xsd:float>", "\"0.01\"^^<xsd:float>", };

    // store values in TupleStore, save integer-key in database
    int[] ids = new int[8];
    int i = 0;
    for (String val : values) {
      ids[i++] = store.putObject(val);
    }

    // create FunctionalOperator
    FunctionalOperator fop = (FunctionalOperator) store
        .checkAndRegisterOperator("de.dfki.lt.hfc.operators.FMin");

    // do operation
    int x = fop.apply(ids);
    assertEquals(store.getObject(ids[2]), store.getObject(x));

  }

  @Test
  public void testFNotEqual() {

    // create example values
    String[] values = { "\"0.01\"^^<xsd:float>", "\"0.02\"^^<xsd:float>" };

    // store values in TupleStore, save integer-key in database
    int[] ids = new int[2];
    int i = 0;
    for (String val : values) {
      ids[i++] = store.putObject(val);
    }

    // create FunctionalOperator
    FunctionalOperator fop = (FunctionalOperator) store
        .checkAndRegisterOperator("de.dfki.lt.hfc.operators.FNotEqual");

    // do operation
    assertEquals(FunctionalOperator.TRUE, fop.apply(ids));
    ids[1] = ids[0];
    assertEquals(FunctionalOperator.FALSE, fop.apply(ids));
  }

  @Test
  public void testcleanUpTuple() {

    // create example values
    String[] values_true = { "\"100.0\"^^<xsd:float>",
        "\"80.0\"^^<xsd:float>", };
    String[] values_true_exp = { "\"8000.0\"^^<xsd:float>" };
    String[] values_false = { "\"100.0\"^^<xsd:float>",
        "\"70.0\"^^<xsd:float>", };
    String[] values_false_exp = { "\"8000.0\"^^<xsd:float>" };

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
        .checkAndRegisterOperator("de.dfki.lt.hfc.operators.FProduct");

    // create FEqual
    FunctionalOperator feq = (FunctionalOperator) store
        .checkAndRegisterOperator("de.dfki.lt.hfc.operators.FEqual");

    // do operation
    t_ids[1] = fop.apply(ids);
    assertEquals("100.0 * 80.0 = 8000.0", FunctionalOperator.TRUE,
        feq.apply(t_ids));
    f_nids[1] = fop.apply(nids);
    assertEquals("100.0 * 70.0 != 8000.0", FunctionalOperator.FALSE,
        feq.apply(f_nids));
  }

  @Test
  public void testFQuotient() {

    // create example values
    String[] values_true = { "\"100.0\"^^<xsd:float>",
        "\"4.0\"^^<xsd:float>", };
    String[] values_true_exp = { "\"25.0\"^^<xsd:float>" };
    String[] values_false = { "\"100.0\"^^<xsd:float>",
        "\"4.0\"^^<xsd:float>", };
    String[] values_false_exp = { "\"20.0\"^^<xsd:float>" };

    // create example values (special cases)
    String[] values_excepNaN = { "\"0.0\"^^<xsd:float>",
        "\"0.0\"^^<xsd:float>", };
    String[] values_excep_expNaN = { "\"NaN\"^^<xsd:float>" };
    String[] values_excepPlusInf = { "\"1.0\"^^<xsd:float>",
        "\"0.0\"^^<xsd:float>", };
    String[] values_excep_expPlusInf = {
        "\"Float.POSITIVE_INFINITY\"^^<xsd:float>" };
    String[] values_excepMinusInf = { "\"-1.0\"^^<xsd:float>",
        "\"0.0\"^^<xsd:float>", };
    String[] values_excep_expMinusInf = {
        "\"Float.NEGATIVE_INFINITY\"^^<xsd:float>" };

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
    int[] e_edsNaN = new int[2];
    i = 0;
    for (String val : values_excep_expNaN) {
      e_edsNaN[i++] = store.putObject(val);
    }
    int[] edsPInf = new int[2];
    i = 0;
    for (String val : values_excepPlusInf) {
      edsPInf[i++] = store.putObject(val);
    }
    int[] edsPInfExp = new int[2];
    i = 0;
    for (String val : values_excep_expPlusInf) {
      edsPInfExp[i++] = store.putObject(val);
    }
    int[] edsPInfm = new int[2];
    i = 0;
    for (String val : values_excepMinusInf) {
      edsPInfm[i++] = store.putObject(val);
    }
    int[] edsPInfmExp = new int[2];
    i = 0;
    for (String val : values_excep_expMinusInf) {
      edsPInfmExp[i++] = store.putObject(val);
    }

    // create FunctionalOperator
    FunctionalOperator fop = (FunctionalOperator) store
        .checkAndRegisterOperator("de.dfki.lt.hfc.operators.FQuotient");

    // create FEqual
    FunctionalOperator feq = (FunctionalOperator) store
        .checkAndRegisterOperator("de.dfki.lt.hfc.operators.FEqual");

    // do operation
    t_ids[1] = fop.apply(ids);
    assertEquals("100.0 / 4.0 = 25.0", FunctionalOperator.TRUE,
        feq.apply(t_ids));
    f_nids[1] = fop.apply(nids);
    assertEquals("100.0 / 4.0 != 20.0", FunctionalOperator.FALSE,
        feq.apply(f_nids));

// TODO: adding Float.NaN as <xsd:float> to store manually (not with
//    e_edsNaN[1] = fop.apply(edsNaN);
//    assertEquals("0.0 / 0.0 = NaN", FunctionalOperator.TRUE, feq.apply(e_edsNaN));
//    edsPInfExp[1] = fop.apply(edsPInf);
//    assertEquals("1.0 / 0.0 = Infinity", FunctionalOperator.TRUE, feq.apply(edsPInfExp));
//    edsPInfmExp[1] = fop.apply(edsPInfm);
//    assertEquals("-1.0 / 0.0 = -Infinity", FunctionalOperator.TRUE, feq.apply(edsPInfmExp));
  }

  @Test
  public void testFSum() {

    // create example values
    String[] values_true = { "\"100.0\"^^<xsd:float>",
        "\"80.0\"^^<xsd:float>", };
    String[] values_true_exp = { "\"180.0\"^^<xsd:float>" };
    String[] values_false = { "\"100.0\"^^<xsd:float>",
        "\"70.0\"^^<xsd:float>", };
    String[] values_false_exp = { "\"180.0\"^^<xsd:float>" };

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
        .checkAndRegisterOperator("de.dfki.lt.hfc.operators.FSum");

    // create FEqual
    FunctionalOperator feq = (FunctionalOperator) store
        .checkAndRegisterOperator("de.dfki.lt.hfc.operators.FEqual");

    // do operation
    t_ids[1] = fop.apply(ids);
    assertEquals("100.0 + 80.0 = 180.0", FunctionalOperator.TRUE,
        feq.apply(t_ids));
    f_nids[1] = fop.apply(nids);
    assertEquals("100.0 + 70.0 != 180.0", FunctionalOperator.FALSE,
        feq.apply(f_nids));
  }
}
