package de.dfki.lt.hfc.operators;

import de.dfki.lt.hfc.FunctionalOperator;
import de.dfki.lt.hfc.NamespaceManager;
import de.dfki.lt.hfc.TupleStore;
import de.dfki.lt.hfc.WrongFormatException;
import de.dfki.lt.hfc.types.XsdDate;
import de.dfki.lt.hfc.types.XsdDateTime;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.io.IOException;

import static de.dfki.lt.hfc.TestingUtils.getTestResource;
import static org.junit.Assert.*;

public class DaIncrementTest {

 @Test
 public void apply() throws FileNotFoundException,
         WrongFormatException, IOException, InterruptedException {

  // load NamespaceManager
  NamespaceManager namespace = NamespaceManager.getInstance();

  // create TupleStore
  TupleStore store =
          new TupleStore(false, true, true, 2, 5, 0,1,2,4, 2, namespace,
                  getTestResource("default.nt"));

  // create FunctionalOperator
  FunctionalOperator fop =
          (FunctionalOperator)store.checkAndRegisterOperator("de.dfki.lt.hfc.operators.DaIncrement");

  // store values in TupleStore, save integer-key in database
  int[] args = new int[5];
  args[0] = store.putObject((new XsdDate(2,0,0)).toString());
  args[1] = store.putObject((new XsdDate(4,0,0)).toString());


  // do operation
  assertEquals("0002-00-01", store.getObject(fop.apply(new int[]{args[0]})).toName());
  assertEquals("0004-00-01", store.getObject(fop.apply(new int[]{args[1]})).toName());

 }

}