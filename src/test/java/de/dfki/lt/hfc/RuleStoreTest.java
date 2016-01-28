package de.dfki.lt.hfc;

import static de.dfki.lt.hfc.TestUtils.*;
import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import org.junit.Test;

public class RuleStoreTest {

	@Test
	public void testRuleStore() throws FileNotFoundException, WrongFormatException, IOException {
		//test constructor that takes Namespace namespace, TupleStore tupleStore
		Namespace namespace = new Namespace(getResource("default.ns"));
		TupleStore tupleStore = new TupleStore(1, 2);
		RuleStore rs1 = new RuleStore(namespace, tupleStore);
		assertNotNull(rs1);
	}
	@Test
	public void testRuleStore1() throws FileNotFoundException, WrongFormatException, IOException{
		//test constructor that takes Namespace namespace, TupleStore tupleStore, String ruleFile
		Namespace namespace = new Namespace(getResource("default.ns"));
		TupleStore tupleStore = new TupleStore(2, 3);
		String ruleFile = new String(getResource("default.test.rdl"));
		RuleStore rs2 = new RuleStore(namespace, tupleStore, ruleFile);
		assertNotNull(rs2);
	}
	@Test
	public void testRuleStore2() throws FileNotFoundException, WrongFormatException, IOException{
		/*test constructor that takes RuleStore(boolean verbose, boolean rdfCheck, int minNoOfArgs, int maxNoOfArgs,
		Namespace namespace, TupleStore tupleStore, String ruleFile)*/
		Namespace namespace = new Namespace(getResource("default.ns"));
		TupleStore tupleStore = new TupleStore(2, 4);
		String ruleFile = new String(getResource("default.test.rdl"));
		RuleStore rs3 = new RuleStore(true, true, 2, 3, namespace, tupleStore, ruleFile);
		assertNotNull(rs3);
		RuleStore rs4 = new RuleStore(false, true, 2, 5, namespace, tupleStore, ruleFile);
		assertNotNull(rs4);
		RuleStore rs5 = new RuleStore(true, false, 2, 6, namespace, tupleStore, ruleFile);
		assertNotNull(rs5);
		RuleStore rs6 = new RuleStore(false, false, 1, 4, namespace, tupleStore, ruleFile);
		assertNotNull(rs6);
	}
	@Test
	public void testisFunctionalVariable1(){
		//test method that takes String literal
		assertFalse(RuleStore.isFunctionalVariable("literal"));
		assertTrue(RuleStore.isFunctionalVariable("?dgdfgdfgj"));
		assertFalse(RuleStore.isFunctionalVariable("??jjgjg"));
		assertFalse(RuleStore.isFunctionalVariable("f?jddgjffjg"));
	}
    @Test
    public void testisFunctionalVariable2(){
    	//test method that takes int id
    	assertFalse(RuleStore.isFunctionalVariable(1));
        assertTrue(RuleStore.isFunctionalVariable(-1));
        assertFalse(RuleStore.isFunctionalVariable(-1000000001));
    }
    @Test
    public void testisRelationalVariable(){
    	//test method that takes int id
    	assertFalse(RuleStore.isRelationalVariable(1));
    }
    @Test
    //TODO refactor
    public void testisValidTuple() throws FileNotFoundException, WrongFormatException, IOException{
    	ArrayList<String> stringTuple = new ArrayList<String>();
    	stringTuple.add("a");
    	Namespace namespace = new Namespace(getResource("default.ns"));
		TupleStore tupleStore = new TupleStore(1, 2);
		String ruleFile = new String(getResource("default.test.rdl"));
		//RuleStore rs = new RuleStore(true, true, 0, 0, namespace, tupleStore, ruleFile);
		//RuleStore rs = new RuleStore(namespace, tupleStore);
		//rs.isValidTuple(stringTuple);
    }
    @Test
    public void testwriteRules() throws FileNotFoundException, WrongFormatException, IOException{
    	Namespace namespace = new Namespace(getResource("default.ns"));
		TupleStore tupleStore = new TupleStore(2, 4);
		String ruleFile = new String(getResource("default.test.rdl"));
		RuleStore rstest = new RuleStore(true, true, 2, 3, namespace, tupleStore, ruleFile);
		rstest.writeRules("fileRules");
		assertFalse("fileRules".isEmpty());
		//test for case verbose != true
		RuleStore rsverbosefalse = new RuleStore(false, true, 2, 3, namespace, tupleStore, ruleFile);
		rsverbosefalse.writeRules("fileRules1");
		assertFalse("fileRules1".isEmpty());
    }
    @Test
    public void testcopyRuleStore() throws FileNotFoundException, WrongFormatException, IOException{
    	Namespace namespace = new Namespace(getResource("default.ns"));
		//TupleStore tupleStore = new TupleStore(2, 4);
		TupleStore ts = new TupleStore(true, true, true, 2, 5, 4, 2, namespace, getResource("default.nt"));
		RuleStore rs = new RuleStore(namespace, ts);
		rs.copyRuleStore(namespace, ts);
		assertFalse(rs == rs.copyRuleStore(namespace, ts));
		//TODO one more branch
    }
}
