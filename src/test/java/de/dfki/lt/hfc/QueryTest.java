package de.dfki.lt.hfc;


import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.io.IOException;

import static de.dfki.lt.hfc.TestingUtils.checkResult;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * @author Christian Willms - Date: 14.09.17 10:25.
 * @version 14.09.17
 */
public class QueryTest {

  static ForwardChainer fc;

  private static String getResource(String name) {
    return TestingUtils.getTestResource("LGetLatestValues", name);
  }

  @Before
  public void setUp() throws IOException, WrongFormatException {
    fc =  new ForwardChainer(Config.getDefaultConfig());
    fc.uploadTuples(getResource("test.child.labvalues.nt"));
  }

  @After
  public void tearDown() {
    fc.shutdownNoExit();
  }

  @Test
  public void testQuery() throws IOException, WrongFormatException {
    //test constructor Query(TupleStore tupleStore)
    TupleStore tupleStore = new TupleStore(Config.getDefaultConfig());
    Query query = new Query(tupleStore);
    assertNotNull(query);
  }

  @Test(expected = QueryParseException.class)
  public void query_Invalid_Empty() throws QueryParseException {
    Query query = new Query(fc.tupleStore);
    query.query("");
  }

  @Test(expected = QueryParseException.class)
  public void query_Invalid_SELECT() throws QueryParseException {
    Query query = new Query(fc.tupleStore);
    query.query("SELECT ");
    query.query("SELECT DISTINCT");
    query.query("?o WHERE ?s <rdf:type> ?o");
    query.query("DISTINCT ?o WHERE ?s <rdf:type> ?o");
    query.query("SELECT FILTER ?s <rdf:type> ?o");
    query.query("SELECT ?s <rdf:type> ?o");
    query.query("SELECT *");
    query.query("SELECT WHERE ?s <rdf:type> ?o");
  }

  @Test(expected = QueryParseException.class)
  public void query_Invalid_Variable() throws QueryParseException {
    Query query = new Query(fc.tupleStore);
    query.query("SELECT ? WHERE ?s <rdf:type> ?o");
    query.query("SELECT !o WHERE ?s <rdf:type> ?o");
    query.query("SELECT _ WHERE ?s <rdf:type> ?o");
    query.query("SELECT ?rel WHERE ?s <rdf:type> ?o");
    query.query("SELECT * ?s WHERE ?s <rdf:type> ?o");
  }

    @Test(expected = QueryParseException.class)
    public void testSingleIntervalErrors() throws Exception {
        TupleStore tupleStore = new TupleStore(Config.getDefaultConfig());
        Query query = new Query(tupleStore);
        // inputs which should  lead to QueryParseExeptions
        query.query("SELECT DISTINCT ?p WHERE ?s ?p ?o [\"(1,2)\"^^<xsd:2DPoint>, \"(1,2)\"^^<xsd:2DPoint>]");
        query.query("SELECT DISTINCT ?p WHERE ?s ?p ?o [\"1\"^^<xsd:long>]");
        query.query("SELECT DISTINCT ?p WHERE ?s ?p ?o [ ]");
        query.query("SELECT DISTINCT ?p WHERE ?s ?p ?o [ , ]");
        query.query("SELECT DISTINCT ?p WHERE ?s ?p ?o [\"1\"^^<xsd:long>, \"1\"^^<xsd:long>, \"1\"^^<xsd:long>]");
        query.query("SELECT DISTINCT ?p WHERE ?s ?p ?o [\"1\"^^<xsd:long>, \"1\"^^<xsd:long>}");

    }

    @Test(expected = QueryParseException.class)
    public void testIntervalRelationsErrors() throws QueryParseException, IOException, WrongFormatException {
        TupleStore tupleStore = new TupleStore(Config.getDefaultConfig());
        Query query = new Query(tupleStore);

        query.query("SELECT DISTINCT ?p WHERE ?s ?p ?o FOO  \"1\"^^<xsd:date> , \"2\"^^<xsd:date>");
        query.query("SELECT DISTINCT ?p WHERE ?s ?p ?o F");
        query.query("SELECT DISTINCT ?p WHERE ?s ?p ?o F  \"1\"^^<xsd:long>  \"2\"^^<xsd:long>  \"1\"^^<xsd:long>");

    }

  @Test(expected = QueryParseException.class)
  public void query_Invalid_Filter() throws QueryParseException {
    Query query = new Query(fc.tupleStore);
    query.query("SELECT ?s WHERE ?s <rdf:type> ?o FILTER ?p != ?o");
    query.query("SELECT ?s WHERE ?s <rdf:type> ?o FILTER Less ?o ?o");
    query.query("SELECT ?s WHERE ?s <rdf:type> ?o FILTER CountDistinct ?o");
  }

  @Test(expected = QueryParseException.class)
  public void query_Invalid_Aggregate() throws QueryParseException {
    Query query = new Query(fc.tupleStore);
    query.query("SELECT ?s WHERE ?s <rdf:type> ?o AGGREGAT ?p != ?o");
    query.query("SELECT ?s WHERE ?s <rdf:type> ?o AGGREGATE ?number = CountDistinct ?o");
    query.query("SELECT ?s WHERE ?s <rdf:type> ?o AGGREGATE ?test = ILess ?s ?s");
    query.query("SELECT ?s WHERE ?s <rdf:type> ?o AGGREGATE ?test ILess ?s ?s");
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
  public void query_SELECT_WHERE() throws QueryParseException {
    String[][] expected = {{"<pal:labval22>"},{"<pal:labval22>"}, {"<pal:labval33>"},
{"<pal:labval33>"}};
    Query query = new Query(fc.tupleStore);
    BindingTable bt = query.query("SELECT ?s WHERE ?s <dom:bsl> ?o ?t");
    checkResult(fc, bt, expected, "?s");
  }

  @Test
  public void query_SELECT_WHERE_EMPTYTABLE() throws QueryParseException {
      Query query = new Query(fc.tupleStore);
      BindingTable bt = query.query("SELECT ?s WHERE ?s <dom:bse> ?o ?t");
      assertTrue(bt.isEmpty());
      query = new Query(fc.tupleStore);
      bt = query.query("SELECT ?s WHERE ?s \"0\"^^<xsd:long> ?o ?t");
      assertTrue(bt.isEmpty());
  }

    @Test
    public void query_SELECTALL_WHERE() throws QueryParseException {
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
  public void query_SELECT_DISTINCT_WHERE() throws QueryParseException {
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
  public void query_SELECT_WHERE_FILTER() throws QueryParseException {
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
  public void query_SELECT_WHERE_AGGREGATE() throws QueryParseException {
    String[][] expected = {{"\"2\"^^<xsd:int>"}};
    Query query = new Query(fc.tupleStore);
    BindingTable bt = query.query("SELECT ?s WHERE ?s <dom:bsl> ?o ?t AGGREGATE ?number = CountDistinct ?s") ;
    checkResult(fc, bt, expected, "?number");
  }

  @Test
  public void query_SELECT_WHERE_FILTER_AGGREGATE() throws QueryParseException {
    String[][] expected = {{"\"1\"^^<xsd:int>"}};
    Query query = new Query(fc.tupleStore);
    BindingTable bt = query.query("SELECT ?s WHERE ?s <dom:bsl> ?o ?t FILTER ?s != <pal:labval33> AGGREGATE ?number = CountDistinct ?s") ;
    checkResult(fc, bt, expected, "?number");
    //?subject = Identity ?p
       expected = new String[][]{{"\"1\"^^<xsd:int>", "\"1\"^^<xsd:int>"}};
      query = new Query(fc.tupleStore);
      bt = query.query("SELECT ?s WHERE ?s <dom:bsl> ?o ?t FILTER ?s != <pal:labval33> AGGREGATE ?number = CountDistinct ?s & ?number2 = CountDistinct <pal:labval33>") ;
      checkResult(fc, bt, expected, "?number", "?number2");
  }

  @Test
  public void isPredicate() {
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