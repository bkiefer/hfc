package de.dfki.lt.hfc.indexParsing;

import de.dfki.lt.hfc.ForwardChainer;
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

    static ForwardChainer fc;

    static String[] testSub1 = new String[]{"<rdf:type>", "<rdf:type>", "<rdf:Property>"};
    static String[] testSub2 = new String[]{"<test:sensor>", "<rdfs:subClassOf>", "<owl:Thing>"};

    private static String getResource(String name) {
        return TestingUtils.getTestResource("Index_Parsing", name);
    }

    @BeforeClass
    public static void init() throws Exception {

        fc =  new ForwardChainer(4,                                                    // #cores
                false,                                                 // verbose
                false,                                                 // RDF Check
                false,                                                // EQ reduction disabled
                6,                                                    // min #args
                6,                                                    // max #args
                100000,                                               // #atoms
                500000,                                               // #tuples
                getResource("transaction_date0_valid_date45.nt"),                // tuple file TODO
                getResource("transaction0_valid45.rdl"),                           // rule file  TODO
                getResource("Transaction.ns"),                           // namespace file TODO
                getResource("transaction_date0_valid_date45.idx")                  // index file TODO
        );

        // compute deductive closure
        // TODO move this into extra tests -> fc.computeClosure();
    }



    @Test
    public void testIndexClosure(){
        assertEquals(1,fc.tupleStore.indexStore.size());
        assertEquals(1, fc.tupleStore.indexStore.secSize());
        XsdDate start = new XsdDate(0,0,1);
        XsdDate end = new XsdDate(0,0,2);
        Set<int[]> values = fc.tupleStore.indexStore.lookup(start,end);
        assertEquals(74,values.size());
        fc.computeClosure();
        assertEquals(1,fc.tupleStore.indexStore.size());
        assertEquals(1, fc.tupleStore.indexStore.secSize());
        values = fc.tupleStore.indexStore.lookup(start,end);
        assertEquals(154,values.size());
    }

    @AfterClass
    public static void finish() {
        fc.shutdownNoExit();
    }
}
