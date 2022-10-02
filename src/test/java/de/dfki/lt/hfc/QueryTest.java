package de.dfki.lt.hfc;

import static de.dfki.lt.hfc.TestingUtils.checkResult;
import static de.dfki.lt.hfc.TestingUtils.printExpected;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.StringReader;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.dfki.lt.hfc.io.QueryParseException;
import de.dfki.lt.hfc.io.QueryParser;
import de.dfki.lt.hfc.types.XsdString;

/**
 * @author Christian Willms - Date: 14.09.17 10:25.
 * @version 14.09.17
 */
public class QueryTest {

 static TestHfc fc;

 private static String getResource(String name) {
  return TestingUtils.getTestResource("LGetLatestValues", name);
 }

 @Before
 public void setUp() throws IOException, WrongFormatException {
  TestConfig config = TestConfig.getDefaultConfig();
  config.addNamespace("pal", "http://www.dfki.de/lt/onto/pal.owl#");
  config.addNamespace("dom", "http://www.dfki.de/lt/onto/dom.owl#");
  fc = new TestHfc(config);
  fc.uploadTuples(getResource("test.child.labvalues.nt"));
 }

 @After
 public void tearDown() {
  fc.shutdownNoExit();
 }

 @Test
 public void testQuery() throws IOException, WrongFormatException {
  //test constructor Query(TupleStore tupleStore)
  Query query = fc.getQuery();
  assertNotNull(query);
 }

 @Test(expected = QueryParseException.class)
 public void query_Invalid_Empty() throws QueryParseException {
  Query query = fc.getQuery();
  query.query("");
 }

 private void checkInvalid(String[] invalid) {
   Query query = fc.getQuery();
   for (String q: invalid) {
     try {
       query.query(q);
       assertFalse(q, true);
     } catch (Throwable qex) {
       assertTrue(q, true);
     }
   }
 }

 @Test
  public void query_Invalid_SELECT() throws QueryParseException {
  String[] invalid ={ "SELECT ",
  "SELECT DISTINCT",
  "?o WHERE ?s <rdf:type> ?o",
  "DISTINCT ?o WHERE ?s <rdf:type> ?o",
  "SELECT FILTER ?s <rdf:type> ?o",
  "SELECT ?s <rdf:type> ?o",
  "SELECT *",
  "SELECT WHERE ?s <rdf:type> ?o"};
  checkInvalid(invalid);
 }

 @Test
 public void query_Invalid_Variable() throws QueryParseException {
  String[] invalid ={ "SELECT ? WHERE ?s <rdf:type> ?o",
  "SELECT !o WHERE ?s <rdf:type> ?o",
  "SELECT _ WHERE ?s <rdf:type> ?o",
  "SELECT ?rel WHERE ?s <rdf:type> ?o",
  "SELECT * ?s WHERE ?s <rdf:type> ?o",
  };
   checkInvalid(invalid);
 }

 @Test
 public void testSingleIntervalErrors() throws Exception {
  // inputs which should  lead to QueryParseExeptions
  String[] invalid ={ "SELECT DISTINCT ?p WHERE ?s ?p ?o [\"(1,2)\"^^<xsd:2DPoint>, \"(1,2)\"^^<xsd:2DPoint>]",
  "SELECT DISTINCT ?p WHERE ?s ?p ?o [\"1\"^^<xsd:long>]",
  "SELECT DISTINCT ?p WHERE ?s ?p ?o [ ]",
  "SELECT DISTINCT ?p WHERE ?s ?p ?o [ , ]",
  "SELECT DISTINCT ?p WHERE ?s ?p ?o [\"1\"^^<xsd:long>, \"1\"^^<xsd:long>, \"1\"^^<xsd:long>]",
  "SELECT DISTINCT ?p WHERE ?s ?p ?o [\"1\"^^<xsd:long>, \"1\"^^<xsd:long>}",
  };
   checkInvalid(invalid);
 }

 @Test
 public void testIntervalRelationsErrors() throws QueryParseException, IOException, WrongFormatException {
  String[] invalid ={ "SELECT DISTINCT ?p WHERE ?s ?p ?o FOO  \"1\"^^<xsd:date> , \"2\"^^<xsd:date>",
  "SELECT DISTINCT ?p WHERE ?s ?p ?o F",
  "SELECT DISTINCT ?p WHERE ?s ?p ?o F  \"1\"^^<xsd:long>  \"2\"^^<xsd:long>  \"1\"^^<xsd:long>",
  };
   checkInvalid(invalid);
 }

 @Test
 public void query_Invalid_Filter() throws QueryParseException {
  String[] invalid ={ "SELECT ?s WHERE ?s <rdf:type> ?o FILTER ?p != ?o",
  "SELECT ?s WHERE ?s <rdf:type> ?o FILTER Less ?o ?o",
  "SELECT ?s WHERE ?s <rdf:type> ?o FILTER CountDistinct ?o",
  };
   checkInvalid(invalid);
 }

 @Test
 public void query_Invalid_Aggregate() throws QueryParseException {
  String[] invalid ={ "SELECT ?s WHERE ?s <rdf:type> ?o AGGREGAT ?p != ?o",
  "SELECT ?s WHERE ?s <rdf:type> ?o AGGREGATE ?number = CountDistinct ?o",
  "SELECT ?s WHERE ?s <rdf:type> ?o AGGREGATE ?test = ILess ?s ?s",
  "SELECT ?s WHERE ?s <rdf:type> ?o AGGREGATE ?test ILess ?s ?s",
  };
   checkInvalid(invalid);
 }


 /**
  * expected bindingTable
  * ==================
  * | ?s             |
  * ==================
  * | <pal:labval22> |
  * | <pal:labval22> |
  * | <pal:labval33> |
  * | <pal:labval33> |
  * ------------------
  *
  * @throws QueryParseException
  */
 @Test
 public void query_SELECT_WHERE() throws QueryParseException {
  String[][] expected = {{"<pal:labval22>"}, {"<pal:labval22>"}, {"<pal:labval33>"},
          {"<pal:labval33>"}};
  Query query = fc.getQuery();
  BindingTable bt = query.query("SELECT ?s WHERE ?s <dom:bsl> ?o ?t");
  checkResult(fc, bt, expected, "?s");
 }

 @Test
 public void query_SELECT_WHERE_EMPTYTABLE() throws QueryParseException {
  Query query = fc.getQuery();
  BindingTable bt = query.query("SELECT ?s WHERE ?s <dom:bse> ?o ?t");
  assertTrue(bt.isEmpty());
  query = fc.getQuery();
  bt = query.query("SELECT ?s WHERE ?s \"0\"^^<xsd:long> ?o ?t");
  assertTrue(bt.isEmpty());
 }

 @Test
 public void query_SELECTALL_WHERE() throws QueryParseException {
  String[][] expected = {{"<pal:labval22>"}, {"<pal:labval33>"},
      {"<pal:labval22>"}, {"<pal:labval33>"}};
  Query query = fc.getQuery();
  BindingTable bt = query.query("SELECTALL ?s WHERE ?s <dom:bsl> ?o ?t");
  checkResult(fc, bt, expected, "?s");
  bt = query.query("SELECTALL DISTINCT ?s WHERE ?s <dom:bsl> ?o ?t");
  assertEquals(2, bt.size());
 }

 /**
  * expected bindingTable
  * ==================
  * | ?s             |
  * ==================
  * | <pal:labval22> |
  * | <pal:labval33> |
  * ------------------
  *
  * @throws QueryParseException
  */
 @Test
 public void query_SELECT_DISTINCT_WHERE() throws QueryParseException {
  String[][] expected = {{"<pal:labval22>"}, {"<pal:labval33>"}};
  Query query = fc.getQuery();
  BindingTable bt = query.query("SELECT DISTINCT ?s WHERE ?s <dom:bsl> ?o ?t");
  checkResult(fc, bt, expected, "?s");
  expected = new String[][]{{"<pal:labval22>"}, {"<pal:labval33>"}};
  query = fc.getQuery();
  bt = query.query("SELECT DISTINCT ?s WHERE ?s <dom:bsl> ?o ?t & ?s <dom:weight> ?o1 ?t1");
  checkResult(fc, bt, expected, "?s");

 }

 @Test
 public void query_SELECT_WHERE_FILTER() throws QueryParseException {
  String[][] expected = {{"<pal:labval22>"}};
  Query query = fc.getQuery();
  BindingTable bt = query.query("SELECT DISTINCT ?s WHERE ?s <dom:bsl> ?o ?t FILTER ?s != <pal:labval33>");
  checkResult(fc, bt, expected, "?s");
  expected = new String[][]{{"<pal:labval22>"}};
  query = fc.getQuery();
  bt = query.query("SELECT DISTINCT ?s WHERE ?s <dom:bsl> ?o ?t FILTER ?s != <pal:labval33> & ?t != \"5577\"^^<xsd:long>");
  checkResult(fc, bt, expected, "?s");
 }

 @Test
 public void query_SELECT_WHERE_AGGREGATE() throws QueryParseException {
  String[][] expected = {{"\"2\"^^<xsd:int>"}};
  Query query = fc.getQuery();
  BindingTable bt = query.query("SELECT ?s WHERE ?s <dom:bsl> ?o ?t AGGREGATE ?number = CountDistinct ?s");
  checkResult(fc, bt, expected, "?number");
 }

 @Test
 public void query_SELECT_WHERE_FILTER_AGGREGATE() throws QueryParseException {
  String[][] expected = {{"\"1\"^^<xsd:int>"}};
  Query query = fc.getQuery();
  BindingTable bt = query.query("SELECT ?s WHERE ?s <dom:bsl> ?o ?t FILTER ?s != <pal:labval33> AGGREGATE ?number = CountDistinct ?s");
  checkResult(fc, bt, expected, "?number");
  //?subject = Identity ?p
  expected = new String[][]{{"\"1\"^^<xsd:int>", "\"1\"^^<xsd:int>"}};
  query = fc.getQuery();
  bt = query.query("SELECT ?s WHERE ?s <dom:bsl> ?o ?t FILTER ?s != <pal:labval33> AGGREGATE ?number = CountDistinct ?s & ?number2 = CountDistinct <pal:labval33>");
  checkResult(fc, bt, expected, "?number", "?number2");
  printExpected(bt, fc._tupleStore);
 }

 @Test
 public void query_SELECT_withBlanknode() throws QueryParseException {
   Query query = fc.getQuery();
   BindingTable bt = query.query("select ?s where _:genid17X004 <rdf:type> ?s");
   assertTrue(bt.isEmpty());
   bt = query.query("select ?s where ?s <rdf:type> _:genid17X004");
   assertTrue(bt.isEmpty());
   bt = query.query("select ?s where ?s <rdf:type> ?o FILTER ?o == _:genid17X004 & ?s == _:genid17X004");
   //System.out.println(bt.toString());
   assertFalse(bt.isEmpty());
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
  //is a variable
  assertFalse(Query.isPredicate("?dgjdfgj"));
 }

 @Test
 public void testPlainString() throws IOException, QueryParseException {
   String query ="select ?uri where ?uri <pal:hasName> \"DFKI\"^^<xsd:string> ?_ ";
   QueryParser parser = new QueryParser(new StringReader(query));
   parser.parse();
   assertEquals(new XsdString("DFKI").toString(), parser.whereClauses.get(0).get(2));
   query ="select ?uri where ?uri <pal:hasName> \"DFKI\" ?_ ";
   parser = new QueryParser(new StringReader(query));
   parser.parse();
   assertEquals(new XsdString("DFKI").toString(), parser.whereClauses.get(0).get(2));
 }

}