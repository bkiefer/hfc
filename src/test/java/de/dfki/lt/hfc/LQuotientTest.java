package de.dfki.lt.hfc;

import static de.dfki.lt.hfc.TestingUtils.*;
import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.Test;

public final class LQuotientTest {

  @Test
  public void testcleanUpTuple() throws FileNotFoundException,
      WrongFormatException, IOException {

    // load Namespace
    Namespace namespace = Namespace.defaultNamespace();

    // create TupleStore
    TupleStore store =
        new TupleStore(false, true, true, 2, 5, 0,1,2,4, 2, namespace,
            getTestResource("default.nt"));

    // create example values
    String[] values_true = { "\"100\"^^<xsd:long>",
      "\"4\"^^<xsd:long>",};
    String[] values_true_exp = {"\"25\"^^<xsd:long>" };
    String[] values_false = { "\"100\"^^<xsd:long>",
      "\"4\"^^<xsd:long>",};
    String[] values_false_exp = {"\"20\"^^<xsd:long>" };

    // create example values (special cases)
    String[] values_excepNaN = { "\"0\"^^<xsd:long>",
      "\"0\"^^<xsd:long>", };

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
        (FunctionalOperator)store.operatorRegistry
        .checkAndRegister("de.dfki.lt.hfc.operators.LQuotient");

    // create FEqual
    FunctionalOperator feq =
        (FunctionalOperator)store.operatorRegistry
        .checkAndRegister("de.dfki.lt.hfc.operators.LEqual");

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
}
