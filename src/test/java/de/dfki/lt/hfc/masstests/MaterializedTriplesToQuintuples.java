package de.dfki.lt.hfc.masstests;

import de.dfki.lt.hfc.ForwardChainer;
import de.dfki.lt.hfc.TupleStore;
import de.dfki.lt.hfc.types.XsdInt;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static de.dfki.lt.hfc.TestUtils.getResource;
import static de.dfki.lt.hfc.TestUtils.getTempFile;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class MaterializedTriplesToQuintuples {

	private static final int MAX_INT = 1000;

	private static final int NO_OF_ATOMS = 100000;

	private static final int NO_OF_TUPLES = 500000;

	private static final String IN_FILE = getTempFile("ltworld.jena.materialized.nt");

	private static final String OUT_FILE = getTempFile("approach1.ltworld.nt");


	private static int makeRandom(int max) {
		return (int)(Math.round(Math.random() * max));
	}

	@Test
	public void writeMaterializedTriples() throws Exception {
    //   time java -server -cp .:../lib/trove-2.1.0.jar -Xmx1024m de/dfki/lt/hfc/tests/MaterializedTriplesToFile
	  int tuples = 0;
	  {
	    ForwardChainer fc = new ForwardChainer(

	      4, // int noOfCores,
        false, // boolean verbose,
        false, //boolean rdfCheck,
        true, //boolean eqReduction,
        3, //int minNoOfArgs,
        3, //int maxNoOfArgs,
        100000, //int noOfAtoms,
        500000, //int noOfTuples,
        getResource("default.eqred.nt"),
        getResource("default.eqred.rdl"),
        getResource("default.ns"));
	    fc.uploadTuples(getResource("ltworld.jena.nt"));
	    fc.computeClosure();
	    tuples = fc.tupleStore.getAllTuples().size();
	    fc.tupleStore.writeTuples(IN_FILE);
	    fc.shutdownNoExit();
	  }
    ForwardChainer fc2 = new ForwardChainer(
        4, // int noOfCores,
        false, // boolean verbose,
        false, //boolean rdfCheck,
        true, //boolean eqReduction,
        3, //int minNoOfArgs,
        3, //int maxNoOfArgs,
        100000, //int noOfAtoms,
        500000, //int noOfTuples,
        IN_FILE,
        getResource("default.eqred.rdl"),
        getResource("default.ns"));
    int tuples2 = fc2.tupleStore.getAllTuples().size();
    fc2.shutdownNoExit();
    assertEquals(tuples, tuples2);
  }


	@Test
	public void triplesToQuintuplesTest() throws Exception {
    {
      ForwardChainer fc = new ForwardChainer(

        4, // int noOfCores,
        false, // boolean verbose,
        false, //boolean rdfCheck,
        true, //boolean eqReduction,
        3, //int minNoOfArgs,
        3, //int maxNoOfArgs,
        100000, //int noOfAtoms,
        500000, //int noOfTuples,
        getResource("default.eqred.nt"),
        getResource("default.eqred.rdl"),
        getResource("default.ns"));
      fc.uploadTuples(getResource("ltworld.jena.nt"));
      fc.computeClosure();
      fc.tupleStore.writeTuples(IN_FILE);
      fc.shutdownNoExit();
    }
		//   time java -server -cp .:../lib/trove-2.1.0.jar -Xmx1024m de/dfki/lt/hfc/tests/MaterializedTriplesToQuintuples
		ForwardChainer fc =	new ForwardChainer(2,      // noOfCores
																					 false,   // verbose
																					 false,  // rdfCheck
																					 true,   // eqReduction
																					 3,      // minNoOfArgs
																					 5,      // maxNoOfArgs
																					 NO_OF_ATOMS,
																					 NO_OF_TUPLES,
																					 IN_FILE,
																					 getResource("default.eqred.rdl"),
																					 getResource("default.ns"));

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
		int tuples = ts.getAllTuples().size();
		ts.writeTuples(newTuples, OUT_FILE);

		fc =  new ForwardChainer(2,      // noOfCores
        false,   // verbose
        false,  // rdfCheck
        true,   // eqReduction
        3,      // minNoOfArgs
        5,      // maxNoOfArgs
        NO_OF_ATOMS,
        NO_OF_TUPLES,
        OUT_FILE,
        getResource("default.eqred.rdl"),
        getResource("default.ns"));

		assertEquals(tuples, fc.tupleStore.getAllTuples().size());

		fc.shutdownNoExit();
	}

}