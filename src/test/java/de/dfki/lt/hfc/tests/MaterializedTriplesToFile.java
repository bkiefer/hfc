package de.dfki.lt.hfc.tests;

import de.dfki.lt.hfc.*;

public class MaterializedTriplesToFile {

	public static void main(String[] args) throws Exception {
		//   time java -server -cp .:../lib/trove-2.1.0.jar -Xmx1024m de/dfki/lt/hfc/tests/MaterializedTriplesToFile
				ForwardChainer fc =	new ForwardChainer(100000, 500000,
																					 "/Users/krieger/Desktop/Java/HFC/hfc/resources/default.nt",
																					 "/Users/krieger/Desktop/Java/HFC/hfc/resources/default.rdl",
																					 "/Users/krieger/Desktop/Java/HFC/hfc/resources/default.ns");
		fc.uploadTuples("/Users/krieger/Desktop/Java/HFC/hfc/resources/ltworld.jena.nt");
		fc.computeClosure();
		fc.tupleStore.writeTuples("/Users/krieger/Desktop/Java/HFC/hfc/resources/ltworld.jena.materialized.nt");
		fc.shutdown();
	}

}