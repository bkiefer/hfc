package de.dfki.lt.hfc.comparators;

import de.dfki.lt.hfc.types.XsdGDay;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * Created by chwi02 on 27.02.17.
 */
public class XsdGDayComp {

    @Test
    public void testLess(){
        XsdGDay c1 = new XsdGDay(2);
        XsdGDay c2 = new XsdGDay(4);
        assertTrue( c1.compareTo(c2) < 0);
        c1 = new XsdGDay("\"---02\"^^<xsd:gDay>");
        c2 = new XsdGDay("\"---04\"^^<xsd:gDay>");
        assertTrue( c1.compareTo(c2)< 0);

    }

    @Test
    public void testEqual(){
        XsdGDay c1 = new XsdGDay(2);
        XsdGDay c2 = new XsdGDay(2);
        assertTrue( c1.compareTo(c2) == 0);
        c1 = new XsdGDay("\"---02\"^^<xsd:gDay>");
        c2 = new XsdGDay("\"---02\"^^<xsd:gDay>");
        assertTrue( c1.compareTo(c2)== 0);
        assertTrue(c1.compareTo(c1)==0);
    }

    @Test
    public void testGreater(){
        XsdGDay c1 = new XsdGDay(4);
        XsdGDay c2 = new XsdGDay(2);
        assertTrue( c1.compareTo(c2) > 0);
        c1 = new XsdGDay("\"---04\"^^<xsd:gDay>");
        c2 = new XsdGDay("\"---02\"^^<xsd:gDay>");
        assertTrue( c1.compareTo(c2)> 0);
    }
    
}
