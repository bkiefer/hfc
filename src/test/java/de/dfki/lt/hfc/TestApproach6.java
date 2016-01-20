package de.dfki.lt.hfc;

import static de.dfki.lt.hfc.AddSameAs.getResource;

import java.util.ArrayList;
import de.dfki.lt.hfc.*;

public class TestApproach6 {

	private static final int NO_OF_ATOMS = 100000;

	private static final int NO_OF_TUPLES = 500000;

	private static final Namespace NAMESPACE = new Namespace(getResource("default.ns"));

	private static final String TUPLE_FILE = getResource("approach6.ltworld.nt");


	public static void main(String[] args) throws Exception {

		long start = System.currentTimeMillis();

		//   time java -server -cp .:../lib/trove-2.1.0.jar -Xmx1024m de/dfki/lt/hfc/tests/QuintuplesToNaryRelations
		TupleStore ts =	new TupleStore(true, false, true, 3, 3, NO_OF_ATOMS, NO_OF_TUPLES,
																	 NAMESPACE,
																	 TUPLE_FILE);

		System.out.println("load time: " + ((System.currentTimeMillis() - start) / 1000.0));

		Query q = new Query(ts);

		System.gc();

		/*
		start = System.currentTimeMillis();
		q.query("SELECT DISTINCT ?start WHERE ?ts <rdf:type> <fourd:TimeSlice> & ?ts <fourd:starts> ?start");
		System.out.println("query time 1: " + ((System.currentTimeMillis() - start) / 1000.0));

		start = System.currentTimeMillis();
		q.query("SELECT DISTINCT ?start ?end WHERE ?ts <rdf:type> <fourd:TimeSlice> & ?ts <fourd:starts> ?start & ?ts <fourd:ends> ?end");
		System.out.println("query time 2: " + ((System.currentTimeMillis() - start) / 1000.0));

		start = System.currentTimeMillis();
		q.query("SELECT ?obj WHERE ?obj <fourd:hasTimeSlice> ?ts-obj & ?ts-subj ?pred ?ts-obj & ?ts-subj <rdf:type> <fourd:TimeSlice> & ?ts-obj <fourd:starts> ?start & ?ts-obj <fourd:ends> ?end FILTER ?start != ?end");
		System.out.println("query time 3: " + ((System.currentTimeMillis() - start) / 1000.0));


		// Java out-of-memory error
		//start = System.currentTimeMillis();
		//System.out.println(q.query("SELECT DISTINCT ?subj WHERE ?subj <fourd:hasTimeSlice> ?ts-subj1 & ?obj <fourd:hasTimeSlice> ?ts-obj1 & ?ts-subj1 ?pred ?ts-obj1 & ?subj <fourd:hasTimeSlice> ?ts-subj2 & ?obj <fourd:hasTimeSlice> ?ts-obj2 & ?ts-obj2 ?pred ?ts-subj2").toString());
		//System.out.println("query time 4: " + ((System.currentTimeMillis() - start) / 1000.0));
		System.out.println("query time 4: Java out-of-memory-error");

		start = System.currentTimeMillis();
		q.query("SELECT ?pred ?obj ?start ?end WHERE <ltw:obj_68081> <fourd:hasTimeSlice> ?ts-subj & ?ts-subj ?pred ?ts-obj & ?obj <fourd:hasTimeSlice> ?ts-obj & ?ts-subj <fourd:starts> ?start & ?ts-subj <fourd:ends> ?end");
		System.out.println("query time 5: " + ((System.currentTimeMillis() - start) / 1000.0));
		 */

		start = System.currentTimeMillis();
		for (int i = 0; i < 10; i++)
			q.query("SELECT DISTINCT ?subj WHERE ?subj <fourd:hasTimeSlice> ?ts-subj & ?ts-subj <rdf:type> <fourd:TimeSlice> & ?ts-subj <fourd:ends> \"936\"^^<xsd:int>");
		System.out.println("query time 6: " + ((System.currentTimeMillis() - start) / 1000.0));


	}


}