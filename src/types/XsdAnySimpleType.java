package de.dfki.lt.hfc.types;

//import de.dfki.lt.hfc.TupleStore;

/**
 * !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
 * NOTE: if further XSD types are added, class Namespace needs to be extended;
 *       same holds for method TupleStore.makeJavaObject()
 * NOTE: I made this class abstract, even though there exist the corresponding
 *       XSD type anySimpleType;
 *       not sure what the toString() and toName() implementation for
 *       XsdAnySimpleType should yield
 * !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
 *
 * @author (C) Hans-Ulrich Krieger
 * @since JDK 1.5
 * @version Fri Aug 28 15:49:42 CEST 2009
 */
public abstract class XsdAnySimpleType extends AnyType {
  
  //public abstract Object getValue(int id, TupleStore ts);
	
}
