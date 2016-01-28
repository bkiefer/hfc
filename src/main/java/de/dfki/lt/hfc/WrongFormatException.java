package de.dfki.lt.hfc;

public class WrongFormatException extends Exception {
  private static final long serialVersionUID = 5547850933480390735L;

  public WrongFormatException(String message) {
    super(message);
  }

  public WrongFormatException(String message, Throwable cause) {
    super(message, cause);
  }
}
