package de.dfki.lt.hfc.db;

import java.util.List;

public class QueryResult {
  public List<String> variables; // required
  public Table table; // required

  public QueryResult(List<String> v, List<List<String>> t) {
    variables = v;
    table = new Table(t);
  }

  public Table getTable() {
    return table;
  }

  public List<String> getVariables() {
    return variables;
  }

  public int getVariablesSize() {
    return variables.size();
  }
}
