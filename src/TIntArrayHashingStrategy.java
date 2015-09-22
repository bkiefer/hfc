package de.dfki.lt.hfc;

import gnu.trove.*;

/**
 * implements a specific hashing and equals strategy for int[] that are used to
 * represent tuples in the tuple store, the rule store, and the forward chainer;
 * NOTE: the positions array must always be SORTED in ascending order to guarantee
 *       compatible hash codes!!!
 * 
 * @author (C) Hans-Ulrich Krieger
 * @since JDK 1.5
 * @version Wed Jun 24 14:36:20 CEST 2009
 */
public final class TIntArrayHashingStrategy implements TObjectHashingStrategy<int[]> {

	/**
	 * when assigned the null value, all positions of the int array(s) are employed
	 * in the execution of computeHashCode() and equals();
	 * when assigned an int array, only the values of the array, interpreted as
	 * positions, are used in computeHashCode() and equals();
	 * NOTE: this.positions must always be sorted in ascending order to guarantee
	 *       compatible hash codes!!!
	 */
	protected int[] positions;
	
	/**
	 * assigns the null value to this.positions
	 */
	public TIntArrayHashingStrategy() {
		this.positions = null;
	}
	
	/**
	 * should be used during initialization, given an int array of positions
	 */
	public TIntArrayHashingStrategy(int[] positions) {
		this.positions = positions;
	}
		
	/**
	 * I borrowed this code from Sun's string implementation for hashCode();
	 * the hash code of tuple t of length n is computed as
	 *   t[0]*31^(n-1) + t[1]*31^(n-2) + ... + t[n-1]
	 * using integer ring arithmetic;
	 * note that I do not check whether array == null;
	 * note further that I do not check whether
	 *   this.positions[this.positions.length - 1] < array.length
	 * note: 1 is returned in case this.positions equals empty int array
	 */
	public int computeHashCode(int[] array) {
		int hash = 1;
		if (this.positions == null) {
			for (int i = 0; i < array.length; i++)
				hash = 31 * hash + array[i];
		}
		else {
			for (int i = 0; i < this.positions.length; i++)
				hash = 31 * hash + array[this.positions[i]];
		}
		return hash;
	}
	
	/**
	 * two tuples are equal iff they are pairwise equal, using == on ints;
	 * I do not check whether array1 or array2 equals the null value;
	 * note further that I do not check whether
	 *   this.position[this.position.length - 1] < array{1|2}.length
	 * note: true is returned in case this.positions equals empty int array
	 */
	public boolean equals(int[] array1, int[] array2) {
		if (array1 == array2)
			return true;
		if (array1.length != array2.length)
			return false;
		// arrays are both of the same length
		if (this.positions == null) {
			// standard procedure: compare all positions pairwise
			for (int i = 0; i < array1.length; i++)
				if (array1[i] != array2[i])
					return false;
		}
		else {
			// compare positions, given by this.positions;
			for (int i = 0; i < this.positions.length; i++)
				if (array1[this.positions[i]] != array2[this.positions[i]])
					return false;
		}
		return true;
	}
	
}
