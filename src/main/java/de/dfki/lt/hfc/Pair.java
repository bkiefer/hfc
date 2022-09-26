package de.dfki.lt.hfc;

import java.util.Objects;

public class Pair<F, S> {

  final public F first;
  final public S second;

  public Pair(F first, S second) {
    this.first = first;
    this.second = second;
  }

  @Override
  @SuppressWarnings("rawtypes")
  public boolean equals(Object obj) {
    if (! (obj instanceof Pair))
      return false;
    Pair pair = (Pair) obj;
    return Objects.equals(first, pair.first)
        && Objects.equals(second, pair.second);
  }

  @Override
  public int hashCode() {
    return Objects.hash(first, second);
  }

  @Override
  public String toString() {
    return '<' + Objects.toString(first) + "|" + Objects.toString(second) + '>';
  }
}
