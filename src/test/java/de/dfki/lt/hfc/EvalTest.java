package de.dfki.lt.hfc;


import org.junit.jupiter.api.Test;
import de.dfki.lt.hfc.indices.IndexingException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

/**
 * @author Christian Willms - Date: 17.11.17 15:53.
 * @version 17.11.17
 */
public class EvalTest {

  private static String getResource(String name) {
    return TestUtils.getTestResource("Transaction", name);
  }


  static ForwardChainer forwardChainerNoIndex;
  static ForwardChainer forwardChainerIndex;

  static Map<String,String> queries = new HashMap<>();


    String q6 = "SELECT ?s ?o WHERE \"0\"^^<xsd:long> ?s <rdf:type> <univBench:FullProfessor>  & [ \"0\"^^<xsd:long> , \"6666\"^^<xsd:long> ]?s <univBench:teacherOf> ?o ";



 @BeforeAll
  public static void doSetup() throws IOException, WrongFormatException, IndexingException {
    forwardChainerNoIndex =new ForwardChainer(4,                                                    // #cores
          false,                                                 // verbose
          false,                                                 // RDF Check
          false,                                                // EQ reduction disabled
          4,
          4,                                                    // max #args
          100000,                                               // #atoms
          500000,                                               // #tuples
          getResource("uni_big_tt.nt"),                 // tuple file
          getResource("testRules_Transaction.rdl"),                           // rule file  TODO
          getResource("default.ns")                           // namespace file
      );
   forwardChainerIndex =new ForwardChainer(4,                                                    // #cores
          false,                                                 // verbose
          false,                                                 // RDF Check
          false,                                                // EQ reduction disabled
          4,                                                    // min #args
          4,                                                    // max #args
          100000,                                               // #atoms
          500000,                                               // #tuples
          getResource("uni_big_tt.nt"),                 // tuple file
          getResource("testRules_Transaction.rdl"),                           // rule file  TODO
          getResource("default.ns"),                  // namespace file
          getResource("BPlusTree_Transaction.idx")
      );
  }

  @AfterAll
  public static void doTearDown() {
    forwardChainerNoIndex.shutdownNoExit();
    forwardChainerIndex.shutdownNoExit();
  }

  @Test
  public void testMethod() throws QueryParseException {
    Query q = new Query(forwardChainerNoIndex.tupleStore);
    Query q1 = new Query(forwardChainerIndex.tupleStore);
    BindingTable bt = q.query(q6);
    BindingTable bt1 = q1.query(q6);
    if(bt.isEmpty()){
      throw new IllegalStateException(bt.toString());
    }
    if(bt1.isEmpty()){
      throw new IllegalStateException(bt1.toString());
    }

  }

}
