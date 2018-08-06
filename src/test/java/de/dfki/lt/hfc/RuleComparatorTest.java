package de.dfki.lt.hfc;

import static de.dfki.lt.hfc.TestingUtils.getTestResource;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import org.junit.Test;


public class RuleComparatorTest {

  @Test
  //TODO
  public void testRuleComparator() throws FileNotFoundException, WrongFormatException, IOException {
    //test method compare(Rule rule1, Rule rule2)
    Namespace namespace = Namespace.defaultNamespace();
    TupleStore ts = new TupleStore(Config.getDefaultConfig());
    RuleStore rs = new RuleStore(Config.getDefaultConfig(),ts);
    int[][] ante = new int[1][2];
    int[][] cons = new int[2][3];
    Rule rule1 = new Rule("name", ante, cons, ts, rs);
    Rule rule2 = new Rule("newname", ante, cons, ts, rs);
    ArrayList<Rule> rules = new ArrayList<Rule>();
    rules.add(rule1);
    rules.add(rule2);
    //Collections.sort(rules, new Comparator<Rule>());
    Collections.sort(rules, new Comparator<Rule>(){

      public int compare(Rule o1, Rule o2) {
        // TODO Auto-generated method stub
        return 0;
      }});
    //for (int i = 0; i < rules.size(); i++){
    //System.out.println(rules.get(i));
    //}
  }

}
