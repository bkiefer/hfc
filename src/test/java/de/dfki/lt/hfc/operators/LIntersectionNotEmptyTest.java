package de.dfki.lt.hfc.operators;

import static de.dfki.lt.hfc.TestingUtils.*;

import de.dfki.lt.hfc.FunctionalOperator;
import de.dfki.lt.hfc.NamespaceManager;
import de.dfki.lt.hfc.TupleStore;
import de.dfki.lt.hfc.WrongFormatException;
import de.dfki.lt.hfc.types.XsdLong;
import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.Test;

/**
 * @author Christophe Biwer, christophe.biwer@dfki.de
 */
public final class LIntersectionNotEmptyTest {

  @Test
  public void testLIntersectionNotEmpty() throws FileNotFoundException,
          WrongFormatException, IOException, InterruptedException {

    // load NamespaceManager
    NamespaceManager namespace = NamespaceManager.getInstance();

    // create TupleStore
    TupleStore store =
        new TupleStore(false, true, true, 2, 5, 0,1,2,4, 2, namespace,
            getTestResource("default.nt"));

    // create FunctionalOperator
    FunctionalOperator fop =
        (FunctionalOperator)store.checkAndRegisterOperator("de.dfki.lt.hfc.operators.LIntersectionNotEmpty");

    // store values in TupleStore, save integer-key in database
    int[] longs = new int[4];

    longs[0] = store.putObject((new XsdLong(2)).toString());
    longs[1] = store.putObject((new XsdLong(3)).toString());
    longs[2] = store.putObject((new XsdLong(4)).toString());
    longs[3] = store.putObject((new XsdLong(5)).toString());

    // do operation
    assertEquals(FunctionalOperator.FALSE, fop.apply(new int[]{longs[0], longs[1],longs[2], longs[3]}));
    assertEquals(FunctionalOperator.TRUE, fop.apply(new int[]{longs[0], longs[3],longs[1], longs[2]}));
  }
}
