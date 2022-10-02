package de.dfki.lt.hfc.comparators;

import de.dfki.lt.hfc.types.XsdFloat;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * Created by chwi02 on 27.02.17.
 */
public class XsdFloatCompTest {
    @Test
    public void testLess(){
        XsdFloat c1 = new XsdFloat(2);
        XsdFloat c2 = new XsdFloat(4);
        assertTrue( c1.compareTo(c2) < 0);
        c1 = new XsdFloat("\"2\"^^<xsd:float>");
        c2 = new XsdFloat("\"4\"^^<xsd:float>");
        assertTrue( c1.compareTo(c2)< 0);

    }

    @Test
    public void testEqual(){
        XsdFloat c1 = new XsdFloat(2);
        XsdFloat c2 = new XsdFloat(2);
        assertTrue( c1.compareTo(c2) == 0);
        c1 = new XsdFloat("\"2\"^^<xsd:float>");
        c2 = new XsdFloat("\"2\"^^<xsd:float>");
        assertTrue( c1.compareTo(c2)== 0);
        assertTrue(c1.compareTo(c1)==0);
    }

    @Test
    public void testGreater(){
        XsdFloat c1 = new XsdFloat(4);
        XsdFloat c2 = new XsdFloat(2);
        assertTrue( c1.compareTo(c2) > 0);
        c1 = new XsdFloat("\"4\"^^<xsd:float>");
        c2 = new XsdFloat("\"2\"^^<xsd:float>");
        assertTrue( c1.compareTo(c2)> 0);
    }
}
