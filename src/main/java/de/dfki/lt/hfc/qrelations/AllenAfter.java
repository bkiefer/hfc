package de.dfki.lt.hfc.qrelations;

import de.dfki.lt.hfc.indices.Index;

import java.util.*;

/**
 * This class represents the After-Relation as defined in Allen´s Interval Algebra.
 * The relation holds if for two intervals a and b the following holds
 *
 *      |  ---a---          |
 *      |                   |
 *      |           ---b--- |
 *      -------------------->
 *      0   1   2   3   4   5
 *
 * Then the interval b is _after_ the interval a.
 *
 * Created by christian on 23/05/17.
 */
public class AllenAfter extends QRelationAllen {
    public static final String NAME = "AllenAfter";


    static {
        QRelationFactory.registerConstructor(AllenAfter.class, "Af");
    }

    public AllenAfter( String name,int firstArgumentID, int secondArgumentID, int index){
        this.firstArgumentID = firstArgumentID;
        this.secondArgumentID = secondArgumentID;
        this.index = index;
        this.isInterval = false;
    }

    @Override
    public String toString(){
        return  ">(" + firstArgumentObject.toString() +
                "," +
                secondArgumentObject.toString() +
                ")";
    }

    @Override
    public Collection<ArrayList<String>> rewrite( Map<Integer, String> idToName) {
        ArrayList<ArrayList<String>> filters = new ArrayList<>();

        // Add first condition
        ArrayList<String> newFilter = new ArrayList<>();
        newFilter.add(QRelationAllenUtilities.getLess(firstArgumentObject.getClass()));
        newFilter.add(firstArgumentObject.toString());
        newFilter.add(idToName.get(firstArgumentVariableID));
        filters.add(newFilter);
        // Add second condition
        newFilter = new ArrayList<>();
        newFilter.add(QRelationAllenUtilities.getLess(firstArgumentObject.getClass()));
        newFilter.add(secondArgumentObject.toString());
        newFilter.add(idToName.get(firstArgumentVariableID));
        filters.add(newFilter);
        // Add second condition
        newFilter = new ArrayList<>();
        newFilter.add(QRelationAllenUtilities.getLess(firstArgumentObject.getClass()));
        newFilter.add(idToName.get(firstArgumentVariableID));
        newFilter.add(idToName.get(secondArgumentVariableID));
        filters.add(newFilter);
        return filters;
    }

    @Override
    public Set<int[]> apply(Index index) {
        // This lookup returns all tuples which contain intervals starting after the endpoint of interval given as reference.
        return index.searchInterval(QRelationAllenUtilities.increment(secondArgumentID), QRelationAllenUtilities.getMaxValue(
            secondArgumentObject.getClass()));
    }
}
