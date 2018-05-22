package de.dfki.lt.hfc;

import java.util.List;

public class Utils {

  public static int[] toPrimitive(List<Integer> l) {
    int[] result = new int[l.size()];
    for (int i = 0; i < result.length; ++i) result[i] = l.get(i);
    return result;
  }

}
