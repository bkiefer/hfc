package de.dfki.lt.hfc.operators;

import de.dfki.lt.hfc.FunctionalOperator;
import de.dfki.lt.hfc.Namespace;

/**
 * NoSubClassOf checks whether the tuple store does NOT _contain_ a rdfs:subClassOf
 * statement between the two arguments given to apply()
 *
 * NOTE: this predicate does in fact check for the _non-existence_ of information,
 *       thus when used during forward chaining in a rule (e.g., integrity constraint)
 *       might lead to wrong information -- thus it is important to properly apply
 *       the constraint at the 'right' time
 *
 * EXAMPLE: $domainRestrictionViolated
 *          ?p <rdfs:domain> ?d
 *          ?s ?p ?o
 *          ?s <rdf:type> ?t
 *          ->
 *          ?s <rdf:type> <owl:Nothing>
 *          @test
 *          NoSubClassOf ?t ?d
 *
 * NOTE: the positive case can be easily checked wo/ a predicate by using the triple
 *         ?t <rdfs:subClassOf> ?d
 *       assuming that the transitive closure has been computed, i.e., subclass hierarchy
 *       has been totally materialized
 *
 * IMPORTANT: since this constraint should be applied at the very end or in a separate
 *            reasoning stage, we do _only_ check whether
 *              ?t <rdfs:subClassOf> ?d
 *             is NOT present (and not whether there is a _chain_ of subClassOf stattements)
 *
 * @see de.dfki.lt.hfc.FunctionalOperator
 * @see de.dfki.lt.hfc.operators.IsNotSubtypeOf
 *      an operator that does address the open-world nature of OWL
 *
 * @author (C) Hans-Ulrich Krieger
 * @since JDK 1.5
 * @version Wed Sep 23 16:44:37 CEST 2009
 */
public final class NoSubClassOf extends FunctionalOperator {
	
	/**
	 * since this constraint is usually only applied at the very end of forward chaining
	 * or in a separate reasoning stage, we do _only_ check whether
	 *              ?t <rdfs:subClassOf> ?d
	 * is NOT present, assuming that total materialization has made the subclass hierarchy
	 * explicit
	 */
	public int apply(int[] args) {
		// do we need to check whether '<rdfs:subClassOf>' is a valid literal?  NO!
		//if (id == -1)
		//	return FunctionalOperator.FALSE;
		return ask(new int[] {args[0], Namespace.RDFS_SUBCLASSOF_ID, args[1]}) ? FunctionalOperator.FALSE : FunctionalOperator.TRUE;
	}

}