package de.dfki.lt.hfc;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * the internal representation of a function as used in the '@test'
 * section of the forward chainer
 *
 * @author (C) Hans-Ulrich Krieger
 * @since JDK 1.5
 * @version Fri Mar  1 16:20:21 CET 2013
 */
public class Predicate extends Relation {
	
	/**
	 *
	 */
	public Predicate(String name,
									 ArrayList<Integer> args) {
		super(name, args);
	}
	
	/**
	 *
	 */
	public Predicate(String name,
									 Operator op,
									 ArrayList<Integer> args) {
		super(name, op, args);
	}
	
	/**
	 *
	 */
	public Predicate(String name,
									 Operator op,
									 ArrayList<Integer> args,
									 HashMap<Integer, ArrayList<Integer>> relIdToFunIds) {
		super(name, op, args, relIdToFunIds);
	}
	
	/**
	 *
	 */
	public Predicate(String name,
									 Operator op,
									 ArrayList<Integer> args,
									 HashMap<Integer, ArrayList<Integer>> relIdToFunIds,
									 HashMap<String, Integer> varToId) {
		super(name, op, args, relIdToFunIds, varToId);
	}
	
}
