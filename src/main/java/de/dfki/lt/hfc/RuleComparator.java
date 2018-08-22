package de.dfki.lt.hfc;

import java.util.Comparator;

/**
 * compares two rules using the value of the priority field
 *
 * @author (C) Hans-Ulrich Krieger
 * @version Thu Dec 17 14:55:46 CET 2009
 * @since JDK 1.5
 */
public class RuleComparator implements Comparator<Rule> {

  public int compare(Rule rule1, Rule rule2) {
    return rule1.priority - rule2.priority;
  }

}