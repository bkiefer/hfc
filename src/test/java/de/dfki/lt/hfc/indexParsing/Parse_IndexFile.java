package de.dfki.lt.hfc.indexParsing;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import org.junit.Test;

import de.dfki.lt.hfc.Config;
import de.dfki.lt.hfc.IndexStore;
import de.dfki.lt.hfc.TestConfig;
import de.dfki.lt.hfc.TestHfc;
import de.dfki.lt.hfc.TestingUtils;
import de.dfki.lt.hfc.indices.BPlusTreeIndex;
import de.dfki.lt.hfc.indices.BTreeIndex;
import de.dfki.lt.hfc.indices.IntervalTreeIndex;
import de.dfki.lt.hfc.types.XsdDate;
import de.dfki.lt.hfc.types.XsdLong;

/**
 * Created by chwi02 on 21.03.17.
 */
public class Parse_IndexFile {

  private static String getResource(String name) {
    return TestingUtils.getTestResource("Index_Parsing", name);
  }

  private static IndexStore getStore(String res) {
    try {
      TestConfig config = TestConfig.getInstance(getResource(res));
      // RDF reading will fail, but is not relevant here
      config.put(Config.RDFCHECK, false);
      TestHfc hfc = new TestHfc(config);
      return hfc.getIndex();
    } catch (Exception ex) {
      throw new RuntimeException(ex);
    }
  }

  @Test
  public void noSecIndex() {
    IndexStore indexStore = getStore("index_Parsing1.yml");
    assertTrue(indexStore.getPrimIndex() instanceof BTreeIndex);
    assertEquals(0, indexStore.getPrimIndex().indexedPosition_start);
    assertTrue(indexStore.primIndexKey != null);
    assertTrue(indexStore.primIndexKey == XsdLong.class);
  }

  @Test
  public void withSecIndex() {
    IndexStore indexStore = getStore("index_Parsing2.yml");
    assertTrue(indexStore.getPrimIndex() instanceof BPlusTreeIndex);
    assertTrue(indexStore.getSecIndex() instanceof IntervalTreeIndex);
    assertEquals(3, indexStore.getPrimIndex().indexedPosition_start);
    assertTrue(indexStore.primIndexKey != null);
    assertTrue(indexStore.primIndexKey == XsdDate.class);
  }

  @Test
  public void comments() throws FileNotFoundException {
    IndexStore indexStore = getStore("index_Parsing3.yml");
    assertTrue(indexStore.getPrimIndex() instanceof BPlusTreeIndex);
    assertEquals(3, indexStore.getPrimIndex().indexedPosition_start);
    assertTrue(indexStore.primIndexKey != null);
    assertTrue(indexStore.primIndexKey == XsdDate.class);
  }

  @Test // (expected = IndexingException.class)
  public void missingParameters() throws FileNotFoundException {
    IndexStore indexStore = getStore("index_Parsing4.yml");
    assertNull(indexStore);
  }
}
