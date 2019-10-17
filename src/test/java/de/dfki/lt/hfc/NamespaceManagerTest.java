package de.dfki.lt.hfc;

import de.dfki.lt.hfc.types.Uri;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class NamespaceManagerTest {

  @Before
  /**
   * Make sure that the namespaceManager is in its default state when loading the ontology
   */
  public void init(){
    NamespaceManager.clear();
    NamespaceManager.TEST.setIsShort(false);
  }

  @Test
  public void setShortIsDefault() {
    System.out.println(NamespaceManager.instance);
    NamespaceManager nsm = NamespaceManager.getInstance();
    assertFalse(nsm.isShortIsDefault());
    assertFalse(nsm.shortToNs.get("test").isShort());
    Uri testObject = new Uri("testObject", NamespaceManager.TEST);
    assertEquals("<http://www.dfki.de/lt/onto/test.owl#testObject>", testObject.toString());
    nsm.setShortIsDefault(true);
    assertTrue(nsm.shortToNs.get("test").isShort());
    assertEquals("<test:testObject>", testObject.toString());
  }

  @Test
  public void getInstance() {
    NamespaceManager nsm = NamespaceManager.getInstance();
    assertNotNull(nsm);
    NamespaceManager nsm2 = NamespaceManager.getInstance();
    assertEquals(nsm, nsm2);
  }

  @Test
  public void addNamespace() {
    Namespace ns = new Namespace("testNs", "http://www.dfki.de/lt/onto/testNS.owl#", false);
    NamespaceManager nsm = NamespaceManager.getInstance();
    nsm.addNamespace(ns);
    assertEquals(ns, nsm.shortToNs.get("testNs"));
    assertEquals(ns, nsm.longToNs.get("http://www.dfki.de/lt/onto/testNS.owl#"));
  }

  @Test
  public void putForm() {
    NamespaceManager nsm = NamespaceManager.getInstance();
    nsm.putForm("testNs", "http://www.dfki.de/lt/onto/testNS.owl#", false);
    Namespace ns = nsm.shortToNs.get("testNs");
    assertEquals(ns.SHORT_NAMESPACE, "testNs");
    assertEquals(ns.LONG_NAMESPACE, "http://www.dfki.de/lt/onto/testNS.owl#");
  }


  @Test
  public void expandUri() {
    NamespaceManager nsm = NamespaceManager.getInstance();
    String uri = "<test:testObject>";
    assertEquals("<http://www.dfki.de/lt/onto/test.owl#testObject>", nsm.expandUri(uri));
  }


  @Test
  public void copy() {
    NamespaceManager nsm = NamespaceManager.getInstance();
    NamespaceManager copy = nsm.copy();
    assertNotEquals(copy, nsm);
    assertEquals(nsm.longToNs, copy.longToNs);
    assertEquals(nsm.shortToNs,copy.shortToNs);
    assertEquals(nsm.isShortIsDefault(), copy.isShortIsDefault());
  }

  @Test
  public void seperateNSfromURI() {
    String uri1 = "<test:testObject1>";
    String uri2 = "<http://www.dfki.de/lt/onto/test.owl#testObject2>";
    String uri3 = "<testObject3>";
    NamespaceManager nsm = NamespaceManager.getInstance();
    assertEquals("test", nsm.separateNSfromURI(uri1)[0]);
    assertEquals("http://www.dfki.de/lt/onto/test.owl#", nsm.separateNSfromURI(uri2)[0]);
    assertEquals("", nsm.separateNSfromURI(uri3)[0]);
  }

  @Test
  public void getNamespaceObject() {
    String ns_short = "test";
    String ns_long = "http://www.dfki.de/lt/onto/test.owl#";
    //String ns_invalid = "foo";
    NamespaceManager nsm = NamespaceManager.getInstance();
    assertEquals(NamespaceManager.TEST, nsm.getNamespaceObject(ns_short));
    assertEquals(NamespaceManager.TEST, nsm.getNamespaceObject(ns_long));
    //assertEquals(NamespaceManager.TEST, nsm.getNamespaceObject(ns_invalid));

  }
}