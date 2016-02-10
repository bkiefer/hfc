package de.dfki.lt.hfc;

import static de.dfki.lt.hfc.TestUtils.getResource;
import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.Test;

public class QueryTest {

  @Test
  public void testQuery() {
    //test constructor Query(TupleStore tupleStore)
    TupleStore tupleStore = new TupleStore(2, 5);
    Query query = new Query(tupleStore);
    assertNotNull(query);
  }
  @Test
  public void testQuery1() throws FileNotFoundException, IOException, WrongFormatException{
    //test constructor Query(ForwardChainer fc)
    String tupleFile = getResource("default.nt");
    String ruleFile = getResource("default.eqred.rdl");
    ForwardChainer fc = new ForwardChainer(tupleFile, ruleFile);
    Query query = new Query(fc);
    assertNotNull(query);
  }
  @Test
  public void testquery() throws FileNotFoundException, IOException, WrongFormatException, QueryParseException{
  //test method public BindingTable query(String query)
    Namespace namespace = new Namespace(getResource("default.ns"));
    TupleStore ts = new TupleStore(true, true, true, 2, 5, 4, 2, namespace, getResource("default.nt"));
    Query obj = new Query(ts);
    assertTrue(obj.query("SELECT ?p WHERE ?s ?p ?o AGGREGATE ?number = CountDistinct ?p & ?subject = Identity ?p") instanceof BindingTable);
  }
  @Test
  public void testisPredicate(){
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
