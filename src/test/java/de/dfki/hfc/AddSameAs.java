package de.dfki.lt.hfc;

import java.io.*;
import java.util.*;
import de.dfki.lt.hfc.*;


/**
 * NOTE: in order to perform the measurements properly, it is important to set the
 *       right flag in class TupleStore, viz.,
 *         equivalenceClassReduction
 */
public class AddSameAs {
	
	private static int makeRandom(int max) {
		return (int)(Math.round(Math.random() * max));
	}
	
	public static void main(String[] args) throws Exception {
		
		// java -server -cp .:../lib/trove-2.1.0.jar -Xms1024m -Xmx4096m de/dfki/lt/hfc/tests/AddSameAs

		/*
		// call main method with three arguments:
		//   input file
		//   output file
		//   no of sameAs triples
		// read in individuals
		HashMap<Integer, String> map = new HashMap<Integer, String>(30000);
		BufferedReader in = new BufferedReader(new FileReader(args[0]));
    String str;
		int noOfTriples = -1;
		while ((str = in.readLine()) != null) {
			map.put(++noOfTriples, str);
		}
		// write out sameAs triples
		try {
			PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(args[1])));
			for (int i = 0; i < Integer.parseInt(args[2]); i++) {
				pw.print(map.get(makeRandom(noOfTriples)));
				pw.print(" <owl:sameAs> ");
				pw.print(map.get(makeRandom(noOfTriples)));
				pw.print(" .");
				pw.println();
			}
			pw.flush();
			pw.close();
		}
		catch (IOException e) {
			System.err.println("Error while writing tuples to " + args[1]);
			System.exit(1);
		}
		*/
		
		//long time = System.currentTimeMillis();

		/*
		ForwardChainer fc =	new ForwardChainer(100000, 500000,
																					 "/Users/krieger/Desktop/Java/HFC/hfc/resources/default.nt",
																					 "/Users/krieger/Desktop/Java/HFC/hfc/resources/default.rdl",
																					 "/Users/krieger/Desktop/Java/HFC/hfc/resources/default.ns");
		fc.uploadTuples("/Users/krieger/Desktop/Java/HFC/hfc/resources/ltworld.jena.nt");
		fc.uploadTuples("/Users/krieger/Desktop/sameas/sameas1000.nt");  // 10000 is too much
		fc.computeClosure();
		fc.shutdown();
		*/
		
		
		ForwardChainer fc =	new ForwardChainer(100000, 500000,
																					 "/Users/krieger/Desktop/Java/HFC/hfc/src/resources/default.eqred.nt",
																					 "/Users/krieger/Desktop/Java/HFC/hfc/src/resources/default.eqred.rdl",
																					 "/Users/krieger/Desktop/Java/HFC/hfc/src/resources/default.sameAs.test.ns");
		fc.uploadTuples("/Users/krieger/Desktop/Java/HFC/hfc/src/resources/ltworld.jena.nt");
		fc.uploadTuples("/Users/krieger/Desktop/PAPERS/ICSC2013/sameAs/sameas100000.nt");
		fc.computeClosure();
		fc.computeClosure();
		fc.shutdown();
		
		
		//System.out.println("time: " + (System.currentTimeMillis() - time) / 1000.0);
		
		/*
		Query q = new Query(fc.tupleStore);
		BindingTable bt = q.query("SELECT ?i WHERE ?i <rdf:type> <ltw:CONCRETE>");
		System.out.println(bt.toString());
		*/
		
	}
	
}