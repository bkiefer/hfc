package de.dfki.lt.hfc;

import static de.dfki.lt.hfc.TestingUtils.getTestResource;
import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashSet;

import org.junit.Test;


public class ClauseComparatorTest {

  @Test
  public void testconstructor(){
    //test ClauseComparator(HashSet<Integer> dontCareVariables, TupleStore tupleStore)
    HashSet<Integer> dontCareVariables = new HashSet();
    dontCareVariables.add(1);
    dontCareVariables.add(100);
    dontCareVariables.add(100000);
    TupleStore tupleStore = new TupleStore(100, 200);
    ClauseComparator cc = new ClauseComparator(dontCareVariables, tupleStore);
    assertNotNull(cc);
  }
  @Test
  public void testcompare(){
  /*test methods compare(int[] clause1, int[] clause2) and
    computeCost(int[] clause)*/
    int[] clause1 = new int[1];
    //to satisfy RuleStore.isVariable(id) use a negative int
    clause1[0] = -2;
    int[] clause2 = new int[1];
    clause2[0] = 5;
    HashSet<Integer> dontCareVariables = new HashSet();
    dontCareVariables.add(1);
    dontCareVariables.add(2);
    dontCareVariables.add(3);
    TupleStore ts = new TupleStore(2,5);
    ClauseComparator cc = new ClauseComparator(dontCareVariables, ts);
    assertTrue(cc.compare(clause1, clause2) > 0);
    assertEquals(990, cc.compare(clause1, clause2));//in this particular case
    assertTrue(cc.compare(clause2, clause1) < 0);
    //to satisfy this.dontCareVariables.contains(id)
    HashSet<Integer> dontCareVariables1 = new HashSet();
    dontCareVariables1.add(1);
    dontCareVariables1.add(-2);//now it contains the id
    dontCareVariables1.add(3);
    ClauseComparator cc1 = new ClauseComparator(dontCareVariables1, ts);
    assertEquals(-10, cc1.compare(clause1, clause2));
    //satisfy varToNoOcc.containsKey(id)
    int[] clause1a = new int[2];
    clause1a[0] = -2;
    clause1a[1] = 2;
    assertEquals(1000, cc.compare(clause1a, clause2));//c = c + 1000
  }

}
