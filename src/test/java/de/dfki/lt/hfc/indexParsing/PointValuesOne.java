package de.dfki.lt.hfc.indexParsing;

import de.dfki.lt.hfc.ForwardChainer;

import de.dfki.lt.hfc.Utils;
import de.dfki.lt.hfc.types.Xsd3DPoint;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;


import java.util.Set;

import static org.junit.Assert.assertEquals;


/**
 * Created by chwi02 on 16.03.17.
 */
public class PointValuesOne {

    static ForwardChainer fc;


    private static String getResource(String name) {
        return Utils.getTestResource("Index_Parsing", name);
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
                getResource("test_transaction_3Dpoint.nt"),                            // tuple file
                getResource("transaction0.rdl"),                           // rule file  TODO
                getResource("Transaction.ns"),                             // namespace file
                getResource("test_transaction_3Dpoint.idx")
                );
    }

    @Test
    public void testIndexNoClosure(){
        assertEquals(11,fc.tupleStore.indexStore.size());
        Xsd3DPoint key = new Xsd3DPoint(0,0,1);
        Set<int[]> values = fc.tupleStore.indexStore.lookup(key);
        assertEquals(1,values.size());

        key = new Xsd3DPoint(0,0,2);
        values = fc.tupleStore.indexStore.lookup(key);
        assertEquals(1,values.size());
        fc.computeClosure();
        assertEquals(11,fc.tupleStore.indexStore.size());
        key = new Xsd3DPoint(0,0,1);
        values = fc.tupleStore.indexStore.lookup(key);
        assertEquals(7,values.size());

        key = new Xsd3DPoint(0,0,2);
        values = fc.tupleStore.indexStore.lookup(key);
        assertEquals(7,values.size());

    }

    @AfterClass
    public static void finish() {
        fc.shutdownNoExit();
    }
}
