package de.dfki.lt.hfc;

/**
 * FunctionalOperator (FO for short) is assumed to be the abstract superclass of all
 * functional operators (functions and predicates) used in the forward chainer;
 * every subclass of FunctionalOperator MUST implement exactly one unary method,
 * called apply(int[] args), together with a package specification and (at least)
 * one import statement:
 * <p>
 * // put your FO in _this_ package:
 * package de.dfki.lt.hfc.operators;
 * <p>
 * // give access to methods from FunctionalOperator which safely access the tuple
 * // store via class Operator
 * import de.dfki.lt.hfc.FunctionalOperator;
 * <p>
 * // put your code for the FO in here with exactly the same signature
 * public int apply(int[] args) {
 * ...
 * }
 * <p>
 * the args (an int array) given to apply() are exactly the arguments given to the FO
 * specified in the rules of the forward chainer;
 * the return value is usually a positive int, encoding the the application of the FO
 * to its arguments;
 * <p>
 * note that we reserve 0, -1, and -2 as special return values:
 *
 * @author (C) Hans-Ulrich Krieger
 * @version Fri Mar  8 12:36:22 CET 2013
 * @see FunctionalOperator.UNBOUND
 * @see FunctionalOperator.TRUE
 * @see FunctionalOperator.FALSE
 * @see FunctionalOperator.DONT_KNOW
 * <p>
 * even a prdicate must return a value (either FunctionalOperator.TRUE or FunctionalOperator.FALSE),
 * that is why a predicate is subsumed by the term _Functional_Operator
 * <p>
 * NOTE: a functional operator is required _not_ to implement any constructor;
 * @see Operator for the abstract superclass providing useful implemented methods
 * @see de.dfki.lt.hfc.Operator for a description of _relational_ operators
 * @see de.dfki.lt.hfc.operators.NoValue for an example (predicate)
 * @see de.dfki.lt.hfc.operators.Concatenate for an example (function)
 * @since JDK 1.5
 */
public abstract class BooleanOperator extends FunctionalOperator {

  public int apply(int[] args) {
    return holds(args) ? TRUE : FALSE;
  }

  /**
   * !!! this is the one and only method that you MUST implement !!!
   */
  protected abstract boolean holds(int[] args);
}
