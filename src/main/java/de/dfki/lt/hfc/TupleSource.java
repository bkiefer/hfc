package de.dfki.lt.hfc;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;

public class TupleSource {
  private String origin;
  private BufferedReader br;

  public TupleSource(File fileName, String charSetName) throws IOException {
    origin = fileName.getPath();
    br = Files.newBufferedReader(fileName.toPath(),
        Charset.forName(charSetName));
  }

  public TupleSource(String name, InputStream in, String charSetName)
      throws IOException {
    origin = name;
    br = new BufferedReader(
        new InputStreamReader(in, Charset.forName(charSetName)));
  }

  public String getOrigin() {
    return origin;
  }

  public BufferedReader getReader() {
    return br;
  }

}
