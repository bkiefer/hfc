package de.dfki.lt.hfc;

import static de.dfki.lt.hfc.Utils.*;
import de.dfki.lt.hfc.types.XsdDateTime;
import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.Test;

/**
 * @author Christophe Biwer, christophe.biwer@dfki.de
 */
public final class DTIntersectionNotEmptyTest {

  @Test
  public void testcleanUpTuple() throws FileNotFoundException,
      WrongFormatException, IOException, InterruptedException {

    // load Namespace
    Namespace namespace = new Namespace(getTestResource("default.ns"), false);

    // create TupleStore
    TupleStore store =
        new TupleStore(false, true, true, 2, 5, 4, 2, namespace,
            getTestResource("default.nt"));

    // create FunctionalOperator
    FunctionalOperator fop =
        (FunctionalOperator)store.operatorRegistry
        .checkAndRegister("de.dfki.lt.hfc.operators.DTIntersectionNotEmpty");

    // store values in TupleStore, save integer-key in database
    int[] dates = new int[4];

    dates[0] = store.putObject((new XsdDateTime(2000, 8, 19, 22, 19, 33.123f)).toString());
    dates[1] = store.putObject((new XsdDateTime(2001, 8, 19, 22, 19, 33.123f)).toString());
    dates[2] = store.putObject((new XsdDateTime(2002, 8, 19, 22, 19, 33.123f)).toString());
    dates[3] = store.putObject((new XsdDateTime(2003, 8, 19, 22, 19, 33.123f)).toString());

    // do operation
    assertEquals(FunctionalOperator.FALSE, fop.apply(new int[]{dates[0], dates[1],dates[2], dates[3]}));
    assertEquals(FunctionalOperator.TRUE, fop.apply(new int[]{dates[0], dates[3],dates[1], dates[2]}));
  }
}
