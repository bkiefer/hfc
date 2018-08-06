package de.dfki.lt.hfc;

import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import org.junit.Test;

public class RuleTest {

  @Test
  public void testRule() throws FileNotFoundException, WrongFormatException, IOException {
    //test the constructor Rule(String name, int[][] ante, int[][] cons, TupleStore tstore, RuleStore rstore)
    TupleStore ts = new TupleStore(Config.getDefaultConfig());
    RuleStore rs = new RuleStore(Config.getDefaultConfig(),ts);
    int[][] ante = new int[1][2];
    int[][] cons = new int[2][3];
    Rule r = new Rule("name", ante, cons, ts, rs);
    assertNotNull(r);
  }

  @Test
  public void testsetName() throws FileNotFoundException, WrongFormatException, IOException {
    TupleStore ts = new TupleStore(Config.getDefaultConfig());
    RuleStore rs = new RuleStore(Config.getDefaultConfig(),ts);
    int[][] ante = new int[1][2];
    int[][] cons = new int[1][2];
    Rule r = new Rule("name", ante, cons, ts, rs);
    r.setName("name");
    assertEquals(r.getName(), "name");
  }

  @Test
  public void testsetAntecedent() throws FileNotFoundException, WrongFormatException, IOException {
    //test method that takes int[][] ante
    TupleStore ts = new TupleStore(Config.getDefaultConfig());
    RuleStore rs = new RuleStore(Config.getDefaultConfig(),ts);
    int[][] ante = new int[1][2];
    int[][] cons = new int[1][2];
    Rule r = new Rule("name", ante, cons, ts, rs);
    r.setAntecedent(ante);
    assertNotNull(r.getAntecedent());
  }

  @Test
  public void testsetAntecedent1() throws FileNotFoundException, WrongFormatException, IOException {
    //test method that takes ArrayList<int[]> anteList
    ArrayList<int[]> anteList = new ArrayList<int[]>();
    anteList.add(null);
    TupleStore ts = new TupleStore(Config.getDefaultConfig());
    RuleStore rs = new RuleStore(Config.getDefaultConfig(),ts);
    int[][] ante = new int[1][2];
    int[][] cons = new int[1][2];
    Rule r = new Rule("name", ante, cons, ts, rs);
    r.setAntecedent(anteList);
    assertNull(r.getAntecedent(0));
  }

  @Test
  public void testsetConsequent() throws FileNotFoundException, WrongFormatException, IOException {
    //test method setConsequent(int[][] cons)
    TupleStore ts = new TupleStore(Config.getDefaultConfig());
    RuleStore rs = new RuleStore(Config.getDefaultConfig(),ts);
    int[][] ante = new int[1][2];
    int[][] cons = new int[2][3];
    Rule r = new Rule("name", ante, cons, ts, rs);
    r.setConsequent(cons);
    assertNotNull(r.getConsequent());
  }

  @Test
  public void testsetConsequent1() throws FileNotFoundException, WrongFormatException, IOException {
    //test method setConsequent(ArrayList<int[]> consList)
    ArrayList<int[]> consList = new ArrayList<int[]>();
    consList.add(null);
    TupleStore ts = new TupleStore(Config.getDefaultConfig());
    RuleStore rs = new RuleStore(Config.getDefaultConfig(),ts);
    int[][] ante = new int[1][2];
    int[][] cons = new int[2][3];
    Rule r = new Rule("name", ante, cons, ts, rs);
    r.setConsequent(consList);
    assertNull(r.getConsequent(0));
  }

  @Test
  public void testsetTupleStore() throws FileNotFoundException, WrongFormatException, IOException {
    //test method setTupleStore(TupleStore tstore)
    TupleStore tstore = new TupleStore(Config.getDefaultConfig());
    RuleStore rs = new RuleStore(Config.getDefaultConfig(),tstore);
    int[][] ante = new int[1][2];
    int[][] cons = new int[2][3];
    Rule r = new Rule("name", ante, cons, tstore, rs);
    r.setTupleStore(tstore);
    assertNotNull(r.getTupleStore());
  }

  @Test
  public void testsetRuleStore() throws FileNotFoundException, WrongFormatException, IOException {
    //test method setRuleStore(RuleStore rstore)
    TupleStore tstore = new TupleStore(Config.getDefaultConfig());
    RuleStore rs = new RuleStore(Config.getDefaultConfig(), tstore);
    int[][] ante = new int[1][2];
    int[][] cons = new int[2][3];
    Rule r = new Rule("name", ante, cons, tstore, rs);
    r.setRuleStore(rs);
    assertNotNull(r.getRuleStore());
  }

  @Test
  public void testtoString() throws FileNotFoundException, WrongFormatException, IOException {
    //test method toString()
    TupleStore tstore = new TupleStore(Config.getDefaultConfig());
    RuleStore rs = new RuleStore(Config.getDefaultConfig(), tstore);
    int[][] ante = new int[1][2];
    int[][] cons = new int[2][3];
    Rule r = new Rule("name", ante, cons, tstore, rs);
    String test = new String("");
    r.toString();
    test = r.toString().substring(0, 4);
    assertEquals(test, "name");

  }

}
