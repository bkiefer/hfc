package de.dfki.lt.hfc;

import static org.junit.Assert.*;

import static de.dfki.lt.hfc.TestUtils.*;

import org.junit.Test;

import java.io.*;


/**
 * NOTE: in order to perform the measurements properly, it is important to set the
 *       right flag in class TupleStore, viz., equivalenceClassReduction
 */
public class AddSameAsTest {

  // shortIsDefault no longer a static field in class Namespace
  public static ForwardChainer getFwChainer(boolean eqRed)
      throws FileNotFoundException, WrongFormatException, IOException {
    ForwardChainer fc = new ForwardChainer(16,
        false, false, eqRed, 3, 5, 100000, 500000,
        getTestResource(eqRed ? "default.eqred.nt" : "default.nt"),
        getTestResource(eqRed ? "default.eqred.rdl" : "default.rdl"),
        getTestResource("AddSameAs", "default.sameAs.test.ns"));
    fc.uploadTuples(getTestResource("ltworld.jena.nt"));
    fc.computeClosure();
    return fc;
  }

	@Test
	public void sameAsTestShortDefault() throws Exception {
    //boolean save = Namespace.shortIsDefault;  // NO LONGER A STATIC FIELD
    //Namespace.shortIsDefault = true;
    ForwardChainer fc =	getFwChainer(true);
		assertEquals(511049, fc.tupleStore.allTuples.size());
		fc.shutdownNoExit();
    //Namespace.shortIsDefault = save;
	}

  @Test
  public void sameAsTestLongDefault() throws Exception {
    //boolean save = Namespace.shortIsDefault;
    //Namespace.shortIsDefault = false;
    ForwardChainer fc = getFwChainer(true);
    // TODO: THIS IS WRONG: THE NUMBERS MUST BE EQUAL
    assertEquals(511049, fc.tupleStore.allTuples.size());
    fc.shutdownNoExit();

    //Namespace.shortIsDefault = save;
  }

  @Test
  public void sameAsTestNoEqRedShortDefault() throws Exception {
    //boolean save = Namespace.shortIsDefault;
    //Namespace.shortIsDefault = true;

    ForwardChainer fc = getFwChainer(false);
    assertEquals(548142, fc.tupleStore.allTuples.size());
    fc.shutdownNoExit();

    //Namespace.shortIsDefault = save;
  }

  @Test
  public void sameAsTestNoEqRedLongDefault() throws Exception {
    //boolean save = Namespace.shortIsDefault;
    //Namespace.shortIsDefault = false;

    ForwardChainer fc = getFwChainer(false);
    assertEquals(548142, fc.tupleStore.allTuples.size());
    fc.shutdownNoExit();

    //Namespace.shortIsDefault = save;
  }

}