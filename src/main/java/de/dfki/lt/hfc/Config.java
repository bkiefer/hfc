package de.dfki.lt.hfc;

import de.dfki.lt.hfc.indices.IndexingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.util.*;

public class Config {

  public static final String EXITONERROR = "exitOnError";
  public static final String VERBOSE = "verbose";
  public static final String CHARACTERENCONDING = "characterEncoding";
  public static final String GARBAGECOLLECTION = "garbageCollection";
  public static final String NOOFCORES = "noOfCores";
  public static final String NOOFTUPLES = "noOfTuples";
  public static final String NOOFATOMS = "noOfAtoms";
  public static final String EQREDUCTION = "eqReduction";
  public static final String TUPLEFILES = "tupleFiles";
  public static final String RULEFILES = "ruleFiles";
  public static final String PERSIST = "persistencyFile";
  public static final String ITERATIONS = "iterations";
  public static final String SHORTISDEFAULT = "shortIsDefault";
  public static final String CLEANUP = "cleanUpRepository";
  public static final String NAMESPACES = "namespaces";
  public static final String MINARGS = "MinArgs";
  public static final String MAXARGS = "MaxArgs";
  public static final String SUBJECTPOS = "subjectPosition";
  public static final String PREDICATEPOS = "predicatePosition";
  public static final String OBJECTPOS = "objectPosition";
  public static final String RDFCHECK = "rdfCheck";

  /**
   * A basic LOGGER.
   */
  private static final Logger logger = LoggerFactory.getLogger(Config.class);

  private static String path = "./src/resources/";

  private final Map<String, Object> configs;

  public NamespaceManager namespace = NamespaceManager.getInstance();

  public  IndexStore indexStore;


  public Config(Map<String, Object> configs) {
    this.configs = configs;
    HashMap<String, String> shortToLong = (HashMap<String, String>) configs.get(NAMESPACES);
    this.namespace.setShortIsDefault((Boolean) configs.get(SHORTISDEFAULT));
    Namespace ns;
    for (Map.Entry<String, String> mapping : shortToLong.entrySet()) {
      ns = new Namespace(mapping.getKey(), mapping.getValue(), (Boolean) configs.get(SHORTISDEFAULT));
      namespace.addNamespace(ns);
    }

    createIndexStore(configs);
  }


  private void createIndexStore(Map<String, Object> configs) {
    IndexStore indexStore;
    try {
      HashMap<String, Object> settings = new HashMap<>();
      if (configs.containsKey("Index")) {
        for (Object e : (ArrayList) configs.get("Index")) {
          settings.putAll((Map<? extends String, ?>) e);
        }
        indexStore = createIndexStore(settings);
      } else {
        indexStore = null;
      }
    } catch (IndexingException e) {
      logger.error(e.getMessage());
      if ((Boolean )configs.get(EXITONERROR))
        System.exit(-1);
      indexStore = null;
    }
    this.indexStore = indexStore;
  }


  public static Config getInstance(String configFileName) throws FileNotFoundException {
    Yaml yaml = new Yaml();
    File confFile = new File(configFileName);
    InputStream in = new FileInputStream(confFile);
    return new Config((Map<String, Object>) yaml.load(in));
  }


  /**
   * the DEFAULT settings basically address the RDF triple case without equivalence class reduction
   *
   * @return an instance of Config containing the default settings
   */
  public static Config getDefaultConfig() throws IOException {
    String configPath = path + "DefaultConfig.yml";
    return getInstance(configPath);
  }


  /**
   * @Deprecated for Tests only
   * @param noOfCores the number of cores used for parallelling the closure computation
   * @param verbose
   * @param rdfCheck
   * @param eqReduction
   * @param minNoOfArgs
   * @param maxNoOfArgs
   * @param noOfAtoms
   * @param noOfTuples
   * @param tupleFile
   * @param ruleFile
   * @return
   */
  public static Config getInstance(int noOfCores, boolean verbose, boolean rdfCheck, boolean eqReduction,
                                   int minNoOfArgs, int maxNoOfArgs, int noOfAtoms, int noOfTuples,
                                   String tupleFile, String ruleFile) throws FileNotFoundException {
    String configPath = path + "DefaultConfig.yml";
    Config config = getInstance(configPath);
    //overwrite/extend default settings of the config
    config.configs.put(NOOFCORES, noOfCores);
    config.configs.put(VERBOSE, verbose);
    config.configs.put(RDFCHECK, rdfCheck);
    config.configs.put(EQREDUCTION, eqReduction);
    config.configs.put(MINARGS, minNoOfArgs);
    config.configs.put(MAXARGS, maxNoOfArgs);
    config.configs.put(NOOFATOMS, noOfAtoms);
    config.configs.put(NOOFTUPLES, noOfTuples);
    List tupleFiles = new ArrayList();
    tupleFiles.add(tupleFile);
    config.configs.put(TUPLEFILES, tupleFiles);
    List ruleFiles = new ArrayList();
    ruleFiles.add(ruleFile);
    config.configs.put(RULEFILES, ruleFiles);
    config.createIndexStore(config.configs);
    return config;
  }


  public void addRuleFile(String ruleFile){
    List ruleFiles = (List) configs.get(RULEFILES);
    ruleFiles.add(ruleFile);
    configs.put(RULEFILES, ruleFiles);
  }

  public void addRuleFiles(List<String> files){
    List ruleFiles = (List) configs.get(RULEFILES);
    ruleFiles.addAll(files);
    configs.put(RULEFILES, ruleFiles);
  }

  public void replaceRuleFiles(List<String> files){
    configs.put(RULEFILES, files);
  }

  public void addTupleFile(String tupleFile){
    List tupleFiles = (List) configs.get(TUPLEFILES);
    tupleFiles.add(tupleFile);
    configs.put(TUPLEFILES, tupleFiles);
  }
  
  public void addTupleFiles(List<String> files){
    List tupleFiles = (List) configs.get(TUPLEFILES);
    tupleFiles.addAll(files);
    configs.put(TUPLEFILES, tupleFiles);
  }


  public void replaceTupleFiles(List<String> files){
    configs.put(TUPLEFILES, files);
  }

  public void updateConfig(Map<String, Object> configuration) {
    //ignore changes that were made to the Tuplefiles, Rulefiles and the Namespaces
    configuration.entrySet().stream().
            filter(x -> !x.equals(TUPLEFILES) && !x.equals(RULEFILES) && !x.equals(NAMESPACES)).
            forEach(x -> configs.put(x.getKey(), x.getValue()));
    //configs.putAll(configuration);
  }


  private IndexStore createIndexStore(HashMap<String, Object> indexSettings) throws IndexingException {
    return new IndexStore(indexSettings);
  }


  public String toString() {
    return Arrays.toString(configs.entrySet().toArray());
  }


  public void addNamespace(String shortForm, String longForm) {
    namespace.putForm(shortForm, longForm, (Boolean) configs.get(SHORTISDEFAULT));
  }



  public void setShortIsDefault(boolean shortIsDefault) {
    configs.put(SHORTISDEFAULT,shortIsDefault);
    namespace.setShortIsDefault(shortIsDefault);
  }


  public boolean isShortIsDefault(){
    return (boolean) configs.get(SHORTISDEFAULT);
  }


  public Config getCopy(int noOfCores, boolean verbose) {
    Config copy = new Config(this.configs);
    copy.configs.put(NOOFCORES, noOfCores);
    copy.configs.put(VERBOSE, verbose);
    copy.namespace = this.namespace.copy();
    if (indexStore != null)
      copy.indexStore = this.indexStore.copy();
    else
      copy.indexStore = null;
    return copy;
  }


  public List<String> getTupleFiles(){
    if (configs.get(TUPLEFILES) == null)
      return new ArrayList<String>();
    return (ArrayList<String>) configs.get(TUPLEFILES);
  }


  public List<String> getRuleFiles(){
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

  public int getNoOfCores() {
    return (int) configs.get(NOOFCORES);
  }

  public int getIterations() {
    return (int) configs.get(ITERATIONS);
  }

  public boolean isEqReduction() {
    return (Boolean) configs.get(EQREDUCTION);
  }

  public int getSubjectPosition() {
    return (int) configs.get(SUBJECTPOS);
  }

  public int getObjectPosition() {
    return (int) configs.get(OBJECTPOS);
  }

  public int getPredicatePosition() {
    return (int) configs.get(PREDICATEPOS);
  }

  public int getNoOfAtoms() {
    return (int) configs.get(NOOFATOMS);
  }

  public int getNoOfTuples() {
    return (int) configs.get(NOOFTUPLES);
  }

  public String getCharacterEncoding() {
    return (String) configs.get(CHARACTERENCONDING);
  }

  public boolean isCleanupRepository() {
    return (Boolean) configs.get(CLEANUP);
  }

  public boolean isGarbageCollection() {return (Boolean) configs.get(GARBAGECOLLECTION); }

  public void setVerbose(boolean b) {
    configs.put(VERBOSE, b);
  }

  public void updateConfig(String key, Object value) {
      configs.put(key, value);
  }
}
