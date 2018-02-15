package de.dfki.lt.hfc.comparators;

import de.dfki.lt.hfc.types.XsdGMonthDay;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Created by chwi02 on 27.02.17.
 */
public class XsdGMonthDayComp {
    @Test
    public void testLess(){
        XsdGMonthDay c1 = new XsdGMonthDay(2,0);
        XsdGMonthDay c2 = new XsdGMonthDay(4,0);
        assertTrue( c1.compareTo(c2) < 0);
        c1 = new XsdGMonthDay("\"--02-00\"^^<xsd:gMonthDay>");
        c2 = new XsdGMonthDay("\"--04-00\"^^<xsd:gMonthDay>");
        assertTrue( c1.compareTo(c2)< 0);
        c1 = new XsdGMonthDay(2,1);
        c2 = new XsdGMonthDay(2,2);
        assertTrue( c1.compareTo(c2) < 0);
    }

    @Test
    public void testEqual(){
        XsdGMonthDay c1 = new XsdGMonthDay(2,0);
        XsdGMonthDay c2 = new XsdGMonthDay(2,0);
        assertTrue( c1.compareTo(c2) == 0);
        c1 = new XsdGMonthDay("\"--02-00\"^^<xsd:gMonthDay>");
        c2 = new XsdGMonthDay("\"--02-00\"^^<xsd:gMonthDay>");
        assertTrue( c1.compareTo(c2)== 0);
        assertTrue(c1.compareTo(c1)==0);
    }

    @Test
    public void testGreater(){
        XsdGMonthDay c1 = new XsdGMonthDay(4,0);
        XsdGMonthDay c2 = new XsdGMonthDay(2,0);
        assertTrue( c1.compareTo(c2) > 0);
        c1 = new XsdGMonthDay("\"--04-00\"^^<xsd:gMonthDay>");
        c2 = new XsdGMonthDay("\"--02-00\"^^<xsd:gMonthDay>");
        assertTrue( c1.compareTo(c2)> 0);
        c1 = new XsdGMonthDay(2,2);
        c2 = new XsdGMonthDay(2,1);
        assertTrue( c1.compareTo(c2) > 0);
    }
}
