package de.dfki.lt.hfc.server;

import de.dfki.lt.hfc.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * the server API that is separated from the server otself;
 * note that certain information is statically mirrored, e.g., HFC and QUERY
 *
 * @author Hans-Ulrich Krieger
 * @version Thu Jun 30 16:10:04 CEST 2011
 */
public class HfcServerApi {


  /**
   * A basic LOGGER.
   */
  private static final Logger logger = LoggerFactory.getLogger(HfcServerApi.class);

  /**
   * this class instance of class ForwardChainer is assigned when starting the HFC Web Server
   *
   * @see HfcServer.startServer()
   */
  protected static Hfc HFC;

  /**
   * this class instance of class Query is assigned when starting the HFC Web Server
   *
   * @see HfcServer.startServer()
   */
  protected static Query QUERY;

  /**
   * queries the query store derived from HFC's tuple store;
   * the query store is assigned globally to the static field QUERY
   *
   * @return an error message/a warning if something has gone wrong
   * NOTE: this method is supposed to be called by any user
   */
  public synchronized String query(String query) {
    try {
      logger.info("  query: " + query);
      long start = System.currentTimeMillis();
      BindingTable bt = HfcServerApi.QUERY.query(query);
      logger.info("  query time: " + (System.currentTimeMillis() - start) + "ms\n");
      if (bt == null)
        // unknown URI
        return "  query contains unknown URI(s)";
      else
        return bt.toString();
    } catch (QueryParseException exception) {
      // adapt the form of the error message so that it can be
      // distinguished from the usual result table
      return "  malformed query: " + exception.getMessage();
    }
  }

  /**
   * checks whether a ground tuple is an element of the set of all tuples
   *
   * @return false iff tuple is NOT contained in the repository
   * NOTE: this method is supposed to be called by any user
   */
  public synchronized Boolean ask(String command) {
    StringTokenizer st = new StringTokenizer(command);
    st.nextToken();  // get rid of 'ask' keyword
    ArrayList<String> tuple = new ArrayList<String>();
    while (st.hasMoreTokens())
      tuple.add(st.nextToken());
    return HfcServerApi.HFC.ask(tuple);
  }

  /**
   * upload a tuple file
   *
   * @return an error message if upload process terminates abnormally
   * NOTE: this method is supposed to be called ADMINISTRATORS only
   */
  public synchronized String upload(String command) {
    try {
      // obtain second token, get rid of 'upload'
      StringTokenizer st = new StringTokenizer(command);
      st.nextToken();
      String filename = st.nextToken();
      // check whether there is such a file
      File file = new File(filename);
      if (file.exists()) {
        HfcServerApi.HFC.uploadTuples(filename);
        return "true";
      } else
        return "  error: file " + filename + " does not exist";
    } catch (Exception exception) {
      // be careful
      return "  error while uploading tuples: " + exception.getMessage();
    }
  }

  /**
   * computes the deductive closure for the HFC repository
   *
   * @return false iff NO further tuples have been generated
   * NOTE: this method is supposed to be called ADMINISTRATORS only
   */
  public synchronized Boolean closure(String command) {
    boolean isNew = HfcServerApi.HFC.computeClosure();
    // if equivalence class reduction is turned on and a cleanup has been performed,
    // further closure computations might be necessary, since cleanups are performed
    // after a closure computation which potentially make passive rules again active
    if (HfcServerApi.HFC.isEquivalenceClassReduction() && HfcServerApi.HFC.isCleanUpRepository()) {
      while (HfcServerApi.HFC.computeClosure()) {
      }
    }
    return isNew;
  }

  /**
   * starts the HFC server, but NOT the web server;
   * note: web server port remains unchanged
   * <p>
   * NOTE: this method is supposed to be called ADMINISTRATORS only;
   * NOTE: similar to the main method in class HfcServer without port number sepecification;
   * <p>
   * USAGE:
   * start <namespaces> <rules> <tuples>^+
   * <p>
   * NOTE: closure computation must be called explicitly
   */
  public synchronized Integer start(String command) {
    // check whether another HFC is already running
    if (HfcServerApi.HFC != null) {
      logger.info("  HFC already running: first call 'stop' command");
      return 1;
    }
    try {
      logger.info("  starting up HFC server ...");
      // get rid of 'start'
      StringTokenizer st = new StringTokenizer(command);
      st.nextToken();
      final String configName = st.nextToken();
      Config config = Config.getInstance(configName);
      // new HFC
      HfcServerApi.HFC = new Hfc(config);
      // further tuples to upload
      while (st.hasMoreTokens()) {
        HfcServerApi.HFC.uploadTuples(st.nextToken());
      }
      HfcServerApi.QUERY = HFC.getQuery();
      return 0;  // to please XML-RPC
    } catch (Exception exception) {
      // be careful
      logger.info("  error while starting HFC (too less arguments or wrong order): " + exception.getMessage());
      return 1;
    }
  }

  /**
   * stops the HFC server, but NOT the web server
   *
   * @return 1 iff something goes wrong
   * <p>
   * NOTE: this method is supposed to be called ADMINISTRATORS only
   */
  public synchronized Integer stop(String command) {
    try {
      logger.info("  shutting down HFC server ...");
      HfcServerApi.HFC.shutdown();
      HfcServerApi.HFC = null;
      HfcServerApi.QUERY = null;
      return 0;
    } catch (Exception exception) {
      // be careful
      logger.info("  error while shutting down HFC: " + exception.getMessage());
      return 1;
    }
  }

}