package de.dfki.lt.hfc.aggregates;

import de.dfki.lt.hfc.*;
import de.dfki.lt.hfc.runnable.Utils;
import de.dfki.lt.hfc.types.XsdLong;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.text.SimpleDateFormat;

import static de.dfki.lt.hfc.TestUtils.checkResult;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * This is a collection of test for the newly introduced aggregtates
 * {@link Sort} and {@link SortR}
 *
 * @author (C) Christian Willms
 * @version Wed Feb  14 09:23:55 CET 2018
 * @since JDK 1.8
 */
public class TestSort {

    static ForwardChainer fc;

    public static String getResource(String name) {
        return Utils.getTestResource("LGetLatestValues", name);
    }

    @BeforeAll
    public static void init() throws Exception {

        fc = new ForwardChainer(4,                                                    // #cores
                false,                                                 // verbose
                false,                                                 // RDF Check
                false,                                                // EQ reduction disabled
                3,                                                    // min #args, the 4th arg is an instance of xsd:long encoding the transaction time
                4,                                                    // max #args
                100000,                                               // #atoms
                500000,                                               // #tuples
                getResource("default.nt"),                            // tuple file
                getResource("default.rdl"),                           // rule file
                getResource("default.ns")                             // namespace file
        );

        fc.uploadTuples(getResource("test.child.labvalues.nt"));
    }


    @AfterAll
    public static void cleanup() {
        fc.shutdownNoExit();
    }


    @Test
    public void testSort() throws QueryParseException, BindingTableIteratorException {
        Query q = new Query(fc.tupleStore);


        String[][] expected = {
                {"<dom:bsl>", "\"162.0\"^^<xsd:mg_dL>", "<pal:lisa>", "\"5544\"^^<xsd:long>"},
                {"<dom:height>", "\"133\"^^<xsd:cm>", "<pal:lisa>", "\"5544\"^^<xsd:long>"},
                {"<dom:weight>", "\"28.2\"^^<xsd:kg>", "<pal:lisa>", "\"5544\"^^<xsd:long>"},
                {"<dom:hr>", "\"75.0\"^^<xsd:min-1>", "<pal:lisa>", "\"5544\"^^<xsd:long>"},
                {"<dom:bmi>", "\"15.9\"^^<xsd:kg_m2>", "<pal:lisa>", "\"5544\"^^<xsd:long>"},
                {"<dom:bsl>", "\"9.0\"^^<xsd:mmol_L>", "<pal:lisa>", "\"5544\"^^<xsd:long>"},
                {"<dom:weight>", "\"28.6\"^^<xsd:kg>", "<pal:lisa>", "\"5577\"^^<xsd:long>"},
                {"<dom:bsl>", "\"9.2\"^^<xsd:mmol_L>", "<pal:lisa>", "\"5577\"^^<xsd:long>"},
                {"<dom:bsl>", "\"165.6\"^^<xsd:mg_dL>", "<pal:lisa>", "\"5577\"^^<xsd:long>"}
        };

        BindingTable bt = q.query("SELECT ?child ?prop ?val ?t "
                + "WHERE ?child <rdf:type> <dom:Child> ?t1 "
                + "& ?child <dom:hasLabValue> ?lv ?t2 "
                + "& ?lv ?prop ?val ?t "
                + "AGGREGATE ?measurement ?result ?patient ?time = Sort ?prop ?val ?child ?t ?t ");
        // check for the correct ordering in the bindingtable
        checkOrdering(bt, false);
        // check whether all expected entries were found
        checkResult(fc, bt, expected, "?measurement", "?result", "?patient", "?time");
    }


    @Test
    public void testSortR() throws QueryParseException, BindingTableIteratorException {
        Query q = new Query(fc.tupleStore);


        String[][] expected = {
                {"<dom:bsl>", "\"9.2\"^^<xsd:mmol_L>", "<pal:lisa>", "\"5577\"^^<xsd:long>"},
                {"<dom:bsl>", "\"165.6\"^^<xsd:mg_dL>", "<pal:lisa>", "\"5577\"^^<xsd:long>"},
                {"<dom:weight>", "\"28.6\"^^<xsd:kg>", "<pal:lisa>", "\"5577\"^^<xsd:long>"},
                {"<dom:bsl>", "\"162.0\"^^<xsd:mg_dL>", "<pal:lisa>", "\"5544\"^^<xsd:long>"},
                {"<dom:bmi>", "\"15.9\"^^<xsd:kg_m2>", "<pal:lisa>", "\"5544\"^^<xsd:long>"},
                {"<dom:hr>", "\"75.0\"^^<xsd:min-1>", "<pal:lisa>", "\"5544\"^^<xsd:long>"},
                {"<dom:height>", "\"133\"^^<xsd:cm>", "<pal:lisa>", "\"5544\"^^<xsd:long>"},
                {"<dom:weight>", "\"28.2\"^^<xsd:kg>", "<pal:lisa>", "\"5544\"^^<xsd:long>"},
                {"<dom:bsl>", "\"9.0\"^^<xsd:mmol_L>", "<pal:lisa>", "\"5544\"^^<xsd:long>"}
        };

        BindingTable bt = q.query("SELECT ?child ?prop ?val ?t "
                + "WHERE ?child <rdf:type> <dom:Child> ?t1 "
                + "& ?child <dom:hasLabValue> ?lv ?t2 "
                + "& ?lv ?prop ?val ?t "
                + "AGGREGATE ?measurement ?result ?patient ?time = SortR ?prop ?val ?child ?t ?t ");

        // check for the correct ordering in the bindingtable
        checkOrdering(bt, true);
        // check whether all expected entries were found
        checkResult(fc, bt, expected, "?measurement", "?result", "?patient", "?time");
    }

    /**
     * Checks whether the results are sorted correctly
     *
     * @param bt       the bindingtable to be checked
     * @param ordering {@code true} for descending ordering, {@code false} for ascending ordering
     * @throws BindingTableIteratorException
     */
    private void checkOrdering(BindingTable bt, boolean ordering) throws BindingTableIteratorException {
        BindingTable.BindingTableIterator it = bt.iterator("?time");
        long lastValue = ((XsdLong) it.nextAsHfcType()[0]).value;
        while (it.hasNext()) {
            long newValue = ((XsdLong) it.nextAsHfcType()[0]).value;
            if (ordering)
                assertTrue(newValue <= lastValue);
            else
                assertTrue(newValue >= lastValue);
            lastValue = newValue;

        }
    }

}
