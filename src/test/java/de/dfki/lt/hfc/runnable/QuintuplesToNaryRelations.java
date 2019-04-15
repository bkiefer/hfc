package de.dfki.lt.hfc.runnable;

import de.dfki.lt.hfc.Config;
import de.dfki.lt.hfc.ForwardChainer;
import de.dfki.lt.hfc.Hfc;
import de.dfki.lt.hfc.TupleStore;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static de.dfki.lt.hfc.TestingUtils.getTestResource;
import static de.dfki.lt.hfc.runnable.Utils.*;

public class QuintuplesToNaryRelations {

	private static final int NO_OF_ATOMS = 100000;

	private static final int NO_OF_TUPLES = 500000;

	private static final String IN_FILE = getResource("quintuples0.95.time.ltworld.nt");

	private static final String OUT_FILE = getTempFile("nary0.95.time.ltworld.nt");


	private static String makeBlankNode(int counter) {
		return "_:BN" + "-" + String.valueOf(counter);
	}

	public static void main(String[] args) throws Exception {


		Config config = Config.getDefaultConfig();
		Map update = new HashMap<>();
		update.put(Config.NOOFCORES, 2);
		update.put(Config.VERBOSE, true);
		update.put(Config.RDFCHECK, false);
		update.put(Config.EQREDUCTION, false);
		update.put(Config.MINARGS, 3);
		update.put(Config.MAXARGS, 5);
		update.put(Config.NOOFATOMS, NO_OF_ATOMS);
		update.put(Config.NOOFTUPLES, NO_OF_TUPLES);
		update.put(Config.RULEFILES,config.getTupleFiles().add(IN_FILE));
		update.put(Config.RULEFILES,config.getRuleFiles().add(getTestResource("default.rdl")));
		config.updateConfig(update);
		//   time java -server -cp .:../lib/trove-2.1.0.jar -Xmx1024m de/dfki/lt/hfc/tests/MaterializedTriplesToQuintuples
		Hfc fc = new Hfc(config);
		/**
		//   time java -server -cp .:../lib/trove-2.1.0.jar -Xmx1024m de/dfki/lt/hfc/tests/QuintuplesToNaryRelations
		Hfc fc =	new ForwardChainer(2,      // noOfCores
																					 true,   // verbose
																					 false,  // rdfCheck
																					 false,  // equiv class reduction
																					 3,      // minNoOfArgs
																					 5,      // maxNoOfArgs
																					 NO_OF_ATOMS,
																					 NO_OF_TUPLES,
																					 IN_FILE,
																					 getResource("default.rdl")
																					 );
		 **/

		TupleStore ts = fc._tupleStore;
		int[] triple;
		int counter = 0;
		int blankNodeId;
		ArrayList<int[]> newTriples = new ArrayList<int[]>(5 * NO_OF_TUPLES);
		for (int[] tuple : ts.getAllTuples()) {
			blankNodeId = ts.putObject(makeBlankNode(counter++));

			triple = new int[3];
			triple[0] = tuple[0];
			triple[1] = tuple[1];
			triple[2] = blankNodeId;
			newTriples.add(triple);

			triple = new int[3];
			triple[0] = blankNodeId;
			triple[1] = ts.putObject("<rdf:type>");
			triple[2] = ts.putObject("<nary:RangePlusTime>");
			newTriples.add(triple);

			triple = new int[3];
			triple[0] = blankNodeId;
			triple[1] = ts.putObject("<nary:value>");
			triple[2] = tuple[2];
			newTriples.add(triple);

			triple = new int[3];
			triple[0] = blankNodeId;
			triple[1] = ts.putObject("<nary:starts>");
			triple[2] = tuple[3];
			newTriples.add(triple);

			triple = new int[3];
			triple[0] = blankNodeId;
			triple[1] = ts.putObject("<nary:ends>");
			triple[2] = tuple[4];
			newTriples.add(triple);
		}

		ts.writeTuples(newTriples, OUT_FILE);

		fc.shutdownNoExit();
	}

}