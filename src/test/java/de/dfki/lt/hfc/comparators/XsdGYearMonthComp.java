package de.dfki.lt.hfc.comparators;

import de.dfki.lt.hfc.types.XsdGYearMonth;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * Created by chwi02 on 27.02.17.
 */
public class XsdGYearMonthComp {

    @Test
    public void testLess(){
        XsdGYearMonth c1 = new XsdGYearMonth(2,0);
        XsdGYearMonth c2 = new XsdGYearMonth(4,0);
        assertTrue( c1.compareTo(c2) < 0);
        c1 = new XsdGYearMonth("\"0002-01\"^^<xsd:gYearMonth>");
        c2 = new XsdGYearMonth("\"0004-01\"^^<xsd:gYearMonth>");
        assertTrue( c1.compareTo(c2)< 0);
        c1 = new XsdGYearMonth("\"-0002-01\"^^<xsd:gYearMonth>");
        c2 = new XsdGYearMonth("\"0004-01\"^^<xsd:gYearMonth>");
        assertTrue( c1.compareTo(c2)< 0);
        c1 = new XsdGYearMonth("\"0002-01\"^^<xsd:gYearMonth>");
        c2 = new XsdGYearMonth("\"-0004-01\"^^<xsd:gYearMonth>");
        assertTrue( c2.compareTo(c1)< 0);

    }

    @Test
    public void testEqual(){
        XsdGYearMonth c1 = new XsdGYearMonth(2,1);
        XsdGYearMonth c2 = new XsdGYearMonth(2,1);
        assertTrue( c1.compareTo(c2) == 0);
        c1 = new XsdGYearMonth("\"0002-01\"^^<xsd:gYearMonth>");
        c2 = new XsdGYearMonth("\"0002-01\"^^<xsd:gYearMonth>");
        assertTrue( c1.compareTo(c2)== 0);
        c1 = new XsdGYearMonth("\"-0002-01\"^^<xsd:gYearMonth>");
        c2 = new XsdGYearMonth("\"-0002-01\"^^<xsd:gYearMonth>");
        assertTrue( c1.compareTo(c2)== 0);
        c1 = new XsdGYearMonth("\"0000-01\"^^<xsd:gYearMonth>");
        c2 = new XsdGYearMonth("\"0000-01\"^^<xsd:gYearMonth>");
        assertTrue( c1.compareTo(c2)== 0);
        c1 = new XsdGYearMonth("\"-0000-01\"^^<xsd:gYearMonth>");
        c2 = new XsdGYearMonth("\"-0000-01\"^^<xsd:gYearMonth>");
        assertTrue( c1.compareTo(c2)== 0);
        assertTrue(c1.compareTo(c1)==0);
    }

    @Test
    public void testGreater(){
        XsdGYearMonth c1 = new XsdGYearMonth(4,1);
        XsdGYearMonth c2 = new XsdGYearMonth(2,1);
        assertTrue( c1.compareTo(c2) > 0);
        c1 = new XsdGYearMonth("\"0004-01\"^^<xsd:gYearMonth>");
        c2 = new XsdGYearMonth("\"0002-01\"^^<xsd:gYearMonth>");
        assertTrue( c1.compareTo(c2)> 0);
        c1 = new XsdGYearMonth("\"-0004-01\"^^<xsd:gYearMonth>");
        c2 = new XsdGYearMonth("\"-0002-01\"^^<xsd:gYearMonth>");
        assertTrue( c2.compareTo(c1)> 0);
    }
}
