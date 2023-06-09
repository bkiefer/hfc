package de.dfki.lt.hfc.operators;

import de.dfki.lt.hfc.BooleanOperator;

/**
 * a SameAsTest call can be used for replacing a sameAs LHS pattern in a rule;
 * this might happen when equivalence class reduction is turned on
 *
 * @author (C) Hans-Ulrich Krieger
 * @version Wed Sep  8 13:50:59 CEST 2010
 * @test SameAsTest ?x ?y
 * when the rule is read in
 * <p>
 * NOTE: the apply() call is always given instantiations to the variables
 * @see TupleStore.equivalenceClassReduction
 * and a rule, such as
 * ?x <owl:sameAs> ?y
 * ?x <owl:differentFrom> ?y
 * ->
 * ?x <rdf:type> <owl:Nothing>
 * ?y <rdf:type> <owl:Nothing>
 * is rewritten to
 * ?x <owl:differentFrom> ?y
 * ->
 * ?x <rdf:type> <owl:Nothing>
 * ?y <rdf:type> <owl:Nothing>
 * @see de.dfki.lt.hfc.FunctionalOperator
 * @see de.dfki.lt.hfc.operators.SameAsAction
 * for handling sameAs patterns on the RHS of a rule
 * @since JDK 1.5
 */
public final class SameAsTest extends BooleanOperator {

  /**
   * checks whether the first and the second arg have the same proxy and the
   * same equivalence relation
   */
  protected boolean holds(int[] args) {
    return getProxy(args[0]) == getProxy(args[1]) &&
            getRelation(args[0]) == getRelation(args[1]);
  }

}
