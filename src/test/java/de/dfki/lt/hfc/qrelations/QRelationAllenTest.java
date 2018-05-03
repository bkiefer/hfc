package de.dfki.lt.hfc.qrelations;


import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * TODO add a dedicated test for isValid()
 * @author Christian Willms - Date: 18.09.17 12:15.
 * @version 18.09.17
 */
public class QRelationAllenTest {


  @Test
  public void isAllenRelation() {
    AllenAfter after = new AllenAfter("After",0, 0, 0);
    assertTrue(after.isAllenRelation());
    AllenBefore before = new AllenBefore("Before",0, 0, 0);
    assertTrue(after.isAllenRelation());
    AllenMeet meet = new AllenMeet("Meet",0, 0, 0);
    assertTrue(after.isAllenRelation());
    AllenDuring during = new AllenDuring("During",0, 0, 0);
    assertTrue(during.isAllenRelation());
    AllenEqual equal = new AllenEqual("equal",0, 0, 0);
    assertTrue(equal.isAllenRelation());
    AllenFinish finish = new AllenFinish("finish",0, 0, 0);
    assertTrue(finish.isAllenRelation());
    AllenOverlaps overlaps = new AllenOverlaps("overlaps",0, 0, 0);
    assertTrue(overlaps.isAllenRelation());
    AllenStart start = new AllenStart("Start",0, 0, 0);
    assertTrue(start.isAllenRelation());
    Interval interval = new Interval("",0,0,0);
    assertTrue(interval.isAllenRelation());
  }

  @Test
  public void isInterval() {
    AllenAfter after = new AllenAfter("After",0, 0, 0);
    assertFalse(after.isInterval());
    AllenBefore before = new AllenBefore("Before",0, 0, 0);
    assertFalse(after.isInterval());
    AllenMeet meet = new AllenMeet("Meet",0, 0, 0);
    assertFalse(after.isInterval());
    AllenDuring during = new AllenDuring("During",0, 0, 0);
    assertFalse(during.isInterval());
    AllenEqual equal = new AllenEqual("equal",0, 0, 0);
    assertFalse(equal.isInterval());
    AllenFinish finish = new AllenFinish("finish",0, 0, 0);
    assertFalse(finish.isInterval());
    AllenOverlaps overlaps = new AllenOverlaps("overlaps",0, 0, 0);
    assertFalse(overlaps.isInterval());
    AllenStart start = new AllenStart("Start",0, 0, 0);
    assertFalse(start.isInterval());
    Interval interval = new Interval("",0,0,0);
    assertTrue(interval.isInterval());
  }

}