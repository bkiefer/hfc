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
import static org.junit.Assert.*;

public class DDecrementTest {

 @Test
 public void testDDecrement() throws FileNotFoundException,
         WrongFormatException, IOException, InterruptedException {

  // load NamespaceManager
  NamespaceManager namespace = NamespaceManager.getInstance();

  // create TupleStore
  TupleStore store =
          new TupleStore(false, true, true, 2, 5, 0,1,2,4, 2, namespace,
                  getTestResource("default.nt"));

  // create FunctionalOperator
  FunctionalOperator fop =
          (FunctionalOperator)store.checkAndRegisterOperator("de.dfki.lt.hfc.operators.DDecrement");

  // store values in TupleStore, save integer-key in database
  int[] args = new int[5];

  args[0] = store.putObject((new XsdDouble(0.1)).toString());
  args[1] = store.putObject((new XsdDouble(1.0)).toString());


  // do operation
  assertEquals("0.09999999999999999", store.getObject(fop.apply(new int[]{args[0]})).toName());
  assertEquals("0.9999999999999999", store.getObject(fop.apply(new int[]{args[1]})).toName());
 }
}