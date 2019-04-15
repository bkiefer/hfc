package de.dfki.lt.hfc.aggregates;

import de.dfki.lt.hfc.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import static de.dfki.lt.hfc.TestingUtils.checkResult;
import static org.junit.Assert.assertTrue;


/**
 * This is a collection of test for the newly introduced aggregtates
 * - {@link GetEventsLastNDays},
 * - {@link GetEventsLastTwoWeeks},
 * - {@link GetEventsLastWeeks},
 * - {@link GetEventsThisWeek},
 * - {@link GetEventsToday},
 * - {@link GetEventsYesterday}
 *
 * @author (C) Christian Willms
 * @version Tue Feb  13 16:41:55 CET 2018
 * @since JDK 1.8
 */
public class TestGetEvents {

    static final long DAY = 86400000;
    Hfc fc;


    public static String getResource(String name) {
        return TestingUtils.getTestResource("LGetLatestValues", name);
    }

    @Before
    public void init() throws Exception {

        fc = new Hfc(Config.getDefaultConfig() );
        fc._tupleStore.namespace.putForm("pal", "http://www.dfki.de/lt/onto/pal.owl#", true );
        fc._tupleStore.namespace.putForm("dom", "http://www.dfki.de/lt/onto/dom.owl#", true);
        fc.uploadTuples(getResource("test.child.labvalues.nt"));
    }


    @After
    public void cleanup() {
        fc.shutdownNoExit();
    }


    /**
     * This method tests the basic logic used for the GetEventsLastNDays aggregate
     */
    @Test
    public void testBasicLogic() {
        // create a set of tuple where the transaction times depend on the current time and load those into the ontology
        long currentTime = System.currentTimeMillis();
        long midnight = currentTime - (currentTime % DAY);
        // check whether this is really 00:00
        LocalDateTime midnightDate = Instant.ofEpochMilli(midnight).atZone(ZoneId.of("UTC")).toLocalDateTime();
        assertTrue(midnightDate.toString().endsWith("00:00"));
        // check whether the computation of sundays work
        long sundayMidnight = currentTime - (currentTime % (7 * DAY)) - 3 * DAY;
        LocalDateTime lastSundayDateTime = Instant.ofEpochMilli(sundayMidnight).atZone(ZoneId.of("UTC")).toLocalDateTime();
        LocalDate lastSundayDate = Instant.ofEpochMilli(sundayMidnight).atZone(ZoneId.of("UTC")).toLocalDate();
        assertTrue(lastSundayDate.getDayOfWeek().toString().endsWith("MONDAY"));
        assertTrue(lastSundayDateTime.toString().endsWith("00:00"));
    }


    /**
     * This method uses randomly generated timestamps to test whether the GetEventsLastNDays aggregate works correctly.
     * It adds 20 tuples with random timestamps to the ontology.
     * All timestamps lay in the last 4 days. Therefore we can test whether the aggregate correctly handles out of range
     * values, i.e. values that are older than 3 days.
     *
     * @throws QueryParseException
     */
    @Test
    public void testLast3Days() throws QueryParseException {
        Query q = new Query(fc._tupleStore);

        long currentTime = System.currentTimeMillis();
        long midnight = currentTime - (currentTime % DAY);
        long start = midnight - 3 * DAY;
        // populate ontology and expected values
        Set<String[]> validTuples = new HashSet<String[]>();
        populateTestSetup(validTuples, currentTime, start, midnight, 1);
        String[][] expected = validTuples.toArray(new String[validTuples.size()][]);

        BindingTable bt = q.query("SELECT ?child ?prop ?val ?t "
                + "WHERE ?child <rdf:type> <dom:Child> ?t1 "
                + "& ?child <dom:hasLabValue> ?lv ?t2 "
                + "& ?lv ?prop ?val ?t "
                + "AGGREGATE ?measurement ?result ?patient ?time = GetEventsLastNDays ?prop ?val ?child ?t ?t \"3\"^^<xsd:int>");

        checkResult(fc, bt, expected, "?measurement", "?result", "?patient", "?time");
    }


    /**
     * This method uses randomly generated timestamps to test whether the GetEventsToday aggregate works correctly.
     * It adds 20 tuples with random timestamps for the transaction time to the ontology.
     *
     * @throws QueryParseException
     */
    @Test
    public void testGetToday() throws QueryParseException {
        Query q = new Query(fc._tupleStore);

        long currentTime = System.currentTimeMillis();
        long start = currentTime - (currentTime % DAY);

        // populate ontology and expected values
        Set<String[]> validTuples = new HashSet<String[]>();
        populateTestSetup(validTuples, currentTime, start, currentTime, 1);
        String[][] expected = validTuples.toArray(new String[validTuples.size()][]);
        BindingTable bt = q.query("SELECT ?child ?prop ?val ?t "
                + "WHERE ?child <rdf:type> <dom:Child> ?t1 "
                + "& ?child <dom:hasLabValue> ?lv ?t2 "
                + "& ?lv ?prop ?val ?t "
                + "AGGREGATE ?measurement ?result ?patient ?time = GetEventsToday ?prop ?val ?child ?t ?t ");


        checkResult(fc, bt, expected, "?measurement", "?result", "?patient", "?time");
    }


    /**
     * This method uses random generated timestamps to test whether the GetEventsYesterday aggregate works correctly.
     * It adds 20 tuples with random timestamps for the transaction time to the ontology.
     *
     * @throws QueryParseException
     */
    @Test
    public void testGetYesterday() throws QueryParseException {
        Query q = new Query(fc._tupleStore);

        long currentTime = System.currentTimeMillis();
        long midnight = currentTime - (currentTime % DAY);
        long start = midnight - DAY;
        // populate ontology and expected values
        Set<String[]> validTuples = new HashSet<String[]>();
        populateTestSetup(validTuples, currentTime, start, midnight, 1);
        String[][] expected = validTuples.toArray(new String[validTuples.size()][]);

        BindingTable bt = q.query("SELECT ?child ?prop ?val ?t "
                + "WHERE ?child <rdf:type> <dom:Child> ?t1 "
                + "& ?child <dom:hasLabValue> ?lv ?t2 "
                + "& ?lv ?prop ?val ?t "
                + "AGGREGATE ?measurement ?result ?patient ?time = GetEventsYesterday ?prop ?val ?child ?t ?t ");

        checkResult(fc, bt, expected, "?measurement", "?result", "?patient", "?time");
    }

    /**
     * This method uses random generated timestamps to test whether the GetEventsLastTwoWeeks aggregate works correctly.
     * It adds 20 tuples with random timestamps for the transaction time to the ontology.
     *
     * @throws QueryParseException
     */
    @Test
    public void testGetLastTwoWeeksEvents() throws QueryParseException {
        Query q = new Query(fc._tupleStore);

        long currentTime = System.currentTimeMillis();
        long sunday = currentTime - (currentTime % (DAY * 7)) - (3 * DAY);
        long start = sunday - (14 * DAY);
        // populate ontology and expected values
        Set<String[]> validTuples = new HashSet<String[]>();
        populateTestSetup(validTuples, currentTime, start, sunday, 1);
        String[][] expected = validTuples.toArray(new String[validTuples.size()][]);

        BindingTable bt = q.query("SELECT ?child ?prop ?val ?t "
                + "WHERE ?child <rdf:type> <dom:Child> ?t1 "
                + "& ?child <dom:hasLabValue> ?lv ?t2 "
                + "& ?lv ?prop ?val ?t "
                + "AGGREGATE ?measurement ?result ?patient ?time = GetEventsLastTwoWeeks ?prop ?val ?child ?t ?t ");

        checkResult(fc, bt, expected, "?measurement", "?result", "?patient", "?time");
    }

    /**
     * This method uses random generated timestamps to test whether the GetEventsLastWeek aggregate works correctly.
     * It adds 20 tuples with random timestamps for the transaction time to the ontology.
     *
     * @throws QueryParseException
     */
    @Test
    public void testGetLastWeeksEvents() throws QueryParseException {
        Query q = new Query(fc._tupleStore);

        long currentTime = System.currentTimeMillis();
        long sunday = currentTime - (currentTime % (DAY * 7)) - (3 * DAY);
        long start = sunday - (7 * DAY);
        // populate ontology and expected values
        Set<String[]> validTuples = new HashSet<String[]>();
        populateTestSetup(validTuples, currentTime, start, sunday, 1);
        String[][] expected = validTuples.toArray(new String[validTuples.size()][]);

        BindingTable bt = q.query("SELECT ?child ?prop ?val ?t "
                + "WHERE ?child <rdf:type> <dom:Child> ?t1 "
                + "& ?child <dom:hasLabValue> ?lv ?t2 "
                + "& ?lv ?prop ?val ?t "
                + "AGGREGATE ?measurement ?result ?patient ?time = GetEventsLastWeeks ?prop ?val ?child ?t ?t ");

        checkResult(fc, bt, expected, "?measurement", "?result", "?patient", "?time");
    }

    /**
     * This method uses random generated timestamps to test whether the GetEventsThisWeek aggregate works correctly.
     * It adds 20 tuples with random timestamps for the transaction time to the ontology.
     *
     * @throws QueryParseException
     */
    @Test
    public void testGetThisWeeksEvents() throws QueryParseException {
        Query q = fc.getQuery();

        long currentTime = System.currentTimeMillis();
        long sunday = currentTime - (currentTime % (DAY * 7)) - (3 * DAY);
        // populate ontology and expected values
        Set<String[]> validTuples = new HashSet<String[]>();
        populateTestSetup(validTuples, currentTime, sunday, currentTime, 1);
        String[][] expected = validTuples.toArray(new String[validTuples.size()][]);

        BindingTable bt = q.query("SELECT ?child ?prop ?val ?t "
                + "WHERE ?child <rdf:type> <dom:Child> ?t1 "
                + "& ?child <dom:hasLabValue> ?lv ?t2 "
                + "& ?lv ?prop ?val ?t "
                + "AGGREGATE ?measurement ?result ?patient ?time = GetEventsThisWeek ?prop ?val ?child ?t ?t ");

        checkResult(fc, bt, expected, "?measurement", "?result", "?patient", "?time");
    }


    private void printValidTuples(Set<String[]> validTuples) {
        System.out.println(" There are " + validTuples.size() + " ValidTuples: ");
        for (String[] tuple : validTuples) {
            System.out.println(Arrays.toString(tuple));
        }
    }

    private void populateTestSetup(Set<String[]> validTuples, long currentTime, long start, long end, int offset) {
        for (int i = 0; i < 20; i++) {
            long randomNum = ThreadLocalRandom.current().nextLong(start - (offset * DAY), currentTime + 1L);
            String[] tuple = new String[]{"<pal:labval22>", "<dom:weight>", "\"28.2\"^^<xsd:kg>", "\"" + randomNum + "\"^^<xsd:long>"};
            fc.addTuple(tuple);
            if (randomNum >= start && randomNum <= end)
                validTuples.add(new String[]{"<dom:weight>", "\"28.2\"^^<xsd:kg>", "<pal:lisa>", "\"" + randomNum + "\"^^<xsd:long>"});
        }
    }


}
