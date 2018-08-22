package de.dfki.lt.hfc.types;

/**
 * An abstract generic class for numbers, to facilitate comparison, conversion,
 * etc.
 */
public abstract class XsdNumber extends XsdAnySimpleType {
  protected Number toNumber() {
    return (Number) toJava();
  }
}
