package de.dfki.lt.hfc;

import java.io.File;
import java.nio.file.Path;

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

}
