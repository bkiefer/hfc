package de.dfki.lt.hfc;


import de.dfki.lt.hfc.indices.BTreeIndex;
import de.dfki.lt.hfc.indices.IntervalTreeIndex;
import de.dfki.lt.hfc.qrelations.AllenEqual;
import de.dfki.lt.hfc.qrelations.QRelation;
import de.dfki.lt.hfc.types.AnyType;
import de.dfki.lt.hfc.types.XsdDate;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static junit.framework.TestCase.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * NOTE: the prepareLookup method is not explicitly tested here. However, it is tested implicitly
 * by QueryTest

 *
 * @author Christian Willms - Date: 30.08.17 18:45.
 * @version 30.08.17
 */
public class IndexStoreTest {

  static Hfc fc;


  private static String getResource(String name) {
    return TestingUtils.getTestResource("Index_Parsing", name);
  }
  
  @Before
  public void  setUp() throws Exception {

    fc =  new Hfc(Config.getInstance(getResource("IndexStoreTest.yml")));

  }

  @Test
  public void  lookup() {
    assertEquals(74,fc.config.indexStore.lookup(new XsdDate(0,0,0)).size());
  }

  @Test
  public void  lookup1() {
    assertEquals(74,fc.config.indexStore.lookup(new XsdDate(0,0,1),new XsdDate(0,0,2)).size());
  }

  @Test
  public void  update_ClosureComputation() {
      fc.computeClosure();
      assertEquals(1,fc.config.indexStore.getPrimIndex().size());
      Set<int[]> res = fc.config.indexStore.lookup(new XsdDate(0,0,0));
      assertEquals(156,fc.config.indexStore.lookup(new XsdDate(0,0,0)).size());
      assertEquals(1,fc.config.indexStore.getSecIndex().size());
      assertEquals(156,fc.config.indexStore.lookup(new XsdDate(0,0,1),new XsdDate(0,0,2)).size());
  }

  @Test
  public void  update(){
    // the size(number of used keys) of the primary index should be unchanged, but the one of the second index must be increased by 1.
    fc._tupleStore.addTuple(new String[]{"\"0000-00-00\"^^<xsd:date>", "<rdf:type>", "<rdf:type>", "<rdf:Property>", "\"0000-00-02\"^^<xsd:date>","\"0000-00-03\"^^<xsd:date>"});
    assertEquals(1,fc.config.indexStore.getPrimIndex().size());
    assertEquals(2,fc.config.indexStore.getSecIndex().size());
  }

  @Test
  public void  getPrimIndex() {
    assertTrue(fc.config.indexStore.getPrimIndex() instanceof BTreeIndex);
  }

  @Test
  public void  getSecIndex() {
    assertTrue(fc.config.indexStore.getSecIndex() instanceof IntervalTreeIndex);
  }

  @Test
  public void  size() {
    assertEquals(1,fc.config.indexStore.getPrimIndex().size());

  }

  @Test
  public void  secSize() {
    assertEquals(1,fc.config.indexStore.getSecIndex().size());
  }

  @Test
  public void  prepareLookup() {
    List<Integer> clause = Arrays.asList(
        new Integer[]{fc._tupleStore.putObject("\"0000-00-00\"^^<xsd:date>"),
        -1,-2,-3,-4,-5});
    Map<Integer, QRelation> integerQRelationMap = new HashMap<Integer, QRelation>();
    Integer start = fc._tupleStore.putObject("\"0000-00-01\"^^<xsd:date>");
    Integer end = fc._tupleStore.putObject("\"0000-00-02\"^^<xsd:date>");
    AllenEqual equal = new AllenEqual("Eq",start,end,4);
    integerQRelationMap.put(-4,equal );
    integerQRelationMap.put(-5,equal );
    Set<IndexLookup> lkps = new HashSet<IndexLookup>();
    Set<QRelation> relationsToBeRewritten = new HashSet<>();
    fc.config.indexStore.prepareLookup(clause,integerQRelationMap, lkps, relationsToBeRewritten);
    assertEquals(1,lkps.size());
  }

}