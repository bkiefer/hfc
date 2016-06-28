package de.dfki.lt.hfc.aggregates;

import static de.dfki.lt.hfc.TestUtils.*;
import static org.junit.Assert.assertEquals;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import de.dfki.lt.hfc.*;

/**
 * this aggregational operator LGetLatestValues only works for the time-stamped
 * triple case and is supposed to be given a table of the following form:
 *   property value arg1 ... argN timestamp
 * the operator returns information from the latest time-stamped tuples
 *   ?_ property value timestamp
 * note that if property is _not_ functional, there might be several such tuples
 * with the _same_ time stamp, but differing in their value, e.g.,
 *   ?_ prop val1 time
 *   ?_ prop val2 time
 * in case the associated timestamp should also be returned, duplicate the time
 * argument and make it one of the arguments from arg1, ..., argN
 *
 * @author (C) Hans-Ulrich Krieger
 * @since JDK 1.5
 * @version Wed Mar 23 16:49:29 CET 2016
 */
public class TestLGetLatestValues {
  static ForwardChainer fc;

  public static String getResource(String name) {
    return TestUtils.getTestResource("LGetLatestValues", name);
  }


  @BeforeClass
  public static void init() throws Exception {

    // forward chainer actually not needed -- tuple store and query object would suffice !
    fc =	new ForwardChainer(4,                                // #cores
        false,                                                // verbose
        true,                                                 // RDF Check
        false,                                                // EQ reduction disabled
        3,                                                    // min #args
        4,                                                    // max #args
        10000,                                               // #atoms
        50000,                                               // #tuples
        getResource("default.nt"),                            // tuple file
        getResource("default.rdl"),                           // rule file
        getResource("default.ns")                             // namespace file
        );

    // manually-constructed child test data from PAL
    //   <pal:lisa> <rdf:type> <dom:Child> "5544"^^<xsd:long> .
    //   <pal:lisa> <dom:hasLabValue> <pal:labval22> "5544"^^<xsd:long> .
    //   <pal:labval22> <dom:height> "133"^^<xsd:cm> "5544"^^<xsd:long> .
    //   <pal:labval22> <dom:weight> "28.2"^^<xsd:kg> "5544"^^<xsd:long> .
    //   <pal:labval22> <dom:bmi> "15.9"^^<xsd:kg_m2> "5544"^^<xsd:long> .
    //   <pal:labval22> <dom:hr> "75.0"^^<xsd:min-1> "5544"^^<xsd:long> .
    //   <pal:labval22> <dom:bsl> "162.0"^^<xsd:mg_dL> "5544"^^<xsd:long> .
    //   <pal:labval22> <dom:bsl> "9.0"^^<xsd:mmol_L> "5544"^^<xsd:long> .
    //   <pal:lisa> <dom:hasLabValue> <pal:labval33> "5577"^^<xsd:long> .
    //   <pal:labval33> <dom:weight> "28.6"^^<xsd:kg> "5577"^^<xsd:long> .
    //   <pal:labval33> <dom:bsl> "9.2"^^<xsd:mmol_L> "5577"^^<xsd:long> .
    //   <pal:labval33> <dom:bsl> "165.6"^^<xsd:mg_dL> "5577"^^<xsd:long> .
    fc.uploadTuples(getResource("test.child.labvalues.nt"));
    }

  @AfterClass
  public static void finish() {
    fc.shutdownNoExit();
  }

  @Test
  public void test() throws QueryParseException {

    Query q = new Query(fc.tupleStore);

    String[][] expected = {
        { "<dom:bmi>", "\"15.9\"^^<xsd:kg_m2>", "<pal:lisa>", "\"5544\"^^<xsd:long>" },
        { "<dom:bsl>", "\"165.6\"^^<xsd:mg_dL>", "<pal:lisa>", "\"5577\"^^<xsd:long>" },
        { "<dom:bsl>", "\"9.2\"^^<xsd:mmol_L>", "<pal:lisa>", "\"5577\"^^<xsd:long>" },
        { "<dom:height>", "\"133\"^^<xsd:cm>", "<pal:lisa>", "\"5544\"^^<xsd:long>" },
        { "<dom:hr>", "\"75.0\"^^<xsd:min-1>", "<pal:lisa>", "\"5544\"^^<xsd:long>" },
        { "<dom:weight>", "\"28.6\"^^<xsd:kg>", "<pal:lisa>", "\"5577\"^^<xsd:long>" }
    };

    //example query from the PAL domain:
    //   SELECT ?child ?prob ?val ?t
    //   WHERE ?child <rdf:type> <dom:Child> ?ts1 &
    //         ?child <dom:hasLabValue> ?labvalue ?ts2 &
    //         ?labvalue ?prob ?val ?
    //   AGGREGATE ?measurement ?result ?patient ?time = LGetLatestValues ?prop ?val ?child ?t ?t

    BindingTable bt = q.query("SELECT ?child ?prop ?val ?t "
        + "WHERE ?child <rdf:type> <dom:Child> ?t1 "
        + "& ?child <dom:hasLabValue> ?lv ?t2 "
        + "& ?lv ?prop ?val ?t "
        + "AGGREGATE ?measurement ?result ?patient ?time = LGetLatestValues ?prop ?val ?child ?t ?t");

    printExpected(bt, fc.tupleStore);

    //   =============================================================================================
    //   | ?measurement         | ?result              | ?patient             | ?time                |
    //   =============================================================================================
    //   | <dom:bmi>            | "15.9"^^<xsd:kg_m2>  | <pal:lisa>           | "5544"^^<xsd:long>   |
    //   | <dom:bsl>            | "165.6"^^<xsd:mg_dL> | <pal:lisa>           | "5577"^^<xsd:long>   |
    //   | <dom:bsl>            | "9.2"^^<xsd:mmol_L>  | <pal:lisa>           | "5577"^^<xsd:long>   |
    //   | <dom:height>         | "133"^^<xsd:cm>      | <pal:lisa>           | "5544"^^<xsd:long>   |
    //   | <dom:hr>             | "75.0"^^<xsd:min-1>  | <pal:lisa>           | "5544"^^<xsd:long>   |
    //   | <dom:weight>         | "28.6"^^<xsd:kg>     | <pal:lisa>           | "5577"^^<xsd:long>   |
    //   ---------------------------------------------------------------------------------------------

    checkResult(fc, bt, expected, "?measurement", "?result", "?patient", "?time");
  }

}
