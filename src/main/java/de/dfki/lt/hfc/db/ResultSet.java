package de.dfki.lt.hfc.db;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import de.dfki.lt.hfc.WrongFormatException;
import de.dfki.lt.hfc.types.XsdAnySimpleType;

public class ResultSet implements Iterable<List<Object>> {

  private List<List<Object>> _table;

  public ResultSet() {
    _table = new ArrayList<List<Object>>();
  }

  public ResultSet(Table queryResult) {
    this();
    for (List<String> row : queryResult.getRows()) {
      List<Object> newRow = new ArrayList<Object>(row.size());
      for (String val : row) {
        try {
          newRow.add(XsdAnySimpleType.getXsdObject(val).toJava());
        }
        catch (WrongFormatException ex) {
          // should never happen
          System.out.println(ex);
        }
      }
      _table.add(newRow);
    }
  }

  @Override
  public Iterator<List<Object>> iterator() {
    return _table.iterator();
  }

}
