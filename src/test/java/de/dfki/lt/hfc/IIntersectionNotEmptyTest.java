package de.dfki.lt.hfc;

import static de.dfki.lt.hfc.Utils.*;
import de.dfki.lt.hfc.types.XsdInt;
import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.Test;

/**
 * @author Christophe Biwer, christophe.biwer@dfki.de
 */
public final class IIntersectionNotEmptyTest {

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
        .checkAndRegister("de.dfki.lt.hfc.operators.IIntersectionNotEmpty");

    // store values in TupleStore, save integer-key in database
    int[] ints = new int[4];

    ints[0] = store.putObject((new XsdInt(2)).toString());
    ints[1] = store.putObject((new XsdInt(3)).toString());
    ints[2] = store.putObject((new XsdInt(4)).toString());
    ints[3] = store.putObject((new XsdInt(5)).toString());

    // do operation
    assertEquals(FunctionalOperator.FALSE, fop.apply(new int[]{ints[0], ints[1],ints[2], ints[3]}));
    assertEquals(FunctionalOperator.TRUE, fop.apply(new int[]{ints[0], ints[3],ints[1], ints[2]}));
  }
}
