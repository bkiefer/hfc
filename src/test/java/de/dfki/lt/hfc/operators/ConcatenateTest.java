package de.dfki.lt.hfc.operators;

import static de.dfki.lt.hfc.TestingUtils.getOperatorTestStore;
import static org.junit.Assert.assertEquals;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.Test;

import de.dfki.lt.hfc.FunctionalOperator;
import de.dfki.lt.hfc.TupleStore;
import de.dfki.lt.hfc.WrongFormatException;
import de.dfki.lt.hfc.types.XsdString;

/**
 * @author Christophe Biwer, christophe.biwer@dfki.de
 */
public final class ConcatenateTest {

  @Test
  public void testConcatenate() throws FileNotFoundException,
      WrongFormatException, IOException, InterruptedException {

    // create TupleStore
    TupleStore store = getOperatorTestStore();

    // create FunctionalOperator
    FunctionalOperator fop = (FunctionalOperator) store
        .checkAndRegisterOperator("de.dfki.lt.hfc.operators.Concatenate");

    // store values in TupleStore, save integer-key in database
    int[] args = new int[7];

    args[0] = store.putObject((new XsdString("foo")));
    args[1] = store.putObject((new XsdString("bar")));
    args[2] = store.putObject((new XsdString("föö")));
    args[3] = store.putObject((new XsdString("bär")));
    args[4] = store.putObject((new XsdString("foobar")));
    args[5] = store.putObject((new XsdString("foobarföö")));
    args[6] = store.putObject((new XsdString("foobarfööbär")));

    // do operation
    assertEquals(args[4], fop.apply(new int[] { args[0], args[1] }));
    assertEquals(args[5], fop.apply(new int[] { args[0], args[1], args[2] }));
    assertEquals(args[6],
        fop.apply(new int[] { args[0], args[1], args[2], args[3] }));
  }
}
