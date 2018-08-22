package de.dfki.lt.hfc.operators;

import de.dfki.lt.hfc.BooleanOperator;
import de.dfki.lt.hfc.types.XsdString;

/**
 * checks whether the XSD string (first argument) has a specific language tag (second
 * argument, again an XSD string);
 * remember the three fundamental ways to write down an XSD string:
 * 1 "a string"
 * 2 "a string"^^xsd:string
 * 3 "a string"@en
 * note that calling the test (via apply below) _always_ returns BooleanOperator.FALSE
 * for cases 1 and 2 as _no_ language tag is provided!
 * note further that we _distinguishes_ between upper and lower case characters and (as far
 * as I know) that language tags in this settings are always a sequence of two lowercase
 * characters (the latter case is _not_ tested)
 * <p>
 * call this functional operator in queries (FILTER) or rules (@test) with
 * HasLanguageTag xsd-string-via-variable language-tag
 * concrete example:
 * HasLanguageTag ?label "en"
 *
 * @author (C) Hans-Ulrich Krieger
 * @version Thu Mar 31 15:56:55 CEST 2016
 * @since JDK 1.5
 */
public final class HasLanguageTag extends BooleanOperator {

  /**
   *
   */
  protected boolean holds(int[] args) {
    final String stringtag = ((XsdString) (getObject(args[0]))).languageTag;
    final String tag = ((XsdString) (getObject(args[1]))).value;
    // cases 1+2
    return (stringtag != null) && stringtag.equals(tag);
  }

}
