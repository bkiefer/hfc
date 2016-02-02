package de.dfki.lt.hfc;

import static de.dfki.lt.hfc.TestUtils.*;

import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import de.dfki.lt.hfc.BindingTable.BindingTableIterator;
import de.dfki.lt.hfc.types.AnyType;

public class TestBindingTableIterator {
  static Query q;

  @BeforeClass
  public static void setup() throws FileNotFoundException, IOException, WrongFormatException {
    Namespace ns = new Namespace(getResource("default.ns"));
    TupleStore ts = new TupleStore(100000, 250000, ns, getResource("default.nt"));
    q = new Query(ts);

    // System.out.println(bt);
  }

  @Test
  public void test11() throws QueryParseException, BindingTableIteratorException {
    // returns triples even though we're asking for pairs (?s, ?o), but because
    // we're using Trove sets and strategy objects for table projection, the
    // underlying int arrays are still of length 3
    BindingTable bt = q.query("SELECT * WHERE ?s <rdf:type> ?o FILTER ?o != <rdfs:Datatype>");
    // System.out.println(bt);
    BindingTableIterator it = bt.iterator();
    System.out.println(it.hasSize());
    while (it.hasNext()) {
      int[] next = it.next();
      for (int i = 0; i < next.length; i++)
        System.out.print(next[i] + " ");
      System.out.println();
    }
  }

  @Test
  public void test12() throws QueryParseException, BindingTableIteratorException {
    // returns triples even though we're asking for pairs (?s, ?o), but because
    // we're using Trove sets and strategy objects for table projection, the
    // underlying int arrays are still of length 3
    BindingTable bt = q.query("SELECT * WHERE ?s <rdf:type> ?o FILTER ?o != <rdfs:Datatype>");
    // System.out.println(bt);
    BindingTableIterator it = bt.iterator();
    System.out.println(it.hasSize());
    while (it.hasNext()) {
      String[] next = it.nextAsString();
      for (int i = 0; i < next.length; i++)
        System.out.print(next[i] + " ");
      System.out.println();
    }
  }

  @Test
  public void test13() throws QueryParseException, BindingTableIteratorException {
    // returns triples even though we're asking for pairs (?s, ?o), but because
    // we're using Trove sets and strategy objects for table projection, the
    // underlying int arrays are still of length 3
    BindingTable bt = q.query("SELECT * WHERE ?s <rdf:type> ?o FILTER ?o != <rdfs:Datatype>");
    // System.out.println(bt);
    BindingTableIterator it = bt.iterator("?s", "?o");
    System.out.println(it.hasSize());
    while (it.hasNext()) {
      String[] next = it.nextAsString();
      for (int i = 0; i < next.length; i++)
        System.out.print(next[i] + " ");
      System.out.println();
    }
  }

  @Test
  public void test14() throws QueryParseException, BindingTableIteratorException {
    // returns triples even though we're asking for pairs (?s, ?o), but because
    // we're using Trove sets and strategy objects for table projection, the
    // underlying int arrays are still of length 3
    BindingTable bt = q.query("SELECT * WHERE ?s <rdf:type> ?o FILTER ?o != <rdfs:Datatype>");
    // System.out.println(bt);
    BindingTableIterator it = bt.iterator("?s", "?o");
    System.out.println(it.hasSize());
    while (it.hasNext()) {
      AnyType[] next = it.nextAsHfcType();
      for (int i = 0; i < next.length; i++)
        System.out.print(next[i] + " ");
      System.out.println();
    }
  }

  @Test
  public void test15() throws QueryParseException, BindingTableIteratorException {
    // returns triples even though we're asking for pairs (?s, ?o), but because
    // we're using Trove sets and strategy objects for table projection, the
    // underlying int arrays are still of length 3
    BindingTable bt = q.query("SELECT * WHERE ?s <rdf:type> ?o FILTER ?o != <rdfs:Datatype>");
    // System.out.println(bt);
    BindingTableIterator it = bt.iterator("?o", "?s");
    System.out.println(it.hasSize());
    while (it.hasNext()) {
      String[] next = it.nextAsString();
      for (int i = 0; i < next.length; i++)
        System.out.print(next[i] + " ");
      System.out.println();
    }
  }

  @Test
  public void test16() throws QueryParseException, BindingTableIteratorException {
    // returns triples even though we're asking for pairs (?s, ?o), but because
    // we're using Trove sets and strategy objects for table projection, the
    // underlying int arrays are still of length 3
    BindingTable bt = q.query("SELECT * WHERE ?s <rdf:type> ?o FILTER ?o != <rdfs:Datatype>");
    // System.out.println(bt);
    BindingTableIterator it = bt.iterator("?s", "?o", "?s", "?o");
    System.out.println(it.hasSize());
    while (it.hasNext()) {
      String[] next = it.nextAsString();
      for (int i = 0; i < next.length; i++)
        System.out.print(next[i] + " ");
      System.out.println();
    }
  }

  @Test
  public void test17() throws QueryParseException, BindingTableIteratorException {
    // returns triples even though we're asking for pairs (?s, ?o), but because
    // we're using Trove sets and strategy objects for table projection, the
    // underlying int arrays are still of length 3
    BindingTable bt = q.query("SELECT * WHERE ?s <rdf:type> ?o FILTER ?o != <rdfs:Datatype>");
    // System.out.println(bt);
    BindingTableIterator it = bt.iterator("?o");
    System.out.println(it.hasSize());
    while (it.hasNext()) {
      String[] next = it.nextAsString();
      for (int i = 0; i < next.length; i++)
        System.out.print(next[i] + " ");
      System.out.println();
    }
  }

  @Test
  public void test21() throws QueryParseException, BindingTableIteratorException {
    BindingTable bt = q.query("SELECT ?p WHERE ?s ?p ?o AGGREGATE ?number = CountDistinct ?p & ?subject = Identity ?p");
    System.out.println(bt);
    BindingTableIterator it = bt.iterator("?number", "?subject");
    System.out.println(it.hasSize());
    while (it.hasNext()) {
      String[] next = it.nextAsString();
      for (int i = 0; i < next.length; i++)
        System.out.print(next[i] + " ");
      System.out.println();
    }
  }

  @Test
  public void test22() throws QueryParseException, BindingTableIteratorException {
    BindingTable bt = q.query("SELECT ?p WHERE ?s ?p ?o AGGREGATE ?number = CountDistinct ?p & ?subject = Identity ?p");
    System.out.println(bt);
    BindingTableIterator it = bt.iterator("?number", "?subject");
    System.out.println(it.hasSize());
    while (it.hasNext()) {
      AnyType[] next = it.nextAsHfcType();
      for (int i = 0; i < next.length; i++)
        System.out.print(next[i] + " ");
      System.out.println();
    }
  }

  @Test
  public void test23() throws QueryParseException, BindingTableIteratorException {
    BindingTable bt = q.query("SELECT ?p WHERE ?s ?p ?o AGGREGATE ?number = CountDistinct ?p & ?subject = Identity ?p");
    System.out.println(bt);
    BindingTableIterator it = bt.iterator("?subject");
    System.out.println(it.hasSize());
    while (it.hasNext()) {
      String[] next = it.nextAsString();
      for (int i = 0; i < next.length; i++)
        System.out.print(next[i] + " ");
      System.out.println();
    }
  }

  @Test(expected = BindingTableIteratorException.class)
  public void testError() throws QueryParseException, BindingTableIteratorException {
    BindingTable bt = q.query("SELECT ?p WHERE ?s ?p ?o AGGREGATE ?number = CountDistinct ?p & ?subject = Identity ?p");
    System.out.println(bt);
    BindingTableIterator it = bt.iterator("?s", "?p", "?o"); // error!
  }
}
