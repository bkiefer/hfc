package de.dfki.lt.hfc.masstests;

import de.dfki.lt.hfc.Config;
import de.dfki.lt.hfc.Hfc;
import de.dfki.lt.hfc.WrongFormatException;
import static org.junit.Assert.*;

import static de.dfki.lt.hfc.TestingUtils.*;

import org.junit.Test;

import java.io.*;

/**
 * NOTE: in order to perform the measurements properly, it is important to set the
 *       right flag in class TupleStore, viz., equivalenceClassReduction
 */
public class AddSameAsTest {

  public static Hfc getFwChainer(boolean eqRed)
      throws FileNotFoundException, WrongFormatException, IOException {
    Hfc fc = new Hfc(Config.getInstance(
        getTestResource(eqRed ? "AddSameAs_eqRed.yml" : "AddSameAs.yml")));
    fc.uploadTuples(getTestResource("ltworld.jena.nt"));
    fc.computeClosure();
    return fc;
  }

  @Test
  public void sameAsTestShortDefault() throws Exception {
    Hfc fc = getFwChainer(true);
    assertEquals(511049, fc.size());
    fc.shutdownNoExit();
  }

  @Test
  public void sameAsTestLongDefault() throws Exception {
    Hfc fc = getFwChainer(true);
    assertEquals(511049, fc.size());
    fc.shutdownNoExit();
  }

  @Test
  public void sameAsTestNoEqRedShortDefault() throws Exception {
    Hfc fc = getFwChainer(false);
    assertEquals(548142, fc.size());
    fc.shutdownNoExit();
  }

  @Test
  public void sameAsTestNoEqRedLongDefault() throws Exception {
    Hfc fc = getFwChainer(false);
    assertEquals(548142, fc.size());
    fc.shutdownNoExit();
  }

}