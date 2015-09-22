package de.dfki.lt.hfc;

import java.util.ArrayList;

/**
 * RelationalOperator (RO for short) is assumed to be the abstract superclass of all relational
 * operators used in the forward chainer;
 * relational operators are operators whose arguments are binding tables (instead of integer
 * numbers as is the case for _functional_ operators);
 * every subclass of RelationalOperator MUST implement exactly one unary method, called
 *   apply(BindingTable[] args)
 * together with a package specification and (at least) one import statement:
 *
 *     // put your RO in _this_ package:
 *     package de.dfki.lt.hfc.operators;
 *
 *     // give access to methods from RelationalOperator which safely access the tuple store
 *     // via class Operator
 *     import de.dfki.lt.hfc.RelationalOperator;
 *
 *     // put your code for the RO in here with exactly the same signature
 *     public BindingTable[] apply(BindingTable[] args) {
 *       ...
 *     }
 *
 * the args (the BindingTable array) given to apply() are exactly the arguments given to the RO
 * via _relational_ variables, as specified in the rules of the forward chainer;
 * the return value is again a binding table array, encoding the application of the RO to its
 * arguments
 *
 * HINT: the binding tables supplied to the relational operator can make use of internal
 *       fields of those binding tables;
 *       since the current implementation of HFC uses Trove's hash sets together with strategy
 *       objects, a relational operator given, e.g., ??(s p o) can NOT be sure that the table
 *       bound to the relational (potentially complex) variable contains triples, NOR is it
 *       usually the case that the ?s column for ??(s p o) is the first column;
 *       however, by using the internal fields of the binding tables, this information can be
 *       obtained!
 * @see de.dfki.lt.hfc.BindingTable
 *        for a description of the fields
 * @see de.dfki.lt.hfc.operators.CardinalityNotEqual
 *        for an implementation of an operator which uses those fields
 *
 * NOTE: a relational operator is required _not_ to implement any constructor;
 *
 * @see de.dfki.lt.hfc.Operator for the abstract superclass providing useful implemented methods
 * @see de.dfki.lt.hfc.FunctionalOperator for a description of _functional_ operators
 *
 * @author (C) Hans-Ulrich Krieger
 * @since JDK 1.5
 * @version Wed Mar 20 14:36:50 CET 2013
 */
public abstract class RelationalOperator extends Operator {
	
	/**
	 * this method offers the possibility that a relational operator can call another relational
	 * operator in its own definition;
	 * note that there is still the possibility to call ordinary functional operators inside a
	 * relational operator
	 */
	public BindingTable[] callRelationalOperator(String name, BindingTable[] args) {
		return this.tupleStore.operatorRegistry.evaluate(name, OperatorRegistry.OPERATOR_PATH, args);
	}
	
	/**
	 * use this method to call further _functional_ operators
	 */
	public int callFunctionalOperator(String name, int[] args) {
		return this.tupleStore.operatorRegistry.evaluate(name, OperatorRegistry.OPERATOR_PATH, args);
	}
	
	/**
	 * given the position (an int) of a (potentially complex) RELATIONAL variable in a predicate (@test)
	 * or function (@action) call, the internal positions of the table column for the functional variables
	 * are returned;
	 * even for a non-complex relational variable (e.g., ??subject), a list is returned (here: of length 1)
	 */
	public int[] obtainPositions(BindingTable table, int argpos) {
		ArrayList<Integer> varids = table.relIdToFunIds.get(table.arguments[argpos]);
		int[] positions = new int[varids.size()];
		for (int i = 0; i < varids.size(); i++)
			positions[i] = table.nameToPos.get(varids.get(i));
		return positions;
	}
	
	/**
   * !!! this is the one and only method that you MUST implement !!!
	 *
	 * IMPORTANT: READ
	 * NOTE: an implementation of this method might destructively work on the input binding tables,
	 *       although brand-new ones might be created too;
	 *       in both cases, some of these (not necessarily all) tables are given back, even one or even
	 *       zero tables;
	 *       all these result tables are then joined with the LHS binding table to which this complex
	 *       predicate has been applied;
	 *       a return value equals an *empty array* of binding tables (i.e., length = 0) is interepreted
	 *       as a test that leaves the LHS binding table unaffected !!
	 *       *empty binding tables* in the result array overall lead to an empty LHS binding table, resulting
	 *       in no RHS instantiations
   */
  public abstract BindingTable[] apply(BindingTable[] args);
	
}
