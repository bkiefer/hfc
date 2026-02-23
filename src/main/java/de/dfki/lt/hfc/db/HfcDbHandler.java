package de.dfki.lt.hfc.db;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.dfki.lt.hfc.BindingTable;
import de.dfki.lt.hfc.BindingTableIteratorException;
import de.dfki.lt.hfc.Config;
import de.dfki.lt.hfc.Hfc;
import de.dfki.lt.hfc.TupleIterator;
import de.dfki.lt.hfc.WrongFormatException;
import de.dfki.lt.hfc.db.rdfProxy.DbClient;
import de.dfki.lt.hfc.io.QueryParseException;
import de.dfki.lt.hfc.types.AnyType;
import de.dfki.lt.hfc.types.XsdLong;

public class HfcDbHandler implements DbClient {

  protected static Logger logger = LoggerFactory.getLogger(HfcDbHandler.class);

  protected Hfc _hfc;

  private ReadWriteLock _rwLock;

  private Writer persistencyWriter;

  private int _r = 0;

  private Map<String, String> _currentTokens;

  /** To allow for dependent functionality to be informed about changes in the
   *  database
   */
  protected StreamingClients _streamingClients;

  private UserCache _userCache;

  private void init(Config config) {
    _rwLock = new ReentrantReadWriteLock();
    persistencyWriter = null;
    _streamingClients = new StreamingClients();
    _currentTokens = new HashMap<>();
    _userCache = new UserCache(this);
    try {
      _hfc = new Hfc(config);
      persistencyWriter = _hfc.getPersistencyWriter();
      computeClosure();
    } catch (Exception ex) {
      throw new RuntimeException(ex);
    }
  }

  /** The methods in this class should be thread safe, except maybe for the
   *  initialization. Exceptions are thrown in RuntimeException.
   * @throws IOException
   * @throws WrongFormatException
   */
  public HfcDbHandler(String configPath) {
    try {
      init(Config.getInstance(configPath));
    } catch (Exception ex) {
      throw new RuntimeException(ex);
    }
  }


  /** The methods in this class should be thread safe, except maybe for the
   *  initialization. Exceptions are thrown in RuntimeException.
   * @throws IOException
   * @throws WrongFormatException
   */
  public HfcDbHandler(Config config) {
    init(config);
  }

  public StreamingClients getStreamingClients() {
    return _streamingClients;
  }

  public UserCache getCache() {
    return _userCache;
  }

  @Override
  public void registerStreamingClient(StreamingClient s) {
    _streamingClients.registerStreamingClient(s);
  }

  public synchronized void shutdown(){
    _streamingClients.shutdown();
    _hfc.shutdown();
  }

  public synchronized void shutdownNoExit(){
    _streamingClients.shutdown();
    _hfc.shutdownNoExit();
  }

  public void setPersistencyWriter(Writer out) {
    persistencyWriter = out;
  }

  public int ping() {
    return (_hfc != null ? -1 : 1);
  }

  private interface IWApply {
    public void apply() throws IOException, WrongFormatException;
  }

  private interface IApply extends IWApply {
    @Override
    public void apply() throws IOException;
  }

  private interface Apply extends IWApply {
    @Override
    public void apply();
  }

  private boolean withWriteLock(IWApply a) {
    try {
      _rwLock.writeLock().lockInterruptibly();
      a.apply();
    } catch (InterruptedException ex) {
      return false;
    } catch (Exception ex) {
      throw new RuntimeException(ex);
    }
    finally {
      _rwLock.writeLock().unlock();
    }
    return true;
  }

  public boolean computeClosure() throws IOException, WrongFormatException {
    return withWriteLock(new Apply() {
      @Override
      public void apply() {
        _hfc.computeClosure();
      }
    });
  }

  private static final String newline = " ." + System.getProperty("line.separator");

  /**
   * writes out time-stamped tuples to an external file;
   * called by insert() every time tuples are uploaded to/inserted in HFC-DB
   * @param t the tuples given as a table, as a List<List<String>>
   * @throws IOException
   */
  private int makePersistent(Table t, long timeStamp) {
    // HUK: for the moment as a first try, write out the table to file given by the PrintWriter
    int noOfTuples = 0;
    synchronized (persistencyWriter) {
      String timeString = timeStamp >= 0 ?
          "\"" + Long.toString(timeStamp) + "\"^^<xsd:long>" : "";
      try {
        logger.debug("Persisting new entries");
        for (List<String> tuple : t.rows) {
          for (String elem : tuple) {
            persistencyWriter.write(elem);
            persistencyWriter.write(' ');
          }
          persistencyWriter.write(timeString);
          persistencyWriter.write(newline);
          ++noOfTuples;
        }
        persistencyWriter.flush();
        logger.debug("Done persisting");
      } catch (IOException ex) {
        logger.error("Error writing to persistency stream :%s", ex);
      }
    }
    return noOfTuples;
  }

  // **********************************************************************
  // Internal PAL DB functionality
  // **********************************************************************

  public boolean addNamespace(String shortForm, String longForm) {
    return withWriteLock(new Apply() {
      @Override
      public void apply() {
        _hfc.addNamespace(shortForm, longForm);
      }
    });
  }

  /**
   * add a set of tuples to the data base, contained in the table t;
   * also make the information that is entered persistent
   * @param table     the table that contains the tuples to add to the storage
   * @param timeStamp if true, the current time will be added to all rows
   * @return the number of tuples added to the database
   * @throws TupleException   in case there are illegal tuples in the set
   */
  @Override
  public int insert(final Table table, final long timeStamp) {
    final int[] result = { 0 };
    try {
      if (persistencyWriter != null) {
        // make table content persistent; use a thread here in order not to wait;
        // makePersistant() has an exclusive lock on the file
        final Runnable task =
            new Runnable() { @Override
            public void run() { makePersistent(table, timeStamp); } };
        new Thread(task).start();
      }
      final String ts = new XsdLong(timeStamp).toString();
      // information derived from the closure computation will _not_ be stored
      boolean res = withWriteLock(new IWApply() {
        @Override
        public void apply() throws IOException, WrongFormatException {
          logger.debug("Adding data");
          if (timeStamp == -2) {
            result[0] = _hfc.addTuples(table.rows, null);
          } else {
            result[0] = _hfc.addTuples(table.rows, null, ts);
          }
          _hfc.computeClosure();
          logger.debug("Adding done");
        }});
      if (! res)
        result[0] = -1;
      if (result[0] > 0) {
        /* Run the streaming clients, making sure they are only called if the
         * last change occured at least NOTIFICATION_INTERVAL msecs before.
         * Otherwise, start a timer that ends NOTIFICATION_INTERVAL msecs after the
         * last change.
         */
        _streamingClients.startStreamingClients(
            _userCache.getAffectedUsers(table, timeStamp));
      }
    }
    catch (WrongFormatException ex) {
      logger.error("WrongFormatException: {}", ex.getMessage());
      throw new TupleException(ex.getMessage());
    }
    catch (QueryException qe) {
      logger.error("QueryException: {}", qe.getMessage());
    }
    catch (Exception e) {
      logger.error("Unexpected Error: {}", e);
      throw(e);
    }

    return result[0];
  }

  /**
   * Add a set of tuples to the data base, contained in the table t
   *
   * @param t
   *          the table that contains the tuples to add to the storage
   * @return the number of tuples added to the database
   * @throws TupleException   in case there are illegal tuples in the set
   */
  @Override
  public int insertPlain(Table t) {
    return insert(t, -2);
  }

  /**
   * Add a set of tuples to the data base, contained in the table t. This adds
   * the current time stamp to every tuple into the last position. All tuples in
   * the table get the same time stamp.
   *
   * @param t
   *          the table that contains the tuples to add to the storage
   * @return the number of tuples added to the database
   * @throws TupleException    in case there are illegal tuples in the set
   */
  @Override
  public int insert(Table t) {
    return insert(t, System.currentTimeMillis());
  }


  @Override
  public QueryResult selectQuery(String query) {
    try {
      _rwLock.readLock().lockInterruptibly();
      logger.debug("executing query: {}", query);
      BindingTable bt = _hfc.executeQuery(query);

      if (!bt.isEmpty()) {
        // System.out.println(bt);
        TupleIterator it = bt.iterator(bt.getVars());
        Table table = new Table();
        while (it.hasNext()) {
          AnyType[] row = it.nextAsHfcType();
          List<String> outRow = new ArrayList<String>(row.length);
          for (AnyType any : row) {
            outRow.add(any.toString());
          }
          table.addToRows(outRow);
        }
        QueryResult result =
            new QueryResult(Arrays.asList(bt.getVars()), table.getRows());
        return result;
      }
      // System.out.println("Empty result: no matching tuples");
      return new QueryResult(Collections.<String> emptyList(),
          Collections.<List<String>> emptyList());
    } catch (InterruptedException ex) {
      logger.error("Interrupted during query");
      return null;
    } catch (QueryParseException e) {
      String msg = "Wrong query: " + e.getMessage() + " for query: " + query;
      logger.error(msg);
      throw new QueryException(msg);
    } catch (BindingTableIteratorException e) {
      String msg = "Wrong use of BindingTableIterator: " + e
          + " for query: " + query;
      logger.error(msg);
      throw new QueryException(msg);
    } catch (Exception e) {
      String msg = "Unknown exception, possibly wrong query: " + e
          + " for query: " + query;
      logger.error(msg);
      throw new QueryException(msg);
    } finally {
      _rwLock.readLock().unlock();
    }
  }


  private interface Exec<T> {
    T exec(BindingTable bt)
        throws QueryParseException, BindingTableIteratorException, Exception;
  }

  protected <T> T withRWLock(String query, Exec<T> fn) {
    try {
      _rwLock.readLock().lockInterruptibly();
      logger.debug("executing query: {}", query);
      BindingTable bt = _hfc.executeQuery(query);
      return fn.exec(bt);
    } catch (InterruptedException e) {
      logger.error("Interrupted query: " + e.getMessage());
    } catch (QueryParseException e) {
      logger.error("Wrong query: " + e.getMessage());
    } catch (BindingTableIteratorException e) {
      logger.error("Wrong use of BindingTableIterator: " + e);
    } catch (Exception e) {
      logger.error("Unknown exception: " + e);
    } finally {
      _rwLock.readLock().unlock();
    }
    return null;
  }


  public List<String> selectListString(String query) {
    return withRWLock(query, new Exec<List<String>>() {
      @Override
      public List<String> exec(BindingTable bt)
          throws QueryParseException, BindingTableIteratorException, Exception {
        List<String> result = new ArrayList<String>();
        if (!bt.isEmpty()) {
          TupleIterator it = bt.iterator(bt.getVars());
          while (it.hasNext()) {
            AnyType[] row = it.nextAsHfcType();
            result.add(row[0].toString());
          }
        }
        return result;
      }
    });
  }

  public Map<String, String> selectMap(String query) {
    return withRWLock(query, new Exec<Map<String, String>>() {
      @Override
      public Map<String, String> exec(BindingTable bt)
          throws QueryParseException, BindingTableIteratorException, Exception {
        Map<String, String> result = new HashMap<String, String>();
        if (!bt.isEmpty()) {
          TupleIterator it = bt.iterator(bt.getVars());
          while (it.hasNext()) {
            AnyType[] row = it.nextAsHfcType();
            result.put(row[0].toString(), row[1].toString());
          }
        }
        return result;
      }
    });
  }

  public Map<String, List<String>> selectMultiMap(String query) {
    return withRWLock(query, new Exec<Map<String, List<String>>>() {
      @Override
      public Map<String, List<String>> exec(BindingTable bt)
          throws QueryParseException, BindingTableIteratorException, Exception {
        Map<String, List<String>> result = new HashMap<>();
        if (!bt.isEmpty()) {
          TupleIterator it = bt.iterator(bt.getVars());
          while (it.hasNext()) {
            AnyType[] row = it.nextAsHfcType();
            List<String> val = new ArrayList<String>(row.length - 1) ;
            for (int i = 1; i < row.length; ++i) {
              val.add(row[i].toString());
            }
            result.put(row[0].toString(), val);
          }
        }
        return result;
      }
    });
  }

  public List<List<String>> selectMultiList(String query) {
    return withRWLock(query, new Exec<List<List<String>>>() {
      @Override
      public List<List<String>> exec(BindingTable bt)
        throws QueryParseException, BindingTableIteratorException, Exception {
        List<List<String>> result = new ArrayList<List<String>>();
        if (!bt.isEmpty()) {
          TupleIterator it = bt.iterator(bt.getVars());
          while (it.hasNext()) {
            AnyType[] row = it.nextAsHfcType();
            List<String> val = new ArrayList<String>(row.length) ;
            for (int i = 0; i < row.length; ++i) {
              val.add(row[i].toString());
            }
            result.add(val);
          }
        }
        return result;
      }
    });
  }

  public String selectString(String query) {
    return withRWLock(query, new Exec<String>() {
      @Override
      public String exec(BindingTable bt)
          throws QueryParseException, BindingTableIteratorException, Exception {
        if (!bt.isEmpty()) {
          TupleIterator it = bt.iterator(bt.getVars());
          AnyType[] row = it.nextAsHfcType();
          return row[0].toString();
        }
        return "";
      }
    });
  }

  // Return the class of some object
  public String getType(String uri) {
    return selectString(
        "select distinct ?o where " + uri + " <rdf:type> ?o ?t");
  }

  /** Get the right language label for the URI under the given label property
   *  and for the given language tag
   */
  private String selectLabel(String uri, String property, String langTag) {
    return selectString(
        "select distinct ?l where " + uri + " " + property + " ?l ?_"
            + " filter HasLanguageTag ?l \"" + langTag + "\"");
  }

  /** Get the right language labels for the URIs under the given label property
   *  and for the given language tag
   */
  List<List<String>> selectLabels(List<String> uris, String property,
      String langTag){
    List<List<String>> result = new ArrayList<>();
    for (String uri : uris) {
      List<String> row = new ArrayList<>();
      row.add(uri);
      row.add(selectLabel(uri, property, langTag));
      result.add(row);
    }
    return result;
  }


  public static Table createTable(String[][] arrayTable){
    Table result = new Table();
    for(String[] row : arrayTable) {
      result.addToRows(Arrays.asList(row));
    }
    return result;
  }

  /**
   *
   * @param groundTuple a ground tuple, i.e., a syntactically-legal
   *                    tuple without variables
   * @return true iff the tuple store contains groundTuple;
   *         false, otherwise
   * @throws QueryException
   */
  @Override
  public boolean askQuery(String groundTuple) {
    throw new UnsupportedOperationException();
    // TODO Implement this method!
    // HUK: in principle, _hfc._tupleStore.ask(query) would do the
    // job, but _tupleStore is private;
    // i.e., add again an additional method in class Hfc
    //return false;
  }

  // ======================================================================
  // High-level API calls
  // ======================================================================

  public static final String VALUE_INVALID = "<owl:Nothing>";

  /**
   * Transfer values from multi-valued predicate (non-functional)
   *
   * If values is empty, the value set is the special INVALID value
   *
   * @throws TupleException
   */
  private int insertValues(String uri, String predicate, Set<String> values) {
    Table t = new Table();
    if (values.isEmpty()) {
      String[] newRow = { uri, predicate, VALUE_INVALID };
      t.addToRows(Arrays.asList(newRow));
    } else {
      for (String v : values) {
        String[] newRow = { uri, predicate, v };
        t.addToRows(Arrays.asList(newRow));
      }
    }
    return insert(t, System.currentTimeMillis());
  }

  /**
   * Return all values from a multi-valued predicate (non-functional)
   *
   * Assumes that for a non-functional property, all values have the same
   * timestamp.
   * @throws TupleException, QueryException
   */
  @Override
  public Set<String> getMultiValue(String uri, String property) {
    QueryResult r = selectQuery(
        "select ?v ?ts where " + uri + ' ' + property + " ?v ?ts "
            + "AGGREGATE ?val = LGetLatest2 ?v ?ts \"1\"^^<xsd:int>");
    Set<String> result = new HashSet<String>();
    if (r != null) {
      for (List<String> l : r.getTable().getRows()) {
        result.add(l.get(0));
      }
    }
    result.remove(VALUE_INVALID);
    return result;
  }

  /**
   * Add new value to multi-valued property (non-functional)
   *
   * @throws TupleException, QueryException
   */
  @Override
  public int addToMultiValue(String uri, String property, String childUri) {
    Set<String> currentValues = getMultiValue(uri, property);
    currentValues.add(childUri);
    return insertValues(uri, property, currentValues);
  }

  /**
   * Remove value from multi-valued property (non-functional)
   *
   * @throws TupleException, QueryException
   */
  @Override
  public int removeFromMultiValue(String uri, String property, String childUri) {
    Set<String> currentValues = getMultiValue(uri, property);
    currentValues.remove(childUri);
    return insertValues(uri, property, currentValues);
  }

  /**
   * Set the non-functional value under a predicate to a set of values
   *
   * @throws TupleException, QueryException
   */
  @Override
  public int setMultiValue(String uri, String property, Set<String> values) {
    return insertValues(uri, property, values);
  }
  /**
   * Return a new unique object from a certain type
   *
   * @throws TupleException
   */
  @Override
  public String getNewId(String namespace, String type) {
    int nameidx = type.indexOf('#');
    if (nameidx < 0)
      nameidx = type.indexOf(':');
    String newUri = '<' + namespace //+ ':' // could also be a long namespace!
        + type.substring(nameidx + 1, type.length() - 1)
        + Integer.toHexString((int) System.currentTimeMillis())
        + '_';
    synchronized(this) {
      newUri = newUri + (++_r) + '>';
    }
    setValue(newUri, "<rdf:type>", type);
    logger.debug("new URI: {}", newUri);
    return newUri;
  }

  /**
   * once you enter or change a "simple" field like name, birthday, etc.
   *
   * @throws TupleException
   */
  @Override
  public int setValue(String uri, String fieldName, String value) {
    Table t = new Table();
    String[] row = { uri, fieldName, value };
    t.addToRows(Arrays.asList(row));
    return insert(t);
  }

  /**
   * Assumes that fieldName is the name of a functional property. Returns the
   * value of that property, if it's a URI, as string.
   *
   * @throws QueryException
   * @throws TupleException
   */
  @Override
  public String getValue(String uri, String fieldName) {
    Set<String> vals = getMultiValue(uri, fieldName);
    if (vals.size() > 1)
      logger.error("More than one value for functional property {} of {}",
          fieldName, uri);
    return vals.isEmpty() ? VALUE_INVALID : vals.iterator().next();
  }

  public boolean isOfType(String uri, String classUri) {
    if (uri == null) return false;
    try {
      return getMultiValue(uri, "<rdf:type>").contains(classUri);
    } catch (TupleException | QueryException e) {
      logger.error("Error retrieving type of element: {}", e.getMessage());
    }
    return false;
  }

  public String getProfessionalFromToken(String authToken) {
    String uri = getUriFromToken(authToken);
    if (isOfType(uri, "<dom:Professional>")) return uri;
    return null;
  }

  // ######################################################################
  // Security Token methods
  // ######################################################################

  public void registerToken(String authToken, String uri) {
    logger.info("Register token for {}", uri);
    _currentTokens.put(authToken, uri);
    this._userCache.initUser(uri);
  }

  public void unregisterToken(String authToken) {
    logger.info("Unregister token for {}", getUriFromToken(authToken));
    _currentTokens.remove(authToken);
  }

  public boolean tokenMatchesUri(String authToken, String uri) {
    String u = getUriFromToken(authToken);
    return u != null && u.equals(uri);
  }

  public String getUriFromToken(String token) {
    return _currentTokens.get(token);
  }

  public void dump(String name) {
    _hfc.writeTuples(name);
  }

  //////////////////////////////////////////////////////////////////////
  // Misc. Utils
  //////////////////////////////////////////////////////////////////////

  private static String capitalize(String in) {
    return in.length() == 0 ? in
            : in.substring(0, 1).toUpperCase()
            + in.substring(1);
  }

  /**
   * Put all data of second into first, and return first
   *
   * Requires that both maps have the same keySet!
   *
   * This condition is *NOT* checked.
   */
  private static Map<String, List<String>> union(
          Map<String, List<String>> first, Map<String, List<String>> second) {
    for (String key : first.keySet()) {
      first.get(key).addAll(second.get(key));
    }
    return first;
  }

  /**
   * Put all data of second into first, and return first
   *
   * To get a proper "table", the rows in each table should a) represent the
   * same variable (same order) b) have the same length
   *
   * This condition is *NOT* checked.
   */
  private static List<List<String>> union(List<List<String>> first,
                                          List<List<String>> second) {
    first.addAll(second);
    return first;
  }

  private static void addRow(List<List<String>> rows, String... args) {
    rows.add(Arrays.asList(args));
  }
}
