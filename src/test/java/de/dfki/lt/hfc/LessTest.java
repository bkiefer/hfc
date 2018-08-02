package de.dfki.lt.hfc;

import static de.dfki.lt.hfc.TestingUtils.*;
import static org.junit.Assert.assertEquals;

import java.io.FileNotFoundException;
import java.io.IOException;

import de.dfki.lt.hfc.*;
import de.dfki.lt.hfc.types.*;

import org.junit.Test;

public class LessTest {

  @Test
  public void testLess()
      throws FileNotFoundException, IOException, WrongFormatException {

    // load Namespace
    Namespace namespace = Namespace.defaultNamespace();

    // create TupleStore
    TupleStore store =
        new TupleStore(false, true, true, 2, 5, 4, 2, namespace,
            getTestResource("default.nt"));
    int[] args = new int[5];

    // create FunctionalOperator
    FunctionalOperator fop =
        (FunctionalOperator)store.operatorRegistry
        .checkAndRegister("de.dfki.lt.hfc.operators.Less");

    args[0] = store.putObject((new XsdDouble(3.1).toString(true)));
    args[1] = store.putObject((new XsdLong(7l)).toString(true));
    args[2] = store.putObject((new XsdInt(6)).toString(true));
    args[3] = store.putObject((new XsdFloat(5.0f)).toString(true));

    args[4] = store.putObject((new XsdDecimal(4.0)).toString(true));

    // do operation
    assertEquals(FunctionalOperator.TRUE, fop.apply(new int[]{args[4], args[2]}));
    assertEquals(FunctionalOperator.FALSE, fop.apply(new int[]{args[4], args[0]}));
    assertEquals(FunctionalOperator.TRUE, fop.apply(new int[]{args[4], args[1]}));
    assertEquals(FunctionalOperator.FALSE, fop.apply(new int[]{args[3], args[4]}));
    assertEquals(FunctionalOperator.TRUE, fop.apply(new int[]{args[2], args[1]}));
    assertEquals(FunctionalOperator.FALSE, fop.apply(new int[]{args[1], args[0]}));

  }

  @Test
  public void testLessEq()
      throws FileNotFoundException, IOException, WrongFormatException {

    // load Namespace
    Namespace namespace = Namespace.defaultNamespace();

    // create TupleStore
    TupleStore store =
        new TupleStore(false, true, true, 2, 5, 4, 2, namespace,
            getTestResource("default.nt"));
    int[] args = new int[5];

    // create FunctionalOperator
    FunctionalOperator fop =
        (FunctionalOperator)store.operatorRegistry
        .checkAndRegister("de.dfki.lt.hfc.operators.LessEqual");

    args[0] = store.putObject((new XsdDouble(3.1).toString(true)));
    args[1] = store.putObject((new XsdLong(7l)).toString(true));
    args[2] = store.putObject((new XsdInt(6)).toString(true));
    args[3] = store.putObject((new XsdFloat(5.0f)).toString(true));

    args[4] = store.putObject((new XsdDecimal(4.0)).toString(true));

    // do operation
    assertEquals(FunctionalOperator.TRUE, fop.apply(new int[]{args[4], args[2]}));
    assertEquals(FunctionalOperator.FALSE, fop.apply(new int[]{args[4], args[0]}));
    assertEquals(FunctionalOperator.TRUE, fop.apply(new int[]{args[4], args[1]}));
    assertEquals(FunctionalOperator.FALSE, fop.apply(new int[]{args[3], args[4]}));
    assertEquals(FunctionalOperator.TRUE, fop.apply(new int[]{args[2], args[1]}));
    assertEquals(FunctionalOperator.FALSE, fop.apply(new int[]{args[1], args[0]}));
    assertEquals(FunctionalOperator.TRUE, fop.apply(new int[]{args[2], args[2]}));

  }

  @Test
  public void testGreater()
      throws FileNotFoundException, IOException, WrongFormatException {

    // load Namespace
    Namespace namespace = Namespace.defaultNamespace();

    // create TupleStore
    TupleStore store =
        new TupleStore(false, true, true, 2, 5, 4, 2, namespace,
            getTestResource("default.nt"));
    int[] args = new int[5];

    // create FunctionalOperator
    FunctionalOperator fop =
        (FunctionalOperator)store.operatorRegistry
        .checkAndRegister("de.dfki.lt.hfc.operators.Greater");

    args[0] = store.putObject((new XsdDouble(3.1).toString(true)));
    args[1] = store.putObject((new XsdLong(7l)).toString(true));
    args[2] = store.putObject((new XsdInt(6)).toString(true));
    args[3] = store.putObject((new XsdFloat(5.0f)).toString(true));

    args[4] = store.putObject((new XsdDecimal(4.0)).toString(true));

    // do operation
    assertEquals(FunctionalOperator.FALSE, fop.apply(new int[]{args[4], args[2]}));
    assertEquals(FunctionalOperator.TRUE, fop.apply(new int[]{args[4], args[0]}));
    assertEquals(FunctionalOperator.FALSE, fop.apply(new int[]{args[4], args[1]}));
    assertEquals(FunctionalOperator.TRUE, fop.apply(new int[]{args[3], args[4]}));
    assertEquals(FunctionalOperator.FALSE, fop.apply(new int[]{args[2], args[1]}));
    assertEquals(FunctionalOperator.TRUE, fop.apply(new int[]{args[1], args[0]}));

  }

  @Test
  public void testGreaterEq()
      throws FileNotFoundException, IOException, WrongFormatException {

    // load Namespace
    Namespace namespace = Namespace.defaultNamespace();

    // create TupleStore
    TupleStore store =
        new TupleStore(false, true, true, 2, 5, 4, 2, namespace,
            getTestResource("default.nt"));
    int[] args = new int[5];

    // create FunctionalOperator
    FunctionalOperator fop =
        (FunctionalOperator)store.operatorRegistry
        .checkAndRegister("de.dfki.lt.hfc.operators.GreaterEqual");

    args[0] = store.putObject((new XsdDouble(3.1).toString(true)));
    args[1] = store.putObject((new XsdLong(7l)).toString(true));
    args[2] = store.putObject((new XsdInt(6)).toString(true));
    args[3] = store.putObject((new XsdFloat(5.0f)).toString(true));

    args[4] = store.putObject((new XsdDecimal(4.0)).toString(true));

    // do operation
    assertEquals(FunctionalOperator.FALSE, fop.apply(new int[]{args[4], args[2]}));
    assertEquals(FunctionalOperator.TRUE, fop.apply(new int[]{args[4], args[0]}));
    assertEquals(FunctionalOperator.FALSE, fop.apply(new int[]{args[4], args[1]}));
    assertEquals(FunctionalOperator.TRUE, fop.apply(new int[]{args[3], args[4]}));
    assertEquals(FunctionalOperator.FALSE, fop.apply(new int[]{args[2], args[1]}));
    assertEquals(FunctionalOperator.TRUE, fop.apply(new int[]{args[1], args[0]}));
    assertEquals(FunctionalOperator.TRUE, fop.apply(new int[]{args[2], args[2]}));
  }

  @Test
  public void testEqual()
      throws FileNotFoundException, IOException, WrongFormatException {

    // load Namespace
    Namespace namespace = Namespace.defaultNamespace();

    // create TupleStore
    TupleStore store =
        new TupleStore(false, true, true, 2, 5, 4, 2, namespace,
            getTestResource("default.nt"));
    int[] args = new int[5];

    // create FunctionalOperator
    FunctionalOperator fop =
        (FunctionalOperator)store.operatorRegistry
        .checkAndRegister("de.dfki.lt.hfc.operators.GreaterEqual");

    args[0] = store.putObject((new XsdDouble(3.1).toString(true)));
    args[1] = store.putObject((new XsdLong(7l)).toString(true));
    args[2] = store.putObject((new XsdInt(6)).toString(true));
    args[3] = store.putObject((new XsdFloat(5.0f)).toString(true));

    args[4] = store.putObject((new XsdDecimal(4.0)).toString(true));

    // do operation
    assertEquals(FunctionalOperator.FALSE, fop.apply(new int[]{args[4], args[2]}));
    assertEquals(FunctionalOperator.TRUE, fop.apply(new int[]{args[4], args[4]}));
    assertEquals(FunctionalOperator.FALSE, fop.apply(new int[]{args[4], args[1]}));
    assertEquals(FunctionalOperator.TRUE, fop.apply(new int[]{args[3], args[3]}));
    assertEquals(FunctionalOperator.FALSE, fop.apply(new int[]{args[2], args[1]}));
    assertEquals(FunctionalOperator.TRUE, fop.apply(new int[]{args[0], args[0]}));
    assertEquals(FunctionalOperator.TRUE, fop.apply(new int[]{args[2], args[2]}));
  }
}
