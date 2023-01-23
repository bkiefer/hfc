package de.dfki.lt.hfc.db.rdfProxy;

import java.util.Iterator;
import java.util.LinkedList;

// for the time-sensitive predicates, make a version that takes a boolean[]
// and sets its first value to true if the information is new.

// alternatively, we could do this actively: if a piece of information has
// been used, mark it as used, or clear the container!

// all methods that change values in this and other RDF proxy classes
// must signal that some data has changed!

public class RdfList implements Iterable<Object> {

  // The underlying RDF list object
  protected Rdf _head, _tail;

  private final String _namespace ;

  private final LinkedList<Object> _impl;

  // The connection to the RDF database
  //protected final RdfProxy _proxy;

  private Rdf newCons() {
    return _head.getClazz().getNewInstance(_namespace);
  }

  RdfList(Rdf rdf, String namespace) {
    //_proxy = proxy;
    _head = _tail = rdf;
    _impl = new LinkedList<>();
    _namespace = namespace;
  }

  void fetchElements() {
    // read the list into memory, only to be accessed by RdfProxy
    Rdf head = _head;
    do {
      Object first = head.getSingleValue(RdfProxy.RDF_FIRST);
      if (first != null) _impl.add(first);
      Rdf next = (Rdf)head.getSingleValue(RdfProxy.RDF_REST);
      if (next == null || next.getURI().equals(RdfProxy.RDF_NIL)) {
        _tail = head;
      }
      head = next;
    } while (head != null);
  }

  /** Return true if list is empty */
  public boolean isEmpty() {
    return _impl.isEmpty();
    // an empty rdf list in this representation only has the URI of type List
    // but no first element
  }

  /** Return the length of the list */
  public int length() { return _impl.size(); }

  /** Return the last element, or null, if the list is empty  */
  public Object peekLast() { return _impl.peekLast(); }

  /** Return the first element, or null, if the list is empty */
  public Object peekFirst() { return _impl.peekFirst(); }

  /** Return the element at position index */
  public Object get(int index) { return _impl.get(index); }

  /** Return an iterator object to iterate over the elements */
  @Override
  public Iterator<Object> iterator() { return _impl.iterator(); }

  /** Add to the end of the list */
  public void add(Object value) {
    if (! _impl.isEmpty()) {
      Rdf newCons = newCons();
      _tail.setUri(RdfProxy.RDF_REST, newCons.getURI());
      _tail = newCons;
    }
    // add element: put it into the first slot of the (possibly new) tail
    _tail.setValue(RdfProxy.RDF_FIRST, value);
    _impl.add(value);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append(_head.getURI()).append('[');
    Iterator<Object> it = _impl.iterator();
    if (it.hasNext()) sb.append(it.next().toString());
    while (it.hasNext()) {
      sb.append(", ").append(it.next().toString());
    }
    sb.append(']');
    return sb.toString();
  }

  /** Return the URI of the head of this list */
  public String getHeadUri() {
    return _head.getURI();
  }

  /** push to the front of the list *
  void push(T value) {
    _impl.push(value);
    _agent.newData();
  }
  */
}
