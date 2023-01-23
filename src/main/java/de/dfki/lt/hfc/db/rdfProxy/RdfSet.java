package de.dfki.lt.hfc.db.rdfProxy;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class RdfSet<E> implements Set<E> {
  /** The Rdf object to which this set belongs as value */
  private Rdf _valueOf;

  /** The predicate under which this set is stored */
  private String _predicate;

  /** This contains the cached Rdf and other objects, *not* the URIs */
  private Set<E> _impl;

  RdfSet(Rdf v, String p) {
    _valueOf = v;
    _predicate = p;
    _impl = new HashSet<E>();
  }

  @Override
  public int size() { return _impl.size(); }

  @Override
  public boolean isEmpty() { return _impl.isEmpty(); }

  @Override
  public boolean contains(Object o) { return _impl.contains(o);  }

  @Override
  public Iterator<E> iterator() { return _impl.iterator(); }

  @Override
  public Object[] toArray() {
    return _impl.toArray();
  }

  @Override
  public <T> T[] toArray(T[] a) {
    return _impl.toArray(a);
  }

  @Override
  public boolean add(E e) {
    boolean res = _impl.add(e);
    if (res) _valueOf.addToDatabase(_predicate, e);
    return res;
  }

  @Override
  public boolean remove(Object o) {
    boolean res = _impl.remove(o);
    if (res) _valueOf.removeFromDatabase(_predicate, o);
    return res;
  }

  @Override
  public boolean containsAll(Collection<?> c) {
    throw new UnsupportedOperationException("Not for RdfSet");
  }

  @Override
  public boolean addAll(Collection<? extends E> c) {
    throw new UnsupportedOperationException("Not for RdfSet");
  }

  @Override
  public boolean retainAll(Collection<?> c) {
    throw new UnsupportedOperationException("Not for RdfSet");
  }

  @Override
  public boolean removeAll(Collection<?> c) {
    throw new UnsupportedOperationException("Not for RdfSet");
  }

  @Override
  public void clear() {
    _impl.clear();
    _valueOf.clearValue(_predicate);
  }

  void addInternal(E o) {
    _impl.add(o);
  }
}
