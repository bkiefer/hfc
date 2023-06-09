package de.dfki.lt.hfc.operators;

import de.dfki.lt.hfc.FunctionalOperator;
import de.dfki.lt.hfc.types.XsdFloat;

import static java.lang.Math.abs;

/**
 * checks whether the first argument is not equal to the second argument;
 * arguments are assumed to be numbers of type xsd:float
 *
 * @author (C) Hans-Ulrich Krieger
 * @version Wed Jun 23 15:19:56 CEST 2010
 * @return FunctionalOperator.TRUE or FunctionalOperator.FALSE
 * @see FunctionalOperator
 * @since JDK 1.5
 */
public final class FNotEqual extends FunctionalOperator {

  /**
   * note that apply() does NOT check at the moment whether the int args
   * represent in fact XSD floats;
   * note that apply() does NOT check whether it is given exactly two arguments
   */
  public int apply(int[] args) {

    // http://www.ibm.com/developerworks/java/library/j-jtp0114/#N10255
    // https://en.wikipedia.org/wiki/Machine_epsilon#Values_for_standard_hardware_floating_point_arithmetics
    float one = ((XsdFloat) getObject(args[0])).value;
    float two = ((XsdFloat) getObject(args[1])).value;
    // recommended value was to small, reason for *100
    double epsilon = 5.96e-08 * 100;
    if (abs(one / two - 1) < epsilon) {
      return FunctionalOperator.FALSE;
    } else {
      return FunctionalOperator.TRUE;
    }
  }

}
