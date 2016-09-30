package de.dfki.lt.hfc;

import static de.dfki.lt.hfc.Utils.*;
import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.Test;

public final class FDifferenceTest {

  @Test
  public void testcleanUpTuple() throws FileNotFoundException,
      WrongFormatException, IOException {

    // load Namespace
    Namespace namespace = new Namespace(getTestResource("default.ns"), false);

    // create TupleStore
    TupleStore store =
        new TupleStore(false, true, true, 2, 5, 4, 2, namespace,
            getTestResource("default.nt"));

    // create example values
    String[] values_true = { "\"100.0\"^^<xsd:float>",
      "\"80.0\"^^<xsd:float>",};
    String[] values_true_exp = {"\"20.0\"^^<xsd:float>" };
    String[] values_false = { "\"100.0\"^^<xsd:float>",
      "\"70.0\"^^<xsd:float>",};
    String[] values_false_exp = {"\"20.0\"^^<xsd:float>"};

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
        (FunctionalOperator)store.operatorRegistry
        .checkAndRegister("de.dfki.lt.hfc.operators.FDifference");

    // create FEqual
    FunctionalOperator feq =
        (FunctionalOperator)store.operatorRegistry
        .checkAndRegister("de.dfki.lt.hfc.operators.FEqual");

    // do operation
    t_ids[1] = fop.apply(ids);
    assertEquals("100.0 - 80.0 = 20.0", FunctionalOperator.TRUE, feq.apply(t_ids));
    f_nids[1] = fop.apply(nids);
    assertEquals("100.0 - 70.0 != 20.0", FunctionalOperator.FALSE, feq.apply(f_nids));
  }
}
