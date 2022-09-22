package de.dfki.lt.hfc.operators;

import de.dfki.lt.hfc.FunctionalOperator;
import de.dfki.lt.hfc.NamespaceManager;
import de.dfki.lt.hfc.TupleStore;
import de.dfki.lt.hfc.WrongFormatException;
import de.dfki.lt.hfc.types.*;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static de.dfki.lt.hfc.TestingUtils.getOperatorTestStore;
import static org.junit.Assert.assertEquals;

/**
 * @author Christian Willms, christian.willms@dfki.de
 */
public final class PrintTest {
  TupleStore store;

  @Before
  public void init() throws IOException, WrongFormatException {
    // create TupleStore
    store = getOperatorTestStore();
  }

  @Test
  public void testPrintFalse() {
    // create FunctionalOperator
    FunctionalOperator fop =
        (FunctionalOperator)store.checkAndRegisterOperator("de.dfki.lt.hfc.operators.PrintFalse");

    // store values in TupleStore, save integer-key in database
    int[] args = new int[5];

    args[0] = store.putObject((new XsdString("0")).toString());
    args[1] = store.putObject((new XsdInt(1)).toString());
    args[2] = store.putObject((new XsdBoolean(true)).toString());
    args[3] = store.putObject((new Uri("<rdf:type>", NamespaceManager.RDF)).toString());
    args[4] = store.putObject((new BlankNode("_blank")).toString());

    // do operation
    assertEquals("PrintFalse", FunctionalOperator.FALSE, fop.apply(args));
  }
  
  @Test
  public void testPrintTrue() {
    // create FunctionalOperator
    FunctionalOperator fop =
        (FunctionalOperator)store.checkAndRegisterOperator("de.dfki.lt.hfc.operators.PrintTrue");

    // store values in TupleStore, save integer-key in database
    int[] args = new int[5];

    args[0] = store.putObject((new XsdString("0")).toString());
    args[1] = store.putObject((new XsdInt(1)).toString());
    args[2] = store.putObject((new XsdBoolean(true)).toString());
    args[3] = store.putObject((new Uri("<rdf:type>", NamespaceManager.RDF)).toString());
    args[4] = store.putObject((new BlankNode("_blank")).toString());

    // do operation
    assertEquals("PrintTrue", FunctionalOperator.TRUE, fop.apply(args));
  }
}
