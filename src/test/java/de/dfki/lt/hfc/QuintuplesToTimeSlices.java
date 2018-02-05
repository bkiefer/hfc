package de.dfki.lt.hfc;

import java.util.ArrayList;

import static de.dfki.lt.hfc.TestUtils.getResource;

public class QuintuplesToTimeSlices {

	private static final int NO_OF_ATOMS = 100000;

	private static final int NO_OF_TUPLES = 500000;

	private static final String IN_FILE = getResource("approach1.ltworld.nt");

	private static final String OUT_FILE = getResource("approach6.ltworld.nt");


	private static String makeUri(String prefix, int counter) {
		// skip ending '>' from URI prefix, add counter, and close name with '>'
		prefix = prefix.substring(0, prefix.length() - 1);
		return prefix + "-" + String.valueOf(counter) + ">";
	}

	private static String makeBlank(String prefix, int counter) {
		// only add counter
		return prefix + "-" + String.valueOf(counter);
	}

	public static void main(String[] args) throws Exception {

		//   time java -server -cp .:../lib/trove-2.1.0.jar -Xmx1024m de/dfki/lt/hfc/tests/QuintuplesToTimeSlices
		ForwardChainer fc =	new ForwardChainer(2,      // noOfCores
																					 true,   // verbose
																					 false,  // rdfCheck
																					 true,
																					 3,      // minNoOfArgs
																					 5,      // maxNoOfArgs
																					 NO_OF_ATOMS,
																					 NO_OF_TUPLES,
																					 IN_FILE,
																					 getResource("default.rdl"),
																					 getResource("default.ns"));

		TupleStore ts = fc.tupleStore;
		int[] triple;
		int counter = 0;
		int subjectId, objectId;

		ArrayList<int[]> newTriples = new ArrayList<int[]>(9 * NO_OF_TUPLES);
		for (int[] tuple : ts.getAllTuples()) {
			// generate new ids for extended subjects (and perhaps objects) from the original tuple
			if (TupleStore.isUri(ts.getObject(tuple[0])))
				subjectId = ts.putObject(makeUri(ts.getObject(tuple[0]), counter++));  // URI
			else
				subjectId = ts.putObject(makeBlank(ts.getObject(tuple[0]), counter++));  // blank node

			// is object in original tuple an XSD atom or a URI/blank node
			if (TupleStore.isAtom(ts.getObject(tuple[2])))
				objectId = -1;
			else if (TupleStore.isUri(ts.getObject(tuple[2])))
				objectId = ts.putObject(makeUri(ts.getObject(tuple[2]), counter++));
			else
				objectId = ts.putObject(makeBlank(ts.getObject(tuple[2]), counter++));

			triple = new int[3];
			triple[0] = subjectId;
			triple[1] = tuple[1];
			if (objectId == -1)  // is original object an atom or not ?
				triple[2] = tuple[2];
			else
				triple[2] = objectId;
			newTriples.add(triple);

			triple = new int[3];
			triple[0] = subjectId;
			triple[1] = ts.putObject("<rdf:type>");
			triple[2] = ts.putObject("<fourd:TimeSlice>");
			newTriples.add(triple);

			if (objectId != -1) {
				triple = new int[3];
				triple[0] = objectId;
				triple[1] = ts.putObject("<rdf:type>");
				triple[2] = ts.putObject("<fourd:TimeSlice>");
				newTriples.add(triple);
			}

			triple = new int[3];
			triple[0] = subjectId;
			triple[1] = ts.putObject("<fourd:starts>");
			triple[2] = tuple[3];
			newTriples.add(triple);

			triple = new int[3];
			triple[0] = subjectId;
			triple[1] = ts.putObject("<fourd:ends>");
			triple[2] = tuple[4];
			newTriples.add(triple);

			if (objectId != -1) {
				triple = new int[3];
				triple[0] = objectId;
				triple[1] = ts.putObject("<fourd:starts>");
				triple[2] = tuple[3];
				newTriples.add(triple);

				triple = new int[3];
				triple[0] = objectId;
				triple[1] = ts.putObject("<fourd:ends>");
				triple[2] = tuple[4];
				newTriples.add(triple);
			}

			// view original ids from tuple as perdurants
			triple = new int[3];
			triple[0] = tuple[0];
			triple[1] = ts.putObject("<fourd:hasTimeSlice>");
			triple[2] = subjectId;
			newTriples.add(triple);

			// if range arg from original relation is not an atom, link perdurant to time slice
			if (objectId != -1) {
				triple = new int[3];
				triple[0] = tuple[2];
				triple[1] = ts.putObject("<fourd:hasTimeSlice>");
				triple[2] = objectId;
				newTriples.add(triple);
			}

		}

		ts.writeTuples(newTriples, OUT_FILE);

		fc.shutdownNoExit();
	}

}