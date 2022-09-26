package de.dfki.lt.hfc.indexParsing;

import static org.junit.Assert.assertEquals;

import java.util.Set;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import de.dfki.lt.hfc.Config;
import de.dfki.lt.hfc.TestHfc;
import de.dfki.lt.hfc.TestingUtils;
import de.dfki.lt.hfc.types.Xsd3DPoint;


/**
 * Created by chwi02 on 16.03.17.
 */
public class PointValuesOne {

    static TestHfc fc;


    private static String getResource(String name) {
        return TestingUtils.getTestResource("Index_Parsing", name);
    }

    @BeforeClass
    public static void init() throws Exception {

        fc =  new TestHfc(Config.getInstance(getResource("IndexParsing.yml")));
    }

    @Test
    public void testIndexNoClosure(){
        assertEquals(11, fc.getIndex().size());
        Xsd3DPoint key = new Xsd3DPoint(0,0,1);
        Set<int[]> values = fc.getIndex().lookup(key);
        assertEquals(1,values.size());

        key = new Xsd3DPoint(0,0,2);
        values = fc.getIndex().lookup(key);
        assertEquals(1,values.size());
        fc.computeClosure();
        assertEquals(11,fc.getIndex().size());
        key = new Xsd3DPoint(0,0,1);
        values = fc.getIndex().lookup(key);
        assertEquals(7,values.size());

        key = new Xsd3DPoint(0,0,2);
        values = fc.getIndex().lookup(key);
        assertEquals(7,values.size());

    }

    @AfterClass
    public static void finish() {
        fc.shutdownNoExit();
    }
}
