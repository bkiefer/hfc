package de.dfki.lt.hfc.aggregates;

import static de.dfki.lt.hfc.TestingUtils.*;
import static org.junit.Assert.assertEquals;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import de.dfki.lt.hfc.*;
import static org.junit.Assert.assertEquals;

public class TestLGetLatest {
  static Hfc fc;

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

    fc = new Hfc(Config.getInstance(getTestResource("test.yml")));

    // upload instance test files
    fc.uploadTuples(getResource("time.nt"));
    fc.uploadTuples(getResource("upper.nt"));
    fc.uploadTuples(getResource("domain.nt"));
    fc.uploadTuples(getResource("dialframe.nt"));
    fc.uploadTuples(getResource("test.data.nt"));

    // compute deductive closure
    fc.computeClosure();
  }

  @AfterClass
  public static void finish() {
    fc.shutdownNoExit();
  }

  @Test
  public void test1() throws QueryParseException {
    Query q = new Query(fc._tupleStore);
    String[][] expected = {
        { "<hst:w1>", "\"Harry\"^^<xsd:string>" },
        { "<hst:m>", "\"Barry\"^^<xsd:string>" },
        { "<hst:w2>", "\"Larry\"^^<xsd:string>" },
    };
    BindingTable bt = q.query("SELECT * WHERE ?e <rdf:type> <dom:Entity> & ?e <dom:name> ?n");

    // This printed the following:
    /*
=================================================
| ?e                    | ?n                    |
=================================================
| <hst:w1>              | "Harry"^^<xsd:string> |
| <hst:m>               | "Barry"^^<xsd:string> |
| <hst:w2>              | "Larry"^^<xsd:string> |
-------------------------------------------------
     */
    checkResult(fc, bt, expected, "?e", "?n");
  }

  @Test
  public void test2() throws QueryParseException, BindingTableIteratorException {
    String[][] expected = {
        { "<hst:da2>", "<hst:da5>" },
        { "<hst:da1>", "<hst:da6>" },
        { "<hst:da1>", "<hst:da3>" },
        { "<hst:da4>", "<hst:da8>" },
        { "<hst:da2>", "<hst:da7>" },
        { "<hst:da1>", "<hst:da2>" },
        { "<hst:da1>", "<hst:da7>" },
        { "<hst:da7>", "<hst:da8>" },
        { "<hst:da2>", "<hst:da8>" },
        { "<hst:da4>", "<hst:da5>" },
        { "<hst:da5>", "<hst:da7>" },
        { "<hst:da3>", "<hst:da6>" },
        { "<hst:da1>", "<hst:da4>" },
        { "<hst:da1>", "<hst:da5>" },
        { "<hst:da4>", "<hst:da7>" },
        { "<hst:da5>", "<hst:da8>" },
        { "<hst:da2>", "<hst:da4>" },
        { "<hst:da1>", "<hst:da8>" },
    };
    Query q = new Query(fc._tupleStore);
    BindingTable bt = q.query("SELECT * WHERE ?d1 <rdf:type> <dafn:DialogueAct> & ?d2 <rdf:type> <dafn:DialogueAct> & ?d1 <dafn:follows> ?d2 FILTER ?d1 != ?d2");
    //System.out.println(bt);
    // TODO call checkresult here, same for the other tests, and remove the
    // println if no longer needed
    checkResult(fc, bt, expected, "?d2", "?d1");

  }

  @Test
  public void test2a() throws QueryParseException {
    String[][] expected = {
        { "<hst:da4>", "\"533\"^^<xsd:long>" },
        { "<hst:da6>", "\"686\"^^<xsd:long>" },
        { "<hst:da1>", "\"468\"^^<xsd:long>" },
        { "<hst:da8>", "\"755\"^^<xsd:long>" },
        { "<hst:da2>", "\"489\"^^<xsd:long>" },
        { "<hst:da5>", "\"548\"^^<xsd:long>" },
        { "<hst:da3>", "\"503\"^^<xsd:long>" },
        { "<hst:da7>", "\"731\"^^<xsd:long>" },
    };

    Query q = new Query(fc._tupleStore);
    BindingTable bt = q.query("SELECT * WHERE ?d <rdf:type> <dafn:DialogueAct> & ?d <dafn:happens> ?t");
  }

  @Test
  public void test3() throws QueryParseException {
    String[][] expected = {
        { "<hst:da3>" }
    };
    Query q = new Query(fc._tupleStore);
    // 503 is related to DA da3
    BindingTable bt = q.query("SELECT ?d WHERE ?d <rdf:type> <dafn:DialogueAct> & ?d <dafn:happens> \"503\"^^<xsd:long>");
    checkResult(fc, bt, expected, "?d");
  }

  @Test
  public void test4() throws QueryParseException {
    // there is _no_ DA with time = "333" -- we formerly returned the null value to distinguish this case from an empty table with _known_ constants !
    // now an empty table is returned for easier API use

    // prints:   unknown constant in WHERE clause: "333"^^<xsd:long>
    Query q = new Query(fc._tupleStore);
    BindingTable bt = q.query("SELECT ?d WHERE ?d <rdf:type> <dafn:DialogueAct> & ?d <dafn:happens> \"333\"^^<xsd:long>");
    assertEquals(0, bt.size());
  }

  @Test
  public void test5() throws QueryParseException {
    String[][] expected = {
        { "\"755\"^^<xsd:long>" }
    };
    // would also like to return the corresponding DA, but this needs a special custom aggregate
    Query q = new Query(fc._tupleStore);
    BindingTable bt = q.query("SELECT ?t WHERE ?d <rdf:type> <dafn:DialogueAct> & ?d <dafn:happens> ?t AGGREGATE ?latest = LMax ?t");
    checkResult(fc, bt, expected, "?latest");
  }

  @Test
  public void test6() throws QueryParseException {
    String[][] expected = {
        { "<hst:da8>", "\"755\"^^<xsd:long>" },
        { "<hst:da7>", "\"731\"^^<xsd:long>" },
        { "<hst:da6>", "\"686\"^^<xsd:long>" },
        { "<hst:da5>", "\"548\"^^<xsd:long>" },
    };

    // "548" = sinceWhen; is known to the tuple store
    Query q = new Query(fc._tupleStore);
    BindingTable bt = q.query("SELECT ?d ?t WHERE ?d <rdf:type> <dafn:DialogueAct> & ?d <dafn:happens> ?t FILTER LGreaterEqual ?t \"548\"^^<xsd:long>");
    checkResult(fc, bt, expected, "?d", "?t");
  }

  @Test
  public void test7() throws QueryParseException {
    String[][] expected = {
        { "<hst:da7>", "\"731\"^^<xsd:long>" },
        { "<hst:da6>", "\"686\"^^<xsd:long>" },
        { "<hst:da8>", "\"755\"^^<xsd:long>" },
    };
    // "650" = sinceWhen; note: there is _no_ "650" so far in the tuple store
    // prints: unknown constant in FILTER predicate: "650"^^<xsd:long>
    Query q = new Query(fc._tupleStore);
    BindingTable bt = q.query("SELECT * WHERE ?d <rdf:type> <dafn:DialogueAct> & ?d <dafn:happens> ?t FILTER LGreaterEqual ?t \"650\"^^<xsd:long>");
    checkResult(fc, bt, expected, "?d", "?t");
  }

  @Test
  public void test8() throws QueryParseException {
    // "1000" = sinceWhen; note: there is _no_ "1000" so far in the tuple store
    // prints: unknown constant in FILTER predicate: "1000\"^^<xsd:long>
    Query q = new Query(fc._tupleStore);
    BindingTable bt = q.query("SELECT ?d ?t WHERE ?d <rdf:type> <dafn:DialogueAct> & ?d <dafn:happens> ?t FILTER LGreaterEqual ?t \"1000\"^^<xsd:long>");
    assertEquals(0, bt.size());
  }

  @Test
  public void test9() throws QueryParseException {
    String[][] expected = {
        { "\"755\"^^<xsd:long>" }
    };
   // "548" = sinceWhen
    Query q = new Query(fc._tupleStore);
    BindingTable bt = q.query("SELECT ?t WHERE ?d <rdf:type> <dafn:DialogueAct> & ?d <dafn:happens> ?t FILTER LGreaterEqual ?t \"548\"^^<xsd:long>  AGGREGATE ?latest = LMax ?t");
    checkResult(fc, bt, expected, "?latest");
  }

  @Test
  public void test10() throws QueryParseException {
    String[][] expected = {
        { "\"755\"^^<xsd:long>" }
    };
    // "650" = sinceWhen
    Query q = new Query(fc._tupleStore);
    BindingTable bt = q.query("SELECT ?t WHERE ?d <rdf:type> <dafn:DialogueAct> & ?d <dafn:happens> ?t FILTER LGreaterEqual ?t \"650\"^^<xsd:long>  AGGREGATE ?latest = LMax ?t");
    checkResult(fc, bt, expected, "?latest");
  }

  @Test
  public void test11() throws QueryParseException {
    String[][] expected = {
    };
    // "1000" = sinceWhen
    Query q = new Query(fc._tupleStore);
    BindingTable bt = q.query("SELECT ?t WHERE ?d <rdf:type> <dafn:DialogueAct> & ?d <dafn:happens> ?t FILTER LGreaterEqual ?t \"1000\"^^<xsd:long>  AGGREGATE ?latest = LMax ?t");
    assertEquals(0, bt.size());
  }

  @Test
  public void test12() throws QueryParseException {
    String[][] expected = {
        { "\"731\"^^<xsd:long>" }
    };
    // explicitly exclude the latest candidate "755"
    Query q = new Query(fc._tupleStore);
    BindingTable bt = q.query("SELECT ?t WHERE ?d <rdf:type> <dafn:DialogueAct> & ?d <dafn:happens> ?t FILTER LGreaterEqual ?t \"650\"^^<xsd:long> & ?t != \"755\"^^<xsd:long>  AGGREGATE ?latest = LMax ?t");
    checkResult(fc, bt, expected, "?latest");
  }

  @Test
  public void test13() throws QueryParseException {
    String[][] expected = {
        { "\"755\"^^<xsd:long>" }
    };
    // exclude non-existing candidate "1000"
    Query q = new Query(fc._tupleStore);
    BindingTable bt = q.query("SELECT ?t WHERE ?d <rdf:type> <dafn:DialogueAct> & ?d <dafn:happens> ?t FILTER LGreaterEqual ?t \"650\"^^<xsd:long> & ?t != \"1000\"^^<xsd:long>  AGGREGATE ?latest = LMax ?t");
    checkResult(fc, bt, expected, "?latest");
  }

  // general form of the aggregate call: ?arg1' ... ?argN' = LGetLatest ?arg1 ... ?argN ?time ?limit

  @Test
  public void test14() throws QueryParseException {
    String[][] expected = {
        { "<hst:da8>", "\"755\"^^<xsd:long>" },
        { "<hst:da7>", "\"731\"^^<xsd:long>" },
        { "<hst:da6>", "\"686\"^^<xsd:long>" },
        { "<hst:da5>", "\"548\"^^<xsd:long>" },
    };
    // double-valued aggregate
    /* prints
    unknown constant in FILTER predicate: "540\"^^<xsd:long>
    unknown constant in AGGREGATE function: "5\"^^<xsd:int>
    */
    Query q = new Query(fc._tupleStore);
    BindingTable bt = q.query("SELECT ?da ?t WHERE ?da <rdf:type> <dafn:DialogueAct> & ?da <dafn:happens> ?t FILTER LGreaterEqual ?t \"540\"^^<xsd:long> AGGREGATE ?dialact ?time = LGetLatest ?da ?t ?t \"5\"^^<xsd:int>");
    checkResult(fc, bt, expected, "?dialact", "?time");
  }

  @Test
  public void test15() throws QueryParseException {
    String[][] expected = {
        { "<hst:da8>", "\"755\"^^<xsd:long>" },
        { "<hst:da7>", "\"731\"^^<xsd:long>" },
        { "<hst:da6>", "\"686\"^^<xsd:long>" },
        { "<hst:da5>", "\"548\"^^<xsd:long>" },
    };
    Query q = new Query(fc._tupleStore);
    BindingTable bt = q.query("SELECT ?da ?t WHERE ?da <rdf:type> <dafn:DialogueAct> & ?da <dafn:happens> ?t FILTER LGreaterEqual ?t \"540\"^^<xsd:long> AGGREGATE ?dialact ?time = LGetLatest ?da ?t ?t \"4\"^^<xsd:int>");
    checkResult(fc, bt, expected, "?dialact", "?time");
    //System.out.println(bt);
  }

  @Test
  public void test16() throws QueryParseException {
    String[][] expected = {
        { "<hst:da8>", "\"755\"^^<xsd:long>" },
        { "<hst:da7>", "\"731\"^^<xsd:long>" },
        { "<hst:da6>", "\"686\"^^<xsd:long>" },
    };
   Query q = new Query(fc._tupleStore);
    BindingTable bt = q.query("SELECT ?da ?t WHERE ?da <rdf:type> <dafn:DialogueAct> & ?da <dafn:happens> ?t FILTER LGreaterEqual ?t \"540\"^^<xsd:long> AGGREGATE ?dialact ?time = LGetLatest ?da ?t ?t \"3\"^^<xsd:int>");
    checkResult(fc, bt, expected, "?dialact", "?time");
    //System.out.println(bt);
  }

  @Test
  public void test17() throws QueryParseException {
    String[][] expected = {
        { "<hst:da8>", "\"755\"^^<xsd:long>" },
        { "<hst:da7>", "\"731\"^^<xsd:long>" },
    };
    Query q = new Query(fc._tupleStore);
    BindingTable bt = q.query("SELECT ?da ?t WHERE ?da <rdf:type> <dafn:DialogueAct> & ?da <dafn:happens> ?t FILTER LGreaterEqual ?t \"540\"^^<xsd:long> AGGREGATE ?dialact ?time = LGetLatest ?da ?t ?t \"2\"^^<xsd:int>");
    checkResult(fc, bt, expected, "?dialact", "?time");
    //System.out.println(bt);
  }

  @Test
  public void test18() throws QueryParseException {
    String[][] expected = {
        { "<hst:da8>", "\"755\"^^<xsd:long>" },
    };
    Query q = new Query(fc._tupleStore);
    BindingTable bt = q.query("SELECT ?da ?t WHERE ?da <rdf:type> <dafn:DialogueAct> & ?da <dafn:happens> ?t FILTER LGreaterEqual ?t \"540\"^^<xsd:long> AGGREGATE ?dialact ?time = LGetLatest ?da ?t ?t \"1\"^^<xsd:int>");
    checkResult(fc, bt, expected, "?dialact", "?time");
    //System.out.println(bt);
  }

  @Test
  public void test19() throws QueryParseException {
    Query q = new Query(fc._tupleStore);
    BindingTable bt = q.query("SELECT ?da ?t WHERE ?da <rdf:type> <dafn:DialogueAct> & ?da <dafn:happens> ?t FILTER LGreaterEqual ?t \"540\"^^<xsd:long> AGGREGATE ?dialact ?time = LGetLatest ?da ?t ?t \"0\"^^<xsd:int>");
    assertEquals(0, bt.size());
  }

  @Test
  public void test20() throws QueryParseException {
   Query q = new Query(fc._tupleStore);
    BindingTable bt = q.query("SELECT ?da ?t WHERE ?da <rdf:type> <dafn:DialogueAct> & ?da <dafn:happens> ?t FILTER LGreaterEqual ?t \"1000\"^^<xsd:long> AGGREGATE ?dialact ?time = LGetLatest ?da ?t ?t \"3\"^^<xsd:int>");
    assertEquals(0, bt.size());
  }

  @Test
  public void test21() throws QueryParseException {
    String[][] expected = {{ "<hst:da8>" },
        { "<hst:da7>" },
        { "<hst:da6>" },
    };
    // single-valued aggregate
    Query q = new Query(fc._tupleStore);
    BindingTable bt = q.query("SELECT ?da ?t WHERE ?da <rdf:type> <dafn:DialogueAct> & ?da <dafn:happens> ?t FILTER LGreaterEqual ?t \"548\"^^<xsd:long> AGGREGATE ?dialact = LGetLatest ?da ?t \"3\"^^<xsd:int>");
    checkResult(fc, bt, expected, "?dialact");
    //System.out.println(bt);
  }

  @Test
  public void test22() throws QueryParseException {
    Query q = new Query(fc._tupleStore);
    BindingTable bt = q.query("SELECT ?da ?t WHERE ?da <rdf:type> <dafn:DialogueAct> & ?da <dafn:happens> ?t FILTER LGreaterEqual ?t \"1000\"^^<xsd:long> AGGREGATE ?dialact = LGetLatest ?da ?t \"3\"^^<xsd:int>");
    assertEquals(0, bt.size());
  }

  // a three-valued returner
  @Test
  public void test23() throws QueryParseException {
    String[][] expected = {
        { "<hst:da8>", "\"755\"^^<xsd:long>", "\"755\"^^<xsd:long>" },
        { "<hst:da7>", "\"731\"^^<xsd:long>", "\"731\"^^<xsd:long>" },
        { "<hst:da6>", "\"686\"^^<xsd:long>", "\"686\"^^<xsd:long>" },
    };

    Query q = new Query(fc._tupleStore);
    BindingTable bt = q.query("SELECT ?da ?t WHERE ?da <rdf:type> <dafn:DialogueAct> & ?da <dafn:happens> ?t FILTER LGreaterEqual ?t \"548\"^^<xsd:long> AGGREGATE ?dialact ?begin ?end = LGetLatest ?da ?t ?t ?t  \"3\"^^<xsd:int>");
    checkResult(fc, bt, expected, "?dialact", "?begin", "?end");
  }

  // This tests separates LGetLatest2 from LGetLatest
  @Test
  public void test24() throws
  QueryParseException, FileNotFoundException, WrongFormatException, IOException {
    String[][] expected = {
        { "<hst:da8>", "\"755\"^^<xsd:long>", "\"755\"^^<xsd:long>" },
        { "<hst:da7>", "\"731\"^^<xsd:long>", "\"731\"^^<xsd:long>" },
        { "<hst:da88>", "\"755\"^^<xsd:long>", "\"755\"^^<xsd:long>" },
    };

    String[][] newTuples = {
        {"<hst:da88>", "<rdf:type>", "<dafn:DialogueAct>"},
        {"<hst:da88>", "<dafn:happens>", "\"755\"^^<xsd:long>"},
        {"<hst:da66>", "<rdf:type>", "<dafn:DialogueAct>"},
        {"<hst:da66>", "<dafn:happens>", "\"686\"^^<xsd:long>"},
    };
    Hfc localfc =	new Hfc(Config.getInstance(getTestResource("test.yml")));

    // upload instance test files
    localfc.uploadTuples(getResource("time.nt"));
    localfc.uploadTuples(getResource("upper.nt"));
    localfc.uploadTuples(getResource("domain.nt"));
    localfc.uploadTuples(getResource("dialframe.nt"));
    localfc.uploadTuples(getResource("test.data.nt"));

    // compute deductive closure
    localfc.computeClosure();
    for (String[] t : newTuples) {
      localfc._tupleStore.addTuple(t);
    }
    Query q = new Query(localfc._tupleStore);
    BindingTable bt = q.query("SELECT ?da ?t WHERE ?da <rdf:type> <dafn:DialogueAct>"
        + " & ?da <dafn:happens> ?t FILTER LGreaterEqual ?t \"548\"^^<xsd:long>"
        + " AGGREGATE ?dialact ?begin ?end = LGetLatest ?da ?t ?t ?t  \"3\"^^<xsd:int>");
    checkResult(localfc, bt, expected, "?dialact", "?begin", "?end");
    localfc.shutdownNoExit();
  }
}
