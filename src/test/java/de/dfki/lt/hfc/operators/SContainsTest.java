package de.dfki.lt.hfc.operators;

import static de.dfki.lt.hfc.TestingUtils.*;

import de.dfki.lt.hfc.FunctionalOperator;
import de.dfki.lt.hfc.NamespaceManager;
import de.dfki.lt.hfc.TupleStore;
import de.dfki.lt.hfc.WrongFormatException;
import de.dfki.lt.hfc.types.XsdString;
import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.Test;

/**
 * @author Christophe Biwer, christophe.biwer@dfki.de
 */
public final class SContainsTest {

  @Test
  public void testcleanUpTuple() throws FileNotFoundException,
          WrongFormatException, IOException, InterruptedException {

    // load NamespaceManager
    NamespaceManager namespace = NamespaceManager.getInstance();
    namespace.putForm("nary","http://www.lt-world.org/dom.owl#", true);

    // create TupleStore
    TupleStore store =
        new TupleStore(false, true, true, 2, 5,0,1,2, 4, 2, namespace,
            getTestResource("default.nt"));

    // create FunctionalOperator
    FunctionalOperator fop =
        (FunctionalOperator)store.checkAndRegisterOperator("de.dfki.lt.hfc.operators.SContains");

    // store values in TupleStore, save integer-key in database
    int[] args = new int[8];

    args[0] = store.putObject((new XsdString("foo")).toString());
    args[1] = store.putObject((new XsdString("bar")).toString());
    args[2] = store.putObject((new XsdString("föö")).toString());
    args[3] = store.putObject((new XsdString("bär")).toString());

    args[4] = store.putObject((new XsdString("foobar")).toString());
    args[5] = store.putObject((new XsdString("foobarföö")).toString());
    args[6] = store.putObject((new XsdString("foobarfööbär")).toString());
    args[7] = store.putObject((new XsdString("xsd")).toString());

    // do operation
    assertEquals(FunctionalOperator.FALSE, fop.apply(new int[]{args[4], args[2]}));
    assertEquals(FunctionalOperator.TRUE, fop.apply(new int[]{args[4], args[0]}));
    assertEquals(FunctionalOperator.FALSE, fop.apply(new int[]{args[4], args[7]}));
  }
}