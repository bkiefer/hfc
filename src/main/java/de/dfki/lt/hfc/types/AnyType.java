package de.dfki.lt.hfc.types;

/**
 * this (abstract) class is the superclass for the _internal_
 * representationof URIs, blank nodes, and XSD types (and their
 * "relatives") in HFC;
 * note: the abstract subclass XsdAnySimpleType is the superclass
 * of all concrete XSD types
 * @see de.dfki.lt.hfc.types.XsdAnySimpleType
 *
 * note that we enforce the toString() method to have access to
 * the namespace object of the tuple store, i.e., access to
 * shortIsDefault and the relevant short and long namespace
 * constants
 *
 * NOTE: all (custom) types should be put in package de.dfki.lt.hfc.types
 * @see de.dfki.lt.hfc.Namespace.TYPE_PATH
 *
 * @author (C) Hans-Ulrich Krieger
 * @since JDK 1.5
 * @version Fri Jan 29 17:02:37 CET 2016
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

  /**
   * returns an instance of a "corresponding" Java class if there is such a class;
   * for instance
   *   + XsdString -> java.lang.String
   *   + XsdInt -> java.lang.Integer
   *   + XsdFloat -> java.lang.Float
   *   + XsdAnyURI -> java.net.URI
   * as some of HFC's XSD types do _not_ have a counterpart in Java, we return the
   * HFC XSD type instance itself in such a case;
   * for instance
   *   + XsdMonetary -> XsdMonetary
   *   + XsdGMonthYear -> XsdGMonthYear
   *   + BlankNode -> BlankNode
   *   + Uri -> Uri
   * as the types of the return values for the different toJava() methods vary, we define
   * the return type of this method to be the most general type, viz., java.lang.Object
   */
  public abstract Object toJava();


  /**
   * The default toString method is overridden returning the long representation
   */
  public String toString() { return toString(false); }

}
