package de.dfki.lt.hfc.comparators;

import de.dfki.lt.hfc.types.XsdDate;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * Created by chwi02 on 27.02.17.
 */
public class XsdDateComp {

    @Test
    public void testLess(){
        XsdDate c1 = new XsdDate(2,0,0);
        XsdDate c2 = new XsdDate(4,0,0);
        assertTrue( c1.compareTo(c2) < 0);
        c1 = new XsdDate(2,1,0);
        c2 = new XsdDate(2,2,0);
        assertTrue( c1.compareTo(c2)< 0);
        c1 = new XsdDate(2,1,1);
        c2 = new XsdDate(2,1,2);
        assertTrue( c1.compareTo(c2)< 0);
    }

    @Test
    public void testEqual(){
        XsdDate c1 = new XsdDate(2,0,0);
        XsdDate c2 = new XsdDate(2,0,0);
        assertTrue( c1.compareTo(c2) == 0);
        c1 = new XsdDate(2,1,0);
        c2 = new XsdDate(2,1,0);
        assertTrue( c1.compareTo(c2)== 0);
        c1 = new XsdDate(2,1,1);
        c2 = new XsdDate(2,1,1);
        assertTrue( c1.compareTo(c2)== 0);
        assertTrue(c1.compareTo(c1)==0);
    }

    @Test
    public void testGreater(){
        XsdDate c1 = new XsdDate(4,0,0);
        XsdDate c2 = new XsdDate(2,0,0);
        assertTrue( c1.compareTo(c2) > 0);
        c1 = new XsdDate(2,4,0);
        c2 = new XsdDate(2,1,0);
        assertTrue( c1.compareTo(c2)> 0);
        c1 = new XsdDate(2,1,4);
        c2 = new XsdDate(2,1,1);
        assertTrue( c1.compareTo(c2)> 0);
    }

}
