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
 * Created by chwi02 on 16.05.17.
 */
public class ActiveSecIndex {

    static Hfc fc;

    static String[] testSub1 = new String[]{"<rdf:type>", "<rdf:type>", "<rdf:Property>"};
    static String[] testSub2 = new String[]{"<test:sensor>", "<rdfs:subClassOf>", "<owl:Thing>"};

    private static String getResource(String name) {
        return TestingUtils.getTestResource("Index_Parsing", name);
    }

    @BeforeClass
    public static void init() throws Exception {

        fc =  new Hfc(Config.getInstance(getResource("transaction_date0_valid_date45.yml")));

        // compute deductive closure
        // TODO move this into extra tests -> fc.computeClosure();
    }



    @Test
    public void testIndexClosure(){
        assertEquals(1,fc._tupleStore.indexStore.size());
        assertEquals(1, fc._tupleStore.indexStore.secSize());
        XsdDate start = new XsdDate(0,0,1);
        XsdDate end = new XsdDate(0,0,2);
        Set<int[]> values = fc._tupleStore.indexStore.lookup(start,end);
        assertEquals(74,values.size());
        fc.computeClosure();
        assertEquals(1,fc._tupleStore.indexStore.size());
        assertEquals(1, fc._tupleStore.indexStore.secSize());
        values = fc._tupleStore.indexStore.lookup(start,end);
        assertEquals(156,values.size());
    }

    @AfterClass
    public static void finish() {
        fc.shutdownNoExit();
    }
}
