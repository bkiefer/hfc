package de.dfki.lt.hfc.indexParsing;

import de.dfki.lt.hfc.ForwardChainer;
import de.dfki.lt.hfc.TestUtils;
import de.dfki.lt.hfc.types.XsdDate;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;


/**
 * This tests ensure that the value parsing for more "complex" Transaction times encoded by <xsd:date> works. Here the Transaction time
 * is stored on the first position of the tuple, e.g. "0200-00-01"^^<xsd:date> <test:Sensor1> <test:hasValue> "142"^^<xsd:long> .
 * Created by christian on 26/02/17.
 */
public class DateComplexValuesOne {

    static ForwardChainer fc;
    static String[] testSub1 = new String[]{"<test:Sensor1>", "<test:hasValue>", "\"142\"^^<xsd:long>"};
    static String[] testSub2 = new String[]{"<test:Sensor2>", "<test:hasValue>", "\"242\"^^<xsd:long>"};

    private static String getResource(String name) {
        return TestUtils.getTestResource("Index_Parsing", name);
    }

    @BeforeAll
    public static void init() throws Exception {

        fc =  new ForwardChainer(4,                                                    // #cores
                false,                                                 // verbose
                false,                                                 // RDF Check
                false,                                                // EQ reduction disabled
                4,                                                    // min #args
                4,                                                    // max #args
                100000,                                               // #atoms
                500000,                                               // #tuples
                getResource("test_transaction_date0_complex.nt"),                            // tuple file
                getResource("transaction0.rdl"),                           // rule file  TODO
                getResource("Transaction.ns"),                             // namespace file
                getResource("basic_transaction_date0.idx")
        );
    }



    @Test
    public void testIndexClosure(){
        assertEquals(10,fc.tupleStore.indexStore.size());
        XsdDate key = new XsdDate(42,1,4);
        Set<int[]> values = fc.tupleStore.indexStore.lookup(key);
        assertEquals(1,values.size());
        key = new XsdDate(42,1,5);
        values = fc.tupleStore.indexStore.lookup(key);
        assertEquals(1,values.size());
        fc.computeClosure();
        assertEquals(10,fc.tupleStore.indexStore.size());
         key = new XsdDate(42,1,4);
         values = fc.tupleStore.indexStore.lookup(key);
        assertEquals(7,values.size());

        key = new XsdDate(42,1,5);
        values = fc.tupleStore.indexStore.lookup(key);
        assertEquals(7,values.size());

    }


    @AfterAll
    public static void finish() {
        fc.shutdownNoExit();
    }

}