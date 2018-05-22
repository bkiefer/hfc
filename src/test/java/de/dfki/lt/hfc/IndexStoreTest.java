package de.dfki.lt.hfc;


import de.dfki.lt.hfc.indices.BTreeIndex;
import de.dfki.lt.hfc.indices.IntervalTreeIndex;
import de.dfki.lt.hfc.qrelations.AllenEqual;
import de.dfki.lt.hfc.qrelations.QRelation;
import de.dfki.lt.hfc.types.XsdDate;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * NOTE: the prepareLookup method is not explicitely tested here. However, it is tested implicitely
 * by QueryTest
 * TODO test it here
 *
 * @author Christian Willms - Date: 30.08.17 18:45.
 * @version 30.08.17
 */
public class IndexStoreTest {

  static ForwardChainer fc;

  static String[] testSub1 = new String[]{"<rdf:type>", "<rdf:type>", "<rdf:Property>"};
  static String[] testSub2 = new String[]{"<test:sensor>", "<rdfs:subClassOf>", "<owl:Thing>"};

  private static String getResource(String name) {
    return TestingUtils.getTestResource("Index_Parsing", name);
  }
  
  @Before
  public void  setUp() throws Exception {

    fc =  new ForwardChainer(4,                                                    // #cores
        false,                                                 // verbose
        false,                                                 // RDF Check
        false,                                                // EQ reduction disabled
        6,                                                    // min #args
        6,                                                    // max #args
        100000,                                               // #atoms
        500000,                                               // #tuples
        getResource("transaction_date0_valid_date45.nt"),                // tuple file TODO
        getResource("transaction0_valid45.rdl"),                           // rule file  TODO
        getResource("Transaction.ns"),                           // namespace file TODO
        getResource("transaction_date0_valid_date45.idx")                  // index file TODO
    );

  }

  @Test
  public void  lookup() {
    assertEquals(74,fc.tupleStore.indexStore.lookup(new XsdDate(0,0,0)).size());
  }

  @Test
  public void  lookup1() {
    assertEquals(74,fc.tupleStore.indexStore.lookup(new XsdDate(0,0,1),new XsdDate(0,0,2)).size());
  }

  @Test
  public void  update_ClosureComputation() {
      fc.computeClosure();
      assertEquals(1,fc.tupleStore.indexStore.getPrimIndex().size());
      assertEquals(154,fc.tupleStore.indexStore.lookup(new XsdDate(0,0,0)).size());
      assertEquals(1,fc.tupleStore.indexStore.getSecIndex().size());
      assertEquals(154,fc.tupleStore.indexStore.lookup(new XsdDate(0,0,1),new XsdDate(0,0,2)).size());
  }

  @Test
  public void  update(){
    // the size(number of used keys) of the primary index should be unchanged, but the one of the second index must be increased by 1.
    fc.tupleStore.addTuple(new String[]{"\"0000-00-00\"^^<xsd:date>", "<rdf:type>", "<rdf:type>", "<rdf:Property>", "\"0000-00-02\"^^<xsd:date>","\"0000-00-03\"^^<xsd:date>"});
    assertEquals(1,fc.tupleStore.indexStore.getPrimIndex().size());
    assertEquals(2,fc.tupleStore.indexStore.getSecIndex().size());
  }

  @Test
  public void  getPrimIndex() {
    assertTrue(fc.tupleStore.indexStore.getPrimIndex() instanceof BTreeIndex);
  }

  @Test
  public void  getSecIndex() {
    assertTrue(fc.tupleStore.indexStore.getSecIndex() instanceof IntervalTreeIndex);
  }

  @Test
  public void  size() {
    assertEquals(1,fc.tupleStore.indexStore.getPrimIndex().size());

  }

  @Test
  public void  secSize() {
    assertEquals(1,fc.tupleStore.indexStore.getSecIndex().size());
  }

  @Test
  public void  prepareLookup() {
    List<Integer> clause = Arrays.asList(
        new Integer[]{fc.tupleStore.objectToId.get("\"0000-00-00\"^^<xsd:date>"),
        -1,-2,-3,-4,-5});
    Map<Integer, QRelation> integerQRelationMap = new HashMap<Integer, QRelation>();
    Integer start = fc.tupleStore.objectToId.get("\"0000-00-01\"^^<xsd:date>");
    Integer end = fc.tupleStore.objectToId.get("\"0000-00-02\"^^<xsd:date>");
    AllenEqual equal = new AllenEqual("Eq",start,end,4);
    integerQRelationMap.put(-4,equal );
    integerQRelationMap.put(-5,equal );
    Set<IndexLookup> lkps = new HashSet<IndexLookup>();
    Set<QRelation> relationsToBeRewritten = new HashSet<>();
    fc.tupleStore.indexStore.prepareLookup(clause,integerQRelationMap, lkps, relationsToBeRewritten);
    assertEquals(1,lkps.size());
  }

}