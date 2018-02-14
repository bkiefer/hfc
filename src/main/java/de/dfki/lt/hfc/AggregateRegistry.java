package de.dfki.lt.hfc;

import java.util.*;
import java.lang.reflect.*;

/**
 * AggregateRegistry encapsulates code centered around the dynamic loading of classes
 * encoding aggregates, and the creation of instances which are unknown at compile time
 *
 * @see AggregationalOperator
 * @see java.lang.Object
 * @see java.lang.Class
 * @see java.lang.reflect.Constructor
 *
 * @author (C) Hans-Ulrich Krieger
 * @since JDK 1.5
 * @version Thu Jan 14 14:57:24 CET 2010
 */
public final class AggregateRegistry {
	
	/**
	 * all (custom) aggregates should be put in package de.dfki.lt.hfc.aggregates
	 */
	public static final String AGGREGATE_PATH = "de.dfki.lt.hfc.aggregates.";  // last '.' char is OK
	
	/**
	 * if true, register() exits with error code 1 in case registration fails;
	 * if false, register() returns null in case registration fails
	 */
	public static final boolean EXIT_WHEN_REGISTRATION_FAILS = true;
	
  /**
   * the association between a class name and its singleton instance
   */
  private Hashtable<String, AggregationalOperator> nameToAo;
	
	/**
	 * will be set by the unary constructor and is accessible to every instance of an aggregational
	 * operator
	 */
	private TupleStore tupleStore;
	
	/**
	 * the argument to AggregateRegistry() is essential, since it will be passed to the
	 * instance of the specific aggregational operator
	 */
	public AggregateRegistry(TupleStore tupleStore) {
		this.tupleStore = tupleStore;
		this.nameToAo = new Hashtable<String, AggregationalOperator>();
	}
	
	/**
	 * @see de.dfki.lt.hfc.TupleStore.copyTupleStore()
	 */
	protected AggregateRegistry(TupleStore tupleStore, AggregateRegistry aggregateRegistry) {
		this.tupleStore = tupleStore;
		this.nameToAo = new Hashtable<String, AggregationalOperator>(aggregateRegistry.nameToAo);
	}
	
  /**
   * potentially constructs an instance of className by dynamically loading the
	 * corresponding class; given that instance, the apply() method of its class
	 * is called;
   * @return a BindingTable object, representing the return value
   */
  public BindingTable evaluate(String className,
															 BindingTable args,
															 SortedMap<Integer, Integer> nameToPos,
															 Map<Integer, String> nameToExternalName) {
		AggregationalOperator ao = this.nameToAo.get(className);
		if (ao == null)
			ao = register(className);
		return ao.apply(args, nameToPos, nameToExternalName);
  }
	
  /**
   * this version of evaluate() distinguishes between the class name and the package path
   */
  public BindingTable evaluate(String className,
															 String packageName,
															 BindingTable args,
															 SortedMap<Integer, Integer> nameToPos,
															 Map<Integer, String> nameToExternalName) {
    return evaluate(packageName + className, args, nameToPos, nameToExternalName);
  }
	
	/**
	 * generates a new aggregational operator instance for className, sets its public
	 * field tupleStore with this tupleStore, and generates an association between
	 * the class name and the specific instance of that class
	 */
	private AggregationalOperator register(String className) {
		try {
			Class clazz = Class.forName(className);
			Constructor<AggregationalOperator> constructor = clazz.getConstructor();
			AggregationalOperator ao = constructor.newInstance();
			ao.tupleStore = this.tupleStore;
			this.nameToAo.put(className, ao);
			return ao;
		}
		catch (Exception e) {
			if (AggregateRegistry.EXIT_WHEN_REGISTRATION_FAILS)
				throw new RuntimeException("FATAL ERROR");
			else
				System.out.println("  registering aggregate " + className + " fails");
			return null;
		}
	}
	
}
