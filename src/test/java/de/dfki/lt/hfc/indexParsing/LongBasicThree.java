package de.dfki.lt.hfc.indexParsing;

import de.dfki.lt.hfc.ForwardChainer;

import de.dfki.lt.hfc.TestingUtils;
import de.dfki.lt.hfc.types.XsdLong;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;


import java.util.Set;

import static org.junit.Assert.assertEquals;


/**
 * This tests ensure that the basic parsing for Transaction time encoded by <xsd:long> works. Here the Transaction time
 * is stored on the last position of the tuple, e.g. <rdf:type> <rdf:type> <rdf:Property> "0"^^<xsd:long> .
 * Created by christian on 26/02/17.
 */
public class LongBasicThree {

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
                4,                                                    // min #args
                4,                                                    // max #args
                100000,                                               // #atoms
                500000,                                               // #tuples
                getResource("basic_transaction_long3.nt"),                            // tuple file
                getResource("transaction3.rdl"),                           // rule file  TODO
                getResource("Transaction.ns"),                             // namespace file
                getResource("basic_transaction_long3.idx")
        );

        // compute deductive closure
        // TODO move this into extra tests -> fc.computeClosure();
    }



    @Test
    public void testIndexClosure(){
        assertEquals(1,fc.tupleStore.indexStore.size());
        XsdLong key = new XsdLong(0L);
        Set<int[]> values = fc.tupleStore.indexStore.lookup(key);
        assertEquals(74,values.size());
        fc.computeClosure();
        assertEquals(1,fc.tupleStore.indexStore.size());
         key = new XsdLong(0L);
         values = fc.tupleStore.indexStore.lookup(key);
        assertEquals(154,values.size());
        //for ( int[] t : values)
        //    System.out.println(Arrays.toString(t));
    }


    @AfterClass
    public static void finish() {
        fc.shutdownNoExit();
    }
}
