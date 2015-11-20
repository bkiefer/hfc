package de.dfki.lt.hfc.types;

/**
 * @author (C) Hans-Ulrich Krieger
 * @since JDK 1.5
 * @version Mon May 30 16:46:09 CEST 2011
 */
public class BlankNode extends AnyType {
	
	public String value;
	
	public BlankNode(String value) {
		this.value = value;
	}
	
	/**
	 * NOTE: shortIsDefault can be ignored at the moment
	 */
	public String toString(boolean shortIsDefault) {
		return this.value;
	}
	
	/**
	 * omit the preceeding "_:"
	 */
	public String toName() {
		return this.value.substring(2);
	}
	
}
