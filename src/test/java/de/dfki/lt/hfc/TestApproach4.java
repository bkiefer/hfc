package de.dfki.lt.hfc;

import static de.dfki.lt.hfc.TestUtils.getResource;

public class TestApproach4 {

	private static final int NO_OF_ATOMS = 100000;

	private static final int NO_OF_TUPLES = 500000;

	private static Namespace NAMESPACE;

	private static final String TUPLE_FILE = getResource("approach4.ltworld.nt");


	public static void main(String[] args) throws Exception {
	  NAMESPACE = new Namespace(getResource("default.ns"));
		long start = System.currentTimeMillis();

		//   time java -server -cp .:../lib/trove-2.1.0.jar -Xmx1024m de/dfki/lt/hfc/tests/QuintuplesToNaryRelations
		TupleStore ts =	new TupleStore(true, false, true, 3, 3, NO_OF_ATOMS, NO_OF_TUPLES,
																	 NAMESPACE,
																	 TUPLE_FILE);

		System.out.println("load time: " + ((System.currentTimeMillis() - start) / 1000.0));

		Query q = new Query(ts);

		start = System.currentTimeMillis();
		q.query("SELECT DISTINCT ?start WHERE ?blank <rdf:type> <nary:RangePlusTime> & ?blank <nary:starts> ?start");
		System.out.println("query time 1: " + ((System.currentTimeMillis() - start) / 1000.0));

		start = System.currentTimeMillis();
		q.query("SELECT DISTINCT ?start ?end WHERE ?blank <rdf:type> <nary:RangePlusTime> & ?blank <nary:starts> ?start & ?blank <nary:ends> ?end");
		System.out.println("query time 2: " + ((System.currentTimeMillis() - start) / 1000.0));

		start = System.currentTimeMillis();
		q.query("SELECT ?obj WHERE ?subj ?pred ?blank & ?blank <rdf:type> <nary:RangePlusTime> & ?blank <nary:value> ?obj & ?blank <nary:starts> ?start & ?blank <nary:ends> ?end FILTER ?start != ?end");
		System.out.println("query time 3: " + ((System.currentTimeMillis() - start) / 1000.0));

		start = System.currentTimeMillis();
		q.query("SELECT DISTINCT ?subj WHERE ?subj ?pred ?blank1 & ?blank1 <rdf:type> <nary:RangePlusTime> & ?blank1 <nary:value> ?obj & ?obj ?pred ?blank2 & ?blank2 <rdf:type> <nary:RangePlusTime> & ?blank2 <nary:value> ?subj");
		System.out.println("query time 4: " + ((System.currentTimeMillis() - start) / 1000.0));

		start = System.currentTimeMillis();
		for (int i = 0; i < 100; i++)
			q.query("SELECT ?pred ?obj ?start ?end WHERE <ltw:obj_68081> ?pred ?blank & ?blank <rdf:type> <nary:RangePlusTime> & ?blank <nary:value> ?obj & ?blank <nary:starts> ?start & ?blank <nary:ends> ?end");
		System.out.println("query time 5: " + ((System.currentTimeMillis() - start) / 1000.0));

		start = System.currentTimeMillis();
		for (int i = 0; i < 100; i++)
			q.query("SELECT DISTINCT ?subj WHERE ?subj ?pred ?blank & ?blank <rdf:type> <nary:RangePlusTime> & ?blank <nary:ends> \"936\"^^<xsd:int>");
		System.out.println("query time 6: " + ((System.currentTimeMillis() - start) / 1000.0));

	}

}