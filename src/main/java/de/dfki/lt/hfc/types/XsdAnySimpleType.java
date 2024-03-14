package de.dfki.lt.hfc.types;

import de.dfki.lt.hfc.NamespaceManager;
import de.dfki.lt.hfc.NamespaceManager.Namespace;
import de.dfki.lt.hfc.WrongFormatException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.util.HashMap;

/**
 * NOTE: if further XSD types are added, class NamespaceManager needs to be extended;
 * same holds for method TupleStore.makeJavaObject()
 * <p>
 * NOTE: I made this class _abstract_, even though there exist the corresponding
 * XSD type anySimpleType;
 * not sure what the toString() and toName() implementation for
 * XsdAnySimpleType should yield
 *
 * @author (C) Hans-Ulrich Krieger
 * @version Wed Fri Jan 29 16:35:17 CET 2016
 * @since JDK 1.5
 */
@SuppressWarnings("rawtypes")
public abstract class XsdAnySimpleType extends AnyType {

  /**
   * all (custom) types should be put in package de.dfki.lt.hfc.types
   */
  // last '.' char is OK!
  public static final String TYPE_PATH = "de.dfki.lt.hfc.types.";
  public static final Namespace NS = NamespaceManager.XSD;

  // XSD: string, int, long, float, double, gYear, gYearMonth, gMonth, gMonthDay, gDay, date, dateTime, duration, boolean, anyURI
  /**
   * A basic LOGGER.
   */
  private static final Logger logger = LoggerFactory.getLogger(XsdAnySimpleType.class);
  /**
   * A hash map mapping from string representations of simple xsd types to
   * constructors of subclasses of XsdAnySimpleType, to be able to construct
   * them with the factory method getXsdObject
   */
  private static final
  HashMap<String, Constructor<XsdAnySimpleType>> typeToConstructor;
  /**
   * A hash map mapping from Java classes to constructors of subclasses of
   * XsdAnySimpleType, to be able to construct xsd objects from java objects
   * with the factory method javaToXsd
   */
  private static final
  HashMap<Class, Constructor<XsdAnySimpleType>> classToConstructor;

  static {
    typeToConstructor = new HashMap<String, Constructor<XsdAnySimpleType>>();
    classToConstructor = new HashMap<Class, Constructor<XsdAnySimpleType>>();
    loadSimpleTypes();
  }

  public XsdAnySimpleType() {
    super(NS);
  }

  // only purpose is to separate the XSD types from uris and blank nodes

  protected static String extractValue(String value) {
    int index = value.lastIndexOf('^');
    return value.substring(1, index - 2);
  }



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
            "XsdString",
            // new XSD datatypes
            "XsdUDateTime",
            "XsdMonetary",
            "XsdGm",
            "XsdKg",
            "XsdCal",
            "XsdKg_m2",
            "XsdMmHg",
            "XsdMmol_L",
            "XsdMg_dL",
            "XsdCm",
            "XsdM",
            "Xsd1_min",
            "XsdIu",
            // new Types - Christian
            "Xsd2DPoint",
            "Xsd3DPoint",
            "XsdDecimal"
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
   * Register the Java class for a simple xsd type here, so that it can be used
   * by the factory method getXsdObject
   *
   * @param clazz The class object of some XsdAnySimpleType type
   * @param forms The short and long form of the type tag
   */
  @SuppressWarnings({"unchecked"})
  protected static void registerXsdClass(Class clazz, String... names) {
    try {
      Constructor<XsdAnySimpleType> constructor =
              clazz.getConstructor(String.class);
      for (String name : names) {
        String shortname = '<' + NS.getShort() + ":" + name + '>';
        String longname = '<' + NS.getLong() + name + '>';
        typeToConstructor.put(shortname, constructor);
        typeToConstructor.put(longname, constructor);
      }
    } catch (Exception e) {  // should never happen
      throw new RuntimeException(e);
    }
  }
  
  /**
   * Register a constructor for a simple xsd type here, so that it can be used
   * by the factory method getXsdObject
   *
   * @param clazz The class object of some XsdAnySimpleType type
   * @param forms The short and long form of the type tag
   */
  @SuppressWarnings({"unchecked"})
  protected static void registerConstructor(Class clazz, String... forms) {
    try {
      Constructor<XsdAnySimpleType> constructor =
              clazz.getConstructor(String.class);
      for (String s : forms)
        typeToConstructor.put(s, constructor);
    } catch (Exception e) {  // should never happen
      throw new RuntimeException(e);
    }
  }

  public static boolean isSimpleXsdType(String clazz) {
    return typeToConstructor.containsKey(clazz);
  }
  

  /**
   * A factory method to generate the correct AnyType subclass from the string
   * representation
   *
   * @throws WrongFormatException in case the type is not known, or the
   *                              constructor<String> does not exist.
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
    // class constructors:  @see de.dfki.lt.hfc.NamespaceManager.readNamespaces()
    final String type = literal.substring(idx + 1);
    final Constructor<XsdAnySimpleType> constructor = typeToConstructor.get(type);
    if (constructor == null)
      throw new WrongFormatException("unknown atomic type: " + type);

    try {
      return constructor.newInstance(literal);
    } catch (Exception e) {
      throw new WrongFormatException(
              "XSD to Java class mapping fails for type " + type, e);
    }
  }

  /**
   * Register a converter from a java class to a Xsd simple type class
   */
  @SuppressWarnings("unchecked")
  public static void registerConverter(Class clazz, Class xsdClass) {
    try {
      Constructor<XsdAnySimpleType> constr = xsdClass.getConstructor(clazz);
      classToConstructor.put(clazz, constr);
    } catch (Exception e) {  // should never happen
      throw new RuntimeException(e);
    }
  }

  /**
   * A factory method to generate the correct AnyType subclass from the given
   * java object
   *
   * @throws WrongFormatException in case the type is not known, or the
   *                              constructor<Class> does not exist.
   */
  public static XsdAnySimpleType javaToXsd(Object obj)
          throws WrongFormatException {
    final Constructor<XsdAnySimpleType> constructor =
            classToConstructor.get(obj.getClass());
    if (constructor == null)
      throw new WrongFormatException("no known converter for object: " + obj);

    try {
      return constructor.newInstance(obj);
    } catch (Exception e) {
      throw new WrongFormatException(
              "Java to Xsd class mapping fails for object " + obj, e);
    }
  }

  /**
   * generates a string representation from the internal fields, but omits
   * the XSD type specification, default implementation
   */
  @Override
  public String toName() {
    return extractValue(toString());
  }


}
