package de.dfki.lt.hfc;

public class NamespaceObject {

  public final String SHORT_NAMESPACE;
  public final String LONG_NAMESPACE;

  private boolean isShort;

  public NamespaceObject(String shortNamespace, String longNamespace, boolean isShort) {
    this.SHORT_NAMESPACE = shortNamespace;
    this.LONG_NAMESPACE = longNamespace;
    this.isShort = isShort;
  }

  public void setIsShort(boolean isShort) {
    this.isShort = isShort;
  }

  public boolean isShort() {
    return isShort;
  }

  public String toString() {
    return isShort ? SHORT_NAMESPACE : LONG_NAMESPACE;
  }
}
