package de.dfki.lt.hfc;

import de.dfki.lt.hfc.NamespaceManager.Namespace;
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

    NamespaceManager nsm = NamespaceManager.getInstance();
    nsm.setShortIsDefault(false);
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
    assertNotEquals(nsm, nsm2);
  }

  @Test
  public void putForm() {
    NamespaceManager nsm = NamespaceManager.getInstance();
    nsm.putForm("testNs", "http://www.dfki.de/lt/onto/testNS.owl#", false);
    Namespace ns = nsm.shortToNs.get("testNs");
    assertEquals(ns.getShort(), "testNs");
    assertEquals(ns.getLong(), "http://www.dfki.de/lt/onto/testNS.owl#");
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
  public void separateNSfromURI() {
    String uri1 = "<test:testObject1>";
    String uri2 = "<http://www.dfki.de/lt/onto/test.owl#testObject2>";
    NamespaceManager nsm = NamespaceManager.getInstance();
    assertEquals(nsm.getNamespaceObject("test"), nsm.separateNSfromURI(uri1).first);
    assertEquals(nsm.getNamespaceObject("http://www.dfki.de/lt/onto/test.owl#"), nsm.separateNSfromURI(uri2).first);
  }

  @Test(expected = WrongFormatException.class)
  public void emptyNSIllegal() {
    NamespaceManager nsm = NamespaceManager.getInstance();
    String uri3 = "<testObject3>";
    assertEquals(nsm.getNamespaceObject(""), nsm.separateNSfromURI(uri3).first);
  }

  @Test
  public void getNamespaceObject() {
    String ns_short = "test";
    String ns_long = "http://www.dfki.de/lt/onto/test.owl#";
    NamespaceManager nsm = NamespaceManager.getInstance();
    assertEquals(NamespaceManager.TEST, nsm.getNamespaceObject(ns_short));
    assertEquals(NamespaceManager.TEST, nsm.getNamespaceObject(ns_long));
  }

  @Test
  public void uriWithUnknownNStest() {
    NamespaceManager nsm = NamespaceManager.getInstance();
    String uriString = "<http://what.de/a/silly/domain.owl#StupidObject>";
    Pair<Namespace, String> sep = nsm.separateNSfromURI(uriString);
    Uri uri = new Uri(sep.second, sep.first);
    // check that i can introduce a short form afterwards
    assertTrue(nsm.putForm("silly", "http://what.de/a/silly/domain.owl#", true));
    assertFalse(nsm.putForm("tooSilly", "http://what.de/a/silly/domain.owl#", true));
    assertEquals("<silly:StupidObject>", uri.toString());
    Uri uri2 = new Uri("StupidObject", nsm.getNamespaceObject("owl"));
    assertNotEquals(uri, uri2);
  }

  @Test
  public void uriWithUnknownShortFormtest() {
    NamespaceManager nsm = NamespaceManager.getInstance();
    String uriString = "<unknown:StupidObject>";
    Pair<Namespace, String> sep = nsm.separateNSfromURI(uriString);
    Uri uri = new Uri(sep.second, sep.first);
    // check that i can introduce a short form afterwards
    nsm.putForm("unknown", "http://what.de/an/unknown/domain.owl#", false);
    assertEquals("<http://what.de/an/unknown/domain.owl#StupidObject>", uri.toString());
  }

  @Test
  public void uriWithNamespaceOnlyTest() {
    // unfortunately, there are URIs that have only a namespace, no name.
    // Protege spits them out to specify the ontology itself, and i'm not sure
    // in which other places that might occur.
    NamespaceManager nsm = NamespaceManager.getInstance();
    String uriString = "<http://www.dfki.de/lt/onto/common/dialogue.owl>";
    Pair<Namespace, String> sep = nsm.separateNSfromURI(uriString);
    assertNotNull(sep.first);
    assertTrue(sep.second.isEmpty());
  }

}