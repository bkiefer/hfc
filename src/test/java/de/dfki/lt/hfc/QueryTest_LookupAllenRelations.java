package de.dfki.lt.hfc;


import junit.framework.Assert;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;


import static junit.framework.TestCase.*;

/**
 * Created by christian on 08/06/17.
 */
public class QueryTest_LookupAllenRelations {

    static ForwardChainer fc;



    private static String getResource(String name) {
        return TestUtils.getTestResource("Query", name);
    }

    @BeforeAll
    public static void init() throws Exception {

        fc =  new ForwardChainer(4,                                                    // #cores
                false,                                                 // verbose
                false,                                                 // RDF Check
                false,                                                // EQ reduction disabled
                5,                                                    // min #args
                5,                                                    // max #args
                100000,                                               // #atoms
                500000,                                               // #tuples
                getResource("lookupIntervalTest.nt"),                 // tuple file
                getResource("allenTest.rdl"),                           // rule file  TODO
                getResource("default.ns"),                            // namespace file
                getResource("allenTest.idx")
        );


        // compute deductive closure
        // TODO move this into extra tests -> fcInterval.computeClosure();
    }

    @Test
    public void testSelectWhere(){
        TupleStore tupleStore = fc.tupleStore;
        Query query = new Query(tupleStore);

        try { // F
            BindingTable bt = query.query("SELECT ?s ?o WHERE ?s <test:hasValue> ?o D \"200\"^^<xsd:long>  \"1600\"^^<xsd:long> ");
            String[][] expected = {{"<test:Sensor2>" ,"\"4\"^^<xsd:int>"},
                    {"<test:Sensor1>" ,"\"3\"^^<xsd:int>"},
                    {"<test:Sensor2>" ,"\"5\"^^<xsd:int>"}};
            TestUtils.checkResult(expected, bt, bt.getVars());
        } catch (QueryParseException e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void testSelectDistinctWhere(){
        TupleStore tupleStore = fc.tupleStore;
        Query query = new Query(tupleStore);
        try { // F
            BindingTable bt = query.query("SELECT DISTINCT ?s ?o WHERE ?s <test:hasValue> ?o D \"100\"^^<xsd:long>  \"1550\"^^<xsd:long> ");
            String[][] expected = {{"<test:Sensor2>" ,"\"4\"^^<xsd:int>"},
                    {"<test:Sensor1>" ,"\"3\"^^<xsd:int>"},
                    {"<test:Sensor2>" ,"\"2\"^^<xsd:int>"},
                    {"<test:Sensor1>" ,"\"2\"^^<xsd:int>"},
                    {"<test:Sensor2>" ,"\"5\"^^<xsd:int>"}};
            TestUtils.checkResult(expected, bt, bt.getVars());
        } catch (QueryParseException e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void testSelectWhereFilter(){
        TupleStore tupleStore = fc.tupleStore;
        Query query = new Query(tupleStore);
        try { // F
            BindingTable bt = query.query("SELECT DISTINCT ?s ?o WHERE ?s <test:hasValue> ?o D \"200\"^^<xsd:long>  \"1600\"^^<xsd:long> FILTER ?s != <test:Sensor1>");
            String[][] expected = {{"<test:Sensor2>" ,"\"4\"^^<xsd:int>"},
                    {"<test:Sensor2>" ,"\"5\"^^<xsd:int>"}};
            TestUtils.checkResult(expected, bt, bt.getVars());
        } catch (QueryParseException e) {
            e.printStackTrace();
            fail();
        }
    }


    @Test
    public void testSelectDistinctWhereFilter(){
        TupleStore tupleStore = fc.tupleStore;
        Query query = new Query(tupleStore);
        try { // F
            BindingTable bt = query.query("SELECT DISTINCT ?s ?o WHERE ?s <test:hasValue> ?o D \"200\"^^<xsd:long>  \"1600\"^^<xsd:long> FILTER ?o IGreater \"2\"^^<xsd:int>");
            String[][] expected = {{"<test:Sensor2>" ,"\"4\"^^<xsd:int>"},
                    {"<test:Sensor1>" ,"\"3\"^^<xsd:int>"},
                    {"<test:Sensor2>" ,"\"5\"^^<xsd:int>"}};
            TestUtils.checkResult(expected, bt, bt.getVars());
        } catch (QueryParseException e) {
            e.printStackTrace();
            fail();
        }
    }


    @Test
    public void testSelectWhereAggregate(){
        TupleStore tupleStore = fc.tupleStore;
        Query query = new Query(tupleStore);
        try { // F
            BindingTable bt = query.query("SELECT DISTINCT ?s ?o WHERE ?s <test:hasValue> ?o D \"200\"^^<xsd:long>  \"1600\"^^<xsd:long> AGGREGATE  ?number = Count ?o");
            String[][] expected = {{"\"3\"^^<xsd:int>"}};
            TestUtils.checkResult(expected, bt, bt.getVars());
        } catch (QueryParseException e) {
            e.printStackTrace();
            fail();
        }
    }


    @AfterAll
    public static void finish() {
        fc.shutdownNoExit();
    }

}
