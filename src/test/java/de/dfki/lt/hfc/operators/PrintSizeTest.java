package de.dfki.lt.hfc.operators;

import de.dfki.lt.hfc.*;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static de.dfki.lt.hfc.TestingUtils.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import org.junit.After;
import static org.junit.Assert.*;
import org.junit.Before;


public class PrintSizeTest {
  static Hfc hfc;
  private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
  private PrintStream safeOut;
  
  @Before
  public void setUpStreams() {
    safeOut = System.out;
    System.setOut(new PrintStream(outContent));
  }

  @After
  public void cleanUpStreams() {
    System.setOut(safeOut);
  }

  private static String getResource(String name) {
    return TestingUtils.getTestResource("PrintSize", name);
  }

  @BeforeClass
  public static void init() throws Exception {

    hfc =  new Hfc(getResource("PrintSize.yml"));

    // compute deductive closure

    hfc.computeClosure();

  }

  @Test
  public void test() throws QueryParseException, WrongFormatException, IOException  {
     // load NamespaceManager
    NamespaceManager namespace = NamespaceManager.getInstance();

    // create TupleStore
    TupleStore store =
        new TupleStore(false, true, true, 2, 5,0,1,2, 4, 2, namespace,
            getTestResource("default.nt"));

    // create FunctionalOperator
    RelationalOperator rop =
        (RelationalOperator)store.checkAndRegisterOperator("de.dfki.lt.hfc.operators.PrintSize");

    String[][] expected = {
        { "<test:db>", "<test:hasName>", "\"Daimler Benz\"^^<xsd:string>" },
        { "<owl:Thing>", "<owl:disjointWith>", "<owl:Nothing>" },
        { "<test:Company>", "<rdfs:subClassOf>", "<owl:Thing>" },
        { "<test:sri>", "<rdf:type>", "<test:Company>" },
        { "<test:dfki>", "<test:hasName>", "\"DFKI\"^^<xsd:string>" },
        { "<test:sri>", "<test:hasName>", "\"Stanford Research Institute\"^^<xsd:string>" },
        { "<test:db>", "<rdf:type>", "<test:Company>" },
        { "<xsd:int>", "<rdf:type>", "<rdfs:Datatype>" },
        { "<test:dfki>", "<test:new>", "_:de.dfki.lt.hfc.ForwardChainer@45283ce20" },
        { "<owl:Thing>", "<rdf:type>", "<owl:Class>" },
        { "<owl:Nothing>", "<rdfs:subClassOf>", "<owl:Thing>" },
        { "<test:dfki>", "<rdf:type>", "<test:Company>" },
        { "<test:sri>", "<test:new>", "_:de.dfki.lt.hfc.ForwardChainer@45283ce21" },
        { "<xsd:string>", "<rdf:type>", "<rdfs:Datatype>" },
        { "<test:dfki>", "<test:hasName>", "\"Deutsches Forschungszentrum für Künstliche Intelligenz\"@de" },
        { "<test:sri>", "<test:hasName>", "\"SRI\"^^<xsd:string>" },
        { "<test:dfki>", "<test:hasName>", "\"German Research Center for Artificial Inteligence\"@en" },
        { "<test:dfki>", "<test:hasName>", "\"DFKI GmbH\"^^<xsd:string>" },
        { "<owl:Nothing>", "<rdf:type>", "<owl:Class>" },

    };
    Query q = hfc.getQuery();
    BindingTable bt = q.query("SELECT ?s ?p ?o WHERE ?s ?p ?o");
    rop.apply(new BindingTable[]{bt});
    String temp = outContent.toString();
    checkResult(expected, bt, bt.getVars());
    // could be problematic if the SystemOutput will change in future versions
    // of hfc
    assertEquals("19 \n", temp.substring(temp.length() -4));
  }

  @AfterClass
  public static void finish() {
    hfc.shutdownNoExit();
  }

}









