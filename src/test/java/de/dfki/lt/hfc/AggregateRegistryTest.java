package de.dfki.lt.hfc;

import static org.junit.Assert.*;

import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import org.junit.Test;

import gnu.trove.THashSet;

public class AggregateRegistryTest {
	@Test
	public void testAggregateRegistry(){
		//test method AggregateRegistry(TupleStore tuplestore)
		TupleStore ts = new TupleStore(1,2);
		//what object to use?
		//?.AggregateRegistry(ts);
	}

	@Test
	public void testevaluate() {
		/*test method evaluate(String className, BindingTable args, SortedMap<Integer, Integer> nameToPos,
		 Map<Integer, String> nameToExternalName) */
		
		Set<int[]> table1 = new THashSet<int[]>(TupleStore.DEFAULT_HASHING_STRATEGY);
		SortedMap<Integer, Integer> nameToPos = new TreeMap<Integer, Integer>();
		Map<Integer, String> nameToExternalName = new TreeMap<Integer, String>();
		BindingTable bt = new BindingTable(table1, nameToPos, nameToExternalName);
		//what object to call evaluate on?
		//?.evaluate("class", bt, nameToPos, nameToExternalName);
	}

}
