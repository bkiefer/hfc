package de.dfki.lt.hfc;

import static de.dfki.lt.hfc.TestingUtils.*;
import de.dfki.lt.hfc.types.XsdString;
import de.dfki.lt.hfc.types.XsdBoolean;
import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.Test;

/**
 * @author Christophe Biwer, christophe.biwer@dfki.de
 */
public final class IntStringToBooleanTest {

  @Test
  public void testcleanUpTuple() throws FileNotFoundException,
      WrongFormatException, IOException, InterruptedException {

    // load Namespace
    Namespace namespace = Namespace.defaultNamespace();
    namespace.setShortIsDefault(true);

    // create TupleStore
    TupleStore store =
        new TupleStore(false, true, true, 2, 5,0,1,2, 4, 2, namespace,
            getTestResource("default.nt"));

    // create FunctionalOperator
    FunctionalOperator fop =
        (FunctionalOperator)store.operatorRegistry
        .checkAndRegister("de.dfki.lt.hfc.operators.IntStringToBoolean");

    // store values in TupleStore, save integer-key in database
    int[] args = new int[9];
    int[] bools = new int[2];

    args[0] = store.putObject((new XsdString("0")).toString());
    args[1] = store.putObject((new XsdString("1")).toString());
    args[2] = store.putObject((new XsdString("true")).toString());
    args[3] = store.putObject((new XsdString("false")).toString());
    args[4] = store.putObject((new XsdString("True")).toString());
    args[5] = store.putObject((new XsdString("False")).toString());
    args[6] = store.putObject((new XsdString("TRUE")).toString());
    args[7] = store.putObject((new XsdString("FALSE")).toString());
    args[8] = store.putObject((new XsdString("2")).toString());

    bools[0] = store.putObject((new XsdBoolean(false)).toString());
    bools[1] = store.putObject((new XsdBoolean(true)).toString());


    // do operation
    assertEquals("0", store.getObject(bools[0]), store.getObject(fop.apply(new int[]{args[0]})));
    assertEquals("1", store.getObject(bools[1]), store.getObject(fop.apply(new int[]{args[1]})));
//    assertEquals("true", store.getObject(bools[1]), store.getObject(fop.apply(new int[]{args[2]})));
//    assertEquals("false", store.getObject(bools[1]), store.getObject(fop.apply(new int[]{args[3]})));
//    assertEquals("True", store.getObject(bools[1]), store.getObject(fop.apply(new int[]{args[4]})));
//    assertEquals("False", store.getObject(bools[1]), store.getObject(fop.apply(new int[]{args[5]})));
//    assertEquals("TRUE", store.getObject(bools[1]), store.getObject(fop.apply(new int[]{args[6]})));
//    assertEquals("FALSE", store.getObject(bools[1]), store.getObject(fop.apply(new int[]{args[7]})));
    assertEquals("2", store.getObject(bools[1]), store.getObject(fop.apply(new int[]{args[1]})));


  }
}
