package de.dfki.lt.hfc.types;

public class Variable extends AnyType {

  private final String name;

  public Variable(String literal) {
    this.name = literal;
  }

  @Override
  public String toString(boolean shortIsDefault) {
    return name;
  }

  @Override
  public String toName() {
    return name;
  }

  @Override
  public Object toJava() {
    return this;
  }

  @Override
  public int compareTo(Object o) {
    if (o instanceof AnyType.MinMaxValue) {
      AnyType.MinMaxValue minMaxValue = (MinMaxValue) o;
      return minMaxValue.compareTo(this);
    }
    if (!(o instanceof Variable)) {
      throw new IllegalArgumentException("Can't compare " + this.getClass() + " and " + o.getClass());
    }
    return this.name.compareTo(((Variable) o).name);
  }

  @Override
  public boolean equals(Object o){
    return compareTo(o) == 0;
  }
}
