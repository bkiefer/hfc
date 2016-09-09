package de.dfki.lt.hfc;

import static de.dfki.lt.hfc.Utils.*;
import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.Test;

public final class FQuotientTest {

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
    String[] values_true = { "\"100.0\"^^<xsd:float>",
      "\"4.0\"^^<xsd:float>",};
    String[] values_true_exp = {"\"25.0\"^^<xsd:float>" };
    String[] values_false = { "\"100.0\"^^<xsd:float>",
      "\"4.0\"^^<xsd:float>",};
    String[] values_false_exp = {"\"20.0\"^^<xsd:float>" };

    // create example values (special cases)
    String[] values_excepNaN = { "\"0.0\"^^<xsd:float>",
      "\"0.0\"^^<xsd:float>", };
    String[] values_excep_expNaN = {"\"NaN\"^^<xsd:float>" };
    String[] values_excepPlusInf = { "\"1.0\"^^<xsd:float>",
      "\"0.0\"^^<xsd:float>", };
    String[] values_excep_expPlusInf = {"\"Float.POSITIVE_INFINITY\"^^<xsd:float>" };
    String[] values_excepMinusInf = { "\"-1.0\"^^<xsd:float>",
      "\"0.0\"^^<xsd:float>", };
    String[] values_excep_expMinusInf = {"\"Float.NEGATIVE_INFINITY\"^^<xsd:float>" };


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
    FunctionalOperator fop =
        (FunctionalOperator)store.operatorRegistry
        .checkAndRegister("de.dfki.lt.hfc.operators.FQuotient");

    // create FEqual
    FunctionalOperator feq =
        (FunctionalOperator)store.operatorRegistry
        .checkAndRegister("de.dfki.lt.hfc.operators.FEqual");

    // do operation
    t_ids[1] = fop.apply(ids);
    assertEquals("100.0 / 4.0 = 25.0", FunctionalOperator.TRUE, feq.apply(t_ids));
    f_nids[1] = fop.apply(nids);
    assertEquals("100.0 / 4.0 != 20.0", FunctionalOperator.FALSE, feq.apply(f_nids));


// TODO: adding Float.NaN as <xsd:float> to store manually (not with
//    e_edsNaN[1] = fop.apply(edsNaN);
//    assertEquals("0.0 / 0.0 = NaN", FunctionalOperator.TRUE, feq.apply(e_edsNaN));
//    edsPInfExp[1] = fop.apply(edsPInf);
//    assertEquals("1.0 / 0.0 = Infinity", FunctionalOperator.TRUE, feq.apply(edsPInfExp));
//    edsPInfmExp[1] = fop.apply(edsPInfm);
//    assertEquals("-1.0 / 0.0 = -Infinity", FunctionalOperator.TRUE, feq.apply(edsPInfmExp));
  }

}
