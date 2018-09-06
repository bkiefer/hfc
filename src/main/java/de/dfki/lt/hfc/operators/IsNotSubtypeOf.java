package de.dfki.lt.hfc.operators;

import de.dfki.lt.hfc.BooleanOperator;
import de.dfki.lt.hfc.NamespaceManager;

/**
 * IsNotSubtypeOf checks whether the two arguments given to apply() are NOT standing in a
 * subtype relation: arg1 <!= arg2;
 * contrary to NoSubClassOf which checks for the non-existence of
 * arg1 <rdfs:subClassOf> arg2
 * IsNotSubtypeOf addresses the open-world nature of OWL:
 * arg1 <!= arg2   <==>
 * !(arg1 <= arg2) <==>
 * (arg1 > arg2) | (arg1 & arg2 = bottom) <==>
 * ((arg1 != arg2) & (arg2 <rdfs:subClassOf> arg1)) | (arg1 <owl:disjointWith> arg2)
 *
 * @author (C) Hans-Ulrich Krieger
 * @version Wed Sep 23 16:44:53 CEST 2009
 * @see de.dfki.lt.hfc.BooleanOperator
 * @see de.dfki.lt.hfc.operators.NoSubClassOf
 * an operator that assumes a closed world, testing for the non-existence of information
 * @since JDK 1.5
 */
public final class IsNotSubtypeOf extends BooleanOperator {

  /**
   *
   */
  protected boolean holds(int[] args) {
    return ((args[0] != args[1]
            && ask(new int[]{args[1], NamespaceManager.RDFS_SUBCLASSOF_ID, args[0]}))
            || ask(new int[]{args[0], NamespaceManager.OWL_DISJOINTWITH_ID, args[1]}));
  }

}
