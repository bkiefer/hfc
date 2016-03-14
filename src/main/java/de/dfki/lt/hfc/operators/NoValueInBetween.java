package de.dfki.lt.hfc.operators;

import de.dfki.lt.hfc.FunctionalOperator;
import de.dfki.lt.hfc.Calc;
import java.util.Set;

/**
 * at the moment, NoValueInBetween checks for the _non-existence_ of
 * time-stamped _triples_
 *   subj pred obj time
 * where start <= time <= end
 *
 * call this operator in the @test section of RDL rules through
 *   NoValueInBetween subj pred obj start end
 *
 * @author (C) Hans-Ulrich Krieger
 * @since JDK 1.5
 * @version Fri Mar 11 19:45:26 GMT 2016
 */
public class NoValueInBetween {


}
