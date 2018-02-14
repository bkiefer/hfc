package de.dfki.lt.hfc;

/**
 * constructing an iterator through BindingTable.iterator(Sting ... vars)
 * whose variables vars do not match the table headings completely should
 * not be allowed!
 * this is signaled though through this exception class which should _not_
 * be regarded as an error, but instead as an exception as the tuple store
 * and the forward chainer are still consistent
 *
 * @author (C) Hans-Ulrich Krieger
 * @since JDK 1.5
 * @version Fri Oct 30 11:54:54 CET 2015
 */

public final class BindingTableIteratorException extends Exception {
  
  public BindingTableIteratorException() {
    super();
  }
  
  public BindingTableIteratorException(String exceptionMessage) {
    super(exceptionMessage);
  }
  
}
