package de.dfki.lt.hfc;

import java.lang.reflect.Constructor;
import java.util.Hashtable;

/**
 * OperatorRegistry encapsulates code centered around the dynamic loading of classes
 * encoding operators, and the creation of instances which are unknown at compile time
 *
 * @author (C) Hans-Ulrich Krieger
 * @version Wed Jun 22 15:20:51 CEST 2016
 * @see de.dfki.lt.hfc.Operator
 * @see de.dfki.lt.hfc.FunctionalOperator
 * @see de.dfki.lt.hfc.RelationalOperator
 * @see java.lang.Object
 * @see java.lang.Class
 * @see java.lang.reflect.Constructor
 * @since JDK 1.5
 */
public final class OperatorRegistry {

  /**
   * all (custom) operators should be put into package de.dfki.lt.hfc.operators
   */
  public static final String OPERATOR_PATH = "de.dfki.lt.hfc.operators.";  // last '.' char is needed

  /**
   * if true, register() exits with error code 1 in case registration fails;
   * if false, register() returns null in case registration fails
   */
  public static final boolean EXIT_WHEN_REGISTRATION_FAILS = true;

  /**
   * the association between a class name and its _singleton_ instance (an operator)
   */
  protected Hashtable<String, Operator> nameToFo;

  /**
   * will be set by the unary constructor and is accessible to every instance of a functional or
   * relational operator
   */
  private TupleStore tupleStore;

  /**
   * the argument to OperatorRegistry() is essential, since the registry will be passed to the
   * instance of the specific operator
   */
  public OperatorRegistry(TupleStore tupleStore) {
    this.tupleStore = tupleStore;
    this.nameToFo = new Hashtable<String, Operator>();
  }


  protected OperatorRegistry(TupleStore tupleStore, OperatorRegistry operatorRegistry) {
    this.tupleStore = tupleStore;
    this.nameToFo = new Hashtable<String, Operator>(operatorRegistry.nameToFo);
  }

  /**
   * potentially constructs an instance of className by dynamically loading the corresponding class;
   * given that instance, the apply() method of its class is called;
   * evaluate(String className, int[] args) should only be used for _functional_ operators
   *
   * @return an int representing the return value
   * special values of interest:
   */
  public int evaluate(String className, int[] args) {
    Operator op = this.nameToFo.get(className);
    if (op == null)
      op = register(className);
    return ((FunctionalOperator) op).apply(args);
  }

  /**
   * evaluate(String className, BindingTable[] args) should only be used for _relational_ operators
   */
  public BindingTable[] evaluate(String className, BindingTable[] args) {
    Operator op = this.nameToFo.get(className);
    if (op == null)
      op = register(className);
    return ((RelationalOperator) op).apply(args);
  }

  /**
   * contrary to evaluate(), this method does NOT check whether the predicate is already
   * registered and directly applies the functional operator to its arguments
   * evaluate(String className, int[] args) should only be used for _functional_ operators
   */
  public int evaluateNoCheck(String className, int[] args) {
    return ((FunctionalOperator) this.nameToFo.get(className)).apply(args);
  }

  /**
   * evaluate(String className, BindingTable[] args) should only be used for _relational_ operators
   */
  public BindingTable[] evaluateNoCheck(String className, BindingTable[] args) {
    return ((RelationalOperator) this.nameToFo.get(className)).apply(args);
  }

  /**
   * this version of evaluate() distinguishes between the class name and the package path
   */
  public int evaluate(String className, String packageName, int[] args) {
    return evaluate(packageName + className, args);
  }

  /**
   * now something similar for a relational operator
   */
  public BindingTable[] evaluate(String className, String packageName, BindingTable[] args) {
    return evaluate(packageName + className, args);
  }

  /**
   * generates a new functional operator instance for className, sets its public field tupleStore
   * with this tupleStore, and generates an association between the class name and the specific
   * instance of that class
   */
  private Operator register(String className) {
    try {
      Class clazz = Class.forName(className);
      Constructor<Operator> constructor = clazz.getConstructor();
      Operator op = constructor.newInstance();
      op.tupleStore = this.tupleStore;
      this.nameToFo.put(className, op);
      return op;
    } catch (Exception e) {
      //
      if (OperatorRegistry.EXIT_WHEN_REGISTRATION_FAILS)
        throw new RuntimeException("FATAL ERROR " +className);
      return null;
    }
  }

  /**
   * checks whether there has already been an operator registered for the _fully-qualified_
   * class name; if so, the operator is returned;
   * if not, a new one is created, stored/registered, and returned
   */
  protected Operator checkAndRegister(String className) {
    Operator op = this.nameToFo.get(className);
    if (op == null)
      op = register(className);
    return op;
  }

}
