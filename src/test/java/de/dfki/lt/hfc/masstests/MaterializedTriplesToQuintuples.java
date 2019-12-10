package de.dfki.lt.hfc.masstests;

import static de.dfki.lt.hfc.TestingUtils.*;
import static org.junit.Assert.*;


import de.dfki.lt.hfc.*;


import static de.dfki.lt.hfc.TestingUtils.getTestResource;

import de.dfki.lt.hfc.types.XsdInt;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class MaterializedTriplesToQuintuples {

	private static final int MAX_INT = 1000;

	private static final int NO_OF_ATOMS = 100000;

	private static final int NO_OF_TUPLES = 500000;

	private static final String IN_FILE = getTempFile("ltworld.jena.materialized.nt");

	private static final String OUT_FILE = getTempFile("approach1.ltworld.nt");


	private static int makeRandom(int max) {
		return (int)(Math.round(Math.random() * max));
	}

	/** Removed this tests as they take forever to finish
	@Test
	public void writeMaterializedTriples() throws Exception {
    //   time java -server -cp .:../lib/trove-2.1.0.jar -Xmx1024m de/dfki/lt/hfc/tests/MaterializedTriplesToFile
	  int tuples = 0;
	  {
	    Hfc fc = new Hfc(Config.getInstance(getTestResource("test_eq.yml")));
	    fc.uploadTuples(getTestResource("ltworld.jena.nt"));
	    fc.computeClosure();
	    tuples = fc._tupleStore.getAllTuples().size();
	    fc._tupleStore.writeTuples(IN_FILE);
	    fc.shutdownNoExit();
	  }

    Hfc fc2 = new Hfc(Config.getInstance(getTestResource("Empty.yml")));
    fc2.uploadTuples(IN_FILE);
		int tuples2 = fc2._tupleStore.getAllTuples().size();
    fc2.shutdownNoExit();
    assertEquals(tuples, tuples2);
  }



	@Test
	public void triplesToQuintuplesTest() throws Exception {
    	{
      		Hfc fc = new Hfc(Config.getInstance(getTestResource("test_eq.yml")));
      		fc.uploadTuples(getTestResource("ltworld.jena.nt"));
      		fc.computeClosure();
      		fc._tupleStore.writeTuples(IN_FILE);
      		fc.shutdownNoExit();
    	}
		//   time java -server -cp .:../lib/trove-2.1.0.jar -Xmx1024m de/dfki/lt/hfc/tests/MaterializedTriplesToQuintuples
		Map namespaces = new HashMap<>();
		Config config = Config.getInstance(2,      // noOfCores
																					 false,   // verbose
																					 false,  // rdfCheck
																					 true,   // eqReduction
																					 3,      // minNoOfArgs
																					 5,      // maxNoOfArgs
																					 NO_OF_ATOMS,
																					 NO_OF_TUPLES,
																					 IN_FILE,
																					 getTestResource("default.eqred.rdl")
																					 );

		config.addNamespace("ltw", "http://www.lt-world.org/ltw.owl#");
		config.addNamespace("lt", "http://www.lt-world.org/lt.owl#");
		Hfc fc = new Hfc(config);
		TupleStore ts = fc._tupleStore;
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
			tuple[3] = ts.putObject(XsdInt.toString(start));
			tuple[4] = ts.putObject(XsdInt.toString(end));
			newTuples.add(tuple);
		}

		int tuples = ts.getAllTuples().size();
		ts.writeTuples(newTuples, OUT_FILE);

		config =  Config.getInstance(2,      // noOfCores
        false,   // verbose
        false,  // rdfCheck
        true,   // eqReduction
        3,      // minNoOfArgs
        5,      // maxNoOfArgs
        NO_OF_ATOMS,
        NO_OF_TUPLES,
        OUT_FILE,
        getTestResource("default.eqred.rdl"));
		fc = new Hfc(config);
		fc.addNamespace("ltw", "http://www.lt-world.org/ltw.owl#");
		fc.addNamespace("lt", "http://www.lt-world.org/lt.owl#");
		assertEquals(tuples, fc._tupleStore.getAllTuples().size());

		fc.shutdownNoExit();
	}
	**/


}
