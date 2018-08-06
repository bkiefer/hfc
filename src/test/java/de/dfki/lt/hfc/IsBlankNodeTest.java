package de.dfki.lt.hfc;

import static de.dfki.lt.hfc.TestingUtils.*;
import de.dfki.lt.hfc.types.XsdString;
import de.dfki.lt.hfc.types.XsdBoolean;
import de.dfki.lt.hfc.types.XsdInt;
import de.dfki.lt.hfc.types.Uri;
import de.dfki.lt.hfc.types.BlankNode;
import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.Test;

/**
 * @author Christophe Biwer, christophe.biwer@dfki.de
 */
public final class IsBlankNodeTest{

  @Test
  public void testcleanUpTuple() throws FileNotFoundException,
      WrongFormatException, IOException, InterruptedException {

    // load Namespace
    Namespace namespace = Namespace.defaultNamespace();

    // create TupleStore
    TupleStore store =
        new TupleStore(false, true, true, 2, 5,0,1,2, 4, 2, namespace,
            getTestResource("default.nt"));

    // create FunctionalOperator
    FunctionalOperator fop =
        (FunctionalOperator)store.operatorRegistry
        .checkAndRegister("de.dfki.lt.hfc.operators.IsBlankNode");

    // store values in TupleStore, save integer-key in database
    int[] args = new int[5];

    args[0] = store.putObject((new XsdString("0")).toString(true));
    args[1] = store.putObject((new XsdInt(1)).toString(true));
    args[2] = store.putObject((new XsdBoolean(false)).toString(true));
    args[3] = store.putObject((new Uri("<rdf:type>")).toString(true));
    args[4] = store.putObject((new BlankNode("_blank")).toString(true));

    // do operation
    assertEquals("XsdString 0", FunctionalOperator.FALSE, fop.apply(new int[]{args[0]}));
    assertEquals("XsdInt 1", FunctionalOperator.FALSE, fop.apply(new int[]{args[1]}));
    assertEquals("XsdBoolean false", FunctionalOperator.FALSE, fop.apply(new int[]{args[2]}));
    assertEquals("Uri <rdf:type>", FunctionalOperator.FALSE, fop.apply(new int[]{args[3]}));
    assertEquals("BlankNode _:blank", FunctionalOperator.TRUE, fop.apply(new int[]{args[4]}));
  }
}
