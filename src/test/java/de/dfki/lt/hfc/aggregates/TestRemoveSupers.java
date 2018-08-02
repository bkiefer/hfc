package de.dfki.lt.hfc.aggregates;

import static de.dfki.lt.hfc.TestingUtils.*;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import de.dfki.lt.hfc.*;

public class TestRemoveSupers {
  static ForwardChainer fc;

  public static String getResource(String name) {
    return TestingUtils.getTestResource("LGetLatest", name);
  }

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
   */
  @BeforeClass
  public static void init() throws Exception {

    fc =	new ForwardChainer(Config.getDefaultConfig());

    // upload instance test files
    fc.uploadTuples(getResource("dialframe.nt"));

    // compute deductive closure
    fc.computeClosure();
  }

  @AfterClass
  public static void finish() {
    fc.shutdownNoExit();
  }

  @Test
  public void test1() throws QueryParseException {
    Query q = new Query(fc.tupleStore);
    String[][] expected = {
        { "<dafn:AcceptOffer>" },
        { "<dafn:DeclineOffer>" }
    };
    BindingTable bt = q.query(
        "SELECT ?c where ?c <rdfs:subClassOf> <dafn:Instruct> " +
        "AGGREGATE ?class = RemoveSupers ?c");
    checkResult(fc, bt, expected, "?class");
  }

  @Test
  public void test2() throws QueryParseException {
    Query q = new Query(fc.tupleStore);
    String[][] expected = {
        { "<dafn:Agreement>" },
        { "<dafn:Confirm>" },
        { "<dafn:Disconfirm>" },
        { "<dafn:Correction>" }
    };
    BindingTable bt = q.query(
        "SELECT ?c where ?c <rdfs:subClassOf> <dafn:Inform> " +
        "AGGREGATE ?class = RemoveSupers ?c");
    checkResult(fc, bt, expected, "?class");
  }

  @Test
  public void test3() throws QueryParseException {
    Query q = new Query(fc.tupleStore);
    String[][] expected = {
        { "<dafn:Agreement>" },
    };
    BindingTable bt = q.query(
        "SELECT ?c where ?c <rdfs:subClassOf> <dafn:Agreement> " +
        "AGGREGATE ?class = RemoveSupers ?c");
    checkResult(fc, bt, expected, "?class");
  }
}
