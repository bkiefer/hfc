package de.dfki.lt.hfc.runnable;

import de.dfki.lt.hfc.TupleStore;

import java.util.Random;

/**
 * the test class for HFC
 */
public class Test {
	
	/**
	 * to measure 100,000,000 triple and 10,000,000 uris/atoms
	 *   time java -server -cp .:trove-2.0.4.jar -Xms15000m -Xmx26000m de/dfki/lt/hfc/TupleStore 10000000 100000000
	 */
	public static void main(String[] args) {
		long start;
		int atoms = Integer.parseInt(args[0]);
		int tuples = Integer.parseInt(args[1]);
		TupleStore ts = new TupleStore(atoms, tuples);
		Random rnd = new Random();
		int[] tuple;
		System.out.println();
		Runtime runtime = Runtime.getRuntime();
		System.out.println("free memory: " + (runtime.freeMemory()/1048576) + "MB");  // 1024 * 1024
		System.out.println("total memory: " + (runtime.totalMemory()/1048576) + "MB");
		System.out.println("max memory: " + (runtime.maxMemory()/1048576) + "MB");
		System.out.println();
		for (int i = 0; i < (tuples/1000000); i++) {
			start = System.currentTimeMillis();
			for (int j = 0; j < 1000000; j++) {
				tuple = new int[3];
				tuple[0] = rnd.nextInt(atoms);
				tuple[1] = rnd.nextInt(atoms);
				tuple[2] = rnd.nextInt(atoms);
				ts.addTuple(tuple);
			}
			System.out.println((1000000 * (i + 1)) + ": " +
												 ((System.currentTimeMillis() - start)/1000.0) + "s " +
												 (runtime.freeMemory()/1048576) + "MB " +
												 (runtime.totalMemory()/1048576) + "MB " +
												 (runtime.maxMemory()/1048576) + "MB");
		}
		System.out.println();
		int no = Integer.parseInt(args[2]);
		System.out.println(args[2] + " in subject position: " + ts.getTuples(0, no).size() + " times");
		System.out.println(args[2] + " in predicate position: " + ts.getTuples(1, no) .size() + " times");
		System.out.println(args[2] + " in object position: " + ts.getTuples(2, no).size() + " times");
		System.out.println();
	}

}