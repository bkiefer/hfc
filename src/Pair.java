package de.dfki.lt.hfc;

/**
 * used in the forward chainer to memoize the result of a join of two tables
 * in complexJoin()
 *
 * @author (C) Hans-Ulrich Krieger
 * @since JDK 1.5
 * @version Thu Sep  3 12:45:09 CEST 2009
 */
public class Pair {

	protected Object first;
	
	protected Object second;
	
	public Pair(Object first, Object second) {
		this.first = first;
		this.second = second;
	}
	
	/**
	 * make sure that this equals obj by NOT distinguishing between first and second
	 */
	public boolean equals(Object obj) {
		Pair pair = (Pair)obj;
		if (this.first.equals(pair.first) && this.second.equals(pair.second))
			return true;
		else if (this.first.equals(pair.second) && this.second.equals(pair.first))
			return true;
		else
			return false;
	}
	
	/**
	 * hash code does not distinguish between first and second arg of a pair, thus
	 * adds the hashes of first and second:
	 *   IF p.first == x && p.second == y &&
	 *      q.first == y && q.second == x
	 *   THEN p.hashCode() == q.hashCode()
	 */
	public int hashCode() {
		return first.hashCode() + second.hashCode();
	}
	
}
