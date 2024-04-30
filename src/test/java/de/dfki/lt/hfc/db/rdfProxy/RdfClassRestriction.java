package de.dfki.lt.hfc.db.rdfProxy;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Set;

import org.junit.Test;

import de.dfki.lt.hfc.db.HfcDbHandler;

public class RdfClassRestriction {
  private static final String RESOURCE_DIR = "src/test/data/";

  @Test
  public void test() {
    HfcDbHandler handler = new HfcDbHandler(RESOURCE_DIR + "restrictions.yml");
    RdfProxy proxy = new RdfProxy(handler);
    RdfClass test = proxy.getRdfClass("<restr:Test>");
    assertEquals(4, test.getProperties().size());
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
    RdfClass range = proxy.getRdfClass(pr);
    assertEquals(range, proxy.getRdfClass("<restr:TestRange>"));
    Rdf testInstance = test.getNewInstance("dom:");
    RdfClass clazz = proxy.getMostSpecificClass(testInstance.getURI());
    assertEquals(test, clazz);
    int pt = test.getPropertyType("<restr:functionalObjectProperty>");
    assertTrue((pt & RdfClass.OBJECT_PROPERTY) != 0);
    assertTrue((pt & RdfClass.FUNCTIONAL_PROPERTY) != 0);
    pt = test.getPropertyType("<restr:functionalDataProperty>");
    assertTrue((pt & RdfClass.DATATYPE_PROPERTY) != 0);
    assertTrue((pt & RdfClass.FUNCTIONAL_PROPERTY) != 0);
  }


  /**
   * Test that get works with restrictions, which are not owl:Class
   * @throws TException
   *
   * @throws java.lang.Exception
   */
  @Test
  public void testGetClassOfUnion() {
    HfcDbHandler handler = new HfcDbHandler(RESOURCE_DIR + "unionfuck.yml");
    RdfProxy proxy = new RdfProxy(handler);
    RdfClass test = proxy.getRdfClass("<soho:Tool>");
    Rdf testInstance = test.getNewInstance("cim:");
    RdfClass clazz =  proxy.getMostSpecificClass(testInstance.getURI());
    assertEquals(test, clazz);
  }
}
