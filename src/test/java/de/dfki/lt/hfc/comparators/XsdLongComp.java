package de.dfki.lt.hfc.comparators;

import de.dfki.lt.hfc.types.XsdLong;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;


/**
 * Created by chwi02 on 27.02.17.
 */
public class XsdLongComp {

    @Test
    public void testLess(){
        XsdLong c1 = new XsdLong(2);
        XsdLong c2 = new XsdLong(4);
        assertTrue( c1.compareTo(c2) < 0);
        c1 = new XsdLong("\"2\"^^<xsd:long>");
        c2 = new XsdLong("\"4\"^^<xsd:long>");
        assertTrue( c1.compareTo(c2)< 0);
    }

    @Test
    public void testEqual(){
        XsdLong c1 = new XsdLong(2);
        XsdLong c2 = new XsdLong(2);
        assertTrue( c1.compareTo(c2)== 0);
        c1 = new XsdLong("\"2\"^^<xsd:long>");
        c2 = new XsdLong("\"2\"^^<xsd:long>");
        assertTrue(c1.compareTo(c2) == 0);
    }

    @Test
    public void testGreater(){
        XsdLong c1 = new XsdLong(2);
        XsdLong c2 = new XsdLong(4);
        assertTrue( c2.compareTo(c1)>0);
        c1 = new XsdLong("\"2\"^^<xsd:long>");
        c2 = new XsdLong("\"4\"^^<xsd:long>");
        assertTrue( c2.compareTo(c1)>0);
    }

}
