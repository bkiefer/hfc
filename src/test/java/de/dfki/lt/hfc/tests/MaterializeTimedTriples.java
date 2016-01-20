package de.dfki.lt.hfc.tests;

import de.dfki.lt.hfc.*;

public class MaterializeTimedTriples {
	
	public static void main(String[] args) throws Exception {
		// java -server -cp .:../lib/trove-2.1.0.jar -Xms1024m -Xmx4096m de/dfki/lt/hfc/tests/MaterializeTimedTriples
		long time = System.currentTimeMillis();
		ForwardChainer fc =	new ForwardChainer(100000, 500000,
																					 "/Users/krieger/Desktop/Java/HFC/hfc/src/resources/nary0.80.time.ltworld.nt",
																					 "/Users/krieger/Desktop/Java/HFC/hfc/src/resources/measurements.time.triple.rdl",
																					 "/Users/krieger/Desktop/Java/HFC/hfc/src/resources/default.ns");
		//System.out.println("time: " + (System.currentTimeMillis() - time) / 1000.0);
		//System.exit(1);
		fc.computeClosure();
		//fc.tupleStore.writeTuples("/Users/krieger/Desktop/Java/HFC/hfc/resources/??????");
		fc.shutdown();
	}
	
}