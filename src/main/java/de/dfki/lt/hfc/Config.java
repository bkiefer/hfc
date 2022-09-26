package de.dfki.lt.hfc;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import de.dfki.lt.hfc.types.XsdLong;

public class Config {

  public static final String EXITONERROR = "exitOnError";
  public static final String VERBOSE = "verbose";
  public static final String CHARACTERENCONDING = "characterEncoding";
  public static final String GARBAGECOLLECTION = "garbageCollection";
  public static final String NOOFCORES = "noOfCores";
  public static final String NOOFTUPLES = "noOfTuples";
  public static final String NOOFATOMS = "noOfAtoms";
  public static final String EQREDUCTION = "eqReduction";
  public static final String PERSIST = "persistenceFile";
  public static final String ITERATIONS = "iterations";
  public static final String SHORTISDEFAULT = "shortIsDefault";
  public static final String CLEANUP = "cleanUpRepository";
  public static final String MINARGS = "minArgs";
  public static final String MAXARGS = "maxArgs";
  public static final String SUBJECTPOS = "subjectPosition";
  public static final String PREDICATEPOS = "predicatePosition";
  public static final String OBJECTPOS = "objectPosition";
  public static final String RDFCHECK = "rdfCheck";
  public static final String ADDTIMESTAMP = "addTimestamps" ;

  public static final String NAMESPACES = "namespaces";
  public static final String TUPLEFILES = "tupleFiles";
  public static final String RULEFILES = "ruleFiles";

  private static final Logger log = LoggerFactory.getLogger(Config.class);

  /** The directory that contains the file the config was read from. If it is
   *  null that means it should be read from resources.
   */
  private File configDir = null;

  /** protected for testing */
  protected final Map<String, Object> configs;

  protected Config(Map<String, Object> configs, File confDir) {
    this.configDir = confDir;
    this.configs = configs;
  }

  @SuppressWarnings("unchecked")
  public static Config getInstance(InputStream in, File confDir) {
    Yaml yaml = new Yaml();
    InputStream def = Config.class.getResourceAsStream("/defaults.yml");
    Config conf = new Config((Map<String, Object>) yaml.load(def), confDir);
    yaml = new Yaml();
    Map<String, Object> confs = (Map<String, Object>) yaml.load(in);
    conf.configs.putAll(confs);
    return conf;
  }

  /** This gets an instance from a config file on the file system ONLY */
  public static Config getInstance(String configFileName)
      throws FileNotFoundException {
    File convFile = new File(configFileName);
    File convDir = convFile.getParentFile();
    if (convDir == null) {
      convDir = new File(".");
    }
    return getInstance(new FileInputStream(convFile), convDir);
  }

  /**
   * the DEFAULT settings basically address the RDF triple case without equivalence class reduction
   *
   * @return an instance of Config containing the default settings
   */
  public static Config getDefaultConfig() throws IOException {
    InputStream in = Config.class.getResourceAsStream("/DefaultConfig.yml");
    return getInstance(in, null);
  }

  private File resolvePath(String name) {
    File f = new File(name);
    if (! f.isAbsolute() && configDir != null) {
      f = new File(configDir, name);
    }
    return f;
  }

  /** This returns a BufferedReader using the specified character encoding */
  public BufferedReader readerFromFilename(String name) throws IOException {
    return Files.newBufferedReader(new File(name).toPath(),
        Charset.forName(getCharacterEncoding()));
  }


  private BufferedReader readerFromName(String name) throws IOException {
    if (configDir == null) {
      return new BufferedReader(
          new InputStreamReader(Config.class.getResourceAsStream("/" + name),
              Charset.forName(getCharacterEncoding())));
    }
    File resolved = resolvePath(name);
    return Files.newBufferedReader(resolved.toPath(),
        Charset.forName(getCharacterEncoding()));
  }

  @SuppressWarnings("unchecked")
  private String[] getTimestampsToAdd(long now) {
    List<Number> ts = (List<Number>) configs.get(ADDTIMESTAMP);
    if (ts != null) {
      String[] res = new String[ts.size()];
      int i = 0;
      for (Number n : ts) {
        long l = n.longValue();
        // -1 stands for infinity, -2 for current time
        res[i++] = new XsdLong(l == -2 ? now : l).toString();
      }
      return res;
    }
    return new String[]{};
  }

  TupleStore createTupleStore(NamespaceManager nsm, IndexStore is)
      throws FileNotFoundException, IOException, WrongFormatException {
    TupleStore tupleStore = new TupleStore(nsm, is, this);
    long startLoad = System.currentTimeMillis();
    // Read all files mentioned in the config
    for (String tuplefile : getTupleFiles()) {
      // TODO: if and what timestamp we add must be based on the config,
      // either on maxArgs, or on a separate config field. Currently, we
      // need a separate field because many tests rely on initialisation
      // without additional fields, even if maxArgs > 3
      tupleStore.readTuples(readerFromName(tuplefile), null, getTimestampsToAdd(startLoad));
    }
    long endLoad = System.currentTimeMillis();
    log.info("Loading ground tuples took {} msecs, {} tuples.",
        endLoad - startLoad, tupleStore.getAllTuples().size());
    return tupleStore;
  }

  RuleStore createRuleStore(TupleStore ts) throws IOException {
    RuleStore ruleStore = new RuleStore(ts);
    if (!getRuleFiles().isEmpty())
      for (String rulefile : getRuleFiles()) {
        ruleStore.readRules(readerFromName(rulefile));
      }
    return ruleStore;
  }

  NamespaceManager createNamespaceManager() {
    NamespaceManager nsm = new NamespaceManager();
    nsm.setShortIsDefault((Boolean) configs.get(SHORTISDEFAULT));
    @SuppressWarnings("unchecked")
    HashMap<String, String> shortToLong = (HashMap<String, String>) configs
        .get(NAMESPACES);
    if (shortToLong != null) {
      for (Map.Entry<String, String> mapping : shortToLong.entrySet()) {
        nsm.putForm(mapping.getKey(), mapping.getValue(),
            (Boolean) configs.get(SHORTISDEFAULT));
      }
    }
    return nsm;
  }

  /** For the persistency, if a file exists and is read, no timestamps will be
   *  added, it is assumed that the file matches the previous use and therefore
   *  contains all necessary fields
   *
   * @param ts The tuplestore to add existing persistency data to and where
   *           new tuples are persisted from
   *
   * @return a Writer that should be used to persist new tuples
   *
   * @throws FileNotFoundException
   * @throws WrongFormatException
   * @throws IOException
   */
  Writer treatPersistencyFile(TupleStore ts)
      throws FileNotFoundException, WrongFormatException, IOException {
    Writer w = null;
    String persistencyFileName = (String)configs.get(PERSIST);
    if (persistencyFileName != null) {
      // we can safely assume that the file for persistency will be on the
      // file system
      File persistencyFile = resolvePath(persistencyFileName);
      if (persistencyFile != null) {
        if (persistencyFile.exists() && persistencyFile.canRead()) {
          long startLoad = System.currentTimeMillis();
          ts.readTuples(Files.newBufferedReader(persistencyFile.toPath(),
              Charset.forName(getCharacterEncoding())), null);
          long endLoad = System.currentTimeMillis();
          log.info("Loading persistency tuples took {} msecs, {} tuples.",
              endLoad - startLoad, ts.getAllTuples().size());
        }

        w = Files.newBufferedWriter(persistencyFile.toPath(),
            Charset.forName(getCharacterEncoding()),
            StandardOpenOption.APPEND, StandardOpenOption.CREATE);
      }
    }
    return w;
  }

  /*
  @Deprecated
  public void addRuleFile(String ruleFile) {
    List ruleFiles = (List) configs.get(RULEFILES);
    ruleFiles.add(ruleFile);
    configs.put(RULEFILES, ruleFiles);
  }

  @Deprecated
  public void addRuleFiles(List<String> files) {
    List ruleFiles = (List) configs.get(RULEFILES);
    ruleFiles.addAll(files);
    configs.put(RULEFILES, ruleFiles);
  }

  @Deprecated
  public void replaceRuleFiles(List<String> files) {
    configs.put(RULEFILES, files);
  }

  @Deprecated
  public void addTupleFile(String tupleFile) {
    List tupleFiles = (List) configs.get(TUPLEFILES);
    tupleFiles.add(tupleFile);
    configs.put(TUPLEFILES, tupleFiles);
  }

  @Deprecated
  public void addTupleFiles(List<String> files) {
    List tupleFiles = (List) configs.get(TUPLEFILES);
    tupleFiles.addAll(files);
    configs.put(TUPLEFILES, tupleFiles);
  }

  @Deprecated
  public void replaceTupleFiles(List<String> files) {
    configs.put(TUPLEFILES, files);
  }

  @Deprecated
  public void updateConfig(Map<String, Object> configuration) {
    // ignore changes that were made to the Tuplefiles, Rulefiles and the
    // Namespaces
    configuration.entrySet().stream()
        .filter(x -> !x.equals(TUPLEFILES) && !x.equals(RULEFILES)
            && !x.equals(NAMESPACES))
        .forEach(x -> configs.put(x.getKey(), x.getValue()));
    // configs.putAll(configuration);
  }
  */


  @Override
  public String toString() {
    return Arrays.toString(configs.entrySet().toArray());
  }

  public boolean isShortIsDefault() {
    return (boolean) configs.get(SHORTISDEFAULT);
  }

  public void setShortIsDefault(boolean val) {
    configs.put(SHORTISDEFAULT, val);
  }

  /** Only TupleStore */
  @SuppressWarnings("unchecked")
  public List<String> getTupleFiles() {
    if (configs.get(TUPLEFILES) == null)
      return new ArrayList<String>();
    return (ArrayList<String>) configs.get(TUPLEFILES);
  }

  @SuppressWarnings("unchecked")
  public List<String> getRuleFiles() {
    if (configs.get(RULEFILES) == null)
      return new ArrayList<String>();
    return (ArrayList<String>) configs.get(RULEFILES);
  }

  public int getMinArgs() {
    return (int) configs.get(MINARGS);
  }

  public int getMaxArgs() {
    return (int) configs.get(MAXARGS);
  }

  public boolean isVerbose() {
    return (boolean) configs.get(VERBOSE);
  }

  public boolean isRdfCheck() {
    return (boolean) configs.get(RDFCHECK);
  }

  public boolean isExitOnError() {
    return (boolean) configs.get(EXITONERROR);
  }

  public int getNoOfCores() {
    return (int) configs.get(NOOFCORES);
  }

  public int getIterations() {
    return (int) configs.get(ITERATIONS);
  }

  public boolean isEqReduction() {
    return (Boolean) configs.get(EQREDUCTION);
  }

  /** Only TupleStore */
  public int getSubjectPosition() {
    return (int) configs.get(SUBJECTPOS);
  }

  /** Only TupleStore */
  public int getObjectPosition() {
    return (int) configs.get(OBJECTPOS);
  }

  /** Only TupleStore */
  public int getPredicatePosition() {
    return (int) configs.get(PREDICATEPOS);
  }

  /** Only TupleStore */
  public int getNoOfAtoms() {
    return (int) configs.get(NOOFATOMS);
  }

  /** Only TupleStore */
  public int getNoOfTuples() {
    return (int) configs.get(NOOFTUPLES);
  }

  /** Only TupleStore */
  public String getCharacterEncoding() {
    return (String) configs.get(CHARACTERENCONDING);
  }

  public boolean isCleanupRepository() {
    return (Boolean) configs.get(CLEANUP);
  }

  public boolean isGarbageCollection() {
    return (Boolean) configs.get(GARBAGECOLLECTION);
  }

  public void setVerbose(boolean b) {
    configs.put(VERBOSE, b);
  }

  /*
  @Deprecated
  public void updateConfig(String key, Object value) {
    configs.put(key, value);
  }
  */

  public String getPersist() {
    return (String) configs.get(PERSIST);
  }

  public boolean has(String key) {
    return configs.containsKey(key);
  }

  public Object get(String key) {
    return configs.get(key);
  }
}
