package de.dfki.lt.hfc.indexParsing;

import de.dfki.lt.hfc.IndexStore;
import de.dfki.lt.hfc.TestingUtils;
import de.dfki.lt.hfc.indices.*;
import de.dfki.lt.hfc.types.XsdDate;
import de.dfki.lt.hfc.types.XsdLong;
import org.junit.Test;


import static junit.framework.Assert.fail;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


/**
 * Created by chwi02 on 21.03.17.
 */
public class Parse_IndexFile {

    private static String getResource(String name) {
        return TestingUtils.getTestResource("Index_Parsing", name);
    }

    @Test
    public void noSecIndex(){
        IndexStore indexStore = null;
        try {
            indexStore = new IndexStore(getResource("index_Parsing1.idx"), true);
        } catch (IndexingException e) {
            e.printStackTrace();
            fail();
        }
        assertTrue(indexStore.getPrimIndex() instanceof BTreeIndex);
        assertEquals(0, indexStore.getPrimIndex().indexedPosition_start);
        assertTrue(indexStore.primIndexKey != null);
        assertTrue(indexStore.primIndexKey == XsdLong.class);
    }

    @Test
    public void withSecIndex(){
        IndexStore indexStore = null;
        try {
            indexStore = new IndexStore(getResource("index_Parsing2.idx"), true);
        } catch (IndexingException e) {
            e.printStackTrace();
            fail();
        }
        assertTrue(indexStore.getPrimIndex() instanceof BPlusTreeIndex);
        assertTrue(indexStore.getSecIndex() instanceof IntervalTreeIndex);
        assertEquals(3, indexStore.getPrimIndex().indexedPosition_start);
        assertTrue(indexStore.primIndexKey != null);
        assertTrue(indexStore.primIndexKey == XsdDate.class);
    }

    @Test
    public void comments(){
        IndexStore indexStore = null;
        try {
            indexStore = new IndexStore(getResource("index_Parsing3.idx"), true);
        } catch (IndexingException e) {
            e.printStackTrace();fail();
        }
        assertTrue(indexStore.getPrimIndex() instanceof BPlusTreeIndex);
        assertEquals(3, indexStore.getPrimIndex().indexedPosition_start);
        assertTrue(indexStore.primIndexKey != null);
        assertTrue(indexStore.primIndexKey == XsdDate.class);
    }

    @Test
    public void missingParameters(){
        try {
            IndexStore indexStore = new IndexStore(getResource("index_Parsing4.idx"), true);
            fail();
        } catch (IndexingException e) {
            // everything ok
        }
    }
}
