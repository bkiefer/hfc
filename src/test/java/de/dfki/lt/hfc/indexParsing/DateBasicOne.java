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
 * This tests ensure that the basic parsing for Transaction time encoded by xsd:date works. Here the Transaction time
 * is stored on the first position of the tuple, e.g. "0000-00-00"^^<xsd:date> <rdf:type> <rdf:type> <rdf:Property> .
 * Created by christian on 26/02/17.
 */
public class DateBasicOne {

    static Hfc fc;

    static String[] testSub1 = new String[]{"<rdf:type>", "<rdf:type>", "<rdf:Property>"};
    static String[] testSub2 = new String[]{"<test:sensor>", "<rdfs:subClassOf>", "<owl:Thing>"};

    private static String getResource(String name) {
        return TestingUtils.getTestResource("Index_Parsing", name);
    }

    @BeforeClass
    public static void init() throws Exception {

        fc =  new Hfc(Config.getInstance(getResource("basic_transaction_date0.yml")));

        // compute deductive closure
        // TODO move this into extra tests -> fc.computeClosure();
    }



    @Test
    public void testIndexClosure(){
        assertEquals(1,fc._tupleStore.indexStore.size());
        XsdDate key = new XsdDate(0,0,0);
        Set<int[]> values = fc._tupleStore.indexStore.lookup(key);
        assertEquals(74,values.size());
        fc.computeClosure();
        assertEquals(1,fc._tupleStore.indexStore.size());
        assertEquals(0, fc._tupleStore.indexStore.secSize());
        key = new XsdDate(0,0,0);
         values = fc._tupleStore.indexStore.lookup(key);
        assertEquals(156,values.size());
//        for ( int[] t : values)
//            System.out.println(Arrays.toString(t));
    }

    @AfterClass
    public static void finish() {
        fc.shutdownNoExit();
    }
}
