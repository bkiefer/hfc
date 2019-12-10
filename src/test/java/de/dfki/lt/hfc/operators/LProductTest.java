package de.dfki.lt.hfc.operators;

import static de.dfki.lt.hfc.TestingUtils.*;
import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.io.IOException;

import de.dfki.lt.hfc.FunctionalOperator;
import de.dfki.lt.hfc.NamespaceManager;
import de.dfki.lt.hfc.TupleStore;
import de.dfki.lt.hfc.WrongFormatException;
import org.junit.Test;

public final class LProductTest {

  @Test
  public void testLProduct() throws FileNotFoundException,
          WrongFormatException, IOException {

    // load NamespaceManager
    NamespaceManager namespace = NamespaceManager.getInstance();

    // create TupleStore
    TupleStore store =
        new TupleStore(false, true, true, 2, 5,0,1,2, 4, 2, namespace,
            getTestResource("default.nt"));

    // create example values
    String[] values_true = { "\"100\"^^<xsd:long>",
      "\"80\"^^<xsd:long>",};
    String[] values_true_exp = {"\"8000\"^^<xsd:long>" };
    String[] values_false = { "\"100\"^^<xsd:long>",
      "\"70\"^^<xsd:long>",};
    String[] values_false_exp = {"\"8000\"^^<xsd:long>" };

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
        (FunctionalOperator)store.checkAndRegisterOperator("de.dfki.lt.hfc.operators.LProduct");

    // create FEqual
    FunctionalOperator feq =
        (FunctionalOperator)store.checkAndRegisterOperator("de.dfki.lt.hfc.operators.LEqual");

    // do operation
    t_ids[1] = fop.apply(ids);
    assertEquals("100 * 80 = 8000", FunctionalOperator.TRUE, feq.apply(t_ids));
    f_nids[1] = fop.apply(nids);
    assertEquals("100 * 70 != 8000", FunctionalOperator.FALSE, feq.apply(f_nids));
  }
}
