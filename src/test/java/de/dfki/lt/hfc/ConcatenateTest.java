package de.dfki.lt.hfc;

import static de.dfki.lt.hfc.Utils.*;
import de.dfki.lt.hfc.types.XsdString;
import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.Test;

/**
 * @author Christophe Biwer, christophe.biwer@dfki.de
 */
public final class ConcatenateTest {

  @Test
  public void testcleanUpTuple() throws FileNotFoundException,
      WrongFormatException, IOException, InterruptedException {

    // load Namespace
    Namespace namespace = new Namespace(getTestResource("default.ns"));

    // create TupleStore
    TupleStore store =
        new TupleStore(false, true, true, 2, 5, 4, 2, namespace,
            getTestResource("default.nt"));

    // create FunctionalOperator
    FunctionalOperator fop =
        (FunctionalOperator)store.operatorRegistry
        .checkAndRegister("de.dfki.lt.hfc.operators.Concatenate");

    // store values in TupleStore, save integer-key in database
    int[] args = new int[7];

    args[0] = store.putObject((new XsdString("foo")).toString(true));
    args[1] = store.putObject((new XsdString("bar")).toString(true));
    args[2] = store.putObject((new XsdString("föö")).toString(true));
    args[3] = store.putObject((new XsdString("bär")).toString(true));

    args[4] = store.putObject((new XsdString("foobar")).toString(true));
    args[5] = store.putObject((new XsdString("foobarföö")).toString(true));
    args[6] = store.putObject((new XsdString("foobarfööbär")).toString(true));

    // do operation
    assertEquals(args[4], fop.apply(new int[]{args[0], args[1]}));
    assertEquals(args[5], fop.apply(new int[]{args[0], args[1], args[2]}));
    assertEquals(args[6], fop.apply(new int[]{args[0], args[1], args[2], args[3]}));
  }
}