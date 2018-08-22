package de.dfki.lt.hfc.operators;

/**
 * the package/directory de.dfki.lt.hfc.operators collects those operators
 * that are used as tests and actions in the @test and @action section of an
 * RDL rule;
 * since tests even returns a value (true or false), we can achieve a unified
 * interface here, viz, that operators return a single int, representing an
 * URI, a blank node, a XSD atom, a boolean value, or the unbound value;
 * the implementation of these operators (being predicates or functions) must
 * fulfill the interface for method apply() specified in the abstract class
 * FunctionalOperator:
 * <p>
 * public abstract int apply(int[] args);
 * <p>
 * a new operator must implement this method and must extend the abstract
 * superclass FunctionalOperator;
 * a new operator might take advantages of already implemented functionality
 * in the (abstract) superclass Operator of FunctionalOperator
 * <p>
 * NOTE: the action operators modify global data structures;
 * this modification might lead to problems in case rule execution is
 * processed in parallel;
 * the action operators use setters from a fixed set of methods
 * specified in class Operator; however, these setters make sure that
 * the data structures are synchronized
 *
 * @author (C) Hans-Ulrich Krieger
 * @version Wed Jun 23 16:53:03 CEST 2010
 * @see FunctionalOperator.UNBOUND
 * @see FunctionalOperator.TRUE
 * @see FunctionalOperator.FALSE
 * @see apply()
 * @see ForwardChainer.noOfCores
 * @see Operator
 */
