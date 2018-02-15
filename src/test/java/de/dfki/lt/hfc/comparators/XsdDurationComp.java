package de.dfki.lt.hfc.comparators;

import de.dfki.lt.hfc.types.XsdDuration;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;


/**
 * Created by chwi02 on 27.02.17.
 */
public class XsdDurationComp {

    @Test
    public void testLess(){
        XsdDuration c1 = new XsdDuration(1,0,0,0,0,0);
        XsdDuration c2 = new XsdDuration(2,0,0,0,0,0);
        assertTrue( c1.compareTo(c2) < 0);
         c1 = new XsdDuration(1,1,0,0,0,0);
         c2 = new XsdDuration(1,2,0,0,0,0);
        assertTrue( c1.compareTo(c2) < 0);
        c1 = new XsdDuration(1,1,1,0,0,0);
        c2 = new XsdDuration(1,1,2,0,0,0);
        assertTrue( c1.compareTo(c2) < 0);
        c1 = new XsdDuration(1,1,1,0,0,1);
        c2 = new XsdDuration(1,1,1,0,0,2);
        assertTrue( c1.compareTo(c2) < 0);
        //signs
         c1 = new XsdDuration(false,1,0,0,0,0,0);
        c2 = new XsdDuration(true,2,0,0,0,0,0);
        assertTrue( c1.compareTo(c2) < 0);
        c1 = new XsdDuration(false,1,1,0,0,0,0);
        c2 = new XsdDuration(false,1,2,0,0,0,0);
        assertTrue( c1.compareTo(c2) < 0);
        c1 = new XsdDuration(true,1,1,1,0,0,0);
        c2 = new XsdDuration(false,1,1,2,0,0,0);
        assertTrue( c1.compareTo(c2) < 0);
    }

    @Test
    public void testEqual(){
        XsdDuration c1 = new XsdDuration(2,1,0,0,0,0);
        XsdDuration c2 = new XsdDuration(2,1,0,0,0,0);
        assertTrue( c1.compareTo(c2) == 0);
        c1 = new XsdDuration(true,1,1,1,0,0,0);
        c2 = new XsdDuration(false,1,1,1,0,0,0);
        assertTrue(c1.compareTo(c2)==0);
        c1 = new XsdDuration(false,1,1,1,0,0,0);
        c2 = new XsdDuration(false,1,1,1,0,0,0);
        assertTrue(c1.compareTo(c2)==0);
        c1 = new XsdDuration(false,1,1,1,0,0,0);
        c2 = new XsdDuration(true,1,1,1,0,0,0);
        assertTrue(c1.compareTo(c2)==0);
        assertTrue(c1.compareTo(c1)==0);
    }

    @Test
    public void testGreater(){
        XsdDuration c1 = new XsdDuration(4,1,0,0,0,0);
        XsdDuration c2 = new XsdDuration(2,1,0,0,0,0);
        assertTrue( c1.compareTo(c2) > 0);
        c1 = new XsdDuration(false,4,1,0,0,0,0);
         c2 = new XsdDuration(true,2,1,0,0,0,0);
        assertTrue( c1.compareTo(c2) > 0);
        c1 = new XsdDuration(true,4,1,0,0,0,0);
         c2 = new XsdDuration(false,2,1,0,0,0,0);
        assertTrue( c1.compareTo(c2) > 0);
         c1 = new XsdDuration(false,4,1,0,0,0,0);
         c2 = new XsdDuration(false,2,1,0,0,0,0);
        assertTrue( c1.compareTo(c2) > 0);
    }
    
}
