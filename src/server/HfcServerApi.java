package de.dfki.lt.hfc.server;

import java.io.File;
import java.util.ArrayList;
import java.util.StringTokenizer;
import de.dfki.lt.hfc.ForwardChainer;
import de.dfki.lt.hfc.Query;
import de.dfki.lt.hfc.BindingTable;
import de.dfki.lt.hfc.QueryParseException;
import org.apache.xmlrpc.webserver.WebServer;

/**
 * the server API that is separated from the server otself;
 * note that certain information is statically mirrored, e.g., HFC and QUERY
 *
 * @author Hans-Ulrich Krieger
 * @version Thu Jun 30 16:10:04 CEST 2011
 */
public class HfcServerApi {

	/**
	 * this class instance of class ForwardChainer is assigned when starting the HFC Web Server
	 * @see HfcServer.startServer()
	 */
	protected static ForwardChainer HFC;
	
	/**
	 * this class instance of class Query is assigned when starting the HFC Web Server
	 * @see HfcServer.startServer()
	 */
	protected static Query QUERY;
	
	/**
	 * queries the query store derived from HFC's tuple store;
	 * the query store is assigned globally to the static field QUERY
	 * @return a string representation of the query result/the binding table
	 * @return an error message/a warning if something has gone wrong
	 * NOTE: this method is supposed to be called by any user
	 */
	public synchronized String query(String query) {
		try {
			System.out.println("  query: " + query);
			long start = System.currentTimeMillis();
			BindingTable bt = HfcServerApi.QUERY.query(query);
			System.out.println("  query time: " + (System.currentTimeMillis() - start) + "ms\n");
			if (bt == null)
				// unknown URI
				return "  query contains unknown URI(s)";
			else
				return bt.toString();
		}
		catch (QueryParseException exception) {
			// adapt the form of the error message so that it can be
			// distinguished from the usual result table
			return "  malformed query: " + exception.getMessage();
		}
	}
	
	/**
	 * checks whether a ground tuple is an element of the set of all tuples
	 * @return true iff tuple is contained in the repository
	 * @return false iff tuple is NOT contained in the repository 
	 * NOTE: this method is supposed to be called by any user
	 */
	public synchronized Boolean ask(String command) {
		StringTokenizer st = new StringTokenizer(command);
		st.nextToken();  // get rid of 'ask' keyword
		ArrayList<String> tuple = new ArrayList<String>();
		while (st.hasMoreTokens())
			tuple.add(st.nextToken());
		return HfcServerApi.HFC.tupleStore.ask(tuple);
	}
	
	/**
	 * upload a tuple file
	 * @return true iff everything went well
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
			}
			else
				return "  error: file " + filename + " does not exist";
		}
		catch (Exception exception) {
			// be careful
			return "  error while uploading tuples: " + exception.getMessage();
		}
	}
	
	/**
	 * computes the deductive closure for the HFC repository
	 * @return true iff new tuples have been generated
	 * @return false iff NO further tuples have been generated
	 * NOTE: this method is supposed to be called ADMINISTRATORS only
	 */
	public synchronized Boolean closure(String command) {
		boolean isNew = HfcServerApi.HFC.computeClosure();
		// if equivalence class reduction is turned on and a cleanup has been performed,
		// further closure computations might be necessary, since cleanups are performed
		// after a closure computation which potentially make passive rules again active
		if (HfcServerApi.HFC.tupleStore.equivalenceClassReduction && HfcServerApi.HFC.cleanUpRepository) {
			while (HfcServerApi.HFC.computeClosure()) {
			}
		}
		return isNew;
	}
	
	/**
	 * starts the HFC server, but NOT the web server;
	 * note: web server port remains unchanged
	 *
	 * NOTE: this method is supposed to be called ADMINISTRATORS only;
	 * NOTE: similar to the main method in class HfcServer without port number sepecification;
	 *
	 * USAGE:
	 *   start <namespaces> <rules> <tuples>^+
	 *
	 * NOTE: closure computation must be called explicitly
	 */
	public synchronized Integer start(String command) {
		// check whether another HFC is already running
		if (HfcServerApi.HFC != null) {
			System.out.println("  HFC already running: first call 'stop' command");
			return 1;
		}
		try {
			System.out.println("  starting up HFC server ...");
			// get rid of 'start'
			StringTokenizer st = new StringTokenizer(command);
			st.nextToken();
			final String namespaces = st.nextToken();
			final String rules = st.nextToken();
			final String tuples = st.nextToken();
			// new HFC
			HfcServerApi.HFC = new ForwardChainer(tuples, rules, namespaces);
			// further tuples to upload
			while (st.hasMoreTokens()) {
				HfcServerApi.HFC.uploadTuples(st.nextToken());
			}
			HfcServerApi.QUERY = new Query(HfcServerApi.HFC.tupleStore);
			return 0;  // to please XML-RPC
		}
		catch (Exception exception) {
			// be careful
			System.out.println("  error while starting HFC (too less arguments or wrong order): " + exception.getMessage());
			return 1;
		}
	}
	
	/**
	 * stops the HFC server, but NOT the web server
	 * @return 0 iff shutdown succeeds
	 * @return 1 iff something goes wrong
	 *
	 * NOTE: this method is supposed to be called ADMINISTRATORS only
	 */
	public synchronized Integer stop(String command) {
		try {
			System.out.println("  shutting down HFC server ...");
			HfcServerApi.HFC.shutdownNoExit();
			HfcServerApi.HFC = null;
			HfcServerApi.QUERY = null;
			return 0;
		}
		catch (Exception exception) {
			// be careful
			System.out.println("  error while shutting down HFC: " + exception.getMessage());
			return 1;
		}
	}

}