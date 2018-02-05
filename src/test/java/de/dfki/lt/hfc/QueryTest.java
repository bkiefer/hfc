package de.dfki.lt.hfc;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static de.dfki.lt.hfc.TestUtils.checkResult;
import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Christian Willms - Date: 14.09.17 10:25.
 * @version 14.09.17
 */
public class QueryTest {

  static ForwardChainer fc;

  private static String getResource(String name) {
    return TestUtils.getTestResource("LGetLatestValues", name);
  }

  @BeforeEach
  void setUp() throws IOException, WrongFormatException {
    fc =  new ForwardChainer(4,                                                    // #cores
        true,                                                    // verbose
        false,                                                   // RDF Check
        false,                                                 // EQ reduction disabled
        3,                                                    // min #args
        4,                                                    // max #args
        100000,                                                 // #atoms
        500000,                                                 // #tuples
        getResource("default.nt"),                                  // tuple file
        getResource("default.rdl"),                                 // rule file
        getResource("default.ns")                                   // index file
    );
    fc.uploadTuples(getResource("test.child.labvalues.nt"));
  }

  @AfterEach
  void tearDown() {
    fc.shutdownNoExit();
  }

  @Test
  public void testQuery() {
    //test constructor Query(TupleStore tupleStore)
    TupleStore tupleStore = new TupleStore(2, 5);
    Query query = new Query(tupleStore);
    assertNotNull(query);
  }

  @Test
  void query_Invalid_Empty() {
    Query query = new Query(fc.tupleStore);
    assertThrows(QueryParseException.class, () -> query.query(""));
  }

  @Test
  void query_Invalid_SELECT() throws QueryParseException {
    Query query = new Query(fc.tupleStore);
      assertThrows(QueryParseException.class, () -> query.query("SELECT "));
      assertThrows(QueryParseException.class, () -> query.query("SELECT DISTINCT"));
    assertThrows(QueryParseException.class, () -> query.query("?o WHERE ?s <rdf:type> ?o"));
    assertThrows(QueryParseException.class, () -> query.query("DISTINCT ?o WHERE ?s <rdf:type> ?o"));
      assertThrows(QueryParseException.class, () -> query.query("SELECT FILTER ?s <rdf:type> ?o"));
      assertThrows(QueryParseException.class, () -> query.query("SELECT ?s <rdf:type> ?o"));
      assertThrows(QueryParseException.class, () -> query.query("SELECT *"));
    assertThrows(QueryParseException.class, () -> query.query("SELECT WHERE ?s <rdf:type> ?o"));
  }

  @Test
  void query_Invalid_Variable(){
    Query query = new Query(fc.tupleStore);
    assertThrows(QueryParseException.class, () -> query.query("SELECT ? WHERE ?s <rdf:type> ?o"));
    assertThrows(QueryParseException.class, () -> query.query("SELECT !o WHERE ?s <rdf:type> ?o"));
    assertThrows(QueryParseException.class, () -> query.query("SELECT _ WHERE ?s <rdf:type> ?o"));
    assertThrows(QueryParseException.class, () -> query.query("SELECT ?rel WHERE ?s <rdf:type> ?o"));
    assertThrows(QueryParseException.class, () -> query.query("SELECT * ?s WHERE ?s <rdf:type> ?o"));
  }

    @Test
    public void testSingleIntervalErrors(){
        TupleStore tupleStore = new TupleStore(2, 5);
        Query query = new Query(tupleStore);
        // inputs which should  lead to QueryParseExeptions
        assertThrows(QueryParseException.class, () -> query.query("SELECT DISTINCT ?p WHERE ?s ?p ?o [\"(1,2)\"^^<xsd:2DPoint>, \"(1,2)\"^^<xsd:2DPoint>]"));
        assertThrows(QueryParseException.class, () ->  query.query("SELECT DISTINCT ?p WHERE ?s ?p ?o [\"1\"^^<xsd:long>]"));
        assertThrows(QueryParseException.class, () ->  query.query("SELECT DISTINCT ?p WHERE ?s ?p ?o [ ]"));
        assertThrows(QueryParseException.class, () ->  query.query("SELECT DISTINCT ?p WHERE ?s ?p ?o [ , ]"));
        assertThrows(QueryParseException.class, () ->  query.query("SELECT DISTINCT ?p WHERE ?s ?p ?o [\"1\"^^<xsd:long>, \"1\"^^<xsd:long>, \"1\"^^<xsd:long>]"));
        assertThrows(QueryParseException.class, () ->  query.query("SELECT DISTINCT ?p WHERE ?s ?p ?o [\"1\"^^<xsd:long>, \"1\"^^<xsd:long>}"));

    }

    @Test
    public void testIntervalRelationsErrors(){
        TupleStore tupleStore = new TupleStore(2, 5);
        Query query = new Query(tupleStore);

        assertThrows(QueryParseException.class, () ->  query.query("SELECT DISTINCT ?p WHERE ?s ?p ?o FOO  \"1\"^^<xsd:date> , \"2\"^^<xsd:date>"));
        assertThrows(QueryParseException.class, () ->  query.query("SELECT DISTINCT ?p WHERE ?s ?p ?o F"));
        assertThrows(QueryParseException.class, () ->  query.query("SELECT DISTINCT ?p WHERE ?s ?p ?o F  \"1\"^^<xsd:long>  \"2\"^^<xsd:long>  \"1\"^^<xsd:long>"));

    }

  @Test
  void query_Invalid_Filter(){
    Query query = new Query(fc.tupleStore);
    assertThrows(QueryParseException.class, () -> query.query("SELECT ?s WHERE ?s <rdf:type> ?o FILTER ?p != ?o"));
    assertThrows(QueryParseException.class, () -> query.query("SELECT ?s WHERE ?s <rdf:type> ?o FILTER Less ?o ?o"));
    assertThrows(QueryParseException.class, () -> query.query("SELECT ?s WHERE ?s <rdf:type> ?o FILTER CountDistinct ?o"));
  }

  @Test
  void query_Invalid_Aggregate(){
    Query query = new Query(fc.tupleStore);
    assertThrows(QueryParseException.class, () -> query.query("SELECT ?s WHERE ?s <rdf:type> ?o AGGREGAT ?p != ?o"));
    assertThrows(QueryParseException.class, () -> query.query("SELECT ?s WHERE ?s <rdf:type> ?o AGGREGATE ?number = CountDistinct ?o"));
    assertThrows(QueryParseException.class, () -> query.query("SELECT ?s WHERE ?s <rdf:type> ?o AGGREGATE ?test = ILess ?s ?s"));
    assertThrows(QueryParseException.class, () -> query.query("SELECT ?s WHERE ?s <rdf:type> ?o AGGREGATE ?test ILess ?s ?s"));
  }


  /**
   * expected bindingTable
   * ==================
   *| ?s             |
   *==================
   *| <pal:labval22> |
   *| <pal:labval22> |
   *| <pal:labval33> |
   *| <pal:labval33> |
   *------------------
   * @throws QueryParseException
   */
  @Test
  void query_SELECT_WHERE() throws QueryParseException {
    String[][] expected = {{"<pal:labval22>"},{"<pal:labval22>"}, {"<pal:labval33>"},
{"<pal:labval33>"}};
    Query query = new Query(fc.tupleStore);
    BindingTable bt = query.query("SELECT ?s WHERE ?s <dom:bsl> ?o ?t");
    checkResult(fc, bt, expected, "?s");
  }

  @Test
  void query_SELECT_WHERE_EMPTYTABLE() throws QueryParseException {
      Query query = new Query(fc.tupleStore);
      BindingTable bt = query.query("SELECT ?s WHERE ?s <dom:bse> ?o ?t");
      assertTrue(bt.isEmpty());
      query = new Query(fc.tupleStore);
      bt = query.query("SELECT ?s WHERE ?s \"0\"^^<xsd:long> ?o ?t");
      assertTrue(bt.isEmpty());
  }

    @Test
    void query_SELECTALL_WHERE() throws QueryParseException {
        String[][] expected = {{"<pal:labval22>"},{"<pal:labval22>"}, {"<pal:labval33>"},
                {"<pal:labval33>"}};
        Query query = new Query(fc.tupleStore);
        BindingTable bt = query.query("SELECTALL ?s WHERE ?s <dom:bsl> ?o ?t");
        checkResult(fc, bt, expected, "?s");
    }

  /**
   * expected bindingTable
   * ==================
   *| ?s             |
   *==================
   *| <pal:labval22> |
   *| <pal:labval33> |
   *------------------
   * @throws QueryParseException
   */
  @Test
  void query_SELECT_DISTINCT_WHERE() throws QueryParseException {
    String[][] expected = {{"<pal:labval22>"},{"<pal:labval33>"}};
    Query query = new Query(fc.tupleStore);
    BindingTable bt = query.query("SELECT DISTINCT ?s WHERE ?s <dom:bsl> ?o ?t");
    checkResult(fc, bt, expected, "?s");
    expected = new String[][]{{"<pal:labval22>"},{"<pal:labval33>"}};
    query = new Query(fc.tupleStore);
     bt = query.query("SELECT DISTINCT ?s WHERE ?s <dom:bsl> ?o ?t & ?s <dom:weight> ?o1 ?t1");
     checkResult(fc, bt, expected, "?s");

  }

  @Test
  void query_SELECT_WHERE_FILTER() throws QueryParseException {
    String[][] expected = {{"<pal:labval22>"}};
    Query query = new Query(fc.tupleStore);
    BindingTable bt = query.query("SELECT DISTINCT ?s WHERE ?s <dom:bsl> ?o ?t FILTER ?s != <pal:labval33>");
    checkResult(fc, bt, expected, "?s");
      expected = new String[][]{{"<pal:labval22>"}};
      query = new Query(fc.tupleStore);
      bt = query.query("SELECT DISTINCT ?s WHERE ?s <dom:bsl> ?o ?t FILTER ?s != <pal:labval33> & ?t != \"5577\"^^<xsd:long>");
      checkResult(fc, bt, expected, "?s");
  }

  @Test
  void query_SELECT_WHERE_AGGREGATE() throws QueryParseException {
    String[][] expected = {{"\"2\"^^<xsd:int>"}};
    Query query = new Query(fc.tupleStore);
    BindingTable bt = query.query("SELECT ?s WHERE ?s <dom:bsl> ?o ?t AGGREGATE ?number = CountDistinct ?s") ;
    checkResult(fc, bt, expected, "?number");
  }

  @Test
  void query_SELECT_WHERE_FILTER_AGGREGATE() throws QueryParseException {
    String[][] expected = {{"\"1\"^^<xsd:int>"}};
    Query query = new Query(fc.tupleStore);
    BindingTable bt = query.query("SELECT ?s WHERE ?s <dom:bsl> ?o ?t FILTER ?s != <pal:labval33> AGGREGATE ?number = CountDistinct ?s") ;
    checkResult(fc, bt, expected, "?number");
    //?subject = Identity ?p
       expected = new String[][]{{"\"1\"^^<xsd:int>", "\"1\"^^<xsd:int>"}};
      query = new Query(fc.tupleStore);
      bt = query.query("SELECT ?s WHERE ?s <dom:bsl> ?o ?t FILTER ?s != <pal:labval33> AGGREGATE ?number = CountDistinct ?s & ?number2 = CountDistinct <pal:labval33>") ;
      System.out.println(bt);
      checkResult(fc, bt, expected, "?number", "?subject");
  }

  @Test
  void isPredicate() {
    //test static boolean isPredicate(String literal)
    //not a blank node, a URI, a variable, nor an XSD atom
    assertTrue(Query.isPredicate("gdgsdf"));
    //is a blank node
    assertFalse(Query.isPredicate("_ddgjdfkgj"));
    //is a URI
    assertFalse(Query.isPredicate("<gkfjg>"));
    //is an XSD atom
    assertFalse(Query.isPredicate("\"hello"));
    //is a varibale
    assertFalse(Query.isPredicate("?dgjdfgj"));
  }

}