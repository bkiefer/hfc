package de.dfki.lt.hfc;

import de.dfki.lt.hfc.indices.IndexingException;
import de.dfki.lt.hfc.types.Uri;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.util.*;

public class Config {

    /**
     * A basic LOGGER.
     */
    private static final Logger logger = LoggerFactory.getLogger(Config.class);

    // special value UNBOUND/NULL, not used at the moment
    public static final Uri UNBOUND = new Uri("Null");
    public static final int UNBOUND_ID = 0;

    // RDFS: subClassOf
    public static final Uri RDFS_SUBCLASSOF_SHORT = new Uri("<rdfs:subClassOf>");
    public static final Uri RDFS_SUBCLASSOF_LONG = new Uri("<http://www.w3.org/2000/01/rdf-schema#subClassOf>");
    public static final int RDFS_SUBCLASSOF_ID = 1;

    // OWL: sameAs, equivalentClass, equivalentProperty, disjointWith
    public static final Uri OWL_SAMEAS_SHORT = new Uri("<owl:sameAs>");
    public static final Uri OWL_SAMEAS_LONG = new Uri("<http://www.w3.org/2002/07/owl#sameAs>");
    public static final int OWL_SAMEAS_ID = 2;

    public static final Uri OWL_EQUIVALENTCLASS_SHORT = new Uri("<owl:equivalentClass>");
    public static final Uri OWL_EQUIVALENTCLASS_LONG = new Uri("<http://www.w3.org/2002/07/owl#equivalentClass>");
    public static final int OWL_EQUIVALENTCLASS_ID = 3;

    public static final Uri OWL_EQUIVALENTPROPERTY_SHORT = new Uri("<owl:equivalentProperty>");
    public static final Uri OWL_EQUIVALENTPROPERTY_LONG = new Uri("<http://www.w3.org/2002/07/owl#equivalentProperty>");
    public static final int OWL_EQUIVALENTPROPERTY_ID = 4;

    public static final Uri OWL_DISJOINTWITH_SHORT = new Uri("<owl:disjointWith>");
    public static final Uri OWL_DISJOINTWITH_LONG = new Uri("<http://www.w3.org/2002/07/owl#disjointWith>");
    public static final int OWL_DISJOINTWITH_ID = 5;


    public boolean verbose;
    public int noOfCores;
    public int noOfTuples;
    public int noOfAtoms;
    public boolean eqReduction;
    public List<String> tupleFiles;
    public String persistencyFile;
    public List<String> ruleFiles;
    public boolean shortIsDefault;
    public int maxArgs;
    public int minArgs;
    public int subjectPosition;
    public int predicatePosition;
    public int objectPosition;
    public boolean rdfCheck;
    public boolean exitOnError;
    public String characterEncoding;
    public int noOfIterations;
    public IndexStore indexStore;
    public boolean gc;
    public boolean cleanUpRepository;

    public final Namespace namespace = new Namespace();


    public Config(Map<String, Object> configs) {IndexStore indexStore1;
        this.verbose = (boolean) configs.get("verbose");
        this.characterEncoding = (String) configs.get("characterEncoding");
        this.gc = (boolean) configs.get("garbageCollection");
        this.noOfCores = (int) configs.get("noOfCores");
        this.noOfTuples = (int) configs.get("noOfTuples");
        this.noOfAtoms = (int) configs.get("noOfAtoms");
        this.eqReduction = (boolean) configs.get("eqReduction");
        this.tupleFiles = (ArrayList) configs.get("tupleFiles");
        this.ruleFiles = (ArrayList) configs.get("ruleFiles");
        this.persistencyFile = (String) configs.get("persistencyFile");
        this.noOfIterations = (int) configs.get("iterations");
        this.shortIsDefault = (boolean)configs.get("shortIsDefault");
        this.cleanUpRepository = (boolean) configs.get("cleanUpRepository");
        HashMap<String, String> shortToLong = (HashMap<String, String>) configs.get("namespaces");
        NamespaceObject ns;
        for (Map.Entry<String, String> mapping : shortToLong.entrySet()){
            ns = new NamespaceObject(mapping.getKey(), mapping.getValue(), shortIsDefault);
            namespace.addNamespace(ns);
        }
        this.minArgs = (int) configs.get("MinArgs");
        this.maxArgs = (int) configs.get("MaxArgs");
        this.subjectPosition = (int) configs.get("subjectPosition");
        this.predicatePosition = (int) configs.get("predicatePosition");
        this.objectPosition = (int) configs.get("objectPosition");
        this.rdfCheck = (boolean) configs.get("rdfCheck");
        this.exitOnError = (boolean) configs.get("exitOnError");
        try {
            indexStore1 = ( configs.containsKey("Index")) ? createIndexStore((HashMap<String, Object>) configs.get("Index")) : null;
        } catch (IndexingException e) {
            indexStore1 = null;
            e.printStackTrace();
        }
        this.indexStore = indexStore1;
    }

    public Config(int noOfCores, boolean verbose) {
        this.verbose = verbose;
        this.noOfCores = noOfCores;
    }

    public static Config getInstance(String configFileName) throws FileNotFoundException {
        Yaml yaml = new Yaml();
        File confFile = new File(configFileName);
        InputStream in = new FileInputStream(confFile);
        return new Config((Map<String, Object>) yaml.load(in));

    }


    public void updateConfig(Map<String, String> configuration){
        for (Map.Entry<String, String> pair : configuration.entrySet()) {
            switch (pair.getKey()) {
                case "characterEncoding" :
                    this.characterEncoding = pair.getValue();
                    break;
                case "cleanUpRepository" :
                    this.cleanUpRepository = Boolean.parseBoolean(pair.getValue());
                    break;
                case "eqReduction" :
                    this.eqReduction = Boolean.parseBoolean(pair.getValue());
                    break;
                case "garbageCollection" :
                    this.gc = Boolean.parseBoolean(pair.getValue());
                    break;
                case "MaxArgs" :
                    this.maxArgs = Integer.parseInt(pair.getValue());
                    break;
                case "MinArgs" :
                    this.minArgs = Integer.parseInt(pair.getValue());
                    break;
                case "noOfAtoms" :
                    this.noOfAtoms = Integer.parseInt(pair.getValue());
                    break;
                case "noOfCores" :
                    this.noOfCores = Integer.parseInt(pair.getValue());
                    break;
                case "noOfIterations" :
                    this.noOfIterations = Integer.parseInt(pair.getValue());
                    break;
                case "noOfTuples" :
                    this.noOfTuples = Integer.parseInt(pair.getValue());
                    break;
                case "objectPosition" :
                    this.objectPosition = Integer.parseInt(pair.getValue());
                    break;
                case "persistencyFile" :
                    this.persistencyFile = pair.getValue();
                    break;
                case "predicatePosition" :
                    this.predicatePosition = Integer.parseInt(pair.getValue());
                    break;
                case "rdfCheck" :
                    this.rdfCheck = Boolean.parseBoolean(pair.getValue());
                    break;
                case "shortIsDefault" :
                    this.setShortIsDefault(Boolean.parseBoolean(pair.getValue()));
                    break;
                case "subjectPosition" :
                    this.subjectPosition = Integer.parseInt(pair.getValue());
                    break;
                case "verbose" :
                    this.verbose = Boolean.parseBoolean(pair.getValue());
                    break;
                default :
                    if (this.verbose)
                        logger.info("  unknown setting option: " + pair.getKey());
                    break;
            }
        }
    }

    private IndexStore createIndexStore(HashMap<String, Object> indexSettings) throws IndexingException {
        return new IndexStore(indexSettings);
    }
    /**
     * the DEFAULT settings basically address the RDF triple case without equivalence class reduction
     * @return an instance of Config containing the default settings
     */
    public static Config getDefaultConfig() throws FileNotFoundException {
        return getInstance("DefaultConfig.yml");
    }

    public String toString(){
        StringBuilder strb = new StringBuilder();
        strb.append("Verbose: " + this.verbose + "\n" );
        strb.append("noOfCores: " + this.noOfCores + "\n" );
        strb.append("TupleFiles: " + this.tupleFiles+ "\n");
        strb.append("MinArgs: " + this.minArgs+ "\n");
        strb.append("MaxArgs: " + this.maxArgs+ "\n");
        strb.append("subjectPosition: " + this.subjectPosition+ "\n");
        strb.append("predicatePosition: " + this.predicatePosition+ "\n");
        strb.append("objectPosition: " + this.objectPosition+ "\n");
        strb.append("rdfCheck: " + rdfCheck+ "\n");
        strb.append("exitOnError: " + exitOnError+ "\n");
        strb.append("RuleFiles: " + this.ruleFiles+ "\n");
        strb.append("shortIsDefault: " + this.shortIsDefault+ "\n");
        strb.append("Namespaces:"+ "\n");
        strb.append("shortToNamespace: " + namespace.shortToNs+ "\n");
        strb.append("longToNamespace: " + namespace.longToNs+ "\n");
        return strb.toString();
    }

    public void addNamespace(String shortForm, String longForm){
        NamespaceObject ns = new NamespaceObject(shortForm,longForm,shortIsDefault);
        namespace.shortToNs.put(shortForm,ns);
        namespace.longToNs.put(longForm,ns);
    }


    public void putNamespace(String shortNamespace, String longNamespace) {
        namespace.putForm(shortNamespace,longNamespace,shortIsDefault);
    }

    public void setShortIsDefault(boolean shortIsDefault) {
        this.shortIsDefault = shortIsDefault;
        namespace.updateNamespace(shortIsDefault);
    }

    public Config getCopy(int noOfCores, boolean verbose) {
        Config copy = new Config(noOfCores,verbose);
        copy.characterEncoding = this.characterEncoding;
        copy.gc = this.gc;
        copy.noOfTuples = this.noOfTuples;
        copy.noOfAtoms = this.noOfAtoms;
        copy.eqReduction = this.eqReduction;
        copy.tupleFiles = this.tupleFiles;
        copy.ruleFiles = this.ruleFiles;
        copy.persistencyFile = this.persistencyFile;
        copy.noOfIterations = this.noOfIterations;
        copy.shortIsDefault = this.shortIsDefault;
        copy.cleanUpRepository = this.cleanUpRepository;
        copy.namespace = this.namespace.copy();
        copy.minArgs = this.minArgs;
        copy.maxArgs = this.maxArgs;
        copy.subjectPosition = this.subjectPosition;
        copy.predicatePosition = this.predicatePosition;
        copy.objectPosition = this.objectPosition;
        copy.rdfCheck = this.rdfCheck;
        copy.exitOnError = this.exitOnError;
        copy.indexStore = this.indexStore.copy();
        return copy;
    }
}
