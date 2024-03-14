package de.dfki.lt.hfc.db.rdfProxy;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Set;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.dfki.lt.hfc.db.HfcDbHandler;

public class RdfClassRestriction {
  private static final String RESOURCE_DIR = "src/test/data/";
  private static HfcDbHandler handler;
  RdfProxy _proxy;
  
  @BeforeClass
  public static void init() {
    handler = new HfcDbHandler(RESOURCE_DIR + "restrictions.yml");
  }
  
  @Before
  public void before() {
    _proxy = new RdfProxy(handler);
  }
  
  @Test
  public void test() {
    RdfClass test = _proxy.getRdfClass("<restr:Test>");
    assertEquals(3, test.getProperties().size());
    for (String prop : test.getProperties()) {
      if (prop.contains("Object")) {
        assertFalse(test.isDatatypeProperty(prop));
      } else {
        assertTrue(test.isDatatypeProperty(prop));
      }
    }
    Set<String> propRange = test.getPropertyRange("<restr:someObjectProperty>");
    assertEquals(1, propRange.size());
    String pr = propRange.iterator().next();
    RdfClass range = _proxy.getRdfClass(pr);
    assertEquals(range, _proxy.getRdfClass("<restr:TestRange>"));
  }

}
