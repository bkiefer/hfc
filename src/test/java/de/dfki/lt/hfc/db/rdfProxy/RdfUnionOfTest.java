/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.dfki.lt.hfc.db.rdfProxy;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.dfki.lt.hfc.db.HfcDbHandler;
import de.dfki.lt.hfc.db.TestUtils;

/**
 *
 */
public class RdfUnionOfTest {

  private static HfcDbHandler server;

  private RdfProxy _proxy;

  @BeforeClass
  public static void startServer() {
    server = TestUtils.setupLocalHandler();
  }

  @Before
  public void setUp() {
    _proxy = TestUtils.setupProxy(server);
  }

  @Test
  public void testStructureUnionOf() {
    RdfClass cl1 = _proxy.getClass("<tml:Food>");
    RdfClass cl2 = _proxy.getClass("<tml:OtherEvent>");
    RdfClass cl3 = _proxy.getClass("<tml:Glycemia>");
    List<RdfClass> sup1 = _proxy.getAllSuperClasses(cl1);
    List<RdfClass> sup2 = _proxy.getAllSuperClasses(cl2);
    List<RdfClass> sup3 = _proxy.getAllSuperClasses(cl3);
    sup1.retainAll(sup2);
    sup1.retainAll(sup3);
    sup1.remove(_proxy.getClass("<tml:Timeline>"));
    assertEquals(1, sup1.size());
  }

  @Test
  public void testInheritedPropertiesOfUnion() {
    RdfClass cl1 = _proxy.getClass("<tml:Food>");
    RdfClass cl2 = _proxy.getClass("<tml:OtherEvent>");
    String propName = "<tml:description>";
    assertNotNull(cl1.fetchProperty("description"));
    assertNotNull(cl2.fetchProperty("description"));
    assertEquals(propName, cl2.fetchProperty("description"));
    assertEquals(cl1.fetchProperty("description"),
        cl2.fetchProperty("description"));
    assertTrue(cl1.getPropertyRange(propName).contains("<xsd:string>"));
    assertEquals(1, cl1.getPropertyRange(propName).size());
  }

}
