package de.dfki.lt.hfc.qrelations;

import de.dfki.lt.hfc.types.*;

import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * This class is used as a super class for all of Allen´s interval relations. It was introduced to provide
 * access to the different operators necessary to model these relations as Indexlookups
 * or when rewriting the relation as filter clauses.
 * <p>
 * Created by christian on 25/05/17.
 */
public abstract class QRelationAllen extends QRelation {

  protected static final Set supportedClasses = Collections.unmodifiableSet(Stream.of(
          XsdDateTime.class, XsdLong.class, XsdInt.class, XsdFloat.NAME, XsdDate.class)
          .collect(Collectors.toSet()));


  protected boolean isInterval;


  private boolean isSupported(XsdAnySimpleType type) {
    return supportedClasses.contains(type.getClass());
  }

  protected boolean isValid() {
    if (!(isSupported(firstArgumentObject) && isSupported(secondArgumentObject))) {
      return false;
    } else {
      if (!Objects.equals(firstArgumentObject.getClass(), secondArgumentObject.getClass())) {
        return false;
      } else {
        return true;
      }
    }
  }

  @Override
  public boolean isAllenRelation() {
    return true;
  }

  @Override
  public boolean isInterval() {
    return isInterval;
  }
}
