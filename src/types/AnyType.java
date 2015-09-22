package de.dfki.lt.hfc.types;

/**
 * note that we enforce the toString() method has access to
 * the namespace object of the tuple store, i.e., access to
 * shortIsDefault and the relevant short and long namespace
 * constants
 *
 * @author (C) Hans-Ulrich Krieger
 * @since JDK 1.5
 * @version Tue Sep 13 16:47:00 CEST 2011
 */
public abstract class AnyType {

	/**
	 * returns a compliant external representation for subtypes
	 * of AnyType, viz., URIs, blank nodes, and various forms of
	 * XSD atoms
	 */
	public abstract String toString(boolean shortIsDefault);
	
	/**
	 * contrary to toString(), toName() returns a somewhat `abstracted'
	 * form of the object, called `name' in order to construct new URIs
	 * or blank nodes using operators that are given arguments;
	 * @see operators.MakeBlankNode
	 *
   * strong suggestion:
	 *   if the object is an URI, the angle brackets are omitted;
	 *   if it is a blank node, the prefix "_:" is omitted;
	 *   if it is an XSD atom, the outer quotes '\"' as well as a
	 *   potential succeeding XSD type is omitted;
	 *   furthermore, if the object is an XSD string, spaces and angle brackets
	 *   are replaced by the underscore character '_'
	 */
	public abstract String toName();
	
}
