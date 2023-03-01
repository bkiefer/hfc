package de.dfki.lt.hfc.db;

import java.util.Set;

public interface StreamingClient {

  public void compute(Set<String> affectedUsers);
}
