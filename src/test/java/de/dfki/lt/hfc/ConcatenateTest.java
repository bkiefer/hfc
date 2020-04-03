package de.dfki.lt.hfc;


import de.dfki.lt.hfc.types.*;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.io.IOException;

import static de.dfki.lt.hfc.TestingUtils.checkResult;
import static de.dfki.lt.hfc.TestingUtils.getTestResource;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;


public class ConcatenateTest {

  @Test
  public void testConcatenate() throws FileNotFoundException,
          WrongFormatException, IOException {

    // load NamespaceManager
    NamespaceManager namespace = NamespaceManager.getInstance();

    // create TupleStore
    TupleStore store =
            new TupleStore(false, true, true, 2, 5,0,1,2, 4, 2, namespace,
                    getTestResource("default.nt"));

    // store values in TupleStore, save integer-key in database
    int[] args = new int[5];

    args[0] = store.putObject((new XsdString("FooBar")).toString());
    args[1] = store.putObject((new XsdString("Bar")).toString());
    args[2] = store.putObject((new XsdString("Foo")).toString());

    // create FunctionalOperator
    FunctionalOperator fop =
            (FunctionalOperator)store.checkAndRegisterOperator("de.dfki.lt.hfc.operators.Concatenate");

    // create FEqual
    FunctionalOperator feq =
            (FunctionalOperator)store.checkAndRegisterOperator("de.dfki.lt.hfc.operators.IEqual");

    // do operation
    int[] ids = new int[2];
    ids[0] = args[1];
    ids[1] = args[2];
    int concat = fop.apply(ids);
    assertEquals("BarFoo",store.getObject(concat).toName());
  }

}


