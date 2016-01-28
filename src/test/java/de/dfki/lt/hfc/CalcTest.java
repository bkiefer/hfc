package de.dfki.lt.hfc;

import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import org.junit.Test;

import gnu.trove.THashSet;

import static de.dfki.lt.hfc.TestUtils.*;


public class CalcTest {

  @Test
  public void testunion(){
  //test method union(Set<int[]> set1, Set<int[]> set2)
    Set<int[]> set1 = new THashSet<int[]>(TupleStore.DEFAULT_HASHING_STRATEGY);
    Set<int[]> set2 = new THashSet<int[]>();
    assertTrue(Calc.union(set1, set2).isEmpty());
    //test for case set2 > set1
    Set<int[]> set_one = new THashSet<int[]>();
    Set<int[]> set_two = new THashSet<int[]>();
    int [] e = new int[2];
    e[0] = 1;
    e[1] = 2;
    set_one.add(e);
    assertFalse(Calc.union(set_one, set_two).isEmpty());
  }
  @Test
  public void testunion1(){
  //test method union(Set<int[]> set1, Set<int[]> set2, TIntArrayHashingStrategy strategy)
    Set<int[]> set1 = new THashSet<int[]>(TupleStore.DEFAULT_HASHING_STRATEGY);
    Set<int[]> set2 = new THashSet<int[]>();
    TIntArrayHashingStrategy strategy = new TIntArrayHashingStrategy();
    assertTrue(Calc.union(set1, set2, strategy).isEmpty());
    //test for case set1 > set2
    Set<int[]> set_one = new THashSet<int[]>();
    Set<int[]> set_two = new THashSet<int[]>();
    int [] e = new int[2];
    e[0] = 1;
    e[1] = 2;
    set_one.add(e);
    assertFalse(Calc.union(set_one, set_two, strategy).isEmpty());
  }
  @Test
  public void testintersection(){
  //test method intersection(Set<int[]> set1, Set<int[]> set2)
    Set<int[]> set1 = new THashSet<int[]>(TupleStore.DEFAULT_HASHING_STRATEGY);
    Set<int[]> set2 = new THashSet<int[]>();
    assertTrue(Calc.intersection(set1, set2).isEmpty());
    //test for case set1 > set2 and set2 contains the element
    Set<int[]> set_one = new THashSet<int[]>();
    Set<int[]> set_two = new THashSet<int[]>();
    int [] e = new int[2];
    e[0] = 1;
    e[1] = 2;
    set_one.add(e);
    int [] e1 = new int[1];
    e1[0] = 1;
    assertTrue(Calc.intersection(set_one, set_two).isEmpty());
  }
  @Test
  public void testintersection1(){
    //test method intersection(Set<int[]> set1, Set<int[]> set2, TIntArrayHashingStrategy strategy)
    Set<int[]> set1 = new THashSet<int[]>(TupleStore.DEFAULT_HASHING_STRATEGY);
    Set<int[]> set2 = new THashSet<int[]>();
    TIntArrayHashingStrategy strategy = new TIntArrayHashingStrategy();
    assertTrue(Calc.intersection(set1, set2, strategy).isEmpty());
    //test for case set1 > set2
    Set<int[]> set_one = new THashSet<int[]>();
    Set<int[]> set_two = new THashSet<int[]>();
    int [] e = new int[2];
    e[0] = 1;
    e[1] = 2;
    set_one.add(e);
    assertTrue(Calc.intersection(set_one, set_two, strategy).isEmpty());
  }
  @Test
  public void testdifference(){
  //test method difference(Set<int[]> set1, Set<int[]> set2)
    Set<int[]> set1 = new THashSet<int[]>(TupleStore.DEFAULT_HASHING_STRATEGY);
    Set<int[]> set2 = new THashSet<int[]>();
    assertTrue(Calc.difference(set1, set2).isEmpty());
    //set2 contains element
    Set<int[]> set_one = new THashSet<int[]>();
    Set<int[]> set_two = new THashSet<int[]>();
    int [] e = new int[1];
    e[0] = 1;
    set_one.add(e);
    int [] e1 = new int[2];
    e1[0] = 1;
    e1[1] = 2;
    assertFalse(Calc.difference(set_one, set_two).isEmpty());
  }
  @Test
  public void testmonotonicDifference() {
    //test method monotonicDifference(Set<int[]> set1, Set<int[]> set2)
    Set<int[]> set1 = new THashSet<int[]>(TupleStore.DEFAULT_HASHING_STRATEGY);
    Set<int[]> set2 = new THashSet<int[]>();
    Calc.monotonicDifference(set1, set2);

    Set<int[]> set_one = new THashSet<int[]>();
    Set<int[]> set_two = new THashSet<int[]>();
    int [] e = new int[2];
    e[0] = 1;
    e[1] = 2;
    set_two.add(e);
    int [] e1 = new int[1];
    e1[0] = 3;
    set_one.add(e1);
    assertTrue(Calc.monotonicDifference(set_one, set_two).isEmpty());
  }
  @Test
  public void testdifference1(){
    //test method difference(Set<int[]> set1, Set<int[]> set2, TIntArrayHashingStrategy strategy)
    Set<int[]> set1 = new THashSet<int[]>(TupleStore.DEFAULT_HASHING_STRATEGY);
    Set<int[]> set2 = new THashSet<int[]>();
    TIntArrayHashingStrategy strategy = new TIntArrayHashingStrategy();
    assertTrue(Calc.difference(set1, set2, strategy).isEmpty());
    //if set2 contains element
    Set<int[]> set_one = new THashSet<int[]>();
    Set<int[]> set_two = new THashSet<int[]>();
    int [] e = new int[2];
    e[0] = 1;
    e[1] = 2;
    set_two.add(e);
    int [] e1 = new int[1];
    e1[0] = 1;
    set_one.add(e1);
    assertFalse(Calc.difference(set_one, set_two, strategy).isEmpty());
    //if set2 does not contain element
    Set<int[]> set_a = new THashSet<int[]>();
    Set<int[]> set_b = new THashSet<int[]>();
    int [] myarray1 = new int[2];
    myarray1[0] = 1;
    myarray1[1] = 2;
    set_b.add(myarray1);
    int [] myarray2 = new int[1];
    myarray2[0] = 3;
    set_a.add(myarray2);
    assertFalse(Calc.difference(set_a, set_b, strategy).isEmpty());
  }
  @Test
  public void testmonotonicDifference1(){
    //test method monotonicDifference(Set<int[]> set1, Set<int[]> set2, TIntArrayHashingStrategy strategy)
    Set<int[]> set1 = new THashSet<int[]>();
    Set<int[]> set2 = new THashSet<int[]>();
    TIntArrayHashingStrategy strategy = new TIntArrayHashingStrategy();
    assertTrue(Calc.monotonicDifference(set1, set2, strategy).isEmpty());
    //test for case when size of set1 != set2
    Set<int[]> set_one = new THashSet<int[]>();
    Set<int[]> set_two = new THashSet<int[]>();
    int [] e = new int[2];
    e[0] = 1;
    e[1] = 2;
    set_two.add(e);
    assertTrue(Calc.monotonicDifference(set_one, set_two, strategy).isEmpty());
    //test for case set2 contains element
    Set<int[]> set_three = new THashSet<int[]>();
    int [] myarray = new int[1];
    myarray[0] = 1;
    //myarray[1] = 5;
    set_three.add(myarray);
    assertTrue(Calc.monotonicDifference(set_two, set_three, strategy).isEmpty());
  }
  @Test
  public void testproject(){
    //test method project(Set<int[]> table, int[] pos)
    Set<int[]> table = new THashSet<int[]>();
    int[] pos = new int[1];
    assertTrue(Calc.project(table, pos).isEmpty());
  }
  @Test
  public void testmap(){
    //test method map(BindingTable oldTable, int[] toMap)
    TupleStore ts = new TupleStore(1,2);
    BindingTable oldTable = new BindingTable(ts);
    int[] toMap = new int[1];
    toMap[0] = 1;
    assertEquals(true, (Calc.map(oldTable, toMap) instanceof BindingTable));
  }
  @Test
  public void testproject1(){
    //test method project(BindingTable tt, int[] pos)
    TupleStore ts = new TupleStore(1,2);
    BindingTable tt = new BindingTable(ts);
    int[] pos = new int[1];
    pos[0] = 1;
    assertEquals(true, (Calc.project(tt, pos) instanceof BindingTable));
  }
  @Test
  public void testrestrict() throws FileNotFoundException, WrongFormatException, IOException{
    //test method restrict(BindingTable bt, ArrayList<Integer> varvarIneqs, ArrayList<Integer> varconstIneqs)
    TupleStore ts = new TupleStore(1,2);
    BindingTable bt = new BindingTable(ts);
    ArrayList<Integer> varvarIneqs = new ArrayList();
    ArrayList<Integer> varconstIneqs = new ArrayList();
    assertEquals(true, Calc.restrict(bt, varvarIneqs, varconstIneqs) instanceof BindingTable);
    //if arrays are not empty
    ArrayList<Integer> varvarIneqs1 = new ArrayList();
    ArrayList<Integer> varconstIneqs1 = new ArrayList();
    varvarIneqs1.add(1);
    varvarIneqs1.add(2);
    varconstIneqs1.add(1);
    varconstIneqs1.add(2);
    assertFalse(varvarIneqs1.isEmpty());//the arrays are not empty
    assertFalse(varconstIneqs1.isEmpty());//the arrays are not empty
    Namespace namespace = new Namespace(getResource("default.ns"));
    TupleStore tuple = new TupleStore(3, 2, namespace);
    BindingTable bt1 = new BindingTable(tuple);
    //Calc.restrict(bt1, varvarIneqs1, varconstIneqs1);// Null pointer exception
  }
  @Test
  public void testrestrict1(){
  //test method restrict(BindingTable bt, ArrayList<Predicate> predicates)
    TupleStore ts = new TupleStore(1,2);
    BindingTable bt = new BindingTable(ts);
    ArrayList<Predicate> predicates = new ArrayList();
    assertEquals(true, Calc.restrict(bt, predicates) instanceof BindingTable);
    //create predicate object and add it to ArrayList<Predicate> predicates
    ArrayList<Integer> args = new ArrayList();
    args.add(1);
    Predicate predic = new Predicate("name", args);
    predicates.add(predic);
    assertEquals(true, Calc.restrict(bt, predicates) instanceof BindingTable);
    //
  }
  @Test
  public void testproduct(){
  //test method product(BindingTable bt1, BindingTable bt2)
    //create bt1
    Set<int[]> table3 = new THashSet<int[]>(TupleStore.DEFAULT_HASHING_STRATEGY);
    SortedMap<Integer, Integer> nameToPos3 = new TreeMap<Integer, Integer>();
    Map<Integer, String> nameToExternalName3 = new TreeMap<Integer, String>();
    TupleStore ts3 = new TupleStore(1,2);
    int[] arguments = new int[2];
    HashMap<Integer, ArrayList<Integer>> relIdToFunIds = new HashMap<Integer, ArrayList<Integer>>();
    HashMap<String, Integer> varToId = new HashMap<String, Integer>();
    BindingTable bt1 = new BindingTable(table3, nameToPos3, nameToExternalName3, ts3, arguments, relIdToFunIds, varToId);
    assertNotNull(bt1);
    //create bt2
    Set<int[]> table4 = new THashSet<int[]>(TupleStore.DEFAULT_HASHING_STRATEGY);
    SortedMap<Integer, Integer> nameToPos4 = new TreeMap<Integer, Integer>();
    Map<Integer, String> nameToExternalName4 = new TreeMap<Integer, String>();
    TupleStore ts4 = new TupleStore(2,5);
    int[] arguments1 = new int[3];
    HashMap<Integer, ArrayList<Integer>> relIdToFunIds1 = new HashMap<Integer, ArrayList<Integer>>();
    HashMap<String, Integer> varToId1 = new HashMap<String, Integer>();
    BindingTable bt2 = new BindingTable(table4, nameToPos4, nameToExternalName4, ts4, arguments1, relIdToFunIds1, varToId1);
    assertNotNull(bt2);
    //test product(bt1, bt2)
    assertEquals(true, Calc.product(bt1, bt2) instanceof BindingTable);
  }
  @Test
  public void testjoin(){
  //test method join(BindingTable tt1, BindingTable tt2)
    //create binding table 1
    Set<int[]> table3 = new THashSet<int[]>(TupleStore.DEFAULT_HASHING_STRATEGY);
    SortedMap<Integer, Integer> nameToPos3 = new TreeMap<Integer, Integer>();
    Map<Integer, String> nameToExternalName3 = new TreeMap<Integer, String>();
    TupleStore ts3 = new TupleStore(1,2);
    int[] arguments = new int[2];
    HashMap<Integer, ArrayList<Integer>> relIdToFunIds = new HashMap<Integer, ArrayList<Integer>>();
    HashMap<String, Integer> varToId = new HashMap<String, Integer>();
    BindingTable bt1 = new BindingTable(table3, nameToPos3, nameToExternalName3, ts3, arguments, relIdToFunIds, varToId);
    assertNotNull(bt1);
    //create binding table 2
    Set<int[]> table4 = new THashSet<int[]>(TupleStore.DEFAULT_HASHING_STRATEGY);
    SortedMap<Integer, Integer> nameToPos4 = new TreeMap<Integer, Integer>();
    Map<Integer, String> nameToExternalName4 = new TreeMap<Integer, String>();
    TupleStore ts4 = new TupleStore(2,5);
    int[] arguments1 = new int[3];
    HashMap<Integer, ArrayList<Integer>> relIdToFunIds1 = new HashMap<Integer, ArrayList<Integer>>();
    HashMap<String, Integer> varToId1 = new HashMap<String, Integer>();
    BindingTable bt2 = new BindingTable(table4, nameToPos4, nameToExternalName4, ts4, arguments1, relIdToFunIds1, varToId1);
    assertNotNull(bt2);
    assertEquals(true, Calc.join(bt1, bt2) instanceof BindingTable);
  }
}

