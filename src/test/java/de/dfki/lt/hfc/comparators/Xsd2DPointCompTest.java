package de.dfki.lt.hfc.comparators;

import de.dfki.lt.hfc.types.Xsd2DPoint;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * Created by chwi02 on 16.03.17.
 */
public class Xsd2DPointCompTest {

    @Test
    public void testLess(){
        Xsd2DPoint c1 = new Xsd2DPoint(1,0);
        Xsd2DPoint c2 = new Xsd2DPoint(2,0);
        assertTrue( c1.compareTo(c2) < 0);
        c1 = new Xsd2DPoint(0,1);
        c2 = new Xsd2DPoint(0,2);
        assertTrue( c1.compareTo(c2)< 0);
        c1 = new Xsd2DPoint(2,1);
        c2 = new Xsd2DPoint(2,2);
        assertTrue( c1.compareTo(c2)< 0);
        //c1 = new Xsd2DPoint(-2,0);
        //c2 = new Xsd2DPoint(-1,0);
        //assertTrue( "c1:  " + c1.getZOrder()+ " c2:  " +c2.getZOrder(),c1.compareTo(c2) < 0);
        //c1 = new Xsd2DPoint(0,-2);
        //c2 = new Xsd2DPoint(0,-1);
        //assertTrue( c1.compareTo(c2)< 0);
        //c1 = new Xsd2DPoint(-2,-2);
        //c2 = new Xsd2DPoint(-2,-1);
        //assertTrue( c1.compareTo(c2)< 0);
        //c1 = new Xsd2DPoint(-2,0);
        //c2 = new Xsd2DPoint(1,0);
        //assertTrue( c1.compareTo(c2) < 0);
        //c1 = new Xsd2DPoint(0,-2);
        //c2 = new Xsd2DPoint(0,1);
        //assertTrue( "c1:  " + c1.getZOrder()+ " c2:  " +c2.getZOrder()  ,c1.compareTo(c2)< 0);
        //c1 = new Xsd2DPoint(-2,-2);
        //c2 = new Xsd2DPoint(2,1);
        //assertTrue( c1.compareTo(c2)< 0);
    }

    @Test
    public void testEqual(){
        Xsd2DPoint c1 = new Xsd2DPoint(2,0);
        Xsd2DPoint c2 = new Xsd2DPoint(2,0);
        assertTrue( c1.compareTo(c2) == 0);
        c1 = new Xsd2DPoint(2,1);
        c2 = new Xsd2DPoint(2,1);
        assertTrue( c1.compareTo(c2)== 0);
        c1 = new Xsd2DPoint(1,1);
        c2 = new Xsd2DPoint(1,1);
        assertTrue( c1.compareTo(c2)== 0);
//        c1 = new Xsd2DPoint(-1,-1);
//        c2 = new Xsd2DPoint(-1,-1);
//        assertTrue( c1.compareTo(c2)== 0);
        c1 = new Xsd2DPoint(0,0);
        c2 = new Xsd2DPoint(0,0);
        assertTrue( c1.compareTo(c2)== 0);
        assertTrue(c1.compareTo(c1)==0);
    }

    @Test
    public void testGreater(){
        Xsd2DPoint c1 = new Xsd2DPoint(1,0);
        Xsd2DPoint c2 = new Xsd2DPoint(2,0);
        assertTrue( c2.compareTo(c1) > 0);
        c1 = new Xsd2DPoint(0,1);
        c2 = new Xsd2DPoint(0,2);
        assertTrue( c2.compareTo(c1) > 0);
        c1 = new Xsd2DPoint(2,1);
        c2 = new Xsd2DPoint(2,2);
        assertTrue( c2.compareTo(c1) > 0);
//        c1 = new Xsd2DPoint(-2,0);
//        c2 = new Xsd2DPoint(-1,0);
//        assertTrue( c2.compareTo(c1) > 0);
//        c1 = new Xsd2DPoint(0,-2);
//        c2 = new Xsd2DPoint(0,-1);
//        assertTrue( c2.compareTo(c1) > 0);
//        c1 = new Xsd2DPoint(-2,-2);
//        c2 = new Xsd2DPoint(-2,-1);
//        assertTrue( c2.compareTo(c1) > 0);
//        c1 = new Xsd2DPoint(-2,0);
//        c2 = new Xsd2DPoint(1,0);
//        assertTrue( c2.compareTo(c1) > 0);
//        c1 = new Xsd2DPoint(0,-2);
//        c2 = new Xsd2DPoint(0,1);
//        assertTrue( c2.compareTo(c1) > 0);
//        c1 = new Xsd2DPoint(-2,-2);
//        c2 = new Xsd2DPoint(2,1);
//        assertTrue( c2.compareTo(c1) > 0);
    }
}
