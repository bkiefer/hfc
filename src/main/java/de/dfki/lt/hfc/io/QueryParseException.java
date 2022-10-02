package de.dfki.lt.hfc.io;

/**
 * posing a syntactically-wrong query should _not_ be regarded
 * as an error, but instead as an exception as the tuple store
 * and the forward chainer are still consistent
 *
 * @author (C) Hans-Ulrich Krieger
 * @version Fri Oct 30 11:54:54 CET 2015
 * @since JDK 1.5
 */

public final class QueryParseException extends Exception {

  public QueryParseException() {
    super();
  }

  public QueryParseException(String exceptionMessage) {
    super(exceptionMessage);
  }

}
