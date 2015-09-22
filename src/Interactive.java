package de.dfki.lt.hfc;

import java.io.*;
import java.util.*;
import jline.*;

/**
 * commands start with a keyword and additional arguments (possibly empty),
 * potentially stretching over several lines;
 *
 * single-line commands:
 *   new <namespaceFile>
 *   new <namespaceFile> <tupleFile>
 *   new <namespaceFile> <tupleFile> <ruleFile>
 *   truncate <integer>
 *   quit OR exit
 *   tuples <tupleFile>
 *   add <tuple>
 *   delete <tuple>
 *   rules <ruleFile>
 *   closure
 *   commands <commandFile>
 *   ask <tuple>
 *   save <tupleFile>
 *   dump <fastLoadFile>
 *   load <fastLoadFile>
 *   open <queryOutputFile>
 *   close
 *   help
 * multiple-line commands, finished by an _empty_ line:
 *   select <query>
 *   selectall <query>
 *
 * @see de.dfki.lt.hfc.Query
 *
 * @author (C) Hans-Ulrich Krieger
 * @since JDK 1.5
 * @version Fri Jul  5 19:12:29 CEST 2013
 */
public class Interactive {

	/**
	 * the following public fields are usually assinged values during the interaction loop
	 */
	public Namespace namespace = null;
	public TupleStore tupleStore = null;
	public RuleStore ruleStore = null;
	public ForwardChainer forwardChainer = null;
	public Query query = null;
	
	/**
	 * this setting is used for output encoding in Interactive
	 * @see TupleStore.INPUT_CHARACTER_ENCODING
	 * @see TupleStore.OUTPUT_CHARACTER_ENCODING
	 */
	public static final String OUTPUT_CHARACTER_ENCODING = "UTF-8";
	
	/**
	 * JLine's console reader
	 */
	private ConsoleReader consoleReader = null;
	
	/**
	 * this print stream is needed to redirect the output obtained by a query
	 */
	private PrintStream queryOutput = null;
	
	/**
	 * a negative truncate value indicates NO truncation
	 */
	public int truncate = -1;
	
	/**
	 * used by the main() method to invoke the interaction
	 */
	public Interactive() {
		readEvalPrint(System.in);
	}
	
	/**
	 * used by the main() method to evaluate commands stored in commandFile;
	 * interaction is NOT allowed afterwards
	 */
	public Interactive(String commandFile) throws Exception {
		this.consoleReader = new ConsoleReader(new FileInputStream(commandFile),
																					 new OutputStreamWriter(System.out));
		readEvalPrintLoop();
		readEvalPrint(System.in);
	}
	
	/**
	 *
	 */
	public Interactive(String namespaceFile, String tupleFile) {
		this.namespace = new Namespace(namespaceFile);
		this.tupleStore = new TupleStore(this.namespace, tupleFile);
		this.query = new Query(this.tupleStore);
		readEvalPrint(System.in);
	}
	
	/**
	 *
	 */
	public Interactive(String namespaceFile, String tupleFile, String ruleFile) {
		this(namespaceFile, tupleFile);
		this.ruleStore = new RuleStore(this.namespace, this.tupleStore, ruleFile);
		this.forwardChainer = new ForwardChainer(this.namespace, this.tupleStore, this.ruleStore);
		readEvalPrint(System.in);
	}
	
	/**
	 *
	 */
	public Interactive(ForwardChainer fc) {
		this.namespace = fc.namespace;
		this.tupleStore = fc.tupleStore;
		this.query = new Query(this.tupleStore);
		this.ruleStore = fc.ruleStore;
		this.forwardChainer = fc;
		readEvalPrint(System.in);
	}
	
	/**
	 * single-line commands:
	 *   new <namespaceFile>
	 *   new <namespaceFile> <tupleFile>
	 *   new <namespaceFile> <tupleFile> <ruleFile>
	 *   truncate <integer>
	 *   quit OR exit
	 *   tuples <tupleFile>
	 *   add <tuple>
	 *   delete <tuple>
	 *   rules <ruleFile>
	 *   closure
	 *   commands <commandFile>
	 *   ask <tuple>
	 *   save <tupleFile>
	 *   dump <fastLoadFile>
	 *   load <fastLoadFile>
	 *   open <queryOutputFile>
	 *   close
	 *   help
	 * multiple-line commands, finished by an _empty_ line:
	 *   select <query>
	 *   selectall <query>
	 */
	protected void readEvalPrint(InputStream in) {
		try {
			this.consoleReader = new ConsoleReader(in, new OutputStreamWriter(System.out));
			this.queryOutput = new PrintStream(System.out, true, Interactive.OUTPUT_CHARACTER_ENCODING);
			readEvalPrintLoop();
		}
		catch (Exception e) {
			System.err.println(e.getMessage());
		}
		// restart loop in case of a query parse exception
		finally {
			readEvalPrint(in);
		}
	}
	
	private void readEvalPrintLoop() throws Exception {
		String line, upline;
		this.consoleReader.printNewline();
		while ((line = this.consoleReader.readLine("hfc> ")) != null) {
			line = line.trim();
			upline = line.toUpperCase();
			if (upline.startsWith("SELECT") || upline.startsWith("SELECTALL"))
				processSelect(line);
			else if (upline.startsWith("ASK"))
				processAsk(line);
			else if (upline.startsWith("TRUNCATE"))
				processTruncate(line);
			else if (upline.startsWith("TUPLE"))  // handles "TUPLE" and "TUPLES"
				processTuples(line);
			else if (upline.startsWith("ADD"))
				processAdd(line);
			else if (upline.startsWith("DELETE"))
				processDelete(line);
			else if (upline.startsWith("RULE"))  // handles "RULE" and "RULES"
				processRules(line);
			else if (upline.startsWith("CLOSURE"))
				processClosure();
			else if (upline.startsWith("NEW"))
				processNew(line);
			else if (upline.startsWith("SAVE"))
				processSave(line);
			else if (upline.startsWith("DUMP"))
				processDump(line);
			else if (upline.startsWith("LOAD"))
				processLoad(line);
			else if (upline.startsWith("OPEN"))
				processOpen(line);
			else if (upline.startsWith("CLOSE"))
				processClose();
			else if (upline.startsWith("CLEANUP"))
				processCleanUp();
			else if (upline.startsWith("COMMAND"))  // handles "COMMAND" and "COMMANDS"
				processCommands(line);
			else if (upline.startsWith("HELP"))
				processHelp();
			else if (upline.startsWith("QUIT") || upline.startsWith("EXIT"))
				processQuit();
			else {
				this.consoleReader.printString("  wrong command: try 'help'");
				this.consoleReader.printNewline();
			}
		}		
	}

	/**
	 *
	 */
	private void processCleanUp() throws IOException {
		this.consoleReader.printString(this.tupleStore.cleanUpTupleStore() + " " + this.tupleStore.allTuples.size());
		this.consoleReader.printNewline();
		//this.consoleReader.printString(this.tupleStore.uriToProxy);
		//this.consoleReader.printNewline();
		//this.consoleReader.printString(this.tupleStore.proxyToUris);
		//this.consoleReader.printNewline();
	}
	
	/**
	 *
	 */
	private void processSelect(String firstLine) throws IOException, QueryParseException {
		String line;
		while ((line = this.consoleReader.readLine()).length() != 0) {
			firstLine = firstLine + " " + line;
		}
		BindingTable bt = this.query.query(firstLine);
		if (bt == null) {
			this.consoleReader.printString("  query contains constants not known to the tuple store");
			this.consoleReader.printNewline();
			return;
		}
		bt.tupleStore = this.tupleStore;
		this.queryOutput.println(firstLine);
		if (this.truncate <= 0)
			// no truncation
			this.queryOutput.println(bt.toString());
		else
			this.queryOutput.println(bt.toString(this.truncate));
	}
	
	/**
	 * checks whether a single ground tuple (tuple NOT containing any variable)
	 * exists in the tuple store;
	 * full ASK queries can be simulated by using SELECT, followed by a test whether
	 * the result is empty or not
	 */
	private void processAsk(String line) throws IOException {
		StringTokenizer st = new StringTokenizer(line);
		// get rid of 'ask'
		st.nextToken();
		ArrayList<String> tuple = new ArrayList<String>();
		while (st.hasMoreTokens())
			tuple.add(st.nextToken());
		this.consoleReader.printString(Boolean.toString(this.tupleStore.ask(tuple)));
		this.consoleReader.printNewline();
	}
	
	/**
	 *
	 */
	private void processTruncate(String line) {
		StringTokenizer st = new StringTokenizer(line);
		// get rid of 'truncate'
		st.nextToken();
		this.truncate = Integer.parseInt(st.nextToken());
	}
	
	/**
	 *
	 */
	private void processTuples(String line) {
		StringTokenizer st = new StringTokenizer(line);
		// get rid of 'tuples'
		st.nextToken();
		this.forwardChainer.uploadTuples(st.nextToken());
	}
	
	/**
	 *
	 */
	private void processAdd(String line) {
		StringTokenizer st = new StringTokenizer(line);
		// get rid of 'add'
		st.nextToken();
		String token;
		ArrayList<String> tuple = new ArrayList<String>();
		while (st.hasMoreTokens())
			tuple.add(st.nextToken());
		this.tupleStore.addTuple(tuple, 1);
	}
	
	/**
	 *
	 */
	private void processDelete(String line) throws IOException {
		StringTokenizer st = new StringTokenizer(line);
		// get rid of 'delete'
		st.nextToken();
		String token;
		int[] tuple = new int[st.countTokens()];
		int i = 0;
		while (st.hasMoreTokens())
			tuple[i++] = this.tupleStore.objectToId.get(st.nextToken());
		this.consoleReader.printString(Boolean.toString(this.tupleStore.removeTuple(tuple)));
		this.consoleReader.printNewline();
	}
	
	/**
	 *
	 */
	private void processRules(String line) {
		StringTokenizer st = new StringTokenizer(line);
		// get rid of 'rules'
		st.nextToken();
		this.forwardChainer.uploadRules(st.nextToken());
	}
	
	/**
	 *
	 */
	private void processClosure() {
		this.forwardChainer.computeClosure();
	}
	
	/**
	 *
	 */
	private void processSave(String line) {
		StringTokenizer st = new StringTokenizer(line);
		// get rid of 'save'
		st.nextToken();
		this.tupleStore.writeTuples(st.nextToken());
	}
	
	/**
	 *
	 */
	private void processDump(String line) {
		StringTokenizer st = new StringTokenizer(line);
		// get rid of 'dump'
		st.nextToken();
		this.tupleStore.writeTupleStore(st.nextToken());
	}
	
	/**
	 *
	 */
	private void processLoad(String line) {
		StringTokenizer st = new StringTokenizer(line);
		// get rid of 'load'
		st.nextToken();
		this.tupleStore.readTupleStore(st.nextToken());
	}
	
	/**
	 *
	 */
	private void processOpen(String line) {
		StringTokenizer st = new StringTokenizer(line);
		// get rid of 'open'
		st.nextToken();
		String filename = st.nextToken();
		try {
			this.queryOutput = new PrintStream(new FileOutputStream(filename), true);
		}
		catch (IOException e) {
			System.err.println("Error while opening query output file " + filename);
			System.exit(1);
		}
	}
	
	/**
	 *
	 */
	private void processClose() {
		this.queryOutput.flush();
		this.queryOutput.close();
		this.queryOutput = System.out;
	}

	/**
	 *
	 */
	private void processCommands(String line) throws Exception {
		StringTokenizer st = new StringTokenizer(line);
		// get rid of 'commands'
		st.nextToken();
		String filename = st.nextToken();
		try {
			this.consoleReader.setInput(new FileInputStream(filename));
			readEvalPrintLoop();
		}
		catch (IOException e) {
			System.err.println("\nerror while reading commands from " + filename);
			System.exit(1);
		}
	}
	
	/**
	 *
	 */
	private void processHelp() throws IOException {
		this.consoleReader.printString("  single-line commands, starting with a keyword:");
		this.consoleReader.printNewline();
		this.consoleReader.printString("    new <namespaceFile> -- start new/overwrite old session, given a namespace file");
		this.consoleReader.printNewline();
		this.consoleReader.printString("    new <namespaceFile> <tupleFile> -- start new/overwrite old session, given a namespace and a tuple file");
		this.consoleReader.printNewline();
		this.consoleReader.printString("    new <namespaceFile> <tupleFile> <ruleFile> -- start new/overwrite old session, given a namespace, a tuple, and a rule file");
		this.consoleReader.printNewline();
		this.consoleReader.printString("    truncate <integer> -- set truncation to restrict output of SELECT queries; negative value means _no_ truncation");
		this.consoleReader.printNewline();
		this.consoleReader.printString("    quit (or exit) -- quit interactive shell");
		this.consoleReader.printNewline();
		this.consoleReader.printString("    tuples <tupleFile> -- upload tuples stored in a file (existing tuples will _not_ be overwritten)");
		this.consoleReader.printNewline();
		this.consoleReader.printString("    add <tuple> -- add a single tuple");
		this.consoleReader.printNewline();
		this.consoleReader.printString("    delete <tuple> -- delete a single tuple");
		this.consoleReader.printNewline();
		this.consoleReader.printString("    rules <ruleFile> -- upload rules stored in a file (existing rules will _not_ be overwritten)");
		this.consoleReader.printNewline();
		this.consoleReader.printString("    closure -- compute deductive closure w.r.t. set of tuples and set of rules");
		this.consoleReader.printNewline();
		this.consoleReader.printString("    commands <commandFile> -- upload commands stored in a file");
		this.consoleReader.printNewline();
		this.consoleReader.printString("    ask <tuple>");
		this.consoleReader.printNewline();
		this.consoleReader.printString("    cleanup -- apply equivalence class reduction");
		this.consoleReader.printNewline();
		this.consoleReader.printString("    save <tupleFile> -- save set of all tuples as a N-Triples file; if possible, use file extension \"nt\"");
		this.consoleReader.printNewline();
		this.consoleReader.printString("    dump <fastLoadFile> -- save set of all tuples as a fast-load file; if possible, use file extension \"ts\"");
		this.consoleReader.printNewline();
		this.consoleReader.printString("    load <fastLoadFile> -- load content of fast-load file (to upload N-Triples file, use \"tuples\" command)");
		this.consoleReader.printNewline();
		this.consoleReader.printString("    open <queryOutputFile> -- redirect query results to file");
		this.consoleReader.printNewline();
		this.consoleReader.printString("    close -- close query output file stream");
		this.consoleReader.printNewline();
		this.consoleReader.printString("    help -- call help (print this output)");
		this.consoleReader.printNewline();
		this.consoleReader.printString("  multiple-line commands, finished by an empty line:");
		this.consoleReader.printNewline();
		this.consoleReader.printString("    select <query> -- evaluate a QDL query (output is affected by value of truncate)");
		this.consoleReader.printNewline();
		this.consoleReader.printString("    selectall <query> -- evaluate a QDL query (output is affected by proxy expansion and by value of truncate)");
		this.consoleReader.printNewline();
	}
	
	/**
	 *
	 */
	private void processNew(String line) throws Exception {
		StringTokenizer st = new StringTokenizer(line);
		// get rid of 'new'
		st.nextToken();
		switch (st.countTokens()) {
			case 1:
				this.namespace = new Namespace(st.nextToken());
				this.tupleStore = new TupleStore(this.namespace);
				this.query = new Query(this.tupleStore);
				break;
			case 2:
				// code from binary constructor
				this.namespace = new Namespace(st.nextToken());
				this.tupleStore = new TupleStore(this.namespace, st.nextToken());
				this.query = new Query(this.tupleStore);
				break;
			case 3:
				// code from ternary constructor
				this.namespace = new Namespace(st.nextToken());
				this.tupleStore = new TupleStore(this.namespace, st.nextToken());
				this.query = new Query(this.tupleStore);
				this.ruleStore = new RuleStore(this.namespace, this.tupleStore, st.nextToken());
				this.forwardChainer = new ForwardChainer(this.namespace, this.tupleStore, this.ruleStore);
				break;
			default:
				this.consoleReader.printString("  wrong command: try 'help'");
				this.consoleReader.printNewline();
				break;
		}
	}
	
	/**
	 *
	 */
	private void processQuit() throws IOException {
		this.consoleReader.printNewline();
		if (this.forwardChainer != null)
			this.forwardChainer.shutdown();
		System.exit(0);
	}
		
	
	/**
	 * starts the read-eval-print loop, e.g.,
	 *   time java -server -cp .:../lib/trove-2.1.0.jar:../lib/jline-0_9_5.jar -Xmx13500m de/dfki/lt/hfc/Interactive ../src/resources/default.ns ../src/resources/default.eqred.nt ../src/resources/default.eqred.rdl
	 */
	public static void main(String[] args) throws Exception {
		if (args.length == 0)
			new Interactive();
		else
			new Interactive(args[0]);
	}
	
}
