package de.dfki.lt.hfc.comparators;

import de.dfki.lt.hfc.types.XsdInt;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * Created by chwi02 on 27.02.17.
 */
public class XsdIntCompTest {
    @Test
    public void testLess(){
        XsdInt c1 = new XsdInt(2);
        XsdInt c2 = new XsdInt(4);
        assertTrue( c1.compareTo(c2) < 0);
        c1 = new XsdInt("\"2\"^^<xsd:int>");
        c2 = new XsdInt("\"4\"^^<xsd:int>");
        assertTrue( c1.compareTo(c2)< 0);

    }

    @Test
    public void testEqual(){
        XsdInt c1 = new XsdInt(2);
        XsdInt c2 = new XsdInt(2);
        assertTrue( c1.compareTo(c2) == 0);
        c1 = new XsdInt("\"2\"^^<xsd:int>");
        c2 = new XsdInt("\"2\"^^<xsd:int>");
        assertTrue( c1.compareTo(c2)== 0);
        assertTrue(c1.compareTo(c1)==0);
    }

    @Test
    public void testGreater(){
        XsdInt c1 = new XsdInt(4);
        XsdInt c2 = new XsdInt(2);
        assertTrue( c1.compareTo(c2) > 0);
        c1 = new XsdInt("\"4\"^^<xsd:int>");
        c2 = new XsdInt("\"2\"^^<xsd:int>");
        assertTrue( c1.compareTo(c2)> 0);
    }
}
