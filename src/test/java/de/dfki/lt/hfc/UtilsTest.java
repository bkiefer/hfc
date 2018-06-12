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

  @Test
  public void testEscape1() {
    String in = "abcdefg";
    String res = Utils.escapeDoublequotes(in);
    assertEquals(in, res);
  }

  @Test
  public void testEscape2() {
    String in = "abc\"de\"fg";
    String res = Utils.escapeDoublequotes(in);
    assertEquals("abc\\\"de\\\"fg", res);
  }

  @Test
  public void testEscape3() {
    String in = "abc\"de\\\"fg";
    String res = Utils.escapeDoublequotes(in);
    assertEquals("abc\\\"de\\\"fg", res);
  }
  @Test
  public void testEscape4() {
    String in = "\"abcde\"";
    String res = Utils.escapeDoublequotes(in);
    assertEquals("\\\"abcde\\\"", res);
  }

  @Test
  public void testEscape5() {
    String in = "\\\"abcde\\\"";
    String res = Utils.escapeDoublequotes(in);
    assertEquals("\\\"abcde\\\"", res);
  }

  @Test
  public void testEscape6() {
    String in = "";
    String res = Utils.escapeDoublequotes(in);
    assertEquals("", res);
  }

  @Test
  public void testEscape11() {
    String in = "\"abc\"aldsjf;lasjkdf\\\"de\"\\\"abc\\\"aldsjf;lasjkdf\\\\\\\""
        + "de\\\"\\\"abc\\\"aldsjf;lasjkdf\\\\\\\""
        + "de\\\"\\\"abc\\\"aldsjf;lasjkdf\\\\\\\"de\\\"";
    String res = null;
    for (int i = 0; i < 1; ++i) {
      res = Utils.escapeDoublequotes(in);
    }
    assertEquals("\\\"abc\\\"aldsjf;lasjkdf\\\"de\\\"\\\"abc\\\"aldsjf;lasjkdf\\\\\\\""
        + "de\\\"\\\"abc\\\"aldsjf;lasjkdf\\\\\\\""
        + "de\\\"\\\"abc\\\"aldsjf;lasjkdf\\\\\\\"de\\\"", res);
  }


  @Test
  public void testIntToExt1() {
    String in = "abcdefg";
    String ex = "abcdefg";
    String res = Utils.stringToExternal(in);
    assertEquals(ex, res);
    res = Utils.externalToString(ex);
    assertEquals(in, res);
  }

  @Test
  public void testIntToExt2() {
    String in = "abc\"de\"fg";
    String ex = "abc\\\"de\\\"fg";
    String res = Utils.stringToExternal(in);
    assertEquals(ex, res);
    res = Utils.externalToString(ex);
    assertEquals(in, res);
  }

  @Test
  public void testIntToExt3() {
    String in = "abc\"de\\\"fg";
    String ex = "abc\\\"de\\\\\\\"fg";
    String res = Utils.stringToExternal(in);
    assertEquals(ex, res);
    res = Utils.externalToString(ex);
    assertEquals(in, res);
  }

  @Test
  public void testIntToExt4() {
    String in = "\"abcde\"";
    String ex = "\\\"abcde\\\"";
    String res = Utils.stringToExternal(in);
    assertEquals(ex, res);
    res = Utils.externalToString(ex);
    assertEquals(in, res);
  }

  @Test
  public void testIntToExt5() {
    String in = "\\\"abcde\\\"";
    String ex = "\\\\\\\"abcde\\\\\\\"";
    String res = Utils.stringToExternal(in);
    assertEquals(ex, res);
    res = Utils.externalToString(ex);
    assertEquals(in, res);
  }

  @Test
  public void testIntToExt6() {
    String in = "";
    String ex = "";
    String res = Utils.stringToExternal(in);
    assertEquals(ex, res);
    res = Utils.externalToString(ex);
    assertEquals(in, res);
  }

  @Test
  public void testIntToExt11() {
    String in = "\"abc\"aldsjf;lasjkdf\\\"de\"\\\"abc\\\"aldsjf;lasjkdf\\\\\\\""
        + "de\\\"\\\"abc\\\"aldsjf;lasjkdf\\\\\\\""
        + "de\\\"\\\"abc\\\"aldsjf;lasjkdf\\\\\\\"de\\\"";
    String ex = "\\\"abc\\\"aldsjf;lasjkdf\\\\\\\"de\\\"\\\\\\\"abc\\\\\\\"aldsjf;lasjkdf\\\\\\\\\\\\\\\""
        + "de\\\\\\\"\\\\\\\"abc\\\\\\\"aldsjf;lasjkdf\\\\\\\\\\\\\\\""
        + "de\\\\\\\"\\\\\\\"abc\\\\\\\"aldsjf;lasjkdf\\\\\\\\\\\\\\\"de\\\\\\\"";
    String res = null;
    for (int i = 0; i < 1000000; ++i) {
      res = Utils.stringToExternal(in);
    }
    assertEquals(ex, res);
    for (int i = 0; i < 1; ++i) {
      res = Utils.externalToString(ex);
    }
    assertEquals(in, res);
  }
}
