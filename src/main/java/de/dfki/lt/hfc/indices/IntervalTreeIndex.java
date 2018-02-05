package de.dfki.lt.hfc.indices;

import de.dfki.lt.hfc.indices.IntervalTree.Interval;
import de.dfki.lt.hfc.indices.IntervalTree.IntervalTree;
import de.dfki.lt.hfc.types.AnyType;
import java.util.Set;

/**
 * @author Christian Willms - Date: 19.10.17 19:04.
 * @version 19.10.17
 */
public class IntervalTreeIndex extends AdvancedIndex {

  private final IntervalTree tree = new IntervalTree();

  public IntervalTreeIndex(Class key, int start, int end) {
    super(key, start, end);
  }

  @Override
  protected void structureSpecificAdd(AnyType anyType, int[] value) {
    this.tree.insert(new Interval(anyType,anyType,value));
  }

  @Override
  protected Set<int[]> structureSpecificRemove(AnyType anyType, int[] value) {
    return null; //TODO implement remove method
  }

  @Override
  protected Set<int[]> structureSpecificSearch(AnyType anyType) {
    return this.tree.findOverlapping(new Interval(anyType,anyType));
  }

  @Override
  protected Set<int[]> structureSpecificIntervalSearch(AnyType start, AnyType end) {
    return this.tree.findWithEquality(new Interval(start,end),false,false);
  }

  @Override
  public String toString() {
    return this.tree.toString();
  }

  @Override
  public long size() {
    return this.tree.size();
  }

  @Override
  public int height() {
    return -1;
  }

  @Override
  public void clear() {
    this.tree.clear();

  }

  @Override
  public void addInterval(AnyType start, AnyType end, int[] v) {
    this.tree.insert(new Interval(start,end,v));

  }

  @Override
  public Set<int[]> removeInterval(AnyType start, AnyType end) {
    return null;//this.tree.delete(new Interval(start,end));
  }

  @Override
  public Set<int[]> searchIntervalWithEqualityConstraints(AnyType start, AnyType end, boolean startMustBeEqual, boolean endMustBeEqual) {
    return this.tree.findWithEquality(new Interval(start,end),startMustBeEqual,endMustBeEqual);
  }

  @Override
  public Set<int[]> searchIntervalsIncluding(AnyType start, AnyType end, boolean startMustBeEqual, boolean endMustBeEqual) {
    return this.tree.findIntervalsContaining(new Interval(start,end),startMustBeEqual,endMustBeEqual);
  }

}