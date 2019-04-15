package de.dfki.lt.hfc;

import org.junit.Test;

import java.io.FileNotFoundException;
import java.io.IOException;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;

public class ConfigTest {

    private static String getResource(String name) {
        return TestingUtils.getTestResource("Index_Parsing", name);
    }

    @Test
    public void test_ValidConfig() throws IOException {
        Config config = Config.getDefaultConfig();
        assertNotNull(config);
        assertEquals(true, config.isVerbose());
        assertEquals("UTF-8", config.getCharacterEncoding());
        assertEquals(4, config.getNoOfCores());
        assertEquals(500000, config.getNoOfTuples());
        assertEquals(100000, config.getNoOfAtoms());
        assertEquals(false,config.isEqReduction());
        assertEquals(false, config.isGarbageCollection());
        assertEquals(true, config.isCleanupRepository());
        assertEquals(true, config.isShortIsDefault());
        assertEquals("./src/resources/default.nt", config.getTupleFiles().get(0));
    }

    @Test (expected = java.lang.NullPointerException.class)
    public void test_InvalidConfig() throws FileNotFoundException {
        Config config = Config.getInstance(TestingUtils.getTestResource("invalid.yml"));

    }

    @Test
    public void test_ConfigWithIndex() throws FileNotFoundException {
        Config config = Config.getInstance(getResource("index_Parsing1.yml"));
        assertNotNull(config);
    }

}
