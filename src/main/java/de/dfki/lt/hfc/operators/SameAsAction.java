package de.dfki.lt.hfc.operators;

import de.dfki.lt.hfc.FunctionalOperator;
import de.dfki.lt.hfc.Namespace;

/**
 * a SameAsAction call can be used for replacing a sameAs RHS pattern in a rule;
 * this might happen when equivalence class reduction is turned on
 *
 * @author (C) Hans-Ulrich Krieger
 * @version Wed Sep  8 13:50:59 CEST 2010
 * @test ?y != ?z
 * is rewritten to
 * ?p <rdf:type> <owl:FunctionalProperty>
 * ?p <rdf:type> <owl:ObjectProperty>
 * ?x ?p ?y
 * ?x ?p ?z
 * ->
 * ?__actionBinder-... <owl:sameAs> ?__actionBinder-...
 * @test ?y != ?z
 * @action ?__actionBinder-... = SameAsAction ?y ?z
 * <p>
 * note that we need an _action_ here (and can _not_ implement this functionality by
 * a _test_ that always returns true), because otherwise we would end up in an empty
 * consequent;
 * the return value bound to ?__actionBinder-... is the proxy for the values stored
 * under ?y and ?z
 * <p>
 * NOTE: the apply() call is always given instantiations to the variables
 * @see TupleStore.equivalenceClassReduction
 * and a rule, such as
 * ?p <rdf:type> <owl:FunctionalProperty>
 * ?p <rdf:type> <owl:ObjectProperty>
 * ?x ?p ?y
 * ?x ?p ?z
 * ->
 * ?y <owl:sameAs> ?z
 * @see de.dfki.lt.hfc.FunctionalOperator
 * @see de.dfki.lt.hfc.operators.SameAsTest
 * for handling sameAs patterns on the LHS of a rule
 * @since JDK 1.5
 */
public final class SameAsAction extends FunctionalOperator {

  /**
   * associates the first and the second arg with its proxy (if already generated);
   * otherwise first arg is regarded as the proxy for first and second arg;
   * furthermore, an association between the equivalence relation (here: sameAs) and
   * the first and second arg is established
   */
  public int apply(int[] args) {
    setProxy(args[0], args[1]);
    setRelation(args[0], Namespace.OWL_SAMEAS_ID);
    setRelation(args[1], Namespace.OWL_SAMEAS_ID);
    return getProxy(args[0]);
  }

}
