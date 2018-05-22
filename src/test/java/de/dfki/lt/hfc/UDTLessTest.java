package de.dfki.lt.hfc;

import static de.dfki.lt.hfc.TestingUtils.*;
import de.dfki.lt.hfc.types.XsdUDateTime;
import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.Test;

/**
 * @author Christophe Biwer, christophe.biwer@dfki.de
 */
public final class UDTLessTest {

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
        .checkAndRegister("de.dfki.lt.hfc.operators.UDTLess");

    // store values in TupleStore, save integer-key in database
    // dates[0]: now
    int[] dates = new int[9];

    // diffs in ms
    dates[0] = store.putObject((new XsdUDateTime(2016, 8, 19, 22, 19, 33.123f)).toString());
    dates[1] = store.putObject((new XsdUDateTime(2016, 8, 19, 22, 19, 33.122f)).toString());
    // identical
    dates[2] = store.putObject((new XsdUDateTime(2048, 8, 19, 22, 19, 33.123f)).toString());
    dates[3] = store.putObject((new XsdUDateTime(2048, 8, 19, 22, 19, 33.123f)).toString());
    // underspecified (= -1 / -1.0f)
    dates[4] = store.putObject((new XsdUDateTime(-1, 8, 19, 22, 19, 33.123f)).toString());
    dates[5] = store.putObject((new XsdUDateTime(-1, 8, 19, 23, 19, 33.123f)).toString());
    dates[6] = store.putObject((new XsdUDateTime(2048, 8, -1, -1, -1, -1.0f)).toString());
    dates[7] = store.putObject((new XsdUDateTime(2048, 9, -1, -1, -1, -1.0f)).toString());
    // extremely underspecified
    dates[8] = store.putObject((new XsdUDateTime(-1, -1, -1, -1, -1, -1.0f)).toString());

    // do operation
    assertEquals(FunctionalOperator.FALSE, fop.apply(new int[] {dates[0], dates[1]}));
    assertEquals(FunctionalOperator.TRUE, fop.apply(new int[] {dates[1], dates[0]}));

    assertEquals(FunctionalOperator.TRUE, fop.apply(new int[] {dates[1], dates[2]}));
    assertEquals(FunctionalOperator.FALSE, fop.apply(new int[] {dates[2], dates[1]}));

    assertEquals(FunctionalOperator.FALSE, fop.apply(new int[] {dates[2], dates[3]}));
    assertEquals(FunctionalOperator.FALSE, fop.apply(new int[] {dates[3], dates[2]}));

    assertEquals(FunctionalOperator.FALSE, fop.apply(new int[] {dates[4], dates[5]}));
    assertEquals(FunctionalOperator.FALSE, fop.apply(new int[] {dates[5], dates[4]}));

    assertEquals(FunctionalOperator.TRUE, fop.apply(new int[] {dates[6], dates[7]}));
    assertEquals(FunctionalOperator.FALSE, fop.apply(new int[] {dates[7], dates[6]}));

    assertEquals(FunctionalOperator.FALSE, fop.apply(new int[] {dates[7], dates[8]}));
    assertEquals(FunctionalOperator.FALSE, fop.apply(new int[] {dates[8], dates[7]}));
  }
}
