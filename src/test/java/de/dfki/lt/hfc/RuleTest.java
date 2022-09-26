package de.dfki.lt.hfc;

import static de.dfki.lt.hfc.TestingUtils.getStore;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class RuleTest {
  
  TupleStore ts;
  RuleStore rs;
  
  @Before
  public void getDefaultStore() {
    try {
      ts = getStore(Config.getDefaultConfig());
      rs = new RuleStore(ts);
     } catch (Exception ex) {
      throw new RuntimeException(ex);
    }
  }
  
  @After
  public void down() {
    ts = null; 
    rs = null;
  }
  
  @Test
  public void testRule() {
    //test the constructor Rule(String name, int[][] ante, int[][] cons, TupleStore tstore, RuleStore rstore)
    int[][] ante = new int[1][2];
    int[][] cons = new int[2][3];
    Rule r = new Rule("name", ante, cons, ts, rs);
    assertNotNull(r);
  }

  @Test
  public void testsetName() {
    int[][] ante = new int[1][2];
    int[][] cons = new int[1][2];
    Rule r = new Rule("name", ante, cons, ts, rs);
    r.setName("name");
    assertEquals(r.getName(), "name");
  }

  @Test
  public void testsetAntecedent() {
    //test method that takes int[][] ante
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
    int[][] ante = new int[1][2];
    int[][] cons = new int[1][2];
    Rule r = new Rule("name", ante, cons, ts, rs);
    r.setAntecedent(anteList);
    assertNull(r.getAntecedent(0));
  }

  @Test
  public void testsetConsequent() {
    //test method setConsequent(int[][] cons)
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
    int[][] ante = new int[1][2];
    int[][] cons = new int[2][3];
    Rule r = new Rule("name", ante, cons, ts, rs);
    r.setConsequent(consList);
    assertNull(r.getConsequent(0));
  }

  @Test
  public void testsetTupleStore() {
    //test method setTupleStore(TupleStore tstore)
    int[][] ante = new int[1][2];
    int[][] cons = new int[2][3];
    Rule r = new Rule("name", ante, cons, ts, rs);
    r.setTupleStore(ts);
    assertNotNull(r.getTupleStore());
  }

  @Test
  public void testsetRuleStore() {
    //test method setRuleStore(RuleStore rstore)
    int[][] ante = new int[1][2];
    int[][] cons = new int[2][3];
    Rule r = new Rule("name", ante, cons, ts, rs);
    r.setRuleStore(rs);
    assertNotNull(r.getRuleStore());
  }

  @Test
  public void testtoString() throws FileNotFoundException, WrongFormatException, IOException {
    //test method toString()
    int[][] ante = new int[1][2];
    int[][] cons = new int[2][3];
    Rule r = new Rule("name", ante, cons, ts, rs);
    String test = new String("");
    r.toString();
    test = r.toString().substring(0, 4);
    assertEquals(test, "name");
  }

}
