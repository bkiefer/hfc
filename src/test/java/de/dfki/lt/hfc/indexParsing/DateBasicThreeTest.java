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
 * This tests ensure that the basic parsing for Transaction time encoded by xsd:date works. Here the Transaction time
 * is stored on the first position of the tuple, e.g.  <rdf:type> <rdf:type> <rdf:Property> "0000-00-00"^^<xsd:date> .
 * Created by christian on 26/02/17.
 */
public class DateBasicThreeTest {

    static TestHfc fc;

    static String[] testSub1 = new String[]{"<rdf:type>", "<rdf:type>", "<rdf:Property>"};
    static String[] testSub2 = new String[]{"<test:sensor>", "<rdfs:subClassOf>", "<owl:Thing>"};

    private static String getResource(String name) {
        return TestingUtils.getTestResource("Index_Parsing", name);
    }

    @BeforeClass
    public static void init() throws Exception {

        fc =  new TestHfc(Config.getInstance(getResource("basic_transaction_date3.yml")));

        // compute deductive closure
        // TODO move this into extra tests -> fc.computeClosure();
    }



    @Test
    public void testIndexClosure(){
        assertEquals(1,fc.getIndex().size());
        XsdDate key = new XsdDate(0,0,0);
        Set<int[]> values = fc.getIndex().lookup(key);
        assertEquals(74,values.size());
        fc.computeClosure();
        assertEquals(1,fc.getIndex().size());
         key = new XsdDate(0,0,0);
         values = fc.getIndex().lookup(key);
        assertEquals(156,values.size());
    }

    @AfterClass
    public static void finish() {
        fc.shutdownNoExit();
    }
}
