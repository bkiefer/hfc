package de.dfki.lt.hfc.db;

import java.util.ArrayList;
import java.util.List;

public class Table {
  public List<List<String>> rows;

  public Table() {
    rows = new ArrayList<>();
  }

  public Table(List<List<String>> r) {
    rows = r;
  }

  public void setRows(List<List<String>> r) {
    rows = r;
  }
  
  public List<List<String>> getRows() {
    return rows;
  }

  public int getRowsSize() {
    // TODO Auto-generated method stub
    return rows.size();
  }

  public void addToRows(List<String> newRow) {
    rows.add(newRow);
  }

  /** TODO: CANDIDATE FOR A CONVENIENCE LIBRARY FUNCTION */
  public List<String> projectColumn(int col) {
    List<String> result = new ArrayList<String>();
    for (List<String> row : getRows()) {
      result.add(row.get(col));
    }
    return result;
  }
}
