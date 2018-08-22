package de.dfki.lt.hfc.server;

import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;

/**
 * an example XML-RPC HFC Client in Java
 * call main method with one or two arguments:
 * + HfcClient <command>
 * + HfcClient <command> <serverurl>
 * where
 * <command> ::= <select-query> |
 * <selectall-query> |
 * 'ASK' <tuple> |
 * 'UPLOAD' <filename> |
 * 'CLOSURE' |
 * 'STOP' |
 * 'START'
 *
 * @author Hans-Ulrich Krieger
 * @version Thu Jun 30 16:10:04 CEST 2011
 * @see de.dfki.lt.hfc.Query for a description of the EBNF for QDL queries
 */
public class HfcClient {

  /**
   * A basic LOGGER.
   */
  private static final Logger logger = LoggerFactory.getLogger(HfcClient.class);

  /**
   * define the server URL plus port number
   */
  private static String SERVER_URL = "http://penguin.dfki.uni-sb.de:1408";
  //private static String SERVER_URL = "http://localhost:1408";

  /**
   * call with, e.g.,
   * java -cp .:../lib/* de/dfki/lt/hfc/server/HfcClient 'select distinct ?p where ?s ?p ?o'
   * note the quotes (' ... ') to make the query a single string
   * alternatively, one might further supply the server URL as a second argiment, e.g.,
   * java -cp .:./lib/* de/dfki/lt/hfc/server/HfcClient 'select distinct ?p where ?s ?p ?o ?b ?e' http://localhost:1408
   */
  public static void main(String[] args) {
    if (args.length == 0) {
      logger.warn("too less arguments: HfcClient <command> [<serverurl>]");
      return;
    } else if (args.length > 2) {
      logger.warn("too much arguments: HfcClient <command> [<serverurl>]");
      return;
    } else if (args.length == 2)
      HfcClient.SERVER_URL = args[1];
    // otherwise use default server URL
    try {
      XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
      config.setServerURL(new URL(HfcClient.SERVER_URL));
      XmlRpcClient client = new XmlRpcClient();
      client.setConfig(config);
      Object[] params = new Object[]{args[0]};
      // dispatch according to the first token
      String command = args[0].trim().toUpperCase();
      // 'select', 'selectall', and 'ask' can be executed by ANY USER
      if (command.startsWith("SELECT") || command.startsWith("SELECTALL")) {
        String result = (String) client.execute("HFC.query", params);
        logger.info(result);
      } else if (command.startsWith("ASK")) {
        Boolean result = (Boolean) client.execute("HFC.ask", params);
        logger.info(result.toString());
      }
      // 'upload', 'closure', 'start', and 'stop' reserved to ADMINISTRATORS ONLY
      else if (command.startsWith("UPLOAD")) {
        String result = (String) client.execute("HFC.upload", params);
        logger.info(result);
      } else if (command.startsWith("CLOSURE")) {
        Boolean result = (Boolean) client.execute("HFC.closure", params);
        logger.info(result.toString());
      } else if (command.startsWith("START")) {
        Integer result = (Integer) client.execute("HFC.start", params);
        logger.info(result.toString());
      } else if (command.startsWith("STOP")) {
        Integer result = (Integer) client.execute("HFC.stop", params);
        logger.info(result.toString());
      } else {
        logger.info("  unknown command: " + command);
      }
    } catch (XmlRpcException exception) {
      logger.error("HfcClient: XML-RPC Fault #" +
              Integer.toString(exception.code) +
              ": " + exception.toString());
    } catch (Exception exception) {
      logger.error("HfcClient: " + exception.toString());
    }
  }

}
