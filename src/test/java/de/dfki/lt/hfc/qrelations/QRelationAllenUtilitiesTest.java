package de.dfki.lt.hfc.qrelations;

import de.dfki.lt.hfc.types.XsdDateTime;
import de.dfki.lt.hfc.types.XsdFloat;
import de.dfki.lt.hfc.types.XsdInt;
import de.dfki.lt.hfc.types.XsdLong;
import org.junit.Test;

import static org.junit.Assert.assertTrue;


/**
 * TODO add tests for increment() and decrement()
 * @author Christian Willms - Date: 18.09.17 12:15.
 * @version 18.09.17
 */
public class QRelationAllenUtilitiesTest {


  @Test
  public void  getGreater() {
    assertTrue(QRelationAllenUtilities.getGreater(XsdInt.class).equals("IGreater"));
    assertTrue(QRelationAllenUtilities.getGreater(XsdFloat.class).equals("FGreater"));
    assertTrue(QRelationAllenUtilities.getGreater(XsdLong.class).equals("LGreater"));
    assertTrue(QRelationAllenUtilities.getGreater(XsdDateTime.class).equals("DTGreater"));
  }

  @Test
  public void  getGreaterEqual() {
    assertTrue(QRelationAllenUtilities.getGreaterEqual(XsdInt.class).equals("IGreaterEqual"));
    assertTrue(QRelationAllenUtilities.getGreaterEqual(XsdFloat.class).equals("FGreaterEqual"));
    assertTrue(QRelationAllenUtilities.getGreaterEqual(XsdLong.class).equals("LGreaterEqual"));
    assertTrue(QRelationAllenUtilities.getGreaterEqual(XsdDateTime.class).equals("DTGreaterEqual"));
  }

  @Test
  public void  getLessEqual() {
    assertTrue(QRelationAllenUtilities.getLessEqual(XsdInt.class).equals("ILessEqual"));
    assertTrue(QRelationAllenUtilities.getLessEqual(XsdFloat.class).equals("FLessEqual"));
    assertTrue(QRelationAllenUtilities.getLessEqual(XsdLong.class).equals("LLessEqual"));
    assertTrue(QRelationAllenUtilities.getLessEqual(XsdDateTime.class).equals("DTLessEqual"));
  }

  @Test
  public void  getLess() {
    assertTrue(QRelationAllenUtilities.getLess(XsdInt.class).equals("ILess"));
    assertTrue(QRelationAllenUtilities.getLess(XsdFloat.class).equals("FLess"));
    assertTrue(QRelationAllenUtilities.getLess(XsdLong.class).equals("LLess"));
    assertTrue(QRelationAllenUtilities.getLess(XsdDateTime.class).equals("DTLess"));
  }

  @Test
  public void  getMinValue() {
    assertTrue(QRelationAllenUtilities.getMinValue(XsdDateTime.class).compareTo(new XsdDateTime(Integer.MIN_VALUE,Integer.MIN_VALUE,Integer.MIN_VALUE,Integer.MIN_VALUE,Integer.MIN_VALUE,Float.MIN_VALUE))== 0);
    assertTrue(QRelationAllenUtilities.getMinValue(XsdLong.class).compareTo(new XsdLong(Long.MIN_VALUE))== 0);
    assertTrue(QRelationAllenUtilities.getMinValue(XsdInt.class).compareTo(new XsdInt(Integer.MIN_VALUE))== 0);
    assertTrue(QRelationAllenUtilities.getMinValue(XsdFloat.class).compareTo(new XsdFloat(Float.MIN_VALUE))== 0);
  }

  @Test
  public void  getMaxValue() {
    assertTrue(QRelationAllenUtilities.getMaxValue(XsdDateTime.class).compareTo(new XsdDateTime(Integer.MAX_VALUE,Integer.MAX_VALUE,Integer.MAX_VALUE,Integer.MAX_VALUE,Integer.MAX_VALUE,Float.MAX_VALUE))== 0);
    assertTrue(QRelationAllenUtilities.getMaxValue(XsdLong.class).compareTo(new XsdLong(Long.MAX_VALUE))== 0);
    assertTrue(QRelationAllenUtilities.getMaxValue(XsdInt.class).compareTo(new XsdInt(Integer.MAX_VALUE))== 0);
    assertTrue(QRelationAllenUtilities.getMaxValue(XsdFloat.class).compareTo(new XsdFloat(Float.MAX_VALUE))== 0);
  }

  @Test
  public void  getEqual() {
    assertTrue(QRelationAllenUtilities.getEqual(XsdInt.class).equals("IEqual"));
    assertTrue(QRelationAllenUtilities.getEqual(XsdFloat.class).equals("FEqual"));
    assertTrue(QRelationAllenUtilities.getEqual(XsdLong.class).equals("LEqual"));
    assertTrue(QRelationAllenUtilities.getEqual(XsdDateTime.class).equals("DTEqual"));
  }

}