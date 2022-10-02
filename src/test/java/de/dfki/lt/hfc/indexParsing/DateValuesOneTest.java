package de.dfki.lt.hfc.indexParsing;

import static org.junit.Assert.assertEquals;

import java.util.Set;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import de.dfki.lt.hfc.Config;
import de.dfki.lt.hfc.TestHfc;
import de.dfki.lt.hfc.TestingUtils;
import de.dfki.lt.hfc.types.XsdDate;


/**
 * This tests ensure that the value parsing for Transaction time encoded by <xsd:date> works. Here the Transaction time
 * is stored on the first position of the tuple, e.g. "0000-00-00"^^<xsd:date> <test:Sensor1> <test:hasValue> "142"^^<xsd:long> .
 * Created by christian on 26/02/17.
 */
public class DateValuesOneTest {

    static TestHfc fc;
    static String[] testSub1 = new String[]{"<test:Sensor1>", "<test:hasValue>", "\"142\"^^<xsd:long>"};
    static String[] testSub2 = new String[]{"<test:Sensor2>", "<test:hasValue>", "\"242\"^^<xsd:long>"};

    private static String getResource(String name) {
        return TestingUtils.getTestResource("Index_Parsing", name);
    }

    @BeforeClass
    public static void init() throws Exception {

        fc =  new TestHfc(Config.getInstance(getResource("test_transaction_date0.yml")));
    }



    @Test
    public void testIndexClosure(){
        assertEquals(11,fc.getIndex().size());
        XsdDate key = new XsdDate(0,0,1);
        Set<int[]> values = fc.getIndex().lookup(key);
        assertEquals(1,values.size());
        key = new XsdDate(0,0,2);
        values = fc.getIndex().lookup(key);
        assertEquals(1,values.size());
        fc.computeClosure();
        assertEquals(11,fc.getIndex().size());
         key = new XsdDate(0,0,1);
         values = fc.getIndex().lookup(key);
        assertEquals(7,values.size());
        key = new XsdDate(0,0,2);
        values = fc.getIndex().lookup(key);
        assertEquals(7,values.size());
    }


    @AfterClass
    public static void finish() {
        fc.shutdownNoExit();
    }

}
