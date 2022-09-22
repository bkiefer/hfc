package de.dfki.lt.hfc.operators;

import static de.dfki.lt.hfc.TestingUtils.getOperatorTestStore;
import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import de.dfki.lt.hfc.FunctionalOperator;
import de.dfki.lt.hfc.NamespaceManager;
import de.dfki.lt.hfc.TupleStore;
import de.dfki.lt.hfc.WrongFormatException;
import de.dfki.lt.hfc.types.BlankNode;
import de.dfki.lt.hfc.types.Uri;
import de.dfki.lt.hfc.types.XsdBoolean;
import de.dfki.lt.hfc.types.XsdInt;
import de.dfki.lt.hfc.types.XsdString;

/**
 * @author Christophe Biwer, christophe.biwer@dfki.de
 */
public final class IntStringToBooleanTest {
  TupleStore store;

  @Before
  public void init() throws IOException, WrongFormatException {
    // create TupleStore
    store = getOperatorTestStore();
  }

  @Test
  public void testIntStringToBoolean() {

    // create FunctionalOperator
    FunctionalOperator fop = (FunctionalOperator) store
        .checkAndRegisterOperator(
            "de.dfki.lt.hfc.operators.IntStringToBoolean");

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
    assertEquals("0", store.getObject(bools[0]),
        store.getObject(fop.apply(new int[] { args[0] })));
    assertEquals("1", store.getObject(bools[1]),
        store.getObject(fop.apply(new int[] { args[1] })));
//    assertEquals("true", store.getObject(bools[1]), store.getObject(fop.apply(new int[]{args[2]})));
//    assertEquals("false", store.getObject(bools[1]), store.getObject(fop.apply(new int[]{args[3]})));
//    assertEquals("True", store.getObject(bools[1]), store.getObject(fop.apply(new int[]{args[4]})));
//    assertEquals("False", store.getObject(bools[1]), store.getObject(fop.apply(new int[]{args[5]})));
//    assertEquals("TRUE", store.getObject(bools[1]), store.getObject(fop.apply(new int[]{args[6]})));
//    assertEquals("FALSE", store.getObject(bools[1]), store.getObject(fop.apply(new int[]{args[7]})));
    assertEquals("2", store.getObject(bools[1]),
        store.getObject(fop.apply(new int[] { args[1] })));
  }

  @Test
  public void testMakeBlankNode() {
    // create FunctionalOperator
    FunctionalOperator fop = (FunctionalOperator) store
        .checkAndRegisterOperator("de.dfki.lt.hfc.operators.MakeBlankNode");

    // store values in TupleStore, save integer-key in database
    int[] args = new int[5];

    args[0] = store.putObject((new XsdString("0")).toString());
    args[1] = store.putObject((new XsdInt(1)).toString());
    args[2] = store.putObject((new XsdBoolean(true)).toString());
    args[3] = store
        .putObject((new Uri("<rdf:type>", NamespaceManager.RDF)).toString());
    args[4] = store.putObject((new BlankNode("_:blank")).toString());

    assertEquals("MakeBlankNode", "_:|0|",
        store.getObject(fop.apply(new int[] { args[0] })).toString());
    assertEquals("MakeBlankNode", "_:|1|",
        store.getObject(fop.apply(new int[] { args[1] })).toString());
    assertEquals("MakeBlankNode", "_:|true|",
        store.getObject(fop.apply(new int[] { args[2] })).toString());
    assertEquals("MakeBlankNode", "_:|rdf:<rdf:type>|",
        store.getObject(fop.apply(new int[] { args[3] })).toString());
    assertEquals("MakeBlankNode", "_:|blank|",
        store.getObject(fop.apply(new int[] { args[4] })).toString());
  }

  @Test
  public void testNoSubClassOf() {
    // store values in TupleStore, save integer-key in database
    int[] validArgs = new int[3];

    // <rdf:type> <rdf:type> <rdf:Property> .
    validArgs[0] = store
        .putObject((new Uri("<rdf:type>", NamespaceManager.RDF)).toString());
    validArgs[1] = store
        .putObject((new Uri("<rdf:type>", NamespaceManager.RDF)).toString());
    validArgs[2] = store.putObject(
        (new Uri("<rdf:Property>", NamespaceManager.RDF)).toString());

    // TODO test also invalid args

    // create FunctionalOperator
    FunctionalOperator fop = (FunctionalOperator) store
        .checkAndRegisterOperator("de.dfki.lt.hfc.operators.NoSubClassOf");

    // do operation
    assertEquals("Valid Tuple: <rdf:type> <rdf:type> <rdf:Property>",
        FunctionalOperator.TRUE, fop.apply(validArgs));
    // assertEquals("Valid Tuple: <rdf:type> <rdf:type> <rdf:Property>",
    // FunctionalOperator.FALSE, fop.apply(invalidArgs));
  }

  @Test
  public void testNoValue() {

    // store values in TupleStore, save integer-key in database
    int[] validArgs = new int[3];

    // <rdf:type> <rdf:type> <rdf:Property> .
    validArgs[0] = store
        .putObject((new Uri("<rdf:type>", NamespaceManager.RDF)).toString());
    validArgs[1] = store
        .putObject((new Uri("<rdf:type>", NamespaceManager.RDF)).toString());
    validArgs[2] = store.putObject(
        (new Uri("<rdf:Property>", NamespaceManager.RDF)).toString());

    // TODO test also invalid args

    // create FunctionalOperator
    FunctionalOperator fop = (FunctionalOperator) store
        .checkAndRegisterOperator("de.dfki.lt.hfc.operators.NoValue");

    // do operation
    assertEquals("Valid Tuple: <rdf:type> <rdf:type> <rdf:Property>",
        FunctionalOperator.TRUE, fop.apply(validArgs));
    // assertEquals("Valid Tuple: <rdf:type> <rdf:type> <rdf:Property>",
    // FunctionalOperator.FALSE, fop.apply(invalidArgs));
  }
}
