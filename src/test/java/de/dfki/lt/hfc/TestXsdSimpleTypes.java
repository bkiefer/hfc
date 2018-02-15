package de.dfki.lt.hfc;

import de.dfki.lt.hfc.types.*;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.URISyntaxException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;


public class TestXsdSimpleTypes {

  @Test
  public void testDouble() {
    String ds = "\"100.1\"^^<xsd:double>";

    assertEquals((double)100.1, new XsdDouble(ds).value, 0.001);

    assertEquals(new Double(100.1), new XsdDouble(ds).toJava());

    assertEquals(ds, new XsdDouble(ds).toString(true));

    assertEquals("100.1", new XsdDouble(ds).toName());

    assertEquals((double)100.1, new XsdDouble(100.1).value, 0.001);

    assertEquals(new Double(100.1), new XsdDouble(100.1).toJava());

    assertEquals(ds, new XsdDouble(100.1).toString(true));

    assertEquals("100.1", new XsdDouble(100.1).toName());

    assertEquals("\"100.1\"^^<http://www.w3.org/2001/XMLSchema#double>",
        new XsdDouble(100.1).toString(false));
  }

  @Test
  public void testAnyURI() throws URISyntaxException {
    XsdAnyURI u1 = new XsdAnyURI("\"rdf:type\"^^<xsd:anyURI>");
    assertEquals("\"rdf:type\"^^<xsd:anyURI>", u1.toString(true));

    XsdAnyURI u2 = new XsdAnyURI(
        "\"http://www.w3.org/1999/02/22-rdf-syntax-ns#type\"^^<xsd:anyURI>");
    assertEquals("http://www.w3.org/1999/02/22-rdf-syntax-ns#type",
        u2.toName());
    assertEquals(new URI("http://www.w3.org/1999/02/22-rdf-syntax-ns#type"),
        u2.toJava());
    assertEquals(
        "\"http://www.w3.org/1999/02/22-rdf-syntax-ns#type\"^^<xsd:anyURI>",
        u2.toString(true));
    assertEquals(
        "\"http://www.w3.org/1999/02/22-rdf-syntax-ns#type\""
        + "^^<http://www.w3.org/2001/XMLSchema#anyURI>",
        u2.toString(false));

    // not compliant with URI syntax; return: null
    XsdAnyURI u3 = new XsdAnyURI("\"<rdf:type>\"^^<xsd:anyURI>");
    assertNull(u3.toJava());
  }

  @Test
  public void testBoolean() {
    assertEquals("\"true\"^^<xsd:boolean>", new XsdBoolean(true).toString(true));
    assertEquals("true",
        new XsdBoolean("\"true\"^^<xsd:boolean>").toName());
  }

  @Test
  public void testDate() {
    XsdDate xt = new XsdDate("\"-12000-03-04\"^^<xsd:date>");
    assertEquals(12000, xt.year);
    assertEquals(3, xt.month);
    assertEquals(4, xt.day);
    assertEquals("\"-12000-03-04\"^^<xsd:date>", xt.toString(true));
    assertEquals("\"-12000-03-04\"^^<http://www.w3.org/2001/XMLSchema#date>", xt.toString(false));

    xt = new XsdDate(2009, 1, 12);
    assertEquals("\"2009-01-12\"^^<xsd:date>", xt.toString(true));
    assertEquals("2009-01-12", xt.toName());
    assertEquals("\"2009-01-12\"^^<http://www.w3.org/2001/XMLSchema#date>", xt.toString(false));
  }

  @Test
  public void testDateTime() {
    XsdDateTime xt = new XsdDateTime("\"-12000-03-04T23:00:01.123\"^^<xsd:dateTime>");
    assertEquals(12000, xt.year);
    assertEquals(23, xt.hour);
    assertEquals(1.123, xt.second, 0.001);
    assertEquals("\"-12000-03-04T23:00:01.123\"^^<xsd:dateTime>", xt.toString(true));
    assertEquals(
        "\"-12000-03-04T23:00:01.123\"^^<http://www.w3.org/2001/XMLSchema#dateTime>",
        xt.toString(false));

    xt = new XsdDateTime(false, 2009, 1, 12, 1, 0, 3.456F);
    assertEquals("\"-2009-01-12T01:00:03.456\"^^<xsd:dateTime>", xt.toString(true));
    assertEquals("-2009-01-12T01:00:03.456", xt.toName());
    assertEquals("\"-2009-01-12T01:00:03.456\"^^<http://www.w3.org/2001/XMLSchema#dateTime>", xt.toString(false));
  }

  @Test
  public void testDuration() {
    XsdDuration xt = new XsdDuration("\"-P12000Y0M04D\"^^<xsd:duration>");
    assertEquals(12000, xt.year);
    assertEquals(0, xt.hour);
    assertEquals(0.0, xt.second, 0.0001);
    assertEquals("\"-P12000Y4D\"^^<xsd:duration>", xt.toString(true));
    assertEquals("\"-P12000Y4D\"^^<http://www.w3.org/2001/XMLSchema#duration>",
        xt.toString(false));

    xt = new XsdDuration(false, 2009, 1, 12, 1, 0, 3.456F);
    assertEquals("\"-P2009Y1M12D1H3.456S\"^^<xsd:duration>", xt.toString(true));
    assertEquals("-P2009Y1M12D1H3.456S", xt.toName());
    assertEquals(
        "\"-P2009Y1M12D1H3.456S\"^^<http://www.w3.org/2001/XMLSchema#duration>",
        xt.toString(false));
  }

  @Test
  public void testFloat() {
    XsdFloat xf = new XsdFloat("\"3.1415\"^^<xsd:float>");
    assertEquals(3.1415, xf.value, 0.001);
    assertEquals("\"3.1415\"^^<xsd:float>", xf.toString(true));
    assertEquals("\"3.1415\"^^<http://www.w3.org/2001/XMLSchema#float>", xf.toString(false));

    xf = new XsdFloat(2.71828f);
    assertEquals(2.71828, xf.value, 0.001);
    assertEquals("\"2.71828\"^^<xsd:float>", xf.toString(true));
    assertEquals("\"2.71828\"^^<http://www.w3.org/2001/XMLSchema#float>", xf.toString(false));
  }

  @Test
  public void testGDay() {
    XsdGDay xt = new XsdGDay("\"---13\"^^<xsd:gDay>");
    assertEquals(13, xt.day);
    assertEquals("\"---13\"^^<http://www.w3.org/2001/XMLSchema#gDay>",
        xt.toString(false));
    assertEquals("\"---13\"^^<xsd:gDay>", xt.toString(true));

    xt = new XsdGDay(1);
    assertEquals("---01", xt.toName());
    assertEquals("\"---01\"^^<xsd:gDay>", xt.toString(true));
    assertEquals("\"---01\"^^<http://www.w3.org/2001/XMLSchema#gDay>",
        xt.toString(false));
  }

  @Test
  public void testGMonth() {
    XsdGMonth xt = new XsdGMonth("\"--03\"^^<xsd:gMonth>");
    assertEquals(3, xt.month);
    assertEquals("\"--03\"^^<xsd:gMonth>", xt.toString(true));
    assertEquals("\"--03\"^^<http://www.w3.org/2001/XMLSchema#gMonth>",
        xt.toString(false));

    xt = new XsdGMonth(1);
    assertEquals("\"--01\"^^<xsd:gMonth>", xt.toString(true));
    assertEquals("--01", xt.toName());
    assertEquals("\"--01\"^^<http://www.w3.org/2001/XMLSchema#gMonth>",
        xt.toString(false));
  }

  @Test
  public void testGMonthDay() {
    XsdGMonthDay xt = new XsdGMonthDay("\"--03-04\"^^<xsd:gMonthDay>");
    assertEquals(3, xt.month);
    assertEquals(4, xt.day);
    assertEquals("\"--03-04\"^^<xsd:gMonthDay>", xt.toString(true));
    assertEquals("\"--03-04\"^^<http://www.w3.org/2001/XMLSchema#gMonthDay>",
        xt.toString(false));

    xt = new XsdGMonthDay(1, 12);
    assertEquals("--01-12", xt.toName());
    assertEquals("\"--01-12\"^^<xsd:gMonthDay>", xt.toString(true));
    assertEquals("\"--01-12\"^^<http://www.w3.org/2001/XMLSchema#gMonthDay>",
        xt.toString(false));
  }

  @Test
  public void testGYear() {
    XsdGYear xt = new XsdGYear("\"2000\"^^<xsd:gYear>");
    assertEquals(2000, xt.year);
    assertEquals("\"2000\"^^<xsd:gYear>", xt.toString(true));
    assertEquals("\"2000\"^^<http://www.w3.org/2001/XMLSchema#gYear>",
        xt.toString(false));

    xt = new XsdGYear("\"-19999\"^^<xsd:gYear>");
    assertEquals(19999, xt.year);
    assertEquals("\"-19999\"^^<xsd:gYear>", xt.toString(true));
    assertEquals("\"-19999\"^^<http://www.w3.org/2001/XMLSchema#gYear>",
        xt.toString(false));

    xt = new XsdGYear(2009);
    assertEquals("2009", xt.toName());
    assertEquals("\"2009\"^^<xsd:gYear>", xt.toString(true));
    assertEquals("\"2009\"^^<http://www.w3.org/2001/XMLSchema#gYear>",
        xt.toString(false));
  }

  @Test
  public void testGYearMonth() {
    XsdGYearMonth xt = new XsdGYearMonth("\"-12000-03\"^^<xsd:gYearMonth>");
    assertEquals(12000, xt.year);
    assertEquals(3, xt.month);
    assertEquals("\"-12000-03\"^^<xsd:gYearMonth>", xt.toString(true));
    assertEquals("\"-12000-03\"^^<http://www.w3.org/2001/XMLSchema#gYearMonth>",
        xt.toString(false));

    xt = new XsdGYearMonth(2009, 1);
    assertEquals("2009-01", xt.toName());
    assertEquals("\"2009-01\"^^<xsd:gYearMonth>", xt.toString(true));
    assertEquals("\"2009-01\"^^<http://www.w3.org/2001/XMLSchema#gYearMonth>",
        xt.toString(false));
  }

  @Test
  public void testInt() {
    XsdInt xi = new XsdInt("\"42\"^^<xsd:int>");
    assertEquals(42, xi.value);
    assertEquals("\"42\"^^<xsd:int>", xi.toString(true));
    assertEquals("\"42\"^^<http://www.w3.org/2001/XMLSchema#int>",
        xi.toString(false));

    xi = new XsdInt("\"-42\"^^<xsd:int>");
    assertEquals(-42, xi.value);
    assertEquals("\"-42\"^^<xsd:int>", xi.toString(true));
    assertEquals("\"-42\"^^<http://www.w3.org/2001/XMLSchema#int>",
        xi.toString(false));

    xi = new XsdInt(2);
    assertEquals(2, xi.value);
    assertEquals("\"2\"^^<xsd:int>", xi.toString(true));
    assertEquals("\"2\"^^<http://www.w3.org/2001/XMLSchema#int>",
        xi.toString(false));
  }

  @Test
  public void testLong() {
    XsdLong xi = new XsdLong("\"42\"^^<xsd:long>");
    assertEquals(42, xi.value);
    assertEquals("\"42\"^^<xsd:long>", xi.toString(true));
    assertEquals("\"42\"^^<http://www.w3.org/2001/XMLSchema#long>",
        xi.toString(false));

    xi = new XsdLong("\"-42\"^^<xsd:long>");
    assertEquals(-42, xi.value);
    assertEquals("\"-42\"^^<xsd:long>", xi.toString(true));
    assertEquals("\"-42\"^^<http://www.w3.org/2001/XMLSchema#long>",
        xi.toString(false));

    xi = new XsdLong(2);
    assertEquals(2, xi.value);
    assertEquals("\"2\"^^<xsd:long>", xi.toString(true));
    assertEquals("\"2\"^^<http://www.w3.org/2001/XMLSchema#long>",
        xi.toString(false));
  }

  @Test
  public void testMonetary() {
    XsdMonetary mon = new XsdMonetary("\"42USD\"^^<xsd:monetary>");
    assertEquals(42.0, mon.amount, 0.001);
    assertEquals("USD", mon.currency);
    assertEquals("\"42.0USD\"^^<xsd:monetary>", mon.toString(true));
    assertEquals("\"42.0USD\"^^<http://www.w3.org/2001/XMLSchema#monetary>",
        mon.toString(false));

    mon = new XsdMonetary(4.31, "EUR");
    assertEquals("4.31EUR", mon.toName());
    assertEquals("\"4.31EUR\"^^<xsd:monetary>", mon.toString(true));
    assertEquals("\"4.31EUR\"^^<http://www.w3.org/2001/XMLSchema#monetary>",
        mon.toString(false));

  }

  @Test
  public void testUDateTime() {
    XsdUDateTime xt = new XsdUDateTime("\"2000-??-04T??:??:??.???\"^^<xsd:uDateTime>");
    assertEquals(-1, xt.hour);
    assertEquals(-1.0, xt.second, 0.001);
    assertEquals("\"2000-??-04T??:??:??.???\"^^<xsd:uDateTime>", xt.toString(true));
    assertEquals("\"2000-??-04T??:??:??.???\"^^<http://www.w3.org/2001/XMLSchema#uDateTime>",
        xt.toString(false));

    // an instance within the 12th Jan 2009
    xt = new XsdUDateTime(2009, 1, 12, -1, -1, -1F);
    assertEquals("\"2009-01-12T??:??:??.???\"^^<xsd:uDateTime>", xt.toString(true));
    assertEquals("\"2009-01-12T??:??:??.???\"^^<http://www.w3.org/2001/XMLSchema#uDateTime>",
        xt.toString(false));
  }

  @Test
  public void testString() {
    XsdString xs = new XsdString("\"hel <lo>\"^^<xsd:string>");
    assertEquals("hel__lo_", xs.toName());

    xs = new XsdString("\"hello\"^^<xsd:string>");
    assertEquals("hello", xs.value);
    assertNull(xs.languageTag);
    assertEquals("\"hello\"^^<xsd:string>", xs.toString(true));
    assertEquals("\"hello\"^^<http://www.w3.org/2001/XMLSchema#string>",
        xs.toString(false));

    xs = new XsdString("\"hello\"");
    assertEquals("hello", xs.value);
    assertNull(xs.languageTag);
    assertEquals("\"hello\"^^<xsd:string>", xs.toString(true));
    assertEquals("\"hello\"^^<http://www.w3.org/2001/XMLSchema#string>",
        xs.toString(false));

    xs = new XsdString("\"hello\"@en");
    assertEquals("hello", xs.value);
    assertEquals("en", xs.languageTag);
    assertEquals("\"hello\"@en", xs.toString(true));
    assertEquals("\"hello\"@en", xs.toString(false));

    xs = new XsdString("hello", null);
    assertEquals("hello", xs.value);
    assertNull(xs.languageTag);
    assertEquals("\"hello\"^^<xsd:string>", xs.toString(true));
    assertEquals("\"hello\"^^<http://www.w3.org/2001/XMLSchema#string>",
        xs.toString(false));

    xs = new XsdString("hello", "en");
    assertEquals("hello", xs.value);
    assertEquals("en", xs.languageTag);
    assertEquals("\"hello\"@en", xs.toString(true));
    assertEquals("\"hello\"@en", xs.toString(false));
  }

  @Test
  public void testFactory() throws WrongFormatException {
    assertEquals(new XsdAnyURI("\"rdf:type\"^^<xsd:anyURI>").toString(false),
        XsdAnySimpleType.getXsdObject("\"rdf:type\"^^<xsd:anyURI>").toString(false));
    assertEquals(new XsdBoolean("\"true\"^^<xsd:boolean>").toString(false),
        XsdAnySimpleType.getXsdObject("\"true\"^^<xsd:boolean>").toString(false));
    assertEquals(new XsdDate("\"-12000-03-04\"^^<xsd:date>").toString(false),
        XsdAnySimpleType.getXsdObject("\"-12000-03-04\"^^<xsd:date>").toString(false));
    assertEquals(new XsdDateTime("\"-12000-03-04T23:00:01.123\"^^<xsd:dateTime>").toString(false),
        XsdAnySimpleType.getXsdObject("\"-12000-03-04T23:00:01.123\"^^<xsd:dateTime>").toString(false));
    assertEquals(new XsdDouble("\"100.1\"^^<xsd:double>").toString(false),
        XsdAnySimpleType.getXsdObject("\"100.1\"^^<xsd:double>").toString(false));
    assertEquals(new XsdDuration("\"-P12000Y0M04D\"^^<xsd:duration>").toString(false),
        XsdAnySimpleType.getXsdObject("\"-P12000Y0M04D\"^^<xsd:duration>").toString(false));
    assertEquals(new XsdFloat("\"3.1415\"^^<xsd:float>").toString(false),
        XsdAnySimpleType.getXsdObject("\"3.1415\"^^<xsd:float>").toString(false));
    assertEquals(new XsdGDay("\"---13\"^^<xsd:gDay>").toString(false),
        XsdAnySimpleType.getXsdObject("\"---13\"^^<xsd:gDay>").toString(false));
    assertEquals(new XsdGMonth("\"--03\"^^<xsd:gMonth>").toString(false),
        XsdAnySimpleType.getXsdObject("\"--03\"^^<xsd:gMonth>").toString(false));
    assertEquals(new XsdGMonthDay("\"--03-04\"^^<xsd:gMonthDay>").toString(false),
        XsdAnySimpleType.getXsdObject("\"--03-04\"^^<xsd:gMonthDay>").toString(false));
    assertEquals(new XsdGYear("\"2000\"^^<xsd:gYear>").toString(false),
        XsdAnySimpleType.getXsdObject("\"2000\"^^<xsd:gYear>").toString(false));
    assertEquals(new XsdGYearMonth("\"-12000-03\"^^<xsd:gYearMonth>").toString(false),
        XsdAnySimpleType.getXsdObject("\"-12000-03\"^^<xsd:gYearMonth>").toString(false));
    assertEquals(new XsdInt("\"42\"^^<xsd:int>").toString(false),
        XsdAnySimpleType.getXsdObject("\"42\"^^<xsd:int>").toString(false));
    assertEquals(new XsdLong("\"42\"^^<xsd:long>").toString(false),
        XsdAnySimpleType.getXsdObject("\"42\"^^<xsd:long>").toString(false));
    assertEquals(new XsdMonetary("\"42USD\"^^<xsd:monetary>").toString(false),
        XsdAnySimpleType.getXsdObject("\"42USD\"^^<xsd:monetary>").toString(false));
    assertEquals(
        new XsdString("\"hello\"^^<http://www.w3.org/2001/XMLSchema#string>").toString(false),
        XsdAnySimpleType.getXsdObject(
            "\"hello\"^^<http://www.w3.org/2001/XMLSchema#string>").toString(false));
    assertEquals(new XsdUDateTime("\"2000-??-04T??:??:??.???\"^^<xsd:uDateTime>").toString(false),
        XsdAnySimpleType.getXsdObject("\"2000-??-04T??:??:??.???\"^^<xsd:uDateTime>").toString(false));
  }


  @Test
  public void testFactory2() throws WrongFormatException {
    assertEquals(XsdAnyURI.class,
        XsdAnySimpleType.getXsdObject("\"rdf:type\"^^<xsd:anyURI>").getClass());
    assertEquals(XsdBoolean.class,
        XsdAnySimpleType.getXsdObject("\"true\"^^<xsd:boolean>").getClass());
    assertEquals(XsdDate.class,
        XsdAnySimpleType.getXsdObject("\"-12000-03-04\"^^<xsd:date>").getClass());
    assertEquals(XsdDateTime.class,
        XsdAnySimpleType.getXsdObject("\"-12000-03-04T23:00:01.123\"^^<xsd:dateTime>").getClass());
    assertEquals(XsdDouble.class,
        XsdAnySimpleType.getXsdObject("\"100.1\"^^<xsd:double>").getClass());
    assertEquals(XsdDuration.class,
        XsdAnySimpleType.getXsdObject("\"-P12000Y0M04D\"^^<xsd:duration>").getClass());
    assertEquals(XsdFloat.class,
        XsdAnySimpleType.getXsdObject("\"3.1415\"^^<xsd:float>").getClass());
    assertEquals(XsdGDay.class,
        XsdAnySimpleType.getXsdObject("\"---13\"^^<xsd:gDay>").getClass());
    assertEquals(XsdGMonth.class,
        XsdAnySimpleType.getXsdObject("\"--03\"^^<xsd:gMonth>").getClass());
    assertEquals(XsdGMonthDay.class,
        XsdAnySimpleType.getXsdObject("\"--03-04\"^^<xsd:gMonthDay>").getClass());
    assertEquals(XsdGYear.class,
        XsdAnySimpleType.getXsdObject("\"2000\"^^<xsd:gYear>").getClass());
    assertEquals(XsdGYearMonth.class,
        XsdAnySimpleType.getXsdObject("\"-12000-03\"^^<xsd:gYearMonth>").getClass());
    assertEquals(XsdInt.class,
        XsdAnySimpleType.getXsdObject("\"42\"^^<xsd:int>").getClass());
    assertEquals(XsdLong.class,
        XsdAnySimpleType.getXsdObject("\"42\"^^<xsd:long>").getClass());
    assertEquals(XsdMonetary.class,
        XsdAnySimpleType.getXsdObject("\"42USD\"^^<xsd:monetary>").getClass());
    assertEquals( XsdString.class,
        XsdAnySimpleType.getXsdObject(
            "\"hello\"^^<http://www.w3.org/2001/XMLSchema#string>").getClass());
    assertEquals(XsdUDateTime.class,
        XsdAnySimpleType.getXsdObject("\"2000-??-04T??:??:??.???\"^^<xsd:uDateTime>").getClass());
  }

}
