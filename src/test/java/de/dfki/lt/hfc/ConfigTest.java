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
        assertEquals(true, config.verbose);
        assertEquals("UTF-8", config.characterEncoding);
        assertEquals(4, config.noOfCores);
        assertEquals(500000, config.noOfTuples);
        assertEquals(100000, config.noOfAtoms);
        assertEquals(false,config.eqReduction);
        assertEquals(false, config.gc);
        assertEquals(true, config.cleanUpRepository);
        assertEquals(true, config.shortIsDefault);
        assertEquals("./src/resources/default.nt", config.tupleFiles.get(0));
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
