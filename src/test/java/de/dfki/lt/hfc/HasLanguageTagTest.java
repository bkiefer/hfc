package de.dfki.lt.hfc;

import static de.dfki.lt.hfc.TestingUtils.*;
import de.dfki.lt.hfc.types.XsdString;
import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.Test;

/**
 * @author Christophe Biwer, christophe.biwer@dfki.de
 */
public final class HasLanguageTagTest {

  @Test
  public void testcleanUpTuple() throws FileNotFoundException,
      WrongFormatException, IOException, InterruptedException {

    // load Namespace
    Namespace namespace = new Namespace();

    // create TupleStore
    TupleStore store =
        new TupleStore(false, true, true, 2, 5,0,1,2, 4, 2, namespace,
            getTestResource("default.nt"));

    // create FunctionalOperator
    FunctionalOperator fop =
        (FunctionalOperator)store.operatorRegistry
        .checkAndRegister("de.dfki.lt.hfc.operators.HasLanguageTag");

    // store values in TupleStore, save integer-key in database
    int[] args = new int[4];

    args[0] = store.putObject((new XsdString("as string")).toString());
    args[1] = store.putObject((new XsdString("\"a string\"^^xsd:string")).toString());
    args[2] = store.putObject((new XsdString("\"a string\"@en")).toString());
    args[3] = store.putObject((new XsdString("en")).toString());

    // do operation
    assertEquals(FunctionalOperator.FALSE,
        fop.apply(new int[] {args[0], args[3]}));
    assertEquals(FunctionalOperator.FALSE,
         fop.apply(new int[] {args[1], args[3]}));
    assertEquals(FunctionalOperator.TRUE,
         fop.apply(new int[] {args[2], args[3]}));


  }
}
