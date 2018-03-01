package de.dfki.lt.hfc;


import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static de.dfki.lt.hfc.TestUtils.checkResult;
import static junit.framework.TestCase.*;

public class QueryTest_AllenRelations {

    static ForwardChainer fcInterval, fcNoIndex;


    private static String getResource(String name) {
        return TestUtils.getTestResource("Query", name);
    }

    @BeforeAll
    public static void init() throws Exception {

        fcInterval =  new ForwardChainer(4,                                                     // #cores
                false,                                                                    // verbose
                false,                                                                  // RDF Check
                false,                                                                // EQ reduction disabled
                5,                                                    // min #args
                5,                                                    // max #args
                100000,                                               // #atoms
                500000,                                               // #tuples
                getResource("lookupIntervalTest.nt"),                 // tuple file
                getResource("allenTest.rdl"),                           // rule file
                getResource("default.ns"),                            // namespace file
                getResource("allenTest.idx")
        );

        fcNoIndex = new ForwardChainer(4,                                                    // #cores
                false,                                                 // verbose
                false,                                                 // RDF Check
                false,                                                // EQ reduction disabled
                5,                                                    // min #args
                5,                                                    // max #args
                100000,                                               // #atoms
                500000,                                               // #tuples
                getResource("lookupIntervalTest.nt"),                 // tuple file
                getResource("allenTest.rdl"),                           // rule file
                getResource("default.ns")                            // namespace file
        );


    }


    @Test
    public void testTemp() throws QueryParseException {
        String[][] expected = {
//                {"<test:Sensor1>", "\"1\"^^<xsd:int>",  "\"100\"^^<xsd:long>" ,"\"1100\"^^<xsd:long>"},
//                {"<test:Sensor1>", "\"2\"^^<xsd:int>",  "\"180\"^^<xsd:long>", "\"800\"^^<xsd:long>"},
//                {"<test:Sensor2>", "\"2\"^^<xsd:int>",   "\"200\"^^<xsd:long>",  "\"1200\"^^<xsd:long>"},
//                {"<test:Sensor1>",  "\"3\"^^<xsd:int>", "\"300\"^^<xsd:long>", "\"1300\"^^<xsd:long>"}
                {"<test:Sensor1>", "\"1\"^^<xsd:int>"},
                {"<test:Sensor1>", "\"2\"^^<xsd:int>"},
                {"<test:Sensor2>", "\"2\"^^<xsd:int>"},
                {"<test:Sensor1>",  "\"3\"^^<xsd:int>"}
        };
        TupleStore tupleStoreInterval = fcInterval.tupleStore;
        TupleStore tupleStoreNoIdex = fcNoIndex.tupleStore;
        Query queryInterval = new Query(tupleStoreInterval);
        Query queryNoIndex = new Query(tupleStoreNoIdex);

            String input = "SELECT ?s ?o WHERE ?s <rdf:type> <test:sensor> \"0\"^^<xsd:long> \"999\"^^<xsd:long> & ?s <test:hasValue> ?o ?t1 ?t2 ";
            BindingTable btInterval = queryInterval.query(input);
            BindingTable btNoIndex = queryNoIndex.query(input);
//            checkResult(fcInterval, btInterval, expected, btInterval.getVars());//"?s", "?o", "?rel1", "?rel2");
//            checkResult(fcNoIndex, btNoIndex, expected, btNoIndex.getVars());//"?s", "?o", "?rel1", "?rel2");

    }

    /**
     * 1)
     * =====================================================================================
     * | ?s                 | ?o                 | ?rel1              | ?rel2              |
     * =====================================================================================
     * | <test:Sensor1>     | "1"^^<xsd:int>     | "100"^^<xsd:long>  | "1100"^^<xsd:long> |
     * | <test:Sensor1>     | "2"^^<xsd:int>     | "180"^^<xsd:long>  | "800"^^<xsd:long>  |
     * | <test:Sensor2>     | "2"^^<xsd:int>     | "200"^^<xsd:long>  | "1200"^^<xsd:long> |
     * | <test:Sensor1>     | "3"^^<xsd:int>     | "300"^^<xsd:long>  | "1300"^^<xsd:long> |
     * ------------------------------------------------------------------------------------
     *
     */
    @Test
    public void testBefore() throws QueryParseException {
        String[][] expected = {
//                {"<test:Sensor1>", "\"1\"^^<xsd:int>",  "\"100\"^^<xsd:long>" ,"\"1100\"^^<xsd:long>"},
//                {"<test:Sensor1>", "\"2\"^^<xsd:int>",  "\"180\"^^<xsd:long>", "\"800\"^^<xsd:long>"},
//                {"<test:Sensor2>", "\"2\"^^<xsd:int>",   "\"200\"^^<xsd:long>",  "\"1200\"^^<xsd:long>"},
//                {"<test:Sensor1>",  "\"3\"^^<xsd:int>", "\"300\"^^<xsd:long>", "\"1300\"^^<xsd:long>"}
                {"<test:Sensor1>", "\"1\"^^<xsd:int>"},
                {"<test:Sensor1>", "\"2\"^^<xsd:int>"},
                {"<test:Sensor2>", "\"2\"^^<xsd:int>"},
                {"<test:Sensor1>",  "\"3\"^^<xsd:int>"}
        };
        TupleStore tupleStoreInterval = fcInterval.tupleStore;
        TupleStore tupleStoreNoIdex = fcNoIndex.tupleStore;
        Query queryInterval = new Query(tupleStoreInterval);
        Query queryNoIndex = new Query(tupleStoreNoIdex);
            String input = "SELECT ?s ?o WHERE ?s <test:hasValue> ?o Bf \"1400\"^^<xsd:long>  \"1600\"^^<xsd:long> ";
            BindingTable btInterval = queryInterval.query(input);
            BindingTable btNoIndex = queryNoIndex.query(input);
            checkResult(fcInterval, btInterval, expected, btInterval.getVars());//"?s", "?o", "?rel1", "?rel2");
            checkResult(fcNoIndex, btNoIndex, expected, btNoIndex.getVars());//"?s", "?o", "?rel1", "?rel2");

    }

    /**
     * 2)
     * =====================================================================================
     * | ?s                 | ?o                 | ?rel1              | ?rel2              |
     * =====================================================================================
     * | <test:Sensor1>     | "8"^^<xsd:int>     | "800"^^<xsd:long>  | "1800"^^<xsd:long> |
     * | <test:Sensor2>     | "7"^^<xsd:int>     | "700"^^<xsd:long>  | "1700"^^<xsd:long> |
     * | <test:Sensor1>     | "9"^^<xsd:int>     | "900"^^<xsd:long>  | "1900"^^<xsd:long> |
     * | <test:Sensor2>     | "10"^^<xsd:int>    | "1000"^^<xsd:long> | "2000"^^<xsd:long> |
     * -------------------------------------------------------------------------------------
     *
     */
    @Test
    public void testAfter() throws QueryParseException {
        String[][] expected = {
                {"<test:Sensor1>", "\"8\"^^<xsd:int>"},
                {"<test:Sensor2>", "\"7\"^^<xsd:int>"},
                {"<test:Sensor1>", "\"9\"^^<xsd:int>"},
                {"<test:Sensor2>",  "\"10\"^^<xsd:int>"}};
        TupleStore tupleStoreInterval = fcInterval.tupleStore;
        TupleStore tupleStoreNoIdex = fcNoIndex.tupleStore;
        Query queryInterval = new Query(tupleStoreInterval);
        Query queryNoIndex = new Query(tupleStoreNoIdex);
            String input = "SELECT ?s ?o WHERE ?s <test:hasValue> ?o Af \"0\"^^<xsd:long>  \"600\"^^<xsd:long> ";
            BindingTable btInterval = queryInterval.query(input);
            BindingTable btNoIndex = queryNoIndex.query(input);
            checkResult(fcInterval, btInterval, expected, btInterval.getVars());//"?s", "?o", "?rel1", "?rel2");
            checkResult(fcNoIndex, btNoIndex, expected, btNoIndex.getVars());//"?s", "?o", "?rel1", "?rel2");

    }

    /**
     * 3)
     * =====================================================================================
     * | ?s                 | ?o                 | ?rel1              | ?rel2              |
     * =====================================================================================
     * | <test:Sensor2>     | "2"^^<xsd:int>     | "200"^^<xsd:long>  | "1200"^^<xsd:long> |
     * -------------------------------------------------------------------------------------
     *
     */
    @Test
    public void testEqual() throws QueryParseException {
        String[][] expected = {
                {"<test:Sensor2>", "\"2\"^^<xsd:int>"}
                };
        TupleStore tupleStoreInterval = fcInterval.tupleStore;
        TupleStore tupleStoreNoIdex = fcNoIndex.tupleStore;
        Query queryInterval = new Query(tupleStoreInterval);
        Query queryNoIndex = new Query(tupleStoreNoIdex);

            String input = "SELECT ?s ?o WHERE ?s <test:hasValue> ?o EA \"200\"^^<xsd:long>  \"1200\"^^<xsd:long> ";
            BindingTable btInterval = queryInterval.query(input);
            BindingTable btNoIndex = queryNoIndex.query(input);
            checkResult(fcInterval, btInterval, expected, btInterval.getVars());//"?s", "?o", "?rel1", "?rel2");
            checkResult(fcNoIndex, btNoIndex, expected, btNoIndex.getVars());//"?s", "?o", "?rel1", "?rel2");

    }

    /**
     * 4)
     * =====================================================================================
     * | <test:Sensor2>     | "2"^^<xsd:int>     | "200"^^<xsd:long>  | "1200"^^<xsd:long> |
     * -------------------------------------------------------------------------------------
     */
    @Test
    public void testStart() throws QueryParseException {
        String[][] expected = {
                {"<test:Sensor2>", "\"2\"^^<xsd:int>"}
        };
        TupleStore tupleStoreInterval = fcInterval.tupleStore;
        TupleStore tupleStoreNoIdex = fcNoIndex.tupleStore;
        
        Query queryInterval = new Query(tupleStoreInterval);
        Query queryNoIndex = new Query(tupleStoreNoIdex);

            String input = "SELECT ?s ?o WHERE ?s <test:hasValue> ?o S \"200\"^^<xsd:long>  \"1300\"^^<xsd:long> ";
            BindingTable btInterval = queryInterval.query(input);
            BindingTable btNoIndex = queryNoIndex.query(input);
            checkResult(fcInterval, btInterval, expected, btInterval.getVars());//"?s", "?o", "?rel1", "?rel2");
            checkResult(fcNoIndex, btNoIndex, expected, btNoIndex.getVars());//"?s", "?o", "?rel1", "?rel2");

    }

    /**
     * 5)
     * =====================================================================================
     * | ?s                 | ?o                 | ?rel3              | ?rel4              |
     * =====================================================================================
     * | <test:Sensor2>     | "2"^^<xsd:int>     | "200"^^<xsd:long>  | "1200"^^<xsd:long> |
     * -------------------------------------------------------------------------------------
     */
    @Test
    public void testStartI() throws QueryParseException {
        String[][] expected = {
                {"<test:Sensor2>", "\"2\"^^<xsd:int>"}
        };
        TupleStore tupleStoreInterval = fcInterval.tupleStore;
        TupleStore tupleStoreNoIdex = fcNoIndex.tupleStore;
        
        Query queryInterval = new Query(tupleStoreInterval);
        Query queryNoIndex = new Query(tupleStoreNoIdex);
        

            String input = "SELECT ?s ?o WHERE ?s <test:hasValue> ?o Si \"200\"^^<xsd:long>  \"1100\"^^<xsd:long> ";
            BindingTable btInterval = queryInterval.query(input);
            BindingTable btNoIndex = queryNoIndex.query(input);
            checkResult(fcInterval, btInterval, expected, btInterval.getVars());//"?s", "?o", "?rel1", "?rel2");
            checkResult(fcNoIndex, btNoIndex, expected, btNoIndex.getVars());//"?s", "?o", "?rel1", "?rel2");

    }

    /**
     * 6)Expected
     * =====================================================================================
     * | ?s                 | ?o                 | ?rel3              | ?rel4              |
     * =====================================================================================
     * | <test:Sensor2>     | "2"^^<xsd:int>     | "200"^^<xsd:long>  | "1200"^^<xsd:long> |
     * -------------------------------------------------------------------------------------
     *
     */
    @Test
    public void testFinish() throws QueryParseException {
        String[][] expected = {
                {"<test:Sensor2>", "\"2\"^^<xsd:int>"}
        };
        TupleStore tupleStoreInterval = fcInterval.tupleStore;
        TupleStore tupleStoreNoIdex = fcNoIndex.tupleStore;
        
        Query queryInterval = new Query(tupleStoreInterval);
        Query queryNoIndex = new Query(tupleStoreNoIdex);
        

            String input = "SELECT ?s ?o WHERE ?s <test:hasValue> ?o F \"100\"^^<xsd:long>  \"1200\"^^<xsd:long> ";
            BindingTable btInterval = queryInterval.query(input);
            BindingTable btNoIndex = queryNoIndex.query(input);
            checkResult(fcInterval, btInterval, expected, btInterval.getVars());//"?s", "?o", "?rel1", "?rel2");
            checkResult(fcNoIndex, btNoIndex, expected, btNoIndex.getVars());//"?s", "?o", "?rel1", "?rel2");

    }

    /**
     * 7)Expected
     * =====================================================================================
     * | ?s                 | ?o                 | ?rel3              | ?rel4              |
     * =====================================================================================
     * | <test:Sensor2>     | "2"^^<xsd:int>     | "200"^^<xsd:long>  | "1200"^^<xsd:long> |
     * -------------------------------------------------------------------------------------
     *
     */
    @Test
    public void testFinishI() throws QueryParseException {
        String[][] expected = {
                {"<test:Sensor2>", "\"2\"^^<xsd:int>"}
        };
        TupleStore tupleStoreInterval = fcInterval.tupleStore;
        TupleStore tupleStoreNoIdex = fcNoIndex.tupleStore;
        
        Query queryInterval = new Query(tupleStoreInterval);
        Query queryNoIndex = new Query(tupleStoreNoIdex);
        

            String input = "SELECT ?s ?o WHERE ?s <test:hasValue> ?o Fi \"800\"^^<xsd:long>  \"1200\"^^<xsd:long> ";
            BindingTable btInterval = queryInterval.query(input);
            BindingTable btNoIndex = queryNoIndex.query(input);

            checkResult(fcInterval, btInterval, expected, btInterval.getVars());//"?s", "?o", "?rel1", "?rel2");
            checkResult(fcNoIndex, btNoIndex, expected, btNoIndex.getVars());//"?s", "?o", "?rel1", "?rel2");

    }

    /**
     * 8)
     * =====================================================================================
     * | ?s                 | ?o                 | ?rel1              | ?rel2              |
     * =====================================================================================
     * | <test:Sensor1>     | "3"^^<xsd:int>     | "300"^^<xsd:long>  | "1300"^^<xsd:long> |
     * | <test:Sensor2>     | "4"^^<xsd:int>     | "400"^^<xsd:long>  | "1400"^^<xsd:long> |
     * | <test:Sensor2>     | "5"^^<xsd:int>     | "500"^^<xsd:long>  | "1500"^^<xsd:long> |
     * | <test:Sensor1>     | "8"^^<xsd:int>     | "800"^^<xsd:long>  | "1800"^^<xsd:long> |
     * | <test:Sensor1>     | "2"^^<xsd:int>     | "180"^^<xsd:long>  | "800"^^<xsd:long>  |
     * | <test:Sensor1>     | "9"^^<xsd:int>     | "900"^^<xsd:long>  | "1900"^^<xsd:long> |
     * | <test:Sensor2>     | "2"^^<xsd:int>     | "200"^^<xsd:long>  | "1200"^^<xsd:long> |
     * | <test:Sensor2>     | "7"^^<xsd:int>     | "700"^^<xsd:long>  | "1700"^^<xsd:long> |
     * | <test:Sensor1>     | "6"^^<xsd:int>     | "600"^^<xsd:long>  | "1600"^^<xsd:long> |
     * -------------------------------------------------------------------------------------
     *
     */
    @Test
    public void testDuring() throws QueryParseException {
        String[][] expected = {
                {"<test:Sensor1>", "\"3\"^^<xsd:int>"},
                {"<test:Sensor2>", "\"4\"^^<xsd:int>"},
                {"<test:Sensor2>", "\"5\"^^<xsd:int>"},
                {"<test:Sensor1>", "\"8\"^^<xsd:int>"},
                {"<test:Sensor1>", "\"2\"^^<xsd:int>"},
                {"<test:Sensor1>", "\"9\"^^<xsd:int>"},
                {"<test:Sensor2>", "\"2\"^^<xsd:int>"},
                {"<test:Sensor2>", "\"7\"^^<xsd:int>"},
                {"<test:Sensor1>", "\"6\"^^<xsd:int>"}
        };
        TupleStore tupleStoreInterval = fcInterval.tupleStore;
        TupleStore tupleStoreNoIdex = fcNoIndex.tupleStore;
        
        Query queryInterval = new Query(tupleStoreInterval);
        Query queryNoIndex = new Query(tupleStoreNoIdex);

        

            String input = "SELECT ?s ?o WHERE ?s <test:hasValue> ?o D \"100\"^^<xsd:long>  \"2000\"^^<xsd:long> ";
            BindingTable btInterval = queryInterval.query(input);
            BindingTable btNoIndex = queryNoIndex.query(input);
            checkResult(fcInterval, btInterval, expected, btInterval.getVars());//"?s", "?o", "?rel1", "?rel2");
            checkResult(fcNoIndex, btNoIndex, expected, btNoIndex.getVars());//"?s", "?o", "?rel1", "?rel2");

    }

    /**
     * 9)
     * =====================================================================================
     * | ?s                 | ?o                 | ?rel1              | ?rel2              |
     * = ====================================================================================
     * | <test:Sensor1>     | "10"^^<xsd:int>    | "0"^^<xsd:long>    | "2100"^^<xsd:long> |
     * -------------------------------------------------------------------------------------
     */
    @Test
    public void testDuringI() throws QueryParseException {
        String[][] expected = {
                {"<test:Sensor1>", "\"10\"^^<xsd:int>"}
        };
        TupleStore tupleStoreInterval = fcInterval.tupleStore;
        TupleStore tupleStoreNoIdex = fcNoIndex.tupleStore;

        
        Query queryInterval = new Query(tupleStoreInterval);
        Query queryNoIndex = new Query(tupleStoreNoIdex);

            String input = "SELECT ?s ?o WHERE ?s <test:hasValue> ?o Di \"100\"^^<xsd:long>  \"2000\"^^<xsd:long> ";
            BindingTable btInterval = queryInterval.query(input);
            BindingTable btNoIndex = queryNoIndex.query(input);
            checkResult(fcInterval, btInterval, expected, btInterval.getVars());//"?s", "?o", "?rel1", "?rel2");
            checkResult(fcNoIndex, btNoIndex, expected, btNoIndex.getVars());//"?s", "?o", "?rel1", "?rel2");

    }

    /**
     * 10)
     * =================================================================================
     * | ?s                | ?o                | ?rel1             | ?rel2             |
     * =================================================================================
     * | <test:Sensor1>    | "2"^^<xsd:int>    | "180"^^<xsd:long> | "800"^^<xsd:long> |
     * ---------------------------------------------------------------------------------
     *
     */
    @Test
    public void testMeeting() throws QueryParseException {
        String[][] expected = {
                {"<test:Sensor1>", "\"2\"^^<xsd:int>"}
        };
        //TupleStore tupleStoreInterval = fcInterval.tupleStore;
        TupleStore tupleStoreNoIdex = fcNoIndex.tupleStore;
        //TupleStore tupleStoreIntervalNew = fcNew.tupleStore;
        
        //Query queryInterval = new Query(tupleStoreInterval);
        Query queryNoIndex = new Query(tupleStoreNoIdex);
        //Query queryNewInterval = new Query(tupleStoreIntervalNew);

            String input = "SELECT ?s ?o WHERE ?s <test:hasValue> ?o M \"800\"^^<xsd:long>  \"820\"^^<xsd:long> ";
            //BindingTable btInterval = queryInterval.query(input);
            BindingTable btNoIndex = queryNoIndex.query(input);
            //BindingTable btNewInterval = queryNewInterval.query(input);
            //checkResult(fcInterval, btInterval, expected, btInterval.getVars());//"?s", "?o", "?rel1", "?rel2");
            checkResult(fcNoIndex, btNoIndex, expected, btNoIndex.getVars());//"?s", "?o", "?rel1", "?rel2");
            //checkResult(fcNew, btNewInterval, expected, btNewInterval.getVars());// "?s", "?o", "?rel1", "?rel2");

    }

    /**
     * 11)
     * =================================================================================
     * | ?s                | ?o                | ?rel1             | ?rel2             |
     * =================================================================================
     * | <test:Sensor2>    | "2"^^<xsd:int>    | "200"^^<xsd:long> | "1200"^^<xsd:long> |
     * ---------------------------------------------------------------------------------
     *
     */
    @Test
    public void testMeetingI() throws QueryParseException {
        String[][] expected = {
                {"<test:Sensor2>", "\"2\"^^<xsd:int>"}
        };
        TupleStore tupleStoreInterval = fcInterval.tupleStore;
        TupleStore tupleStoreNoIdex = fcNoIndex.tupleStore;
        
        Query queryInterval = new Query(tupleStoreInterval);
        Query queryNoIndex = new Query(tupleStoreNoIdex);
        

            String input = "SELECT ?s ?o WHERE ?s <test:hasValue> ?o Mi \"100\"^^<xsd:long>  \"200\"^^<xsd:long> ";
            BindingTable btInterval = queryInterval.query(input);
            BindingTable btNoIndex = queryNoIndex.query(input);

            checkResult(fcInterval, btInterval, expected, btInterval.getVars());//"?s", "?o", "?rel1", "?rel2");
            checkResult(fcNoIndex, btNoIndex, expected, btNoIndex.getVars());//"?s", "?o", "?rel1", "?rel2");

    }

    /**
     * 12)
     * =================================================================================
     * | ?s                | ?o                | ?rel1             | ?rel2             |
     * =================================================================================
     * | <test:Sensor1>    | "2"^^<xsd:int>    | "180"^^<xsd:long> | "800"^^<xsd:long> |
     * ---------------------------------------------------------------------------------
     */
    @Test
    public void testOverlap() throws QueryParseException {
        String[][] expected = {
                {"<test:Sensor1>", "\"2\"^^<xsd:int>"}
        };
        TupleStore tupleStoreInterval = fcInterval.tupleStore;
        TupleStore tupleStoreNoIdex = fcNoIndex.tupleStore;
        Query queryInterval = new Query(tupleStoreInterval);
        Query queryNoIndex = new Query(tupleStoreNoIdex);

            String input = "SELECT ?s ?o WHERE ?s <test:hasValue> ?o O \"400\"^^<xsd:long>  \"900\"^^<xsd:long> ";
            BindingTable btInterval = queryInterval.query(input);
            BindingTable btNoIndex = queryNoIndex.query(input);
            String tempQuery = "SELECT ?s ?o WHERE ?s <test:hasValue> ?o ?t ?t1 FILTER LGreater ?t \"400\"^^<xsd:long>";
            BindingTable tempTable = queryNoIndex.query(tempQuery);

            checkResult(fcInterval, btInterval, expected, btInterval.getVars());//"?s", "?o", "?rel1", "?rel2");
            checkResult(fcNoIndex, btNoIndex, expected, btNoIndex.getVars());//"?s", "?o", "?rel1", "?rel2");


    }

    /**
     * 13)
     * =====================================================================================
     * | ?s                 | ?o                 | ?rel1              | ?rel2              |
     * =====================================================================================
     * | <test:Sensor2>     | "10"^^<xsd:int>    | "1000"^^<xsd:long> | "2000"^^<xsd:long> |
     * | <test:Sensor1>     | "9"^^<xsd:int>     | "900"^^<xsd:long>  | "1900"^^<xsd:long> |
     * -------------------------------------------------------------------------------------
     *
     */
    @Test
    public void testOverlapI() throws QueryParseException {
        String[][] expected = {
                {"<test:Sensor2>", "\"10\"^^<xsd:int>"},
                {"<test:Sensor1>", "\"9\"^^<xsd:int>"}
        };
        TupleStore tupleStoreInterval = fcInterval.tupleStore;
        TupleStore tupleStoreNoIdex = fcNoIndex.tupleStore;

        
        Query queryInterval = new Query(tupleStoreInterval);
        Query queryNoIndex = new Query(tupleStoreNoIdex);

            String input = "SELECT ?s ?o WHERE ?s <test:hasValue> ?o Oi \"800\"^^<xsd:long>  \"1300\"^^<xsd:long> ";
            BindingTable btInterval = queryInterval.query(input);
            BindingTable btNoIndex = queryNoIndex.query(input);

            checkResult(fcInterval, btInterval, expected, btInterval.getVars());//"?s", "?o", "?rel1", "?rel2");
            checkResult(fcNoIndex, btNoIndex, expected, btNoIndex.getVars());//"?s", "?o", "?rel1", "?rel2");


    }

    @AfterAll
    public static void finish() {
        fcInterval.shutdownNoExit();
    }

}
