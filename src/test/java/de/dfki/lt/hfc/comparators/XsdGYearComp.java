package de.dfki.lt.hfc.comparators;

import de.dfki.lt.hfc.types.XsdGYear;
import org.junit.Test;

import static org.junit.Assert.assertTrue;


/**
 * Created by chwi02 on 27.02.17.
 */
public class XsdGYearComp {

    @Test
    public void testLess(){
        XsdGYear c1 = new XsdGYear(2);
        XsdGYear c2 = new XsdGYear(4);
        assertTrue( c1.compareTo(c2) < 0);
        c1 = new XsdGYear("\"0002\"^^<xsd:gYear>");
        c2 = new XsdGYear("\"0004\"^^<xsd:gYear>");
        assertTrue( c1.compareTo(c2)< 0);
        c1 = new XsdGYear("\"-0002\"^^<xsd:gYear>");
        c2 = new XsdGYear("\"0004\"^^<xsd:gYear>");
        assertTrue( c1.compareTo(c2)< 0);
        c1 = new XsdGYear("\"0002\"^^<xsd:gYear>");
        c2 = new XsdGYear("\"-0004\"^^<xsd:gYear>");
        assertTrue( c2.compareTo(c1)< 0);

    }

    @Test
    public void testEqual(){
        XsdGYear c1 = new XsdGYear(2);
        XsdGYear c2 = new XsdGYear(2);
        assertTrue( c1.compareTo(c2) == 0);
        c1 = new XsdGYear("\"0002\"^^<xsd:gYear>");
        c2 = new XsdGYear("\"0002\"^^<xsd:gYear>");
        assertTrue( c1.compareTo(c2)== 0);
        c1 = new XsdGYear("\"-0002\"^^<xsd:gYear>");
        c2 = new XsdGYear("\"-0002\"^^<xsd:gYear>");
        assertTrue( c1.compareTo(c2)== 0);
        c1 = new XsdGYear("\"0000\"^^<xsd:gYear>");
        c2 = new XsdGYear("\"0000\"^^<xsd:gYear>");
        assertTrue( c1.compareTo(c2)== 0);
        c1 = new XsdGYear("\"-0000\"^^<xsd:gYear>");
        c2 = new XsdGYear("\"-0000\"^^<xsd:gYear>");
        assertTrue( c1.compareTo(c2)== 0);
        assertTrue(c1.compareTo(c1)==0);
    }

    @Test
    public void testGreater(){
        XsdGYear c1 = new XsdGYear(4);
        XsdGYear c2 = new XsdGYear(2);
        assertTrue( c1.compareTo(c2) > 0);
        c1 = new XsdGYear("\"0004\"^^<xsd:gYear>");
        c2 = new XsdGYear("\"0002\"^^<xsd:gYear>");
        assertTrue( c1.compareTo(c2)> 0);
        c1 = new XsdGYear("\"-0004\"^^<xsd:gYear>");
        c2 = new XsdGYear("\"-0002\"^^<xsd:gYear>");
        assertTrue( c2.compareTo(c1)> 0);
    }
}
