package de.dfki.lt.hfc.server;

import de.dfki.lt.hfc.Config;
import de.dfki.lt.hfc.ForwardChainer;
import de.dfki.lt.hfc.Query;
import de.dfki.lt.hfc.WrongFormatException;
import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.server.PropertyHandlerMapping;
import org.apache.xmlrpc.server.XmlRpcServer;
import org.apache.xmlrpc.webserver.WebServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * establishes an XML-RPC server for querying information from the repository;
 * starting the server means to call the main method with exactly four arguments
 *
 * @author Hans-Ulrich Krieger
 * @version Thu Jun 30 16:10:04 CEST 2011
 */
public class HfcServer {

  /**
   * A basic LOGGER.
   */
  private static final Logger logger = LoggerFactory.getLogger(HfcServer.class);

  /**
   * the port number for the web server
   */
  private int port;

  /**
   * forward chainer generated from the input args of the main method
   */
  private ForwardChainer hfc;

  /**
   * the query object generated form the tuple store sitting inside the
   * forward chainer
   */
  private Query query;

  /**
   * the web server that embodies the XML-RPC server
   */
  private WebServer webServer;

  /**
   * called by the main method and is given EXACTLY four arguments:
   * + port number
   * + path to namespace directory
   * + path to tuple directory
   * + path to rule directory
   * <p>
   * NOTE: subdirectories, i.e., recursive embeddings of files are NOT allowed
   *
   * @throws IOException
   * @throws WrongFormatException
   * @throws FileNotFoundException
   */
  private HfcServer(String[] args) throws FileNotFoundException, WrongFormatException, IOException {
    this.port = Integer.parseInt(args[0]);
    String configFile = args[1];
    String[] tuples = (new File(args[2])).list();
    String[] rules = (new File(args[3])).list();
    // remove files which start with '.' or end with '~'
    if (!configFile.endsWith(".yml")) {
      logger.error("  config file " + args[1] + " is not valid");
      System.exit(1);
    }
    // construct minimal forward chainer
    this.hfc = new ForwardChainer(Config.getInstance(configFile));
    // upload additional namespace, tuple, and rule files, if specified
    // always compute closure
    this.hfc.computeClosure();
    // if equivalence class reduction is turned on and a cleanup has been performed,
    // further closure computations might be necessary, since cleanups are performed
    // after a closure computation which potentially make passive rules active again
    if (this.hfc.isEquivalenceClassReduction() && this.hfc.isCleanUpRepository()) {
      while (this.hfc.computeClosure()) {
      }
    }
    // construct query store
    this.query = new Query(this.hfc.tupleStore);
  }

  /**
   * the main method requires EXACTLY four (4) arguments:
   * + 1st arg: port number (int)
   * + 2nd arg: namespace directory (String)
   * + 3rd arg: tuple directory (String)
   * + 4th arg: rule directory (String)
   * <p>
   * call with, e.g.,
   * java -server -cp .:../lib/* -Xms800m -Xmx1200m de/dfki/lt/hfc/server/HfcServer 1408 ../resources/namespaces/ ../resources/tuples/ ../resources/rules/
   *
   * @throws IOException
   * @throws WrongFormatException
   * @throws FileNotFoundException
   */
  public static void main(String[] args) throws FileNotFoundException, WrongFormatException, IOException {
    if (args.length != 4) {
      logger.error("  wrong number of arguments; required (4): port-no namespace-dir tuple-dir rule-dir");
      System.exit(1);
    }
    logger.info("\n  starting HFC Server ... ");
    HfcServer hs = new HfcServer(args);
    hs.startServer();
  }

  /**
   * returns true iff the file does NOT start with a '.' and/or ends with a '~';
   * otherwise false is returned
   */
  private boolean isProperFile(String name) {
    if (name.charAt(0) == '.')
      return false;
    if (name.charAt(name.length() - 1) == '~')
      return false;
    return true;
  }

  /**
   * starts the server and assigns instance fields hfc and query to the static
   * class fields HFC and QUERY in class HfcServerApi
   */
  public synchronized void startServer() {
    try {
      HfcServerApi.HFC = this.hfc;
      HfcServerApi.QUERY = this.query;
      this.webServer = new WebServer(this.port);
      XmlRpcServer xmlRpcServer = this.webServer.getXmlRpcServer();
      PropertyHandlerMapping phm = new PropertyHandlerMapping();
      // HFC redirects the request to the static Query instance
      phm.addHandler("HFC", de.dfki.lt.hfc.server.HfcServerApi.class);
      xmlRpcServer.setHandlerMapping(phm);
      this.webServer.start();
      logger.info("\n  HFC server started, waiting for input ...");
    } catch (XmlRpcException exception) {
      logger.error("\n  HfcServer: XML-RPC Fault #" +
              Integer.toString(exception.code) +
              ": " + exception.toString());
    } catch (Exception exception) {
      logger.error("\n  HfcServer: " + exception.toString());
    }
  }

}
