package de.dfki.lt.hfc.db;


import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.dfki.lt.hfc.db.rdfProxy.DbClient;

/** This class is used to improve the performance of streaming clients based
 *  on the set of users that is affected by some change in the database.
 *
 * @author kiefer
 *
 */
public class UserCache {

  private static final Logger logger = LoggerFactory.getLogger(UserCache.class);

  private Map<String, Set<String>> entity2affectedUsers = new HashMap<>();

  private final DbClient _dbClient;

  public UserCache(DbClient _dbClient) {
    this._dbClient = _dbClient;
  }


  public String toString() {
    StringBuilder strb = new StringBuilder();
    strb.append("-- UserCache -- \n");
    for (Map.Entry entry : entity2affectedUsers.entrySet()) {
      strb.append(entry + "\n");
    }
    return strb.toString();
  }


  public Set<String> getAffectedUsers(Table change, long currentTime) throws QueryException {
    Set<String> result = new HashSet<>();
    if(currentTime > 0l){
      for (List<String> tuple : change.getRows()) {
        getRecursive(new HashSet<>(), tuple, result);
      }
    }
    return result;
  }

  public void initUser(String uri) {
    @SuppressWarnings("serial")
    Set<String> affectedUsers = new HashSet<String>() {{ add(uri); }};
    this.entity2affectedUsers.put(uri, affectedUsers);
    Set<String> result = new HashSet<>();
    Set<String> visited = new HashSet<>();
    try {
      QueryResult queryResult = _dbClient.selectQuery(
          "Select ?p ?o ?t where " + uri + " ?p ?o ?t filter "
              + "LLess \"0\"^^<xsd:long> ?t");
      for(List<String> row :queryResult.table.getRows()){
        row.add(0,uri);
        getRecursive(visited, row, result);
      }
      // We have to handle the relations <dom:hasChildId> and <epmem:sharedWith> separately
      queryResult = _dbClient.selectQuery(
          "Select ?s ?t where ?s <dom:hasChildId> " + uri + " ?t filter "
              + "LLess \"0\"^^<xsd:long> ?t");
      for (List<String> row : queryResult.table.getRows()){
        row.add(2, uri);
        getRecursive(visited, row, result);
      }
      queryResult = _dbClient.selectQuery(
          "Select ?s ?t where ?s <epmem:sharedWith> " + uri + " ?t filter "
              + "LLess \"0\"^^<xsd:long> ?t");
      for (List<String> row : queryResult.table.getRows()){
        row.add(2, uri);
        getRecursive(visited, row, result);
      }
    } catch (QueryException e) {
      e.printStackTrace();
    }
  }

  private void workForward(Set<String> visited, String uri, Set<String> result) {
    if(!visited.contains(uri) && !uri.startsWith("\"")) {
      visited.add(uri);
      QueryResult subUp;
      try {
        subUp = _dbClient.selectQuery("Select ?p ?o ?t Where "
            + uri + " ?p ?o ?t Filter LLess \"0\"^^<xsd:long> ?t");
        for (List<String> row : subUp.table.getRows()) {
          row.add(0, uri);
          getRecursive(visited, row, result);
        }
      } catch (QueryException e) {
        logger.error("Recursive exploration fails for {}!", uri);
      }
    }
  }

  private void getRecursive(Set<String> visited, List<String> tuple,
      Set<String> result) {
    //base case
    String subject = tuple.get(0), object = tuple.get(2), pred = tuple.get(1);
    Set<String> affectedUsers;
    if( entity2affectedUsers.containsKey(subject)){
      affectedUsers = entity2affectedUsers.get(subject);
      result.addAll(affectedUsers);
      if (!object.startsWith("\"") && !isIgnored(pred)) {
        updateMap(object, affectedUsers);
      }
    } else {
      if (entity2affectedUsers.containsKey(object)) {
        affectedUsers = entity2affectedUsers.get(object);
        result.addAll(affectedUsers);
        if (!object.startsWith("\"") && !isIgnored(pred)) {
          updateMap(subject, affectedUsers);
        }
      } else { //recursion
        workForward(visited, subject, result);
        workForward(visited, object, result);
      }
    }
  }

  private void updateMap(String object, Set<String> affectedUsers) {
    entity2affectedUsers.merge(object, affectedUsers, (list1, list2) ->
    Stream.of(list1, list2)
    .flatMap(Collection::stream)
    .collect(Collectors.toSet()));
  }

  private boolean isIgnored(String pred){
    return pred.startsWith("<owl") || pred.startsWith("<rdf");
  }

  public Set<String> getEntity(String s) {
    return entity2affectedUsers.get(s);
  }
}
