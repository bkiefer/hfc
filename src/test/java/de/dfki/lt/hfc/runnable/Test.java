package de.dfki.lt.hfc.runnable;

import de.dfki.lt.hfc.Config;
import de.dfki.lt.hfc.TupleStore;
import de.dfki.lt.hfc.WrongFormatException;

import java.io.IOException;
import java.util.*;

/**
 * the test class for HFC
 */
public class Test {

	/**
	 * to measure 100,000,000 triple and 10,000,000 uris/atoms
	 *   time java -server -cp .:trove-2.0.4.jar -Xms15000m -Xmx26000m de/dfki/lt/hfc/TupleStore 10000000 100000000
	 */
	public static void main(String[] args) throws IOException, WrongFormatException {
		long start;
		int atoms = Integer.parseInt(args[0]);
		int tuples = Integer.parseInt(args[1]);
		Config config = Config.getDefaultConfig();
		Map update = new HashMap();
		update.put(Config.NOOFATOMS, atoms);
		update.put(Config.NOOFTUPLES,tuples);
		config.updateConfig(update);
		TupleStore ts = new TupleStore(config);
		Random rnd = new Random();
		int[] tuple;

		Runtime runtime = Runtime.getRuntime();
		  // 1024 * 1024



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

		int no = Integer.parseInt(args[2]);




	}

}