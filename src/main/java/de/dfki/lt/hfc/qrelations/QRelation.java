package de.dfki.lt.hfc.qrelations;

import de.dfki.lt.hfc.QueryParseException;
import de.dfki.lt.hfc.indices.Index;
import de.dfki.lt.hfc.types.XsdAnySimpleType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;


/**
 * Most general class representing all kinds of Interval and RCC8 relations.
 * Created by christian on 19/05/17.
 */
public abstract class QRelation {

  /**
   * A basic LOGGER.
   */
  private static final Logger logger = LoggerFactory.getLogger(QRelation.class);

  protected int firstArgumentVariableID;
  protected int secondArgumentVariableID;
  protected int secondArgumentID;
  protected int firstArgumentID;
  protected int index;

  protected XsdAnySimpleType firstArgumentObject;

  protected XsdAnySimpleType secondArgumentObject;
  protected Class type;

  protected abstract boolean isValid();

  /**
   * Internalizes the QRelation by replacing the string representation of its arguments with new variables and adding these variables to the idToRelation mapping.
   * the original id of the arguments is stored within firstArgumentID and secondArgumentID
   *
   * @param nameToId        a mapping from string to their internalized representation
   * @param idToRelation    a mapping from ids to the corresponding QRelations
   * @param idToName        a mapping from internalized representation to the corresponding name
   * @param variableCounter global counter used to internalize variables
   * @param whereClause     The where clause this relation belongs to
   * @param foundVars
   * @return the new overall varcount
   */
  public int prepareMappings(Map<String, Integer> nameToId, Map<Integer, QRelation> idToRelation, Map<Integer, String> idToName, int variableCounter, List<Integer> whereClause, HashSet<String> foundVars) {
    //remove the interval from the whereClause
    String firstReplacementVariable = QRelationFactory.createNewVariable();
    firstArgumentVariableID = updateStatus(firstReplacementVariable, nameToId, idToName, --variableCounter, whereClause, 2);
    idToRelation.put(firstArgumentVariableID, this);
    foundVars.add(firstReplacementVariable);

    String secondReplacementVariable = QRelationFactory.createNewVariable();
    secondArgumentVariableID = updateStatus(secondReplacementVariable, nameToId, idToName, --variableCounter, whereClause, 1);
    idToRelation.put(secondArgumentVariableID, this);
    foundVars.add(secondReplacementVariable);
    return secondArgumentVariableID;
  }


  protected int updateStatus(String replacementVariable, Map<String, Integer> nameToId, Map<Integer, String> idToName, int variableCounter, List<Integer> whereClause, int dec) {
    String oldName;
    int oldId;
    if (replacementVariable != null) {
      nameToId.put(replacementVariable, variableCounter);
      idToName.put(variableCounter, replacementVariable);
    }
    oldId = whereClause.get(index - dec);
    oldName = idToName.get(whereClause.get(index - dec));
    idToName.remove(oldId);
    nameToId.remove(oldName);
    nameToId.remove(whereClause.get(index - dec));
    if (replacementVariable == null)
      whereClause.remove(index - dec);
    else
      whereClause.set(index - dec, variableCounter);
    return variableCounter;
  }


  /**
   * Rewrite the QRelation into filter clauses that simulate its behaviour.
   *
   * @param idToName a mapping form the internalized ids to their name (string rep)
   * @return The new filter clauses
   */
  public abstract Collection<ArrayList<String>> rewrite(Map<Integer, String> idToName) throws QueryParseException;


  /**
   * Performs the lookup associated with this QRelation using the given index.
   *
   * @param index The index to be searched.
   * @return The tuples that matched the search criteria.
   */
  public abstract Set<int[]> apply(Index index);


  public abstract boolean isAllenRelation();


  public abstract boolean isInterval();

  public Class getType() {
    return this.type;
  }

  public void setType(Class type) {
    this.type = type;
  }


}
