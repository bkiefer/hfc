package de.dfki.lt.hfc;

import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static de.dfki.lt.hfc.TestUtils.*;

public class TestLtWorld {
  /**
   * for test purposes, I have (currently) switched off the equivalence class reduction
   *   TupleStore.equivalenceClassReduction == false
   * and have restricted ourselves to pure RDF triples:
   *   TupleStore.minNoOfArgs == 3
   *   TupleStore.maxNoOfArgs == 3
   * these settings are already defined below in the below constructor !
   *
   * note that temporal information is represented through the dafn:happens property, so
   * that we can restrict ourselved to the standard RDFS & OWL entailment rule set
   *
   * compile with
   *   javac -cp .:../jars/hfc.jar:../lib/trove-2.1.0.jar Template.java
   * run with
   *   java -server -cp .:../jars/hfc.jar:../lib/trove-2.1.0.jar -Xms800m -Xmx1200m Template
   *
   *
   * @author (C) Hans-Ulrich Krieger
   * @version Mon Jan  4 10:11:02 CET 2016
   * @throws IOException
   * @throws WrongFormatException
   * @throws FileNotFoundException
   */

  @Test
  public void testEqRed() throws QueryParseException, FileNotFoundException, WrongFormatException, IOException  {
    ForwardChainer fc =  new ForwardChainer(8,             // #cores
        false,                                             // verbose
        true,                                              // RDF Check
        true,                                              // EQ reduction enabled
        3,                                                 // min #args
        3,                                                 // max #args
        100000,                                            // #atoms
        1000000,                                           // #tuples
        getResource("default.eqred.nt"),                   // tuple file
        getResource("default.eqred.rdl"),                  // rule file
        getResource("default.ns")                          // namespace file
        );
    fc.uploadTuples(getResource("ltworld.jena.nt"));
    // compute deductive closure
    fc.computeClosure();
    assertEquals(511046, fc.tupleStore.getAllTuples().size());
    fc.computeClosure();
    assertEquals(511046, fc.tupleStore.getAllTuples().size());
    fc.shutdownNoExit();
  }


  @Test
  public void testNoEqRed() throws QueryParseException, FileNotFoundException, WrongFormatException, IOException  {
    ForwardChainer fc =  new ForwardChainer(8,             // #cores
        false,                                             // verbose
        true,                                              // RDF Check
        false,                                              // EQ reduction enabled
        3,                                                 // min #args
        3,                                                 // max #args
        100000,                                            // #atoms
        1000000,                                           // #tuples
        getResource("default.eqred.nt"),                   // tuple file
        getResource("default.eqred.rdl"),                  // rule file
        getResource("default.ns")                          // namespace file
        );
    fc.uploadTuples(getResource("ltworld.jena.nt"));
    // compute deductive closure
    fc.computeClosure();
    assertEquals(547786, fc.tupleStore.getAllTuples().size());
    fc.computeClosure();
    assertEquals(547786, fc.tupleStore.getAllTuples().size());
    fc.shutdownNoExit();
  }

}
