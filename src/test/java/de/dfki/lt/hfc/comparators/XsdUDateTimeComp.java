package de.dfki.lt.hfc.comparators;

import de.dfki.lt.hfc.types.XsdUDateTime;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Created by chwi02 on 27.02.17.
 */
public class XsdUDateTimeComp {

    @Test
    public void testLess(){
        XsdUDateTime c1 = new XsdUDateTime(2,0,0,0,0,0);
        XsdUDateTime c2 = new XsdUDateTime(4,0,0,0,0,0);
        assertTrue( c1.compareTo(c2) < 0);
        c1 = new XsdUDateTime(2,1,0,0,0,0);
        c2 = new XsdUDateTime(2,2,0,0,0,0);
        assertTrue( c1.compareTo(c2)< 0);
        c1 = new XsdUDateTime(2,1,1,0,0,0);
        c2 = new XsdUDateTime(2,1,2,0,0,0);
        assertTrue( c1.compareTo(c2)< 0);
        //underspecify facts
        c1 = new XsdUDateTime(2,-1,1,-1,0,1);
        c2 = new XsdUDateTime(2,-1,2,-1,0,0);
        assertTrue( c1.compareTo(c2)< 0);
        c1 = new XsdUDateTime(-1,-1,-1,-1,0,1);
        c2 = new XsdUDateTime(-1,-1,-1,-1,0,2);
        assertTrue( c1.compareTo(c2)< 0);
    }

    @Test
    public void testEqual(){
        XsdUDateTime c1 = new XsdUDateTime(2,0,0,0,0,0);
        XsdUDateTime c2 = new XsdUDateTime(2,0,0,0,0,0);
        assertTrue( c1.compareTo(c2) == 0);
        c1 = new XsdUDateTime(2,1,0,0,0,0);
        c2 = new XsdUDateTime(2,1,0,0,0,0);
        assertTrue( c1.compareTo(c2)== 0);
        c1 = new XsdUDateTime(2,1,1,0,0,0);
        c2 = new XsdUDateTime(2,1,1,0,0,0);
        assertTrue( c1.compareTo(c2)== 0);
        assertTrue(c1.compareTo(c1)==0);
        c1 = new XsdUDateTime(-1,-1,-1,-1,-1,-1);
        c2 = new XsdUDateTime(-1,-1,-1,-1,-1,-1);
        assertTrue( c1.compareTo(c2)== 0);
    }

    @Test
    public void testGreater(){
        XsdUDateTime c1 = new XsdUDateTime(4,0,0,0,0,0);
        XsdUDateTime c2 = new XsdUDateTime(2,0,0,0,0,0);
        assertTrue( c1.compareTo(c2) > 0);
        c1 = new XsdUDateTime(2,4,0,0,0,0);
        c2 = new XsdUDateTime(2,1,0,0,0,0);
        assertTrue( c1.compareTo(c2)> 0);
        c1 = new XsdUDateTime(2,1,4,0,0,0);
        c2 = new XsdUDateTime(2,1,1,0,0,0);
        assertTrue( c1.compareTo(c2)> 0);
        c1 = new XsdUDateTime(2,-1,4,0,0,0);
        c2 = new XsdUDateTime(2,-1,1,0,0,0);
        assertTrue( c1.compareTo(c2)> 0);
        c1 = new XsdUDateTime(2,-1,-1,0,0,1);
        c2 = new XsdUDateTime(2,-1,-1,0,0,0);
        assertTrue( c1.compareTo(c2)> 0);
    }
    
}
