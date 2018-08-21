package de.dfki.lt.hfc.indexParsing;

import de.dfki.lt.hfc.Config;
import de.dfki.lt.hfc.IndexStore;
import de.dfki.lt.hfc.TestingUtils;
import de.dfki.lt.hfc.indices.*;
import de.dfki.lt.hfc.types.XsdDate;
import de.dfki.lt.hfc.types.XsdLong;
import org.junit.Test;


import java.io.FileNotFoundException;

import static junit.framework.Assert.fail;
import static org.junit.Assert.*;


/**
 * Created by chwi02 on 21.03.17.
 */
public class Parse_IndexFile {

    private static String getResource(String name) {
        return TestingUtils.getTestResource("Index_Parsing", name);
    }

    @Test
    public void noSecIndex() throws FileNotFoundException {
        Config config = Config.getInstance(getResource("index_Parsing1.yml"));
        IndexStore indexStore = config.indexStore;
        assertTrue(indexStore.getPrimIndex() instanceof BTreeIndex);
        assertEquals(0, indexStore.getPrimIndex().indexedPosition_start);
        assertTrue(indexStore.primIndexKey != null);
        assertTrue(indexStore.primIndexKey == XsdLong.class);
    }

    @Test
    public void withSecIndex() throws FileNotFoundException {
        Config config = Config.getInstance(getResource("index_Parsing2.yml"));
        IndexStore indexStore = config.indexStore;
        assertTrue(indexStore.getPrimIndex() instanceof BPlusTreeIndex);
        assertTrue(indexStore.getSecIndex() instanceof IntervalTreeIndex);
        assertEquals(3, indexStore.getPrimIndex().indexedPosition_start);
        assertTrue(indexStore.primIndexKey != null);
        assertTrue(indexStore.primIndexKey == XsdDate.class);

    }

    @Test
    public void comments() throws FileNotFoundException {
        Config config = Config.getInstance(getResource("index_Parsing3.yml"));
        IndexStore indexStore = config.indexStore;
        assertTrue(indexStore.getPrimIndex() instanceof BPlusTreeIndex);
        assertEquals(3, indexStore.getPrimIndex().indexedPosition_start);
        assertTrue(indexStore.primIndexKey != null);
        assertTrue(indexStore.primIndexKey == XsdDate.class);

    }

    @Test//(expected = IndexingException.class)
    public void missingParameters() throws FileNotFoundException {
        Config config = Config.getInstance(getResource("index_Parsing4.yml"));
        IndexStore indexStore = config.indexStore;
        assertNull(indexStore);

    }
}
