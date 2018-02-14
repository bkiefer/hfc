package de.dfki.lt.hfc;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

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
