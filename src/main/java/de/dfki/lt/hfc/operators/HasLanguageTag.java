package de.dfki.lt.hfc.operators;

import de.dfki.lt.hfc.FunctionalOperator;
import de.dfki.lt.hfc.types.*;

/**
 * checks whether the XSD string (first argument) has a specific language tag (second
 * argument, again an XSD string);
 * remember the three fundamental ways to write down an XSD string:
 *    1 "a string"
 *    2 "a string"^^xsd:string
 *    3 "a string"@en
 *  note that calling the test (via apply below) always returns FunctionalOperator.FALSE
 *  for cases 1 and 2 as _no_ language tag is provided!
 *  note further that we distinguishes between upper and lower case characters and (as far
 *  as I know) that language tags in this settings are always a sequence of two lowercase
 *  characters
 *
 *  call this test with
 *   HasLanguageTag string tag
 *
 * @author (C) Hans-Ulrich Krieger
 * @since JDK 1.5
 * @version Thu Mar 31 15:56:55 CEST 2016
 */
public final class HasLanguageTag extends FunctionalOperator {

  /**
   *
   */
  public int apply(int[] args) {
    final String stringtag = ((XsdString)(getObject(args[0]))).languageTag;
    final String tag = ((XsdString)(getObject(args[1]))).value;
    // cases 1+2
    if (stringtag == null)
      return FunctionalOperator.FALSE;
    // case 3
    if (stringtag.equals(tag))
      return FunctionalOperator.TRUE;
    else
      return FunctionalOperator.FALSE;
  }

}
