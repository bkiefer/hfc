package de.dfki.lt.hfc;

/**
 * FunctionalOperator (FO for short) is assumed to be the abstract superclass of all
 * functional operators (functions and predicates) used in the forward chainer;
 * every subclass of FunctionalOperator MUST implement exactly one unary method,
 * called apply(int[] args), together with a package specification and (at least)
 * one import statement:
 *
 *     // put your FO in _this_ package:
 *     package de.dfki.lt.hfc.operators;
 *
 *     // give access to methods from FunctionalOperator which safely access the tuple
 *     // store via class Operator
 *     import de.dfki.lt.hfc.FunctionalOperator;
 *
 *     // put your code for the FO in here with exactly the same signature
 *     public int apply(int[] args) {
 *       ...
 *     }
 *
 * the args (an int array) given to apply() are exactly the arguments given to the FO
 * specified in the rules of the forward chainer;
 * the return value is usually a positive int, encoding the the application of the FO
 * to its arguments;
 *
 * note that we reserve 0, -1, and -2 as special return values:
 *   @see FunctionalOperator.UNBOUND
 *   @see FunctionalOperator.TRUE
 *   @see FunctionalOperator.FALSE
 *   @see FunctionalOperator.DONT_KNOW
 *
 * even a prdicate must return a value (either FunctionalOperator.TRUE or FunctionalOperator.FALSE),
 * that is why a predicate is subsumed by the term _Functional_Operator
 *
 * NOTE: a functional operator is required _not_ to implement any constructor;
 *
 * @see Operator for the abstract superclass providing useful implemented methods
 * @see de.dfki.lt.hfc.Operator for a description of _relational_ operators
 *
 * @see de.dfki.lt.hfc.operators.NoValue for an example (predicate)
 * @see de.dfki.lt.hfc.operators.Concatenate for an example (function)
 *
 * @author (C) Hans-Ulrich Krieger
 * @since JDK 1.5
 * @version Fri Mar  8 12:36:22 CET 2013
 */
public abstract class FunctionalOperator extends Operator {
	
	/**
	 * the input args are positive ints, but 0 can be used to indicate an unbound
	 * (or NULL) return value
	 */
	public static final int UNBOUND = 0;
	
	/**
	 * the input args and the return value are positive ints, but TRUE (= -1) can
	 * be used to indicate that the return value is 'true' in case the functional
	 * operator is assumed to be a predicate
	 */
	public static final int TRUE = -1;
	
	/**
	 * the input args and the return value are positive ints, but FALSE (= -2) can
	 * be used to indicate that the return value is 'false' in case the functional
	 * operator is assumed to be a predicate
	 */
	public static final int FALSE = -2;
	
	/**
	 * used by three-valued tests that talk, e.g., about underspecified temporal information
	 */
	public static final int DONT_KNOW = -3;
	
	/**
	 * this method offers the possibility that a functional operator can call another functional
   * operator in its own definition; for instance, DTMax2 uses DTLess
	 */
	public int callFunctionalOperator(String name, int[] args) {
		return this.tupleStore.operatorRegistry.evaluate(name, OperatorRegistry.OPERATOR_PATH, args);
	}
		
  /**
   * !!! this is the one and only method that you MUST implement !!!
   */
  public abstract int apply(int[] args);
	
}
