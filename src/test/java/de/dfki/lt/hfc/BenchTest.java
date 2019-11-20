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
    Hfc fc = new Hfc(Config.getInstance(TestingUtils.getTestResource("bench.yml")));
    //long mid = System.currentTimeMillis();
    fc.computeClosure();
    //
    //
  }

  @Test
  public void TestQ1() throws IOException, WrongFormatException, QueryParseException {
    Hfc fc = new Hfc(Config.getInstance(TestingUtils.getTestResource("bench.yml")));
    // further PAL-specific tuples (special XSD DT)
    fc.uploadTuples(TestingUtils.getTestResource("ltworld.jena.nt"));
    fc.computeClosure();
    String queryString = "Select * where ?s ?o ?p ";
    Query query = new Query(fc._tupleStore);
    BindingTable bt = query.query(queryString);

  }

  @Test
  public void TestQ2() throws IOException, WrongFormatException, QueryParseException {
    Hfc fc = new Hfc(Config.getInstance(TestingUtils.getTestResource("bench.yml")));
    // further PAL-specific tuples (special XSD DT)
    fc.uploadTuples(TestingUtils.getTestResource("ltworld.jena.nt"));
    fc.computeClosure();
    boolean b = fc._tupleStore.ask(new String[]{"<ltw:obj_67783>", "<ltw:lt_technologicalApplication>", "<ltw:KB_788599_Individual_78>"});
    //
    assertTrue(b);
  }

  @Test
  public void TestQ3() throws IOException, WrongFormatException, QueryParseException {
    Hfc fc = new Hfc(Config.getInstance(TestingUtils.getTestResource("bench.yml")));
    // further PAL-specific tuples (special XSD DT)
    fc.uploadTuples(TestingUtils.getTestResource("ltworld.jena.nt"));
    fc.computeClosure();
    String queryString = "Select ?s ?p where ?s <ltw:participatedIn> ?p ";
    Query query = new Query(fc._tupleStore);

    BindingTable bt = query.query(queryString);

  }

  @Test
  public void TestQ4() throws IOException, WrongFormatException, QueryParseException {
    Hfc fc = new Hfc(Config.getInstance(TestingUtils.getTestResource("bench.yml")));
    // further PAL-specific tuples (special XSD DT)
    fc.uploadTuples(TestingUtils.getTestResource("ltworld.jena.nt"));
    fc.computeClosure();
    String queryString = "Select ?s where ?s <rdf:type> <ltw:Active_Patent> ";
    Query query = new Query(fc._tupleStore);
    BindingTable bt = query.query(queryString);

  }

}
