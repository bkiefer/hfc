package de.dfki.lt.hfc.operators;

import de.dfki.lt.hfc.FunctionalOperator;
import de.dfki.lt.hfc.types.XsdDecimal;
import de.dfki.lt.hfc.types.XsdDouble;
import java.util.Arrays;

/**
 * @author Christian Willms - Date: 28.11.17 13:24.
 * @version 28.11.17
 */
public class Q5Filter extends FunctionalOperator {

  /**
   * note that apply() does NOT check at the moment whether the int args represent in fact XSD
   * longs; note that apply() does NOT check whether it is given exactly two arguments
   *
   * This is ugly, but there was no other way to implement this, as in the citytraffic ontology
   * xsd:decimal and xsd:double were used inconsistently.
   */
  public int apply(int[] args) {
    Object la1 = getObject(args[0]);
    Object lo1 = getObject(args[1]);
    Object la2 = getObject(args[2]);
    Object lo2 = getObject(args[3]);
    double lat1 = la1 instanceof XsdDecimal? ((XsdDecimal) la1).value: ((XsdDouble)la1).value;
    double lon1 = lo1 instanceof XsdDecimal? ((XsdDecimal) lo1).value: ((XsdDouble)lo1).value;
    double lat2 = la2 instanceof XsdDecimal? ((XsdDecimal) la2).value: ((XsdDouble)la2).value;
    double lon2 = lo2 instanceof XsdDecimal? ((XsdDecimal) lo2).value: ((XsdDouble)lo2).value;


    return ((lat2-lat1)*(lat2-lat1)+(lon2-lon1)*(lon2-lon1))<0.1
        ? FunctionalOperator.TRUE : FunctionalOperator.FALSE;
  }

}
