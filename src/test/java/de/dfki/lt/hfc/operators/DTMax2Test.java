package de.dfki.lt.hfc.operators;

import static de.dfki.lt.hfc.TestingUtils.*;

import de.dfki.lt.hfc.FunctionalOperator;
import de.dfki.lt.hfc.NamespaceManager;
import de.dfki.lt.hfc.TupleStore;
import de.dfki.lt.hfc.WrongFormatException;
import de.dfki.lt.hfc.types.XsdDateTime;
import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.Test;

/**
 * @author Christophe Biwer, christophe.biwer@dfki.de
 */
public final class DTMax2Test {

  @Test
  public void testDTMax2() throws FileNotFoundException,
          WrongFormatException, IOException, InterruptedException {

    // load NamespaceManager
    NamespaceManager namespace = NamespaceManager.getInstance();

    // create TupleStore
    TupleStore store =
        new TupleStore(false, true, true, 2, 5,0,1,2, 4, 2, namespace,
            getTestResource("default.nt"));

    // create FunctionalOperator
    FunctionalOperator fop =
        (FunctionalOperator)store.checkAndRegisterOperator("de.dfki.lt.hfc.operators.DTMax2");

    FunctionalOperator gdt =
        (FunctionalOperator)store.checkAndRegisterOperator("de.dfki.lt.hfc.operators.GetDateTime");

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
    assertEquals(dates[0], fop.apply(new int[]{dates[0], dates[1]}));
    assertEquals(dates[0], fop.apply(new int[]{dates[1], dates[0]}));
    assertEquals(dates[1], fop.apply(new int[]{dates[2], dates[1]}));
    assertEquals(dates[3], fop.apply(new int[]{dates[3], dates[4]}));
    assertEquals(dates[4], fop.apply(new int[]{dates[3], dates[4]}));
  }
}