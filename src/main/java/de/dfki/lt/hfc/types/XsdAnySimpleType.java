package de.dfki.lt.hfc.types;

/**
 * NOTE: if further XSD types are added, class Namespace needs to be extended;
 *       same holds for method TupleStore.makeJavaObject()
 *
 * NOTE: I made this class abstract, even though there exist the corresponding
 *       XSD type anySimpleType;
 *       not sure what the toString() and toName() implementation for
 *       XsdAnySimpleType should yield
 * 
 * NOTE: every subclass of XsdAnySimpleType is advised to implement a _static_
 *       method having the following signature
 *         public static Object getValue(int id, TupleStore ts)
 *       which is supposed to return an existing Java object for HFC's XSD
 *       representation of the object
 *
 * @author (C) Hans-Ulrich Krieger
 * @since JDK 1.5
 * @version Wed Sep 23 11:15:58 CEST 2015
 */
public abstract class XsdAnySimpleType extends AnyType {
  
  public abstract String toString(boolean shortIsDefault);
  
  public abstract String toName();
	
}
