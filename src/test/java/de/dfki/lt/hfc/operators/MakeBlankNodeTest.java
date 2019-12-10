package de.dfki.lt.hfc.operators;

import de.dfki.lt.hfc.FunctionalOperator;
import de.dfki.lt.hfc.NamespaceManager;
import de.dfki.lt.hfc.TupleStore;
import de.dfki.lt.hfc.WrongFormatException;
import de.dfki.lt.hfc.types.*;
import org.junit.Test;

import java.io.IOException;

import static de.dfki.lt.hfc.TestingUtils.getTestResource;
import static org.junit.Assert.*;

/**
 * @author Christian Willms | christian.willms@dfki.de
 */
public class MakeBlankNodeTest {

 @Test
 public void testMakeBlankNode() throws
         WrongFormatException, IOException {

  // load NamespaceManager
  NamespaceManager namespace = NamespaceManager.getInstance();

  // create TupleStore
  TupleStore store =
          new TupleStore(false, true, true, 2, 5, 0,1,2,4, 2, namespace,
                  getTestResource("default.nt"));

  // create FunctionalOperator
  FunctionalOperator fop =
          (FunctionalOperator)store.checkAndRegisterOperator("de.dfki.lt.hfc.operators.MakeBlankNode");

  // store values in TupleStore, save integer-key in database
  int[] args = new int[5];

  args[0] = store.putObject((new XsdString("0")).toString());
  args[1] = store.putObject((new XsdInt(1)).toString());
  args[2] = store.putObject((new XsdBoolean(true)).toString());
  args[3] = store.putObject((new Uri("<rdf:type>", NamespaceManager.RDF)).toString());
  args[4] = store.putObject((new BlankNode("_:blank")).toString());

  assertEquals("MakeBlankNode", "_:|0|", store.getObject(fop.apply(new int[]{args[0]})).toString());
  assertEquals("MakeBlankNode", "_:|1|", store.getObject(fop.apply(new int[]{args[1]})).toString());
  assertEquals("MakeBlankNode", "_:|true|", store.getObject(fop.apply(new int[]{args[2]})).toString());
  assertEquals("MakeBlankNode", "_:|rdf:<rdf:type>|", store.getObject(fop.apply(new int[]{args[3]})).toString());
  assertEquals("MakeBlankNode", "_:|blank|", store.getObject(fop.apply(new int[]{args[4]})).toString());


 }
}