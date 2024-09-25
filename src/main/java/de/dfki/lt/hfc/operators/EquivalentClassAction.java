package de.dfki.lt.hfc.operators;

import de.dfki.lt.hfc.FunctionalOperator;
import de.dfki.lt.hfc.NamespaceManager;

/**
 * an EquivalentClassAction call can be used for replacing an equivalentClass RHS
 * pattern in a rule;
 * this might happen when equivalence class reduction is turned on
 *
 * @author (C) Hans-Ulrich Krieger
 * @version Wed Sep  8 15:45:33 CEST 2010
 * @test ?y != ?z
 * @action ?__actionBinder-... = EquivalentClassAction ?y ?z
 * <p>
 * note that we need an _action_ here (and can _not_ implement this functionality by
 * a _test_ that always returns true), because otherwise we would end up in an empty
 * consequent;
 * the return value is the proxy for the values stored under ?y and ?z
 * <p>
 * NOTE: the apply() call is always given instantiations to the variables
 * <p>
 * NOTE: it is very unlikely that we have such a rule often in practice since it
 * will change the TBox at runtime, and so modifies the ABox as a result;
 * such rules, however, might occur during ontology learning or when two
 * ontologies are interfaced/merged in an offline process
 * @see TupleStore.equivalenceClassReduction
 * and a rule, such as
 * ...
 * ...
 * ->
 * ?x <owl:equivalentClass> ?y
 * is rewritten to
 * ...
 * ...
 * ->
 * ?__actionBinder-... <owl:equivalentClass> ?__actionBinder-...
 * @since JDK 1.5
 */
public final class EquivalentClassAction extends FunctionalOperator {

  /**
   * associates the first and the second arg with its proxy (if already generated);
   * otherwise first arg is regarded as the proxy for first and second arg;
   * furthermore, an association between the equivalence relation (here: equivalentClass)
   * and the first and second arg is established
   */
  public int apply(int[] args) {
    setProxy(args[0], args[1]);
    setRelation(args[0], NamespaceManager.OWL_EQUIVALENTCLASS_ID);
    setRelation(args[1], NamespaceManager.OWL_EQUIVALENTCLASS_ID);
    return getProxy(args[0]);
  }

}
