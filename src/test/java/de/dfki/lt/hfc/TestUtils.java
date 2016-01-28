package de.dfki.lt.hfc;

import java.io.File;

import de.dfki.lt.hfc.BindingTable.BindingTableIterator;

public class TestUtils {
  private static final File testResourceDir = new File("src/test/data/");
  private static final File resourceDir = new File("src/resources/");
  private static final File tmpDir = new File("/tmp");

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

  public static boolean checkResult(String[][] expected,
      BindingTable bt, String ... vars) {
    int rows = 0;
    //boolean result;
    try {
      BindingTableIterator it = bt.iterator(vars);
      while (it.hasNext()) {
        String[] tuple = it.nextAsString();
        // make sure all tuples from expected are covered: find the right one
        // TODO: WRITE THE CODE AND APPLY IT CORRECTLY IN THE TESTS
        Iterator<String[]> exp = expected.iterator();
        while (exp.hasNext()) {
          String[] other = exp.next();
          if (tuple.equals(other)) {
           //result = true; 
           ++rows;
           break;
          } 
        //return result;
      }
      }
    }
    catch (BindingTableIteratorException e) {
      throw new RuntimeException(e); // should never happen
    }
    return rows == expected.length;
  }

}
