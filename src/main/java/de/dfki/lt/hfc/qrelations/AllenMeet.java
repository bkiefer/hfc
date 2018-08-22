package de.dfki.lt.hfc.qrelations;

import de.dfki.lt.hfc.indices.AdvancedIndex;
import de.dfki.lt.hfc.indices.Index;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Set;


/**
 * This class represents the Meets-Relation (and its inverse) as defined in Allen´s Interval Algebra.
 * The relation holds if for two intervals a and b the following holds
 * <p>
 * |    ---a---        |
 * |                   |
 * |           ---b--- |
 * -------------------->
 * 0   1   2   3   4   5
 * <p>
 * Then the interval a is _meets_ the interval b.
 * <p>
 * Created by christian on 23/05/17.
 */
public class AllenMeet extends QRelationAllen {
  public static final String NAME = "AllenMeet";


  static {
    QRelationFactory.registerConstructor(AllenMeet.class, "M", "Mi");
  }

  private final boolean isInverted;

  public AllenMeet(String literal, int firstArgumentID, int secondArgumentID, int index) {
    this.isInverted = literal.endsWith("i");
    this.firstArgumentID = firstArgumentID;
    this.secondArgumentID = secondArgumentID;
    this.index = index;
    this.isInterval = false;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder(isInverted ? "Mi(" : "M(");
    sb.append(firstArgumentObject.toString());
    sb.append(",");
    sb.append(secondArgumentObject.toString());
    sb.append(")");
    return sb.toString();
  }

  @Override
  public Collection<ArrayList<String>> rewrite(Map<Integer, String> idToName) {
    ArrayList<ArrayList<String>> filters = new ArrayList<>();


    // Add first condition
    ArrayList<String> newFilter = new ArrayList<>();
    newFilter.add(isInverted ? QRelationAllenUtilities.getGreater(firstArgumentObject.getClass()) : QRelationAllenUtilities.getLess(
            firstArgumentObject.getClass()));
    newFilter.add(idToName.get(firstArgumentVariableID));
    newFilter.add(firstArgumentObject.toString());
    filters.add(newFilter);
    newFilter = new ArrayList<>();
    newFilter.add(QRelationAllenUtilities.getEqual(firstArgumentObject.getClass()));
    newFilter.add(isInverted ? idToName.get(firstArgumentVariableID) : idToName.get(
            secondArgumentVariableID));
    newFilter.add(isInverted ? secondArgumentObject.toString() : firstArgumentObject.toString());
    filters.add(newFilter);
    newFilter = new ArrayList<>();
    newFilter.add(isInverted ? QRelationAllenUtilities.getGreater(firstArgumentObject.getClass()) : QRelationAllenUtilities.getLess(
            firstArgumentObject.getClass()));
    newFilter.add(idToName.get(secondArgumentVariableID));
    newFilter.add(secondArgumentObject.toString());
    filters.add(newFilter);
    return filters;
  }

  @Override
  public Set<int[]> apply(Index index) {
    AdvancedIndex advancedIndex = (AdvancedIndex) index;
    return (isInverted) ? advancedIndex.searchIntervalWithEqualityConstraints(
            secondArgumentObject, QRelationAllenUtilities.getMaxValue(secondArgumentObject.getClass()), true, false)
            : advancedIndex.searchIntervalWithEqualityConstraints(QRelationAllenUtilities.getMinValue(
            secondArgumentObject.getClass()),
            firstArgumentObject, false, true);
  }


}
