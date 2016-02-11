package de.dfki.lt.hfc.types;

import java.lang.reflect.Constructor;
import java.util.HashMap;

import de.dfki.lt.hfc.Namespace;
import de.dfki.lt.hfc.WrongFormatException;

/**
 * NOTE: if further XSD types are added, class Namespace needs to be extended;
 *       same holds for method TupleStore.makeJavaObject()
 *
 * NOTE: I made this class _abstract_, even though there exist the corresponding
 *       XSD type anySimpleType;
 *       not sure what the toString() and toName() implementation for
 *       XsdAnySimpleType should yield
 *
 * @author (C) Hans-Ulrich Krieger
 * @since JDK 1.5
 * @version Wed Fri Jan 29 16:35:17 CET 2016
 */
public abstract class XsdAnySimpleType extends AnyType {

  static {
    typeToConstructor = new HashMap<String, Constructor<XsdAnySimpleType>>();
    loadSimpleTypes();
  }

  /**
   * all (custom) types should be put in package de.dfki.lt.hfc.types
   */
  // last '.' char is OK!
  public static final String TYPE_PATH = "de.dfki.lt.hfc.types.";

  // XSD: string, int, long, float, double, gYear, gYearMonth, gMonth, gMonthDay, gDay, date, dateTime, duration, boolean, anyURI

  protected static final String SHORT_PREFIX = Namespace.XSD_SHORT + ":";
  protected static final String LONG_PREFIX = Namespace.XSD_LONG ;

  public static final String XSD_STRING_SHORT = "<xsd:string>";
  public static final String XSD_STRING_LONG = "<http://www.w3.org/2001/XMLSchema#string>";

  public static final String XSD_INT_SHORT = "<xsd:int>";
  public static final String XSD_INT_LONG = "<http://www.w3.org/2001/XMLSchema#int>";

  public static final String XSD_LONG_SHORT = "<xsd:long>";
  public static final String XSD_LONG_LONG = "<http://www.w3.org/2001/XMLSchema#long>";

  public static final String XSD_FLOAT_SHORT = "<xsd:float>";
  public static final String XSD_FLOAT_LONG = "<http://www.w3.org/2001/XMLSchema#float>";

  public static final String XSD_DOUBLE_SHORT = "<xsd:double>";
  public static final String XSD_DOUBLE_LONG = "<http://www.w3.org/2001/XMLSchema#double>";

  public static final String XSD_GYEAR_SHORT = "<xsd:gYear>";
  public static final String XSD_GYEAR_LONG = "<http://www.w3.org/2001/XMLSchema#gYear>";

  public static final String XSD_GYEARMONTH_SHORT = "<xsd:gYearMonth>";
  public static final String XSD_GYEARMONTH_LONG = "<http://www.w3.org/2001/XMLSchema#gYearMonth>";

  public static final String XSD_GMONTH_SHORT = "<xsd:gMonth>";
  public static final String XSD_GMONTH_LONG = "<http://www.w3.org/2001/XMLSchema#gMonth>";

  public static final String XSD_GMONTHDAY_SHORT = "<xsd:gMonthDay>";
  public static final String XSD_GMONTHDAY_LONG = "<http://www.w3.org/2001/XMLSchema#gMonthDay>";

  public static final String XSD_GDAY_SHORT = "<xsd:gDay>";
  public static final String XSD_GDAY_LONG = "<http://www.w3.org/2001/XMLSchema#gDay>";

  public static final String XSD_DATE_SHORT = "<xsd:date>";
  public static final String XSD_DATE_LONG = "<http://www.w3.org/2001/XMLSchema#date>";

  public static final String XSD_DATETIME_SHORT = "<xsd:dateTime>";
  public static final String XSD_DATETIME_LONG = "<http://www.w3.org/2001/XMLSchema#dateTime>";

  public static final String XSD_DURATION_SHORT = "<xsd:duration>";
  public static final String XSD_DURATION_LONG = "<http://www.w3.org/2001/XMLSchema#duration>";

  public static final String XSD_BOOLEAN_SHORT = "<xsd:boolean>";
  public static final String XSD_BOOLEAN_LONG = "<http://www.w3.org/2001/XMLSchema#boolean>";

  public static final String XSD_ANYURI_SHORT = "<xsd:anyURI>";
  public static final String XSD_ANYURI_LONG = "<http://www.w3.org/2001/XMLSchema#anyURI>";

  // NOT predefined XSD datatypes: uDateTime, monetary

  public static final String XSD_UDATETIME_SHORT = "<xsd:uDateTime>";
  public static final String XSD_UDATETIME_LONG = "<http://www.w3.org/2001/XMLSchema#uDateTime>";

  public static final String XSD_MONETARY_SHORT = "<xsd:monetary>";
  public static final String XSD_MONETARY_LONG = "<http://www.w3.org/2001/XMLSchema#monetary>";

  /**
   * A hash map mapping from string representations of simple xsd types to
   * constructors of subclasses of XsdAnySimpleType, to be able to construct
   * them with the factory method getXsdObject
   */
  private static final
  HashMap<String, Constructor<XsdAnySimpleType>> typeToConstructor;

  // only purpose is to separate the XSD types from uris and blank nodes

  protected static String extractValue(String value) {
    int index = value.lastIndexOf('^');
    return value.substring(1, index - 2);
  }

  /* TODO: find a more clever way to collect the subtypes of XsdAnySimpleType
  private static void loadSimpleTypes() {
    Package p = AnyType.class.getPackage();
    AnyType.class.getClassLoader().getResources(name)
    Class cl = Class.forName(TYPE_PATH + "XsdAnySimpleType");
    ClassLoader.getSystemClassLoader().
  }
  */

  private static void loadSimpleTypes() {
    final String[] simpleXsdClasses = {
        "XsdAnyURI",
        "XsdBoolean",
        "XsdDate",
        "XsdDateTime",
        "XsdDouble",
        "XsdDuration",
        "XsdFloat",
        "XsdGDay",
        "XsdGMonthDay",
        "XsdGMonth",
        "XsdGYear",
        "XsdGYearMonth",
        "XsdInt",
        "XsdLong",
        "XsdMonetary",
        "XsdString",
        "XsdUDateTime"
    };
    for (String type : simpleXsdClasses) {
      try {
        Class.forName(TYPE_PATH + type);
      } catch (ClassNotFoundException e) {
        // Should never happen
        throw new RuntimeException(e);
      }
    }
  }


  /**
   * Register a constructor for a simple xsd type here, so that it can be used
   * by the factory method getXsdObject
   *
   * @param clazz      The class object of some XsdAnySimpleType type
   * @param forms      The short and long form of the type tag
   */
  @SuppressWarnings({ "unchecked", "rawtypes" })
  protected static void registerConstructor(Class clazz, String ... forms) {
    try {
      Constructor<XsdAnySimpleType> constructor =
          clazz.getConstructor(String.class);
      for (String s : forms) typeToConstructor.put(s, constructor);
    }
    catch (Exception e) {  // should never happen
      throw new RuntimeException(e);
    }
  }


  /**
   * A factory method to generate the correct AnyType subclass from the string
   * representation
   * @throws WrongFormatException in case the type is not known, or the
   *         constructor<String> does not exist.
   */
  public static XsdAnySimpleType getXsdObject(String literal)
      throws WrongFormatException {
    int idx = literal.lastIndexOf('^');
    if (idx == -1) {
      // note: parseAtom() completes a bare string by adding "^^<xsd:string>",
      //       but if the string has a language tag, nothing is appended, thus
      //       '^' is missing (as is required by the specification)
      return new XsdString(literal);
    }
    // now do the `clever' dispatch through mapping the type names to Java
    // class constructors:  @see de.dfki.lt.hfc.Namespace.readNamespaces()
    final String type = literal.substring(idx + 1);
    final Constructor<XsdAnySimpleType> constructor = typeToConstructor.get(type);
    if (constructor == null)
      throw new WrongFormatException("unknown atomic type: " + type);

    try {
      return constructor.newInstance(literal);
    }
    catch (Exception e) {
      throw new WrongFormatException(
          "XSD to Java class mapping fails for type " + type, e);
    }
  }


  /**
   * generates a string representation from the internal fields, but omits
   * the XSD type specification, default implementation
   */
  public String toName() {
    return extractValue(toString(false));
  }
}
