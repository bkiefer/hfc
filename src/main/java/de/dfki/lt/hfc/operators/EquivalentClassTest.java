package de.dfki.lt.hfc.operators;

import de.dfki.lt.hfc.BooleanOperator;

/**
 * an EquivalentClassTest call can be used for replacing an equivalentClass LHS pattern in a rule;
 * this might happen when equivalence class reduction is turned on
 *
 * @author (C) Hans-Ulrich Krieger
 * @version Wed Sep  8 15:21:54 CEST 2010
 * @test EquivalentClassTest ?x ?y
 * when the rule is read in
 * <p>
 * NOTE: the apply() call is always given instantiations to the variables
 * @see TupleStore.equivalenceClassReduction
 * and a rule, such as
 * ?x <owl:equivalentClass> ?y
 * ?x <owl:disjointWith> ?y
 * ->
 * ?x <rdf:type> <owl:Nothing>
 * ?y <rdf:type> <owl:Nothing>
 * is rewritten to
 * ?x <owl:disjointWith> ?y
 * ->
 * ?x <rdf:type> <owl:Nothing>
 * ?y <rdf:type> <owl:Nothing>
 * @see de.dfki.lt.hfc.BooleanOperator
 * @see de.dfki.lt.hfc.operators.EquivalentClassAction
 * for handling equivalentClass patterns on the RHS of a rule
 * @since JDK 1.5
 */
public final class EquivalentClassTest extends BooleanOperator {

  /**
   * checks whether the first and the second arg have the same proxy
   */
  protected boolean holds(int[] args) {
    return getProxy(args[0]) == getProxy(args[1]) &&
            getRelation(args[0]) == getRelation(args[1]);
  }

}
