package de.dfki.lt.hfc;

/**
 * we define our interface version of an iterator over tuples (int[]) stored
 * in a Collection object
 * <p>
 * besides having hasNext() and next() as the prime methods, we define four
 * further methods, worth to have:
 * + hasSize()
 * returns the total number of stored in the collection
 * + nextAsString()
 * returns an _external_ representation of the tuple as a String[]
 * + nextAsHfcType
 * returns an HFC XSD type representation of the tuple
 * + nextAsJavaObject()
 * returns a Java representation of Java objects, related to the XSD types
 * <p>
 * an implementing class of this interface is BindingTable.BindingTableIterator
 *
 * @author (C) Hans-Ulrich Krieger
 * @version Thu Oct 29 14:42:59 CET 2015
 * @since JDK 1.5
 */
public interface TupleIterator extends java.util.Iterator<int[]> {

  public abstract int hasSize();

  public abstract String[] nextAsString();

  public abstract de.dfki.lt.hfc.types.AnyType[] nextAsHfcType();

  public abstract Object[] nextAsJavaObject();

}