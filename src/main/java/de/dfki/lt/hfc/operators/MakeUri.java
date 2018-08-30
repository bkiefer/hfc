package de.dfki.lt.hfc.operators;

import de.dfki.lt.hfc.FunctionalOperator;
import de.dfki.lt.hfc.types.Uri;
import de.dfki.lt.hfc.types.XsdString;

/**
 * given an arbitrary number of input args (int ids), encoding objects of XSD type
 * string, this operator deterministically generates an URI by appending the args
 * into a new string that is `surrounded' by '<' and `>'
 *
 * @author (C) Hans-Ulrich Krieger
 * @version Mon Jan 25 12:20:35 CET 2010
 * @see FunctionalOperator
 * @see MakeUriFromUri for a more generalized operator (which is slightly slower)
 * @since JDK 1.5
 */
public final class MakeUri extends FunctionalOperator {

  /**
   * input args are REQUIRED to be representations of type xsd:string
   */
  public int apply(int[] args) {
    StringBuilder sb = new StringBuilder("<");
    XsdString string;
    for (int i = 0; i < args.length; i++) {
      string = (XsdString) getObject(args[i]);
      sb.append(string.value);
    }
    sb.append(">");
    final Uri uri = new Uri(sb.toString());
    return registerObject(uri.toString(), uri);
  }

}
