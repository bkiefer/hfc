package de.dfki.lt.hfc.operators;

import de.dfki.lt.hfc.FunctionalOperator;
import de.dfki.lt.hfc.Namespace;

/**
 * a SameAsTest call can be used for replacing a sameAs LHS pattern in a rule;
 * this might happen when equivalence class reduction is turned on
 *   @see TupleStore.equivalenceClassReduction
 * and a rule, such as
 *   ?x <owl:sameAs> ?y
 *   ?x <owl:differentFrom> ?y
 *   ->
 *   ?x <rdf:type> <owl:Nothing>
 *   ?y <rdf:type> <owl:Nothing>
 * is rewritten to
 *   ?x <owl:differentFrom> ?y
 *   ->
 *   ?x <rdf:type> <owl:Nothing>
 *   ?y <rdf:type> <owl:Nothing>
 *   @test
 *   SameAsTest ?x ?y
 * when the rule is read in
 *
 * NOTE: the apply() call is always given instantiations to the variables
 *
 * @see de.dfki.lt.hfc.FunctionalOperator
 * @see de.dfki.lt.hfc.operators.SameAsAction
 *      for handling sameAs patterns on the RHS of a rule
 *
 * @author (C) Hans-Ulrich Krieger
 * @since JDK 1.5
 * @version Wed Sep  8 13:50:59 CEST 2010
 */
public final class SameAsTest extends FunctionalOperator {
	
	/**
	 * checks whether the first and the second arg have the same proxy and the
	 * same equivalence relation
	 */
	public int apply(int[] args) {
		if (getProxy(args[0]) == getProxy(args[1]) &&
				getRelation(args[0]) == getRelation(args[1]))
			return FunctionalOperator.TRUE;
		else
			return FunctionalOperator.FALSE;
	}
	
}
