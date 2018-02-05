package de.dfki.lt.hfc.qrelations;

import de.dfki.lt.hfc.indices.Index;
import de.dfki.lt.hfc.indices.AdvancedIndex;

import java.util.*;

/**
 * This class represents the During-Relation (and its inverse) as defined in Allen´s Interval Algebra.
 * The relation holds if for two intervals a and b the following holds
 *
 *      |     ---a---       |
 *      |                   |
 *      |  --------b------- |
 *      -------------------->
 *      0   1   2   3   4   5
 *
 * Then the interval a is _during_ the interval b.
 *
 * Created by christian on 23/05/17.
 */
public class AllenDuring extends QRelationAllen {

    public static final String NAME = "AllenDuring";

    static {
        QRelationFactory.registerConstructor(AllenDuring.class, "D", "Di");
    }

    private final boolean isInverted;

    public AllenDuring(String literal, int firstArgumentID, int secondArgumentID, int index){
        this.isInverted = literal.endsWith("i");
        this.firstArgumentID = firstArgumentID;
        this.secondArgumentID = secondArgumentID;
        this.index = index;
        this.isInterval = false;
    }

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder(isInverted? "Di(":"D(");
        sb.append(firstArgumentObject.toString());
        sb.append(",");
        sb.append(secondArgumentObject.toString());
        sb.append(")");
        return sb.toString();
    }

    @Override
    public Collection<ArrayList<String>> rewrite( Map<Integer, String> idToName) {
        ArrayList<ArrayList<String>> filters = new ArrayList<>();


        // Add first condition
        ArrayList<String> newFilter = new ArrayList<>();
        newFilter.add(isInverted ? QRelationAllenUtilities.getLess(firstArgumentObject.getClass()): QRelationAllenUtilities.getGreater(
            firstArgumentObject.getClass()));
        newFilter.add(idToName.get(firstArgumentVariableID));
        newFilter.add(firstArgumentObject.toString());
        filters.add(newFilter);
        // Add second condition
        newFilter = new ArrayList<>();
        newFilter.add(QRelationAllenUtilities.getLess(firstArgumentObject.getClass()));
        newFilter.add(idToName.get(firstArgumentVariableID));
        newFilter.add(secondArgumentObject.toString());
        filters.add(newFilter);
        newFilter = new ArrayList<>();
        newFilter.add(!isInverted? QRelationAllenUtilities.getLess(firstArgumentObject.getClass()): QRelationAllenUtilities.getGreater(
            firstArgumentObject.getClass()));
        newFilter.add(idToName.get(secondArgumentVariableID));
        newFilter.add(secondArgumentObject.toString());
        filters.add(newFilter);
        // Add second condition
        newFilter = new ArrayList<>();
        newFilter.add(QRelationAllenUtilities.getGreater(firstArgumentObject.getClass()));
        newFilter.add(idToName.get(secondArgumentVariableID));
        newFilter.add(firstArgumentObject.toString());
        filters.add(newFilter);
        return filters;
    }

    @Override
    public Set<int[]> apply(Index index) {
        AdvancedIndex advancedIndex = (AdvancedIndex) index;
        if( isInverted) {
            return advancedIndex.searchIntervalsIncluding(firstArgumentObject, secondArgumentObject,false,false);
        }
        else {
            return index.searchInterval(QRelationAllenUtilities.increment(firstArgumentID), QRelationAllenUtilities.decrement(
                secondArgumentID));
        }

    }
}
