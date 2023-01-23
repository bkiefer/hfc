package de.dfki.lt.hfc.db.rdfProxy;

import java.util.Set;

import de.dfki.lt.hfc.db.QueryResult;
import de.dfki.lt.hfc.db.StreamingClient;
import de.dfki.lt.hfc.db.Table;
import de.dfki.lt.hfc.db.TupleException;

public interface DbClient {

  /** Register a streaming client */
  public void registerStreamingClient(StreamingClient sc);

  /** Return a new instance uri of class clazzUri in namespace
   *
   * @param  namespace the namespace for the instance
   * @param  clazzUri must point to a RDF classs
   * @return a new instance URI that is already stored in the DB
   */
  public String getNewId(String namespace, String clazzUri)
      throws TupleException;

  public boolean askQuery(String groundTuple);

  /** Execute a select query on the RDF DB and return the result
   *
   * @param  query a select query
   * @return a QueryResult object that contains a table and a mapping from
   *         variable names to columns.
   */
  public QueryResult selectQuery(String query);

  /** Return the value of the predicate under uri. Abstracts away from the
   *  representation of modifiable values, and assumes that predicate is a
   *  FunctionalPredicate
   *
   * @param  uri some object in the DB
   * @param  predicate the URI of a functional predicate
   * @return The value x of (<uri> <predicate> x ..) if it exists,
   *         null otherwise
   */
  public String getValue(String uri, String predicate);

  /** change a "simple" (functional) field like name, birthday, etc. */
  public int setValue(String uri, String fieldName, String value);

  /** Return all values from a multi-valued predicate (non-functional)
   *
   * Assumes that for a non-functional property, all values have the same
   * timestamp.
   */
  public Set<String> getMultiValue(String uri, String property);

  /** Set the value of a multi-valued property (non-functional) */
  public int setMultiValue(String uri, String property, Set<String> values);

  /** Add new value to multi-valued property (non-functional) */
  public int addToMultiValue(String uri, String property, String val);

  /** Remove value from multi-valued property (non-functional) */
  public int removeFromMultiValue(String uri, String property, String val);

  /** Insert a set of tuples and add the current time stamp (not provided) */
  public int insert(Table createTable);

  /** Insert a set of tuples as is, no adding of time stamp */
  public int insertPlain(Table createTable);
}
