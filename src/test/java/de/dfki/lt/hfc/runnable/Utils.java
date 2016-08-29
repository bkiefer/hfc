package de.dfki.lt.hfc.runnable;

import de.dfki.lt.hfc.*;
import static org.junit.Assert.*;

import java.io.File;
import java.util.*;

import de.dfki.lt.hfc.types.AnyType;

public class Utils {

  private static final File resourceDir = new File("src/resources/");
  private static final File tmpDir = new File("/tmp");

  public static String getResource(String name) {
    // System.out.println(new File(".").getAbsolutePath());
    return new File(resourceDir, name).getPath();
  }

  public static String getTempFile(String name) {
    // System.out.println(new File(".").getAbsolutePath());
    return new File(tmpDir, name).getPath();
  }



}
