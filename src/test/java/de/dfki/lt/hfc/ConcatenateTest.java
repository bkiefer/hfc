package de.dfki.lt.hfc;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Test;

import de.dfki.lt.hfc.types.XsdString;

public class ConcatenateTest {

  @Test
  public void testConcatenate() throws IOException, WrongFormatException {
    TupleStore store = TestingUtils.getOperatorTestStore();

    // store values in TupleStore, save integer-key in database
    int[] args = new int[5];

    args[0] = store.putObject((new XsdString("FooBar")).toString());
    args[1] = store.putObject((new XsdString("Bar")).toString());
    args[2] = store.putObject((new XsdString("Foo")).toString());

    // create FunctionalOperator
    FunctionalOperator fop =
            (FunctionalOperator)store.checkAndRegisterOperator("de.dfki.lt.hfc.operators.Concatenate");

    // do operation
    int[] ids = new int[2];
    ids[0] = args[1];
    ids[1] = args[2];
    int concat = fop.apply(ids);
    assertEquals("BarFoo",store.getObject(concat).toName());
  }

}


