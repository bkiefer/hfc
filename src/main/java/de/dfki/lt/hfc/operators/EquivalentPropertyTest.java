package de.dfki.lt.hfc.operators;

import de.dfki.lt.hfc.BooleanOperator;

/**
 * an EquivalentPropertyTest call can be used for replacing an equivalentProperty LHS pattern in a rule;
 * this might happen when equivalence class reduction is turned on
 *
 * @author (C) Hans-Ulrich Krieger
 * @version Wed Sep  8 15:25:33 CEST 2010
 * @test EquivalentPropertyTest ?x ?y
 * when the rule is read in
 * <p>
 * NOTE: the apply() call is always given instantiations to the variables
 * @see TupleStore.equivalenceClassReduction
 * and a rule, such as
 * ?x <owl:equivalentProperty> ?y
 * ?x <owl:disjointProperty> ?y
 * ->
 * ?x <rdf:type> <owl:Nothing>
 * ?y <rdf:type> <owl:Nothing>
 * is rewritten to
 * ?x <owl:disjointProperty> ?y
 * ->
 * ?x <rdf:type> <owl:Nothing>
 * ?y <rdf:type> <owl:Nothing>
 * @see de.dfki.lt.hfc.BooleanOperator
 * @see de.dfki.lt.hfc.operators.EquivalentPropertyAction
 * for handling equivalentProperty patterns on the RHS of a rule
 * @since JDK 1.5
 */
public final class EquivalentPropertyTest extends BooleanOperator {

  /**
   * checks whether the first and the second arg have the same proxy
   */
  protected boolean holds(int[] args) {
    return getProxy(args[0]) == getProxy(args[1]) &&
            getRelation(args[0]) == getRelation(args[1]);
  }

}
