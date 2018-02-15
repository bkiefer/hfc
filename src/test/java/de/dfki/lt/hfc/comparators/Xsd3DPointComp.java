package de.dfki.lt.hfc.comparators;

import de.dfki.lt.hfc.types.Xsd3DPoint;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;


/**
 * Created by chwi02 on 16.03.17.
 */
public class Xsd3DPointComp {

    @Test
    public void testLess(){
        Xsd3DPoint c1 = new Xsd3DPoint(1,0,0);
        Xsd3DPoint c2 = new Xsd3DPoint(2,0,0);
        assertTrue( c1.compareTo(c2) < 0);
        c1 = new Xsd3DPoint(0,1,0);
        c2 = new Xsd3DPoint(0,2,0);
        assertTrue( c1.compareTo(c2)< 0);
        c1 = new Xsd3DPoint(2,1,0);
        c2 = new Xsd3DPoint(2,2,0);
        assertTrue( c1.compareTo(c2)< 0);
//        c1 = new Xsd3DPoint(-2,0,0);
//        c2 = new Xsd3DPoint(-1,0,0);
//        assertTrue( c1.compareTo(c2) < 0);
//        c1 = new Xsd3DPoint(0,-2,0);
//        c2 = new Xsd3DPoint(0,-1,0);
//        assertTrue( c1.compareTo(c2)< 0);
//        c1 = new Xsd3DPoint(-2,-2,0);
//        c2 = new Xsd3DPoint(-2,-1,0);
//        assertTrue( c1.compareTo(c2)< 0);
//        c1 = new Xsd3DPoint(-2,0,0);
//        c2 = new Xsd3DPoint(1,0,0);
//        assertTrue( c1.compareTo(c2) < 0);
//        c1 = new Xsd3DPoint(0,-2,0);
//        c2 = new Xsd3DPoint(0,1,0);
//        assertTrue( c1.compareTo(c2)< 0);
//        c1 = new Xsd3DPoint(-2,-2,0);
//        c2 = new Xsd3DPoint(2,1,0);
//        assertTrue( c1.compareTo(c2)< 0);
    }

    @Test
    public void testEqual(){
        Xsd3DPoint c1 = new Xsd3DPoint(2,0,0);
        Xsd3DPoint c2 = new Xsd3DPoint(2,0,0);
        assertTrue( c1.compareTo(c2) == 0);
        c1 = new Xsd3DPoint(2,1,0);
        c2 = new Xsd3DPoint(2,1,0);
        assertTrue( c1.compareTo(c2)== 0);
        c1 = new Xsd3DPoint(1,1,0);
        c2 = new Xsd3DPoint(1,1,0);
        assertTrue( c1.compareTo(c2)== 0);
//        c1 = new Xsd3DPoint(-1,-1,0);
//        c2 = new Xsd3DPoint(-1,-1,0);
//        assertTrue( c1.compareTo(c2)== 0);
        c1 = new Xsd3DPoint(0,0,0);
        c2 = new Xsd3DPoint(0,0,0);
        assertTrue( c1.compareTo(c2)== 0);
        assertTrue(c1.compareTo(c1)==0);
    }

    @Test
    public void testGreater(){
        Xsd3DPoint c1 = new Xsd3DPoint(1,0,0);
        Xsd3DPoint c2 = new Xsd3DPoint(2,0,0);
        assertTrue( c2.compareTo(c1) > 0);
        c1 = new Xsd3DPoint(0,1,0);
        c2 = new Xsd3DPoint(0,2,0);
        assertTrue( c2.compareTo(c1) > 0);
        c1 = new Xsd3DPoint(2,1,0);
        c2 = new Xsd3DPoint(2,2,0);
        assertTrue( c2.compareTo(c1) > 0);
//        c1 = new Xsd3DPoint(-2,0,0);
//        c2 = new Xsd3DPoint(-1,0,0);
//        assertTrue( c2.compareTo(c1) > 0);
//        c1 = new Xsd3DPoint(0,-2,0);
//        c2 = new Xsd3DPoint(0,-1,0);
//        assertTrue( c2.compareTo(c1) > 0);
//        c1 = new Xsd3DPoint(-2,-2,0);
//        c2 = new Xsd3DPoint(-2,-1,0);
//        assertTrue( c2.compareTo(c1) > 0);
//        c1 = new Xsd3DPoint(-2,0,0);
//        c2 = new Xsd3DPoint(1,0,0);
//        assertTrue( c2.compareTo(c1) > 0);
//        c1 = new Xsd3DPoint(0,-2,0);
//        c2 = new Xsd3DPoint(0,1,0);
//        assertTrue( c2.compareTo(c1) > 0);
//        c1 = new Xsd3DPoint(-2,-2,0);
//        c2 = new Xsd3DPoint(2,1,0);
//        assertTrue( c2.compareTo(c1) > 0);
    }
}
