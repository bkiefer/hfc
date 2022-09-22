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
import de.dfki.lt.hfc.types.XsdUDateTime;

/**
 * @author Christophe Biwer, christophe.biwer@dfki.de
 */
public final class BoolFuncTest {
  TupleStore store;

  @Before
  public void init() throws IOException, WrongFormatException {
    // create TupleStore
    store = getOperatorTestStore();
  }

  @Test
  public void testIsAtom() {

    // create FunctionalOperator
    FunctionalOperator fop = (FunctionalOperator) store
        .checkAndRegisterOperator("de.dfki.lt.hfc.operators.IsAtom");

    // store values in TupleStore, save integer-key in database
    int[] args = new int[5];

    args[0] = store.putObject((new XsdString("0")).toString());
    args[1] = store.putObject((new XsdInt(1)).toString());
    args[2] = store.putObject((new XsdBoolean(false)).toString());
    args[3] = store
        .putObject((new Uri("<rdf:type>", NamespaceManager.RDF)).toString());
    args[4] = store.putObject((new BlankNode("_blank")).toString());

    // do operation
    assertEquals("XsdString 0", FunctionalOperator.TRUE,
        fop.apply(new int[] { args[0] }));
    assertEquals("XsdInt 1", FunctionalOperator.TRUE,
        fop.apply(new int[] { args[1] }));
    assertEquals("XsdBoolean false", FunctionalOperator.TRUE,
        fop.apply(new int[] { args[2] }));
    assertEquals("Uri <rdf:type>", FunctionalOperator.FALSE,
        fop.apply(new int[] { args[3] }));
    assertEquals("BlankNode _:blank", FunctionalOperator.FALSE,
        fop.apply(new int[] { args[4] }));
  }

  @Test
  public void testIsBlankNode() {

    // create FunctionalOperator
    FunctionalOperator fop = (FunctionalOperator) store
        .checkAndRegisterOperator("de.dfki.lt.hfc.operators.IsBlankNode");

    // store values in TupleStore, save integer-key in database
    int[] args = new int[5];

    args[0] = store.putObject((new XsdString("0")).toString());
    args[1] = store.putObject((new XsdInt(1)).toString());
    args[2] = store.putObject((new XsdBoolean(false)).toString());
    args[3] = store
        .putObject((new Uri("<rdf:type>", NamespaceManager.RDF)).toString());
    args[4] = store.putObject((new BlankNode("_blank")).toString());

    // do operation
    assertEquals("XsdString 0", FunctionalOperator.FALSE,
        fop.apply(new int[] { args[0] }));
    assertEquals("XsdInt 1", FunctionalOperator.FALSE,
        fop.apply(new int[] { args[1] }));
    assertEquals("XsdBoolean false", FunctionalOperator.FALSE,
        fop.apply(new int[] { args[2] }));
    assertEquals("Uri <rdf:type>", FunctionalOperator.FALSE,
        fop.apply(new int[] { args[3] }));
    assertEquals("BlankNode _:blank", FunctionalOperator.TRUE,
        fop.apply(new int[] { args[4] }));
  }

  @Test
  public void testNoSubTypeOf() {

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
        .checkAndRegisterOperator("de.dfki.lt.hfc.operators.IsNotSubtypeOf");

    // do operation
    assertEquals("Valid Tuple: <rdf:type> <rdf:type> <rdf:Property>",
        FunctionalOperator.FALSE, fop.apply(validArgs));
    // assertEquals("Valid Tuple: <rdf:type> <rdf:type> <rdf:Property>",
    // FunctionalOperator.FALSE, fop.apply(invalidArgs));
  }

  @Test
  public void testIsTrue() {

    // create FunctionalOperator
    FunctionalOperator fop = (FunctionalOperator) store
        .checkAndRegisterOperator("de.dfki.lt.hfc.operators.IsTrue");

    // store values in TupleStore, save integer-key in database
    int[] args = new int[5];

    args[0] = store.putObject((new XsdString("0")).toString());
    args[1] = store.putObject((new XsdInt(1)).toString());
    args[2] = store.putObject((new XsdBoolean(true)).toString());
    args[3] = store
        .putObject((new Uri("<rdf:type>", NamespaceManager.RDF)).toString());
    args[4] = store.putObject((new BlankNode("_blank")).toString());

    // do operation
    assertEquals("XsdString 0", FunctionalOperator.FALSE,
        fop.apply(new int[] { args[0] }));
    assertEquals("XsdInt 1", FunctionalOperator.FALSE,
        fop.apply(new int[] { args[1] }));
    assertEquals("XsdBoolean false", FunctionalOperator.TRUE,
        fop.apply(new int[] { args[2] }));
    assertEquals("Uri <rdf:type>", FunctionalOperator.FALSE,
        fop.apply(new int[] { args[3] }));
    assertEquals("BlankNode _:blank", FunctionalOperator.FALSE,
        fop.apply(new int[] { args[4] }));
  }

  @Test
  public void testIsUri() {

    // create FunctionalOperator
    FunctionalOperator fop = (FunctionalOperator) store
        .checkAndRegisterOperator("de.dfki.lt.hfc.operators.IsUri");

    // store values in TupleStore, save integer-key in database
    int[] args = new int[5];

    args[0] = store.putObject((new XsdString("0")).toString());
    args[1] = store.putObject((new XsdInt(1)).toString());
    args[2] = store.putObject((new XsdBoolean(false)).toString());
    args[3] = store
        .putObject((new Uri("<rdf:type>", NamespaceManager.RDF)).toString());
    args[4] = store.putObject((new BlankNode("_blank")).toString());

    // do operation
    assertEquals("XsdString 0", FunctionalOperator.FALSE,
        fop.apply(new int[] { args[0] }));
    assertEquals("XsdInt 1", FunctionalOperator.FALSE,
        fop.apply(new int[] { args[1] }));
    assertEquals("XsdBoolean false", FunctionalOperator.FALSE,
        fop.apply(new int[] { args[2] }));
    assertEquals("Uri <rdf:type>", FunctionalOperator.TRUE,
        fop.apply(new int[] { args[3] }));
    assertEquals("BlankNode _:blank", FunctionalOperator.FALSE,
        fop.apply(new int[] { args[4] }));
  }

  @Test
  public void testHasLanguageTag() {
    // create FunctionalOperator
    FunctionalOperator fop = (FunctionalOperator) store
        .checkAndRegisterOperator("de.dfki.lt.hfc.operators.HasLanguageTag");

    // store values in TupleStore, save integer-key in database
    int[] args = new int[4];

    args[0] = store.putObject((new XsdString("as string")).toString());
    args[1] = store
        .putObject((new XsdString("\"a string\"^^xsd:string")).toString());
    args[2] = store.putObject((new XsdString("\"a string\"@en")).toString());
    args[3] = store.putObject((new XsdString("en")).toString());

    // do operation
    assertEquals(FunctionalOperator.FALSE,
        fop.apply(new int[] { args[0], args[3] }));
    assertEquals(FunctionalOperator.FALSE,
        fop.apply(new int[] { args[1], args[3] }));
    assertEquals(FunctionalOperator.TRUE,
        fop.apply(new int[] { args[2], args[3] }));
  }

  @Test
  public void testSContains() {

    // create FunctionalOperator
    FunctionalOperator fop = (FunctionalOperator) store
        .checkAndRegisterOperator("de.dfki.lt.hfc.operators.SContains");

    // store values in TupleStore, save integer-key in database
    int[] args = new int[8];

    args[0] = store.putObject((new XsdString("foo")).toString());
    args[1] = store.putObject((new XsdString("bar")).toString());
    args[2] = store.putObject((new XsdString("föö")).toString());
    args[3] = store.putObject((new XsdString("bär")).toString());

    args[4] = store.putObject((new XsdString("foobar")).toString());
    args[5] = store.putObject((new XsdString("foobarföö")).toString());
    args[6] = store.putObject((new XsdString("foobarfööbär")).toString());
    args[7] = store.putObject((new XsdString("xsd")).toString());

    // do operation
    assertEquals(FunctionalOperator.FALSE,
        fop.apply(new int[] { args[4], args[2] }));
    assertEquals(FunctionalOperator.TRUE,
        fop.apply(new int[] { args[4], args[0] }));
    assertEquals(FunctionalOperator.FALSE,
        fop.apply(new int[] { args[4], args[7] }));
  }

  @Test
  public void testUDateTimeLess() {
    // create FunctionalOperator
    FunctionalOperator fop = (FunctionalOperator) store
        .checkAndRegisterOperator("de.dfki.lt.hfc.operators.UDTLess");

    // store values in TupleStore, save integer-key in database
    // dates[0]: now
    int[] dates = new int[9];

    // diffs in ms
    dates[0] = store
        .putObject((new XsdUDateTime(2016, 8, 19, 22, 19, 33.123f)).toString());
    dates[1] = store
        .putObject((new XsdUDateTime(2016, 8, 19, 22, 19, 33.122f)).toString());
    // identical
    dates[2] = store
        .putObject((new XsdUDateTime(2048, 8, 19, 22, 19, 33.123f)).toString());
    dates[3] = store
        .putObject((new XsdUDateTime(2048, 8, 19, 22, 19, 33.123f)).toString());
    // underspecified (= -1 / -1.0f)
    dates[4] = store
        .putObject((new XsdUDateTime(-1, 8, 19, 22, 19, 33.123f)).toString());
    dates[5] = store
        .putObject((new XsdUDateTime(-1, 8, 19, 23, 19, 33.123f)).toString());
    dates[6] = store
        .putObject((new XsdUDateTime(2048, 8, -1, -1, -1, -1.0f)).toString());
    dates[7] = store
        .putObject((new XsdUDateTime(2048, 9, -1, -1, -1, -1.0f)).toString());
    // extremely underspecified
    dates[8] = store
        .putObject((new XsdUDateTime(-1, -1, -1, -1, -1, -1.0f)).toString());

    // do operation
    assertEquals(FunctionalOperator.FALSE,
        fop.apply(new int[] { dates[0], dates[1] }));
    assertEquals(FunctionalOperator.TRUE,
        fop.apply(new int[] { dates[1], dates[0] }));

    assertEquals(FunctionalOperator.TRUE,
        fop.apply(new int[] { dates[1], dates[2] }));
    assertEquals(FunctionalOperator.FALSE,
        fop.apply(new int[] { dates[2], dates[1] }));

    assertEquals(FunctionalOperator.FALSE,
        fop.apply(new int[] { dates[2], dates[3] }));
    assertEquals(FunctionalOperator.FALSE,
        fop.apply(new int[] { dates[3], dates[2] }));

    assertEquals(FunctionalOperator.FALSE,
        fop.apply(new int[] { dates[4], dates[5] }));
    assertEquals(FunctionalOperator.FALSE,
        fop.apply(new int[] { dates[5], dates[4] }));

    assertEquals(FunctionalOperator.TRUE,
        fop.apply(new int[] { dates[6], dates[7] }));
    assertEquals(FunctionalOperator.FALSE,
        fop.apply(new int[] { dates[7], dates[6] }));

    assertEquals(FunctionalOperator.FALSE,
        fop.apply(new int[] { dates[7], dates[8] }));
    assertEquals(FunctionalOperator.FALSE,
        fop.apply(new int[] { dates[8], dates[7] }));
  }
}
