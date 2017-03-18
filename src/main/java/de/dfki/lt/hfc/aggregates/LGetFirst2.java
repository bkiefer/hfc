package de.dfki.lt.hfc.aggregates;

import java.util.*;
import de.dfki.lt.hfc.*;
import de.dfki.lt.hfc.types.XsdInt;
import de.dfki.lt.hfc.types.XsdLong;

/**
 * this aggregational operator LGetFirst2
 *   = take the first time-stamped tuple(s)
 * is supposed to be given a table of several columns of the following form
 *   arg1 ... argN time limit
 * where the tuples <arg1, ..., argN> with the k first TIME STAMPS are
 * returned, e.g, if there are 10 tuples with the newest time stamp and the limit
 * is one, 10 tuples will be returned
 *
 * note that time in this version requires to be filled with XSD long integers
 *
 * example query:
 *
 * // TODO: this example probably doesn't make sense for getFirst
 *
 *   // look for the first 3 dialogue acts (if possible), labelled with a
 *   // time stamp greater or equal 548
 *   SELECT ?da ?t
 *   WHERE ?da <rdf:type> <dafn:DialogueAct> &
 *         ?da <dafn:happens> ?t
 *   FILTER LGreaterEqual ?t "548"^^<xsd:long>
 *   AGGREGATE ?dialact = LGetLatest ?da ?t "3"^^<xsd:int>
 *
 * returns a binding table of one column and 0 to 3 rows (depending on the
 * data stored in the tuple store), where the columns are headed by the labels
 * "?dialact" and "?time"
 *
 * in case time ?t should also be returned (stored under heading "?time"),
 * we have to duplicate the temporal argument in the AGGREGATE section:
 *
 *   AGGREGATE ?dialact ?time = LGetLatest ?da ?t ?t "3"^^<xsd:int>
 *
 *
 * @author (C) Hans-Ulrich Krieger
 * @since JDK 1.5
 * @version Thu Jan  7 17:41:55 CET 2016
 */
public final class LGetFirst2 extends LGetTimestamped2 {

  /**
   * general form of the aggregate call:  ?arg1' ... ?argN' = LGetLatest ?arg1 ... ?argN ?time ?limit
   */
  public BindingTable apply(BindingTable args,
                            SortedMap<Integer, Integer> nameToPos,
                            Map<Integer, String> nameToExternalName) {
    return applyInternal(tupleStore, args, nameToPos, nameToExternalName, false);
  }

}
