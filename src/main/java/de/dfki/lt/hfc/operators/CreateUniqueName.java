package de.dfki.lt.hfc.operators;

import de.dfki.lt.hfc.FunctionalOperator;
import de.dfki.lt.hfc.types.AnyType;
import de.dfki.lt.hfc.types.Uri;
import de.dfki.lt.hfc.types.XsdLong;

/**
 * returns an URI based on the given uri and the current time in milliseconds
 * (i.e. the difference between current time and midnight, January 1, 1970 UTC).
 *
 * @author (C) Christian Willms
 * @version Thu Nov 07 08:11:19 CEST 2019
 * @see de.dfki.lt.hfc.FunctionalOperator
 * @see de.dfki.lt.hfc.types.XsdDateTime
 * @since JDK 1.5
 */
public final class CreateUniqueName extends FunctionalOperator {

 /**
  * only the first arg is considered -- it is further assumed that this arg is a valid uri
  * This URI is then used as template to create a new uri
  * <test:input> -> <test:input_84665464458>
  */
 public int apply(int[] args) {
  AnyType t = tupleStore.getObject(args[0]);
  if (!(t instanceof Uri))
   throw new IllegalArgumentException("CreateUniqueName expects a valid URI as its input");
  String tValue = ((Uri) t).value;
  // replace tValue with tValue and appended timestamp
  Uri nUri = new Uri(tValue + "_"+System.currentTimeMillis(), t.getNamespace());
  return registerObject(nUri.toString(), nUri);
 }

}