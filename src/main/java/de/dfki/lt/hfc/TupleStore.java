package de.dfki.lt.hfc;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.dfki.lt.hfc.NamespaceManager.Namespace;
import de.dfki.lt.hfc.types.AnyType;
import de.dfki.lt.hfc.types.BlankNode;
import de.dfki.lt.hfc.types.Uri;
import de.dfki.lt.hfc.types.XsdAnySimpleType;
import de.dfki.lt.hfc.types.XsdString;

public class TupleStore extends TupleIntStore {
  private static final Logger logger = LoggerFactory
      .getLogger(TupleStore.class);

  /**
   * this setting is used for input encoding in TupleStore
   */
  public String inputCharacterEncoding = "UTF-8";

  /**
   * this setting is used for output encoding in TupleStore
   */
  public String outputCharacterEncoding = "UTF-8";

  /**
   * it seems reasonable to have tuples of at least length 1;
   * use value 3 to be compliant with RDF;
   * a similar variable exists in class RuleStore
   */
  public int minNoOfArgs = 3;

  /**
   * when tuples are read in, this variable decides whether tuples are compliant with
   * what RDF requests, viz., that the first argument is either an URI or a blank node,
   * and that the second arg is a URI
   */
  public boolean rdfCheck = true;

  /**
   * a constant that controls whether the system is terminated in case an invalid
   * tuple is read in (exit code = 1);
   * a similar variable exists in class RuleStore
   *
   * @see #verbose
   */
  public boolean exitOnError = false;

  /** Add a time stamp by default */
  private boolean addTS = false;

  /**
   * When reading multiple files, blank nodes in different files might have the
   * same name. To make it less likely there is a clash of blank node names, we
   * use this id generator to append a unique id for each round of reading.
   * TODO: this is not 100% safe.
   */
  protected int blankNodeSuffixNo = 0;

  String blankNodeSuffix = null;

  /**
   * used to generate unique blank node names for _this_ forward chainer
   */
  private final String _blankNodePrefix = "_:" + this.toString();

  /**
   * a namespace object used to expand short form namespaces into full forms
   */
  public NamespaceManager namespace;

  private Random r;

  public TupleStore(NamespaceManager nsm, IndexStore is, Config conf) {
    init(conf, is, new OperatorRegistry(this), new AggregateRegistry(this));
    if (this.indexStore != null) {
      indexStore.setTuplestore(this);
    }
    namespace = nsm;
    r = new Random(System.currentTimeMillis());

    inputCharacterEncoding = conf.getCharacterEncoding();
    outputCharacterEncoding = conf.getCharacterEncoding();
    minNoOfArgs = conf.getMinArgs();
    rdfCheck = conf.isRdfCheck();
    exitOnError = conf.isExitOnError();
  }

  /**
  * at several places, messages were output depending on this.exitOnError
  * and this.verbose -- unify this in this special private method;
  * perhaps will be replaced by Apache's log4j
  *
  * @throws WrongFormatException
  */
  boolean sayItLoud(int lineNo, String message, String args)
      throws WrongFormatException {
    logger.error("{}: {} {}", lineNo, message, args);
    if (this.exitOnError) {
      throw new WrongFormatException("  " + lineNo + " " + message + " " + args);
    }
    return false;
  }

  /**
   * same method without the line numbering
   */
  boolean sayItLoud(String message) {
    logger.error("{}", message);
    if (this.exitOnError) {
      throw new RuntimeException("FATAL ERROR: " + message);
    }
    return false;
  }

  public AnyType makeAnyType(String literal) {
    AnyType anyType;
    if (isUri(literal)) {
      Pair<Namespace, String> ns_name = namespace.separateNSfromURI(literal);
      anyType = new Uri(ns_name.second, ns_name.first);
    } else if (isBlankNode(literal)) {
      anyType = new BlankNode(literal);
    } else {
      int idx = literal.lastIndexOf('^');
      if (idx == -1) {
        // note: parseAtom() completes a bare string by adding "^^<xsd:string>",
        // but if the string has a language tag, nothing is appended, thus
        // '^' is missing (as is required by the specification)
        anyType = new XsdString(literal);
      }
      // now do the `clever' dispatch through mapping the type names to Java
      // class constructors: @see
      // de.dfki.lt.hfc.NamespaceManager.readNamespaces()
      else {
        try {
          anyType = XsdAnySimpleType.getXsdObject(literal);
        } catch (WrongFormatException e) {
          sayItLoud(e.getMessage());
          return null;
        }
      }
    }
    return anyType;
  }

  /**
   * internalizeTuple() maps array lists of strings to int arrays of unique
   * ints;
   * uses putObject() to generate new ints in case the string argument is
   * brand new, or retrieves the already generated int in case the string
   * argument has already been seen
   * <p>
   */
  public int[] internalizeTuple(List<String> stringTuple) {
    int[] intTuple = new int[stringTuple.size()];
    int i = 0;
    for (String s : stringTuple)
      intTuple[i++] = putObject(makeAnyType(s));
    return intTuple;
  }

  /**
   * internalizeTuple() maps string arrays to int arrays of unique ints;
   * uses putObject() to generate new ints in case the string argument is
   * brand new, or retrieves the already generated int in case the string
   * argument has already been seen
   * <p>
   */
  public int[] internalizeTuple(String... stringTuple) {
    int[] intTuple = new int[stringTuple.length];
    for (int i = 0; i < stringTuple.length; i++)
      intTuple[i] = putObject(makeAnyType(stringTuple[i]));
    return intTuple;
  }

  /**
   * readTuples() reads in a sequence of tuples from a text file;
   * tuples must be finished in a _single_ line, constrained by the following
   * side conditions:
   * + a tuple starts with an URI or a blank node
   * + tuples must have at least one argument
   * + elements of a tuple (URIs, blank nodes, XSD atoms) must be separated
   * by the space character ' '
   * + URIs start with "<" and end with ">" (both for short and long prefixes)
   * + blank nodes start with "_:"
   * + XSD atoms start with '"' and ends with '"', potentially followed by type
   * or language information; inside the leading string value, strings must
   * be enclosed by "\""
   * + comments only start with the '#' character at the very first position of
   * a line
   * + tuples need not end with the '.' character
   * <p>
   * this is essentially N-Triples syntax, see
   * Jan Grant & Dave Beckett: RDF Test Cases, 10 Feb 2004.
   * http://www.w3.org/TR/rdf-testcases/
   * <p>
   * NOTE: if equivalence class reduction is switched on, the cleanup mechanism
   * is always applied AFTER the whole file is read in, but not each time
   * an equivalence relation instance is detected! (more efficient at the
   * very end)
   * <p>
   * NOTE: tuples can be extended by at most one front element and several back
   * elements
   * <p>
   * example
   * <huk> <rdf:type> <Person> .
   * <huk> <dateOfBirth> "1960-08-14"^^<xsd:date> .
   * <huk> <worksFor> <dfki> .
   * <huk> <hasAge> "47"^^<xsd:int> .
   * <huk> <hasName> _:foo42 .
   * _:foo42 <firstName> "Uli" .
   * _:foo42 <lastName> "Krieger" .
   *
   * @param br
   * @param front use null to indicate that there is no front element
   * @param backs use an empty String array that there are no back elements
   * @throws IOException
   * @throws WrongFormatException
   */
  public void readTuples(TupleSource ts, String front, String... backs)
      throws IOException, WrongFormatException {
    try {
      // TODO: THIS IS NOT REALLY SAFE!!!!
      blankNodeSuffix = String.format("%0,3d", blankNodeSuffixNo);
      ++blankNodeSuffixNo;
      TupleParser parser = new TupleParser(ts.getReader(), ts.getOrigin(), this);
      parser.parse(front, backs);
    } finally {
      blankNodeSuffix = null;
    }
  }

  /**
   * this version of readTuples() adds one back element, viz., a time stamp given
   * as a Java long and represented by an XSD long
   *
   * @param br
   * @param timeStamp
   * @throws IOException
   * @throws WrongFormatException
   *
  private void readTuples(BufferedReader br, long timeStamp)
      throws IOException, WrongFormatException {
    readTuples(br, null, XsdLong.toString(timeStamp));
  }

  void readTuples(InputStream in) throws IOException, WrongFormatException {
    readTuples(new BufferedReader(new InputStreamReader(in,
        Charset.forName(this.inputCharacterEncoding))));
  }

  /**
   * read in tuples from a buffered reader
   *
   * @param br
   * @throws IOException
   * @throws WrongFormatException
   *
  private void readTuples(BufferedReader br)
      throws IOException, WrongFormatException {
    readTuples(br, null);
  }

  /**
   * read in the tuple file as it is
   *
   * @param filename
   * @throws FileNotFoundException
   * @throws IOException
   * @throws WrongFormatException
   *
  public void readTuples(BufferedReader br, boolean addTS)
      throws FileNotFoundException, IOException, WrongFormatException {
    //logger.info("\n  reading tuples from " + filename + " ...");
    if (addTS)
      readTuples(br, System.currentTimeMillis());
    else
      readTuples(br);
  }

  /**
   * read in the tuple file and add potential front and back elements to every tuple
   *
  public void readTuples(String filename, String front, String... backs)
      throws FileNotFoundException, IOException, WrongFormatException {
    logger.info("\n  reading tuples from " + filename + " ...");
    readTuples(Files.newBufferedReader(new File(filename).toPath(),
        Charset.forName(this.inputCharacterEncoding)), front, backs);
  }

  /**
   * helper for readTupleStore()
   *
  private void readIdToObject(BufferedReader br, int noOfLines)
      throws IOException {
    for (int i = noOfLines; i > 0; i--) {
      idToJavaObject.add(makeAnyType(br.readLine()));
    }
  }

  /**
   * helper for readTupleStore()
   *
  private void readAllTuples(BufferedReader br, int noOfLines)
      throws IOException {
    String line;
    int begin, end, length;
    int[] tuple;
    for (int i = noOfLines; i > 0; i--) {
      line = br.readLine();
      end = line.indexOf(' ');
      length = Integer.parseInt(line.substring(0, end));
      tuple = new int[length];
      begin = end + 1;
      for (int j = 0; j < length; j++) {
        end = line.indexOf(' ', begin);
        tuple[j] = Integer.parseInt(line.substring(begin, end));
        begin = end + 1;
      }
      addTuple(tuple);
    }
  }
  *

  public void readTuples(File tuples, long timestamp)
      throws WrongFormatException, IOException {
    logger.info("Read tuples from " + tuples.toPath());
    readTuples(Files.newBufferedReader(tuples.toPath(),
        Charset.forName(inputCharacterEncoding)),
        null, new XsdLong(timestamp).toString());
  }
  */

  private int getSymbolId(String symbol) {
    int id = putObject(symbol);
    return getProxy(id);
  }

  /**
   * TODO keep this?
   * Normalize namespaces, and get ids directly to put in the tuples without
   * using the hfc internal functions. Also, honor the equivalence reduction
   * by always entering the representative.
   *
   * @param rows  the table that contains the tuples to add to the storage
   * @param front the potentially-empty (== null) front element
   * @param backs arbitrary-many back elements (or an empty array)
   *              <p>
   *              This is done so i can add the <it>now<it/> time stamp transparently
   *              TODO: refactor, and make this part of HFC core
   */
  public int addTuples(List<List<String>> rows, String front, String... backs) {

    // normalize namespaces for front and backs
    int frontId = -1; // Java wants an initial value
    if (front != null)
      frontId = getSymbolId(front);
    int[] backIds = new int[backs.length];
    if (backs.length != 0) {
      for (int i = 0; i < backs.length; ++i)
        backIds[i] = getSymbolId(backs[i]);
    }
    // (front == null) means _no_ front element
    final int frontLength = (front == null) ? 0 : 1;
    final int backLength = backs.length;
    int[] tuple;
    int noOfTuples = 0;
    // extend and add tuples, given by parameters rows, and front and backs
    for (List<String> row : rows) {
      tuple = new int[row.size() + frontLength + backLength];
      int i = 0;
      // front element
      if (front != null) {
        tuple[0] = frontId;
        i = 1;
      }
      // table row
      for (String s : row) {
        tuple[i++] = getSymbolId(s);
      }
      // back elements
      if (backs.length != 0) {
        for (int j = 0; j < backs.length; ++j)
          tuple[i++] = backIds[j];
      }
      if (addTuple(tuple)) {
        ++noOfTuples;
      }
    }
    return noOfTuples;
  }


  /**
   * checks whether a tuple of proper length as specified by this.minNoOfArgs
   * and this.maxNoOfArgs;
   * <p>
   * also checks whether the arguments of a tuple are of the right "kind":
   * arg 1: URI or blank node
   * arg 2: URI
   * arg 3, 4, ... : URI, blank node or XSD atom
   * thus only make sure that first and second arg are OK;
   * <p>
   * finally check whether in case of a XSD atom, the atom is compatible
   * with the specified XSD type
   * ************************ NOT IMPLEMENTED YET ************************
   * <p>
   * depending on this.verbose and this.exitOnError, the method is
   * silent, outputs a warning, or exit the process
   *
   * @throws WrongFormatException
   * @see RuleStore#isValidTuple
   */
  public boolean isValidTuple(List<String> stringTuple, int lineNo)
      throws WrongFormatException {
    // check against min length
    if (stringTuple.size() < this.minNoOfArgs)
      return sayItLoud(lineNo, ": tuple too short", stringTuple.toString());
    // check against max length
    if (stringTuple.size() > this.maxNoOfArgs)
      return sayItLoud(lineNo, ": tuple too long", stringTuple.toString());
    // is tuple RDF compliant
    if (rdfCheck) {
      // check for valid first arg
      if ((stringTuple.size() > 0)
          && (isAtom(stringTuple.get(subjectPosition))))
        return sayItLoud(lineNo, ": first arg is an atom: ", stringTuple.toString());
      // check for valid second arg
      if ((stringTuple.size() > 1)
          && (!isUri(stringTuple.get(predicatePosition))))
        return sayItLoud(lineNo, ": second arg is not an URI: ", stringTuple.toString());
    }
    return true;
  }

  /**
   * checks whether a tuple of proper length as specified by this.minNoOfArgs
   * and this.maxNoOfArgs;
   * <p>
   * also checks whether the arguments of a _rule_ tuple are of the right "kind":
   * arg 1: URI or variable
   * arg 2: URI or variable
   * arg 3, 4, ... : URI, variable, or XSD atom
   * NOTE: no blank nodes are allowed in rule tuples (contrary to ground tuples)
   * <p>
   * finally check whether in case of a XSD atom, the atom is compatible
   * with the specified XSD type
   * ***** NOT IMPLEMENTED YET *****
   * <p>
   * depending on this.verbose and this.exitOnError, the method is silent,
   * outputs a warning, or exit the process
   *
   * @see de.dfki.lt.hfc.TupleStore#isValidTuple
   */
  public boolean isValidRuleTuple(ArrayList<String> stringTuple, int lineNo) {
    // check against min length
    if (stringTuple.size() < this.minNoOfArgs)
      return sayItLoud(lineNo, ": tuple too short: ", stringTuple.toString());
    // check against max length
    if (stringTuple.size() > this.maxNoOfArgs)
      return sayItLoud(lineNo, ": tuple too long", stringTuple.toString());
    // note: blank nodes (if any) will NOT show up here and are filtered out beforehand
    // is tuple RDF compliant
    if (rdfCheck) {
      // check for valid first arg: either URI or variable, but not an atom
      if ((stringTuple.size() > 0) && TupleStore.isAtom(stringTuple.get(0)))
        return sayItLoud(lineNo, ": first arg must be an URI or variable", stringTuple.toString());
      // check for valid second arg: either URI or variable, but not an atom
      if ((stringTuple.size() > 1) && TupleStore.isAtom(stringTuple.get(0)))
        return sayItLoud(lineNo, ": second arg must be an URI or variable", stringTuple.toString());
    }
    return true;
  }
  /**
   * addTuple() assumes a textual tuple representation after tokenization
   * (an array list of strings);
   * the bidirectional mapping is established and the index is updated;
   * this method is used when an external tuple file is read in;
   * lineNo refers to the line number in the file that is read in
   * <p>
   *
   * @return the int[] representation of parameter stringTuple, otherwise
   * @throws WrongFormatException
   */
  protected int[] addTuple(List<String> stringTuple, int lineNo)
      throws WrongFormatException {
    // check whether external representation is valid for a ground tuple
    if (!isValidTuple(stringTuple, lineNo))
      return null;
    // internalize tuple
    if (this.addTS)
      stringTuple.add(currentTime());
    int[] intTuple = internalizeTuple(stringTuple);
    if (addTuple(intTuple))
      return intTuple;
    else {
      logger.info("tuple specified twice at line {}: {}", lineNo, stringTuple);
      return null;
    }
  }

  private static String currentTime() {
    StringBuilder stringBuilder = new StringBuilder("\"");
    stringBuilder.append(System.currentTimeMillis());
    stringBuilder.append("\"^^<xsd:long>");
    return stringBuilder.toString();
  }

  /**
   * addTuple(String[]) performs the internalization and then calls addTuple(int[])
   * <p>
   */
  public synchronized boolean addTuple(String[] stringTuple) {
    return addTuple(internalizeTuple(stringTuple));
  }

  /**
   * a URI starts with a '<' and ends with a '>';
   */
  public String parseURI(StringTokenizer st, ArrayList<String> tuple) {
    // the leading '<' char has already been consumed, so consume tokens until
    // we
    // find the closing '>'
    // note: no blanks are allowed inside a URI, but a URI might clearly contain
    // '_' and '\' chars, so reading only the next two tokens would lead to
    // wrong
    // results in general
    StringBuilder sb = new StringBuilder("<");
    String token;
    while (st.hasMoreTokens()) {
      token = st.nextToken();
      if (token.equals(">"))
        break;
      else
        // normalize the namespace
        sb.append(token);
    }
    token = sb.append(">").toString();
    tuple.add(token);
    return token;
  }

  /**
   * a blank node starts with a "_:" and ends before the next whitespace character,
   * i.e., no whitespaces are allowed inside the name of the blank node;
   * since, blank nodes make no reference to a namespace, we make this a static method
   */

  protected void parseBlankNode(StringTokenizer st, ArrayList<String> tuple) {
    // the leading '_' char has already been consumed, so consume tokens until
    // we
    // find the next whitespace char
    StringBuilder sb = new StringBuilder("_");
    sb.append(st.nextToken()); // the rest of the blank node
    if (blankNodeSuffix != null)
      sb.append('X').append(blankNodeSuffix);
    tuple.add(sb.toString());
  }

  /**
   * generates a new unique blank node id (an int);
   * used during forward chaining when unbounded right-hand side variables are introduced;
   * it is important that the method is synchronized to exclusively lock the blank counter;
   */
  public int newBlankNode() {
    BlankNode b;
    do {
      String name = _blankNodePrefix + Long.toHexString(r.nextLong());
      b = new BlankNode(name);
    } while (isInFactBase(b));
    return putObject(b);
  }

  /**
   * XSD atoms are strings optionally followed by either a type identifier (^^type)
   * or a language tag (@lang);
   * within the preceding string, further strings are allowed, surrounded by "\"",
   * as well as spaces, "\\", etc.
   */
  public String parseAtom(StringTokenizer st, ArrayList<String> tuple) {
    StringBuilder sb = new StringBuilder("\"");
    boolean backquote = false;
    String token;
    while (st.hasMoreTokens()) {
      token = st.nextToken();
      if (!backquote && token.equals("\""))
        // string fully parsed
        break;
      if (token.equals("\\"))
        backquote = true;
      else
        backquote = false;
      sb.append(token);
    }
    sb.append("\"");
    // now gather potential additional information (XSD Type or language tag);
    // the first whitespace char terminates XSD atom recognition
    //
    // type checking of XSD atoms should be implemented HERE -- use a second
    // string buffer to separate the bare atom from its type
    boolean bareAtom = true;
    while (st.hasMoreTokens()) {
      token = st.nextToken();
      if (token.equals(" "))
        break;
      else {
        bareAtom = false;
        sb.append("^^").append(namespace.getXSDNamespace(st));
        if (!(token.equals("^^") || token.equals("<") || token.equals(">")))
          // normalize namespace
          sb.append(token);
      }
    }
    if (bareAtom) {
      // complete type in order to recognize duplicates (perhaps output a
      // message?)
      if (this.namespace.isShortIsDefault())
        sb.append("^^").append(XsdString.SHORT_NAME);
      else
        sb.append("^^").append(XsdString.LONG_NAME);
    }
    token = sb.toString();
    Pattern p = Pattern.compile("\\\\u(\\p{XDigit}{4})");
    Matcher m = p.matcher(token);
    StringBuffer buf = new StringBuffer(token.length());
    while (m.find()) {
      String ch = String.valueOf((char) Integer.parseInt(m.group(1), 16));
      m.appendReplacement(buf, Matcher.quoteReplacement(ch));
    }
    m.appendTail(buf);
    tuple.add(buf.toString());
    return token;
  }

  public static String toUnicode(String in) {
    StringBuilder out = new StringBuilder();
    for (int i = 0; i < in.length(); i++) {
      final char ch = in.charAt(i);
      if (ch <= 127)
        out.append(ch);
      else
        out.append("\\u").append(String.format("%04x", (int) ch));
    }
    return out.toString();
  }

  public String toString(int code) {
    return toUnicode(getObject(code).toString());
  }

  /**
   * generates an external string representation from the internal int[]
   * representation of a tuple
   */
  @Override
  public String toString(int[] tuple, boolean dot) {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < tuple.length; i++)
      sb.append(toString(tuple[i]) + " ");
    if (dot)
      sb.append(".");
    return sb.toString();
  }

  @Override
  public String toString(int[] tuple) {
    return toString(tuple, true);
  }

  /**
   * this method differs from toString() above in that is always tries
   * to fully expand the namespace of an URI (if present)
   */
  public String toExpandedString(int code) {
    // distinguish between URIs vs. XSD atoms or blank nodes
    String literal = getObject(code).toString();
    return (isAtom(literal) || isBlankNode(literal))
        ? toUnicode(literal.replace("xsd:", this.namespace.getLongForm("xsd")))
          : toUnicode(this.namespace.expandUri(literal));   // a URI
  }

  /**
   * this method differs from toString() above in that is always tries
   * to fully expand the namespace of an URI (if present)
   */
  public String toExpandedString(int[] tuple) {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < tuple.length; i++) {
      sb.append(toExpandedString(tuple[i]) + " ");
    }
    sb.append(".");
    return sb.toString();
  }

  /**
   * a simple version that writes all tuple from the tuple store to a file using
   * the external representation;
   * intended file extension is '.nt' (to indicate N-Tiple syntax)
   */
  public synchronized void writeTuples(String filename) {
    writeTuples(allTuples, filename);
  }

  /**
   * a simple version that writes all tuple from the tuple store to a file using
   * the external representation (encoding: UTF-16);
   * intended file extension is '.nt' (to indicate N-Tiple syntax)
   * can be used to iterate over lists or sets
   */
  public void writeTuples(Collection<int[]> collection, String filename) {
    logger.debug("  writing tuples to " + filename + " ...");
    try {
      PrintWriter pw = new PrintWriter(new OutputStreamWriter(
          new FileOutputStream(filename), this.outputCharacterEncoding));
      for (int[] tuple : collection) {
        logger.debug("Writing tuple: " + toExpandedString(tuple));
        pw.println(toString(tuple));
      }
      pw.flush();
      pw.close();
    } catch (IOException e) {
      logger.error("Error while writing tuples to " + filename);
      throw new RuntimeException("FATAL ERROR");
    }
  }

  /**
   * this method differs from writeTuples() above in that it always tries to
   * fully expand the namespace of an URI when writing out tuples
   */
  public void writeExpandedTuples(String filename) {
    logger.debug("  writing tuples to " + filename + " ...");
    try {
      PrintWriter pw = new PrintWriter(new OutputStreamWriter(
          new FileOutputStream(filename), this.outputCharacterEncoding));
      for (int[] tuple : allTuples)
        pw.println(toExpandedString(tuple));
      pw.flush();
      pw.close();
    } catch (IOException e) {
      logger.error("Error while writing tuples to " + filename);
      throw new RuntimeException("FATAL ERROR");
    }
  }

  /**
   * a simple version that writes the relevant parts of a tuple store to a file, using
   * a specific format; much faster than Java's serialization;
   * intended file extension is '.ts' (for tuple store)
   */
  public void writeTupleStore(String filename) {
    logger.debug("  writing tuple store to " + filename + " ...");
    try {
      PrintWriter pw = new PrintWriter(new OutputStreamWriter(
          new FileOutputStream(filename), this.outputCharacterEncoding));
      // dump objectToId (null -> 0 given by constructor is overwritten by
      // exactly the same mapping)
      pw.println("&object2id " + objectToId.size());
      for (Map.Entry<AnyType, Integer> entry : objectToId.entrySet())
        pw.println(entry.getKey() + " " + entry.getValue());
      // dump idToObject
      pw.println("&id2object " + (idToJavaObject.size() - 1));
      // start with 1, since TupleStore constructor assigns a special meaning to
      // index 0
      for (int i = 1; i < idToJavaObject.size(); i++)
        // no need to write i (ascending order!)
        pw.println(idToJavaObject.get(i).toString());
      // do _not_ dump idToJavaObject: null values are assigned (lazy!) when
      // tuple store is read in
      // dump allTuples
      pw.println("&tuples " + allTuples.size());
      for (int[] tuple : allTuples) {
        pw.print(tuple.length + " ");
        for (int i = 0; i < tuple.length; i++)
          pw.print(tuple[i] + " ");
        pw.println();
      }
      pw.flush();
      pw.close();
    } catch (IOException e) {
      logger.error("Error while writing tuples to " + filename);
      throw new RuntimeException("FATAL ERROR");
    }
  }

  public Integer putObject(String literal) {
    return putObject(makeAnyType(literal));
  }

  /**
  * given a string representation of a literal (an argument of a tuple),
  * isUri() returns true iff literal is a URI; false, otherwise
  */
  public static boolean isUri(String literal) {
    return literal.startsWith("<");
  }

  /**
  * given a string representation of a literal (an argument of a tuple),
  * isBlankNode() returns true iff literal is a blank node; false, otherwise
  */
  public static boolean isBlankNode(String literal) {
    return literal.startsWith("_");
  }

  /**
  * given a string representation of a literal (an argument of a tuple),
  * isAtom() returns true iff literal is an XSD atom; false, otherwise
  */
  public static boolean isAtom(String literal) {
    return literal.startsWith("\"");
  }

  /**
   * tests whether literal is a constant (i.e., URI, blank node, or XSD atom) as known
   * by the tuple store; i.e., literal must be part of a tuple that has been added to
   * the tuple store;
   * NOTE: false does NOT indicate that literal is a variable!
   */
  public boolean isConstant(String literal) {
    return isInFactBase(makeAnyType(literal));
  }

  public void logStoreStatus() {
    // some statistics
    if (logger.isInfoEnabled()) {
      int noOfURIs = 0, noOfBlanks = 0, noOfAtoms = 0;
      for (int i = 0; i < idToJavaObject.size(); i++) {
        if (idToJavaObject.get(i) instanceof Uri)
          ++noOfURIs;
        else if (idToJavaObject.get(i) instanceof BlankNode)
          ++noOfBlanks;
        else
          ++noOfAtoms;
      }
      logger.info("{} unique {} URIs {} blanks {} atoms",
          allTuples.size(), noOfURIs, noOfBlanks, noOfAtoms);
    }
  }

  ////////////////////////////////////////////////////////////////////////
  // Query functionality
  ////////////////////////////////////////////////////////////////////////

  private boolean isInFactBase(String literal) {
    AnyType t = makeAnyType(literal);
    return isInFactBase(t);
  }

  /** TODO: MOVE TO HFC class
  * checks whether a tuple (represented as a string array) is contained in the
  * tuple store
  */
  public boolean ask(String[] externalTuple) {
    int[] internalTuple = new int[externalTuple.length];
    for (int i = 0; i < externalTuple.length; i++) {
      // check whether the external symbols are even known by the tuple store
      if (externalTuple[i] == null)
        return false;
      if (!isInFactBase(externalTuple[i]))
        return false;
      internalTuple[i] = putObject(externalTuple[i]);
    }
    return this.allTuples.contains(internalTuple);
  }

  /**
   * checks whether a tuple (represented as an array list of strings) is contained
   * in the tuple store
   */
  public boolean ask(ArrayList<String> externalTuple) {
    return ask(externalTuple.toArray(new String[0]));
  }

  /** return the number of tuples contained this store */
  public int size() {
    return allTuples.size();
  }

  ////////////////////////////////////////////////////////////////////////
  // "Deep" Copy
  ////////////////////////////////////////////////////////////////////////

  /** TODO: ***copy***
  * returns a copy of the tuple store that can be used to generate "choice points",
  * e.g., during reasoning, as is done by the forward chainer
  * <p>
  * The copy uses the same namespace object as this object
  * @throws WrongFormatException
  * @throws IOException
  *
  public TupleStore copyTupleIntStore() throws IOException, WrongFormatException {
    TupleStore copy = new TupleIntStore();
    copy.currentId = this.currentId;  // means different things in different tuple stores
    //copy.minNoOfArgs = this.minNoOfArgs;
    copy.maxNoOfArgs = this.maxNoOfArgs;
    //copy.verbose = this.verbose;
    //copy.rdfCheck = this.rdfCheck;
    //copy.exitOnError = this.exitOnError;
    //copy.namespace = this.namespace;
    copy.equivalenceClassReduction = this.equivalenceClassReduction;
    // JavaDoc says clone() returns deep copy in both cases; second clone does not need casting
    /*
  	copy.uriToProxy = (TIntIntHashMap)this.uriToProxy.clone();
  	copy.proxyToUris = this.proxyToUris.clone();
  	copy.uriToEquivalenceRelation = (TIntIntHashMap)this.uriToEquivalenceRelation.clone();
     *
    copy.uriToProxy = new TIntIntHashMap(this.uriToProxy);
    copy.proxyToUris = new TIntObjectHashMap<TIntArrayList>(this.proxyToUris);
    copy.uriToEquivalenceRelation = new TIntIntHashMap(this.uriToEquivalenceRelation);
  // use copy constructor for objectToId, idToObject, idToJavaObject, and allTuples
    copy.objectToId = new HashMap<AnyType, Integer>(this.objectToId);
    copy.idToJavaObject = new ArrayList<AnyType>(this.idToJavaObject);
    copy.allTuples = new TCustomHashSet<int[]>(TupleIntStore.DEFAULT_HASHING_STRATEGY, this.allTuples);
  // operatorRegistry and aggregateRegistry need to be copied (above mappings might be different
  // for different tuple stores)
    copy.operatorRegistry = new OperatorRegistry(copy, this.operatorRegistry);
    copy.aggregateRegistry = new AggregateRegistry(copy, this.aggregateRegistry);
    // index needs to be copied
    copy.index = copyIndex();
    // finished!
    return copy;
  }*/
}
