package de.dfki.lt.hfc.comparators;

import de.dfki.lt.hfc.types.XsdGMonth;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * Created by chwi02 on 27.02.17.
 */
public class XsdGMonthCompTest {

    @Test
    public void testLess(){
        XsdGMonth c1 = new XsdGMonth(2);
        XsdGMonth c2 = new XsdGMonth(4);
        assertTrue( c1.compareTo(c2) < 0);
        c1 = new XsdGMonth("\"--02\"^^<xsd:gMonth>");
        c2 = new XsdGMonth("\"--04\"^^<xsd:gMonth>");
        assertTrue( c1.compareTo(c2)< 0);

    }

    @Test
    public void testEqual(){
        XsdGMonth c1 = new XsdGMonth(2);
        XsdGMonth c2 = new XsdGMonth(2);
        assertTrue( c1.compareTo(c2) == 0);
        c1 = new XsdGMonth("\"--02\"^^<xsd:gMonth>");
        c2 = new XsdGMonth("\"--02\"^^<xsd:gMonth>");
        assertTrue( c1.compareTo(c2)== 0);
        assertTrue(c1.compareTo(c1)==0);
    }

    @Test
    public void testGreater(){
        XsdGMonth c1 = new XsdGMonth(4);
        XsdGMonth c2 = new XsdGMonth(2);
        assertTrue( c1.compareTo(c2) > 0);
        c1 = new XsdGMonth("\"--04\"^^<xsd:gMonth>");
        c2 = new XsdGMonth("\"--02\"^^<xsd:gMonth>");
        assertTrue( c1.compareTo(c2)> 0);
    }

}
