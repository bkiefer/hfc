package de.dfki.lt.hfc;

import java.util.ArrayList;
import de.dfki.lt.hfc.*;

public class QuintuplesToNaryRelations {
	
	private static final int NO_OF_ATOMS = 100000;
	
	private static final int NO_OF_TUPLES = 500000;
	
	private static final String IN_FILE = "/Users/krieger/Desktop/Java/HFC/hfc/src/resources/quintuples0.95.time.ltworld.nt";
	
	private static final String OUT_FILE = "/Users/krieger/Desktop/Java/HFC/hfc/src/resources/nary0.95.time.ltworld.nt";
	
	
	private static String makeBlankNode(int counter) {
		return "_:BN" + "-" + String.valueOf(counter);
	}
	
	public static void main(String[] args) throws Exception {
		
		//   time java -server -cp .:../lib/trove-2.1.0.jar -Xmx1024m de/dfki/lt/hfc/tests/QuintuplesToNaryRelations
		ForwardChainer fc =	new ForwardChainer(2,      // noOfCores
																					 true,   // verbose
																					 false,  // rdfCheck
																					 false,  // equiv class reduction
																					 3,      // minNoOfArgs
																					 5,      // maxNoOfArgs
																					 NO_OF_ATOMS,
																					 NO_OF_TUPLES,
																					 IN_FILE,
																					 "/Users/krieger/Desktop/Java/HFC/hfc/src/resources/default.rdl",
																					 "/Users/krieger/Desktop/Java/HFC/hfc/src/resources/default.ns");
		
		TupleStore ts = fc.tupleStore;
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
		
		fc.shutdown();
	}
	
}