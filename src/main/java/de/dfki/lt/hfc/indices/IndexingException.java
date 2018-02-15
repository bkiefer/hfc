package de.dfki.lt.hfc.indices;

/**
 * {@link Exception}s used to handle problems while maintainting the btree.
 * TODO Might be refactored to a general index error class.
 *
 *
 * Created by chwi02 on 07.03.17.
 */
public class IndexingException extends Exception{
    public IndexingException() {
        super();
    }

    public IndexingException(String message) {
        super(message);
    }

    public IndexingException(String message, Throwable cause) {
        super(message, cause);
    }

    public IndexingException(Throwable cause) {
        super(cause);
    }
}
