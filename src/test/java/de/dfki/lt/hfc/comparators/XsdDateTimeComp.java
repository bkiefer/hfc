package de.dfki.lt.hfc.comparators;

import de.dfki.lt.hfc.types.XsdDateTime;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;


/**
 * Created by chwi02 on 27.02.17.
 */
public class XsdDateTimeComp {

    @Test
    public void testLess(){
        XsdDateTime c1 = new XsdDateTime(2,0,0,0,0,0);
        XsdDateTime c2 = new XsdDateTime(4,0,0,0,0,0);
        assertTrue( c1.compareTo(c2) < 0);
        c1 = new XsdDateTime(2,1,0,0,0,0);
        c2 = new XsdDateTime(2,2,0,0,0,0);
        assertTrue( c1.compareTo(c2)< 0);
        c1 = new XsdDateTime(2,1,1,0,0,0);
        c2 = new XsdDateTime(2,1,2,0,0,0);
        assertTrue( c1.compareTo(c2)< 0);
    }

    @Test
    public void testEqual(){
        XsdDateTime c1 = new XsdDateTime(2,0,0,0,0,0);
        XsdDateTime c2 = new XsdDateTime(2,0,0,0,0,0);
        assertTrue( c1.compareTo(c2) == 0);
        c1 = new XsdDateTime(2,1,0,0,0,0);
        c2 = new XsdDateTime(2,1,0,0,0,0);
        assertTrue( c1.compareTo(c2)== 0);
        c1 = new XsdDateTime(2,1,1,0,0,0);
        c2 = new XsdDateTime(2,1,1,0,0,0);
        assertTrue( c1.compareTo(c2)== 0);
        assertTrue(c1.compareTo(c1)==0);
    }

    @Test
    public void testGreater(){
        XsdDateTime c1 = new XsdDateTime(4,0,0,0,0,0);
        XsdDateTime c2 = new XsdDateTime(2,0,0,0,0,0);
        assertTrue( c1.compareTo(c2) > 0);
        c1 = new XsdDateTime(2,4,0,0,0,0);
        c2 = new XsdDateTime(2,1,0,0,0,0);
        assertTrue( c1.compareTo(c2)> 0);
        c1 = new XsdDateTime(2,1,4,0,0,0);
        c2 = new XsdDateTime(2,1,1,0,0,0);
        assertTrue( c1.compareTo(c2)> 0);
    }
    
}
