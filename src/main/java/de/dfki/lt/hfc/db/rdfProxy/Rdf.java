/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.dfki.lt.hfc.db.rdfProxy;

import static de.dfki.lt.hfc.db.HfcDbHandler.VALUE_INVALID;
import static de.dfki.lt.hfc.db.rdfProxy.RdfProxy.logger;
import static de.dfki.lt.hfc.db.rdfProxy.RdfProxy.objectToUri;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.dfki.lt.hfc.db.QueryException;
import de.dfki.lt.hfc.db.TupleException;
import de.dfki.lt.hfc.types.XsdString;

/**
 *
 * @author Christophe Biwer, christophe.biwer@dfki.de
 * @author Bernd Kiefer, kiefer@dfki.de
 */
public class Rdf {

  public static final String RDFS_LABEL = "<rdfs:label>";

  // URI of the object
  private final String _uri;

  // object of what class?
  private final RdfClass _clazz;

  // What value has a propriety inside an object in hfc?
  // Rdf or xsd, but realized as Object
  private final Map<String, RdfSet<Object>> _fields;

  // The connection to the RDF database
  protected final RdfProxy _proxy;

  /**
   * Link an existing URI to a proxy instance
   * should be as minimalistic as possible
   *
   * @param uri the uri (must exist in the DB)
   * @param clazz RdfClass of given Rdf
   * @param proxy
   */
  protected Rdf(String uri, RdfClass clazz, RdfProxy proxy) {
    _proxy = proxy;
    _uri = uri;
    _fields = new HashMap<>();  // lazyfetch
    _clazz = clazz;
  }

  private DbClient getClient() {
    return _proxy._client;
  }



  /**
   * in case that the RdfObject does not contain information about a certain
   * predicate
   *
   * @param predicate: get this value from the database.
   */
  private void getFromDatabase(String predicate) {
    try {
      // check with class if this predicate is legal
      int predicateType = _clazz.getPropertyType(predicate);
      if (predicateType == 0) {
        logger.warn("predicate {} not defined for {}", predicate, _uri);
      }

      // second, get value with given predicate.
      Set<String> values = new HashSet<>();
      if ((predicateType & RdfClass.FUNCTIONAL_PROPERTY ) == 0) {
        values = getClient().getMultiValue(_uri, predicate);
        values.remove(VALUE_INVALID);
      } else {
        String value = getClient().getValue(_uri, predicate);
        if (!VALUE_INVALID.equals(value)) {
          values.add(value);
        }
      }
      RdfSet<Object> set = new RdfSet<>(this, predicate);

      // values could be xsds or Rdfs. Therefore they need to be transformed.
      for (String obtained : values) {
        set.addInternal(_proxy.uriToObject(obtained));
      }
      _fields.put(predicate, set);
    } catch (Exception ex) {
      throw new RuntimeException(ex);
    }
  }


  /**
   * in case of accessing data: get data from database can be either a Set of
   * URIs or xsds
   *
   * @param predicate
   * @return a Set of Objects, the values under the predicate, possibly empty
   */
  public Set<Object> getValue(String predicate) {
    if (_fields.get(predicate) == null) {
      getFromDatabase(predicate);
    }
    return _fields.get(predicate);
  }


  /** Set the value under predicate to the given URI (as string) */
  public void setUri(String predicate, String uri)  {
    try {
      getClient().setValue(_uri, predicate, uri);
    } catch (TupleException e) {
      throw new RuntimeException(e);
    }
  }


  /**
   * Get the value for a functional predicate, or null if there is no value
   *
   * @param predicate
   */
  public Object getSingleValue(String predicate) {
    if (! _clazz.isFunctionalProperty(predicate))
      logger.warn("Apply getSingleValue for {} to non-functional property {}",
          _uri, predicate);
    Set<Object> result = getValue(predicate);
    if (result.size() > 1) {
      logger.warn("getSingleValue called for {} on {} but more than one value",
          _uri, predicate);
    }
    if (result.isEmpty()) {
      return null;
    }
    return result.iterator().next();
  }

  public String getString(String p) { return (String)getSingleValue(p); }
  public Byte getByte(String p) { return (Byte)getSingleValue(p); }
  public Short getShort(String p) { return (Short)getSingleValue(p); }
  public Integer getInteger(String p) { return (Integer)getSingleValue(p); }
  public Long getLong(String p) { return (Long)getSingleValue(p); }
  public Float getFloat(String p) { return (Float)getSingleValue(p); }
  public Double getDouble(String p) { return (Double)getSingleValue(p); }
  public Boolean getBoolean(String p) { return (Boolean)getSingleValue(p); }
  public Character getCharacter(String p) { return (Character)getSingleValue(p); }

  // preincrement, e.g., ++a
  public Byte incrByte(String p) {
    Byte b = getByte(p); return setValue(p, (byte)(b+1));
  }
  public Short incrShort(String p) {
    Short b = getShort(p); return setValue(p, (short)(b+1));
  }
  public Integer incrInteger(String p) {
    Integer b = getInteger(p); return setValue(p, b+1);
  }
  public Long incrLong(String p) {
    Long b = getLong(p); return setValue(p, b+1);
  }
  public Float incrFloat(String p) {
    Float b = getFloat(p); return setValue(p, b+1);
  }
  public Double incrDouble(String p) {
    Double b = getDouble(p); return setValue(p, b+1);
  }

  // postincrement, e.g., a++
  public Byte pincrByte(String p) {
    Byte b = getByte(p); setValue(p, b+1); return b;
  }
  public Short pincrShort(String p) {
    Short b = getShort(p); setValue(p, b+1); return b;
  }
  public Integer pincrInteger(String p) {
    Integer b = getInteger(p); setValue(p, b+1); return b;
  }
  public Long pincrLong(String p) {
    Long b = getLong(p); setValue(p, b+1); return b;
  }
  public Float pincrFloat(String p) {
    Float b = getFloat(p); setValue(p, b+1); return b;
  }
  public Double pincrDouble(String p) {
    Double b = getDouble(p); setValue(p, b+1); return b;
  }


  // predecrement, e.g., --a
  public Byte decrByte(String p) {
    Byte b = getByte(p); return setValue(p, (byte)(b-1));
  }
  public Short decrShort(String p) {
    Short b = getShort(p); return setValue(p, (short)(b-1));
  }
  public Integer decrInteger(String p) {
    Integer b = getInteger(p); return setValue(p, b-1);
  }
  public Long decrLong(String p) {
    Long b = getLong(p); return setValue(p, b-1);
  }
  public Float decrFloat(String p) {
    Float b = getFloat(p); return setValue(p, b-1);
  }
  public Double decrDouble(String p) {
    Double b = getDouble(p); return setValue(p, b-1);
  }

  // postdecrement, e.g., a--
  public Byte pdecrByte(String p) {
    Byte b = getByte(p); setValue(p, b-1); return b;
  }
  public Short pdecrShort(String p) {
    Short b = getShort(p); setValue(p, b-1); return b;
  }
  public Integer pdecrInteger(String p) {
    Integer b = getInteger(p); setValue(p, b-1); return b;
  }
  public Long pdecrLong(String p) {
    Long b = getLong(p); setValue(p, b-1); return b;
  }
  public Float pdecrFloat(String p) {
    Float b = getFloat(p); setValue(p, b-1); return b;
  }
  public Double pdecrDouble(String p) {
    Double b = getDouble(p); setValue(p, b-1); return b;
  }

  public Rdf getRdf(String p) { return (Rdf)getSingleValue(p); }
  public Date getDate(String p) { return (Date)getSingleValue(p); }

  /** Return the class of this object
   *  @return RdfClass of Rdf
   */
  public RdfClass getClazz() { return _clazz; }

  /** Return the uri of this object
   *  @return URI of Rdf
   */
  public String getURI() { return _uri; }

  /** Return the proxy associated with this Rdf object */
  public RdfProxy getProxy() { return _proxy; }

  /** Is there a value under this predicate?
   *
   * @param predicate
   * @return
   */
  public boolean has(String predicate) {
    Set<Object> result = getValue(predicate);
    return !result.isEmpty();
  }

  /**
   * Is the value under this predicate contained in the value list?
   *
   * @param predicate
   * @param value
   * @return
   */
  public boolean oneOf(String predicate, Object ... value) {
    return Arrays.asList(value).indexOf(getSingleValue(predicate)) >= 0;
  }

  /** Set the cached value of a predicate, but perform some checks first */
  private void setField(String predicate, RdfSet<Object> set) {
    // check with class if this predicate is legal
    int predicateType = _clazz.getPropertyType(predicate);
    if (predicateType == 0) {
      logger.warn("predicate {} not defined for {}", predicate, _uri);
    }
    if ((predicateType & RdfClass.FUNCTIONAL_PROPERTY ) == 1
        && set.size() != 1) {
      logger.warn("functional {}.{} set to muliple values", predicate, _uri);
    }

    _fields.put(predicate, set);
  }


  /** Set the value under predicate to the given value, which can be a set or
   *  a single value
   *
   * @param predicate the predicate under which to put newValue
   * @param newValue the new value
   */
  @SuppressWarnings("rawtypes")
  public <T> T setValue(String predicate, T newValue) {
    HashSet<String> set = new HashSet<>();
    RdfSet<Object> rdfSet = new RdfSet<>(this, predicate);
    try {
      // convert (the elements of) newValue
      if  (newValue instanceof Collection) {
        for (Object o: (Collection)newValue) {
          set.add(objectToUri(o));
          rdfSet.addInternal(o);
        }
      } else {
        set.add(objectToUri(newValue));
        rdfSet.addInternal(newValue);
      }
      // add to database
      getClient().setMultiValue(_uri, predicate, set);
    } catch (TupleException | QueryException e) {
      StringBuilder sb = new StringBuilder();
      sb.append("[");
      for (String u: set) {
        sb.append(" ").append(u);
      }
      sb.append(" ]");
      logger.error("Exception for : {} {} {}", _uri, predicate, sb.toString());
      throw new RuntimeException(e);
    }
    // set cached value
    setField(predicate, rdfSet);
    return newValue;
  }

  /** Set the value under predicate to the empty value (no value)
   *
   * @param predicate the predicate under which to put newValue
   */
  public void clearValue(String predicate) {
    setUri(predicate, VALUE_INVALID);
    _fields.remove(predicate);
  }

  /** Only to be used internally */
  void addToDatabase(String predicate, Object newValue) {
    try {
      getClient().addToMultiValue(_uri, predicate, objectToUri(newValue));
    } catch (TupleException | QueryException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * add information to the database and the concrete Rdf Object
   *
   * @param predicate the new predicate
   * @param newValue its associated value
   */
  public void add(String predicate, Object newValue) {
    Set<Object> current = getValue(predicate);
    // current is an RdfSet that will add the value to the DB
    current.add(newValue);
  }

  /** Only to be used internally */
  void removeFromDatabase(String predicate, Object newValue) {
    try {
      getClient().removeFromMultiValue(_uri, predicate, objectToUri(newValue));
    } catch (TupleException | QueryException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * remove information froam the database and the concrete Rdf Object
   *
   * @param predicate the new predicate
   * @param newValue the value to remove
   */
  public void remove(String predicate, Object newValue) {
    Set<Object> current = getValue(predicate);
    // current is an RdfSet that will remove the value from the DB
    current.remove(newValue);
  }

  /** Get the label of this object, if any, as Java string, or null */
  public String getLabel() {
    Set<Object> labels = getValue(RDFS_LABEL);
    if (labels.isEmpty()) return null;
    XsdString x = null;
    if (labels.size() > 1) {
      for(Object label : labels) {
        if (label instanceof String) return (String)label;
        if (label instanceof XsdString) x = (XsdString)label;
      }
    }
    return x == null ? null : x.toCharSequence();
  }

  /** Get the label of this object for the given language tag, if any,
   *  as Java string, or null
   */
  public String getLabel(String languageTag) {
    Set<Object> labels = getValue(RDFS_LABEL);
    for(Object label : labels) {
      if (label instanceof XsdString) {
        XsdString x = (XsdString)label;
        if (languageTag.equals(x.getLanguageTag()))
          return x.toCharSequence();
      }
    }
    return null;
  }

  /** Return the timestamp when something directly related to this object
   *  changed, meaning a value that is only one "predicate" away, with the
   *  current thing in the specified position.
   * @param asSubject if true, consider entries where current thing is subject
   * @param asObject if true, consider entries where current thing is object
   */
  public long getLastChange(boolean asSubject, boolean asObject) {
    long result = -1;
    if (asSubject) {
      List<Object> res = _proxy.query(
          "select ?t where {} ?p ?o ?t aggregate ?tt = LMax ?t", _uri);
      if (! res.isEmpty()) {
        result = Math.max(result, (Long)res.get(0));
      }
    }
    if (asObject) {
      List<Object> res = _proxy.query(
          "select ?t where ?s ?p {} ?t aggregate ?tt = LMax ?t", _uri);
      if (! res.isEmpty()) {
        result = Math.max(result, (Long)res.get(0));
      }
    }
    return result;
  }

  public long getLastChange() { return getLastChange(true, true); }

  /** Is there a value under predicate, and is it equal to the given argument
   *  value?
   */
  public boolean isEqual(String predicate, Object value) {
    Object val = getSingleValue(predicate);
    return value == null ? val == null : value.equals(val);
  }

  /** If this Rdf object represents a RDF list, return the list proxy
   * representation, otherwise, return null.
   */
  public RdfList getList() {
    return _proxy.getList(this);
  }

  /** Return the uri of this object
   *  @return URI of Rdf
   */
  @Override
  public String toString() {
    return _uri;
  }

  @Override
  public boolean equals(Object o) {
    if (! (o instanceof Rdf)) return false;
    Rdf r = (Rdf)o;
    return _uri.equals(r._uri);
  }

  @Override
  public int hashCode() { return _uri.hashCode(); }
}
