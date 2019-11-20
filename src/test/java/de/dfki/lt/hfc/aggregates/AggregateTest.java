package de.dfki.lt.hfc.aggregates;

import static org.junit.Assert.*;

import de.dfki.lt.hfc.Aggregate;
import org.junit.Test;

public class AggregateTest {

	@Test
	public void test() {
		//test constructor Aggregate(String name, String[] vars, int[] args) 
		String[] vars = new String[1];
		int[] args = new int[1];
		Aggregate ag = new Aggregate("name", vars, args);
		assertNotNull(ag);
	}

}
