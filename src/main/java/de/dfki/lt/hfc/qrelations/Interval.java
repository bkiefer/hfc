package de.dfki.lt.hfc.qrelations;

import de.dfki.lt.hfc.indices.Index;
import de.dfki.lt.hfc.QueryParseException;

import java.util.*;

/**
 * This class represents the intervals, both open and closed ones. These intervals may be used in queries as conditions.
 *
 * Created by christian on 19/05/17.
 */
public class Interval extends QRelationAllen{
    public static final String NAME = "Interval";

    /**
     * boolean flag indicating whether the begin of the is open or closed.
     */
    private final boolean isExclusiveE;

    /**
     * boolean flag indicating whether the end of the is open or closed.
     */
    private final boolean isExclusiveS;



    static {
        QRelationFactory.registerConstructor(Interval.class, "[]","[)", "(]", "()");

    }

    public Interval(String literal, int v1, int v2, int index)  {
        isExclusiveE = literal.endsWith("]");
        isExclusiveS = literal.startsWith("[");
        firstArgumentID = v1;
        secondArgumentID = v2;
        this.index = index;
        this.isInterval = true;
    }




    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder(this.isExclusiveS? "[":"(");
        sb.append(firstArgumentObject.toString());
        sb.append(",");
        sb.append(secondArgumentObject.toString());
        sb.append(this.isExclusiveE? "]":")");
        return sb.toString();
    }

    @Override
    public Collection<ArrayList<String>> rewrite( Map<Integer, String> idToName) throws QueryParseException {
        ArrayList<ArrayList<String>> filters = new ArrayList<>();
        // Add first condition
        ArrayList<String> f1 = new ArrayList<>();
        f1.add(isExclusiveS? QRelationAllenUtilities.getGreater(firstArgumentObject.getClass()): QRelationAllenUtilities.getGreaterEqual(
            firstArgumentObject.getClass()));
        f1.add(idToName.get(firstArgumentVariableID));
        f1.add(firstArgumentObject.toString());
        filters.add(f1);
        // Add second condition
        f1 = new ArrayList<>();
        f1.add(isExclusiveE? QRelationAllenUtilities.getLess(secondArgumentObject.getClass()): QRelationAllenUtilities.getLessEqual(
            secondArgumentObject.getClass()));
        f1.add(idToName.get(firstArgumentVariableID));
        f1.add(secondArgumentObject.toString());
        filters.add(f1);
        return filters;
    }

    @Override
    public Set<int[]> apply(Index index) {
        if (isExclusiveS)
            if ( isExclusiveE)
                return  index.searchInterval(QRelationAllenUtilities.increment(firstArgumentID),QRelationAllenUtilities.decrement(
                    secondArgumentID));
            else
                return  index.searchInterval(QRelationAllenUtilities.increment(firstArgumentID),
                    secondArgumentObject);
        else
            if ( isExclusiveE)
                return  index.searchInterval(firstArgumentObject,QRelationAllenUtilities.decrement(
                    secondArgumentID));
            else
                return  index.searchInterval(firstArgumentObject, secondArgumentObject);
    }



    @Override
    public int prepareMappings(Map<String, Integer> nameToId, Map<Integer, QRelation> idToRelation, Map<Integer, String> idToName, int variableCounter, List<Integer> whereClause, HashSet<String> foundVars){
        //remove the interval from the whereClause
        String v1 = QRelationFactory.createNewVariable();
        firstArgumentVariableID = updateStatus(v1, nameToId, idToName, --variableCounter, whereClause, 2);
        idToRelation.put(firstArgumentVariableID,this);
        updateStatus(null, nameToId, idToName, variableCounter, whereClause, 1);
        return variableCounter;
    }

}
