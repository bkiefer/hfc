package de.dfki.lt.hfc;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Test;

public class FunctionTest {

 @Test
 public void testconstructor1() {
  //test Function(int result, String name, ArrayList<Integer> args)
  int result = 2;
  String name = "name";
  ArrayList<Integer> args = new ArrayList<>();
  args.add(1);
  Function function = new Function(result, name, args);
  assertNotNull(function);
 }

}
