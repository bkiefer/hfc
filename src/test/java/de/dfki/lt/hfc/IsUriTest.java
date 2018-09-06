package de.dfki.lt.hfc;

import static de.dfki.lt.hfc.TestingUtils.*;
import de.dfki.lt.hfc.types.BlankNode;
import de.dfki.lt.hfc.types.XsdString;
import de.dfki.lt.hfc.types.XsdBoolean;
import de.dfki.lt.hfc.types.XsdInt;
import de.dfki.lt.hfc.types.Uri;
import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.Test;

/**
 * @author Christophe Biwer, christophe.biwer@dfki.de
 */
public final class IsUriTest{

  @Test
  public void testcleanUpTuple() throws FileNotFoundException,
      WrongFormatException, IOException, InterruptedException {

    // load NamespaceManager
    NamespaceManager namespace = NamespaceManager.getInstance();

    // create TupleStore
    TupleStore store =
        new TupleStore(false, true, true, 2, 5, 0,1,2,4, 2, namespace,
            getTestResource("default.nt"));

    // create FunctionalOperator
    FunctionalOperator fop =
        (FunctionalOperator)store.operatorRegistry
        .checkAndRegister("de.dfki.lt.hfc.operators.IsUri");

    // store values in TupleStore, save integer-key in database
    int[] args = new int[5];

    args[0] = store.putObject((new XsdString("0")).toString());
    args[1] = store.putObject((new XsdInt(1)).toString());
    args[2] = store.putObject((new XsdBoolean(false)).toString());
    args[3] = store.putObject((new Uri("<rdf:type>", NamespaceManager.RDF)).toString());
    args[4] = store.putObject((new BlankNode("_blank")).toString());

    // do operation
    assertEquals("XsdString 0", FunctionalOperator.FALSE, fop.apply(new int[]{args[0]}));
    assertEquals("XsdInt 1", FunctionalOperator.FALSE, fop.apply(new int[]{args[1]}));
    assertEquals("XsdBoolean false", FunctionalOperator.FALSE, fop.apply(new int[]{args[2]}));
    assertEquals("Uri <rdf:type>", FunctionalOperator.TRUE, fop.apply(new int[]{args[3]}));
    assertEquals("BlankNode _:blank", FunctionalOperator.FALSE, fop.apply(new int[]{args[4]}));

  }
}
