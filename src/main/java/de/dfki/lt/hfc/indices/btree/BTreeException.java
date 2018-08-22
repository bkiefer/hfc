package de.dfki.lt.hfc.indices.btree;

/**
 * {@link Exception}s used to handle problems while maintainting the btree.
 * TODO Might be refactored to a general index error class.
 * <p>
 * Created by christian on 05/03/17.
 */
public class BTreeException extends Exception {

  public BTreeException(String message) {
    super(message);
  }

}

