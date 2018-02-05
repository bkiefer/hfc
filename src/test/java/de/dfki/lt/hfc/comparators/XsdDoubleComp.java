package de.dfki.lt.hfc.comparators;

import de.dfki.lt.hfc.types.XsdDouble;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;


/**
 * Created by chwi02 on 27.02.17.
 */
public class XsdDoubleComp {

    @Test
    public void testLess(){
        XsdDouble c1 = new XsdDouble(2);
        XsdDouble c2 = new XsdDouble(4);
        assertTrue( c1.compareTo(c2) < 0);
        c1 = new XsdDouble("\"2\"^^<xsd:double>");
        c2 = new XsdDouble("\"4\"^^<xsd:double>");
        assertTrue( c1.compareTo(c2)< 0);

    }

    @Test
    public void testEqual(){
        XsdDouble c1 = new XsdDouble(2);
        XsdDouble c2 = new XsdDouble(2);
        assertTrue( c1.compareTo(c2) == 0);
        c1 = new XsdDouble("\"2\"^^<xsd:double>");
        c2 = new XsdDouble("\"2\"^^<xsd:double>");
        assertTrue( c1.compareTo(c2)== 0);
        assertTrue(c1.compareTo(c1)==0);
    }

    @Test
    public void testGreater(){
        XsdDouble c1 = new XsdDouble(4);
        XsdDouble c2 = new XsdDouble(2);
        assertTrue( c1.compareTo(c2) > 0);
        c1 = new XsdDouble("\"4\"^^<xsd:double>");
        c2 = new XsdDouble("\"2\"^^<xsd:double>");
        assertTrue( c1.compareTo(c2)> 0);
    }
}
