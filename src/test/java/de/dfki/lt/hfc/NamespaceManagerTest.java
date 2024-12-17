package de.dfki.lt.hfc;

import static de.dfki.lt.hfc.LiteralManager.*;

import de.dfki.lt.hfc.NamespaceManager.Namespace;
import de.dfki.lt.hfc.types.Uri;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class NamespaceManagerTest {

  @Before
  /**
   * Make sure that the namespaceManager is in its default state when loading
   * the ontology
   */
  public void init(){
    NamespaceManager.clear();
    NamespaceManager.TEST.setIsShort(false);
  }

  @Test
  public void setShortIsDefault() {

    NamespaceManager nsm = new NamespaceManager();
    nsm.setShortIsDefault(false);
    assertFalse(nsm.isShortIsDefault());
    assertFalse(nsm.shortToNs.get("test").isShort());
    Uri testObject = new Uri("testObject", NamespaceManager.TEST);
    assertEquals("<http://www.dfki.de/lt/onto/test.owl#testObject>",
        testObject.toString());
    nsm.setShortIsDefault(true);
    assertTrue(nsm.shortToNs.get("test").isShort());
    assertEquals("<test:testObject>", testObject.toString());
  }

  @Test
  public void getInstance() {
    NamespaceManager nsm = new NamespaceManager();
    assertNotNull(nsm);
    NamespaceManager nsm2 = new NamespaceManager();
    assertNotEquals(nsm, nsm2);
  }

  @Test
  public void putForm() {
    NamespaceManager nsm = new NamespaceManager();
    nsm.putForm("testNs", "http://www.dfki.de/lt/onto/testNS.owl#", false);
    Namespace ns = nsm.shortToNs.get("testNs");
    assertEquals(ns.getShort(), "testNs");
    assertEquals(ns.getLong(), "http://www.dfki.de/lt/onto/testNS.owl#");
  }


  @Test
  public void expandUri() {
    NamespaceManager nsm = new NamespaceManager();
    LiteralManager lsm = new LiteralManager(nsm);
    String uri = "<test:testObject>";
    assertEquals("<http://www.dfki.de/lt/onto/test.owl#testObject>",
        lsm.toExpandedString(uri));
  }


  @Test
  public void copy() {
    NamespaceManager nsm = new NamespaceManager();
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
    assertEquals("test", splitUriNsName(uri1)[0]);
    assertEquals("http://www.dfki.de/lt/onto/test.owl#",
        splitUriNsName(uri2)[0]);
  }

  @Test(expected = WrongFormatException.class)
  public void emptyNSIllegal() {
    String uri3 = "<testObject3>";
    assertEquals(null, new NamespaceManager().getNamespaceObject(
        splitUriNsName(uri3)[0]));
  }

  @Test
  public void getNamespaceObject() {
    String ns_short = "test";
    String ns_long = "http://www.dfki.de/lt/onto/test.owl#";
    NamespaceManager nsm = new NamespaceManager();
    assertEquals(NamespaceManager.TEST, nsm.getNamespaceObject(ns_short));
    assertEquals(NamespaceManager.TEST, nsm.getNamespaceObject(ns_long));
  }

  @Test
  public void uriWithUnknownNStest() {
    NamespaceManager nsm = new NamespaceManager();
    LiteralManager lsm = new LiteralManager(nsm);
    String uriString = "<http://what.de/a/silly/domain.owl#StupidObject>";
    Uri uri = (Uri)lsm.makeAnyType(uriString);
    // check that i can introduce a short form afterwards
    assertTrue(nsm.putForm("silly", "http://what.de/a/silly/domain.owl#", true));
    assertFalse(nsm.putForm("tooSilly", "http://what.de/a/silly/domain.owl#", true));
    assertEquals("<silly:StupidObject>", uri.toString());
    Uri uri2 = new Uri("StupidObject", nsm.getNamespaceObject("owl"));
    assertNotEquals(uri, uri2);
  }

  @Test
  public void uriWithUnknownShortFormtest() {
    NamespaceManager nsm = new NamespaceManager();
    LiteralManager lsm = new LiteralManager(nsm);
    String uriString = "<unknown:StupidObject>";
    Uri uri = (Uri)lsm.makeAnyType(uriString);
    // check that i can introduce a short form afterwards
    nsm.putForm("unknown", "http://what.de/an/unknown/domain.owl#", false);
    assertEquals("<http://what.de/an/unknown/domain.owl#StupidObject>", uri.toString());
  }

  @Test
  public void uriWithNamespaceOnlyTest() {
    // unfortunately, there are URIs that have only a namespace, no name.
    // Protege spits them out to specify the ontology itself, and i'm not sure
    // in which other places that might occur.
    String uriString = "<http://www.dfki.de/lt/onto/common/dialogue.owl>";
    String[] sep = splitUriNsName(uriString);
    assertEquals(uriString.length() - 1, sep[0].length());
    assertEquals("", sep[1]);
  }

}