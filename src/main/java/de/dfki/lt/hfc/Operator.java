package de.dfki.lt.hfc;

import java.util.*;
import de.dfki.lt.hfc.types.AnyType;

/**
 * NOTE: since some of the below operators modify global data structures (usually
 *       from the tuple store) and since such operators are used in rules which
 *       are applied in parallel, it is extremely IMPORTANT to lock, i.e., to
 *       SYNCHRONIZE such structure, making them a ``critical section'' (as
 *       Java calls it) !!!
 *
 * @see de.dfki.lt.hfc.FunctionalOperator
 * @see de.dfki.lt.hfc.AggregationalOperator
 * 
 * @author (C) Hans-Ulrich Krieger
 * @since JDK 1.5
 * @version Fri Jun 17 15:33:16 CEST 2011
 */
public abstract class Operator {

  /**
   * access (and modification) to the tuple store by FO is only allowed by the below
	 * methods, calling (potentially synchronized) methods from the tuple store
   */
  protected TupleStore tupleStore;
	
	/**
	 * calls getJavaObject() from class TupleStore;
	 * calling this.tupleStore.getJavaObject(...) inside a FO can thus be reduced to
	 * getObject(...)
	 */
	public AnyType getObject(int id) {
		return this.tupleStore.getJavaObject(id);
	}
	
	/**
	 * calls getObject() from class TupleStore
	 */
	public String getExternalRepresentation(int id) {
		return this.tupleStore.getObject(id);
	}
	
	/**
	 * returns the id for a given literal (e.g., a string encoding a URI, blank node,
	 * XSD int, XSD string, etc.);
	 * @return a positive int iff there exists a mapping in the tuple store between
	 *         the literal and this int
	 * @return -1, otherwise
	 */
	public int getId(String literal) {
		Integer id = this.tupleStore.objectToId.get(literal);
		if (id == null)
			return -1;
		else
			return id;
	}
	
	/**
	 * returns the proxy (the representative) for a given uri which serves as the internal
	 * name for a URI or a blank node
	 */
	public int getProxy(int uri) {
		return this.tupleStore.uriToProxy.get(uri);
	}
	
	/**
	 * calls addEquivalentElements() for the int representation of two individuals;
	 * we make sure that the modification of the data structures is synchronized!
	 */
	public void setProxy(int left, int right) {
		// note: TupleStore.addEquivalentElements() is already synchronized
		this.tupleStore.addEquivalentElements(left, right);
	}
	
	/**
	 * returns the equivalence relation for uri
	 */
	public int getRelation(int uri) {
		return this.tupleStore.uriToEquivalenceRelation.get(uri);
	}
	
	/**
	 * make an association between an internal uri and the internal representation
	 * of the equivalence relation;
	 * we make sure that the modification of the data structures is synchronized!
	 */
	public void setRelation(int uri, int rel) {
		// I do not mark the relation as being synchronized, but instead obtain a lock
		// directly on the field that I modify
		synchronized (this.tupleStore.uriToEquivalenceRelation) {
			this.tupleStore.uriToEquivalenceRelation.put(uri, rel);
		}
	}
	
	/**
	 * calls registerJavaObject() from class TupleStore;
	 * calling this.tupleStore.registerJavaObject(...) inside a FO can thus be reduced to
	 * registerObject(...);
	 * we make sure that the modification of the data structures is synchronized!
	 */
	public int registerObject(String string, AnyType object) {
		// note: TupleStore.registerJavaObject() is already synchronized
		return this.tupleStore.registerJavaObject(string, object);
	}
	
	/**
	 * checks whether a ground tuple (i.e., a tuple wo/ vars) exists in the tuple store
	 */
	public boolean ask(int[] tuple) {
		return this.tupleStore.allTuples.contains(tuple);
	}
	
	/**
	 * obtains all those tuples which contain an object obj (represented as an int)
	 * at a specific position pos in a tuple (an int);
	 * @return a set of matching tuples which have obj at position pos or the empty
	 *         set if there are no tuples matching the constraints
	 *
	 * @dangerous IT IS REQUESTED NOT TO MODIFY THE RETURN VALUE
	 */
	public Set<int[]> ask(int pos, int obj) {
		return this.tupleStore.getTuples(pos, obj);
	}
	
	/**
	 * @return the set of all tuples
	 *
	 * @dangerous IT IS REQUESTED NOT TO MODIFY THE RETURN VALUE
	 */
	public Set<int[]> ask() {
		return this.tupleStore.allTuples;
	}
	
}