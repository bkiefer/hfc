/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.dfki.lt.hfc;

import static de.dfki.lt.hfc.runnable.Utils.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

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
    assertArrayEquals(exp, in, expstring(exp, in));
  }

  @Test
  public void testReverse2() {
    int[] in = { 1, 2, 3, 4, 5, 6 };
    int[] exp = { 6, 5, 4, 3, 2, 1 };
    reverse(in);
    assertArrayEquals(exp, in, expstring(exp, in));
  }

  @Test
  public void testReverse3() {
    int[] in = { 1, 2 };
    int[] exp = { 2, 1 };
    reverse(in);
    assertArrayEquals(exp, in, expstring(exp, in));
  }

  @Test
  public void testReverse4() {
    int[] in = { 1  };
    int[] exp = { 1 };
    reverse(in);
    assertArrayEquals(exp, in, expstring(exp, in));
  }

  @Test
  public void testReverse5() {
    int[] in = {   };
    int[] exp = {  };
    reverse(in);
    assertArrayEquals(exp, in, expstring(exp, in));
  }
}
