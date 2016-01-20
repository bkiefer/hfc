package de.dfki.lt.hfc.tests;

import de.dfki.lt.hfc.*;

import static de.dfki.lt.hfc.AddSameAs.getResource;

import static org.junit.Assert.*;

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
		fc.tupleStore.writeTuples(getResource("ltworld.jena.materialized.nt"));
		fc.shutdownNoExit();
    ForwardChainer fc2 = new ForwardChainer(100000, 500000,
        getResource("ltworld.jena.materialized.nt"),
        getResource("default.rdl"),
        getResource("default.ns"));
    assertEquals(tuples, fc2.tupleStore.allTuples.size());
    fc2.shutdownNoExit();
	}

}