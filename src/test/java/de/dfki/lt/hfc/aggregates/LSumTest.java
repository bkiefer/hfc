package de.dfki.lt.hfc.aggregates;

import de.dfki.lt.hfc.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static de.dfki.lt.hfc.TestingUtils.checkResult;
import static org.junit.Assert.*;

public class LSumTest {

 static Hfc fc;

 public static String getResource(String name) {
  return TestingUtils.getTestResource("LGetLatestValues", name);
 }

 @Before
 public void init() throws Exception {

  fc = new Hfc(Config.getDefaultConfig());
  fc._tupleStore.namespace.putForm("pal", "http://www.lt-world.org/pal.owl#", true);
  fc._tupleStore.namespace.putForm("dom", "http://www.lt-world.org/dom.owl#", true);
  fc.uploadTuples(getResource("test.child.labvalues.nt"));
 }


 @After
 public void cleanup() {
  fc.shutdownNoExit();
 }

 @Test
 public void testLSum() throws QueryParseException, BindingTableIteratorException {
  Query q = new Query(fc._tupleStore);


  String[][] expected = {
          {"\"49995\"^^<xsd:long>"}
  };

  BindingTable bt = q.query("SELECT ?child ?prop ?val ?t "
          + "WHERE ?child <rdf:type> <dom:Child> ?t1 "
          + "& ?child <dom:hasLabValue> ?lv ?t2 "
          + "& ?lv ?prop ?val ?t "
          + "AGGREGATE ?time = LSum ?t ");
  // check whether all expected entries were found
  checkResult(fc, bt, expected, "?time");
 }

}