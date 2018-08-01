package de.dfki.lt.hfc;

import de.dfki.lt.hfc.indices.IndexingException;
import de.dfki.lt.hfc.types.Uri;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.util.*;

public class Config {

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


    public final boolean verbose;
    public final int noOfCores;
    public final int noOfTuples;
    public final int noOfAtoms;
    public final boolean eqReduction;
    public final List<String> tupleFiles;
    public final List<String> ruleFiles;
    public final boolean shortIsDefault;
    public final int maxArgs;
    public final int minArgs;
    public final int subjectPosition;
    public final int predicatePosition;
    public final int objectPosition;
    public final boolean rdfCheck;
    public final boolean exitOnError;
    public final String characterEncoding;
    public final int noOfIterations;
    public final IndexStore indexStore;

    public final Namespace namespace = new Namespace();


    public Config(Map<String, Object> configs) {IndexStore indexStore1;
        this.verbose = (boolean) configs.get("verbose");
        this.characterEncoding = (String) configs.get("characterEncoding");
        this.noOfCores = (int) configs.get("noOfCores");
        this.noOfTuples = (int) configs.get("noOfTuples");
        this.noOfAtoms = (int) configs.get("noOfAtoms");
        this.eqReduction = (boolean) configs.get("eqReduction");
        this.tupleFiles = (ArrayList) configs.get("tupleFiles");
        this.ruleFiles = (ArrayList) configs.get("ruleFiles");
        this.noOfIterations = (int) configs.get("iterations");
        this.shortIsDefault = (boolean)configs.get("shortIsDefault");
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

    public static Config getInstance(String configFileName) throws FileNotFoundException {
        Yaml yaml = new Yaml();
        File confFile = new File(configFileName);
        InputStream in = new FileInputStream(confFile);
        return new Config((Map<String, Object>) yaml.load(in));

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





}
