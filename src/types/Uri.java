package de.dfki.lt.hfc.types;

/**
 * NOTE: at the moment, we do NOT decompose an URI into namespace and value
 *
 * @author (C) Hans-Ulrich Krieger
 * @since JDK 1.5
 * @version Mon May 30 16:46:09 CEST 2011
 */
public class Uri extends AnyType {
	
	public String value;
	
	public Uri(String value) {
		this.value = value;
	}
	
	/**
	 * NOTE: shortIsDefault can be ignored at the moment
	 */
	public String toString(boolean shortIsDefault) {
		return this.value;
	}
	
	/**
	 * omit the surrounding angle brackets
	 */
	public String toName() {
		return this.value.substring(1, this.value.length() - 1);
	}
	
}
