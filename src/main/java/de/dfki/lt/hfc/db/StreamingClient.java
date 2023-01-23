package de.dfki.lt.hfc.db;

import java.util.Set;

public interface StreamingClient {

  public void init(HfcDbHandler handler);

  public void compute(Set<String> affectedUsers);

}
