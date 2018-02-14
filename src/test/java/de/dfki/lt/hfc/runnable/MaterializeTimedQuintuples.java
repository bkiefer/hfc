package de.dfki.lt.hfc.runnable;

import de.dfki.lt.hfc.ForwardChainer;

public class MaterializeTimedQuintuples {

	public static void main(String[] args) throws Exception {
		// java -server -cp .:../lib/trove-2.1.0.jar -Xms1024m -Xmx4096m de/dfki/lt/hfc/tests/MaterializeTimedQuintuples
		//long time = System.currentTimeMillis();
		ForwardChainer fc =	new ForwardChainer(100000, 500000,
																					 "/Users/krieger/Desktop/Java/HFC/hfc/src/resources/quintuples0.75.time.ltworld.nt",
																					 "/Users/krieger/Desktop/Java/HFC/hfc/src/resources/measurements.time.quintuple.rdl",
																					 "/Users/krieger/Desktop/Java/HFC/hfc/src/resources/default.ns");
		//System.out.println("time: " + (System.currentTimeMillis() - time) / 1000.0);
		//System.exit(1);
		fc.computeClosure();
		//fc.tupleStore.writeTuples(getResource("??????"));
		fc.shutdownNoExit();
	}

}
