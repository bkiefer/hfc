package de.dfki.lt.hfc;

import java.util.Objects;

public class Namespace {

  public final String SHORT_NAMESPACE;
  public final String LONG_NAMESPACE;

  private boolean isShort;

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Namespace that = (Namespace) o;
    return isShort == that.isShort &&
            Objects.equals(SHORT_NAMESPACE, that.SHORT_NAMESPACE) &&
            Objects.equals(LONG_NAMESPACE, that.LONG_NAMESPACE);
  }

  @Override
  public int hashCode() {
    return Objects.hash(SHORT_NAMESPACE, LONG_NAMESPACE, isShort);
  }

  public Namespace(String shortNamespace, String longNamespace, boolean isShort) {
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
    return isShort ? SHORT_NAMESPACE + ":" : LONG_NAMESPACE;
  }
}
