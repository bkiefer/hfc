package de.dfki.lt.hfc;

import static org.junit.Assert.*;

import static de.dfki.lt.hfc.TestUtils.getResource;

import java.util.ArrayList;

import org.junit.Test;

public class RuleTest {

  @Test
  public void testRule() {
    //test the constructor Rule(String name, int[][] ante, int[][] cons, TupleStore tstore, RuleStore rstore)
    TupleStore ts = new TupleStore(1, 3);
    Namespace namespace = new Namespace(getResource("default.ns"));
    RuleStore rs = new RuleStore(namespace, ts);
    int[][] ante = new int[1][2];
    int[][] cons = new int[2][3];
    Rule r = new Rule("name", ante, cons, ts, rs);
    assertNotNull(r);
  }

  @Test
  public void testsetName() {
    TupleStore ts = new TupleStore(1, 3);
    Namespace namespace = new Namespace(getResource("default.ns"));
    RuleStore rs = new RuleStore(namespace, ts);
    int[][] ante = new int[1][2];
    int[][] cons = new int[1][2];
    Rule r = new Rule("name", ante, cons, ts, rs);
    r.setName("name");
    assertEquals(r.getName(), "name");
  }

  @SuppressWarnings("deprecation")
  @Test
  public void testsetAntecedent() {
    //test method that takes int[][] ante
    TupleStore ts = new TupleStore(1, 3);
    Namespace namespace = new Namespace(getResource("default.ns"));
    RuleStore rs = new RuleStore(namespace, ts);
    int[][] ante = new int[1][2];
    int[][] cons = new int[1][2];
    Rule r = new Rule("name", ante, cons, ts, rs);
    r.setAntecedent(ante);
    assertNotNull(r.getAntecedent());
  }

  @Test
  public void testsetAntecedent1() {
    //test method that takes ArrayList<int[]> anteList
    ArrayList<int[]> anteList = new ArrayList<int[]>();
    anteList.add(null);
    TupleStore ts = new TupleStore(1, 3);
    Namespace namespace = new Namespace(getResource("default.ns"));
    RuleStore rs = new RuleStore(namespace, ts);
    int[][] ante = new int[1][2];
    int[][] cons = new int[1][2];
    Rule r = new Rule("name", ante, cons, ts, rs);
    r.setAntecedent(anteList);
    assertNull(r.getAntecedent(0));
  }

  @Test
  public void testsetConsequent() {
    //test method setConsequent(int[][] cons)
    TupleStore ts = new TupleStore(1, 3);
    Namespace namespace = new Namespace(getResource("default.ns"));
    RuleStore rs = new RuleStore(namespace, ts);
    int[][] ante = new int[1][2];
    int[][] cons = new int[2][3];
    Rule r = new Rule("name", ante, cons, ts, rs);
    r.setConsequent(cons);
    assertNotNull(r.getConsequent());
  }

  @Test
  public void testsetConsequent1() {
    //test method setConsequent(ArrayList<int[]> consList)
    ArrayList<int[]> consList = new ArrayList<int[]>();
    consList.add(null);
    TupleStore ts = new TupleStore(1, 3);
    Namespace namespace = new Namespace(getResource("default.ns"));
    RuleStore rs = new RuleStore(namespace, ts);
    int[][] ante = new int[1][2];
    int[][] cons = new int[2][3];
    Rule r = new Rule("name", ante, cons, ts, rs);
    r.setConsequent(consList);
    assertNull(r.getConsequent(0));
  }

  @Test
  public void testsetTupleStore() {
    //test method setTupleStore(TupleStore tstore)
    TupleStore tstore = new TupleStore(1, 2);
    Namespace namespace = new Namespace(getResource("default.ns"));
    RuleStore rs = new RuleStore(namespace, tstore);
    int[][] ante = new int[1][2];
    int[][] cons = new int[2][3];
    Rule r = new Rule("name", ante, cons, tstore, rs);
    r.setTupleStore(tstore);
    assertNotNull(r.getTupleStore());
  }

  @Test
  public void testsetRuleStore() {
    //test method setRuleStore(RuleStore rstore)
    TupleStore tstore = new TupleStore(1, 2);
    Namespace namespace = new Namespace(getResource("default.ns"));
    RuleStore rs = new RuleStore(namespace, tstore);
    int[][] ante = new int[1][2];
    int[][] cons = new int[2][3];
    Rule r = new Rule("name", ante, cons, tstore, rs);
    r.setRuleStore(rs);
    assertNotNull(r.getRuleStore());
  }

  @Test
  public void testtoString() {
    //test method toString()
    TupleStore tstore = new TupleStore(1, 2);
    Namespace namespace = new Namespace(getResource("default.ns"));
    RuleStore rs = new RuleStore(namespace, tstore);
    int[][] ante = new int[1][2];
    int[][] cons = new int[2][3];
    Rule r = new Rule("name", ante, cons, tstore, rs);
    String test = new String("");
    r.toString();
    test = r.toString().substring(0, 4);
    assertEquals(test, "name");

  }

}