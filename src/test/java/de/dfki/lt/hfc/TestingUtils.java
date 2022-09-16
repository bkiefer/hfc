package de.dfki.lt.hfc;

import static org.junit.Assert.*;

import java.io.File;
import java.util.*;

import de.dfki.lt.hfc.BindingTable.BindingTableIterator;
import de.dfki.lt.hfc.types.AnyType;

public class TestingUtils {
  private static final File testResourceDir = new File("src/test/data/");
  private static final File tmpDir = new File("/tmp");

  public static String getTestResource(String subdir, String name) {
    return new File(new File(testResourceDir, subdir), name).getPath();
  }

  public static String getTestResource(String name) {
    return new File(testResourceDir, name).getPath();
  }

  public static String getTempFile(String name) {
    //
    return new File(tmpDir, name).getPath();
  }

  private static String [] neutralizeBlanknodes(String[] in) {
    in = Arrays.copyOf(in, in.length);
    for (int i = 0; i < in.length; ++i) {
      String s = in[i];
      if (s.startsWith("_:")) {
        in[i] = "_:";
      }
    }
    return in;
  }


  /** Check if expected contains all the rows that are in the BindingTable,
   *  even if there are duplicates.
   *
   *  CAVEAT: all blank nodes are reduced to "_:", so if you do structural
   *  tests involving blank nodes, you can NOT use this function.
   */
  public static void checkResult(String[][] expected,
      BindingTable bt, String ... vars) {
    if (vars.length == 0)
      throw new IllegalArgumentException("Variables list to test may not be empty!");
    try {
      // to make sure there can be multiple equal rows, we erase all rows
      // one by one from the List, which must be empty in the end
      List<String[]> exp = new ArrayList<String[]>(Arrays.asList(expected));
      BindingTableIterator bindIt = bt.iterator(vars);
      assertEquals(expected.length, bindIt.hasSize());

      while (bindIt.hasNext()) {
        String[] tuple = neutralizeBlanknodes(bindIt.nextAsString());
        // make sure all tuples from expected are covered: find the right one
        boolean found = false;
        for(Iterator<String[]> expIt = exp.iterator(); expIt.hasNext();) {
          String[] expectedRow = neutralizeBlanknodes(expIt.next());
          // This does not go into the elements of an array, will only return
          // true if tuple == other, which is never the case
          // if (tuple.equals(other)) {
          if (Arrays.equals(tuple, expectedRow)) {
            expIt.remove();
            found = true;
            break; // take the next row
          }
        }
        assertTrue("Row not found "+Arrays.toString(tuple), found);
      }
      assertTrue("Not all expected rows found ", exp.isEmpty());
    }
    catch (BindingTableIteratorException e) {
      throw new RuntimeException(e); // should never happen
    }
  }

  public interface NextCall<T> {
    public T[] next(BindingTableIterator it);
  }

  public static class NextAsStringCall implements NextCall<String> {
    public String[] next(BindingTableIterator it) {
      return it.nextAsString();
    }
  }

  public static class NextAsHfcCall implements NextCall<String> {
    public String[] next(BindingTableIterator it) {
      AnyType[] in = it.nextAsHfcType();
      String[] res = new String[in.length];
      int i = 0;
      for (AnyType a : in) res[i++] = a.toName();
      return res;
    }
  }

  public static class NextAsObjectCall implements NextCall<Object> {
    public Object[] next(BindingTableIterator it) {
      Object[] in = it.nextAsJavaObject();
      Object[] res = new Object[in.length];
      int i = 0;
      for (Object a : in)
        res[i++] = ((a instanceof AnyType) ? ((AnyType)a).toName() : a);
      return res;
    }
  }

  public static class NextAsIntCall implements NextCall<String> {
    private TupleStore _ts;

    public NextAsIntCall(TupleStore ts){
      _ts = ts;
    }

    public String[] next(BindingTableIterator it) {
      int[] in = it.next();
      String[] res = new String[in.length];
      int i = 0;
      for (int sym : in) {
        String name = _ts.getObject(sym).toString();
        res[i++] = name;
      }
      return res;
    }
  }

  public static <T> void printNext(BindingTableIterator it, NextCall<T> nc) {
    while (it.hasNext()) {
      T[] next = nc.next(it);
      System.out.print("{ \"");
      for (int i = 0; i < next.length; i++) {
        System.out.print(next[i].toString() + "\"");
        if (i < next.length - 1) {
          System.out.print(", \"");
        }
      }

    }
  }


  public static <T> void check(BindingTableIterator it, T[][] expected,
      NextCall<T> nc) {
    List<T[]> ee = new ArrayList<T[]>(Arrays.asList(expected));

    while (it.hasNext()) {
      T[]next = nc.next(it);
      boolean found = false;
      for (Iterator<T[]> eit = ee.iterator(); eit.hasNext();) {
        if (Arrays.equals(next, eit.next())) {
          eit.remove();
          found = true;
          break;
        }
      }
      assertTrue(Arrays.toString(next) + " " + ee.size(), found);
    }

    assertTrue("Remaining: " + ee.size(), ee.isEmpty());
  }

  public static void printExpected(BindingTable bt, TupleStore store) {
    printNext(bt.iterator(), new NextAsIntCall(store));
  }

  private static void checkResult(Hfc hfc, BindingTable bt, String[][] expected){
    check(bt.iterator(), expected,
        new NextAsIntCall(hfc._tupleStore));
  }
  
  public static void checkDoubleResult(Hfc hfc, BindingTable bt, Double expected) {
    assertTrue(bt.iterator().hasNext());
    int[] row = bt.iterator().next();
    Double actual = (Double)hfc._tupleStore.getObject(row[0]).toJava();
    assertEquals(expected, actual, 1E-5);
  }
  
  public static void checkResult(Hfc hfc, BindingTable bt, String[][] expected, String ... vars) {
    try {
      check(bt.iterator(vars), expected,
          new NextAsIntCall(hfc._tupleStore));
    } catch (BindingTableIteratorException e) {
      e.printStackTrace();
    }
  }

  public static<T> T[] reverse(T[] in) {
    for (int i = 0, j = in.length - 1; i < in.length >> 1; ++i, --j) {
      T help = in[j];
      in[j] = in[i];
      in[i] = help;
    }
    return in;
  }

  public static int[] reverse(int[] in) {
    for (int i = 0, j = in.length - 1; i < in.length / 2; ++i, --j) {
      int help = in[j];
      in[j] = in[i];
      in[i] = help;
    }
    return in;
  }

  public static String expstring(int[]exp, int[]in) {
    return "Expected " + Arrays.toString(exp) + " but was " + Arrays.toString(in);
  }
}
