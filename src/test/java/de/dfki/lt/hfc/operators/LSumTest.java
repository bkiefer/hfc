package de.dfki.lt.hfc.operators;

import static de.dfki.lt.hfc.TestingUtils.*;
import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.io.IOException;

import de.dfki.lt.hfc.FunctionalOperator;
import de.dfki.lt.hfc.NamespaceManager;
import de.dfki.lt.hfc.TupleStore;
import de.dfki.lt.hfc.WrongFormatException;
import de.dfki.lt.hfc.types.XsdInt;
import org.junit.Test;

public final class LSumTest {

  @Test
  public void testLSum() throws FileNotFoundException,
          WrongFormatException, IOException {

    // load NamespaceManager
    NamespaceManager namespace = NamespaceManager.getInstance();

    // create TupleStore
    TupleStore store
        = new TupleStore(false, true, true, 2, 5,0,1,2, 4, 2, namespace,
            getTestResource("default.nt"));

    // create example values
    String[] values_true = {"\"100\"^^<xsd:long>",
      "\"80\"^^<xsd:long>",};
    String[] values_true_exp = {"\"180\"^^<xsd:long>"};
    String[] values_false = {"\"100\"^^<xsd:long>",
      "\"70\"^^<xsd:long>",};
    String[] values_false_exp = {"\"180\"^^<xsd:long>"};

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
    FunctionalOperator fop
        = (FunctionalOperator) store.checkAndRegisterOperator("de.dfki.lt.hfc.operators.LSum");

    // create FEqual
    FunctionalOperator feq
        = (FunctionalOperator) store.checkAndRegisterOperator("de.dfki.lt.hfc.operators.LEqual");

    // do operation
    t_ids[1] = fop.apply(ids);
    assertEquals("100 + 80 = 180", FunctionalOperator.TRUE, feq.apply(t_ids));
    f_nids[1] = fop.apply(nids);
    assertEquals("100 + 70 != 180", FunctionalOperator.FALSE, feq.apply(f_nids));
  }

  @Test
  public void testISum1() throws FileNotFoundException,
          WrongFormatException, IOException {

    // load NamespaceManager
    NamespaceManager namespace = NamespaceManager.getInstance();

    // create TupleStore
    TupleStore store =
            new TupleStore(false, true, true, 2, 5,0,1,2, 4, 2, namespace,
                    getTestResource("default.nt"));

    // store values in TupleStore, save integer-key in database
    int[] args = new int[5];

    args[0] = store.putObject((new XsdInt(100)).toString());
    args[1] = store.putObject((new XsdInt(80)).toString());
    args[2] = store.putObject((new XsdInt(180)).toString());

    // create FunctionalOperator
    FunctionalOperator fop =
            (FunctionalOperator)store.checkAndRegisterOperator("de.dfki.lt.hfc.operators.ISum");

    // create FEqual
    FunctionalOperator feq =
            (FunctionalOperator)store.checkAndRegisterOperator("de.dfki.lt.hfc.operators.IEqual");

    // do operation
    int[] ids = new int[2];
    ids[0] = args[0];
    ids[1] = args[1];
    int sum = fop.apply(ids);
    //System.out.println(store.getObject(sum));
    assertEquals(180,((XsdInt)store.getObject(sum)).value);
  }

  @Test
  public void testISum2() throws FileNotFoundException,
          WrongFormatException, IOException {

    // load NamespaceManager
    NamespaceManager namespace = NamespaceManager.getInstance();

    // create TupleStore
    TupleStore store =
            new TupleStore(false, true, true, 2, 5,0,1,2, 4, 2, namespace,
                    getTestResource("default.nt"));

    // store values in TupleStore, save integer-key in database
    int[] args = new int[3];

    args[0] = store.putObject((new XsdInt(100)).toString());
    args[1] = store.putObject((new XsdInt(80)).toString());
    args[2] = store.putObject((new XsdInt(180)).toString());

    // create FunctionalOperator
    FunctionalOperator fop =
            (FunctionalOperator)store.checkAndRegisterOperator("de.dfki.lt.hfc.operators.ISum");

    // create FEqual
    FunctionalOperator feq =
            (FunctionalOperator)store.checkAndRegisterOperator("de.dfki.lt.hfc.operators.IEqual");

    // do operation
    int sum = fop.apply(args);
    //System.out.println(store.getObject(sum));
    assertEquals(360,((XsdInt)store.getObject(sum)).value);
  }


  @Test
  public void testISum0() throws FileNotFoundException,
          WrongFormatException, IOException {

    // load NamespaceManager
    NamespaceManager namespace = NamespaceManager.getInstance();

    // create TupleStore
    TupleStore store =
            new TupleStore(false, true, true, 2, 5,0,1,2, 4, 2, namespace,
                    getTestResource("default.nt"));

    // store values in TupleStore, save integer-key in database
    int[] args = new int[3];

    args[0] = store.putObject((new XsdInt(100)).toString());
    args[1] = store.putObject((new XsdInt(80)).toString());
    args[2] = store.putObject((new XsdInt(180)).toString());

    // create FunctionalOperator
    FunctionalOperator fop =
            (FunctionalOperator)store.checkAndRegisterOperator("de.dfki.lt.hfc.operators.ISum");

    // create FEqual
    FunctionalOperator feq =
            (FunctionalOperator)store.checkAndRegisterOperator("de.dfki.lt.hfc.operators.IEqual");

    // do operation
    int[] ids = new int[1];
    ids[0] = args[0];
    int sum = fop.apply(ids);
    //System.out.println(store.getObject(sum));
    assertEquals(100,((XsdInt)store.getObject(sum)).value);
  }
}
