package de.dfki.lt.hfc;

import de.dfki.lt.hfc.types.XsdInt;

import java.util.ArrayList;

import static de.dfki.lt.hfc.TestUtils.getResource;

public class TriplesWithoutTimeToQuintuplesWithTime {

	private static final int MAX_INT = 1000;

	private static final int NO_OF_ATOMS = 100000;

	private static final int NO_OF_TUPLES = 500000;

	private static final String IN_FILE = getResource("ltworld.jena.nt");

	private static final String OUT_FILE = getResource("quintuples.time.ltworld.nt");


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
																					 getResource("default.nt"),
																					 getResource("default.rdl"),
																					 getResource("default.ns"));
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
			tuple[3] = ts.putObject(XsdInt.toString(start, fc.tupleStore.namespace.shortIsDefault));
			tuple[4] = ts.putObject(XsdInt.toString(end, fc.tupleStore.namespace.shortIsDefault));
			newTuples.add(tuple);
		}

		ts.writeTuples(newTuples, OUT_FILE);

		fc.shutdownNoExit();
	}

}