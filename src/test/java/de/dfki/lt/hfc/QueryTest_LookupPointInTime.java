package de.dfki.lt.hfc;


import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static de.dfki.lt.hfc.TestUtils.checkResult;

/**
 * Created by christian on 29/05/17.
 */
public class QueryTest_LookupPointInTime {

    static ForwardChainer fc;

    private static String getResource(String name) {
        return TestUtils.getTestResource("Query", name);
    }

    @BeforeAll
    public static void init() throws Exception {

        fc = new ForwardChainer(4,                                                    // #cores
                true,                                                 // verbose
                false,                                                 // RDF Check
                false,                                                // EQ reduction disabled
                4,                                                    // min #args
                4,                                                    // max #args
                100000,                                               // #atoms
                500000,                                               // #tuples
                getResource("lookupAtomTest.nt"),                 // tuple file
                getResource("intervalTest.rdl"),                           // rule file  TODO
                getResource("default.ns"),                            // namespace file
                getResource("lookupAtom.idx")
        );

    }

    @AfterAll
    public static void finish() {
        fc.shutdownNoExit();
    }

    @Test
    public void testWhereNoInterval() throws QueryParseException {
        TupleStore tupleStore = fc.tupleStore;
        Query query = new Query(tupleStore);
        String[][] expected = {{"<test:Sensor1>", "\"1\"^^<xsd:int>"}};

        BindingTable bt = query.query("SELECT ?s ?o WHERE \"1\"^^<xsd:long> ?s <test:hasValue> ?o ");
        checkResult(fc, bt, expected, "?s", "?o");

    }

    @Test
    public void testSelectWhere() throws QueryParseException {
        TupleStore tupleStore = fc.tupleStore;
        Query query = new Query(tupleStore);
        String[][] expected = {{"<test:Sensor2>", "\"2\"^^<xsd:int>"},
                {"<test:Sensor1>", "\"2\"^^<xsd:int>"},
                {"<test:Sensor2>", "\"4\"^^<xsd:int>"},
                {"<test:Sensor1>", "\"3\"^^<xsd:int>"}};

        BindingTable bt = query.query("SELECT ?s ?o WHERE [\"1\"^^<xsd:long>, \"5\"^^<xsd:long>] ?s <test:hasValue> ?o ");
        checkResult(fc, bt, expected, "?s", "?o");

    }

    @Test
    public void testSelectDistinctWhere() throws QueryParseException {
        TupleStore tupleStore = fc.tupleStore;
        Query query = new Query(tupleStore);
        String[][] expected = {{"<test:Sensor2>", "\"2\"^^<xsd:int>"},
                {"<test:Sensor1>", "\"2\"^^<xsd:int>"},
                {"<test:Sensor2>", "\"4\"^^<xsd:int>"},
                {"<test:Sensor1>", "\"3\"^^<xsd:int>"}};

        BindingTable bt = query.query("SELECT DISTINCT ?s ?o WHERE [\"1\"^^<xsd:long>, \"5\"^^<xsd:long>] ?s <test:hasValue> ?o");
        System.out.println(bt.toString());
        checkResult(fc, bt, expected, "?s", "?o");

    }

    @Test
    public void testSelectWhereFilter() throws QueryParseException {
        TupleStore tupleStore = fc.tupleStore;
        Query query = new Query(tupleStore);
        String[][] expected = {{"<test:Sensor2>", "\"2\"^^<xsd:int>"},
                {"<test:Sensor2>", "\"4\"^^<xsd:int>"}};

        BindingTable bt = query.query("SELECT DISTINCT ?s ?o WHERE [\"1\"^^<xsd:long>, \"5\"^^<xsd:long>] ?s <test:hasValue> ?o  FILTER ?s != <test:Sensor1>");
        System.out.println(bt.toString());
        checkResult(fc, bt, expected, "?s", "?o");
    }

    @Test
    public void testSelectDistinctWhereFilter() throws QueryParseException {
        TupleStore tupleStore = fc.tupleStore;
        Query query = new Query(tupleStore);
        String[][] expected = {
                {"<test:Sensor1>", "\"3\"^^<xsd:int>"}, {"<test:Sensor2>", "\"4\"^^<xsd:int>"}};

        BindingTable bt = query.query("SELECT DISTINCT ?s ?o WHERE [\"1\"^^<xsd:long>, \"5\"^^<xsd:long>] ?s <test:hasValue> ?o  FILTER IGreater ?o  \"2\"^^<xsd:int>");
        System.out.println(bt.toString());
        checkResult(fc, bt, expected, "?s", "?o");

    }

    @Test
    public void testSelectWhereAggregate() throws QueryParseException {
        TupleStore tupleStore = fc.tupleStore;
        Query query = new Query(tupleStore);
        String[][] expected = {{"\"4\"^^<xsd:int>"}};

        BindingTable bt = query.query("SELECT DISTINCT ?s ?o WHERE [\"1\"^^<xsd:long>, \"5\"^^<xsd:long>] ?s <test:hasValue> ?o AGGREGATE  ?number = Count ?o");
        System.out.println(bt.toString());
        checkResult(fc, bt, expected, "?number");

    }

}
