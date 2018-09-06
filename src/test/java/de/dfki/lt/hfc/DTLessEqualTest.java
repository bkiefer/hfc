package de.dfki.lt.hfc;

import static de.dfki.lt.hfc.TestingUtils.*;
import de.dfki.lt.hfc.types.XsdDateTime;
import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.Test;

/**
 * @author Christophe Biwer, christophe.biwer@dfki.de
 */
public final class DTLessEqualTest {

  @Test
  public void testcleanUpTuple() throws FileNotFoundException,
      WrongFormatException, IOException, InterruptedException {

    // load NamespaceManager
    NamespaceManager namespace = NamespaceManager.getInstance();;

    // create TupleStore
    TupleStore store =
        new TupleStore(false, true, true, 2, 5,0,1,2, 4, 2, namespace,
            getTestResource("default.nt"));

    // create FunctionalOperator
    FunctionalOperator fop =
        (FunctionalOperator)store.operatorRegistry
        .checkAndRegister("de.dfki.lt.hfc.operators.DTLessEqual");

    FunctionalOperator gdt =
        (FunctionalOperator)store.operatorRegistry
        .checkAndRegister("de.dfki.lt.hfc.operators.GetDateTime");

    // store values in TupleStore, save integer-key in database
    // dates[0]: now
    int[] temp = new int[1];
    int[] dates = new int[5];

    dates[0] = gdt.apply(temp);
    // diffs in ms
    dates[1] = store.putObject((new XsdDateTime(2016, 8, 19, 22, 19, 33.123f)).toString());
    dates[2] = store.putObject((new XsdDateTime(2016, 8, 19, 22, 19, 33.122f)).toString());
    // identical
    dates[3] = store.putObject((new XsdDateTime(2048, 8, 19, 22, 19, 33.123f)).toString());
    dates[4] = store.putObject((new XsdDateTime(2048, 8, 19, 22, 19, 33.123f)).toString());

    // do operation
    assertEquals(FunctionalOperator.FALSE, fop.apply(new int[] {dates[0], dates[1]}));
    assertEquals(FunctionalOperator.TRUE, fop.apply(new int[] {dates[1], dates[0]}));

    assertEquals(FunctionalOperator.FALSE, fop.apply(new int[] {dates[1], dates[2]}));
    assertEquals(FunctionalOperator.TRUE, fop.apply(new int[] {dates[2], dates[1]}));

    assertEquals(FunctionalOperator.TRUE, fop.apply(new int[] {dates[2], dates[3]}));
    assertEquals(FunctionalOperator.FALSE, fop.apply(new int[] {dates[3], dates[2]}));

    assertEquals(FunctionalOperator.TRUE, fop.apply(new int[] {dates[3], dates[4]}));
  }
}
