package de.dfki.lt.hfc.operators;

import de.dfki.lt.hfc.BooleanOperator;
import de.dfki.lt.hfc.types.XsdFloat;
import static java.lang.Math.abs;

/**
 * checks whether the first argument is equal to the second argument;
 * arguments are assumed to be numbers of type xsd:float
 *
 * @return true or false
 *
 * @see BooleanOperator
 *
 * @author (C) Hans-Ulrich Krieger
 * @since JDK 1.5
 * @version Wed Jun 23 11:04:27 CEST 2010
 */
public final class FEqual extends BooleanOperator {

	/**
	 * note that apply() does NOT check at the moment whether the int args
	 * represent in fact XSD floats;
	 * note that apply() does NOT check whether it is given exactly two arguments
	 */
  protected boolean holds(int[] args) {

    // http://www.ibm.com/developerworks/java/library/j-jtp0114/#N10255
    // https://en.wikipedia.org/wiki/Machine_epsilon#Values_for_standard_hardware_floating_point_arithmetics
    float one = ((XsdFloat)getObject(args[0])).value;
    float two = ((XsdFloat)getObject(args[1])).value;
    // recommended value was to small, reason for *100
    double epsilon = 5.96e-08 * 100;
    return (abs(one/two -1) < epsilon);
	}

}
