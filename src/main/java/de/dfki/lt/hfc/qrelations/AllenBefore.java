package de.dfki.lt.hfc.qrelations;

import de.dfki.lt.hfc.indices.Index;

import java.util.*;

/**
 * This class represents the Before-Relation as defined in Allen´s Interval Algebra.
 * The relation holds if for two intervals a and b the following holds
 *
 *      |  ---a---          |
 *      |                   |
 *      |           ---b--- |
 *      -------------------->
 *      0   1   2   3   4   5
 *
 * Then the interval a is _before_ the interval b.
 *
 * Created by christian on 23/05/17.
 */
public class AllenBefore extends QRelationAllen {

    public static final String NAME = "AllenBefore";

    static {
        QRelationFactory.registerConstructor(AllenBefore.class, "Bf" );
    }

    public AllenBefore(String name, int firstArgumentID, int secondArgumentID, int index){
        this.firstArgumentID = firstArgumentID;
        this.secondArgumentID = secondArgumentID;
        this.index = index;
        this.isInterval = false;
    }

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder("<(");
        sb.append(firstArgumentObject.toString());
        sb.append(",");
        sb.append(secondArgumentObject.toString());
        sb.append(")");
        return sb.toString();
    }

    @Override
    public Collection<ArrayList<String>> rewrite( Map<Integer, String> idToName){
        ArrayList<ArrayList<String>> filters = new ArrayList<>();

        // Add first condition
        ArrayList<String> newFilter = new ArrayList<>();
        newFilter.add(QRelationAllenUtilities.getLess(firstArgumentObject.getClass()));
        newFilter.add(idToName.get(firstArgumentVariableID));
        newFilter.add(firstArgumentObject.toString());
        filters.add(newFilter);
        // Add second condition
        newFilter = new ArrayList<>();
        newFilter.add(QRelationAllenUtilities.getLess(firstArgumentObject.getClass()));
        newFilter.add(idToName.get(secondArgumentVariableID));
        newFilter.add(firstArgumentObject.toString());
        filters.add(newFilter);
        return filters;
    }

    @Override
    public Set<int[]> apply(Index index) {
        // This lookup returns all tuples which contain intervals starting before the start point of referenced interval.
        return index.searchInterval(QRelationAllenUtilities.getMinValue(firstArgumentObject.getClass()), QRelationAllenUtilities.decrement(
            firstArgumentID));
    }
}
