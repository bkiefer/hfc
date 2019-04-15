package de.dfki.lt.hfc.indexParsing;

import de.dfki.lt.hfc.Config;
import de.dfki.lt.hfc.Hfc;

import de.dfki.lt.hfc.TestingUtils;
import de.dfki.lt.hfc.types.XsdDate;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;


import java.util.Set;

import static org.junit.Assert.assertEquals;


/**
 * This tests ensure that the value parsing for more "complex" Transaction times encoded by <xsd:date> works. Here the Transaction time
 * is stored on the first position of the tuple, e.g. "0200-00-01"^^<xsd:date> <test:Sensor1> <test:hasValue> "142"^^<xsd:long> .
 * Created by christian on 26/02/17.
 */
public class DateComplexValuesOne {

    static Hfc fc;
    static String[] testSub1 = new String[]{"<test:Sensor1>", "<test:hasValue>", "\"142\"^^<xsd:long>"};
    static String[] testSub2 = new String[]{"<test:Sensor2>", "<test:hasValue>", "\"242\"^^<xsd:long>"};

    private static String getResource(String name) {
        return TestingUtils.getTestResource("Index_Parsing", name);
    }

    @BeforeClass
    public static void init() throws Exception {

        fc =  new Hfc(Config.getInstance(getResource("test_transaction_date0_complex.yml")));

    }



    @Test
    public void testIndexClosure(){
        assertEquals(10,fc._tupleStore.indexStore.size());
        XsdDate key = new XsdDate(42,1,4);
        Set<int[]> values = fc._tupleStore.indexStore.lookup(key);
        assertEquals(1,values.size());
        key = new XsdDate(42,1,5);
        values = fc._tupleStore.indexStore.lookup(key);
        assertEquals(1,values.size());
        fc.computeClosure();
        assertEquals(10,fc._tupleStore.indexStore.size());
         key = new XsdDate(42,1,4);
         values = fc._tupleStore.indexStore.lookup(key);
        assertEquals(7,values.size());

        key = new XsdDate(42,1,5);
        values = fc._tupleStore.indexStore.lookup(key);
        assertEquals(7,values.size());

    }


    @AfterClass
    public static void finish() {
        fc.shutdownNoExit();
    }

}
