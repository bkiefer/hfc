package de.dfki.lt.hfc.db;

public class QueryException extends RuntimeException {

  private static final long serialVersionUID = -3209618400889821347L;

  public QueryException(String msg) {
    super(msg);
  }

  public String getWhy() {
    return getMessage();
  }

}
