package de.dfki.lt.hfc.types;

/**
 * NOTE: if further XSD types are added, class Namespace needs to be extended;
 *       same holds for method TupleStore.makeJavaObject()
 *
 * NOTE: I made this class _abstract_, even though there exist the corresponding
 *       XSD type anySimpleType;
 *       not sure what the toString() and toName() implementation for
 *       XsdAnySimpleType should yield
 * 
 * @author (C) Hans-Ulrich Krieger
 * @since JDK 1.5
 * @version Wed Fri Jan 29 16:35:17 CET 2016
 */
public abstract class XsdAnySimpleType extends AnyType {
  
  // only purpose is to separate the XSD types from uris and blank nodes
  
}
