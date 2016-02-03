package de.dfki.lt.hfc;

import java.io.File;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import de.dfki.lt.hfc.BindingTable.BindingTableIterator;

public class TestUtils {
  private static final File testResourceDir = new File("src/test/data/");
  private static final File resourceDir = new File("src/resources/");
  private static final File tmpDir = new File(".");

  public static String getResource(String name) {
    // System.out.println(new File(".").getAbsolutePath());
    return new File(resourceDir, name).getPath();
  }

  public static String getTestResource(String subdir, String name) {
    return new File(new File(testResourceDir, subdir), name).getPath();
  }

  public static String getTempFile(String name) {
    // System.out.println(new File(".").getAbsolutePath());
    return new File(tmpDir, name).getPath();
  }

  /** Check if expected contains all the rows that are in the BindingTable,
   *  even if there are duplicates
   */
  public static boolean checkResult(String[][] expected,
      BindingTable bt, String ... vars) {
    try {
      // to make sure there can be multiple equal rows, we erase all rows
      // one by one from the List, which must be empty in the end
      List<String[]> exp = Arrays.asList(expected);
      BindingTableIterator bindIt = bt.iterator(vars);
      if (bindIt.hasSize() != expected.length)
        return false; // wrong size

      while (bindIt.hasNext()) {
        String[] tuple = bindIt.nextAsString();
        // make sure all tuples from expected are covered: find the right one
        boolean found = false;
        for(Iterator<String[]> expIt = exp.iterator(); expIt.hasNext();) {
          String[] expectedRow = expIt.next();
          // This does not go into the elements of an array, will only return
          // true if tuple == other, which is never the case
          // if (tuple.equals(other)) {
          if (Arrays.equals(tuple, expectedRow)) {
            expIt.remove();
            found = true;
            continue; // take the next row
          }
        }
        if (! found) return false;
      }
      return exp.isEmpty();
    }
    catch (BindingTableIteratorException e) {
      throw new RuntimeException(e); // should never happen
    }
  }

}
