package de.dfki.lt.hfc.operators;

import de.dfki.lt.hfc.FunctionalOperator;
import de.dfki.lt.hfc.types.BlankNode;

/**
 * given an arbitrary number of input args (int ids) encoding URIs, blank nodes,
 * or XSD atoms, this operator deterministically generates a new blank node by
 * appending the external representation of the args
 *
 * @author (C) Hans-Ulrich Krieger
 * @version Mon May 30 15:52:06 CEST 2011
 * @see FunctionalOperator
 * @see MakeUri for a more restricted operator (which is slightly faster)
 * @since JDK 1.5
 */
public final class MakeBlankNode extends FunctionalOperator {

  /**
   * input args can be of arbitrary type, either URIs, blank nodes, or XSD atoms;
   * use toName() method required to be implemented by all concrete subclasses of
   * AnyType in order to generate a new blank node;
   * in case an arg is an URI, the angle brackets are omitted;
   * if it is a blank node, the "_:" prefix is deleted;
   * if it is an atom, the surrounding '"' are removed, as well as the XSD type is deleted;
   * if it is even more an XSD string, ' ', '<', and '>' characters are replaced by '_'
   */
  public int apply(int[] args) {
    StringBuilder sb = new StringBuilder("_:|");
    for (int i = 0; i < args.length; i++)
      // separate args by '|' character
      sb.append(getObject(args[i]).toName()).append("|");
    final BlankNode bn = new BlankNode(sb.toString());
    return registerObject(bn.toString(), bn);
  }

}
