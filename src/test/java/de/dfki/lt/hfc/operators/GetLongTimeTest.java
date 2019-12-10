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
import static java.lang.Math.abs;

import org.junit.Test;
import static java.lang.Math.abs;

/**
 * tests GetLongTime by comparing the functions result and a manual
 * System.currentTimeMillis(); could be problematic on slow systems.
 * @author Christophe Biwer, christophe.biwer@dfki.de
 */
public final class GetLongTimeTest {

  @Test
  public void testGetLongTime() throws FileNotFoundException,
          WrongFormatException, IOException {

    // load NamespaceManager
    NamespaceManager namespace = NamespaceManager.getInstance();;

    // create TupleStore
    TupleStore store =
        new TupleStore(false, true, true, 2, 5,0,1,2, 4, 2, namespace,
            getTestResource("default.nt"));

    // empty array to be given to function.
    int[] ids = new int[2];

    // create FunctionalOperator
    FunctionalOperator fop =
        (FunctionalOperator)store.checkAndRegisterOperator("de.dfki.lt.hfc.operators.GetLongTime");

    // do operation
    int temp = fop.apply(ids);
    long expected = System.currentTimeMillis();
    long result = ((XsdLong)store.getObject(temp)).value;
    long diff = (abs(expected - result));
    assertTrue(500 > diff);

  }
}
