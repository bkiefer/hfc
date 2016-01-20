package de.dfki.lt.hfc;

import static de.dfki.lt.hfc.TestUtils.*;
import static org.junit.Assert.assertEquals;

import java.io.File;

import org.junit.Test;

public class MaterializedTriplesToFile {

	@Test public void writeMaterializedTriples() throws Exception {
		//   time java -server -cp .:../lib/trove-2.1.0.jar -Xmx1024m de/dfki/lt/hfc/tests/MaterializedTriplesToFile
				ForwardChainer fc =	new ForwardChainer(100000, 500000,
																					 getResource("default.nt"),
																					 getResource("default.rdl"),
																					 getResource("default.ns"));
		fc.uploadTuples(getResource("ltworld.jena.nt"));
		fc.computeClosure();
		int tuples = fc.tupleStore.allTuples.size();
		String tmpFile = getTempFile("ltworld.jena.materialized.nt");
		fc.tupleStore.writeTuples(tmpFile);
		fc.shutdownNoExit();
    ForwardChainer fc2 = new ForwardChainer(100000, 500000,
        tmpFile,
        getResource("default.rdl"),
        getResource("default.ns"));
    assertEquals(tuples, fc2.tupleStore.allTuples.size());
    fc2.shutdownNoExit();
    System.exit(0);
	}

}