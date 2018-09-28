package de.dfki.lt.hfc;

import org.junit.Test;

import java.io.FileNotFoundException;
import java.io.IOException;

import static org.junit.Assert.assertTrue;

public class BenchTest {


  /**
   * Simply load the ltworld ontology and return the runtime
   */
  @Test
  public void test() throws IOException, WrongFormatException {
    long start = System.currentTimeMillis();
    ForwardChainer fc = new ForwardChainer(Config.getInstance(TestingUtils.getTestResource("bench.yml")));
    long mid = System.currentTimeMillis();
    System.out.println("Reading took: " + (mid - start) + "ms");
    fc.computeClosure();
    long end = System.currentTimeMillis();
    System.out.println("Reading and closure took: " + (end - start) + "ms");
    System.out.println("closure took: " + (end - mid) + "ms");
  }

  @Test
  public void TestQ1() throws IOException, WrongFormatException, QueryParseException {
    ForwardChainer fc = new ForwardChainer(Config.getInstance(TestingUtils.getTestResource("bench.yml")));
    // further PAL-specific tuples (special XSD DT)
    fc.uploadTuples(TestingUtils.getTestResource("ltworld.jena.nt"));
    fc.computeClosure();
    String queryString = "Select * where ?s ?o ?p ";
    Query query = new Query(fc.tupleStore);
    long start = System.currentTimeMillis();
    BindingTable bt = query.query(queryString);
    long end = System.currentTimeMillis();
    System.out.println("Query parsing took: " + (end - start));
  }

  @Test
  public void TestQ2() throws IOException, WrongFormatException, QueryParseException {
    ForwardChainer fc = new ForwardChainer(Config.getInstance(TestingUtils.getTestResource("bench.yml")));
    // further PAL-specific tuples (special XSD DT)
    fc.uploadTuples(TestingUtils.getTestResource("ltworld.jena.nt"));
    fc.computeClosure();
    long start = System.currentTimeMillis();
    boolean b = fc.tupleStore.ask(new String[]{"<ltw:obj_67783>", "<ltw:lt_technologicalApplication>", "<ltw:KB_788599_Individual_78>"});
    long end = System.currentTimeMillis();
    System.out.println("Query parsing took: " + (end - start));
    assertTrue(b);
  }

  @Test
  public void TestQ3() throws IOException, WrongFormatException, QueryParseException {
    ForwardChainer fc = new ForwardChainer(Config.getInstance(TestingUtils.getTestResource("bench.yml")));
    // further PAL-specific tuples (special XSD DT)
    fc.uploadTuples(TestingUtils.getTestResource("ltworld.jena.nt"));
    fc.computeClosure();
    String queryString = "Select ?s ?p where ?s <ltw:participatedIn> ?p ";
    Query query = new Query(fc.tupleStore);
    long start = System.currentTimeMillis();
    BindingTable bt = query.query(queryString);
    long end = System.currentTimeMillis();
    System.out.println("Query parsing took: " + (end - start));
    //System.out.println(bt.toString());
  }

  @Test
  public void TestQ4() throws IOException, WrongFormatException, QueryParseException {
    ForwardChainer fc = new ForwardChainer(Config.getInstance(TestingUtils.getTestResource("bench.yml")));
    // further PAL-specific tuples (special XSD DT)
    fc.uploadTuples(TestingUtils.getTestResource("ltworld.jena.nt"));
    fc.computeClosure();
    String queryString = "Select ?s where ?s <rdf:type> <ltw:Active_Patent> ";
    Query query = new Query(fc.tupleStore);
    long start = System.currentTimeMillis();
    BindingTable bt = query.query(queryString);
    long end = System.currentTimeMillis();
    System.out.println("Query parsing took: " + (end - start));
    System.out.println(bt.toString());
  }

}
