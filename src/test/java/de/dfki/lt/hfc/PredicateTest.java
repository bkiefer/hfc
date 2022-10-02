package de.dfki.lt.hfc;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;

import org.junit.Test;

public class PredicateTest {
  @Test
  public void testconstructor1(){
    //test constructor Predicate(String name,ArrayList<Integer> args)
    String name = "name";
    ArrayList<Integer> args = new ArrayList<>();
    Predicate predicate = new Predicate(name, args);
    assertNotNull(predicate);
  }
  @Test
  public void testconstructor2() {
  //test constructor Predicate(String name, Operator op, ArrayList<Integer> args)
    String name = "name";
    Operator op = null;
    ArrayList<Integer> args = new ArrayList<>();
    Predicate predicate = new Predicate(name, op, args);
    assertNotNull(predicate);
  }
  @Test
  public void testconstructor3(){
  //test constructor Predicate(String name, Operator op, ArrayList<Integer> args,
  //HashMap<Integer, ArrayList<Integer>> relIdToFunIds)
    String name = "name";
    Operator op = null;
    ArrayList<Integer> args = new ArrayList<>();
    HashMap<Integer, ArrayList<Integer>> relIdToFunIds = new HashMap<>();
    Predicate predicate = new Predicate(name, op, args, relIdToFunIds);
    assertNotNull(predicate);
  }
  @Test
  public void testconstructor4() {
    //test constructor Predicate(String name, Operator op, ArrayList<Integer> args,HashMap<Integer,
    //ArrayList<Integer>> relIdToFunIds, HashMap<String, Integer> varToId)
    String name = "name";
    Operator op = null;
    ArrayList<Integer> args = new ArrayList<>();
    args.add(1);
    HashMap<Integer, ArrayList<Integer>> relIdToFunIds = new HashMap<>();
    HashMap<String, Integer> varToId = new HashMap<>();
    Predicate predicate = new Predicate(name, op, args, relIdToFunIds, varToId);
    assertNotNull(predicate);
  }

}
