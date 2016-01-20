package de.dfki.lt.hfc;

import static org.junit.Assert.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.junit.Test;

public class RuleComparatorTest {

	@Test
	//TODO
	public void testRuleComparator() {
		//test method compare(Rule rule1, Rule rule2)
		TupleStore ts = new TupleStore(1, 3);
		Namespace namespace = new Namespace("D:/DFKI/hfc/src/main/resources/default.ns");
		RuleStore rs = new RuleStore(namespace, ts);
		int[][] ante = new int[1][2];
		int[][] cons = new int[2][3];
		Rule rule1 = new Rule("name", ante, cons, ts, rs);
		Rule rule2 = new Rule("newname", ante, cons, ts, rs);
		ArrayList rules = new ArrayList();
		rules.add(rule1);
		rules.add(rule2);
		//Collections.sort(rules, new Comparator<Rule>());
		Collections.sort(rules);
		//for (int i = 0; i < rules.size(); i++){
            //System.out.println(rules.get(i));
		//}
	}

}
