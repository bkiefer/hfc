package de.dfki.lt.hfc;

import static de.dfki.lt.hfc.Utils.*;

import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.Before;
import org.junit.After;
import org.junit.Test;

import de.dfki.lt.hfc.BindingTable.BindingTableIterator;
import de.dfki.lt.hfc.types.AnyType;
import de.dfki.lt.hfc.types.XsdString;

public class BindingTableIteratorTest {
  Query q;
  Namespace ns;
  TupleStore ts;

  @Before
  public void setup() throws FileNotFoundException, IOException, WrongFormatException {
    ns = new Namespace(getTestResource("default.ns"), false);
    ts = new TupleStore(100000, 250000, ns, getTestResource("default.nt"));
    q = new Query(ts);
  }

  @After
  public void tearDown() {
    ns = null;
    ts = null;
    q = null;
  }

  @Test
  public void test11() throws QueryParseException, BindingTableIteratorException {
    String[][] expected = {
        { "<rdf:first>", "<rdf:type>", "<rdf:Property>" },
        { "<rdfs:subClassOf>", "<rdf:type>", "<owl:TransitiveProperty>" },
        { "<rdf:predicate>", "<rdf:type>", "<rdf:Property>" },
        { "<owl:equivalentProperty>", "<rdf:type>", "<owl:TransitiveProperty>" },
        { "<owl:inverseOf>", "<rdf:type>", "<owl:SymmetricProperty>" },
        { "<owl:disjointWith>", "<rdf:type>", "<owl:SymmetricProperty>" },
        { "<rdf:subject>", "<rdf:type>", "<rdf:Property>" },
        { "<rdf:type>", "<rdf:type>", "<rdf:Property>" },
        { "<owl:equivalentClass>", "<rdf:type>", "<owl:SymmetricProperty>" },
        { "<owl:Nothing>", "<rdf:type>", "<owl:Class>" },
        { "<owl:differentFrom>", "<rdf:type>", "<owl:SymmetricProperty>" },
        { "<rdf:nil>", "<rdf:type>", "<rdf:List>" },
        { "<owl:sameAs>", "<rdf:type>", "<owl:SymmetricProperty>" },
        { "<owl:equivalentProperty>", "<rdf:type>", "<owl:SymmetricProperty>" },
        { "<rdf:object>", "<rdf:type>", "<rdf:Property>" },
        { "<owl:Thing>", "<rdf:type>", "<owl:Class>" },
        { "<rdf:rest>", "<rdf:type>", "<rdf:Property>" },
        { "<owl:sameAs>", "<rdf:type>", "<owl:TransitiveProperty>" },
        { "<rdfs:subPropertyOf>", "<rdf:type>", "<owl:TransitiveProperty>" },
        { "<owl:equivalentClass>", "<rdf:type>", "<owl:TransitiveProperty>" },
    };
    // returns triples even though we're asking for pairs (?s, ?o), but because
    // we're using Trove sets and strategy objects for table projection, the
    // underlying int arrays are still of length 3
    BindingTable bt = q.query("SELECT * WHERE ?s <rdf:type> ?o FILTER ?o != <rdfs:Datatype>");
    BindingTableIterator it = bt.iterator();
    // printNext(it, new NextAsIntCall());
    check(it, expected, new NextAsIntCall(ts));
  }

  @Test
  public void test12() throws QueryParseException, BindingTableIteratorException {
    String[][] expected = {
        { "<rdf:subject>", "<rdf:type>", "<rdf:Property>" },
        { "<owl:equivalentProperty>", "<rdf:type>", "<owl:SymmetricProperty>" },
        { "<owl:equivalentClass>", "<rdf:type>", "<owl:SymmetricProperty>" },
        { "<owl:Nothing>", "<rdf:type>", "<owl:Class>" },
        { "<rdf:type>", "<rdf:type>", "<rdf:Property>" },
        { "<owl:disjointWith>", "<rdf:type>", "<owl:SymmetricProperty>" },
        { "<rdf:object>", "<rdf:type>", "<rdf:Property>" },
        { "<owl:differentFrom>", "<rdf:type>", "<owl:SymmetricProperty>" },
        { "<owl:sameAs>", "<rdf:type>", "<owl:SymmetricProperty>" },
        { "<owl:Thing>", "<rdf:type>", "<owl:Class>" },
        { "<owl:equivalentClass>", "<rdf:type>", "<owl:TransitiveProperty>" },
        { "<rdfs:subClassOf>", "<rdf:type>", "<owl:TransitiveProperty>" },
        { "<rdf:rest>", "<rdf:type>", "<rdf:Property>" },
        { "<rdfs:subPropertyOf>", "<rdf:type>", "<owl:TransitiveProperty>" },
        { "<owl:inverseOf>", "<rdf:type>", "<owl:SymmetricProperty>" },
        { "<owl:sameAs>", "<rdf:type>", "<owl:TransitiveProperty>" },
        { "<rdf:nil>", "<rdf:type>", "<rdf:List>" },
        { "<rdf:first>", "<rdf:type>", "<rdf:Property>" },
        { "<owl:equivalentProperty>", "<rdf:type>", "<owl:TransitiveProperty>" },
        { "<rdf:predicate>", "<rdf:type>", "<rdf:Property>" },
    };

    // returns triples even though we're asking for pairs (?s, ?o), but because
    // we're using Trove sets and strategy objects for table projection, the
    // underlying int arrays are still of length 3
    BindingTable bt = q.query("SELECT * WHERE ?s <rdf:type> ?o FILTER ?o != <rdfs:Datatype>");
    // System.out.println(bt);
    BindingTableIterator it = bt.iterator();
    // printNextAsString(it);
    check(it, expected, new NextAsStringCall());
  }

  @Test
  public void test13() throws QueryParseException, BindingTableIteratorException {
    // returns triples even though we're asking for pairs (?s, ?o), but because
    // we're using Trove sets and strategy objects for table projection, the
    // underlying int arrays are still of length 3
    String[][] expected = {
        { "<rdf:subject>", "<rdf:Property>" },
        { "<owl:equivalentProperty>", "<owl:SymmetricProperty>" },
        { "<owl:equivalentClass>", "<owl:SymmetricProperty>" },
        { "<owl:Nothing>", "<owl:Class>" },
        { "<rdf:type>", "<rdf:Property>" },
        { "<owl:disjointWith>", "<owl:SymmetricProperty>" },
        { "<rdf:object>", "<rdf:Property>" },
        { "<owl:differentFrom>", "<owl:SymmetricProperty>" },
        { "<owl:sameAs>", "<owl:SymmetricProperty>" },
        { "<owl:Thing>", "<owl:Class>" },
        { "<owl:equivalentClass>", "<owl:TransitiveProperty>" },
        { "<rdfs:subClassOf>", "<owl:TransitiveProperty>" },
        { "<rdf:rest>", "<rdf:Property>" },
        { "<rdfs:subPropertyOf>", "<owl:TransitiveProperty>" },
        { "<owl:inverseOf>", "<owl:SymmetricProperty>" },
        { "<owl:sameAs>", "<owl:TransitiveProperty>" },
        { "<rdf:nil>", "<rdf:List>" },
        { "<rdf:first>", "<rdf:Property>" },
        { "<owl:equivalentProperty>", "<owl:TransitiveProperty>" },
        { "<rdf:predicate>", "<rdf:Property>" },
    };
    BindingTable bt = q.query("SELECT * WHERE ?s <rdf:type> ?o FILTER ?o != <rdfs:Datatype>");
    // System.out.println(bt);
    BindingTableIterator it = bt.iterator("?s", "?o");
    check(it, expected, new NextAsStringCall());
  }

  @Test
  public void test14() throws QueryParseException, BindingTableIteratorException {
    String[][] expected = {
        { "rdf:subject", "rdf:Property" },
        { "owl:equivalentProperty", "owl:SymmetricProperty" },
        { "owl:equivalentClass", "owl:SymmetricProperty" },
        { "owl:Nothing", "owl:Class" },
        { "rdf:type", "rdf:Property" },
        { "owl:disjointWith", "owl:SymmetricProperty" },
        { "rdf:object", "rdf:Property" },
        { "owl:differentFrom", "owl:SymmetricProperty" },
        { "owl:sameAs", "owl:SymmetricProperty" },
        { "owl:Thing", "owl:Class" },
        { "owl:equivalentClass", "owl:TransitiveProperty" },
        { "rdfs:subClassOf", "owl:TransitiveProperty" },
        { "rdf:rest", "rdf:Property" },
        { "rdfs:subPropertyOf", "owl:TransitiveProperty" },
        { "owl:inverseOf", "owl:SymmetricProperty" },
        { "owl:sameAs", "owl:TransitiveProperty" },
        { "rdf:nil", "rdf:List" },
        { "rdf:first", "rdf:Property" },
        { "owl:equivalentProperty", "owl:TransitiveProperty" },
        { "rdf:predicate", "rdf:Property" },
    };
    // returns triples even though we're asking for pairs (?s, ?o), but because
    // we're using Trove sets and strategy objects for table projection, the
    // underlying int arrays are still of length 3
    BindingTable bt = q.query("SELECT * WHERE ?s <rdf:type> ?o FILTER ?o != <rdfs:Datatype>");
    // System.out.println(bt);
    BindingTableIterator it = bt.iterator("?s", "?o");

    // printNext(it, new NextAsHfcCall());
    check(it, expected, new NextAsHfcCall());
  }

  @Test
  public void test15() throws QueryParseException, BindingTableIteratorException {
    String[][] expected = {
        { "<rdf:Property>", "<rdf:subject>" },
        { "<owl:SymmetricProperty>", "<owl:equivalentProperty>" },
        { "<owl:SymmetricProperty>", "<owl:equivalentClass>" },
        { "<owl:Class>", "<owl:Nothing>" },
        { "<rdf:Property>", "<rdf:type>" },
        { "<owl:SymmetricProperty>", "<owl:disjointWith>" },
        { "<rdf:Property>", "<rdf:object>" },
        { "<owl:SymmetricProperty>", "<owl:differentFrom>" },
        { "<owl:SymmetricProperty>", "<owl:sameAs>" },
        { "<owl:Class>", "<owl:Thing>" },
        { "<owl:TransitiveProperty>", "<owl:equivalentClass>" },
        { "<owl:TransitiveProperty>", "<rdfs:subClassOf>" },
        { "<rdf:Property>", "<rdf:rest>" },
        { "<owl:TransitiveProperty>", "<rdfs:subPropertyOf>" },
        { "<owl:SymmetricProperty>", "<owl:inverseOf>" },
        { "<owl:TransitiveProperty>", "<owl:sameAs>" },
        { "<rdf:List>", "<rdf:nil>" },
        { "<rdf:Property>", "<rdf:first>" },
        { "<owl:TransitiveProperty>", "<owl:equivalentProperty>" },
        { "<rdf:Property>", "<rdf:predicate>" },
    };
    // returns triples even though we're asking for pairs (?s, ?o), but because
    // we're using Trove sets and strategy objects for table projection, the
    // underlying int arrays are still of length 3
    BindingTable bt = q.query("SELECT * WHERE ?s <rdf:type> ?o FILTER ?o != <rdfs:Datatype>");
    // System.out.println(bt);
    BindingTableIterator it = bt.iterator("?o", "?s");
    // printNext(it, new NextAsStringCall());
    check(it, expected, new NextAsStringCall());
  }

  @Test
  public void test16() throws QueryParseException, BindingTableIteratorException {
    String[][] expected = {
        { "<rdf:subject>", "<rdf:Property>", "<rdf:subject>", "<rdf:Property>" },
        { "<owl:equivalentProperty>", "<owl:SymmetricProperty>", "<owl:equivalentProperty>", "<owl:SymmetricProperty>" },
        { "<owl:equivalentClass>", "<owl:SymmetricProperty>", "<owl:equivalentClass>", "<owl:SymmetricProperty>" },
        { "<owl:Nothing>", "<owl:Class>", "<owl:Nothing>", "<owl:Class>" },
        { "<rdf:type>", "<rdf:Property>", "<rdf:type>", "<rdf:Property>" },
        { "<owl:disjointWith>", "<owl:SymmetricProperty>", "<owl:disjointWith>", "<owl:SymmetricProperty>" },
        { "<rdf:object>", "<rdf:Property>", "<rdf:object>", "<rdf:Property>" },
        { "<owl:differentFrom>", "<owl:SymmetricProperty>", "<owl:differentFrom>", "<owl:SymmetricProperty>" },
        { "<owl:sameAs>", "<owl:SymmetricProperty>", "<owl:sameAs>", "<owl:SymmetricProperty>" },
        { "<owl:Thing>", "<owl:Class>", "<owl:Thing>", "<owl:Class>" },
        { "<owl:equivalentClass>", "<owl:TransitiveProperty>", "<owl:equivalentClass>", "<owl:TransitiveProperty>" },
        { "<rdfs:subClassOf>", "<owl:TransitiveProperty>", "<rdfs:subClassOf>", "<owl:TransitiveProperty>" },
        { "<rdf:rest>", "<rdf:Property>", "<rdf:rest>", "<rdf:Property>" },
        { "<rdfs:subPropertyOf>", "<owl:TransitiveProperty>", "<rdfs:subPropertyOf>", "<owl:TransitiveProperty>" },
        { "<owl:inverseOf>", "<owl:SymmetricProperty>", "<owl:inverseOf>", "<owl:SymmetricProperty>" },
        { "<owl:sameAs>", "<owl:TransitiveProperty>", "<owl:sameAs>", "<owl:TransitiveProperty>" },
        { "<rdf:nil>", "<rdf:List>", "<rdf:nil>", "<rdf:List>" },
        { "<rdf:first>", "<rdf:Property>", "<rdf:first>", "<rdf:Property>" },
        { "<owl:equivalentProperty>", "<owl:TransitiveProperty>", "<owl:equivalentProperty>", "<owl:TransitiveProperty>" },
        { "<rdf:predicate>", "<rdf:Property>", "<rdf:predicate>", "<rdf:Property>" },
    };
    // returns triples even though we're asking for pairs (?s, ?o), but because
    // we're using Trove sets and strategy objects for table projection, the
    // underlying int arrays are still of length 3
    BindingTable bt = q.query("SELECT * WHERE ?s <rdf:type> ?o FILTER ?o != <rdfs:Datatype>");
    // System.out.println(bt);
    BindingTableIterator it = bt.iterator("?s", "?o", "?s", "?o");
    System.out.println(it.hasSize());
    check(it, expected, new NextAsStringCall());
  }

  @Test
  public void test17() throws QueryParseException, BindingTableIteratorException {
    String[][] expected = {
        { "<rdf:Property>" },
        { "<owl:SymmetricProperty>" },
        { "<owl:SymmetricProperty>" },
        { "<owl:Class>" },
        { "<rdf:Property>" },
        { "<owl:SymmetricProperty>" },
        { "<rdf:Property>" },
        { "<owl:SymmetricProperty>" },
        { "<owl:SymmetricProperty>" },
        { "<owl:Class>" },
        { "<owl:TransitiveProperty>" },
        { "<owl:TransitiveProperty>" },
        { "<rdf:Property>" },
        { "<owl:TransitiveProperty>" },
        { "<owl:SymmetricProperty>" },
        { "<owl:TransitiveProperty>" },
        { "<rdf:List>" },
        { "<rdf:Property>" },
        { "<owl:TransitiveProperty>" },
        { "<rdf:Property>" },
    };
    // returns triples even though we're asking for pairs (?s, ?o), but because
    // we're using Trove sets and strategy objects for table projection, the
    // underlying int arrays are still of length 3
    BindingTable bt = q.query("SELECT * WHERE ?s <rdf:type> ?o FILTER ?o != <rdfs:Datatype>");
    // System.out.println(bt);
    BindingTableIterator it = bt.iterator("?o");
    //printNext(it, new NextAsStringCall());
    check(it, expected, new NextAsStringCall());
  }

  @Test
  public void test21() throws QueryParseException, BindingTableIteratorException {
    String[][] expected = {
        { "\"4\"^^<xsd:int>", "<rdf:type>" },
        { "\"4\"^^<xsd:int>", "<rdf:type>" },
        { "\"4\"^^<xsd:int>", "<rdf:type>" },
        { "\"4\"^^<xsd:int>", "<rdf:type>" },
        { "\"4\"^^<xsd:int>", "<rdf:type>" },
        { "\"4\"^^<xsd:int>", "<rdf:type>" },
        { "\"4\"^^<xsd:int>", "<rdf:type>" },
        { "\"4\"^^<xsd:int>", "<rdf:type>" },
        { "\"4\"^^<xsd:int>", "<rdf:type>" },
        { "\"4\"^^<xsd:int>", "<rdfs:subClassOf>" },
        { "\"4\"^^<xsd:int>", "<rdf:type>" },
        { "\"4\"^^<xsd:int>", "<rdf:type>" },
        { "\"4\"^^<xsd:int>", "<rdf:type>" },
        { "\"4\"^^<xsd:int>", "<rdf:type>" },
        { "\"4\"^^<xsd:int>", "<rdf:type>" },
        { "\"4\"^^<xsd:int>", "<rdf:type>" },
        { "\"4\"^^<xsd:int>", "<rdfs:subClassOf>" },
        { "\"4\"^^<xsd:int>", "<rdfs:subPropertyOf>" },
        { "\"4\"^^<xsd:int>", "<rdf:type>" },
        { "\"4\"^^<xsd:int>", "<rdf:type>" },
        { "\"4\"^^<xsd:int>", "<rdf:type>" },
        { "\"4\"^^<xsd:int>", "<rdf:type>" },
        { "\"4\"^^<xsd:int>", "<rdf:type>" },
        { "\"4\"^^<xsd:int>", "<rdfs:subClassOf>" },
        { "\"4\"^^<xsd:int>", "<owl:disjointWith>" },
        { "\"4\"^^<xsd:int>", "<rdf:type>" },
        { "\"4\"^^<xsd:int>", "<rdf:type>" },
        { "\"4\"^^<xsd:int>", "<rdf:type>" },
        { "\"4\"^^<xsd:int>", "<rdfs:subClassOf>" },
        { "\"4\"^^<xsd:int>", "<rdf:type>" },
        { "\"4\"^^<xsd:int>", "<rdf:type>" },
        { "\"4\"^^<xsd:int>", "<rdf:type>" },
        { "\"4\"^^<xsd:int>", "<rdfs:subClassOf>" },
        { "\"4\"^^<xsd:int>", "<rdf:type>" },
        { "\"4\"^^<xsd:int>", "<rdfs:subPropertyOf>" },
        { "\"4\"^^<xsd:int>", "<rdf:type>" },
        { "\"4\"^^<xsd:int>", "<rdfs:subClassOf>" },
        { "\"4\"^^<xsd:int>", "<rdfs:subClassOf>" },
        { "\"4\"^^<xsd:int>", "<rdf:type>" },
        { "\"4\"^^<xsd:int>", "<rdf:type>" },
        { "\"4\"^^<xsd:int>", "<rdf:type>" },
        { "\"4\"^^<xsd:int>", "<rdf:type>" },
        { "\"4\"^^<xsd:int>", "<rdf:type>" },
        { "\"4\"^^<xsd:int>", "<rdf:type>" },
        { "\"4\"^^<xsd:int>", "<rdf:type>" },
        { "\"4\"^^<xsd:int>", "<rdfs:subClassOf>" },
    };
    BindingTable bt = q.query("SELECT ?p WHERE ?s ?p ?o AGGREGATE ?number = CountDistinct ?p & ?subject = Identity ?p");
    BindingTableIterator it = bt.iterator("?number", "?subject");
    //printNext(it, new NextAsStringCall());
    check(it, expected, new NextAsStringCall());
  }

  @Test
  public void testAsObject() throws QueryParseException, BindingTableIteratorException {
    Object[][] expected = {
        { 4, "rdf:type" },
        { 4, "rdf:type" },
        { 4, "rdf:type" },
        { 4, "rdf:type" },
        { 4, "rdfs:subPropertyOf" },
        { 4, "rdf:type" },
        { 4, "rdfs:subClassOf" },
        { 4, "rdfs:subPropertyOf" },
        { 4, "rdf:type" },
        { 4, "rdf:type" },
        { 4, "rdf:type" },
        { 4, "rdfs:subClassOf" },
        { 4, "rdf:type" },
        { 4, "rdf:type" },
        { 4, "rdfs:subClassOf" },
        { 4, "rdf:type" },
        { 4, "rdf:type" },
        { 4, "rdf:type" },
        { 4, "rdfs:subClassOf" },
        { 4, "rdf:type" },
        { 4, "rdf:type" },
        { 4, "rdf:type" },
        { 4, "rdfs:subClassOf" },
        { 4, "rdf:type" },
        { 4, "rdf:type" },
        { 4, "rdf:type" },
        { 4, "rdfs:subClassOf" },
        { 4, "rdf:type" },
        { 4, "rdf:type" },
        { 4, "rdf:type" },
        { 4, "rdf:type" },
        { 4, "rdf:type" },
        { 4, "rdf:type" },
        { 4, "rdf:type" },
        { 4, "rdfs:subClassOf" },
        { 4, "rdf:type" },
        { 4, "rdf:type" },
        { 4, "rdf:type" },
        { 4, "rdf:type" },
        { 4, "rdf:type" },
        { 4, "rdfs:subClassOf" },
        { 4, "rdf:type" },
        { 4, "rdf:type" },
        { 4, "rdf:type" },
        { 4, "rdf:type" },
        { 4, "owl:disjointWith" },
    };

    BindingTable bt = q.query("SELECT ?p WHERE ?s ?p ?o AGGREGATE ?number = CountDistinct ?p & ?subject = Identity ?p");
    BindingTableIterator it = bt.iterator("?number", "?subject");
    assertEquals(expected.length, bt.size());
    check(it, expected, new NextAsObjectCall());
  }

  @Test
  public void test22() throws QueryParseException, BindingTableIteratorException {
    String[][] expected = {
        { "4", "rdf:type" },
        { "4", "rdf:type" },
        { "4", "rdfs:subClassOf" },
        { "4", "rdf:type" },
        { "4", "rdfs:subClassOf" },
        { "4", "rdf:type" },
        { "4", "rdf:type" },
        { "4", "rdf:type" },
        { "4", "rdf:type" },
        { "4", "rdf:type" },
        { "4", "rdf:type" },
        { "4", "rdf:type" },
        { "4", "rdf:type" },
        { "4", "rdf:type" },
        { "4", "rdfs:subClassOf" },
        { "4", "rdfs:subClassOf" },
        { "4", "rdf:type" },
        { "4", "rdf:type" },
        { "4", "rdf:type" },
        { "4", "rdf:type" },
        { "4", "rdf:type" },
        { "4", "rdf:type" },
        { "4", "rdf:type" },
        { "4", "rdfs:subClassOf" },
        { "4", "rdf:type" },
        { "4", "rdf:type" },
        { "4", "rdf:type" },
        { "4", "rdf:type" },
        { "4", "rdf:type" },
        { "4", "rdf:type" },
        { "4", "rdf:type" },
        { "4", "rdfs:subClassOf" },
        { "4", "rdfs:subClassOf" },
        { "4", "owl:disjointWith" },
        { "4", "rdf:type" },
        { "4", "rdf:type" },
        { "4", "rdf:type" },
        { "4", "rdfs:subPropertyOf" },
        { "4", "rdfs:subClassOf" },
        { "4", "rdf:type" },
        { "4", "rdf:type" },
        { "4", "rdf:type" },
        { "4", "rdfs:subPropertyOf" },
        { "4", "rdf:type" },
        { "4", "rdf:type" },
        { "4", "rdf:type" },
    };

    BindingTable bt = q.query("SELECT ?p WHERE ?s ?p ?o AGGREGATE ?number = CountDistinct ?p & ?subject = Identity ?p");
    BindingTableIterator it = bt.iterator("?number", "?subject");
    //printNext(it, new NextAsHfcCall());
    check(it, expected, new NextAsHfcCall());
  }

  @Test
  public void test23() throws QueryParseException, BindingTableIteratorException {
    String[][] expected = {
        { "<rdfs:subClassOf>" },
        { "<rdfs:subClassOf>" },
        { "<rdf:type>" },
        { "<rdf:type>" },
        { "<rdf:type>" },
        { "<rdf:type>" },
        { "<rdfs:subClassOf>" },
        { "<rdfs:subClassOf>" },
        { "<rdf:type>" },
        { "<rdf:type>" },
        { "<rdf:type>" },
        { "<owl:disjointWith>" },
        { "<rdf:type>" },
        { "<rdf:type>" },
        { "<rdf:type>" },
        { "<rdf:type>" },
        { "<rdf:type>" },
        { "<rdf:type>" },
        { "<rdf:type>" },
        { "<rdf:type>" },
        { "<rdf:type>" },
        { "<rdf:type>" },
        { "<rdf:type>" },
        { "<rdf:type>" },
        { "<rdf:type>" },
        { "<rdfs:subClassOf>" },
        { "<rdfs:subPropertyOf>" },
        { "<rdf:type>" },
        { "<rdf:type>" },
        { "<rdf:type>" },
        { "<rdf:type>" },
        { "<rdf:type>" },
        { "<rdf:type>" },
        { "<rdf:type>" },
        { "<rdf:type>" },
        { "<rdfs:subClassOf>" },
        { "<rdf:type>" },
        { "<rdf:type>" },
        { "<rdf:type>" },
        { "<rdf:type>" },
        { "<rdfs:subClassOf>" },
        { "<rdfs:subPropertyOf>" },
        { "<rdf:type>" },
        { "<rdfs:subClassOf>" },
        { "<rdf:type>" },
        { "<rdf:type>" },
    };
    BindingTable bt = q.query("SELECT ?p WHERE ?s ?p ?o AGGREGATE ?number = CountDistinct ?p & ?subject = Identity ?p");
    BindingTableIterator it = bt.iterator("?subject");
    //printNext(it, new NextAsStringCall());
    check(it, expected, new NextAsStringCall());
  }

  @SuppressWarnings("unused")
  @Test(expected = BindingTableIteratorException.class)
  public void testError() throws QueryParseException, BindingTableIteratorException {
    BindingTable bt = q.query("SELECT ?p WHERE ?s ?p ?o AGGREGATE ?number = CountDistinct ?p & ?subject = Identity ?p");
    BindingTableIterator it = bt.iterator("?s", "?p", "?o"); // error!
  }

  @Test
  public void testUnknownAtom() throws QueryParseException, BindingTableIteratorException {
    BindingTable bt = q.query("SELECT ?s WHERE ?s <rdf:flabbergast> ?o ");
    BindingTableIterator it = bt.iterator("?s"); // empty binding table
    assertEquals(0, it.hasSize());
  }

  @Test
  public void testAsHfcObject() throws QueryParseException, BindingTableIteratorException {
    String[] stringTuple = { "<owl:disjointWith>", "<rdfs:comment>", "\"A comment\"^^<xsd:string>"};
    ts.addTuple(stringTuple);
    BindingTable bt = q.query("SELECT ?s ?o WHERE ?s <rdfs:comment> ?o ");
    BindingTableIterator it = bt.iterator("?s", "?o");
    assertTrue(it.hasNext());
    AnyType[] res = it.nextAsHfcType();
    assertEquals(XsdString.class, res[1].getClass());
    assertEquals("A comment", ((XsdString)res[1]).value);
  }

  /*  TODO: CHECK IT OR ACCOMODATE
  @Test
  public void testAsHfcObject2() throws QueryParseException, BindingTableIteratorException {
    String[] stringTuple = { "<owl:disjointWith>", "<rdfs:comment>", "A comment"};
    ts.addTuple(stringTuple);
    BindingTable bt = q.query("SELECT ?s ?o WHERE ?s <rdfs:comment> ?o ");
    BindingTableIterator it = bt.iterator("?s", "?o");
    assertTrue(it.hasNext());
    AnyType[] res = it.nextAsHfcType();
    assertEquals(XsdString.class, res[1].getClass());
    assertEquals("A comment", ((XsdString)res[1]).value);
  }
  */

  @Test
  public void testAsHfcObject1() throws QueryParseException, BindingTableIteratorException {
    String[] stringTuple = { "<owl:disjointWith>", "<rdfs:comment>", "\"A comment\""};
    ts.addTuple(stringTuple);
    BindingTable bt = q.query("SELECT ?s ?o WHERE ?s <rdfs:comment> ?o ");
    BindingTableIterator it = bt.iterator("?s", "?o");
    assertTrue(it.hasNext());
    AnyType[] res = it.nextAsHfcType();
    assertEquals(XsdString.class, res[1].getClass());
    assertEquals("A comment", ((XsdString)res[1]).value);
  }
}