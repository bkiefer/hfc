package de.dfki.lt.hfc.db;

import static de.dfki.lt.hfc.db.TestUtils.setupLocalHandler;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestPalOnto {

  private static HfcDbHandler server;

  @BeforeClass
  public static void startServerHere() {
    server = setupLocalHandler();
  }

  @AfterClass
  public static void shutdownServerHere() {

  }

  @Test
  public void getRelativesLabels() {
    String[][] res = {{"<dom:Father>", "\"Vader\"@nl"},
        {"<dom:Mother>", "\"Moeder\"@nl"},
        {"<dom:Brother>", "\"Broer\"@nl"},
        {"<dom:Sister>", "\"Zus\"@nl"}};
    Map<String, String> expected = new HashMap<>();
    for (String[] pair :res) {
      expected.put(pair[0], pair[1]);
    }

    List<String> types = server.selectQuery(
        "select ?s where ?s <rdfs:subClassOf> <dom:Family> ?_"
            + " filter ?s != <dom:Family>")
        .getTable()
        .projectColumn(0);
    List<List<String>> rels = server.selectLabels(types, "<rdfs:label>", "nl");
    assertEquals(4, rels.size());
    for (List<String> type_lab : rels) {
      assertTrue(expected.containsKey(type_lab.get(0)));
      assertEquals(expected.get(type_lab.get(0)), type_lab.get(1));
    }
  }
}
