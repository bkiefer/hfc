package de.dfki.lt.hfc.runnable;

import de.dfki.lt.hfc.Namespace;
import de.dfki.lt.hfc.Query;
import de.dfki.lt.hfc.TupleStore;

import static de.dfki.lt.hfc.TestUtils.getResource;

public class TestApproach1copy {

  private static final int NO_OF_ATOMS = 100000;

  private static final int NO_OF_TUPLES = 500000;

  private static Namespace NAMESPACE;

  private static final String TUPLE_FILE = getResource("approach1.ltworld.nt");


  public static void main(String[] args) throws Exception {
    NAMESPACE = new Namespace(getResource("default.ns"));

    long start = System.currentTimeMillis();
    //   time java -server -cp .:../lib/trove-2.1.0.jar -Xmx1024m de/dfki/lt/hfc/tests/QuintuplesToNaryRelations
    TupleStore ts = new TupleStore(true, false, true, 5, 5, NO_OF_ATOMS, NO_OF_TUPLES,
                                   NAMESPACE,
                                   TUPLE_FILE);
    System.out.println("load time: " + ((System.currentTimeMillis() - start) / 1000.0));

    Query q = new Query(ts);

    start = System.currentTimeMillis();
    q.query("SELECT DISTINCT ?start WHERE ?subj ?pred ?obj ?start ?end");
    System.out.println("query time 1: " + ((System.currentTimeMillis() - start) / 1000.0));

    start = System.currentTimeMillis();
    q.query("SELECT DISTINCT ?start ?end WHERE ?subj ?pred ?obj ?start ?end");
    System.out.println("query time 2: " + ((System.currentTimeMillis() - start) / 1000.0));

    start = System.currentTimeMillis();
    q.query("SELECT ?obj WHERE ?subj ?pred ?obj ?start ?end FILTER ?start != ?end");
    System.out.println("query time 3: " + ((System.currentTimeMillis() - start) / 1000.0));

    start = System.currentTimeMillis();
    q.query("SELECT DISTINCT ?subj WHERE ?subj ?pred ?obj ?start1 ?end1 & ?obj ?pred ?subj ?start2 ?end2");
    System.out.println("query time 4: " + ((System.currentTimeMillis() - start) / 1000.0));

    start = System.currentTimeMillis();
    for (int i = 0; i < 100; i++)
      q.query("SELECT * WHERE <ltw:obj_68081> ?pred ?obj ?start ?end");
    System.out.println("query time 5: " + ((System.currentTimeMillis() - start) / 1000.0));

    start = System.currentTimeMillis();
    for (int i = 0; i < 100; i++)
      q.query("SELECT DISTINCT ?subj WHERE ?subj ?pred ?obj ?start \"936\"^^<xsd:int>");
    System.out.println("query time 6: " + ((System.currentTimeMillis() - start) / 1000.0));

  }

}
