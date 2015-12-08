package de.dfki.lt.hfc;

import java.util.ArrayList;
import de.dfki.lt.hfc.*;
import de.dfki.lt.hfc.types.XsdInt;

public class TriplesWithoutTimeToQuintuplesWithTime {
	
	private static final int MAX_INT = 1000;
	
	private static final int NO_OF_ATOMS = 100000;
	
	private static final int NO_OF_TUPLES = 500000;
	
	private static final String IN_FILE = "/Users/krieger/Desktop/Java/HFC/hfc/resources/ltworld.jena.nt";
	
	private static final String OUT_FILE = "/Users/krieger/Desktop/Java/HFC/hfc/resources/quintuples.time.ltworld.nt";
	
	
	private static int makeRandom(int max) {
		return (int)(Math.round(Math.random() * max));
	}
	
	public static void main(String[] args) throws Exception {
		
		//   time java -server -cp .:../lib/trove-2.1.0.jar -Xmx1024m de/dfki/lt/hfc/tests/MaterializedTriplesToQuintuples
		ForwardChainer fc =	new ForwardChainer(2,      // noOfCores
																					 true,   // verbose
																					 false,  // rdfCheck
																					 true,
																					 3,      // minNoOfArgs
																					 5,      // maxNoOfArgs
																					 NO_OF_ATOMS,
																					 NO_OF_TUPLES,
																					 "/Users/krieger/Desktop/Java/HFC/hfc/resources/default.nt",
																					 "/Users/krieger/Desktop/Java/HFC/hfc/resources/default.rdl",
																					 "/Users/krieger/Desktop/Java/HFC/hfc/resources/default.ns");
		fc.uploadTuples(IN_FILE);
		TupleStore ts = fc.tupleStore;
		int start, end;
		int[] tuple;
		ArrayList<int[]> newTuples = new ArrayList<int[]>(NO_OF_TUPLES);
		
		for (int[] triple : ts.getAllTuples()) {
			tuple = new int[5];
			tuple[0] = triple[0];
			tuple[1] = triple[1];
			tuple[2] = triple[2];
			start = makeRandom(MAX_INT);
			end = makeRandom(MAX_INT);
			while (end < start) {
				end = makeRandom(MAX_INT);
			}
			tuple[3] = ts.putObject(XsdInt.toString(start, Namespace.shortIsDefault));
			tuple[4] = ts.putObject(XsdInt.toString(end, Namespace.shortIsDefault));
			newTuples.add(tuple);
		}
		
		ts.writeTuples(newTuples, OUT_FILE);
		
		fc.shutdown();
	}
	
}