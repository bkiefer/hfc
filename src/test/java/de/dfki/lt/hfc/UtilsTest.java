/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.dfki.lt.hfc;

import static de.dfki.lt.hfc.TestingUtils.*;
import org.junit.Assert;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author kiefer
 */
public class UtilsTest {

  @Test
  public void testReverse1() {
    int[] in = { 1, 2, 3, 4, 5 };
    int[] exp = { 5, 4, 3, 2, 1 };
    reverse(in);
    assertArrayEquals(expstring(exp, in), exp, in);
  }

  @Test
  public void testReverse2() {
    int[] in = { 1, 2, 3, 4, 5, 6 };
    int[] exp = { 6, 5, 4, 3, 2, 1 };
    reverse(in);
    assertArrayEquals(expstring(exp, in), exp, in);
  }

  @Test
  public void testReverse3() {
    int[] in = { 1, 2 };
    int[] exp = { 2, 1 };
    reverse(in);
    assertArrayEquals(expstring(exp, in), exp, in);
  }

  @Test
  public void testReverse4() {
    int[] in = { 1  };
    int[] exp = { 1 };
    reverse(in);
    assertArrayEquals(expstring(exp, in), exp, in);
  }

  @Test
  public void testReverse5() {
    int[] in = {   };
    int[] exp = {  };
    reverse(in);
    assertArrayEquals(expstring(exp, in), exp, in);
  }
}
