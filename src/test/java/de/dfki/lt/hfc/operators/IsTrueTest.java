package de.dfki.lt.hfc.operators;

import de.dfki.lt.hfc.FunctionalOperator;
import de.dfki.lt.hfc.NamespaceManager;
import de.dfki.lt.hfc.TupleStore;
import de.dfki.lt.hfc.WrongFormatException;
import de.dfki.lt.hfc.types.*;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.io.IOException;

import static de.dfki.lt.hfc.TestingUtils.getTestResource;
import static org.junit.Assert.assertEquals;

/**
 * @author Christian Willms, christian.willms@dfki.de
 */
public final class IsTrueTest {

  @Test
  public void testIsTrue() throws
          WrongFormatException, IOException {

    // load NamespaceManager
    NamespaceManager namespace = NamespaceManager.getInstance();

    // create TupleStore
    TupleStore store =
        new TupleStore(false, true, true, 2, 5, 0,1,2,4, 2, namespace,
            getTestResource("default.nt"));

    // create FunctionalOperator
    FunctionalOperator fop =
        (FunctionalOperator)store.checkAndRegisterOperator("de.dfki.lt.hfc.operators.IsTrue");

    // store values in TupleStore, save integer-key in database
    int[] args = new int[5];

    args[0] = store.putObject((new XsdString("0")).toString());
    args[1] = store.putObject((new XsdInt(1)).toString());
    args[2] = store.putObject((new XsdBoolean(true)).toString());
    args[3] = store.putObject((new Uri("<rdf:type>", NamespaceManager.RDF)).toString());
    args[4] = store.putObject((new BlankNode("_blank")).toString());

    // do operation
    assertEquals("XsdString 0", FunctionalOperator.FALSE, fop.apply(new int[]{args[0]}));
    assertEquals("XsdInt 1", FunctionalOperator.FALSE, fop.apply(new int[]{args[1]}));
    assertEquals("XsdBoolean false", FunctionalOperator.TRUE, fop.apply(new int[]{args[2]}));
    assertEquals("Uri <rdf:type>", FunctionalOperator.FALSE, fop.apply(new int[]{args[3]}));
    assertEquals("BlankNode _:blank", FunctionalOperator.FALSE, fop.apply(new int[]{args[4]}));

  }
}
