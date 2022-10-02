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
 * Created by chwi02 on 16.05.17.
 */
public class ActiveSecIndexTest {

    static TestHfc fc;

    static String[] testSub1 = new String[]{"<rdf:type>", "<rdf:type>", "<rdf:Property>"};
    static String[] testSub2 = new String[]{"<test:sensor>", "<rdfs:subClassOf>", "<owl:Thing>"};

    private static String getResource(String name) {
        return TestingUtils.getTestResource("Index_Parsing", name);
    }

    @BeforeClass
    public static void init() throws Exception {

        fc =  new TestHfc(Config.getInstance(getResource("transaction_date0_valid_date45.yml")));

        // compute deductive closure
        // TODO move this into extra tests -> fc.computeClosure();
    }



    @Test
    public void testIndexClosure(){
        assertEquals(1,fc.getIndex().size());
        assertEquals(1, fc.getIndex().secSize());
        XsdDate start = new XsdDate(0,0,1);
        XsdDate end = new XsdDate(0,0,2);
        Set<int[]> values = fc.getIndex().lookup(start,end);
        assertEquals(74,values.size());
        fc.computeClosure();
        assertEquals(1,fc.getIndex().size());
        assertEquals(1, fc.getIndex().secSize());
        values = fc.getIndex().lookup(start,end);
        assertEquals(156,values.size());
    }

    @AfterClass
    public static void finish() {
        fc.shutdownNoExit();
    }
}
