    package de.dfki.lt.hfc.qrelations;

import de.dfki.lt.hfc.indices.Index;

import java.util.*;

/**
 * This class represents the Overlaps-Relation (and its inverse) as defined in Allen´s Interval Algebra.
 * The relation holds if for two intervals a and b the following holds
 *
 *      |  ---a---          |
 *      |                   |
 *      |       ---b---     |
 *      -------------------->
 *      0   1   2   3   4   5
 *
 * Then the interval a is _overlaps_ with the interval b.
 *
 * Created by christian on 23/05/17.
 */
public class AllenOverlaps extends QRelationAllen {

    public static final String NAME = "AllenOverlaps";

    static {
        QRelationFactory.registerConstructor(AllenOverlaps.class, "O", "Oi");
    }

    private final boolean isInverted;

    public AllenOverlaps(String literal, int firstArgumentID, int secondArgumentID, int index){
        this.isInverted = literal.endsWith("i");
        this.firstArgumentID = firstArgumentID;
        this.secondArgumentID = secondArgumentID;
        this.index = index;
        this.isInterval = false;
    }

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder(isInverted? "Oi(":"O(");
        sb.append(firstArgumentObject);
        sb.append(",");
        sb.append(secondArgumentObject);
        sb.append(")");
        return sb.toString();
    }

    @Override
    public Collection<ArrayList<String>> rewrite( Map<Integer, String> idToName) {
        ArrayList<ArrayList<String>> filters = new ArrayList<>();

        // Add first condition
        ArrayList<String> newFilter = new ArrayList<>();
        newFilter.add(isInverted?QRelationAllenUtilities.getGreater(firstArgumentObject.getClass()):QRelationAllenUtilities.getLess(
            firstArgumentObject.getClass()));
        newFilter.add(idToName.get(firstArgumentVariableID));
        newFilter.add(firstArgumentObject.toString());
        filters.add(newFilter);
        newFilter = new ArrayList<>();
        newFilter.add(QRelationAllenUtilities.getLess(firstArgumentObject.getClass()));
        newFilter.add(idToName.get(firstArgumentVariableID));
        newFilter.add(secondArgumentObject.toString());
        filters.add(newFilter);
        // Add second condition
        newFilter = new ArrayList<>();
        newFilter.add(isInverted?QRelationAllenUtilities.getGreater(firstArgumentObject.getClass()):QRelationAllenUtilities.getLess(
            firstArgumentObject.getClass()));
        newFilter.add(idToName.get(secondArgumentVariableID));
        newFilter.add(secondArgumentObject.toString());
        filters.add(newFilter);
        newFilter = new ArrayList<>();
        newFilter.add(QRelationAllenUtilities.getGreater(firstArgumentObject.getClass()));
        newFilter.add(idToName.get(secondArgumentVariableID));
        newFilter.add(firstArgumentObject.toString());
        filters.add(newFilter);
        return filters;
    }

        @Override
        public Set<int[]> apply(Index index) {
            return (isInverted)?index.searchInterval(QRelationAllenUtilities.increment(
                firstArgumentID),QRelationAllenUtilities.getMaxValue(firstArgumentObject.getClass())):
                    index.searchInterval(QRelationAllenUtilities.getMinValue(secondArgumentObject.getClass()),QRelationAllenUtilities.decrement(
                        secondArgumentID));
        }

    }
