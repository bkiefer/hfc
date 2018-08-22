package de.dfki.lt.hfc.operators;

import de.dfki.lt.hfc.FunctionalOperator;
import de.dfki.lt.hfc.types.XsdDateTime;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * returns the current time encoded as an XsdDateTime object, internally
 * represented by a collection of ints and a float
 *
 * @author (C) Hans-Ulrich Krieger
 * @version Tue Sep 29 11:11:19 CEST 2009
 * @see http://www.w3.org/TR/xmlschema-2/
 * @see de.dfki.lt.hfc.FunctionalOperator
 * @see de.dfki.lt.hfc.types.XsdDateTime
 * @since JDK 1.5
 */
public final class GetDateTime extends FunctionalOperator {

  /**
   * determine current time down to milliseconds: "yyyy-MM-dd'T'HH:mm:ss.SSS"
   */
  private static final SimpleDateFormat DATETIME_FORMATTER = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");

  /**
   * none of the args are considered -- in fact, there should be NO args;
   * current time is measured up to milliseconds: yyyy-MM-dd'T'HH:mm:ss.SSS
   */
  public int apply(int[] args) {
    String stringTime = DATETIME_FORMATTER.format(new Date());
    XsdDateTime dateTime = new XsdDateTime(Integer.parseInt(stringTime.substring(0, 4)),
            Integer.parseInt(stringTime.substring(5, 7)),
            Integer.parseInt(stringTime.substring(8, 10)),
            Integer.parseInt(stringTime.substring(11, 13)),
            Integer.parseInt(stringTime.substring(14, 16)),
            Float.parseFloat(stringTime.substring(17)));
    return registerObject(dateTime.toString(this.tupleStore.namespace.shortIsDefault), dateTime);
  }

}
